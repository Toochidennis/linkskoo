package com.digitaldream.winskool.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.winskool.R
import com.digitaldream.winskool.activities.Login
import com.digitaldream.winskool.activities.PaymentActivity
import com.digitaldream.winskool.adapters.OnTransactionClickListener
import com.digitaldream.winskool.adapters.ReceiptsHistoryAdapter
import com.digitaldream.winskool.models.AdminPaymentModel
import com.digitaldream.winskool.utils.FunctionUtils
import com.digitaldream.winskool.utils.FunctionUtils.drawGraph
import com.digitaldream.winskool.utils.FunctionUtils.requestFromServer
import com.digitaldream.winskool.utils.VolleyCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.achartengine.GraphicalView
import org.json.JSONObject
import java.util.*


class ReceiptsHistoryFragment : Fragment(), OnTransactionClickListener {

    private lateinit var mReceiptView: NestedScrollView
    private lateinit var mReceiptChart: LinearLayout
    private lateinit var mReceiptSum: TextView
    private lateinit var mReceiptCount: TextView
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mReceiptMessage: TextView
    private lateinit var mReceiptImage: ImageView
    private lateinit var mErrorView: LinearLayout
    private lateinit var mRefreshBtn: Button
    private lateinit var mAddReceipt: FloatingActionButton

    private var mGraphicalView: GraphicalView? = null
    private val mReceiptList = mutableListOf<AdminPaymentModel>()
    private val mGraphAmountList = arrayListOf<String>()
    private val mGraphDateList = arrayListOf<String>()
    private lateinit var mAdapter: ReceiptsHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(
            R.layout.fragment_receipts_history,
            container, false
        )
        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        mReceiptView = view.findViewById(R.id.receipt_view)
        mReceiptChart = view.findViewById(R.id.chart)
        mReceiptSum = view.findViewById(R.id.receipt_sum)
        mReceiptCount = view.findViewById(R.id.receipt_count)
        mRecyclerView = view.findViewById(R.id.receipt_recycler)
        mReceiptMessage = view.findViewById(R.id.receipt_error_message)
        mReceiptImage = view.findViewById(R.id.error_image)
        mErrorView = view.findViewById(R.id.error_view)
        mRefreshBtn = view.findViewById(R.id.refresh_btn)
        mAddReceipt = view.findViewById(R.id.add_receipt)

        toolbar.apply {
            title = "Receipts"
            setNavigationIcon(R.drawable.arrow_left)
            setNavigationOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        }

        mAdapter = ReceiptsHistoryAdapter(requireContext(), mReceiptList, this)
        mRecyclerView.hasFixedSize()
        mRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        mRecyclerView.adapter = mAdapter

        refreshData()

        return view
    }

    private fun getReceipts() {
        val sharedPreferences =
            requireContext().getSharedPreferences("loginDetail", Context.MODE_PRIVATE)
        val term = sharedPreferences.getString("term", "")
        val year = sharedPreferences.getString("school_year", "")

        val url = "${Login.urlBase}/manageTransactions.php?type=receipts&&term=$term&&year=$year"
        val hashMap = hashMapOf<String, String>()

        requestFromServer(Request.Method.GET, url, requireContext(), hashMap,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    try {
                        val jsonObject = JSONObject(response)
                        val receiptsArray = jsonObject.getJSONArray("receipts")

                        val receiptsObject = receiptsArray.getJSONObject(0)
                        val receiptSum = receiptsObject.getString("sum")
                        val receiptCount = receiptsObject.getString("count")

                        mReceiptCount.text = receiptCount

                        if (receiptSum == "null" || receiptSum.isNullOrBlank()) {
                            mReceiptSum.text = getString(R.string.zero_balance)
                        } else {
                            String.format(
                                Locale.getDefault(), "%s%s", getString(R.string.naira),
                                FunctionUtils.currencyFormat(receiptSum.toDouble())
                            ).also { mReceiptSum.text = it }
                        }

                        if (jsonObject.has("graph")) {
                            val graphArray = jsonObject.getJSONArray("graph")

                            for (i in 0 until graphArray.length()) {
                                val graphObject = graphArray.getJSONObject(i)
                                val graphAmount = graphObject.getString("amount").replace(".00", "")
                                val graphDate = graphObject.getString("date")

                                mGraphAmountList.add(graphAmount)
                                mGraphDateList.add(graphDate)
                            }

                            if (mGraphicalView == null) {
                                mGraphicalView = drawGraph(
                                    mGraphAmountList,
                                    mGraphDateList,
                                    requireContext(),
                                    "Month/Year"
                                )
                                mReceiptChart.addView(mGraphicalView)
                            } else {
                                mGraphicalView!!.repaint()
                            }
                        } else {
                            mGraphicalView = drawGraph(
                                mGraphAmountList,
                                mGraphDateList,
                                requireContext(),
                                "Month/Year"
                            )
                            mReceiptChart.addView(mGraphicalView)
                        }

                        if (jsonObject.has("transactions")) {

                            val transactionsArray = jsonObject.getJSONArray("transactions")

                            for (i in 0 until transactionsArray.length()) {
                                val transactionObject = transactionsArray.getJSONObject(i)
                                val transactionType = transactionObject.getString("trans_type")
                                val reference = transactionObject.getString("reference")
                                val registrationNo = transactionObject.getString("reg_no")
                                val studentName = transactionObject.getString("name")
                                val receiptAmount = transactionObject.getString("amount")
                                val date = transactionObject.getString("date")
                                val receiptTerm = when (transactionObject.getString("term")) {
                                    "1" -> "First Term Fees"
                                    "2" -> "Second Term Fees"
                                    else -> "Third Term Fees"
                                }
                                val receiptYear = transactionObject.getString("year")
                                val levelName = transactionObject.getString("level_name")
                                val className = transactionObject.getString("class_name")

                                val previousYear = receiptYear.toInt() - 1
                                val session =
                                    String.format(
                                        Locale.getDefault(),
                                        "%d/%s",
                                        previousYear,
                                        receiptYear
                                    )

                                val receiptModel = AdminPaymentModel()
                                receiptModel.setStudentName(studentName)
                                receiptModel.setTransactionName(transactionType)
                                receiptModel.setReferenceNumber(reference)
                                receiptModel.setRegistrationNumber(registrationNo)
                                receiptModel.setReceivedAmount(receiptAmount)
                                receiptModel.setTransactionDate(date)
                                receiptModel.setTerm(receiptTerm)
                                receiptModel.setSession(session)
                                receiptModel.setLevelName(levelName)
                                receiptModel.setClassName(className)

                                mReceiptList.add(receiptModel)
                                mReceiptList.sortByDescending { it.getTransactionDate() }
                            }
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    if (mReceiptList.isEmpty()) {
                        mReceiptView.isVisible = true
                        mReceiptMessage.isVisible = true
                        mErrorView.isVisible = false
                        mAddReceipt.isVisible = true
                    } else {
                        mReceiptView.isVisible = true
                        mReceiptMessage.isVisible = false
                        mErrorView.isVisible = false
                        mAddReceipt.isVisible = true
                    }
                    mAdapter.notifyItemChanged(mReceiptList.size - 1)
                }

                override fun onError(error: VolleyError) {
                    mReceiptView.isVisible = false
                    mErrorView.isVisible = true
                    mAddReceipt.isVisible = false
                }
            }
        )
    }

    private fun refreshData() {
        mRefreshBtn.setOnClickListener {
            mReceiptList.clear()
            getReceipts()
        }
    }

    override fun onResume() {
        super.onResume()
        getReceipts()

    }

    override fun onTransactionClick(position: Int) {
        val paymentModel = mReceiptList[position]
        val amount = paymentModel.getReceivedAmount()
        val name = paymentModel.getStudentName()
        val levelName = paymentModel.getLevelName()
        val className = paymentModel.getClassName()
        val regNo = paymentModel.getRegistrationNumber()
        val reference = paymentModel.getReferenceNumber()
        val session = paymentModel.getSession()
        val term = paymentModel.getTerm()
        val date = paymentModel.getTransactionDate()

        startActivity(
            Intent(requireContext(), PaymentActivity::class.java)
                .putExtra("amount", amount)
                .putExtra("name", name)
                .putExtra("level_name", levelName)
                .putExtra("class_name", className)
                .putExtra("reg_no", regNo)
                .putExtra("reference", reference)
                .putExtra("session", session)
                .putExtra("term", term)
                .putExtra("date", date)
                .putExtra("from", "admin_receipt")
        )

    }
}
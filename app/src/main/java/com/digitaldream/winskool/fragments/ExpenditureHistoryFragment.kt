package com.digitaldream.winskool.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
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
import com.digitaldream.winskool.activities.PaymentActivity
import com.digitaldream.winskool.adapters.ExpenditureHistoryAdapter
import com.digitaldream.winskool.adapters.OnItemClickListener
import com.digitaldream.winskool.dialog.TermSessionPickerBottomSheet
import com.digitaldream.winskool.models.ChartModel
import com.digitaldream.winskool.models.ExpenditureHistoryModel
import com.digitaldream.winskool.utils.FunctionUtils.currencyFormat
import com.digitaldream.winskool.utils.FunctionUtils.plotLineChart
import com.digitaldream.winskool.utils.FunctionUtils.requestToServer
import com.digitaldream.winskool.utils.VolleyCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.achartengine.GraphicalView
import org.json.JSONObject
import java.util.Locale

class ExpenditureHistoryFragment : Fragment(R.layout.fragment_history_expenditure),
    OnItemClickListener {

    private lateinit var mExpenditureView: NestedScrollView
    private lateinit var mExpenditureChart: LinearLayout
    private lateinit var mExpenditureSum: TextView
    private lateinit var mExpenditureCount: TextView
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mExpenditureMessage: TextView
    private lateinit var mExpenditureImage: ImageView
    private lateinit var mErrorView: LinearLayout
    private lateinit var mRefreshBtn: Button
    private lateinit var mTimeFrameBtn: Button
    private lateinit var mTermBtn: Button
    private lateinit var mAddExpenditure: FloatingActionButton

    private var mGraphicalView: GraphicalView? = null
    private val mExpenditureList = mutableListOf<ExpenditureHistoryModel>()
    private val mGraphList = arrayListOf<ChartModel>()
    private lateinit var mAdapter: ExpenditureHistoryAdapter

    private var mTerm: String? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find child views in the inflated layout by their IDs
        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        mExpenditureView = view.findViewById(R.id.expenditure_view)
        mExpenditureChart = view.findViewById(R.id.expenditure_chart)
        mExpenditureSum = view.findViewById(R.id.expenditure_sum)
        mExpenditureCount = view.findViewById(R.id.expenditure_count)
        mRecyclerView = view.findViewById(R.id.expenditure_recycler)
        mExpenditureMessage = view.findViewById(R.id.expenditure_error_message)
        mExpenditureImage = view.findViewById(R.id.error_image)
        mErrorView = view.findViewById(R.id.error_view)
        mRefreshBtn = view.findViewById(R.id.refresh_btn)
        mTimeFrameBtn = view.findViewById(R.id.time_frame_btn)
        mTermBtn = view.findViewById(R.id.term_btn)
        mAddExpenditure = view.findViewById(R.id.add_expenditure)

        toolbar.apply {
            title = "Expenditures"
            setNavigationIcon(R.drawable.arrow_left)
            setNavigationOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        }

        mAddExpenditure.setOnClickListener {
            startActivity(
                Intent(
                    context,
                    PaymentActivity::class.java
                ).putExtra("from", "add_expenditure")
            )
        }

        mAdapter = ExpenditureHistoryAdapter(
            requireContext(),
            mExpenditureList,
            this
        )
        mRecyclerView.hasFixedSize()
        mRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        mRecyclerView.adapter = mAdapter

        refreshData()

        mTermBtn.setOnClickListener {
            TermSessionPickerBottomSheet().show(
                requireActivity().supportFragmentManager,
                "Term/Session"
            )
        }
    }

    private fun getExpenditure() {
        mExpenditureList.clear()
        val sharedPreferences =
            requireContext().getSharedPreferences("loginDetail", Context.MODE_PRIVATE)

        val term = sharedPreferences.getString("term", "")
        val year = sharedPreferences.getString("school_year", "")

        val url = "${getString(R.string.base_url)}/manageTransactions" +
                ".php?type=expenditure&&term=$term&&year=$year"
        val hashMap = hashMapOf<String, String>()

        requestToServer(Request.Method.GET, url, requireContext(), hashMap,
            object : VolleyCallback {
                override fun onResponse(response: String) = try {
                    val jsonObject = JSONObject(response)
                    val expenditureArray = jsonObject.getJSONArray("expenditure")
                    val expenditureObject = expenditureArray.getJSONObject(0)
                    val expenditureSum = expenditureObject.getString("sum")
                    val expenditureCount = expenditureObject.getString("count")

                    if (expenditureSum == "null" || expenditureSum.isNullOrBlank()) {
                        mExpenditureSum.text = getString(R.string.zero_balance)
                    } else {
                        String.format(
                            Locale.getDefault(), "%s%s", getString(R.string.naira),
                            currencyFormat(expenditureSum.toDouble())
                        ).also { mExpenditureSum.text = it }
                    }
                    mExpenditureCount.text = expenditureCount

                    if (jsonObject.has("graph")) {
                        val graphArray = jsonObject.getJSONArray("graph")

                        for (i in 0 until graphArray.length()) {
                            val graphObject = graphArray.getJSONObject(i)
                            val graphAmount = graphObject.getString("amount")
                            val graphDate = graphObject.getString("date")

                            mGraphList.add(ChartModel(graphAmount, graphDate))
                            mGraphList.sortBy { it.horizontalValues }
                        }

                        if (mGraphicalView == null) {
                            mGraphicalView = plotLineChart(
                                mGraphList,
                                requireContext(),
                                "Received",
                                "Month/Year",
                            )
                            mExpenditureChart.addView(mGraphicalView)
                        } else {
                            mGraphicalView!!.repaint()
                        }
                    } else {
                        mGraphicalView = plotLineChart(
                            mGraphList,
                            requireContext(),
                            "Received",
                            "Month/Year",
                        )
                        mExpenditureChart.addView(mGraphicalView)
                    }

                    if (jsonObject.has("transactions")) {
                        val transactionsArray = jsonObject.getJSONArray("transactions")
                        for (i in 0 until transactionsArray.length()) {
                            val transactionObject = transactionsArray.getJSONObject(i)
                            val transactionType = transactionObject.getString("description")
                            val reference = transactionObject.getString("reference")
                            val vendorName = transactionObject.getString("name")
                            val telephone = transactionObject.getString("reg_no")
                            val expenditureAmount = transactionObject.getString("amount")
                            val date = transactionObject.getString("date")
                            val receiptTerm = when (transactionObject.getString("term")) {
                                "1" -> "First Term Fees"
                                "2" -> "Second Term Fees"
                                else -> "Third Term Fees"
                            }
                            val receiptYear = transactionObject.getString("year")

                            val previousYear = receiptYear.toInt() - 1
                            val session =
                                String.format(
                                    Locale.getDefault(),
                                    "%d/%s",
                                    previousYear,
                                    receiptYear
                                )

                            val historyModel = ExpenditureHistoryModel()
                            historyModel.setVendorName(vendorName)
                            historyModel.setAmount(expenditureAmount)
                            historyModel.setDate(date)
                            historyModel.setTypeName(transactionType)
                            historyModel.setReferenceNumber(reference)
                            historyModel.setTerm(receiptTerm)
                            historyModel.setSession(session)
                            historyModel.setPhone(telephone)

                            mExpenditureList.add(historyModel)
                            mExpenditureList.sortByDescending { it.getDate() }
                        }
                        if (mExpenditureList.isEmpty()) {
                            mExpenditureView.isVisible = true
                            mExpenditureMessage.isVisible = true
                            mAddExpenditure.isVisible = true
                            mErrorView.isVisible = false
                        } else {
                            mExpenditureView.isVisible = true
                            mExpenditureMessage.isVisible = false
                            mAddExpenditure.isVisible = true
                            mErrorView.isVisible = false
                        }
                        mAdapter.notifyDataSetChanged()

                    } else {
                        mExpenditureView.isVisible = true
                        mExpenditureMessage.isVisible = true
                        mAddExpenditure.isVisible = true
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                override fun onError(error: VolleyError) {
                    mExpenditureView.isVisible = false
                    mAddExpenditure.isVisible = false
                    mErrorView.isVisible = true
                    mRefreshBtn.isVisible = true
                }
            }
        )

    }


    private fun refreshData() {
        mRefreshBtn.setOnClickListener {
            getExpenditure()
        }
    }

    override fun onResume() {
        super.onResume()
        getExpenditure()

    }

    override fun onItemClick(position: Int) {
        val model = mExpenditureList[position]

        startActivity(
            Intent(requireContext(), PaymentActivity::class.java)
                .putExtra("amount", model.getAmount())
                .putExtra("vendor_name", model.getVendorName())
                .putExtra("vendor_phone", model.getPhone())
                .putExtra("reference", model.getReferenceNumber())
                .putExtra("session", model.getSession())
                .putExtra("term", model.getTerm())
                .putExtra("date", model.getDate())
                .putExtra("description", model.getType())
                .putExtra("from", "admin_expenditure")
        )

    }

}
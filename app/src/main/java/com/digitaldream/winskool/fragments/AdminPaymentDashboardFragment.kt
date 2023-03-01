package com.digitaldream.winskool.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.digitaldream.winskool.R
import com.digitaldream.winskool.activities.Login
import com.digitaldream.winskool.activities.PaymentActivity
import com.digitaldream.winskool.adapters.AdminPaymentDashboardAdapter
import com.digitaldream.winskool.adapters.OnTransactionClickListener
import com.digitaldream.winskool.models.AdminPaymentDashboardModel
import com.digitaldream.winskool.utils.UtilsFun
import org.json.JSONArray
import org.json.JSONObject
import java.util.*


class AdminPaymentDashboardFragment : Fragment(),
    OnTransactionClickListener {

    private lateinit var menuHost: MenuHost
    private lateinit var mMainLayout: LinearLayout
    private lateinit var mExpectedAmount: TextView
    private lateinit var mReceivedAmount: TextView
    private lateinit var mDebtAmount: TextView
    private lateinit var mReceiptBtn: Button
    private lateinit var mExpenditureBtn: Button
    private lateinit var mSeeAllBtn: CardView
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mTransactionMessage: TextView
    private lateinit var mTransactionImage: ImageView
    private lateinit var mErrorMessage: TextView
    private lateinit var mRefreshBtn: Button

    private val mTransactionList = mutableListOf<AdminPaymentDashboardModel>()
    private lateinit var mAdapter: AdminPaymentDashboardAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(
            R.layout.fragment_dashboard_payment_admin, container,
            false
        )

        val toolbar: Toolbar = view.findViewById(R.id.toolbar)

        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        menuHost = requireActivity()

        setUpMenu()

        actionBar!!.apply {
            setHomeButtonEnabled(true)
            title = "Payment"
            setHomeAsUpIndicator(R.drawable.arrow_left)
            setDisplayHomeAsUpEnabled(true)
        }

        mMainLayout = view.findViewById(R.id.dashboard_view)
        mReceivedAmount = view.findViewById(R.id.received_balance)
        mRecyclerView = view.findViewById(R.id.transaction_recycler)
        mDebtAmount = view.findViewById(R.id.debt_balance)
        mTransactionImage = view.findViewById(R.id.error_image)
        mTransactionMessage = view.findViewById(R.id.transaction_error_message)
        mExpenditureBtn = view.findViewById(R.id.expenditure_btn)
        mReceiptBtn = view.findViewById(R.id.receipt_btn)
        mExpectedAmount = view.findViewById(R.id.expected_revenue)
        mSeeAllBtn = view.findViewById(R.id.see_all_btn)
        mErrorMessage = view.findViewById(R.id.error_message)
        mRefreshBtn = view.findViewById(R.id.refresh_btn)

        mExpenditureBtn.setOnClickListener {
            startActivity(
                Intent(activity, PaymentActivity().javaClass)
                    .putExtra("from", "expenditure")
            )

        }

        mReceiptBtn.setOnClickListener {
            startActivity(
                Intent(activity, PaymentActivity().javaClass)
                    .putExtra("from", "receipt")
            )
        }

        mSeeAllBtn.setOnClickListener {
            startActivity(
                Intent(activity, PaymentActivity().javaClass)
                    .putExtra("from", "see_all")
            )
        }

        mAdapter = AdminPaymentDashboardAdapter(requireContext(), mTransactionList, this)
        mRecyclerView.hasFixedSize()
        mRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        mRecyclerView.adapter = mAdapter
        
        refreshData()

        return view
    }
    private fun getDashboardDetails() {
        val sharedPreferences =
            requireContext().getSharedPreferences("loginDetail", Context.MODE_PRIVATE)
        val db = sharedPreferences.getString("db", "")
        val term = sharedPreferences.getString("term", "")
        val year = sharedPreferences.getString("school_year", "")

        val progressFlower = ACProgressFlower.Builder(context)
            .direction(ACProgressConstant.DIRECT_CLOCKWISE)
            .textMarginTop(10)
            .fadeColor(ContextCompat.getColor((context as AppCompatActivity), R.color.color_5))
            .build()
        progressFlower.setCancelable(false)
        progressFlower.setCanceledOnTouchOutside(false)
        progressFlower.show()

        val url = "${Login.urlBase}/manageTransactions.php?dashboard=1&&term=$term&&year=$year"
        val stringRequest = object : StringRequest(
            Method.GET,
            url,
            { response: String ->
                Log.i("response", response)
                progressFlower.dismiss()

                try {
                    val jsonObject = JSONObject(response)
                    val receiptsArray = jsonObject.getJSONArray("receipts")
                    val invoiceArray = jsonObject.getJSONArray("invoice")
                    val transactionsArray = jsonObject.getJSONArray("transactions")

                    val receiptsObject = receiptsArray.getJSONObject(0)
                    val receiptsSum = receiptsObject.getString("sum").replace(".00", "")

                    val invoiceObject = invoiceArray.getJSONObject(0)
                    val invoiceSum = invoiceObject.getString("sum").replace(".00", "")

                    if (receiptsSum == "null" && invoiceSum == "null") {
                        mReceivedAmount.text = getString(R.string.zero_balance)
                        mExpectedAmount.text = getString(R.string.zero_balance)
                        mDebtAmount.text = getString(R.string.zero_balance)
                    } else {
                        String.format(
                            Locale.getDefault(), "%s%s", getString(R.string.naira),
                            UtilsFun.currencyFormat(receiptsSum.toDouble())
                        ).also { mReceivedAmount.text = it }

                        String.format(
                            Locale.getDefault(), "%s%s", getString(R.string.naira),
                            UtilsFun.currencyFormat(invoiceSum.toDouble())
                        ).also { mExpectedAmount.text = it }

                        val debtSum = invoiceSum.toLong() - receiptsSum.toLong()

                        String.format(
                            Locale.getDefault(), "%s%s", getString(R.string.naira),
                            UtilsFun.currencyFormat(debtSum.toDouble())
                        ).also { mDebtAmount.text = it }
                    }

                    for (i in 0 until 5) {
                        val transactionsObject = transactionsArray.getJSONObject(i)
                        val transactionType = transactionsObject.getString("trans_type")
                        val reference = transactionsObject.getString("reference")
                        val description = transactionsObject.getString("description")
                        val amount = transactionsObject.getString("amount")
                        val date = transactionsObject.getString("date")

                        val adminModel = AdminPaymentDashboardModel()
                        adminModel.setTransactionName(transactionType)
                        adminModel.setDescription(description)
                        adminModel.setReceivedAmount(amount)
                        adminModel.setTransactionDate(date)
                        mTransactionList.add(adminModel)
                        mTransactionList.sortByDescending { it.getTransactionDate() }
                    }

                    if (mTransactionList.isEmpty()) {
                        mMainLayout.isVisible = true
                        mTransactionMessage.isVisible = true
                        mTransactionImage.isVisible = true
                        mErrorMessage.isVisible = false
                        mRefreshBtn.isVisible = false
                    } else {
                        mMainLayout.isVisible = true
                        mTransactionMessage.isVisible = false
                        mTransactionImage.isVisible = false
                        mErrorMessage.isVisible = false
                        mRefreshBtn.isVisible = false
                    }
                    mAdapter.notifyItemChanged(mTransactionList.size - 1)

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }, { error: VolleyError ->
                error.printStackTrace()
                progressFlower.dismiss()
                mMainLayout.isVisible = false
                mErrorMessage.isVisible = true
                mRefreshBtn.isVisible = true
                mErrorMessage.text = getString(R.string.can_not_retrieve)

            }) {
            override fun getParams(): MutableMap<String, String> {
                val stringMap = hashMapOf<String, String>()
                stringMap["_db"] = db!!

                return stringMap
            }
        }

        val requestQueue = Volley.newRequestQueue(requireContext())
        requestQueue.add(stringRequest)
    }
    private fun setUpMenu() {
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.setup_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.settings -> {
                        startActivity(
                            Intent(context, PaymentActivity().javaClass).putExtra
                                ("from", "settings")
                        )
                        true
                    }
                    android.R.id.home -> {
                        requireActivity().onBackPressedDispatcher
                            .onBackPressed()
                        return true
                    }
                    else -> false
                }
            }
        })
    }

    private fun refreshData() {
        mRefreshBtn.setOnClickListener {
            mTransactionList.clear()
            getDashboardDetails()
        }
    }

    override fun onResume() {
        super.onResume()
        mTransactionList.clear()
        getDashboardDetails()
    }
    override fun onTransactionClick(position: Int) {
        Toast.makeText(requireContext(), ":)", Toast.LENGTH_SHORT).show()
    }
}


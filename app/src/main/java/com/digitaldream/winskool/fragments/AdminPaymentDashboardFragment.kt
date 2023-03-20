package com.digitaldream.winskool.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.winskool.R
import com.digitaldream.winskool.activities.Login
import com.digitaldream.winskool.activities.PaymentActivity
import com.digitaldream.winskool.adapters.AdminPaymentDashboardAdapter
import com.digitaldream.winskool.adapters.OnItemClickListener
import com.digitaldream.winskool.models.AdminPaymentModel
import com.digitaldream.winskool.utils.FunctionUtils
import com.digitaldream.winskool.utils.FunctionUtils.requestToServer
import com.digitaldream.winskool.utils.VolleyCallback
import org.json.JSONObject
import java.util.*


class AdminPaymentDashboardFragment : Fragment(),
    OnItemClickListener {

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
    private lateinit var mErrorView: LinearLayout
    private lateinit var mRefreshBtn: Button
    private lateinit var mMenu: Menu

    private val mTransactionList = mutableListOf<AdminPaymentModel>()
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

        // setUpMenu()

        actionBar!!.apply {
            setHomeButtonEnabled(true)
            title = "Payment"
            setHomeAsUpIndicator(R.drawable.arrow_left)
            setDisplayHomeAsUpEnabled(true)
        }

        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
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
        mErrorView = view.findViewById(R.id.error_view)
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
        val term = sharedPreferences.getString("term", "")
        val year = sharedPreferences.getString("school_year", "")

        val url = "${Login.urlBase}/manageTransactions.php?dashboard=1&&term=$term&&year=$year"
        val hashMap = hashMapOf<String, String>()

        requestToServer(Request.Method.GET, url, requireContext(), hashMap,
            object : VolleyCallback {
                override fun onResponse(response: String) {

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
                                FunctionUtils.currencyFormat(receiptsSum.toDouble())
                            ).also { mReceivedAmount.text = it }

                            String.format(
                                Locale.getDefault(), "%s%s", getString(R.string.naira),
                                FunctionUtils.currencyFormat(invoiceSum.toDouble())
                            ).also { mExpectedAmount.text = it }

                            val debtSum = invoiceSum.toLong() - receiptsSum.toLong()

                            String.format(
                                Locale.getDefault(), "%s%s", getString(R.string.naira),
                                FunctionUtils.currencyFormat(debtSum.toDouble())
                            ).also { mDebtAmount.text = it }
                        }

                        for (i in 0 until transactionsArray.length()) {
                            val transactionsObject = transactionsArray.getJSONObject(i)
                            val transactionType = transactionsObject.getString("trans_type")
                            val reference = transactionsObject.getString("reference")
                            val description = transactionsObject.getString("description")
                            val amount = transactionsObject.getString("amount")
                            val date = transactionsObject.getString("date")

                            val adminModel = AdminPaymentModel()
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
                            mErrorView.isVisible = false
                        } else {
                            mMainLayout.isVisible = true
                            mTransactionMessage.isVisible = false
                            mTransactionImage.isVisible = false
                            mErrorView.isVisible = false
                        }
                        mAdapter.notifyDataSetChanged()

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(error: VolleyError) {
                    mMainLayout.isVisible = false
                    mErrorView.isVisible = true
                }
            }
        )
    }

    private fun setUpMenu() {
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.settings_menu, menu)
                mMenu = menu
                menu.getItem(0).isVisible = false
                menu.getItem(2).isVisible = false
                menu.getItem(1).isVisible = true
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
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
        setUpMenu()
    }

    override fun onItemClick(position: Int) {
        Toast.makeText(requireContext(), ":)", Toast.LENGTH_SHORT).show()
    }
}
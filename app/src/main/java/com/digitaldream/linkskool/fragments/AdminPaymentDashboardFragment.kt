package com.digitaldream.linkskool.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
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
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.activities.PaymentActivity
import com.digitaldream.linkskool.adapters.AdminPaymentDashboardAdapter
import com.digitaldream.linkskool.adapters.OnItemClickListener
import com.digitaldream.linkskool.dialog.AdminClassesDialog
import com.digitaldream.linkskool.models.AdminPaymentModel
import com.digitaldream.linkskool.utils.FunctionUtils.currencyFormat
import com.digitaldream.linkskool.utils.FunctionUtils.requestToServer
import com.digitaldream.linkskool.utils.VolleyCallback
import org.json.JSONObject
import java.util.Locale


class AdminPaymentDashboardFragment : Fragment(),
    OnItemClickListener {

    private lateinit var menuHost: MenuHost
    private lateinit var mMainLayout: LinearLayout
    private lateinit var mExpectedAmount: TextView
    private lateinit var mReceivedBtn: LinearLayout
    private lateinit var mDebtBtn: LinearLayout
    private lateinit var mDebtTxt: TextView
    private lateinit var mReceivedTxt: TextView
    private lateinit var mReceiptBtn: CardView
    private lateinit var mExpenditureBtn: CardView
    private lateinit var mRecyclerLayout: CardView
    private lateinit var mSeeAllBtn: CardView
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mTransactionMessage: TextView
    private lateinit var mErrorMessage: TextView
    private lateinit var mTransactionImage: ImageView
    private lateinit var mHideAndSee: ImageView
    private lateinit var mErrorView: LinearLayout
    private lateinit var mRefreshBtn: Button

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

        setUpMenu()

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
        mReceivedBtn = view.findViewById(R.id.received_btn)
        mRecyclerView = view.findViewById(R.id.transaction_recycler)
        mDebtBtn = view.findViewById(R.id.debt_btn)
        mDebtTxt = view.findViewById(R.id.debt_txt)
        mReceivedTxt = view.findViewById(R.id.received_txt)
        mTransactionImage = view.findViewById(R.id.error_image)
        mTransactionMessage = view.findViewById(R.id.transaction_error_message)
        mErrorMessage = view.findViewById(R.id.error_message)
        mHideAndSee = view.findViewById(R.id.hide_and_See)
        mExpenditureBtn = view.findViewById(R.id.expenditure_btn)
        mReceiptBtn = view.findViewById(R.id.receipt_btn)
        mRecyclerLayout = view.findViewById(R.id.recycler_layout)
        mExpectedAmount = view.findViewById(R.id.expected_revenue)
        mSeeAllBtn = view.findViewById(R.id.see_all_btn)
        mErrorView = view.findViewById(R.id.error_view)
        mRefreshBtn = view.findViewById(R.id.refresh_btn)

        mAdapter = AdminPaymentDashboardAdapter(requireContext(), mTransactionList, this)
        mRecyclerView.hasFixedSize()
        mRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        mRecyclerView.adapter = mAdapter

        refreshData()

        buttonsCLick()


        return view
    }

    private fun buttonsCLick() {

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

        mReceivedBtn.setOnClickListener {
            AdminClassesDialog(requireContext(), "received", "", null)
                .apply {
                    setCancelable(true)
                    show()
                }.window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
        }

        mDebtBtn.setOnClickListener {
            AdminClassesDialog(requireContext(), "debt", "", null)
                .apply {
                    setCancelable(true)
                    show()
                }.window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
        }

    }


    private fun getDashboardDetails() {
        val sharedPreferences =
            requireContext().getSharedPreferences("loginDetail", Context.MODE_PRIVATE)
        val term = sharedPreferences.getString("term", "")
        val year = sharedPreferences.getString("school_year", "")
        val isHide = sharedPreferences.getBoolean("hide", false)

        val url = "${getString(R.string.base_url)}/manageTransactions" +
                ".php?dashboard=1&&term=$term&&year=$year"
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

                        hideAndShowBalance(isHide, invoiceSum, receiptsSum)

                        for (i in 0 until transactionsArray.length()) {
                            val transactionsObject = transactionsArray.getJSONObject(i)
                            val transactionType = transactionsObject.getString("trans_type")
                          //  val reference = transactionsObject.getString("reference")
                            val description = transactionsObject.getString("description")
                            val amount = transactionsObject.getString("amount")
                            val date = transactionsObject.getString("date")

                            val adminModel = AdminPaymentModel()
                            adminModel.mTransactionName = transactionType
                            adminModel.mDescription = description
                            adminModel.mReceivedAmount = amount
                            adminModel.mTransactionDate = date
                            mTransactionList.add(adminModel)
                            mTransactionList.sortByDescending { it.mTransactionDate }
                        }

                        if (mTransactionList.isEmpty()) {
                            mMainLayout.isVisible = true
                            mTransactionMessage.isVisible = true
                            mTransactionImage.isVisible = true
                            mErrorView.isVisible = false
                            mRecyclerLayout.isVisible = false
                        } else {
                            mMainLayout.isVisible = true
                            mTransactionMessage.isVisible = false
                            mTransactionImage.isVisible = false
                            mErrorView.isVisible = false
                            mRecyclerLayout.isVisible = true
                        }
                        mAdapter.notifyDataSetChanged()

                    } catch (e: Exception) {
                        e.printStackTrace()
                        mMainLayout.isVisible = false
                        mErrorView.isVisible = true
                        mRefreshBtn.isVisible = false
                        "An error occurred, please contact your developer for more info".also {
                            mErrorMessage.text = it
                        }

                    }
                }

                override fun onError(error: VolleyError) {
                    mMainLayout.isVisible = false
                    mErrorView.isVisible = true
                }
            }
        )
    }


    private fun hideAndShowBalance(isHide: Boolean, balance: String, paid: String) {

        try {
            val notPaid = balance.toDouble() - paid.toDouble()

            val debt = if (notPaid == 0.0 || paid == "null") {
                getString(R.string.zero_balance)
            } else {
                String.format(
                    Locale.getDefault(), "%s%s", getString(R.string.naira),
                    currencyFormat(notPaid)
                )
            }

            val amount = if (balance == "null") {
                getString(R.string.zero_balance)
            } else {
                String.format(
                    Locale.getDefault(), "%s%s", getString(R.string.naira),
                    currencyFormat(balance.toDouble())
                )
            }

            val income = if (paid == "null") {
                getString(R.string.zero_balance)
            } else {
                String.format(
                    Locale.getDefault(), "%s%s", getString(R.string.naira),
                    currencyFormat(paid.toDouble())
                )
            }

            if (isHide) {
                mHideAndSee.setImageResource(R.drawable.ic_visibility_off)
                mExpectedAmount.text = getString(R.string.hide)
                mDebtTxt.text = getString(R.string.hide)
                mReceivedTxt.text = getString(R.string.hide)
            } else {
                mHideAndSee.setImageResource(R.drawable.ic_eye_view)
                mExpectedAmount.text = amount
                mDebtTxt.text = debt
                mReceivedTxt.text = income
            }

            var mHide = isHide

            mHideAndSee.setOnClickListener {

                if (mHide) {
                    mExpectedAmount.text = amount
                    mDebtTxt.text = debt
                    mReceivedTxt.text = income
                    mHideAndSee.setImageResource(R.drawable.ic_eye_view)
                } else {
                    mExpectedAmount.text = getString(R.string.hide)
                    mDebtTxt.text = getString(R.string.hide)
                    mReceivedTxt.text = getString(R.string.hide)
                    mHideAndSee.setImageResource(R.drawable.ic_visibility_off)
                }
                mHide = !mHide

                requireContext().getSharedPreferences("loginDetail", Context.MODE_PRIVATE).edit()
                    .putBoolean("hide", mHide).apply()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun setUpMenu() {
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.settings_menu, menu)

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

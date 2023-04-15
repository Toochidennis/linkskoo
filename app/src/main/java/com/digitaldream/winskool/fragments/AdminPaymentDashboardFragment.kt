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
import com.digitaldream.winskool.activities.PaymentActivity
import com.digitaldream.winskool.adapters.AdminPaymentDashboardAdapter
import com.digitaldream.winskool.adapters.OnItemClickListener
import com.digitaldream.winskool.dialog.AdminClassesDialog
import com.digitaldream.winskool.models.AdminPaymentModel
import com.digitaldream.winskool.utils.FunctionUtils.currencyFormat
import com.digitaldream.winskool.utils.FunctionUtils.requestToServer
import com.digitaldream.winskool.utils.VolleyCallback
import org.json.JSONObject
import java.util.*


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
            AdminClassesDialog(requireContext(), "payment", "", null)
                .apply {
                    setCancelable(true)
                    show()
                }.window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
        }

        mDebtBtn.setOnClickListener {
            AdminClassesDialog(requireContext(), "payment", "", null)
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

/*
* <?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:ads="http://schemas.android.com/apk/res-auto"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".fragments.AdminPaymentDashboardFragment">

    <RelativeLayout
        android:id="@+id/appBar"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="@drawable/background_gradient_color"
        android:padding="10dp">

        <ImageView
            android:id="@+id/back_btn"
            android:layout_width="36dp"
            android:layout_height="36dp"
            android:layout_alignParentStart="true"
            android:layout_alignParentTop="true"
            android:layout_marginTop="10dp"
            android:clickable="true"
            android:contentDescription="@string/todo"
            android:focusable="true"
            android:foreground="?android:attr/selectableItemBackgroundBorderless"
            android:src="@drawable/arrow_left" />

        <TextView
            android:id="@+id/toolbar_text"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_alignParentTop="true"
            android:layout_marginStart="20dp"
            android:layout_marginTop="16dp"
            android:layout_marginEnd="10dp"
            android:layout_toStartOf="@id/settings_btn"
            android:layout_toEndOf="@id/back_btn"
            android:fontFamily="@font/montserrat"
            android:textColor="@color/white"
            android:textSize="16sp"
            android:textStyle="bold" />

        <ImageView
            android:id="@+id/settings_btn"
            android:layout_width="32dp"
            android:layout_height="32dp"
            android:layout_alignBaseline="@id/back_btn"
            android:layout_alignParentEnd="true"
            android:layout_marginTop="10dp"
            android:clickable="true"
            android:contentDescription="@string/todo"
            android:focusable="true"
            android:foreground="?android:attr/selectableItemBackgroundBorderless"
            android:src="@drawable/ic_setting_white_24dp" />

    </RelativeLayout>

    <androidx.core.widget.NestedScrollView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_below="@id/appBar">

        <LinearLayout
            android:id="@+id/dashboard_view"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:visibility="visible">

            <RelativeLayout
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:background="@drawable/background_gradient_color"
                android:padding="15dp">

                <TextView
                    android:id="@+id/revenue_label"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_alignParentStart="true"
                    android:layout_alignParentTop="true"
                    android:layout_marginTop="20dp"
                    android:fontFamily="@font/montserrat"
                    android:text="Expected Revenue"
                    android:textColor="@color/icons_color"
                    android:textSize="14sp" />

                <ImageView
                    android:id="@+id/hide_and_See"
                    android:layout_width="32dp"
                    android:layout_height="32dp"
                    android:layout_alignParentTop="true"
                    android:layout_alignParentEnd="true"
                    android:layout_marginTop="20dp"
                    android:clickable="true"
                    android:contentDescription="@string/todo"
                    android:focusable="true"
                    android:foreground="?android:attr/selectableItemBackgroundBorderless"
                    android:src="@drawable/ic_eye_view"
                    app:tint="@color/white" />

                <TextView
                    android:id="@+id/expected_revenue"
                    android:layout_width="0dp"
                    android:layout_height="wrap_content"
                    android:layout_below="@id/revenue_label"
                    android:layout_alignParentStart="true"
                    android:layout_alignParentEnd="true"
                    android:layout_marginTop="10dp"
                    android:fontFamily="@font/montserrat"
                    android:text="@string/zero_balance"
                    android:textColor="@color/white"
                    android:textSize="28sp"
                    android:textStyle="bold" />

                <LinearLayout
                    android:id="@+id/btn_layout"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_below="@id/expected_revenue"
                    android:layout_marginTop="15dp"
                    android:orientation="horizontal">

                    <androidx.cardview.widget.CardView
                        android:layout_width="0dp"
                        android:layout_height="wrap_content"
                        android:layout_marginEnd="15dp"
                        android:layout_weight="1"
                        android:backgroundTint="@color/test_color_1"
                        ads:cardCornerRadius="40dp"
                        ads:cardElevation="1dp"
                        android:foreground="?android:attr/selectableItemBackgroundBorderless"
                        android:clickable="true"
                        android:focusable="true"
                        ads:contentPadding="25dp">

                        <ImageView
                            android:layout_width="32dp"
                            android:layout_height="32dp"
                            android:layout_gravity="center"
                            android:contentDescription="@string/todo"
                            android:src="@drawable/ic_eye_view"
                            app:tint="@color/green_cyan" />


                    </androidx.cardview.widget.CardView>

                    <androidx.cardview.widget.CardView
                        android:layout_width="0dp"
                        android:layout_height="wrap_content"
                        android:layout_marginEnd="15dp"
                        android:layout_weight="1"
                        android:backgroundTint="@color/test_color_1"
                        ads:cardCornerRadius="40dp"
                        ads:cardElevation="1dp"
                        ads:contentPadding="25dp">

                        <ImageView
                            android:layout_width="32dp"
                            android:layout_height="32dp"
                            android:layout_gravity="center"
                            android:contentDescription="@string/todo"
                            android:src="@drawable/ic_eye_view"
                            app:tint="@color/green_cyan" />


                    </androidx.cardview.widget.CardView>

                    <androidx.cardview.widget.CardView
                        android:layout_width="0dp"
                        android:layout_height="wrap_content"
                        android:layout_marginEnd="15dp"
                        android:layout_weight="1"
                        android:backgroundTint="@color/test_color_1"
                        ads:cardCornerRadius="40dp"
                        ads:cardElevation="1dp"
                        ads:contentPadding="25dp">

                        <ImageView
                            android:layout_width="32dp"
                            android:layout_height="32dp"
                            android:layout_gravity="center"
                            android:contentDescription="@string/todo"
                            android:src="@drawable/ic_eye_view"
                            app:tint="@color/green_cyan" />


                    </androidx.cardview.widget.CardView>

                    <androidx.cardview.widget.CardView
                        android:layout_width="0dp"
                        android:layout_height="wrap_content"
                        android:layout_weight="1"
                        android:backgroundTint="@color/test_color_1"
                        ads:cardCornerRadius="40dp"
                        ads:cardElevation="1dp"
                        ads:contentPadding="25dp">

                        <ImageView
                            android:layout_width="32dp"
                            android:layout_height="32dp"
                            android:layout_gravity="center"
                            android:contentDescription="@string/todo"
                            android:src="@drawable/ic_eye_view"
                            app:tint="@color/green_cyan" />


                    </androidx.cardview.widget.CardView>

                </LinearLayout>

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_below="@id/btn_layout"
                    android:orientation="horizontal"
                    android:padding="15dp">

                    <TextView
                        android:id="@+id/received_txt"
                        android:layout_width="0dp"
                        android:layout_height="wrap_content"
                        android:layout_marginEnd="15dp"
                        android:layout_weight="1"
                        android:fontFamily="@font/montserrat"
                        android:text="Received"
                        android:textColor="@color/white"
                        android:textSize="12sp" />

                    <TextView
                        android:id="@+id/expenditure_txt"
                        android:layout_width="0dp"
                        android:layout_height="wrap_content"
                        android:layout_marginEnd="20dp"
                        android:layout_weight="1"
                        android:fontFamily="@font/montserrat"
                        android:text="@string/expenditure"
                        android:textColor="@color/white"
                        android:textSize="12sp" />

                    <TextView
                        android:id="@+id/receipt_txt"
                        android:layout_width="0dp"
                        android:layout_height="wrap_content"
                        android:layout_weight="1"
                        android:fontFamily="@font/montserrat"
                        android:text="Receipt"
                        android:textAlignment="center"
                        android:textColor="@color/white"
                        android:textSize="12sp" />

                    <TextView
                        android:id="@+id/debt_txt"
                        android:layout_width="0dp"
                        android:layout_height="wrap_content"
                        android:layout_marginStart="15dp"
                        android:layout_weight="1"
                        android:fontFamily="@font/montserrat"
                        android:text="Not paid"
                        android:textAlignment="textEnd"
                        android:textColor="@color/white"
                        android:textSize="12sp" />

                </LinearLayout>


            </RelativeLayout>


            <RelativeLayout
                android:id="@+id/history_view"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_margin="15dp">

                <TextView
                    android:id="@+id/history_text"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_alignParentStart="true"
                    android:layout_alignParentTop="true"
                    android:layout_alignParentEnd="true"
                    android:fontFamily="@font/poppins_regular"
                    android:text="Transactions"
                    android:textColor="@color/text_bg_color"
                    android:textSize="16sp"/>

                <androidx.cardview.widget.CardView
                    android:id="@+id/see_all_btn"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_alignParentTop="true"
                    android:layout_alignParentEnd="true"
                    android:clickable="true"
                    android:focusable="true"
                    android:foreground="?android:attr/selectableItemBackgroundBorderless"
                    app:cardCornerRadius="5dp">

                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:layout_gravity="center"
                        android:background="@color/test_color_1"
                        android:fontFamily="@font/montserrat"
                        android:paddingStart="10dp"
                        android:paddingTop="5dp"
                        android:paddingEnd="10dp"
                        android:paddingBottom="5dp"
                        android:text="@string/see_all"
                        android:textColor="@color/white"
                        android:textSize="12sp" />

                </androidx.cardview.widget.CardView>

                <androidx.cardview.widget.CardView
                    android:id="@+id/recycler_layout"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_below="@id/history_text"
                    android:layout_marginTop="@dimen/dimen_10"
                    android:layout_marginBottom="55dp"
                    ads:cardCornerRadius="@dimen/dimen_10"
                    ads:cardElevation="5dp">

                    <androidx.recyclerview.widget.RecyclerView
                        android:id="@+id/transaction_recycler"
                        android:layout_width="match_parent"
                        android:layout_height="match_parent"
                        android:layout_marginTop="@dimen/dimen_10"
                        android:clipToPadding="true" />

                </androidx.cardview.widget.CardView>

                <ImageView
                    android:id="@+id/error_image"
                    android:layout_width="80dp"
                    android:layout_height="80dp"
                    android:layout_centerInParent="true"
                    android:layout_marginTop="150dp"
                    android:contentDescription="@string/todo"
                    android:src="@drawable/baseline_money_off_24"
                    android:visibility="gone"
                    app:tint="@color/color_7" />

                <TextView
                    android:id="@+id/transaction_error_message"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginStart="0dp"
                    android:layout_marginTop="200dp"
                    android:layout_marginEnd="0dp"
                    android:fontFamily="@font/montserrat"
                    android:lines="2"
                    android:text="@string/no_history"
                    android:textAlignment="center"
                    android:textColor="@color/text_bg_color"
                    android:textSize="14sp"
                    android:visibility="gone" />

            </RelativeLayout>

        </LinearLayout>

    </androidx.core.widget.NestedScrollView>

    <LinearLayout
        android:id="@+id/error_view"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_centerInParent="true"
        android:layout_marginStart="15dp"
        android:layout_marginEnd="15dp"
        android:gravity="center"
        android:orientation="vertical"
        android:visibility="gone">

        <TextView
            android:id="@+id/error_message"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:fontFamily="@font/poppins_medium"
            android:lines="2"
            android:text="@string/can_not_retrieve"
            android:textAlignment="center"
            android:textColor="@color/black"
            android:textSize="18sp" />

        <Button
            android:id="@+id/refresh_btn"
            android:layout_width="150dp"
            android:layout_height="50dp"
            android:layout_marginTop="16dp"
            android:background="@drawable/ripple_effect"
            android:clickable="true"
            android:focusable="true"
            android:text="@string/refresh_layout"
            android:textColor="@color/color_5"
            android:textSize="14sp" />
    </LinearLayout>

</RelativeLayout>
*
* */

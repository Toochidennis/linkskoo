package com.digitaldream.winskool.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
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
import com.digitaldream.winskool.activities.PaymentActivity
import com.digitaldream.winskool.adapters.ExpenditureHistoryAdapter
import com.digitaldream.winskool.adapters.OnItemClickListener
import com.digitaldream.winskool.dialog.ExpenditureTimeFrameBottomSheet
import com.digitaldream.winskool.dialog.TermSessionPickerBottomSheet
import com.digitaldream.winskool.models.ChartModel
import com.digitaldream.winskool.models.ExpenditureHistoryModel
import com.digitaldream.winskool.models.TimeFrameDataModel
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
    private lateinit var mOpenBtn: FloatingActionButton
    private lateinit var mAddExpenditureBtn1: Button
    private lateinit var mAddExpenditureBtn2: ImageButton
    private lateinit var mSetupReportBtn1: Button
    private lateinit var mSetupReportBtn2: ImageButton
    private lateinit var mSetupLayout: LinearLayout
    private lateinit var mExpenditureLayout: LinearLayout


    private var mGraphicalView: GraphicalView? = null
    private val mExpenditureList = mutableListOf<ExpenditureHistoryModel>()
    private val mGraphList = mutableListOf<ChartModel>()
    private lateinit var mAdapter: ExpenditureHistoryAdapter
    private lateinit var mTimeFrameDataModel: TimeFrameDataModel

    private var isOpen = false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find child views in the inflated layout by their IDs
        view.apply {
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            mExpenditureView = findViewById(R.id.expenditure_view)
            mExpenditureChart = findViewById(R.id.expenditure_chart)
            mExpenditureSum = findViewById(R.id.expenditure_sum)
            mExpenditureCount = findViewById(R.id.expenditure_count)
            mRecyclerView = findViewById(R.id.expenditure_recycler)
            mExpenditureMessage = findViewById(R.id.expenditure_error_message)
            mExpenditureImage = findViewById(R.id.error_image)
            mErrorView = findViewById(R.id.error_view)
            mRefreshBtn = findViewById(R.id.refresh_btn)
            mTimeFrameBtn = findViewById(R.id.time_frame_btn)
            mTermBtn = findViewById(R.id.term_btn)
            mOpenBtn = findViewById(R.id.open_btn)
            mAddExpenditureBtn1 = findViewById(R.id.add_expenditure_btn1)
            mAddExpenditureBtn2 = findViewById(R.id.add_expenditure_btn2)
            mSetupReportBtn1 = findViewById(R.id.setup_btn1)
            mSetupReportBtn2 = findViewById(R.id.setup_btn2)
            mSetupLayout = findViewById(R.id.setup_layout)
            mExpenditureLayout = findViewById(R.id.add_expenditure_layout)


            toolbar.apply {
                title = "Expenditures"
                setNavigationIcon(R.drawable.arrow_left)
                setNavigationOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
            }

        }


        mAdapter = ExpenditureHistoryAdapter(
            requireContext(),
            mExpenditureList,
            this
        )

//
//        GenericAdapter(
//            mExpenditureList,
//            R.layout.fragment_history_expenditure_item,
//            bindItem = { itemView, model ->
//
//                mExpenditureName = itemView.findViewById(R.id.expenditure_name)
//                val mExpenditureDate: TextView = itemView.findViewById(R.id.expenditure_date)
//                val mExpenditureAmount: TextView = itemView.findViewById(R.id.expenditure_amount)
//                val mExpenditureType: TextView = itemView.findViewById(R.id.expenditure_type)
//
//
//                mExpenditureName.text = model.getDate()
//
//            },
//
//            onItemClick = {
//
//            }
//        )

        mRecyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
            isAnimating
        }

        refreshData()

        mTimeFrameDataModel = TimeFrameDataModel { getTimeFrameData() }

        performButtonsClick()

    }


    private fun performButtonsClick() {

        val btnOpen = AnimationUtils.loadAnimation(requireContext(), R.anim.fab_open)
        val btnClose = AnimationUtils.loadAnimation(requireContext(), R.anim.fab_close)

        val rotateForward = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_forward)
        val rotateBackward = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_backward)

        val arrayList = arrayListOf(rotateBackward, btnClose)

        mOpenBtn.setOnClickListener {

            if (isOpen) {
                closeBtnAnimation(arrayList)

            } else {
                mOpenBtn.startAnimation(rotateForward)
                mExpenditureLayout.startAnimation(btnOpen)
                mSetupLayout.startAnimation(btnOpen)

                mAddExpenditureBtn1.isClickable = true
                mAddExpenditureBtn2.isClickable = true

                mSetupReportBtn1.isClickable = true
                mSetupReportBtn2.isClickable = true

                mExpenditureLayout.isVisible = true
                mSetupLayout.isVisible = true

                isOpen = true
            }
        }


        //open expenditure fragment
        mAddExpenditureBtn1.setOnClickListener {
            startActivity(
                Intent(context, PaymentActivity::class.java)
                    .putExtra("from", "add_expenditure")
            )
            closeBtnAnimation(arrayList)
        }


        //open expenditure fragment
        mAddExpenditureBtn2.setOnClickListener {
            startActivity(
                Intent(context, PaymentActivity::class.java)
                    .putExtra("from", "add_expenditure")
            )
            closeBtnAnimation(arrayList)
        }


        //open time frame dialog
        mSetupReportBtn1.setOnClickListener {
            timeFrameDialog()

            closeBtnAnimation(arrayList)
        }


        //open time frame dialog
        mSetupReportBtn2.setOnClickListener {
            timeFrameDialog()

            closeBtnAnimation(arrayList)
        }


        //open time frame dialog
        mTimeFrameBtn.setOnClickListener {
            timeFrameDialog()
        }


        //open term /session dialog
        mTermBtn.setOnClickListener {
            TermSessionPickerBottomSheet().show(
                requireActivity().supportFragmentManager,
                "Term/Session"
            )
        }

    }


    private fun timeFrameDialog() {
        ExpenditureTimeFrameBottomSheet(mTimeFrameDataModel).show(
            childFragmentManager, "time frame"
        )
    }


    private fun closeBtnAnimation(arrayList: ArrayList<Animation>) {
        mOpenBtn.startAnimation(arrayList[0])
        mExpenditureLayout.startAnimation(arrayList[1])
        mSetupLayout.startAnimation(arrayList[1])

        mAddExpenditureBtn1.isClickable = false
        mAddExpenditureBtn2.isClickable = false

        mSetupReportBtn1.isClickable = false
        mSetupReportBtn2.isClickable = false

        isOpen = false
    }


    private fun getTimeFrameData() {

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
                            mGraphList.sortBy { it.label }
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

                            val session = "${receiptYear.toInt() - 1}/$receiptYear"

                            ExpenditureHistoryModel().apply {
                                this.vendorName = vendorName
                                this.amount = expenditureAmount
                                this.date = date
                                this.session = session
                                this.type = transactionType
                                this.referenceNumber = reference
                                this.term = receiptTerm
                                this.phone = telephone
                            }.let {
                                mExpenditureList.apply {
                                    add(it)
                                    sortByDescending { it.date }
                                }
                            }


                        }
                        if (mExpenditureList.isEmpty()) {
                            mExpenditureView.isVisible = true
                            mExpenditureMessage.isVisible = true
                            mOpenBtn.isVisible = true
                            mErrorView.isVisible = false
                        } else {
                            mExpenditureView.isVisible = true
                            mExpenditureMessage.isVisible = false
                            mOpenBtn.isVisible = true
                            mErrorView.isVisible = false
                        }
                        mAdapter.notifyDataSetChanged()

                    } else {
                        mExpenditureView.isVisible = true
                        mExpenditureMessage.isVisible = true
                        mOpenBtn.isVisible = true
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                override fun onError(error: VolleyError) {
                    mExpenditureView.isVisible = false
                    mOpenBtn.isVisible = false
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
                .putExtra("amount", model.amount)
                .putExtra("vendor_name", model.vendorName)
                .putExtra("vendor_phone", model.phone)
                .putExtra("reference", model.referenceNumber)
                .putExtra("session", model.session)
                .putExtra("term", model.term)
                .putExtra("date", model.date)
                .putExtra("description", model.type)
                .putExtra("from", "admin_expenditure")
        )

    }

}
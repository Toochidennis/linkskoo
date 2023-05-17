package com.digitaldream.winskool.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuHost
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.winskool.R
import com.digitaldream.winskool.activities.PaymentActivity
import com.digitaldream.winskool.adapters.OnItemClickListener
import com.digitaldream.winskool.adapters.ReceiptsHistoryAdapter
import com.digitaldream.winskool.dialog.ReceiptTimeFrameBottomSheet
import com.digitaldream.winskool.dialog.TermFeeDialog
import com.digitaldream.winskool.dialog.TermSessionPickerBottomSheet
import com.digitaldream.winskool.models.AdminPaymentModel
import com.digitaldream.winskool.models.ChartModel
import com.digitaldream.winskool.models.TimeFrameDataModel
import com.digitaldream.winskool.utils.FunctionUtils
import com.digitaldream.winskool.utils.FunctionUtils.getDate
import com.digitaldream.winskool.utils.FunctionUtils.plotLineChart
import com.digitaldream.winskool.utils.FunctionUtils.requestToServer
import com.digitaldream.winskool.utils.VolleyCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.achartengine.GraphicalView
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale


class ReceiptsHistoryFragment : Fragment(R.layout.fragment_receipts_history), OnItemClickListener {

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
    private lateinit var mTimeFrameBtn: Button
    private lateinit var mTermBtn: Button

    private lateinit var mMenuHost: MenuHost

    private var mGraphicalView: GraphicalView? = null
    private val mReceiptList = mutableListOf<AdminPaymentModel>()
    private val mGraphList = arrayListOf<ChartModel>()
    private lateinit var mAdapter: ReceiptsHistoryAdapter
    private lateinit var timeFrameDataModel: TimeFrameDataModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        mTimeFrameBtn = view.findViewById(R.id.time_frame_btn)
        mTermBtn = view.findViewById(R.id.term_btn)


        toolbar.apply {
            title = "Receipts"
            this.setNavigationIcon(R.drawable.arrow_left)
            setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher
                    .onBackPressed()
            }
        }


        mMenuHost = requireActivity()

        mAdapter = ReceiptsHistoryAdapter(requireContext(), mReceiptList, this)

        mRecyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(requireContext())
            isAnimating
            adapter = mAdapter
        }


        refreshData()

        receiptsDialog()


        timeFrameDataModel = TimeFrameDataModel { getTimeFrameData() }

        mTimeFrameBtn.setOnClickListener {
            ReceiptTimeFrameBottomSheet(
                timeFrameDataModel
            ).show(requireActivity().supportFragmentManager, "Time Frame")
        }

        mTermBtn.setOnClickListener {
            TermSessionPickerBottomSheet().show(
                requireActivity().supportFragmentManager,
                "Term/Session"
            )
        }


        timeFrameTitle()

    }

    private fun getReceipts() {

        val sharedPreferences =
            requireContext().getSharedPreferences("loginDetail", Context.MODE_PRIVATE)
        val term = sharedPreferences.getString("term", "")
        val year = sharedPreferences.getString("school_year", "")

        val url = "${getString(R.string.base_url)}/manageTransactions" +
                ".php?type=receipts&&term=$term&&year=$year&&dateRange=custom&&startDate=2023-03" +
                "-01&&endDate=2023-04-06"
        val hashMap = hashMapOf<String, String>()

        requestToServer(Request.Method.GET, url, requireContext(), hashMap,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    try {
                        //clear list
                        mReceiptList.clear()

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
                                    "Month/Year"
                                )
                                mReceiptChart.addView(mGraphicalView)
                            } else {
                                mGraphicalView!!.repaint()
                            }
                        } else {
                            mGraphicalView = plotLineChart(
                                mGraphList,
                                requireContext(),
                                "Received",
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
                                receiptModel.mStudentName = studentName
                                receiptModel.mTransactionName = transactionType
                                receiptModel.mReferenceNumber = reference
                                receiptModel.mRegistrationNumber = registrationNo
                                receiptModel.mReceivedAmount = receiptAmount
                                receiptModel.mTransactionDate = date
                                receiptModel.mTerm = receiptTerm
                                receiptModel.mSession = session
                                receiptModel.mLevelName = levelName
                                receiptModel.mClassName = className

                                mReceiptList.add(receiptModel)
                                mReceiptList.sortByDescending { it.mTransactionDate }
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
                    mAdapter.notifyDataSetChanged()
                }

                override fun onError(error: VolleyError) {
                    mReceiptView.isVisible = false
                    mErrorView.isVisible = true
                    mAddReceipt.isVisible = false
                }
            }
        )
    }

    private fun getTimeFrameData() {
        timeFrameDataModel.endDate
        //   Log.d("time frame",  timeFrameData.others.toString())
    }


    private fun refreshData() {
        mRefreshBtn.setOnClickListener {
            getReceipts()
        }
    }


    private fun receiptsDialog() {
        mAddReceipt.setOnClickListener {
            TermFeeDialog(requireContext(), "receipts", null)
                .apply {
                    setCancelable(true)
                    show()
                }.window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
        }

    }

    /*  private fun setUpMenu() {
          mMenuHost.addMenuProvider(object : MenuProvider {
              override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                  menuInflater.inflate(R.menu.search_menu, menu)

                  val searchItem = menu.findItem(R.id.search)
                  val searchView = searchItem.actionView as SearchView

                  searchView.imeOptions = EditorInfo.IME_ACTION_DONE

                  searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                      override fun onQueryTextSubmit(query: String?): Boolean {
                          return false
                      }

                      override fun onQueryTextChange(newText: String?): Boolean {
                          mAdapter.filter.filter(newText)
                          return false
                      }
                  })
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
      }*/

    private fun timeFrameTitle() {
        try {

            val simpleDateFormat = SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            )

            val parseDate = simpleDateFormat.parse(getDate())!!
            val sdf = SimpleDateFormat("MMMM, yyyy", Locale.getDefault())
            mTimeFrameBtn.text = sdf.format(parseDate)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        getReceipts()

    }

    override fun onItemClick(position: Int) {
        val paymentModel = mReceiptList[position]

        startActivity(
            Intent(requireContext(), PaymentActivity::class.java)
                .putExtra("amount", paymentModel.mReceivedAmount)
                .putExtra("name", paymentModel.mStudentName)
                .putExtra("level_name", paymentModel.mLevelName)
                .putExtra("class_name", paymentModel.mClassName)
                .putExtra("reg_no", paymentModel.mRegistrationNumber)
                .putExtra("reference", paymentModel.mReferenceNumber)
                .putExtra("session", paymentModel.mSession)
                .putExtra("term", paymentModel.mTerm)
                .putExtra("date", paymentModel.mTransactionDate)
                .putExtra("from", "admin_receipt")
        )

    }
}
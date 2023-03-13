package com.digitaldream.winskool.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
import com.digitaldream.winskool.activities.TermlyFeeSetupActivity
import com.digitaldream.winskool.adapters.OnItemClickListener
import com.digitaldream.winskool.adapters.ReceiptStudentBudgetAdapter
import com.digitaldream.winskool.adapters.StudentFeesDetailsAdapter
import com.digitaldream.winskool.dialog.AddReceiptDialog
import com.digitaldream.winskool.models.StudentPaymentModel
import com.digitaldream.winskool.models.TermFeesDataModel
import com.digitaldream.winskool.utils.FunctionUtils.currencyFormat
import com.digitaldream.winskool.utils.FunctionUtils.getDate
import com.digitaldream.winskool.utils.FunctionUtils.requestToServer
import com.digitaldream.winskool.utils.VolleyCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONObject
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"
private const val ARG_PARAM4 = "param4"
private const val ARG_PARAM5 = "param5"
private const val ARG_PARAM6 = "param6"


class ReceiptStudentBudgetFragment : Fragment(), OnItemClickListener {

    private lateinit var mMainView: NestedScrollView
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mErrorMessage: TextView
    private lateinit var mErrorImage: ImageView
    private lateinit var mErrorView: LinearLayout
    private lateinit var mRefreshBtn: Button
    private lateinit var mAddBudgetBtn: FloatingActionButton
    private lateinit var mAdapter: ReceiptStudentBudgetAdapter

    private lateinit var mDetailsView: NestedScrollView
    private lateinit var mPaymentLayout: RelativeLayout
    private lateinit var mDetailsRecyclerView: RecyclerView
    private lateinit var mFeeTotal1: TextView
    private lateinit var mFeeTotal2: TextView
    private lateinit var mSchoolName: TextView
    private lateinit var mTitle: TextView
    private lateinit var mPayBtn: Button
    private lateinit var mDetailsAdapter: StudentFeesDetailsAdapter

    private var levelId: String? = null
    private var levelName: String? = null
    private var studentName: String? = null
    private var classId: String? = null
    private var studentId: String? = null
    private var regNo: String? = null
    private var mTerm: String? = null
    private var mYear: String? = null
    private var mSession: String? = null
    private var mInvoiceId: String? = null
    private var mAmount: String? = null

    private val mBudgetList = mutableListOf<StudentPaymentModel>()
    private val mFeeList = mutableListOf<TermFeesDataModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            levelId = it.getString(ARG_PARAM1)
            classId = it.getString(ARG_PARAM2)
            studentId = it.getString(ARG_PARAM3)
            regNo = it.getString(ARG_PARAM4)
            levelName = it.getString(ARG_PARAM5)
            studentName = it.getString(ARG_PARAM6)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(
            sLevelId: String,
            sClassId: String,
            sStudentId: String,
            sRegNo: String,
            sLevelName: String,
            sStudentName: String,
        ) =
            ReceiptStudentBudgetFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, sLevelId)
                    putString(ARG_PARAM2, sClassId)
                    putString(ARG_PARAM3, sStudentId)
                    putString(ARG_PARAM4, sRegNo)
                    putString(ARG_PARAM5, sLevelName)
                    putString(ARG_PARAM6, sStudentName)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_receipt_student_budget, container, false)

        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        mMainView = view.findViewById(R.id.main_view)
        mRecyclerView = view.findViewById(R.id.budget_recycler)
        mErrorView = view.findViewById(R.id.error_view)
        mErrorMessage = view.findViewById(R.id.budget_error_message)
        mRefreshBtn = view.findViewById(R.id.refresh_btn)
        mAddBudgetBtn = view.findViewById(R.id.add_receipt)
        mErrorImage = view.findViewById(R.id.error_image)

        mDetailsView = view.findViewById(R.id.details_view)
        mPaymentLayout = view.findViewById(R.id.payment_layout)
        mDetailsRecyclerView = view.findViewById(R.id.details_recycler)
        mFeeTotal1 = view.findViewById(R.id.fee_total)
        mFeeTotal2 = view.findViewById(R.id.total2)
        mSchoolName = view.findViewById(R.id.school_name)
        mTitle = view.findViewById(R.id.title)
        mPayBtn = view.findViewById(R.id.pay_btn)


        toolbar.apply {
            title = "Select Term Fee"
            setNavigationIcon(R.drawable.arrow_left)
            setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        val sharedPreferences =
            requireContext().getSharedPreferences("loginDetail", Context.MODE_PRIVATE)
        val mNameSchool = sharedPreferences.getString("school_name", "")
        mSchoolName.text = mNameSchool

        mAdapter = ReceiptStudentBudgetAdapter(requireContext(), mBudgetList, this)
        mRecyclerView.hasFixedSize()
        mRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        mRecyclerView.adapter = mAdapter

        mDetailsAdapter = StudentFeesDetailsAdapter(requireContext(), mFeeList)
        mDetailsRecyclerView.hasFixedSize()
        mDetailsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        mDetailsRecyclerView.adapter = mDetailsAdapter

        mAddBudgetBtn.setOnClickListener {
            startActivity(
                Intent(context, TermlyFeeSetupActivity()::class.java)
                    .putExtra("level_name", levelName)
                    .putExtra("level_id", levelId)
            )
        }

        refreshData()

        return view
    }

    private fun getFees() {
        val url = Login.urlBase + "/manageReceipts.php?list=$studentId"
        val hashMap = hashMapOf<String, String>()

        requestToServer(Request.Method.GET, url, requireContext(), hashMap,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    if (response == "[]") {
                        mMainView.isVisible = true
                        mErrorMessage.isVisible = true
                        mAddBudgetBtn.isVisible = true
                        "Fees not set yet. Use the button below to set!".also {
                            mErrorMessage.text = it
                        }
                    } else {
                        try {
                            val jsonObject = JSONObject(response)
                            if (jsonObject.has("invoice")) {
                                val invoiceArray = jsonObject.getJSONArray("invoice")

                                for (i in 0 until invoiceArray.length()) {
                                    val invoiceObjects = invoiceArray.getJSONObject(i)
                                    val descriptionArray =
                                        invoiceObjects.getJSONArray("description")
                                    mInvoiceId = invoiceObjects.getString("tid")
                                    mAmount =
                                        invoiceObjects.getString("amount")
                                    mYear = invoiceObjects.getString("year")
                                    mTerm = invoiceObjects.getString("term")

                                    val previousYear = mYear!!.toInt() - 1
                                    mSession =
                                        String.format(
                                            Locale.getDefault(),
                                            "%d/%s",
                                            previousYear,
                                            mYear!!
                                        )

                                    val paymentModel = StudentPaymentModel()
                                    paymentModel.setInvoiceId(mInvoiceId!!)
                                    paymentModel.setAmount(mAmount!!)
                                    paymentModel.setSession(mSession!!)
                                    paymentModel.setTerm(mTerm!!)
                                    paymentModel.setYear(mYear!!)
                                    mBudgetList.add(paymentModel)

                                    if (invoiceArray.length() == 1) {
                                        for (j in 0 until descriptionArray.length()) {
                                            val descriptionObject = descriptionArray
                                                .getJSONObject(j)
                                            val feeName = descriptionObject.getString("fee_name")
                                            val feeAmount =
                                                descriptionObject.getString("fee_amount")

                                            val termText = when (mTerm) {
                                                "1" -> "First Term School Fee Charges for"
                                                "2" -> "Second Term School Fee Charges for"
                                                else -> "Third Term School Fee Charges for"
                                            }

                                            mTitle.text = String.format(
                                                Locale.getDefault(),
                                                "%s %s %s", termText, mSession, "session"
                                            )

                                            mFeeTotal1.text = String.format(
                                                Locale.getDefault(),
                                                "%s%s",
                                                requireActivity().getString(R.string.naira),
                                                currencyFormat(mAmount!!.toDouble())
                                            )

                                            mFeeTotal2.text = String.format(
                                                Locale.getDefault(),
                                                "%s%s",
                                                requireActivity().getString(R.string.naira),
                                                currencyFormat(mAmount!!.toDouble())
                                            )

                                            val termFeesDataModel = TermFeesDataModel()
                                            termFeesDataModel.setFeeName(feeName)
                                            termFeesDataModel.setFeeAmount(feeAmount)
                                            mFeeList.add(termFeesDataModel)
                                            mFeeList.sortBy { it.getFeeName() }
                                        }

                                        if (mFeeList.isNotEmpty()) {
                                            mDetailsView.isVisible = true
                                            mPaymentLayout.isVisible = true
                                            mMainView.isVisible = false
                                        }
                                        mDetailsAdapter.notifyItemChanged(mFeeList.size - 1)

                                    } else {

                                        if (mBudgetList.isEmpty()) {
                                            mErrorView.isVisible = false
                                            mDetailsView.isVisible = false
                                            mMainView.isVisible = true
                                            mErrorMessage.isVisible = true
                                            mAddBudgetBtn.isVisible = true
                                            mPaymentLayout.isVisible = false
                                        } else {
                                            mDetailsView.isVisible = false
                                            mErrorView.isVisible = false
                                            mMainView.isVisible = true
                                            mErrorMessage.isVisible = false
                                            mAddBudgetBtn.isVisible = false
                                            mPaymentLayout.isVisible = false
                                        }
                                        mAdapter.notifyItemChanged(mBudgetList.size - 1)
                                    }

                                }
                            } else {
                                mMainView.isVisible = true
                                mErrorMessage.isVisible = true
                                mAddBudgetBtn.isVisible = false
                                mPaymentLayout.isVisible = false
                                "$studentName have paid!".also {
                                    mErrorMessage
                                        .text = it
                                }
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onError(error: VolleyError) {
                    mMainView.isVisible = false
                    mErrorView.isVisible = true
                    mAddBudgetBtn.isVisible = false
                    mPaymentLayout.isVisible = false
                }
            })
    }


    private fun refreshData() {
        mRefreshBtn.setOnClickListener {
            mBudgetList.clear()
            mFeeList.clear()
            getFees()
        }
    }

    override fun onResume() {
        super.onResume()
        mBudgetList.clear()
        mFeeList.clear()
        getFees()
    }

    override fun onItemClick(position: Int) {
        val model = mBudgetList[position]
        val year = model.getYear()
        val term = model.getTerm()
        val invoiceId = model.getInvoiceId()
        val amount = model.getAmount()

        AddReceiptDialog(
            requireContext(), invoiceId!!,
            studentId!!,
            classId!!,
            levelId!!,
            regNo!!,
            studentName!!,
            amount!!,
            year!!,
            term!!,
        ).apply {
            setCancelable(true)
            show()
        }.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun makePayment() {
        mPayBtn.setOnClickListener {

        }
    }

    private fun postReferenceNumber(
        sReference: String,
        sAmount: String,
        sDate: String,
        sName: String,
    ) {
        val url = Login.urlBase + "/manageReceipts.php"
        val stringMap = hashMapOf<String, String>()

        stringMap["invoice_id"] = mInvoiceId!!
        stringMap["student_id"] = studentId!!
        stringMap["class"] = classId!!
        stringMap["level"] = levelId!!
        stringMap["reg_no"] = regNo!!
        stringMap["name"] = studentName!!
        stringMap["amount"] = mAmount!!
        stringMap["date"] = getDate()
        stringMap["reference"] = sReference
        stringMap["year"] = mYear!!
        stringMap["term"] = mTerm!!

        requestToServer(Request.Method.POST, url, requireContext(), stringMap,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                }

                override fun onError(error: VolleyError) {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

}
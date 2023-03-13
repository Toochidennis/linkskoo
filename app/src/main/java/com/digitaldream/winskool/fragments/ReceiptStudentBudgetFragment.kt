package com.digitaldream.winskool.fragments

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
import com.digitaldream.winskool.activities.TermlyFeeSetupActivity
import com.digitaldream.winskool.adapters.OnItemClickListener
import com.digitaldream.winskool.adapters.ReceiptStudentBudgetAdapter
import com.digitaldream.winskool.dialog.AddReceiptDialog
import com.digitaldream.winskool.models.StudentPaymentModel
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

    private var levelId: String? = null
    private var levelName: String? = null
    private var studentName: String? = null
    private var classId: String? = null
    private var studentId: String? = null
    private var regNo: String? = null

    private val mBudgetList = mutableListOf<StudentPaymentModel>()
    private lateinit var mAdapter: ReceiptStudentBudgetAdapter

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

        toolbar.apply {
            title = "Select Term Fee"
            setNavigationIcon(R.drawable.arrow_left)
            setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        mAdapter = ReceiptStudentBudgetAdapter(requireContext(), mBudgetList, this)
        mRecyclerView.hasFixedSize()
        mRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        mRecyclerView.adapter = mAdapter

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

    private fun getBudget() {
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
                            mErrorMessage
                                .text = it
                        }
                    } else {
                        try {
                            val jsonObject = JSONObject(response)
                            if (jsonObject.has("invoice")) {
                                val invoiceArray = jsonObject.getJSONArray("invoice")
                                for (i in 0 until invoiceArray.length()) {
                                    val invoiceObjects = invoiceArray.getJSONObject(i)
                                    val invoiceId = invoiceObjects.getString("tid")
                                    val amount =
                                        invoiceObjects.getString("amount")
                                    val year = invoiceObjects.getString("year")
                                    val term = invoiceObjects.getString("term")
                                    val previousYear = year.toInt() - 1
                                    val session =
                                        String.format(
                                            Locale.getDefault(),
                                            "%d/%s",
                                            previousYear,
                                            year
                                        )

                                    val paymentModel = StudentPaymentModel()
                                    paymentModel.setInvoiceId(invoiceId)
                                    paymentModel.setAmount(amount)
                                    paymentModel.setSession(session)
                                    paymentModel.setTerm(term)
                                    paymentModel.setYear(year)
                                    mBudgetList.add(paymentModel)
                                }

                                if (mBudgetList.isEmpty()) {
                                    mErrorView.isVisible = false
                                    mMainView.isVisible = true
                                    mErrorMessage.isVisible = true
                                    mAddBudgetBtn.isVisible = true
                                } else {
                                    mErrorView.isVisible = false
                                    mMainView.isVisible = true
                                    mErrorMessage.isVisible = false
                                    mAddBudgetBtn.isVisible = false
                                }
                                mAdapter.notifyItemChanged(mBudgetList.size - 1)
                            } else {
                                mMainView.isVisible = true
                                mErrorMessage.isVisible = true
                                mAddBudgetBtn.isVisible = false
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
                }
            })
    }

    private fun refreshData() {
        mRefreshBtn.setOnClickListener {
            mBudgetList.clear()
            getBudget()
        }
    }

    override fun onResume() {
        super.onResume()
        mBudgetList.clear()
        getBudget()
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

}
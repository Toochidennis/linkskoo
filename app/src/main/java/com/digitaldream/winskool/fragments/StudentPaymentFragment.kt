package com.digitaldream.winskool.fragments

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.digitaldream.winskool.BuildConfig
import com.digitaldream.winskool.R
import com.digitaldream.winskool.activities.Login
import com.digitaldream.winskool.activities.PaymentActivity
import com.digitaldream.winskool.activities.PaystackPaymentActivity
import com.digitaldream.winskool.adapters.StudentPaymentAdapter
import com.digitaldream.winskool.dialog.OnInputListener
import com.digitaldream.winskool.dialog.PaymentEmailDialog
import com.digitaldream.winskool.models.StudentPaymentModel
import com.digitaldream.winskool.utils.UtilsFun
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class StudentPaymentFragment : Fragment(), OnInputListener,
    StudentPaymentAdapter.OnHistoryClickListener {

    private lateinit var mMainView: RelativeLayout
    private lateinit var mFirstTermAmount: TextView
    private lateinit var mFirstTermViewDetails: Button
    private lateinit var mFirstTermPayBtn: Button
    private lateinit var mSecondTermAmount: TextView
    private lateinit var mSecondTermViewDetails: Button
    private lateinit var mSecondTermPayBtn: Button
    private lateinit var mThirdTermAmount: TextView
    private lateinit var mThirdTermViewDetails: Button
    private lateinit var mThirdTermPayBtn: Button
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mHistoryMessage: TextView
    private lateinit var mErrorMessage: TextView
    private lateinit var mRefreshBtn: Button
    private lateinit var mFirstTermTitle: TextView
    private lateinit var mSecondTermTitle: TextView
    private lateinit var mThirdTermTitle: TextView

    private lateinit var mAdapter: StudentPaymentAdapter
    private var mStudentId: String? = null
    private var mDb: String? = null
    private var mFirstTerm: String? = null
    private var mSecondTerm: String? = null
    private var mThirdTerm: String? = null
    private var mInvoiceId: String? = null
    private var mFirstAmount: String? = null
    private var mSecondAmount: String? = null
    private var mThirdAmount: String? = null
    private var clickedValue: String? = null
    private var mSession: String? = null
    private var mYear: String? = null
    private var mTerm: String? = null
    private val mHistoryList = mutableListOf<StudentPaymentModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(
            R.layout.fragment_student_payment, container,
            false
        )

        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        mMainView = view.findViewById(R.id.main_layout)
        mFirstTermAmount = view.findViewById(R.id.first_term_amount)
        mFirstTermViewDetails = view.findViewById(R.id.first_term_view_details)
        mFirstTermPayBtn = view.findViewById(R.id.first_term_pay_btn)
        mSecondTermAmount = view.findViewById(R.id.second_term_amount)
        mSecondTermViewDetails = view.findViewById(R.id.second_term_view_details)
        mSecondTermPayBtn = view.findViewById(R.id.second_term_pay_btn)
        mThirdTermAmount = view.findViewById(R.id.third_term_amount)
        mThirdTermViewDetails = view.findViewById(R.id.third_term_view_details)
        mThirdTermPayBtn = view.findViewById(R.id.third_term_pay_btn)
        mRecyclerView = view.findViewById(R.id.history_recycler)
        mHistoryMessage = view.findViewById(R.id.history_error_message)
        mErrorMessage = view.findViewById(R.id.error_message)
        mRefreshBtn = view.findViewById(R.id.refresh_btn)
        mFirstTermTitle = view.findViewById(R.id.first_term_fee_title)
        mSecondTermTitle = view.findViewById(R.id.second_term_fee_title)
        mThirdTermTitle = view.findViewById(R.id.third_term_fee_title)

        val sharedPreferences = requireContext().getSharedPreferences(
            "loginDetail", Context
                .MODE_PRIVATE
        )
        mDb = sharedPreferences.getString("db", "")
        mStudentId = sharedPreferences.getString("user_id", "")
        val studentEmail = sharedPreferences.getString("student_email", "")

        toolbar.apply {
            title = "Payment"
            setNavigationIcon(R.drawable.arrow_left)
            setNavigationOnClickListener { requireActivity().onBackPressed() }
        }

        mAdapter = StudentPaymentAdapter(mHistoryList, this)
        mRecyclerView.hasFixedSize()
        mRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        mRecyclerView.adapter = mAdapter

        makePayment(studentEmail!!)

        viewDetails()

        refreshData()

        return view
    }

    private fun refreshData() {
        mRefreshBtn.setOnClickListener {
            paymentHistory()
        }
    }

    private fun makePayment(studentEmail: String) {
        mFirstTermPayBtn.setOnClickListener {
            clickedValue = "1"
            if (studentEmail.isNotBlank()) {
                requestURL(studentEmail, mFirstAmount!!.toLong())
            } else {
                val emailDialog = PaymentEmailDialog(requireContext(), this)
                    .apply {
                        setCancelable(true)
                        show()
                    }
                val window = emailDialog.window
                window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
        }

        mSecondTermPayBtn.setOnClickListener {
            clickedValue = "2"
            if (studentEmail.isNotBlank()) {
                requestURL(studentEmail, mSecondAmount!!.toLong())
            } else {
                val emailDialog = PaymentEmailDialog(requireContext(), this)
                    .apply {
                        setCancelable(true)
                        show()
                    }
                val window = emailDialog.window
                window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
        }

        mThirdTermPayBtn.setOnClickListener {
            clickedValue = "3"
            if (studentEmail.isNotBlank()) {
                requestURL(studentEmail, mThirdAmount!!.toLong())
            } else {
                val emailDialog = PaymentEmailDialog(requireContext(), this)
                    .apply {
                        setCancelable(true)
                        show()
                    }
                val window = emailDialog.window
                window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestURL(sStudentEmail: String, sAmount: Long) {
        val builder = StringBuilder()
        val json = JSONObject()
            .put("email", sStudentEmail)
            .put("amount", "${sAmount * 100}")
        val url = "https://api.paystack.co/transaction/initialize"

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val mEntity = StringEntity(json.toString())
                val httpClient: HttpClient = HttpClientBuilder.create().build()
                val post = HttpPost(url)
                post.apply {
                    entity = mEntity
                    addHeader("Content-type", "application/json")
                    addHeader(
                        "Authorization", BuildConfig.PSTK_SECRET_KEY
                    )
                }

                val response = httpClient.execute(post)

                val reader = BufferedReader(InputStreamReader(response.entity.content))

                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    builder.append(line)
                }

                val jsonObject = JSONObject(builder.toString())
                val status = jsonObject.getString("status")
                val objects = jsonObject.getJSONObject("data")
                val authorizationURL = objects.getString("authorization_url")
                val reference = objects.getString("reference")
                println("url: $authorizationURL  $status  $reference")

                when (status) {
                    "true" -> {
                        val intent = Intent(activity, PaystackPaymentActivity::class.java)
                        intent.putExtra("url", authorizationURL)
                        intent.putExtra("reference", reference)
                        intent.putExtra("transaction_id", mInvoiceId)
                        intent.putExtra("session", mSession)
                        intent.putExtra("term", mTerm)
                        intent.putExtra("year", mYear)

                        when (clickedValue) {
                            "1" -> intent.putExtra("amount", mFirstAmount)
                            "2" -> intent.putExtra("amount", mSecondAmount)
                            else -> intent.putExtra("amount", mThirdAmount)
                        }
                        startActivity(intent)
                    }
                    else -> throw Exception("Can't generate url")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    private fun paymentHistory() {
        val progressFlower = ACProgressFlower.Builder(context)
            .direction(ACProgressConstant.DIRECT_CLOCKWISE)
            .textMarginTop(10)
            .fadeColor(ContextCompat.getColor((context as AppCompatActivity), R.color.color_5))
            .build()
        progressFlower.setCancelable(false)
        progressFlower.setCanceledOnTouchOutside(false)
        progressFlower.show()

        val url = Login.urlBase + "/manageReceipts.php?list=$mStudentId"
        val stringRequest: StringRequest = object : StringRequest(
            Method.GET, url,
            { response: String ->
                Log.d("TAG", response)
                progressFlower.dismiss()
                try {
                    val jsonObject = JSONObject(response)
                    val receiptsArray = jsonObject.getJSONArray("receipts")
                    val invoiceArray = jsonObject.getJSONArray("invoice")

                    for (i in 0 until invoiceArray.length()) {
                        val invoiceObjects = invoiceArray.getJSONObject(i)
                        mInvoiceId = invoiceObjects.getString("tid")
                        val amount = invoiceObjects.getString("amount")
                        mYear = invoiceObjects.getString("year")
                        mTerm = invoiceObjects.getString("term")

                        val previousYear = mYear!!.toInt() - 1
                        mSession =
                            String.format(Locale.getDefault(), "%d/%s", previousYear, mYear)

                        when (mTerm) {
                            "1" -> {
                                mFirstAmount = amount.replace(".00", "")
                                mFirstTerm = mTerm

                                mFirstTermTitle.text = String.format(
                                    Locale.getDefault(), "%s " +
                                            "%s", mSession, "First Term Fees"
                                )
                            }

                            "2" -> {
                                mSecondAmount = amount.replace(".00", "")
                                mSecondTerm = mTerm

                                mSecondTermTitle.text = String.format(
                                    Locale.getDefault(), "%s " +
                                            "%s", mSession, "Second Term Fees"
                                )
                            }
                            else -> {
                                mThirdAmount = amount.replace(".00", "")
                                mThirdTerm = mTerm

                                mThirdTermTitle.text = String.format(
                                    Locale.getDefault(), "%s " +
                                            "%s", mSession, "Third Term Fees"
                                )
                            }
                        }

                    }

                    if (mFirstAmount == "" || mFirstAmount == null) {
                        mFirstTermAmount.text = getString(R.string.paid)
                        mFirstTermPayBtn.isVisible = false
                        mFirstTermViewDetails.isVisible = false
                    } else {
                        String.format(
                            Locale.getDefault(), "%s%s",
                            getString(R.string.naira),
                            UtilsFun.currencyFormat(
                                mFirstAmount!!.toDouble()
                            )
                        ).also { mFirstTermAmount.text = it }

                        mFirstTermPayBtn.isVisible = true
                        mFirstTermViewDetails.isVisible = true
                    }

                    if (mSecondAmount == null || mSecondAmount == "") {
                        mSecondTermAmount.text = getString(R.string.paid)
                        mSecondTermPayBtn.isVisible = false
                        mSecondTermViewDetails.isVisible = false
                    } else {
                        String.format(
                            Locale.getDefault(), "%s%s",
                            getString(R.string.naira),
                            UtilsFun.currencyFormat(
                                mSecondAmount!!.toDouble()
                            )
                        ).also { mSecondTermAmount.text = it }

                        mSecondTermPayBtn.isVisible = true
                        mSecondTermViewDetails.isVisible = true
                    }

                    if (mThirdAmount == "" || mThirdAmount == null) {
                        mThirdTermAmount.text = getString(R.string.paid)
                        mThirdTermPayBtn.isVisible = false
                        mThirdTermViewDetails.isVisible = false
                    } else {
                        String.format(
                            Locale.getDefault(), "%s%s",
                            getString(R.string.naira),
                            UtilsFun.currencyFormat(
                                mThirdAmount!!.toDouble()
                            )
                        ).also { mThirdTermAmount.text = it }

                        mThirdTermPayBtn.isVisible = true
                        mThirdTermViewDetails.isVisible = true
                    }


                    for (i in 0 until receiptsArray.length()) {
                        val receiptsObjects = receiptsArray.getJSONObject(i)
                        val name = receiptsObjects.getString("name")
                        val reference = receiptsObjects.getString("reference")
                        val amount = receiptsObjects.getString("amount")
                        val date = receiptsObjects.getString("date")
                        val year = receiptsObjects.getString("year")
                        val term = when (receiptsObjects.getString("term")) {
                            "1" -> "First Term Fees"
                            "2" -> "Second Term Fees"
                            else -> "Third Term Fees"
                        }
                        requireContext().getSharedPreferences(
                            "loginDetail",
                            Context.MODE_PRIVATE
                        ).edit()
                            .putString("level_name", receiptsObjects.getString("level_name"))
                            .apply()
                        val previousYear = year.toInt() - 1
                        val session =
                            String.format(Locale.getDefault(), "%d/%s", previousYear, year)
                        val formattedAmount = UtilsFun.currencyFormat(amount.toDouble())

                        val paymentModel = StudentPaymentModel()
                        paymentModel.setName(name)
                        paymentModel.setAmount(
                            String.format(
                                Locale.getDefault(), "%s%s",
                                getString(R.string.naira), formattedAmount
                            )
                        )
                        paymentModel.setSession(session)
                        paymentModel.setTerm(term)
                        paymentModel.setDate(date)
                        paymentModel.setReferenceNumber(reference)
                        paymentModel.setStatus("Success")

                        mHistoryList.add(paymentModel)
                        mHistoryList.sortByDescending { it.getDate() }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (mHistoryList.isEmpty()) {
                    mHistoryMessage.isVisible = true
                    mHistoryMessage.text = getString(R.string.no_history)
                    mMainView.isVisible = true
                    mErrorMessage.isVisible = false
                    mRefreshBtn.isVisible = false
                } else {
                    mHistoryMessage.isVisible = false
                    mMainView.isVisible = true
                    mErrorMessage.isVisible = false
                    mRefreshBtn.isVisible = false
                }

                mAdapter.notifyDataSetChanged()

            }, { error: VolleyError ->
                error.printStackTrace()
                progressFlower.dismiss()
                mMainView.isVisible = false
                mErrorMessage.isVisible = true
                mErrorMessage.text = getString(R.string.can_not_retrieve)
                mRefreshBtn.isVisible = true
            }) {
            override fun getParams(): Map<String, String> {
                val stringMap: MutableMap<String, String> = HashMap()
                stringMap["_db"] = mDb!!
                return stringMap
            }
        }
        val requestQueue: RequestQueue = Volley.newRequestQueue(requireContext())
        requestQueue.add(stringRequest)
    }

    private fun viewDetails() {
        mFirstTermViewDetails.setOnClickListener {
            startActivity(
                Intent(activity, PaymentActivity::class.java)
                    .putExtra("from", "fee_details")
                    .putExtra("term", mFirstTerm)
            )
        }

        mSecondTermViewDetails.setOnClickListener {
            startActivity(
                Intent(activity, PaymentActivity::class.java)
                    .putExtra("from", "fee_details")
                    .putExtra("term", mSecondTerm)
            )
        }

        mThirdTermViewDetails.setOnClickListener {
            startActivity(
                Intent(activity, PaymentActivity::class.java)
                    .putExtra("from", "fee_details")
                    .putExtra("term", mThirdTerm)
            )
        }
    }

    override fun onHistoryClick(position: Int) {
        val paymentModel = mHistoryList[position]
        val amount = paymentModel.getAmount()
        val reference = paymentModel.getReferenceNumber()
        val status = paymentModel.getStatus()
        val session = paymentModel.getSession()
        val term = paymentModel.getTerm()
        val date = paymentModel.getDate()

        startActivity(
            Intent(requireContext(), PaymentActivity::class.java)
                .putExtra("amount", amount)
                .putExtra("reference", reference)
                .putExtra("status", status)
                .putExtra("session", session)
                .putExtra("term", term)
                .putExtra("date", date)
                .putExtra("from", "student_receipt")
        )
    }

    override fun sendInput(input: String) {

        requireContext().getSharedPreferences(
            "loginDetail",
            Context.MODE_PRIVATE
        ).edit()
            .putString("student_email", input)
            .apply()

        when (clickedValue) {
            "1" -> requestURL(input, mFirstAmount!!.toLong())
            "2" -> requestURL(input, mSecondAmount!!.toLong())
            else -> requestURL(input, mThirdAmount!!.toLong())
        }
    }

    override fun onResume() {
        super.onResume()
        mHistoryList.clear()
        paymentHistory()
    }
}
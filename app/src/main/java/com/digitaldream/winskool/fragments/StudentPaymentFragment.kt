package com.digitaldream.winskool.fragments

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
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
import com.digitaldream.winskool.adapters.StudentPaymentSliderAdapter
import com.digitaldream.winskool.dialog.OnInputListener
import com.digitaldream.winskool.dialog.PaymentEmailDialog
import com.digitaldream.winskool.models.StudentPaymentModel
import com.digitaldream.winskool.utils.UtilsFun
import com.google.android.material.tabs.TabLayout
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
    StudentPaymentAdapter.OnHistoryClickListener, StudentPaymentSliderAdapter
    .OnCardClickListener {

    private lateinit var mMainView: LinearLayout
    private lateinit var mPaidSate: LinearLayout
    private lateinit var mTermView: RelativeLayout
    private lateinit var mHistoryRecyclerView: RecyclerView
    private lateinit var mHistoryMessage: TextView
    private lateinit var mErrorMessage: TextView
    private lateinit var mRefreshBtn: Button
    private lateinit var mViewPager: ViewPager
    private lateinit var mTabLayout: TabLayout

    private val mHistoryList = mutableListOf<StudentPaymentModel>()
    private val mCardList = mutableListOf<StudentPaymentModel>()
    private lateinit var mHistoryAdapter: StudentPaymentAdapter
    private lateinit var mCardAdapter: StudentPaymentSliderAdapter
    private var mStudentId: String? = null
    private var mDb: String? = null
    private var mInvoiceId: String? = null
    private var mAmount: String? = null
    private var mStudentEmail: String? = null
    private var mSession: String? = null
    private var mYear: String? = null
    private var mTerm: String? = null


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
        mPaidSate = view.findViewById(R.id.paid_state)
        mTermView = view.findViewById(R.id.slider_view)
        mHistoryRecyclerView = view.findViewById(R.id.history_recycler)
        mHistoryMessage = view.findViewById(R.id.history_error_message)
        mErrorMessage = view.findViewById(R.id.error_message)
        mRefreshBtn = view.findViewById(R.id.refresh_btn)
        mViewPager = view.findViewById(R.id.card_pager)
        mTabLayout = view.findViewById(R.id.card_tab)

        val sharedPreferences = requireContext().getSharedPreferences(
            "loginDetail", Context
                .MODE_PRIVATE
        )
        mDb = sharedPreferences.getString("db", "")
        mStudentId = sharedPreferences.getString("user_id", "")
        mStudentEmail = sharedPreferences.getString("student_email", "")

        toolbar.apply {
            title = "Payment"
            setNavigationIcon(R.drawable.arrow_left)
            setNavigationOnClickListener { requireActivity().onBackPressed() }
        }

        mHistoryAdapter = StudentPaymentAdapter(mHistoryList, this)
        mHistoryRecyclerView.hasFixedSize()
        mHistoryRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        mHistoryRecyclerView.adapter = mHistoryAdapter

        mCardAdapter = StudentPaymentSliderAdapter(requireContext(), mCardList, this)
        mViewPager.adapter = mCardAdapter

        Timer().apply {
            scheduleAtFixedRate(CardTimer(), 1000, 3000)
        }

        mTabLayout.setupWithViewPager(mViewPager, true)

        refreshData()

        return view
    }

    private fun refreshData() {
        mRefreshBtn.setOnClickListener {
            paymentHistory()
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
                        intent.putExtra("amount", mAmount)
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

                if (response == "[]") {
                    mErrorMessage.isVisible = true
                    "Fees not set yet. Check back later!".also { mErrorMessage.text = it }
                } else {
                    try {
                        val jsonObject = JSONObject(response)
                        if (jsonObject.has("invoice")) {
                            val invoiceArray = jsonObject.getJSONArray("invoice")
                            for (i in 0 until invoiceArray.length()) {
                                val invoiceObjects = invoiceArray.getJSONObject(i)
                                val invoiceId = invoiceObjects.getString("tid")
                                val amount = invoiceObjects.getString("amount").replace(".00", "")
                                val year = invoiceObjects.getString("year")
                                val term = invoiceObjects.getString("term")
                                val previousYear = year.toInt() - 1
                                val session =
                                    String.format(Locale.getDefault(), "%d/%s", previousYear, year)

                                val paymentModel = StudentPaymentModel()
                                paymentModel.setInvoiceId(invoiceId)
                                paymentModel.setAmount(amount)
                                paymentModel.setAmountT(amount)
                                paymentModel.setSession(session)
                                paymentModel.setTerm(term)
                                mCardList.add(paymentModel)
                            }

                        }
                        if (mCardList.isEmpty()) {
                            mTermView.isVisible = false
                            mPaidSate.isVisible = true
                        } else {
                            mTermView.isVisible = true
                            mPaidSate.isVisible = false
                        }
                        mCardAdapter.notifyDataSetChanged()

                        if (jsonObject.has("receipts")) {
                            val receiptsArray = jsonObject.getJSONArray("receipts")
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
                                    .putString(
                                        "level_name",
                                        receiptsObjects.getString("level_name")
                                    )
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

                    mHistoryAdapter.notifyDataSetChanged()
                }

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

        requestURL(input, mAmount!!.toLong())
    }

    override fun onResume() {
        super.onResume()
        mHistoryList.clear()
        mCardList.clear()
        mCardAdapter.notifyDataSetChanged()
        paymentHistory()
    }

    override fun viewDetails(position: Int) {
        val paymentModel = mCardList[position]
        mTerm = paymentModel.getTerm()

        startActivity(
            Intent(activity, PaymentActivity::class.java)
                .putExtra("from", "fee_details")
                .putExtra("term", mTerm)
        )

    }

    override fun makePayment(position: Int) {
        val paymentModel = mCardList[position]
        mSession = paymentModel.getSession()
        mTerm = paymentModel.getTerm()
        mAmount = paymentModel.getAmountT()
        mInvoiceId = paymentModel.getInvoiceId()

        if (mStudentEmail!!.isNotBlank()) {
            requestURL(mStudentEmail!!, mAmount!!.toLong())
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

    inner class CardTimer : TimerTask() {
        override fun run() {
            try {
                requireActivity().runOnUiThread {
                    if (mViewPager.currentItem < mCardList.size - 1) {
                        mViewPager.currentItem = mViewPager.currentItem + 1
                    } else {
                        mViewPager.currentItem = 0
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

    }
}
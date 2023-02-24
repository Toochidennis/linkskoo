package com.digitaldream.winskool.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.digitaldream.winskool.R
import com.digitaldream.winskool.utils.UtilsFun
import java.util.*

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class PaystackPaymentActivity : AppCompatActivity(R.layout.activity_payment_paystack) {

    private var mStudentId: String? = null
    private var mClassId: String? = null
    private var mLevelId: String? = null
    private var mRegistrationNumber: String? = null
    private var mStudentName: String? = null
    private var mSchoolName: String? = null
    private var mClassName: String? = null
    private var mLevelName: String? = null
    private var mYear: String? = null
    private var mTerm: String? = null
    private var mDb: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission
                    .WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            ),
            PackageManager.PERMISSION_GRANTED
        )

        val intent = intent
        val reference = intent.getStringExtra("reference")
        val authorizationURL = intent.getStringExtra("url")
        val transactionId = intent.getStringExtra("transaction_id")

        val toolbar: Toolbar = findViewById(R.id.toolbar)

        toolbar.apply {
            title = "Pay"
            setNavigationIcon(R.drawable.arrow_left)
            setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }

        val sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE)
        mStudentId = sharedPreferences.getString("user_id", "")
        mClassId = sharedPreferences.getString("classId", "")
        mLevelId = sharedPreferences.getString("level", "")
        mRegistrationNumber = sharedPreferences.getString("student_reg_no", "")
        mStudentName = sharedPreferences.getString("user", "")
        mSchoolName = sharedPreferences.getString("school_name", "")
        mClassName = sharedPreferences.getString("student_class", "")
        mLevelName = sharedPreferences.getString("level_name", "")
        mYear = sharedPreferences.getString("year", "")
        mTerm = sharedPreferences.getString("term", "")
        mDb = sharedPreferences.getString("db", "")

        loadCheckOut(authorizationURL!!, reference!!, transactionId!!)

        writeReceipt()

    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadCheckOut(authorizationURL: String, sReference: String, sTransactionId: String) {
        val webView: WebView = findViewById(R.id.web_view)
        val card: NestedScrollView = findViewById(R.id.receipt_view)

        webView.settings.apply {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            domStorageEnabled = true
        }

        webView.webViewClient = object : WebViewClient() {
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                url: String?
            ): Boolean {
                postReferenceNumber(sReference, sTransactionId)
                webView.isVisible = false
                card.isVisible = true
                generateReceipt(sReference)
                return false
            }
        }

        webView.loadUrl(authorizationURL)
    }

    private fun postReferenceNumber(sReference: String, sTransactionId: String) {
        val url = Login.urlBase + "/manageReceipts.php"
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, url,
            { response: String ->
                Log.d("Paystack", response)

            }, { error: VolleyError ->
                error.printStackTrace()
            }) {
            override fun getParams(): Map<String, String> {
                val stringMap: MutableMap<String, String> = HashMap()
                stringMap["tid"] = sTransactionId
                stringMap["student_id"] = mStudentId!!
                stringMap["class"] = mClassId!!
                stringMap["level"] = mLevelId!!
                stringMap["reg_no"] = mRegistrationNumber!!
                stringMap["name"] = mStudentName!!
                stringMap["amount"] = "10000"
                stringMap["date"] = getDate()
                stringMap["reference"] = sReference
                stringMap["year"] = mYear!!
                stringMap["term"] = mTerm!!
                stringMap["_db"] = mDb!!
                return stringMap
            }
        }
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    private fun generateReceipt(sReference: String) {
        val schoolName: TextView = findViewById(R.id.school_name)
        val amount: TextView = findViewById(R.id.paid_amount)
        val status: TextView = findViewById(R.id.status)
        val date: TextView = findViewById(R.id.date)
        val name: TextView = findViewById(R.id.student_name)
        val level: TextView = findViewById(R.id.student_level)
        val studentClass: TextView = findViewById(R.id.student_class)
        val studentRegNo: TextView = findViewById(R.id.registration_no)
        val session: TextView = findViewById(R.id.session)
        val term: TextView = findViewById(R.id.term)
        val referenceNumber: TextView = findViewById(R.id.reference_number)

        schoolName.text = mSchoolName
        amount.text = getString(R.string.zero_balance)
        status.text = getString(R.string.success)
        date.text = getDate()
        name.text = UtilsFun.capitaliseFirstLetter(mStudentName!!)
        level.text = mLevelName
        studentClass.text = mClassName
        studentRegNo.text = mRegistrationNumber
        session.text = ""
        term.text = mTerm
        referenceNumber.text = sReference
    }

    private fun writeReceipt() {
        val receiptCard: CardView = findViewById(R.id.receipt_card)
        val shareReceipt: Button = findViewById(R.id.share_receipt)
        val downloadReceipt: Button = findViewById(R.id.download_receipt)

        downloadReceipt.setOnClickListener {
            UtilsFun.downloadPDF(receiptCard, this)
        }

        shareReceipt.setOnClickListener {
            UtilsFun.sharePDF(receiptCard, this)
        }

    }

    fun getDate(): String {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR].toString()
        val month = (calendar[Calendar.MONTH] + 1).toString()
        val dayOfMonth = calendar[Calendar.DAY_OF_MONTH].toString()
        return "$year-$month-$dayOfMonth"
    }
}
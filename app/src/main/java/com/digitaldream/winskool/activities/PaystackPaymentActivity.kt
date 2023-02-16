package com.digitaldream.winskool.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import co.paystack.android.Paystack.TransactionCallback
import co.paystack.android.PaystackSdk
import co.paystack.android.Transaction
import co.paystack.android.model.Card
import co.paystack.android.model.Charge
import com.digitaldream.winskool.BuildConfig
import com.digitaldream.winskool.R


class PaystackPaymentActivity : AppCompatActivity(R.layout.activity_payment_paystack) {

    private lateinit var mCardNumber: EditText
    private lateinit var mCardExpiry: EditText
    private lateinit var mCardCVV: EditText
    private lateinit var mPayBtn: Button

    private var mCardNumberText: String? = null
    private var mCardExpiryText: String? = null
    private var mCardCVVText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        mCardNumber = findViewById(R.id.card_number)
        mCardExpiry = findViewById(R.id.card_expiry)
        mCardCVV = findViewById(R.id.card_cvv)
        mPayBtn = findViewById(R.id.btn_make_payment)

        toolbar.apply {
            title = "Pay"
            setNavigationIcon(R.drawable.arrow_left)
            setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }

        initializePayStack()
        validateFormVariables()


        mPayBtn.setOnClickListener {
            if (
                mCardNumber.text.length == 19 && mCardExpiry.text.length == 5 &&
                mCardCVV.text.length == 3
            ) {
                performCharge()
            } else {
                Toast.makeText(this, "Complete", Toast.LENGTH_LONG).show()
            }

        }

    }

    private fun initializePayStack() {
        PaystackSdk.initialize(this)
        PaystackSdk.setPublicKey(BuildConfig.PSTK_PUBLIC_KEY)
    }

    private fun validateFormVariables() {
        mCardNumber.addTextChangedListener(object : TextWatcher {
            var lock = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (lock || s!!.length > 16) {
                    return
                }
                lock = true

                var i = 4
                while (i < s.length) {
                    if (s.toString()[i] != ' ') {
                        s.insert(i, " ")
                    }
                    i += 5
                }
                lock = false
            }
        })

        mCardExpiry.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (mCardExpiry.text.length == 2 &&
                    !mCardExpiry.text.contains("/")
                ) {
                    mCardExpiry.append("/")
                }
            }
        })

    }

    private fun performCharge() {

        mCardNumberText = mCardNumber.text.toString()
        mCardExpiryText = mCardExpiry.text.toString()
        mCardCVVText = mCardCVV.text.toString()

        val cardExpiryArray = mCardExpiryText!!.split("/").toTypedArray()
        val cardNumber = mCardNumberText!!.replace(" ", "")
        val expiryMonth = cardExpiryArray[0].toInt()
        val expiryYear = cardExpiryArray[1].toInt()
        val amountToPay = intent.getIntExtra("amount", 0)
        //amountToPay *= 100 // convert to kobo

        val mCard = Card(cardNumber, expiryMonth, expiryYear, mCardCVVText)
        if (mCard.isValid) {
            val charge = Charge().apply {
                amount = amountToPay
                email = "dennistoochi@gmail.com"
                card = mCard
            }

            PaystackSdk.chargeCard(
                this@PaystackPaymentActivity,
                charge,
                object : TransactionCallback {
                    override fun onSuccess(transaction: Transaction?) {
                        parseResponse(transaction!!.reference)
                    }

                    override fun beforeValidate(transaction: Transaction?) {
                        Log.d("PaystackActivity", "beforeValidate: " + transaction!!.reference)
                    }

                    override fun onError(error: Throwable?, transaction: Transaction?) {
                        Log.d("PaystackActivity", "onError: " + error!!.localizedMessage)
                        Log.d("PaystackActivity", "onError: $error")
                    }
                })
        } else {
            Toast.makeText(this, "Invalid card", Toast.LENGTH_LONG).show()
        }

    }

    private fun parseResponse(transactionReference: String) {
        val message = "Payment Successful - $transactionReference"
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

}
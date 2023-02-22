package com.digitaldream.winskool.activities

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.digitaldream.winskool.R
import com.digitaldream.winskool.fragments.*

open class PaymentActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_payment)

        val intent = intent
        val amount = intent.getStringExtra("amount")
        val reference = intent.getStringExtra("reference")
        val status = intent.getStringExtra("status")
        val session = intent.getStringExtra("session")
        val term = intent.getStringExtra("term")
        val date = intent.getStringExtra("date")

        when (intent.getStringExtra("from")) {

            "dashboard" -> supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container, AdminPaymentFragment()
            ).commit()

            "transactions" -> supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container, AdminTransactions()
            ).commit()

            "expenditure" -> supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container,
                ExpenditureHistoryFragment()
            ).commit()

            "receipt" -> supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container,
                ReceiptHistoryFragment()
            ).commit()

            "student_receipt" -> supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container, StudentTransactionReceiptFragment
                    .newInstance(
                        amount!!,
                        reference!!,
                        status!!,
                        session!!,
                        term!!,
                        date!!
                    )
            ).commit()

            "add_expenditure" -> supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container, VendorFragment()
            ).commit()

            "vendor" -> supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container, AddExpenditureFragment()
            ).commit()

            "fee_details" -> supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container, SchoolFeesDetailsFragment()
            ).commit()

            "settings" -> supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container, PaymentSettingsFragment()
            ).commit()

            "fee_settings" -> supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container, FeeTypeSetupFragment()
            ).commit()

            "account_settings" -> supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container, AccountSetupFragment()
            ).commit()
        }

    }

}

/*
val arrays = intArrayOf(8, 4, 2, 16)

var gd: Int
var lcm = 1

for (i in 1 until arrays.size) {
    gd = gcd(arrays[i], lcm)
    lcm = lcm * arrays[i] / gd
}

println(lcm)


open fun gcd(a: Int, b: Int): Int {
    return if (b == 0) a else gcd(b, a % b)
}*/

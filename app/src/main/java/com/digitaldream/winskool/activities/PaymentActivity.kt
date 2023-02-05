package com.digitaldream.winskool.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.digitaldream.winskool.R
import com.digitaldream.winskool.fragments.*

open class PaymentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_payment)

        val intent = intent

        when (intent.getStringExtra("from")) {

            "dashboard" -> supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container, AdminPaymentFragment()).commit()

            "transactions" -> supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container, AdminTransactions()).commit()

            "expenditure" -> supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container,
                ExpenditureHistoryFragment()).commit()

            "receipt" -> supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container,
                ReceiptHistoryFragment()).commit()

            "details" -> supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container, AdminTransactionDetails()).commit()

            "add_expenditure" -> supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container, VendorFragment()).commit()

            "vendor" -> supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container, AddExpenditureFragment()).commit()

            "fee_details" -> supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container, SchoolFeesDetailsFragment()).commit()

            "settings" -> supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container, PaymentSettingsFragment()).commit()

            "fee_settings" -> supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container, FeeTypeSetupFragment()).commit()
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

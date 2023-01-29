package com.digitaldream.winskool.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.digitaldream.winskool.R
import com.digitaldream.winskool.fragments.*

class PaymentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_payment)

        val intent = intent

        when (intent.getStringExtra("from")) {

            "transactions" -> supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container, AdminTransactions()).commit()

            "expenditure" -> supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container, ExpenditureFragment()).commit()

            "receipt" -> supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container, ReceiptsFragment()).commit()

            "details" -> supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container, AdminTransactionDetails()).commit()

            "add_expenditure" -> supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container, VendorFragment()).commit()
        }

    }
}

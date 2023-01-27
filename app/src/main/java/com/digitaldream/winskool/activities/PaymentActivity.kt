package com.digitaldream.winskool.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.digitaldream.winskool.R
import com.digitaldream.winskool.fragments.AdminTransactionDetails
import com.digitaldream.winskool.fragments.AdminTransactions
import com.digitaldream.winskool.fragments.ExpenditureFragment
import com.digitaldream.winskool.fragments.ReceiptsFragment

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
        }

    }
}

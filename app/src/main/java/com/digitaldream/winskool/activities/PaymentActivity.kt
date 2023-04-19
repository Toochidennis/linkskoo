package com.digitaldream.winskool.activities

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.digitaldream.winskool.R
import com.digitaldream.winskool.fragments.*


open class PaymentActivity : AppCompatActivity(R.layout.activity_payment) {


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val amount = intent.getStringExtra("amount")
        val name = intent.getStringExtra("name")
        val levelName = intent.getStringExtra("level_name")
        val studentName = intent.getStringExtra("student_name")
        val customerName = intent.getStringExtra("vendor_name")
        val customerPhone = intent.getStringExtra("vendor_phone")
        val customerReference = intent.getStringExtra("vendorId")
        val description = intent.getStringExtra("description")
        val id = intent.getStringExtra("id")
        val classId = intent.getStringExtra("classId")
        val levelId = intent.getStringExtra("levelId")
        val studentId = intent.getStringExtra("studentId")
        val className = intent.getStringExtra("class_name")
        val regNo = intent.getStringExtra("reg_no")
        val reference = intent.getStringExtra("reference")
        val status = intent.getStringExtra("status")
        val session = intent.getStringExtra("session")
        val term = intent.getStringExtra("term")
        val date = intent.getStringExtra("date")

        try {

            when (intent.getStringExtra("from")) {

                "dashboard" -> supportFragmentManager.beginTransaction().replace(
                    R.id.payment_container, AdminPaymentDashboardFragment()
                ).commit()

                "expenditure" -> supportFragmentManager.beginTransaction().replace(
                    R.id.payment_container,
                    ExpenditureHistoryFragment()
                ).commit()

                "receipt" -> supportFragmentManager.beginTransaction().replace(
                    R.id.payment_container,
                    ReceiptsHistoryFragment()
                ).commit()

                "student_receipt" -> supportFragmentManager.beginTransaction().replace(
                    R.id.payment_container, StudentTransactionReceiptFragment
                        .newInstance(
                            amount!!,
                            reference!!,
                            status!!,
                            session!!,
                            term!!,
                            date!!
                        )
                ).commit()

                "admin_receipt" -> supportFragmentManager.beginTransaction().replace(
                    R.id.payment_container, ReceiptsDetailsFragment
                        .newInstance(
                            amount!!,
                            name!!,
                            levelName!!,
                            className!!,
                            regNo!!,
                            reference!!,
                            session!!,
                            term!!,
                            date!!
                        )
                ).commit()

                "admin_expenditure" -> supportFragmentManager.beginTransaction().replace(
                    R.id.payment_container, ReceiptsDetailsFragment
                        .newInstance(
                            amount!!,
                            customerName!!,
                            null,
                            description!!,
                            customerPhone!!,
                            reference!!,
                            session!!,
                            term!!,
                            date!!
                        )
                ).commit()

                "add_expenditure" -> supportFragmentManager.beginTransaction().replace(
                    R.id.payment_container, VendorFragment()
                ).commit()

                "vendor" -> supportFragmentManager.beginTransaction().replace(
                    R.id.payment_container, AddExpenditureFragment.newInstance(
                        customerName!!,
                        customerPhone!!,
                        customerReference!!,
                        id!!,
                    )
                ).commit()

                "fee_details" -> supportFragmentManager.beginTransaction().replace(
                    /* containerViewId = */
                    R.id.payment_container, /* fragment = */
                    StudentFeesDetailsFragment.newInstance(term!!),
                ).commit()

                "settings" -> supportFragmentManager.beginTransaction().replace(
                    R.id.payment_container, PaymentSettingsFragment()
                ).commit()

                "fee_settings" -> supportFragmentManager.beginTransaction().replace(
                    R.id.payment_container, FeeTypeSetupFragment()
                ).commit()

                "account_settings" -> supportFragmentManager.beginTransaction().replace(
                    R.id.payment_container, AccountSetupFragment()
                ).commit()


                "see_all" -> supportFragmentManager.beginTransaction().replace(
                    R.id.payment_container, AdminTransactionHistoryFragment()
                ).commit()


                "receipt_class_name" -> supportFragmentManager.commit {
                    replace(
                        R.id.payment_container, ReceiptStudentNameFragment.newInstance(
                            classId!!, className!!, levelName!!,
                        )
                    )
                }

                "st" -> supportFragmentManager.commit {
                    replace(
                        R.id.payment_container, AdminStudentResultFragment.newInstance(
                            studentId!!,
                            classId!!
                        )
                    )
                }

                "student_profile" -> supportFragmentManager.commit {
                    replace(
                        R.id.payment_container,
                        StudentResultDownloadFragment.newInstance(
                            studentName!!,
                            studentId!!,
                            levelId!!,
                            classId!!,
                            regNo!!
                        )
                    )
                }

            }

        } catch (e: Exception) {
            e.printStackTrace()
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

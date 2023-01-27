package com.digitaldream.winskool.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.digitaldream.winskool.R
import com.digitaldream.winskool.activities.PaymentActivity
import java.util.*


class AdminPaymentFragment : Fragment() {

    private var clicked = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?, ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_admin_payment, container,
            false)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        setHasOptionsMenu(true)

        toolbar.apply {
            setNavigationIcon(R.drawable.arrow_left)
            title = "Payment"
            setNavigationOnClickListener { requireActivity().onBackPressed() }
        }


        val expenditureBtn = view.findViewById<CardView>(R.id.expenditure_btn)
        val receiptBtn = view.findViewById<CardView>(R.id.receipt_btn)
        val detailsBtn = view.findViewById<CardView>(R.id.details)
        val expectedRevenue = view.findViewById<TextView>(R.id.expected_revenue)
        val btnHideShow = view.findViewById<ImageButton>(R.id.visibility)

        btnHideShow.setOnClickListener {
            if (clicked) {
                btnHideShow.setImageResource(R.drawable.ic_eye_view)
                expectedRevenue.text = String.format(Locale.getDefault(), "%s%s",
                    getString(R.string.naira), "100,000,000.00")
                // view.visibility = View.GONE
            } else {
                btnHideShow.setImageResource(R.drawable.ic_visibility_off)
                expectedRevenue.text = "****"
            }
            clicked = !clicked
        }


        expenditureBtn.setOnClickListener {

            startActivity(Intent(activity, PaymentActivity().javaClass)
                .putExtra("from", "expenditure"))

        }

        receiptBtn.setOnClickListener {
            startActivity(Intent(activity, PaymentActivity().javaClass)
                .putExtra("from", "receipt"))
        }


        detailsBtn.setOnClickListener {
            startActivity(Intent(activity, PaymentActivity().javaClass)
                .putExtra("from", "details"))
        }



        return view
    }

}
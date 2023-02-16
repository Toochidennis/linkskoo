package com.digitaldream.winskool.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import com.digitaldream.winskool.R
import com.digitaldream.winskool.activities.PaymentActivity


class AdminPaymentFragment : Fragment() {

    private var clicked = false
    private lateinit var menuHost: MenuHost

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(
            R.layout.fragment_admin_payment, container,
            false
        )

        val toolbar: Toolbar = view.findViewById(R.id.toolbar)

        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        menuHost = requireActivity()

        setUpMenu()

        actionBar!!.apply {
            setHomeButtonEnabled(true)
            title = "Payment"
            setHomeAsUpIndicator(R.drawable.arrow_left)
            setDisplayHomeAsUpEnabled(true)
        }

        val expenditureBtn = view.findViewById<Button>(R.id.expenditure_btn)
        val receiptBtn = view.findViewById<Button>(R.id.receipt_btn)
        val detailsBtn = view.findViewById<CardView>(R.id.details)
        val expectedRevenue = view.findViewById<TextView>(R.id.expected_revenue)

//        btnHideShow.setOnClickListener {
//            if (clicked) {
//                btnHideShow.setImageResource(R.drawable.ic_eye_view)
//                expectedRevenue.text = String.format(Locale.getDefault(), "%s%s",
//                    getString(R.string.naira), "100,000,000.00")
//                // view.visibility = View.GONE
//            } else {
//                btnHideShow.setImageResource(R.drawable.ic_visibility_off)
//                expectedRevenue.text = "****"
//            }
//            clicked = !clicked
//        }

        expenditureBtn.setOnClickListener {

            startActivity(
                Intent(activity, PaymentActivity().javaClass)
                    .putExtra("from", "expenditure")
            )

        }

        receiptBtn.setOnClickListener {
            startActivity(
                Intent(activity, PaymentActivity().javaClass)
                    .putExtra("from", "receipt")
            )
        }

        detailsBtn.setOnClickListener {

            startActivity(
                Intent(activity, PaymentActivity().javaClass)
                    .putExtra("from", "details")
            )
        }

        return view
    }

    private fun setUpMenu() {
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.setup_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.settings -> {
                        startActivity(
                            Intent(context, PaymentActivity().javaClass).putExtra
                                ("from", "settings")
                        )
                        true
                    }
                    android.R.id.home -> {
                        requireActivity().onBackPressedDispatcher
                            .onBackPressed()
                        return true
                    }
                    else -> false
                }
            }
        })
    }
}
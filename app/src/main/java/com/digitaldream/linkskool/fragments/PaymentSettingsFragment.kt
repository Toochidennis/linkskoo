package com.digitaldream.linkskool.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.activities.PaymentActivity
import com.digitaldream.linkskool.dialog.TermFeeDialog


class PaymentSettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings_payment, container, false)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        toolbar.apply {
            title = "Payment Settings"
            setNavigationIcon(R.drawable.arrow_left)
            setNavigationOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        }

        val feeBtn = view.findViewById<CardView>(R.id.fee_settings)
        val termBtn: CardView = view.findViewById(R.id.term_settings)
        val accountBtn: CardView = view.findViewById(R.id.account_settings)

        feeBtn.setOnClickListener {
            startActivity(
                Intent(context, PaymentActivity().javaClass).putExtra(
                    "from",
                    "fee_settings"
                )
            )

        }

        termBtn.setOnClickListener {
            TermFeeDialog(requireContext(), "term", null)
                .apply {
                    setCancelable(true)
                    show()
                }.window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
        }

        accountBtn.setOnClickListener {
            startActivity(
                Intent(context, PaymentActivity().javaClass).putExtra(
                    "from",
                    "account_settings"
                )
            )
        }

        return view
    }
}
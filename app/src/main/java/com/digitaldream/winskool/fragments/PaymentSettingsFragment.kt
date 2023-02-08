package com.digitaldream.winskool.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.digitaldream.winskool.R
import com.digitaldream.winskool.activities.PaymentActivity
import com.digitaldream.winskool.dialog.TermFeeDialog

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class PaymentSettingsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PaymentSettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings_payment, container, false)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        toolbar.apply {
            setNavigationIcon(R.drawable.arrow_left)
            title = "Payment Settings"
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
            val termFeeDialog = TermFeeDialog(requireContext())
            termFeeDialog.apply {
                setCancelable(true)
                show()
            }
            val window = termFeeDialog.window
            window?.setLayout(
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
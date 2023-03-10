package com.digitaldream.winskool.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.digitaldream.winskool.R
import com.digitaldream.winskool.activities.PaymentActivity
import com.digitaldream.winskool.dialog.VendorDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class VendorFragment : Fragment() {

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
            VendorFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?, ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_vendor, container, false)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        toolbar.apply {
            setNavigationIcon(R.drawable.arrow_left)
            title = "Select a vendor"
            setNavigationOnClickListener { requireActivity().onBackPressedDispatcher
                .onBackPressed() }
        }

        val addVendorBtn = view.findViewById<FloatingActionButton>(R.id.add_vendor)

        addVendorBtn.setOnClickListener {
            val vendorDialog = VendorDialog(requireContext())
            vendorDialog.setCancelable(true)
            vendorDialog.show()
            val window = vendorDialog.window
            window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        val cardBtn = view.findViewById<CardView>(R.id.vendor_card)

        cardBtn.setOnClickListener {
            startActivity(Intent(requireContext(), PaymentActivity().javaClass)
                .putExtra("from", "vendor"))
        }

       // val searchBar = view.findViewById<EditText>(R.id.search_bar)

        return view;
    }

}
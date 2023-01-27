package com.digitaldream.winskool.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.digitaldream.winskool.R


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AdminTransactionDetails : Fragment() {
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
            AdminTransactionDetails().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_admin_transaction_details,
            container, false)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        setHasOptionsMenu(true)
        toolbar.apply {
            setNavigationIcon(R.drawable.arrow_left)
            title = "Transaction details"
            setNavigationOnClickListener { activity?.onBackPressed() }
        }

        return view
    }

}
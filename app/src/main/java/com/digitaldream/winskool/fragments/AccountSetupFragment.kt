package com.digitaldream.winskool.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.digitaldream.winskool.R
import com.digitaldream.winskool.dialog.AccountSetupDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AccountSetupFragment : Fragment() {

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
            AccountSetupFragment().apply {
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
        val view = inflater.inflate(R.layout.fragment_account_setup, container, false)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        val addAccountBtn: FloatingActionButton = view.findViewById(R.id.add_account_btn)


        ((activity as AppCompatActivity)).setSupportActionBar(toolbar)
        val actionBar = ((activity as AppCompatActivity)).supportActionBar

        actionBar!!.apply {
            setHomeButtonEnabled(true)
            title = "Account Settings"
            setHomeAsUpIndicator(R.drawable.arrow_left)
            setDisplayHomeAsUpEnabled(true)
        }

        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        addAccountBtn.setOnClickListener {
            openDialog()
        }

        return view
    }

    private fun openDialog() {
        val accountDialog = AccountSetupDialog(requireContext())
        accountDialog.apply {
            setCancelable(true)
            show()
        }
        val window = accountDialog.window
        window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

}
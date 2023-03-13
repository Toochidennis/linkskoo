package com.digitaldream.winskool.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.winskool.R
import com.digitaldream.winskool.adapters.VendorFragmentAdapter
import com.digitaldream.winskool.dialog.VendorDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton


class VendorFragment : Fragment() {

    private lateinit var mView: NestedScrollView
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mErrorView: LinearLayout
    private lateinit var mErrorMessage: TextView
    private lateinit var mAddBtn: FloatingActionButton
    private lateinit var mSearchInput: EditText
    private lateinit var mAdapter: VendorFragmentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_vendor, container, false)

        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        mView = view.findViewById(R.id.vendor_view)
        mRecyclerView = view.findViewById(R.id.vendor_recycler)
        mErrorView = view.findViewById(R.id.error_view)
        mErrorMessage = view.findViewById(R.id.vendor_error_message)
        mAddBtn = view.findViewById(R.id.add_vendor)
        mSearchInput = view.findViewById(R.id.search_bar)

        toolbar.apply {
            setNavigationIcon(R.drawable.arrow_left)
            title = "Select a vendor"
            setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher
                    .onBackPressed()
            }
        }


        mAddBtn.setOnClickListener {
            VendorDialog(requireContext()).apply {
                setCancelable(true)
                show()
            }.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }


        /*    cardBtn.setOnClickListener {
                startActivity(
                    Intent(requireContext(), PaymentActivity().javaClass)
                        .putExtra("from", "vendor")
                )
            }*/



        return view;
    }

}
package com.digitaldream.winskool.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.digitaldream.winskool.R


class SchoolFeesDetailsFragment : Fragment() {


    // android:text="FIRST TERM SCHOOL FEE CHARGES FOR 2022/2023 SESSION"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_student_fees_details, container, false)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        toolbar.apply {
            setNavigationIcon(R.drawable.arrow_left)
            title = "Fee Details"
            setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher
                    .onBackPressed()
            }
        }

        return view;
    }

}
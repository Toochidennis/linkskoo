package com.digitaldream.winskool.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.digitaldream.winskool.R
import com.digitaldream.winskool.models.TimeFrameData


class ReceiptsGroupingFragment(
    private val sTimeFrameData: TimeFrameData
) : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_receipts_grouping, container, false)
    }


}
package com.digitaldream.winskool.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.digitaldream.winskool.R
import com.digitaldream.winskool.models.TimeFrameDataModel
import com.digitaldream.winskool.utils.FunctionUtils.selectDeselectButton


class ExpenditureGroupingFragment(
    private val sTimeFrameDataModel: TimeFrameDataModel
) : Fragment(R.layout.fragment_expenditure_grouping) {


    private lateinit var mMonthBtn: Button
    private lateinit var mVendorBtn: Button
    private lateinit var mAccountBtn: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mMonthBtn = view.findViewById(R.id.month_btn)
        mVendorBtn = view.findViewById(R.id.vendor_btn)
        mAccountBtn = view.findViewById(R.id.account_btn)

        mMonthBtn.setOnClickListener { onClick(it) }
        mVendorBtn.setOnClickListener { onClick(it) }
        mAccountBtn.setOnClickListener { onClick(it) }
    }

    private fun onClick(view: View) {
        when (view.id) {

            R.id.month_btn -> {
                if (!mMonthBtn.isSelected) {
                    selectDeselectButton(mMonthBtn, "selected")
                    selectDeselectButton(mVendorBtn, "deselected")
                    selectDeselectButton(mAccountBtn, "deselected")

                    sTimeFrameDataModel.grouping = "By Month"
                } else {
                    selectDeselectButton(mMonthBtn, "deselected")
                    sTimeFrameDataModel.grouping = null
                }

            }

            R.id.vendor_btn -> {
                if (!mVendorBtn.isSelected) {
                    selectDeselectButton(mMonthBtn, "deselected")
                    selectDeselectButton(mVendorBtn, "selected")
                    selectDeselectButton(mAccountBtn, "deselected")

                    sTimeFrameDataModel.grouping = "By Vendor"
                } else {
                    selectDeselectButton(mVendorBtn, "deselected")
                    sTimeFrameDataModel.grouping = null
                }
            }

            R.id.account_btn -> {
                if (!mAccountBtn.isSelected) {
                    selectDeselectButton(mMonthBtn, "deselected")
                    selectDeselectButton(mVendorBtn, "deselected")
                    selectDeselectButton(mAccountBtn, "selected")

                    sTimeFrameDataModel.grouping = "By Account"
                } else {
                    selectDeselectButton(mAccountBtn, "deselected")
                    sTimeFrameDataModel.grouping = null
                }
            }


        }

    }
}
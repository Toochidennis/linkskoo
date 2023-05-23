package com.digitaldream.winskool.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import com.digitaldream.winskool.R
import com.digitaldream.winskool.dialog.FilterLevelClassDialog
import com.digitaldream.winskool.dialog.VendorAccountNamesBottomSheet
import com.digitaldream.winskool.models.TimeFrameDataModel
import com.digitaldream.winskool.utils.FunctionUtils.selectDeselectButton


class ExpenditureFilterFragment(
    private val sTimeFrameDataModel: TimeFrameDataModel
) : Fragment(R.layout.fragment_expenditure_filter) {

    private lateinit var mVendorBtn: Button
    private lateinit var mAccountBtn: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mVendorBtn = view.findViewById(R.id.vendor_btn)
        mAccountBtn = view.findViewById(R.id.account_btn)

        mVendorBtn.setOnClickListener { onClick(it) }
        mAccountBtn.setOnClickListener { onClick(it) }

        selectDeselectedButton()

    }

    private fun onClick(view: View) {
        when (view.id) {
            R.id.vendor_btn -> {
                if (!mVendorBtn.isSelected) {
                    selectDeselectButton(mVendorBtn, "selected")
                    selectDeselectButton(mAccountBtn, "deselected")
                    "Account".let { mAccountBtn.text = it }

                    VendorAccountNamesBottomSheet(
                        sTimeFrameDataModel,
                        "vendor"
                    ) { setSelectedName() }
                        .show(childFragmentManager, "vendor")

                } else {
                    selectDeselectButton(mVendorBtn, "deselected")
                    sTimeFrameDataModel.vendorId = null
                    sTimeFrameDataModel.vendorName = null
                    "Vendor".let { mVendorBtn.text = it }
                }
            }

            R.id.account_btn -> {
                if (!mAccountBtn.isSelected) {

                    selectDeselectButton(mVendorBtn, "deselected")
                    selectDeselectButton(mAccountBtn, "selected")
                    "Vendor".let { mVendorBtn.text = it }

                    VendorAccountNamesBottomSheet(
                        sTimeFrameDataModel,
                        "account"
                    ) { setSelectedName() }
                        .show(childFragmentManager, "account")

                } else {
                    sTimeFrameDataModel.accountId = null
                    sTimeFrameDataModel.accountName = null
                    "Account".let { mAccountBtn.text = it }
                    selectDeselectButton(mAccountBtn, "deselected")
                }
            }

        }
    }

    private fun selectDeselectedButton() {
        if (sTimeFrameDataModel.accountId != null) {
            selectDeselectButton(mAccountBtn, "selected")
            selectDeselectButton(mVendorBtn, "deselected")
            setBtnText(mAccountBtn, sTimeFrameDataModel.accountName.toString(), "account")
        } else if (sTimeFrameDataModel.vendorId != null) {
            selectDeselectButton(mVendorBtn, "selected")
            selectDeselectButton(mAccountBtn, "deselected")
            setBtnText(mAccountBtn, sTimeFrameDataModel.vendorName.toString(), "vendor")
        }
    }

    private fun setSelectedName() {
        if (sTimeFrameDataModel.vendorName != null) {
            setBtnText(mVendorBtn, sTimeFrameDataModel.vendorName.toString(), "vendor")
        } else if (sTimeFrameDataModel.accountName != null) {
            setBtnText(mAccountBtn, sTimeFrameDataModel.accountName.toString(), "account")
        } else {
            selectDeselectButton(mVendorBtn, "deselected")
            selectDeselectButton(mAccountBtn, "deselected")
        }

    }


    private fun setBtnText(button: Button, name: String, from: String) {
        if (from == "vendor") {
            "Vendor: $name".let { button.text = it }
        } else {
            "Account: $name".let { button.text = it }
        }

    }


}
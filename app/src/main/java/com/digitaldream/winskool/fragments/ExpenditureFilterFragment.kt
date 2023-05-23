package com.digitaldream.winskool.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import com.digitaldream.winskool.R
import com.digitaldream.winskool.dialog.VendorAccountNamesBottomSheet
import com.digitaldream.winskool.models.TimeFrameDataModel
import com.digitaldream.winskool.utils.FunctionUtils.selectDeselectButton
import org.json.JSONArray


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

                    VendorAccountNamesBottomSheet(
                        sTimeFrameDataModel,
                        "vendor"
                    ) { setSelectedName() }
                        .show(childFragmentManager, "vendor")

                } else {
                    selectDeselectButton(mVendorBtn, "deselected")
                    sTimeFrameDataModel.vendor = null
                    "Vendor".let { mVendorBtn.text = it }
                }
            }

            R.id.account_btn -> {
                if (!mAccountBtn.isSelected) {

                    selectDeselectButton(mAccountBtn, "selected")

                    VendorAccountNamesBottomSheet(
                        sTimeFrameDataModel,
                        "account"
                    ) { setSelectedName() }
                        .show(childFragmentManager, "account")

                } else {
                    sTimeFrameDataModel.account = null
                    "Account".let { mAccountBtn.text = it }
                    selectDeselectButton(mAccountBtn, "deselected")
                }
            }

        }
    }

    private fun selectDeselectedButton() {
        if (sTimeFrameDataModel.account != null) {
            selectDeselectButton(mAccountBtn, "selected")
            setBtnText(mAccountBtn, parseJson(sTimeFrameDataModel.account.toString()), "account")
        }

        if (sTimeFrameDataModel.vendor != null) {
            selectDeselectButton(mVendorBtn, "selected")
            setBtnText(mVendorBtn, parseJson(sTimeFrameDataModel.vendor.toString()), "vendor")
        }
    }

    private fun setSelectedName() {
        if (sTimeFrameDataModel.account != null) {
            setBtnText(mAccountBtn, parseJson(sTimeFrameDataModel.account.toString()), "account")
        } else {
            selectDeselectButton(mAccountBtn, "deselected")
        }

        if (sTimeFrameDataModel.vendor != null) {
            setBtnText(mVendorBtn, parseJson(sTimeFrameDataModel.vendor.toString()), "vendor")
        } else {
            selectDeselectButton(mVendorBtn, "deselected")
        }

    }

    private fun parseJson(json: String): String {
        val nameList = mutableListOf<String>()
        JSONArray(json).run {
            for (i in 0 until length()) {
                val name = getJSONObject(i).getString("name")
                nameList.add(name)
            }
        }

        return when (nameList.size) {
            0 -> ""
            1 -> nameList[0]
            else -> nameList.dropLast(1)
                .joinToString(separator = ", ") +
                    " & " + nameList.last()
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
package com.digitaldream.winskool.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.digitaldream.winskool.R
import com.digitaldream.winskool.dialog.FilterLevelClassDialog
import com.digitaldream.winskool.models.TimeFrameDataModel
import com.digitaldream.winskool.utils.FunctionUtils.selectDeselectButton
import org.json.JSONArray


class ReceiptsFilterFragment(
    private val sTimeFrameDataModel: TimeFrameDataModel
) : Fragment() {

    private lateinit var mClassBtn: Button
    private lateinit var mLevelBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_receipts_filter, container, false)

        mClassBtn = view.findViewById(R.id.class_btn)
        mLevelBtn = view.findViewById(R.id.level_btn)

        mClassBtn.setOnClickListener { onClick(it) }
        mLevelBtn.setOnClickListener { onClick(it) }

        selectDeselectedButton()

        return view
    }

    private fun onClick(view: View) {
        when (view.id) {
            R.id.class_btn -> {
                if (!mClassBtn.isSelected) {
                    selectDeselectButton(mClassBtn, "selected")

                    FilterLevelClassDialog(sTimeFrameDataModel, "class") { setSelectedName() }
                        .show(
                            childFragmentManager,
                            "Class Names"
                        )

                } else {
                    selectDeselectButton(mClassBtn, "deselected")
                    sTimeFrameDataModel.classData = null
                    "Class".let { mClassBtn.text = it }
                }
            }

            R.id.level_btn -> {
                if (!mLevelBtn.isSelected) {

                    selectDeselectButton(mLevelBtn, "selected")

                    FilterLevelClassDialog(sTimeFrameDataModel, "level") { setSelectedName() }
                        .show(
                            childFragmentManager,
                            "Level Names"
                        )

                } else {
                    sTimeFrameDataModel.levelData = null
                    "Level".let { mLevelBtn.text = it }
                    selectDeselectButton(mLevelBtn, "deselected")
                }
            }

        }
    }

    private fun selectDeselectedButton() {
        if (sTimeFrameDataModel.levelData != null) {
            selectDeselectButton(mLevelBtn, "selected")
            setBtnText(mLevelBtn, parseJson(sTimeFrameDataModel.account.toString()), "level")
        }

        if (sTimeFrameDataModel.classData != null) {
            selectDeselectButton(mClassBtn, "selected")
            setBtnText(mClassBtn, parseJson(sTimeFrameDataModel.vendor.toString()), "class")
        }
    }

    private fun setSelectedName() {
        if (sTimeFrameDataModel.levelData != null) {
            setBtnText(mLevelBtn, parseJson(sTimeFrameDataModel.account.toString()), "level")
        } else {
            selectDeselectButton(mLevelBtn, "deselected")
        }

        if (sTimeFrameDataModel.classData != null) {
            setBtnText(mClassBtn, parseJson(sTimeFrameDataModel.vendor.toString()), "class")
        } else {
            selectDeselectButton(mClassBtn, "deselected")
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
        if (from == "level") {
            "Level: $name".let { button.text = it }
        } else {
            "Class: $name".let { button.text = it }
        }

    }

}


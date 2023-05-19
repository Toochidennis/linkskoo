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
                    selectDeselectButton(mLevelBtn, "deselected")
                    "Level".let { mLevelBtn.text = it }

                    FilterLevelClassDialog(sTimeFrameDataModel, "class") { setSelectedName() }
                        .show(
                            childFragmentManager,
                            "Class Names"
                        )

                } else {
                    selectDeselectButton(mClassBtn, "deselected")
                    sTimeFrameDataModel.classId = null
                    sTimeFrameDataModel.className = null
                    "Class".let { mClassBtn.text = it }
                }
            }

            R.id.level_btn -> {
                if (!mLevelBtn.isSelected) {

                    selectDeselectButton(mClassBtn, "deselected")
                    selectDeselectButton(mLevelBtn, "selected")
                    "Class".let { mClassBtn.text = it }

                    FilterLevelClassDialog(sTimeFrameDataModel, "level") { setSelectedName() }
                        .show(
                            childFragmentManager,
                            "Level Names"
                        )

                } else {
                    sTimeFrameDataModel.levelId = null
                    sTimeFrameDataModel.levelName = null
                    "Level".let { mLevelBtn.text = it }
                    selectDeselectButton(mLevelBtn, "deselected")
                }
            }

        }
    }

    private fun selectDeselectedButton() {
        if (sTimeFrameDataModel.levelId != null) {
            selectDeselectButton(mLevelBtn, "selected")
            selectDeselectButton(mClassBtn, "deselected")
            setBtnText(mLevelBtn, sTimeFrameDataModel.levelName.toString(), "level")

        } else if (sTimeFrameDataModel.classId != null) {
            selectDeselectButton(mClassBtn, "selected")
            selectDeselectButton(mLevelBtn, "deselected")
            setBtnText(mLevelBtn, sTimeFrameDataModel.className.toString(), "class")
        }
    }

    private fun setSelectedName() {
        if (sTimeFrameDataModel.levelName != null) {
            setBtnText(mLevelBtn, sTimeFrameDataModel.levelName.toString(), "level")
        } else if (sTimeFrameDataModel.className != null) {
            setBtnText(mClassBtn, sTimeFrameDataModel.className.toString(), "class")
        } else {
            selectDeselectButton(mClassBtn, "deselected")
            selectDeselectButton(mLevelBtn, "deselected")
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


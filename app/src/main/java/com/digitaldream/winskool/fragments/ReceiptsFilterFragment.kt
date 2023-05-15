package com.digitaldream.winskool.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.digitaldream.winskool.R
import com.digitaldream.winskool.dialog.FilterLevelClassDialog
import com.digitaldream.winskool.models.TimeFrameData
import com.digitaldream.winskool.utils.FunctionUtils.selectDeselectButton


class ReceiptsFilterFragment(
    private val sTimeFrameData: TimeFrameData
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

                    FilterLevelClassDialog(sTimeFrameData, "class") { setSelectedName() }
                        .show(
                            childFragmentManager,
                            "Class Names"
                        )

                } else {
                    selectDeselectButton(mClassBtn, "deselected")
                    sTimeFrameData.classId = null
                    "Class".let { mClassBtn.text = it }
                }
            }

            R.id.level_btn -> {
                if (!mLevelBtn.isSelected) {

                    selectDeselectButton(mClassBtn, "deselected")
                    selectDeselectButton(mLevelBtn, "selected")
                    "Class".let { mClassBtn.text = it }

                    FilterLevelClassDialog(sTimeFrameData, "level") { setSelectedName() }
                        .show(
                            childFragmentManager,
                            "Level Names"
                        )

                } else {
                    sTimeFrameData.levelId = null
                    "Level".let { mLevelBtn.text = it }
                    selectDeselectButton(mLevelBtn, "deselected")
                }
            }


        }
    }

    private fun selectDeselectedButton() {
        if (sTimeFrameData.levelId != null) {
            selectDeselectButton(mLevelBtn, "selected")
            selectDeselectButton(mClassBtn, "deselected")
            setBtnText(mLevelBtn, sTimeFrameData.levelName.toString(), "level")

        } else if (sTimeFrameData.classId != null) {
            selectDeselectButton(mClassBtn, "selected")
            selectDeselectButton(mLevelBtn, "deselected")
            setBtnText(mLevelBtn, sTimeFrameData.className.toString(), "class")
        }
    }

    private fun setSelectedName() {
        if (sTimeFrameData.levelName != null) {
            setBtnText(mLevelBtn, sTimeFrameData.levelName.toString(), "level")
        } else if (sTimeFrameData.className != null) {
            setBtnText(mClassBtn, sTimeFrameData.className.toString(), "class")
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


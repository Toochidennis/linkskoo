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
import com.digitaldream.winskool.utils.FunctionUtils.deselectButton


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
                    deselectButton(mClassBtn, "selected")
                    deselectButton(mLevelBtn, "deselected")

                    FilterLevelClassDialog(sTimeFrameData, "class")
                        .show(
                            childFragmentManager,
                            "Class Names"
                        )

                } else {
                    deselectButton(mClassBtn, "deselected")
                    sTimeFrameData.className = null
                }
            }

            R.id.level_btn -> {
                if (!mLevelBtn.isSelected) {

                    deselectButton(mClassBtn, "deselected")
                    deselectButton(mLevelBtn, "selected")

                    FilterLevelClassDialog(sTimeFrameData, "level")
                        .show(
                            childFragmentManager,
                            "Level Names"
                        )

                } else {
                    sTimeFrameData.levelName = null
                    deselectButton(mLevelBtn, "deselected")
                }
            }


        }
    }

    private fun selectDeselectedButton() {
        if (sTimeFrameData.levelName != null) {
            deselectButton(mLevelBtn, "selected")
            deselectButton(mClassBtn, "deselected")
        } else if (sTimeFrameData.className != null) {
            deselectButton(mClassBtn, "selected")
            deselectButton(mLevelBtn, "deselected")
        }
    }

}


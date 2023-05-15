package com.digitaldream.winskool.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.digitaldream.winskool.R
import com.digitaldream.winskool.models.TimeFrameData
import com.digitaldream.winskool.utils.FunctionUtils.selectDeselectButton


class ReceiptsGroupingFragment(
    private val sTimeFrameData: TimeFrameData
) : Fragment() {

    private lateinit var mClassBtn: Button
    private lateinit var mLevelBtn: Button
    private lateinit var mMonthBtn: Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_receipts_grouping, container, false)

        mClassBtn = view.findViewById(R.id.class_btn)
        mLevelBtn = view.findViewById(R.id.level_btn)
        mMonthBtn = view.findViewById(R.id.month_btn)

        mClassBtn.setOnClickListener { onClick(it) }
        mLevelBtn.setOnClickListener { onClick(it) }
        mMonthBtn.setOnClickListener { onClick(it) }

        return view
    }

    private fun onClick(view: View) {
        when (view.id) {
            R.id.class_btn -> {
                if (!mClassBtn.isSelected) {
                    selectDeselectButton(mClassBtn, "selected")
                    selectDeselectButton(mLevelBtn, "deselected")
                    selectDeselectButton(mMonthBtn, "deselected")
                } else {
                    selectDeselectButton(mClassBtn, "deselected")

                }
            }

            R.id.level_btn -> {
                if (!mLevelBtn.isSelected) {
                    selectDeselectButton(mClassBtn, "deselected")
                    selectDeselectButton(mLevelBtn, "selected")
                    selectDeselectButton(mMonthBtn, "deselected")
                } else {
                    selectDeselectButton(mLevelBtn, "deselected")
                }
            }

            R.id.month_btn -> {
                if (!mMonthBtn.isSelected) {
                    selectDeselectButton(mClassBtn, "deselected")
                    selectDeselectButton(mLevelBtn, "deselected")
                    selectDeselectButton(mMonthBtn, "selected")
                } else {
                    selectDeselectButton(mMonthBtn, "deselected")
                }

            }

        }
    }

}
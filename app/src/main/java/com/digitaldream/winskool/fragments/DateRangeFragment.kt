package com.digitaldream.winskool.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.digitaldream.winskool.R
import com.digitaldream.winskool.activities.CourseAttendance.getDate
import com.digitaldream.winskool.dialog.DatePickerBottomSheet
import com.digitaldream.winskool.interfaces.DateListener
import com.digitaldream.winskool.models.TimeFrameDataModel
import com.digitaldream.winskool.utils.FunctionUtils
import com.digitaldream.winskool.utils.FunctionUtils.selectDeselectButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class DateRangeFragment(
    private val sTimeFrameDataModel: TimeFrameDataModel,
) : Fragment() {

    private lateinit var mCustomBtn: Button
    private lateinit var mTodayBtn: Button
    private lateinit var mYesterdayBtn: Button
    private lateinit var mThisWeekBtn: Button
    private lateinit var mLast7DaysBtn: Button
    private lateinit var mLastWeekBtn: Button
    private lateinit var mThisMonthBtn: Button
    private lateinit var mLast30DaysBtn: Button
    private lateinit var mStartDateInput: EditText
    private lateinit var mEndDateInput: EditText
    private lateinit var mStartDateTxt: TextView
    private lateinit var mEndDateTxt: TextView

    private var mStartDate: String? = null
    private var mEndDate: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_date_range, container, false)

        mCustomBtn = view.findViewById(R.id.custom_btn)
        mTodayBtn = view.findViewById(R.id.today_btn)
        mYesterdayBtn = view.findViewById(R.id.yesterday_btn)
        mThisWeekBtn = view.findViewById(R.id.this_week_btn)
        mLast7DaysBtn = view.findViewById(R.id.last_7_days_btn)
        mLastWeekBtn = view.findViewById(R.id.last_week_btn)
        mThisMonthBtn = view.findViewById(R.id.this_month_btn)
        mLast30DaysBtn = view.findViewById(R.id.last_30_days_btn)
        mStartDateInput = view.findViewById(R.id.start_date)
        mEndDateInput = view.findViewById(R.id.end_date)
        mStartDateTxt = view.findViewById(R.id.start_date_txt)
        mEndDateTxt = view.findViewById(R.id.end_date_txt)


        defaultDate()

        customStartDate()

        customEndDate()

        buttonClicks()

        if (sTimeFrameDataModel.duration != null) {
            selectDeselectedButton()
        } else {
            selectDeselectButton(mCustomBtn, "selected")
            sTimeFrameDataModel.duration = null
        }

        return view

    }

    private fun onClick(view: View) {
        when (view.id) {

            R.id.custom_btn -> {
                if (!mCustomBtn.isSelected) {
                    selectDeselectButton(mCustomBtn, "selected")
                    sTimeFrameDataModel.duration = null


                    mStartDateInput.apply {
                        isEnabled = true
                        setBackgroundResource(R.drawable.edit_text_bg3)
                    }

                    mEndDateInput.apply {
                        isEnabled = true
                        setBackgroundResource(R.drawable.edit_text_bg3)

                    }

                    mStartDateTxt.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.test_color_1
                        )
                    )
                    mEndDateTxt.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.test_color_1
                        )
                    )

                    selectDeselectButton(mTodayBtn, "deselected")

                    selectDeselectButton(mYesterdayBtn, "deselected")

                    selectDeselectButton(mThisWeekBtn, "deselected")

                    selectDeselectButton(mLast7DaysBtn, "deselected")

                    selectDeselectButton(mLastWeekBtn, "deselected")

                    selectDeselectButton(mThisMonthBtn, "deselected")

                    selectDeselectButton(mLast30DaysBtn, "deselected")

                } else {
                    selectDeselectButton(mCustomBtn, "deselected")
                    sTimeFrameDataModel.startDate = null
                    sTimeFrameDataModel.endDate = null
                    disableEditText()

                }
            }

            R.id.today_btn -> {
                if (!mTodayBtn.isSelected) {
                    selectDeselectButton(mTodayBtn, "selected")

                    sTimeFrameDataModel.duration = "Today"

                    selectDeselectButton(mCustomBtn, "deselected")

                    selectDeselectButton(mYesterdayBtn, "deselected")

                    selectDeselectButton(mThisWeekBtn, "deselected")

                    selectDeselectButton(mLast7DaysBtn, "deselected")

                    selectDeselectButton(mLastWeekBtn, "deselected")

                    selectDeselectButton(mThisMonthBtn, "deselected")

                    selectDeselectButton(mLast30DaysBtn, "deselected")

                    disableEditText()

                } else {
                    selectDeselectButton(mTodayBtn, "deselected")
                    sTimeFrameDataModel.duration = null
                }
            }

            R.id.yesterday_btn -> {

                if (!mYesterdayBtn.isSelected) {

                    selectDeselectButton(mCustomBtn, "deselected")

                    selectDeselectButton(mTodayBtn, "deselected")

                    selectDeselectButton(mYesterdayBtn, "selected")

                    sTimeFrameDataModel.duration = "Yesterday"

                    selectDeselectButton(mThisWeekBtn, "deselected")

                    selectDeselectButton(mLast7DaysBtn, "deselected")

                    selectDeselectButton(mLastWeekBtn, "deselected")

                    selectDeselectButton(mThisMonthBtn, "deselected")

                    selectDeselectButton(mLast30DaysBtn, "deselected")

                    disableEditText()

                } else {
                    selectDeselectButton(mYesterdayBtn, "deselected")
                    sTimeFrameDataModel.duration = null
                }
            }

            R.id.this_week_btn -> {
                if (!mThisWeekBtn.isSelected) {

                    selectDeselectButton(mCustomBtn, "deselected")

                    selectDeselectButton(mTodayBtn, "deselected")

                    selectDeselectButton(mYesterdayBtn, "deselected")

                    selectDeselectButton(mThisWeekBtn, "selected")

                    sTimeFrameDataModel.duration = "This Week"

                    selectDeselectButton(mLast7DaysBtn, "deselected")

                    selectDeselectButton(mLastWeekBtn, "deselected")

                    selectDeselectButton(mThisMonthBtn, "deselected")

                    selectDeselectButton(mLast30DaysBtn, "deselected")

                    disableEditText()

                } else {
                    selectDeselectButton(mThisWeekBtn, "deselected")
                    sTimeFrameDataModel.duration = null

                }
            }

            R.id.last_7_days_btn -> {

                if (!mLast7DaysBtn.isSelected) {

                    selectDeselectButton(mCustomBtn, "deselected")

                    selectDeselectButton(mTodayBtn, "deselected")

                    selectDeselectButton(mYesterdayBtn, "deselected")

                    selectDeselectButton(mThisWeekBtn, "deselected")

                    selectDeselectButton(mLast7DaysBtn, "selected")

                    sTimeFrameDataModel.duration = "Last 7 Days"

                    selectDeselectButton(mLastWeekBtn, "deselected")

                    selectDeselectButton(mThisMonthBtn, "deselected")

                    selectDeselectButton(mLast30DaysBtn, "deselected")

                    disableEditText()


                } else {
                    selectDeselectButton(mLast7DaysBtn, "deselected")
                    sTimeFrameDataModel.duration = null
                }
            }

            R.id.last_week_btn -> {

                if (!mLastWeekBtn.isSelected) {

                    selectDeselectButton(mCustomBtn, "deselected")

                    selectDeselectButton(mTodayBtn, "deselected")

                    selectDeselectButton(mYesterdayBtn, "deselected")

                    selectDeselectButton(mThisWeekBtn, "deselected")

                    selectDeselectButton(mLast7DaysBtn, "deselected")

                    selectDeselectButton(mLastWeekBtn, "selected")

                    sTimeFrameDataModel.duration = "Last Week"

                    selectDeselectButton(mThisMonthBtn, "deselected")

                    selectDeselectButton(mLast30DaysBtn, "deselected")

                    disableEditText()

                } else {
                    selectDeselectButton(mLastWeekBtn, "deselected")

                    sTimeFrameDataModel.duration = null
                }
            }

            R.id.this_month_btn -> {
                if (!mThisMonthBtn.isSelected) {

                    selectDeselectButton(mCustomBtn, "deselected")

                    selectDeselectButton(mTodayBtn, "deselected")

                    selectDeselectButton(mYesterdayBtn, "deselected")

                    selectDeselectButton(mThisWeekBtn, "deselected")

                    selectDeselectButton(mLast7DaysBtn, "deselected")

                    selectDeselectButton(mLastWeekBtn, "deselected")

                    selectDeselectButton(mThisMonthBtn, "selected")

                    sTimeFrameDataModel.duration = "This Month"

                    selectDeselectButton(mLast30DaysBtn, "deselected")

                    disableEditText()

                } else {
                    selectDeselectButton(mThisMonthBtn, "deselected")

                    sTimeFrameDataModel.duration = null

                }
            }

            R.id.last_30_days_btn -> {
                if (!mLast30DaysBtn.isSelected) {

                    selectDeselectButton(mCustomBtn, "deselected")

                    selectDeselectButton(mTodayBtn, "deselected")

                    selectDeselectButton(mYesterdayBtn, "deselected")

                    selectDeselectButton(mThisWeekBtn, "deselected")

                    selectDeselectButton(mLast7DaysBtn, "deselected")

                    selectDeselectButton(mLastWeekBtn, "deselected")

                    selectDeselectButton(mThisMonthBtn, "deselected")

                    selectDeselectButton(mLast30DaysBtn, "selected")

                    sTimeFrameDataModel.duration = "Last 30 Days"

                    disableEditText()

                } else {
                    selectDeselectButton(mLast30DaysBtn, "deselected")
                    sTimeFrameDataModel.duration = null

                }

            }

        }

    }

    private fun defaultDate() {
        try {
            val calendar = Calendar.getInstance()
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(getDate())
            calendar.time = date!!
            calendar.add(Calendar.MONTH, -1)
            mStartDate = sdf.format(calendar.time)

            mStartDateInput.setText(FunctionUtils.formatDate(mStartDate!!))
            mEndDateInput.setText(FunctionUtils.formatDate((getDate())))

            sTimeFrameDataModel.startDate = mStartDate
            sTimeFrameDataModel.endDate = getDate()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun customStartDate() {

        mStartDateInput.setOnClickListener {

            DatePickerBottomSheet(
                "start date",
                object : DateListener {
                    override fun selectedDate(selectedDate: String) {
                        if (selectedDate.isNotEmpty()) {
                            mStartDateInput.setText(FunctionUtils.formatDate(selectedDate))

                            mEndDate = FunctionUtils.getEndDate(selectedDate)
                            mEndDateInput.setText(FunctionUtils.formatDate(mEndDate!!))

                            sTimeFrameDataModel.startDate = selectedDate
                            sTimeFrameDataModel.endDate = mEndDate
                        }


                    }
                }
            ).show(requireActivity().supportFragmentManager, "Time frame")

        }

    }

    private fun customEndDate() {

        mEndDateInput.setOnClickListener {
            DatePickerBottomSheet(
                "end date",
                object : DateListener {
                    override fun selectedDate(selectedDate: String) {
                        if (selectedDate.isNotEmpty()) {
                            mEndDateInput.setText(FunctionUtils.formatDate(selectedDate))

                            sTimeFrameDataModel.endDate = selectedDate
                        }

                    }
                }
            ).show(requireActivity().supportFragmentManager, "Time frame")
        }
    }

    private fun disableEditText() {
        sTimeFrameDataModel.startDate = null
        sTimeFrameDataModel.endDate = null

        mStartDateInput.apply {
            isEnabled = false
            setBackgroundResource(R.drawable.edit_text_bg2)
        }

        mEndDateInput.apply {
            isEnabled = false
            setBackgroundResource(R.drawable.edit_text_bg2)

        }

        mStartDateTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.test_color_7))
        mEndDateTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.test_color_7))
    }

    private fun buttonClicks() {

        mTodayBtn.setOnClickListener { onClick(it) }

        mYesterdayBtn.setOnClickListener { onClick(it) }

        mThisWeekBtn.setOnClickListener { onClick(it) }

        mLast7DaysBtn.setOnClickListener { onClick(it) }

        mLastWeekBtn.setOnClickListener { onClick(it) }

        mThisMonthBtn.setOnClickListener { onClick(it) }

        mLast30DaysBtn.setOnClickListener { onClick(it) }

        mCustomBtn.setOnClickListener { onClick(it) }
    }

    private fun deselectAllButtons() {
        selectDeselectButton(mTodayBtn, "deselected")

        selectDeselectButton(mYesterdayBtn, "deselected")

        selectDeselectButton(mThisWeekBtn, "deselected")

        selectDeselectButton(mLast7DaysBtn, "deselected")

        selectDeselectButton(mLastWeekBtn, "deselected")

        selectDeselectButton(mThisMonthBtn, "deselected")

        selectDeselectButton(mLast30DaysBtn, "deselected")

        sTimeFrameDataModel.duration = null
    }

    private fun selectDeselectedButton() {
        when (sTimeFrameDataModel.duration) {

            "Today" -> selectDeselectButton(mTodayBtn, "selected")

            "Yesterday" -> selectDeselectButton(mYesterdayBtn, "selected")

            "This Week" -> selectDeselectButton(mThisWeekBtn, "selected")

            "Last 7 Days" -> selectDeselectButton(mLast7DaysBtn, "selected")

            "Last Week" -> selectDeselectButton(mLastWeekBtn, "selected")

            "This Month" -> selectDeselectButton(mThisMonthBtn, "selected")

            "Last 30 Days" -> selectDeselectButton(mLast30DaysBtn, "selected")

            else -> deselectAllButtons()
        }

        disableEditText()
    }


}
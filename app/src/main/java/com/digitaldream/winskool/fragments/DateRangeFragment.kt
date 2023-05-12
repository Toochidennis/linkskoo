package com.digitaldream.winskool.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.digitaldream.winskool.R
import com.digitaldream.winskool.activities.CourseAttendance.getDate
import com.digitaldream.winskool.dialog.DatePickerBottomSheet
import com.digitaldream.winskool.interfaces.DateListener
import com.digitaldream.winskool.models.TimeFrameData
import com.digitaldream.winskool.utils.FunctionUtils
import com.digitaldream.winskool.utils.FunctionUtils.deselectButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class DateRangeFragment(
    private val sTimeFrameData: TimeFrameData,
    val dismiss: () -> Unit
) : Fragment() {

    private lateinit var mTodayBtn: Button
    private lateinit var mYesterdayBtn: Button
    private lateinit var mThisWeekBtn: Button
    private lateinit var mLast7DaysBtn: Button
    private lateinit var mLastWeekBtn: Button
    private lateinit var mThisMonthBtn: Button
    private lateinit var mLast30DaysBtn: Button
    private lateinit var mStartDateInput: EditText
    private lateinit var mEndDateInput: EditText

    private var mStartDate: String? = null
    private var mEndDate: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_date_range, container, false)

        mTodayBtn = view.findViewById(R.id.today_btn)
        mYesterdayBtn = view.findViewById(R.id.yesterday_btn)
        mThisWeekBtn = view.findViewById(R.id.this_week_btn)
        mLast7DaysBtn = view.findViewById(R.id.last_7_days_btn)
        mLastWeekBtn = view.findViewById(R.id.last_week_btn)
        mThisMonthBtn = view.findViewById(R.id.this_month_btn)
        mLast30DaysBtn = view.findViewById(R.id.last_30_days_btn)
        mStartDateInput = view.findViewById(R.id.start_date)
        mEndDateInput = view.findViewById(R.id.end_date)

        defaultDate()

        customStartDate()

        customEndDate()

        buttonClicks()

        selectDeselectedButton()


        return view

    }

    private fun onClick(view: View) {
        when (view.id) {

            R.id.today_btn -> {
                if (!mTodayBtn.isSelected) {
                    deselectButton(mTodayBtn, "selected")

                    sTimeFrameData.duration = "Today"

                    deselectButton(mYesterdayBtn, "deselected")

                    deselectButton(mThisWeekBtn, "deselected")

                    deselectButton(mLast7DaysBtn, "deselected")

                    deselectButton(mLastWeekBtn, "deselected")

                    deselectButton(mThisMonthBtn, "deselected")

                    deselectButton(mLast30DaysBtn, "deselected")

                } else {
                    deselectButton(mTodayBtn, "deselected")
                    sTimeFrameData.duration = null
                }
            }

            R.id.yesterday_btn -> {

                if (!mYesterdayBtn.isSelected) {

                    deselectButton(mTodayBtn, "deselected")

                    deselectButton(mYesterdayBtn, "selected")

                    sTimeFrameData.duration = "Yesterday"

                    deselectButton(mThisWeekBtn, "deselected")

                    deselectButton(mLast7DaysBtn, "deselected")

                    deselectButton(mLastWeekBtn, "deselected")

                    deselectButton(mThisMonthBtn, "deselected")

                    deselectButton(mLast30DaysBtn, "deselected")

                } else {
                    deselectButton(mYesterdayBtn, "deselected")
                    sTimeFrameData.duration = null
                }
            }

            R.id.this_week_btn -> {
                if (!mThisWeekBtn.isSelected) {

                    deselectButton(mTodayBtn, "deselected")

                    deselectButton(mYesterdayBtn, "deselected")

                    deselectButton(mThisWeekBtn, "selected")

                    sTimeFrameData.duration = "This Week"

                    deselectButton(mLast7DaysBtn, "deselected")

                    deselectButton(mLastWeekBtn, "deselected")

                    deselectButton(mThisMonthBtn, "deselected")

                    deselectButton(mLast30DaysBtn, "deselected")

                } else {
                    deselectButton(mThisWeekBtn, "deselected")
                    sTimeFrameData.duration = null

                }
            }

            R.id.last_7_days_btn -> {

                if (!mLast7DaysBtn.isSelected) {

                    deselectButton(mTodayBtn, "deselected")

                    deselectButton(mYesterdayBtn, "deselected")

                    deselectButton(mThisWeekBtn, "deselected")

                    deselectButton(mLast7DaysBtn, "selected")

                    sTimeFrameData.duration = "Last 7 Days"

                    deselectButton(mLastWeekBtn, "deselected")

                    deselectButton(mThisMonthBtn, "deselected")

                    deselectButton(mLast30DaysBtn, "deselected")


                } else {
                    deselectButton(mLast7DaysBtn, "deselected")
                    sTimeFrameData.duration = null
                }
            }

            R.id.last_week_btn -> {

                if (!mLastWeekBtn.isSelected) {

                    deselectButton(mTodayBtn, "deselected")

                    deselectButton(mYesterdayBtn, "deselected")

                    deselectButton(mThisWeekBtn, "deselected")

                    deselectButton(mLast7DaysBtn, "deselected")

                    deselectButton(mLastWeekBtn, "selected")

                    sTimeFrameData.duration = "Last Week"

                    deselectButton(mThisMonthBtn, "deselected")

                    deselectButton(mLast30DaysBtn, "deselected")

                } else {
                    deselectButton(mLastWeekBtn, "deselected")

                    sTimeFrameData.duration = null
                }
            }

            R.id.this_month_btn -> {
                if (!mThisMonthBtn.isSelected) {

                    deselectButton(mTodayBtn, "deselected")

                    deselectButton(mYesterdayBtn, "deselected")

                    deselectButton(mThisWeekBtn, "deselected")

                    deselectButton(mLast7DaysBtn, "deselected")

                    deselectButton(mLastWeekBtn, "deselected")

                    deselectButton(mThisMonthBtn, "selected")

                    sTimeFrameData.duration = "This Month"

                    deselectButton(mLast30DaysBtn, "deselected")

                } else {
                    deselectButton(mThisMonthBtn, "deselected")

                    sTimeFrameData.duration = null

                }
            }

            R.id.last_30_days_btn -> {
                if (!mLast30DaysBtn.isSelected) {

                    deselectButton(mTodayBtn, "deselected")

                    deselectButton(mYesterdayBtn, "deselected")

                    deselectButton(mThisWeekBtn, "deselected")

                    deselectButton(mLast7DaysBtn, "deselected")

                    deselectButton(mLastWeekBtn, "deselected")

                    deselectButton(mThisMonthBtn, "deselected")

                    deselectButton(mLast30DaysBtn, "selected")

                    sTimeFrameData.duration = "Last 30 Days"

                } else {
                    deselectButton(mLast30DaysBtn, "deselected")
                    sTimeFrameData.duration = null

                }

            }

        }

    }


    private fun defaultDate() {
        try {
            val calendar = Calendar.getInstance()
            mEndDate = getDate()
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(mEndDate!!)
            calendar.time = date!!
            calendar.add(Calendar.MONTH, -1)
            mStartDate = sdf.format(calendar.time)

            mStartDateInput.setText(FunctionUtils.formatDate(mStartDate!!))
            mEndDateInput.setText(FunctionUtils.formatDate((mEndDate!!)))

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

                            sTimeFrameData.endDate = mEndDate
                            sTimeFrameData.startDate = selectedDate
                        }


                    }
                }
            ).show(requireActivity().supportFragmentManager, "Time frame")


            deselectAllButtons()
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

                            sTimeFrameData.endDate = selectedDate
                        }

                    }
                }
            ).show(requireActivity().supportFragmentManager, "Time frame")

            deselectAllButtons()
        }
    }

    private fun buttonClicks() {

        mTodayBtn.setOnClickListener { onClick(it) }

        mYesterdayBtn.setOnClickListener { onClick(it) }

        mThisWeekBtn.setOnClickListener { onClick(it) }

        mLast7DaysBtn.setOnClickListener { onClick(it) }

        mLastWeekBtn.setOnClickListener { onClick(it) }

        mThisMonthBtn.setOnClickListener { onClick(it) }

        mLast30DaysBtn.setOnClickListener { onClick(it) }
    }

    private fun deselectAllButtons() {
        deselectButton(mTodayBtn, "deselected")

        deselectButton(mYesterdayBtn, "deselected")

        deselectButton(mThisWeekBtn, "deselected")

        deselectButton(mLast7DaysBtn, "deselected")

        deselectButton(mLastWeekBtn, "deselected")

        deselectButton(mThisMonthBtn, "deselected")

        deselectButton(mLast30DaysBtn, "deselected")

        sTimeFrameData.duration = null
    }

    private fun selectDeselectedButton() {
        when (sTimeFrameData.duration) {

            "Today" -> deselectButton(mTodayBtn, "selected")

            "Yesterday" -> deselectButton(mYesterdayBtn, "selected")

            "This Week" -> deselectButton(mThisWeekBtn, "selected")

            "Last 7 Days" -> deselectButton(mLast7DaysBtn, "selected")

            "Last Week" -> deselectButton(mLastWeekBtn, "selected")

            "This Month" -> deselectButton(mThisMonthBtn, "selected")

            "Last 30 Days" -> deselectButton(mLast30DaysBtn, "selected")

            else -> deselectAllButtons()


        }
    }

}
package com.digitaldream.winskool.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.digitaldream.winskool.R
import com.digitaldream.winskool.dialog.DatePickerBottomSheet
import com.digitaldream.winskool.interfaces.DateListener
import com.digitaldream.winskool.models.TimeFrameData
import com.digitaldream.winskool.utils.FunctionUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class DateRangeFragment(
    private val sTimeFrameData: TimeFrameData,
    val dismiss: ()->Unit
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


        mStartDateInput.setOnClickListener {
            DatePickerBottomSheet(
                "start date",
                object : DateListener {
                    override fun selectedDate(selectedDate: String) {
                        if (selectedDate.isNotEmpty()){
                            mStartDateInput.setText(FunctionUtils.formatDate(selectedDate))

                            mEndDate = FunctionUtils.getEndDate(selectedDate)
                            mEndDateInput.setText(FunctionUtils.formatDate(mEndDate!!))
                        }


                    }
                }
            ).show(requireActivity().supportFragmentManager, "Time frame")
        }


        mEndDateInput.setOnClickListener {
            DatePickerBottomSheet(
                "end date",
                object : DateListener {
                    override fun selectedDate(selectedDate: String) {
                        if (selectedDate.isNotEmpty()) {
                            mEndDateInput.setText(FunctionUtils.formatDate(selectedDate))
                        }

                    }
                }
            ).show(requireActivity().supportFragmentManager, "Time frame")
        }

        defaultDate()

        buttonsOnClick()

        confirmBtn()


        return view

    }

    private fun confirmBtn() {

//        mConfirmBtn.setOnClickListener {
//            if (mStartDate.isNullOrEmpty()) {
//                Toast.makeText(requireContext(), "Start date is not selected", Toast.LENGTH_SHORT)
//                    .show()
//            } else if (mEndDate.isNullOrEmpty()) {
//                Toast.makeText(requireContext(), "End date is not selected", Toast.LENGTH_SHORT)
//                    .show()
//            } else {
//              //  sOnclick.startAndEndDate(mStartDate, mEndDate)
//                println("start date: $mStartDate end date: $mEndDate")
//
//            }
//        }
    }

    private fun buttonsOnClick() {

        mTodayBtn.setOnClickListener {
            //sOnclick.singleDate("Today")
            sTimeFrameData.others = "Sent"
            dismiss()
        }
//
//        mYesterdayBtn.setOnClickListener {
//            sOnclick.singleDate("Yesterday")
//        }
//
//        mThisWeekBtn.setOnClickListener {
//            sOnclick.singleDate("This Week")
//
//        }
//
//        mLast7DaysBtn.setOnClickListener {
//            sOnclick.singleDate("Last 7 Days")
//
//        }
//
//        mLastWeekBtn.setOnClickListener {
//            sOnclick.singleDate("Last Week")
//
//        }
//
//        mThisMonthBtn.setOnClickListener {
//            sOnclick.singleDate("This Month")
//        }
//
//        mLast30DaysBtn.setOnClickListener {
//            sOnclick.singleDate("Last 30 Days")
//
//        }

    }

    private fun defaultDate() {
        try {
            val calendar = Calendar.getInstance()
            mEndDate = FunctionUtils.getDate()
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

}
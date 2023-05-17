package com.digitaldream.winskool.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.NumberPicker
import androidx.cardview.widget.CardView
import com.digitaldream.winskool.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.Calendar

class TermSessionPickerBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_term_session_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val termPicker: NumberPicker = view.findViewById(R.id.term_picker)
        val sessionPicker: NumberPicker = view.findViewById(R.id.session_picker)
        val confirmBtn: CardView = view.findViewById(R.id.confirm_btn)
        val dismissBtn: ImageView = view.findViewById(R.id.close_btn)

        dismissBtn.setOnClickListener { dismiss() }

        val currentTerm = requireActivity()
            .getSharedPreferences("loginDetail", Context.MODE_PRIVATE)
            .getString("term", "")

        val terms = resources.getStringArray(R.array.terms)

        termPicker.apply {
            minValue = 0
            maxValue = terms.size - 1
            displayedValues = terms
        }

        when (currentTerm) {
            "1" -> termPicker.value = 0
            "2" -> termPicker.value = 1
            "3" -> termPicker.value = 2
        }


        val sessionList = mutableListOf<String>()
        val calendar = Calendar.getInstance().get(Calendar.YEAR)
        val futureYear = calendar + 2
        val last20Years = futureYear - 20

        for (year in last20Years..futureYear) {
            sessionList.add("${year - 1}/$year")
        }

        sessionPicker.apply {
            minValue = 0
            maxValue = sessionList.size - 1
            wrapSelectorWheel = false
            displayedValues = sessionList.toTypedArray()
            value = sessionList.indexOf(sessionList[sessionList.lastIndex - 2])
        }

    }

}
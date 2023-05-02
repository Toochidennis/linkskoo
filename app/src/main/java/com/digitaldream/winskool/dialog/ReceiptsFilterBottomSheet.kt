package com.digitaldream.winskool.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.digitaldream.winskool.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ReceiptsFilterBottomSheet: BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_receipts_filter, container, false)


        return view
    }
}
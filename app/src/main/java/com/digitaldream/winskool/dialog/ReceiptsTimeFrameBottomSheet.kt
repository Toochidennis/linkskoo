package com.digitaldream.winskool.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.digitaldream.winskool.R
import com.digitaldream.winskool.adapters.SectionPagerAdapter
import com.digitaldream.winskool.fragments.DateRangeFragment
import com.digitaldream.winskool.fragments.ReceiptsFilterFragment
import com.digitaldream.winskool.fragments.ReceiptsGroupingFragment
import com.digitaldream.winskool.models.TimeFrameData
import com.digitaldream.winskool.utils.CustomViewPager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout

class ReceiptsTimeFrameBottomSheet(
    private val timeFrameData: TimeFrameData
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.bottom_sheet_receipts_time_frame, container, false)

        val tabLayout: TabLayout = view.findViewById(R.id.tab_layout)
        val viewPager: CustomViewPager = view.findViewById(R.id.time_frame_container)
        val adapter = SectionPagerAdapter(childFragmentManager)

        adapter.apply {
            addFragment(DateRangeFragment(timeFrameData) { dismissDialog() }, "Date Range")
            addFragment(ReceiptsGroupingFragment(timeFrameData), "Grouping")
            addFragment(ReceiptsFilterFragment(timeFrameData) { dismissDialog() }, "Filter")

            viewPager.adapter = adapter
            tabLayout.setupWithViewPager(viewPager, true)

        }


        return view

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        timeFrameData.getData()
    }

    private fun dismissDialog() {
        dismiss()
    }


}
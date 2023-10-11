package com.digitaldream.toyibatskool.dialog

import android.os.Bundle
import android.view.View
import androidx.cardview.widget.CardView
import androidx.viewpager.widget.ViewPager
import com.digitaldream.toyibatskool.R
import com.digitaldream.toyibatskool.adapters.SectionPagerAdapter
import com.digitaldream.toyibatskool.fragments.DateRangeFragment
import com.digitaldream.toyibatskool.fragments.ReceiptsFilterFragment
import com.digitaldream.toyibatskool.fragments.ReceiptsGroupingFragment
import com.digitaldream.toyibatskool.models.TimeFrameDataModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout

class ReceiptTimeFrameBottomSheet(
    private val sTimeFrameDataModel: TimeFrameDataModel
) : BottomSheetDialogFragment(R.layout.bottom_sheet_receipts_time_frame) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout: TabLayout = view.findViewById(R.id.tab_layout)
        val viewPager: ViewPager = view.findViewById(R.id.view_pager)
        val adapter = SectionPagerAdapter(childFragmentManager)
        val generateReportBtn: CardView = view.findViewById(R.id.confirm_btn)

        adapter.apply {
            addFragment(DateRangeFragment(sTimeFrameDataModel), "Date Range")
            addFragment(ReceiptsGroupingFragment(sTimeFrameDataModel), "Grouping")
            addFragment(ReceiptsFilterFragment(sTimeFrameDataModel), "Filter")

            viewPager.adapter = this
            tabLayout.setupWithViewPager(viewPager, true)
        }

        generateReportBtn.setOnClickListener {
            dismiss()
            sTimeFrameDataModel.getData()
        }
    }


}
package com.digitaldream.winskool.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.viewpager.widget.ViewPager
import com.digitaldream.winskool.R
import com.digitaldream.winskool.adapters.SectionPagerAdapter
import com.digitaldream.winskool.fragments.DateRangeFragment
import com.digitaldream.winskool.fragments.ReceiptsFilterFragment
import com.digitaldream.winskool.fragments.ReceiptsGroupingFragment
import com.digitaldream.winskool.models.TimeFrameData
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout

class ReceiptTimeFrameBottomSheet(
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
        val viewPager: ViewPager = view.findViewById(R.id.view_pager)
        val adapter = SectionPagerAdapter(childFragmentManager)

        adapter.apply {
            addFragment(DateRangeFragment(timeFrameData) { dismissDialog() }, "Date Range")
            addFragment(ReceiptsGroupingFragment(timeFrameData), "Grouping")
            addFragment(ReceiptsFilterFragment(timeFrameData), "Filter")

            viewPager.adapter = this
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

/*childFragmentManager.commit {
    replace(R.id.frame_layout, DateRangeFragment
        (timeFrameData) { dismissDialog() })

    setTransition(
        FragmentTransaction
            .TRANSIT_FRAGMENT_OPEN
    )


var fragment: Fragment

tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
    override fun onTabSelected(tab: TabLayout.Tab?) {

        fragment = when (tab?.position) {
            0 -> {
                DateRangeFragment(timeFrameData) { dismissDialog() }
            }

            1-> {
                ReceiptsGroupingFragment(timeFrameData)
            }

            else ->{
                ReceiptsFilterFragment(timeFrameData) { dismissDialog() }
            }
        }

        childFragmentManager.commit {
            replace(R.id.frame_layout, fragment)

            setTransition(
                FragmentTransaction
                    .TRANSIT_FRAGMENT_OPEN
            )

        }

    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {

    }

    override fun onTabReselected(tab: TabLayout.Tab?) {

    }
})


}*/

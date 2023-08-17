package com.digitaldream.linkskool.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.SectionPagerAdapter
import com.google.android.material.tabs.TabLayout

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AdminELearningAssignmentPreviewFragment :
    Fragment(R.layout.fragment_admin_e_learning_assignment_preview) {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager


    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdminELearningAssignmentPreviewFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView(view)

        setUpPager()
    }

    private fun setUpView(view: View) {
        view.apply {
            val toolbar:Toolbar = findViewById(R.id.toolbar)
            tabLayout = findViewById(R.id.tabLayout)
            viewPager = findViewById(R.id.viewPager)

            toolbar.apply {
               setNavigationIcon(R.drawable.arrow_left)
                setNavigationOnClickListener {
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }
    }

    private fun setUpPager() {
        SectionPagerAdapter(parentFragmentManager).apply {
            addFragment(AdminELearningAssignmentInstructionsFragment(), "Instructions")
            addFragment(AdminELearningAssignmentStudentWorkFragment(), "Student Work")
        }.let {
            viewPager.apply {
                adapter = it
                tabLayout.setupWithViewPager(viewPager, true)
            }
        }
    }

}
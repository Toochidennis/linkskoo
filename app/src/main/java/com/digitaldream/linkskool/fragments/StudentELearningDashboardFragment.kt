package com.digitaldream.linkskool.fragments

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.SectionPagerAdapter
import com.digitaldream.linkskool.adapters.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class StudentELearningDashboardFragment : Fragment(R.layout.fragment_student_e_learning_dashboard) {

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
            StudentELearningDashboardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        setUpViewPager()
    }

    private fun setUpViews(view: View) {
        view.apply {
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            tabLayout = findViewById(R.id.tabLayout)
            viewPager = findViewById(R.id.viewPager)

            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
            val actionBar = (requireActivity() as AppCompatActivity).supportActionBar

            val outlineTitle = requireActivity()
                .getSharedPreferences("loginDetail", MODE_PRIVATE)
                .getString("outline_title", "")

            actionBar?.apply {
                title = outlineTitle
                setHomeButtonEnabled(true)
                setDisplayHomeAsUpEnabled(true)
            }

            toolbar.setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

        }
    }

    private fun setUpViewPager() {
        val pagerAdapter = SectionPagerAdapter(parentFragmentManager).apply {
            addFragment(StudentELearningStreamFragment(), "Stream")
            addFragment(StudentELearningClassworkFragment(), "Classwork")
        }

        viewPager.adapter = pagerAdapter

        tabLayout.setupWithViewPager(
            viewPager, true
        )
    }
}
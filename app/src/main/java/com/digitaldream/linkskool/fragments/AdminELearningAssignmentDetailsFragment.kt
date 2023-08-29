package com.digitaldream.linkskool.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.SectionPagerAdapter
import com.digitaldream.linkskool.models.ActionBarViewModel
import com.digitaldream.linkskool.utils.CustomViewPager2
import com.google.android.material.tabs.TabLayout

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AdminELearningAssignmentDetailsFragment :
    Fragment(R.layout.fragment_admin_e_learning_assignment_details) {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: CustomViewPager2
    private lateinit var menuHost: MenuHost
    private var actionBar: ActionBar? = null
    private var customActionBarView: View? = null
    var menuView: Menu? = null

    private lateinit var actionBarViewModel: ActionBarViewModel

    private var json: String? = null
    private var from: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            json = it.getString(ARG_PARAM1)
            from = it.getString(ARG_PARAM2)
        }

        actionBarViewModel = ViewModelProvider(requireActivity())[ActionBarViewModel::class.java]

    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdminELearningAssignmentDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView(view)
        defaultActionBar()
        setUpViewPager()

        actionBarViewModel.customActionBarVisible.observe(requireActivity()) { showCustomActionBar ->
            viewPager.setCustomBarVisibility(showCustomActionBar)

            for (i in 0 until tabLayout.tabCount){
                tabLayout.getTabAt(i)?.view?.isClickable = !showCustomActionBar
            }

            if (showCustomActionBar) {
                setUpCustomActionBar()
            } else {
                defaultActionBar()
            }
        }
    }

    private fun setUpView(view: View) {
        view.apply {
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            tabLayout = findViewById(R.id.tabLayout)
            viewPager = findViewById(R.id.viewPager)

            menuHost = requireActivity()
            (activity as AppCompatActivity).setSupportActionBar(toolbar)
        }
    }

    private fun setUpViewPager() {
        SectionPagerAdapter(childFragmentManager).apply {
            addFragment(AdminELearningAssignmentInstructionsFragment(), "Instructions")
            addFragment(AdminELearningAssignmentStudentWorkFragment(), "Student work")
        }.let {
            viewPager.apply {
                adapter = it
                currentItem = 1
                tabLayout.setupWithViewPager(viewPager, true)
            }
        }
    }

    private fun defaultActionBar() {
        actionBar = (activity as AppCompatActivity).supportActionBar

        actionBar?.apply {
            displayOptions = ActionBar.DISPLAY_SHOW_TITLE
            setHomeButtonEnabled(true)
            title = ""
            setDisplayHomeAsUpEnabled(true)
            customView = null
        }

        setUpMenu()
    }

    private fun setUpCustomActionBar() {
        customActionBarView = layoutInflater.inflate(R.layout.custom_action_bar_layout, null)
        customActionBarView?.apply {
        }

        actionBar?.apply {
            displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            customView = customActionBarView
        }

    }


    private fun setUpMenu() {
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_e_learning_details, menu)
                menuView = menu
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.refresh -> {
                        // setUpPager()

                        true
                    }

                    R.id.edit -> {
                        true
                    }

                    R.id.delete -> {
                        true
                    }

                    else -> {
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                        false
                    }

                }
            }
        })
    }
}
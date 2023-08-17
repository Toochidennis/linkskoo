package com.digitaldream.linkskool.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
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
    private lateinit var menuHost: MenuHost


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
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            tabLayout = findViewById(R.id.tabLayout)
            viewPager = findViewById(R.id.viewPager)

            (activity as AppCompatActivity).setSupportActionBar(toolbar)
            val actionBar = (activity as AppCompatActivity).supportActionBar
            menuHost = requireActivity()

            setUpMenu()

            actionBar!!.apply {
                setHomeButtonEnabled(true)
                title = ""
                setDisplayHomeAsUpEnabled(true)
            }

        }
    }

    private fun setUpPager() {
        SectionPagerAdapter(childFragmentManager).apply {
            addFragment(AdminELearningAssignmentInstructionsFragment(), "Instructions")
            addFragment(AdminELearningAssignmentStudentWorkFragment(), "Student work")
        }.let {
            viewPager.apply {
                adapter = it
                tabLayout.setupWithViewPager(viewPager, true)
            }
        }
    }

    private fun setUpMenu() {
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.e_learning_preview_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.refresh -> {
                        setUpPager()
                        
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
package com.digitaldream.linkskool.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.SectionPagerAdapter
import com.digitaldream.linkskool.fragments.StudentELearningCourseworkFragment
import com.digitaldream.linkskool.fragments.StudentELearningStreamFragment
import com.google.android.material.tabs.TabLayout

class StudentELearningCourseOutlineActivity :
    AppCompatActivity(R.layout.activity_student_e_learning_course_outline) {

    private lateinit var courseViewPager: ViewPager
    private lateinit var courseTabLayout: TabLayout
    private var actionBar: ActionBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpViews()

        setUpViewPager()
    }

    private fun setUpViews() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        courseTabLayout = findViewById(R.id.courseTabLayout)
        courseViewPager = findViewById(R.id.courseViewPager)

        setSupportActionBar(toolbar)
        actionBar = supportActionBar

        actionBar?.apply {
            title = ""
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setUpViewPager() {
        val pagerAdapter = SectionPagerAdapter(supportFragmentManager).apply {
            addFragment(StudentELearningCourseworkFragment(), "Coursework")
            addFragment(StudentELearningStreamFragment(), "Stream")
        }

        courseViewPager.adapter = pagerAdapter
        courseTabLayout.setupWithViewPager(courseViewPager, true)
        courseTabLayout.getTabAt(0)?.setIcon(R.drawable.ic_assignment_black_24dp)
        courseTabLayout.getTabAt(1)?.setIcon(R.drawable.ic_forum_24)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return false
    }


}
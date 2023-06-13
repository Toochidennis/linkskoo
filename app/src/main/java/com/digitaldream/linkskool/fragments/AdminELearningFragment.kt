package com.digitaldream.linkskool.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.activities.ELearningActivity


private const val COURSE_NAME = "course_name"
private const val COURSE_ID = "course_id"
private const val LEVEL_NAME = "level_name"
private const val LEVEL_ID = "level_id"

class AdminELearningFragment : Fragment(R.layout.fragment_admin_elearning) {


    private var mCourseName: String? = null
    private var mCourseId: String? = null
    private var mLevelName: String? = null
    private var mLevelId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mCourseName = it.getString(COURSE_NAME)
            mCourseId = it.getString(COURSE_ID)
            mLevelName = it.getString(LEVEL_NAME)
            mLevelId = it.getString(LEVEL_ID)
        }
    }


    companion object {

        @JvmStatic
        fun newInstance(courseName: String, courseId: String, levelName: String, levelId: String) =
            AdminELearningFragment().apply {
                arguments = Bundle().apply {
                    putString(COURSE_NAME, courseName)
                    putString(COURSE_ID, courseId)
                    putString(LEVEL_NAME, levelName)
                    putString(LEVEL_ID, levelId)
                }
            }


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.apply {
            val toolbar: Toolbar =  findViewById(R.id.toolbar)
            val postLayout:RelativeLayout = findViewById(R.id.notification_layout)

            toolbar.apply {
                title = "Stream"
                setNavigationIcon(R.drawable.arrow_left)
                setNavigationOnClickListener { requireActivity().onBackPressed() }
            }

            postLayout.setOnClickListener {
                startActivity(Intent(requireContext(), ELearningActivity::class.java)
                    .putExtra("from", "view_post"))
            }

        }

    }

}
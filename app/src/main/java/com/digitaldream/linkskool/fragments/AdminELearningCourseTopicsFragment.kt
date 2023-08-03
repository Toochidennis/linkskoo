package com.digitaldream.linkskool.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.dialog.AdminELearningCreateContentDialog
import com.digitaldream.linkskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.linkskool.utils.VolleyCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"


class AdminELearningCourseTopicsFragment :
    Fragment(R.layout.fragment_admin_e_learning_course_topics) {

    private lateinit var addContentButton: FloatingActionButton

    private var mLevelId: String? = null
    private var mCourseId: String? = null
    private var mCourseName: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mLevelId = it.getString(ARG_PARAM1)
            mCourseId = it.getString(ARG_PARAM2)
            mCourseName = it.getString(ARG_PARAM3)
        }
    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String, param3: String) =
            AdminELearningCourseTopicsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    putString(ARG_PARAM3, param3)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        addContent()

        getCourseOutline()
    }

    private fun setUpViews(view: View) {
        view.apply {
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            addContentButton = findViewById(R.id.add_btn)

            toolbar.apply {
                title = "Topics"
                setNavigationIcon(R.drawable.arrow_left)
                setNavigationOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
            }

        }
    }

    private fun addContent() {
        addContentButton.setOnClickListener {
            AdminELearningCreateContentDialog(
                requireContext(), mLevelId!!,
                mCourseId!!, mCourseName!!
            ).apply {
                setCancelable(true)
                show()
            }.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private fun getCourseOutline() {
        val term = requireActivity()
            .getSharedPreferences("loginDetail", Context.MODE_PRIVATE)
            .getString("term", "")
        println("term $term")

        val url = "${getString(R.string.base_url)}/getOutline.php?" +
                "course=$mCourseId&&level=$mLevelId&&term=$term"

        sendRequestToServer(
            Request.Method.GET,
            url,
            requireContext(),
            null,
            object : VolleyCallback {
                override fun onResponse(response: String) {

                }

                override fun onError(error: VolleyError) {

                }
            })


    }
}
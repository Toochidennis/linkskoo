package com.digitaldream.linkskool.fragments

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.activities.AdminELearningActivity
import com.digitaldream.linkskool.adapters.GenericAdapter
import com.digitaldream.linkskool.models.CourseOutlineModel
import com.digitaldream.linkskool.utils.FunctionUtils.capitaliseFirstLetter
import com.digitaldream.linkskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.linkskool.utils.VolleyCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import timber.log.Timber


private const val COURSE_NAME = "course_name"
private const val COURSE_ID = "course_id"
private const val LEVEL_NAME = "level_name"
private const val LEVEL_ID = "level_id"

class AdminELearningClassRoomFragment : Fragment(R.layout.fragment_admin_e_learning_class_room) {


    private lateinit var mAddCourseOutlineBtn: FloatingActionButton
    private lateinit var outlineRecyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var emptyTxt: TextView

    private lateinit var outlineAdapter: GenericAdapter<CourseOutlineModel>
    private var outlineList = mutableListOf<CourseOutlineModel>()

    private var mCourseName: String? = null
    private var mCourseId: String? = null
    private var mLevelName: String? = null
    private var mLevelId: String? = null
    private var mTerm: String? = null


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
            AdminELearningClassRoomFragment().apply {
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

        setUpViews(view)

        getCourseOutline()

        createCourseOutline()

        refresh()
    }

    private fun setUpViews(view: View) {
        view.apply {
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            outlineRecyclerView = findViewById(R.id.outlineRecyclerView)
            mAddCourseOutlineBtn = findViewById(R.id.addCourseLineButton)
            swipeRefreshLayout = findViewById(R.id.swipeRefresh)
            emptyTxt = findViewById(R.id.emptyTxt)

            (requireContext() as AppCompatActivity).setSupportActionBar(toolbar)
            val actionBar = (requireContext() as AppCompatActivity).supportActionBar

            actionBar?.apply {
                title = "Classroom"
                setHomeButtonEnabled(true)
                setDisplayHomeAsUpEnabled(true)
            }

            toolbar.apply {
                setNavigationOnClickListener { requireActivity().onBackPressed() }
            }

        }

        mTerm =
            requireActivity().getSharedPreferences("loginDetail", MODE_PRIVATE)
                .getString("term", "")
    }

    private fun getCourseOutline() {
        val url = "${requireContext().getString(R.string.base_url)}/getOutlineList" +
                ".php?course=$mCourseId&level=$mLevelId&term=$mTerm"

        sendRequestToServer(Request.Method.GET, url, requireContext(), null,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    if (response != "[]") {
                        parseResponseJson(response)

                        hideEmptyTxt()
                    } else {
                        showEmptyTxt("Outline not created yet. Use the button below to start")
                    }
                }

                override fun onError(error: VolleyError) {
                    showEmptyTxt("Something went wrong, please try again")
                }
            })
    }

    private fun showEmptyTxt(message: String) {
        emptyTxt.isVisible = true
        emptyTxt.text = message
    }

    private fun hideEmptyTxt() {
        emptyTxt.isVisible = false
    }

    private fun parseResponseJson(response: String) {
        try {
            outlineList.clear()

            with(JSONArray(response)) {
                for (i in 0 until length()) {
                    getJSONObject(i).let {
                        val id = it.getString("id")
                        val title = it.getString("title")
                        val description = it.getString("body")
                        val courseId = it.getString("course_id")
                        val levelId = it.getString("level")
                        val teacherName = it.getString("author_name")
                        val term = it.getString("term")

                        val outlineModel = CourseOutlineModel(
                            id,
                            title,
                            description,
                            courseId,
                            levelId,
                            teacherName,
                            term
                        )

                        outlineList.add(outlineModel)
                    }
                }
            }

            setUpOutlineAdapter()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpOutlineAdapter() {
        outlineAdapter = GenericAdapter(
            outlineList,
            R.layout.item_course_outline_layout,
            bindItem = { itemView, model, _ ->
                val courseName: TextView = itemView.findViewById(R.id.courseNameTxt)
                val levelName: TextView = itemView.findViewById(R.id.levelNameTxt)
                val teacherName: TextView = itemView.findViewById(R.id.teacherNameTxt)

                courseName.text = capitaliseFirstLetter(model.title)
                teacherName.text = capitaliseFirstLetter(model.teacherName)
            }
        ) {
            startActivity(
                Intent(requireContext(), AdminELearningActivity::class.java)
                    .putExtra("from", "view_post")
                    .putExtra("levelId", mLevelId)
                    .putExtra("courseId", mCourseId)
                    .putExtra("courseName", mCourseName)
            )
        }

        setUpRecyclerView()
    }


    private fun setUpRecyclerView() {
        outlineRecyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(requireContext())
            adapter = outlineAdapter
        }
    }

    private fun createCourseOutline() {
        mAddCourseOutlineBtn.setOnClickListener {
            AdminELearningCreateClassDialogFragment(
                mLevelId ?: "",
                mCourseName ?: "",
                mCourseId ?: ""
            ) {
                getCourseOutline()
            }.show(parentFragmentManager, "")
        }
    }

    private fun refresh() {
        swipeRefreshLayout.apply {
            setColorSchemeResources(R.color.test_color_1)
            setOnRefreshListener {
                getCourseOutline()
                isRefreshing = false
            }

        }

    }

}

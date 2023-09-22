package com.digitaldream.linkskool.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.GenericAdapter
import com.digitaldream.linkskool.adapters.StudentELearningAdapter
import com.digitaldream.linkskool.models.CourseOutlineModel
import com.digitaldream.linkskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.linkskool.utils.VolleyCallback
import org.json.JSONArray
import java.util.Locale

class StudentELearningCourseOutlineActivity :
    AppCompatActivity(R.layout.activity_student_e_learning_course_outline) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var emptyStateTxt: TextView

    private var courseOutlineList = mutableListOf<CourseOutlineModel>()

    private var levelId: String? = null
    private var courseId: String? = null
    private var courseName: String? = null
    private var term: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpViews()

        loadCourseOutline()

    }

    private fun setUpViews() {
        recyclerView = findViewById(R.id.outlineRecyclerView)
        val mToolbar = findViewById<Toolbar>(R.id.toolbar)
        emptyStateTxt = findViewById(R.id.emptyStateTxt)
        swipeRefreshLayout = findViewById(R.id.swipeRefresh)

        val sharedPreferences = getSharedPreferences(
            "loginDetail", MODE_PRIVATE
        )
        levelId = sharedPreferences.getString("level", "")
        term = sharedPreferences.getString("term", "")
        courseName = intent.getStringExtra("courseName")
        courseId = intent.getStringExtra("courseId")

        setSupportActionBar(mToolbar)
        supportActionBar?.apply {
            title = "$courseName outline"
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun loadCourseOutline() {
        val url = String.format(
            Locale.getDefault(),
            "%s/getOutlineList.php?level=%s&course=%s&term=%s",
            getString(R.string.base_url), levelId, courseId, term
        )

        sendRequestToServer(
            Request.Method.GET,
            url,
            this,
            null,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    if (response != "[]") {
                        emptyStateTxt.visibility = View.INVISIBLE
                        parseResponse(response)
                    } else {
                        emptyStateTxt.visibility = View.VISIBLE
                    }
                }

                override fun onError(error: VolleyError) {
                    emptyStateTxt.visibility = View.VISIBLE
                }
            }
        )
    }

    private fun parseResponse(response: String) {
        try {
            with(JSONArray(response)) {
                for (i in 0 until length()) {
                    val contentObject = getJSONObject(i)

                    contentObject.let {
                        val id = it.getString("id")
                        val title = it.getString("title")
                        val description = it.getString("body")
                        val courseId = it.getString("course_id")
                        val levelId = it.getString("level")
                        val term = it.getString("term")

                        val outline = CourseOutlineModel(
                            id, title, description, courseId,
                            levelId, courseName ?: "",
                            term
                        )

                        courseOutlineList.add(outline)
                    }
                }

                setUpRecyclerView()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpRecyclerView() {
        val outlineAdapter = GenericAdapter(
            courseOutlineList,
            R.layout.item_course_outline_layout,
            bindItem = { itemView, model, _ ->
                val titleTxt: TextView = itemView.findViewById(R.id.titleTxt)
                val descriptionTxt: TextView = itemView.findViewById(R.id.descriptionTxt)

                titleTxt.text = model.title
                descriptionTxt.text = model.description

            }
        ) { position ->
            val outlineModel = courseOutlineList[position]

            getSharedPreferences("loginDetail", MODE_PRIVATE).edit().apply {
                putString("course_name", courseName)
                putString("courseId", courseId)
                putString("outline_title", outlineModel.title)
            }.apply()

            startActivity(
                Intent(
                    this@StudentELearningCourseOutlineActivity,
                    StudentELearningActivity::class.java
                ).putExtra("from", "dashboard")
            )

        }

        recyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(this@StudentELearningCourseOutlineActivity)
            adapter = outlineAdapter
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return false
    }

}
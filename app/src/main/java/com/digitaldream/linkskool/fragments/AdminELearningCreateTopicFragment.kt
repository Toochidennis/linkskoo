package com.digitaldream.linkskool.fragments

import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.AdminELearningQuestionSettingsAdapter
import com.digitaldream.linkskool.config.DatabaseHelper
import com.digitaldream.linkskool.models.ClassNameTable
import com.digitaldream.linkskool.models.TagModel
import com.digitaldream.linkskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.linkskool.utils.VolleyCallback
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import org.json.JSONArray
import org.json.JSONObject

private const val ARG_PARAM1 = "1"
private const val ARG_PARAM2 = "2"
private const val ARG_PARAM3 = "3"
private const val ARG_PARAM4 = "4"

class AdminELearningCreateTopicFragment :
    Fragment(R.layout.fragment_admin_e_learning_create_topic) {

    private lateinit var backBtn: ImageButton
    private lateinit var topicEditText: EditText
    private lateinit var classRecyclerView: RecyclerView
    private lateinit var selectAllBtn: Button
    private lateinit var emptyClassTxt: TextView
    private lateinit var objectivesEditText: EditText
    private lateinit var createTopicBtn: Button

    private var mClassList = mutableListOf<ClassNameTable>()
    private val selectedClassItems = hashMapOf<String, String>()
    private val mTagList = mutableListOf<TagModel>()
    private var newHashMap = mutableMapOf<Any?, Any?>()

    private var courseId: String? = null
    private var levelId: String? = null
    private var courseName: String? = null
    private var existingTopic: String? = null
    private var year: String? = null
    private var term: String? = null
    private var userId: String? = null
    private var userName: String? = null

    private var topic: String? = null
    private var objectives: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            courseId = it.getString(ARG_PARAM1)
            levelId = it.getString(ARG_PARAM2)
            courseName = it.getString(ARG_PARAM3)
            existingTopic = it.getString(ARG_PARAM4)
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(
            courseId: String = "",
            levelId: String = "",
            courseName: String = "",
            existingTopic: String = ""
        ) = AdminELearningCreateTopicFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, courseId)
                    putString(ARG_PARAM2, levelId)
                    putString(ARG_PARAM3, courseName)
                    putString(ARG_PARAM4, existingTopic)
                }
            }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpView(view)

        setUpClassAdapter()

        val sharedPreferences =
            requireActivity().getSharedPreferences("loginDetail", Context.MODE_PRIVATE)
        year = sharedPreferences.getString("school_year", "")
        term = sharedPreferences.getString("term", "")
        userId = sharedPreferences.getString("user_id", "")
        userName = sharedPreferences.getString("user", "")

        createTopicBtn.setOnClickListener {
            verifyTopic()
        }

        backBtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

    }

    private fun setUpView(view: View) {
        view.apply {
            backBtn = findViewById(R.id.backBtn)
            topicEditText = findViewById(R.id.topicEditText)
            classRecyclerView = findViewById(R.id.classRecyclerView)
            selectAllBtn = findViewById(R.id.selectAllBtn)
            emptyClassTxt = findViewById(R.id.emptyClassTxt)
            objectivesEditText = findViewById(R.id.objectivesEditText)
            createTopicBtn = findViewById(R.id.createTopicButton)
        }
    }

    private fun setExistingTopic(){

    }

    private fun setUpClassAdapter() {
        try {
            val mDatabaseHelper = DatabaseHelper(requireContext())
            val dao: Dao<ClassNameTable, Long> = DaoManager.createDao(
                mDatabaseHelper.connectionSource, ClassNameTable::class.java
            )
            mClassList = dao.queryBuilder().where().eq("level", levelId).query()
            mClassList.sortBy { it.className }

            mClassList.forEach { item ->
                mTagList.add(TagModel(item.classId, item.className))
            }

            if (selectedClassItems.isNotEmpty()) {
                mTagList.forEach { tagModel ->
                    if (selectedClassItems[tagModel.tagId] == tagModel.tagName)
                        tagModel.isSelected = true
                }
            }

            if (mTagList.isEmpty()) {
                classRecyclerView.isVisible = false
                selectAllBtn.isVisible = false
                emptyClassTxt.isVisible = true
            } else {
                AdminELearningQuestionSettingsAdapter(
                    selectedClassItems,
                    mTagList,
                    selectAllBtn
                ).let {
                    classRecyclerView.apply {
                        hasFixedSize()
                        layoutManager = LinearLayoutManager(
                            requireContext(),
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                        adapter = it
                        isVisible = true

                        selectAllBtn.isVisible = true
                        emptyClassTxt.isVisible = false
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun verifyTopic() {
        topic = topicEditText.text.toString().trim()
        objectives = objectivesEditText.text.toString().trim()

        if (topic.isNullOrBlank()) {
            topicEditText.error = "Please provide a topic"
        } else if (objectives.isNullOrBlank()) {
            objectivesEditText.error = "Please provide objectives"
        } else if (selectedClassItems.size == 0) {
            Toast.makeText(requireContext(), "Please select a class", Toast.LENGTH_SHORT).show()
        } else {
            postTopic()
        }
    }

    private fun prepareTopic(): HashMap<String, String> {
        val classArray = JSONArray()

        return HashMap<String, String>().apply {
            put("title", topic!!)
            put("type", "4")
            put("description", "")
            put("topic", "")
            put("objective", objectives!!)
            put("files", "")

            selectedClassItems.forEach { (key, value) ->
                if (key.isNotEmpty() and value.isNotEmpty()) {
                    JSONObject().apply {
                        put("id", key)
                        put("name", value)
                    }.let {
                        classArray.put(it)
                    }
                }
            }

            put("class", classArray.toString())
            put("level", levelId!!)
            put("course", courseId!!)
            put("course_name", courseName!!)
            put("start_date", "")
            put("end_date", "")
            put("grade", "")
            put("author_id", userId!!)
            put("author_name", userName!!)
            put("year", year!!)
            put("term", term!!)
        }
    }

    private fun postTopic() {
        val url = "${getString(R.string.base_url)}/addContent.php"
        val hashMap = prepareTopic()

        sendRequestToServer(
            Request.Method.POST,
            url,
            requireContext(),
            hashMap,
            object
                : VolleyCallback {
                override fun onResponse(response: String) {
                    Toast.makeText(
                        requireContext(), "Topic submitted successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    SystemClock.sleep(1000)
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }

                override fun onError(error: VolleyError) {
                    Toast.makeText(
                        requireContext(), "Something went wrong please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}
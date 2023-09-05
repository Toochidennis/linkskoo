package com.digitaldream.linkskool.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.utils.FunctionUtils.formatDate2
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONArray
import org.json.JSONObject


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AdminELearningAssignmentInstructionsFragment :
    Fragment(R.layout.fragment_admin_e_learning_assignment_instructions) {

    // Define UI elements
    private lateinit var dueDateTxt: TextView
    private lateinit var titleTxt: TextView
    private lateinit var gradeTxt: TextView
    private lateinit var descriptionTxt: TextView
    private lateinit var attachmentTxt: TextView
    private lateinit var attachmentRecyclerView: RecyclerView
    private lateinit var commentRecyclerView: RecyclerView
    private lateinit var addCommentTxt: TextView
    private lateinit var addCommentInput: TextInputLayout

    // Variables to store data
    private var jsonData: String? = null
    private var taskType: String? = null

    private var title: String? = null
    private var dueDate: String? = null
    private var description: String? = null
    private var grade: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            jsonData = it.getString(ARG_PARAM1)
            taskType = it.getString(ARG_PARAM2)
        }
    }


    companion object {

        @JvmStatic
        fun newInstance(jsonData: String, taskType: String = "") =
            AdminELearningAssignmentInstructionsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, jsonData)
                    putString(ARG_PARAM2, taskType)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        parseJsonObject(jsonData!!)
    }

    private fun setUpViews(view: View) {
        view.apply {
            dueDateTxt = findViewById(R.id.assignmentDueDateTxt)
            titleTxt = findViewById(R.id.assignmentTitleTxt)
            gradeTxt = findViewById(R.id.assignmentGradeTxt)
            descriptionTxt = findViewById(R.id.assignmentDescriptionTxt)
            attachmentTxt = findViewById(R.id.attachmentTxt)
            attachmentRecyclerView = findViewById(R.id.attachmentRecyclerView)
            commentRecyclerView = findViewById(R.id.commentRecyclerView)
            addCommentTxt = findViewById(R.id.addCommentTxt)
            addCommentInput = findViewById(R.id.commentEditText)
        }
    }

    private fun parseJsonObject(json: String) {
        try {
            JSONObject(json).let {
                //    put("id", it.getString("id"))
                title = it.getString("title")
                grade = "${it.getString("objective")} points"
                description = it.getString("description")
                dueDate = "Due ${formatDate2(it.getString("end_date"), "custom1")}"

//                put("type", it.getString("type"))
//                put("topic", it.getString("category"))
//                put("topic_id", it.getString("parent"))
//                put("files", parseFilesArray(JSONArray(it.getString("picref"))))
//                put("class", parseClassArray(JSONArray(it.getString("class"))))
//                put("level", it.getString("level"))
//                put("course", it.getString("course_id"))
//                put("course_name", it.getString("course_name"))
//                put("start_date", it.getString("start_date"))
//                put("author_id", it.getString("author_id"))
//                put("author_name", it.getString("author_name"))
//                put("year", it.getString("term"))
//                put("term", it.getString("term"))
            }

            setDataOnFields()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setDataOnFields() {
        dueDateTxt.text = dueDate
        titleTxt.text = title
        gradeTxt.text = grade
        descriptionTxt.text = description
    }

    private fun parseFilesArray(files: JSONArray): JSONArray {
        return JSONArray().apply {
            JSONObject().apply {
                files.getJSONObject(0).let {
                    put("file_name", trimText(it.getString("file_name")))
                    put("old_file_name", trimText(it.getString("file_name")))
                    put("type", it.getString("type"))
                    put("file", it.getString("file_name"))
                }
            }.let { jsonObject ->
                put(jsonObject)
            }
        }
    }

    private fun trimText(text: String): String {
        return text.replace("../assets/elearning/practice/", "").ifEmpty { "" }
    }

    private fun parseClassArray(classArray: JSONArray): JSONArray {
        return JSONArray().apply {
            for (i in 0 until classArray.length()) {
                classArray.getJSONObject(i).let {
                    JSONObject().apply {
                        put("id", it.getString("id"))
                        put("name", it.getString("name"))
                    }.let { jsonObject ->
                        put(jsonObject)
                    }
                }
            }
        }
    }
}
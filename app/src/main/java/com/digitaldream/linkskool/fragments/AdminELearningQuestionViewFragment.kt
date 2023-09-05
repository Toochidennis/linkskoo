package com.digitaldream.linkskool.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONArray
import org.json.JSONObject


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AdminELearningQuestionViewFragment :
    Fragment(R.layout.fragment_admin_e_learning_question_view) {

    // Define UI elements
    private lateinit var dueDateTxt: TextView
    private lateinit var titleTxt: TextView
    private lateinit var durationTxt: TextView
    private lateinit var viewQuestionBtn: Button
    private lateinit var commentRecyclerView: RecyclerView
    private lateinit var commentTxt: TextView
    private lateinit var commentInput: TextInputLayout

    // Variables to store data
    private var jsonData: String? = null
    private var taskType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            jsonData = it.getString(ARG_PARAM1)
            taskType = it.getString(ARG_PARAM2)
        }
    }


    companion object {

        @JvmStatic
        fun newInstance(jsonData: String, taskType: String) =
            AdminELearningQuestionViewFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, jsonData)
                    putString(ARG_PARAM2, taskType)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

    }

    private fun setUpViews(view: View) {
        view.apply {
            dueDateTxt = findViewById(R.id.questionDueDateTxt)
            titleTxt = findViewById(R.id.questionTitleTxt)
            durationTxt = findViewById(R.id.questionDurationTxt)
            viewQuestionBtn = findViewById(R.id.viewQuestionsButton)
            commentRecyclerView = findViewById(R.id.commentRecyclerView)
            commentTxt = findViewById(R.id.addCommentTxt)
            commentInput = findViewById(R.id.commentEditText)
        }
    }


    // Parse the JSON data from a given JSON string
    private fun parseJsonObject(json: String):JSONObject {
       return JSONObject().apply {
            JSONObject(json).let { jsonObject ->
                put("settings", parseSettingsJson(JSONObject(jsonObject.getString("e"))))
                put("questions", parseQuestionJson(JSONArray(jsonObject.getString("q"))))
            }
        }
    }


    // Parse the settings JSON object
    private fun parseSettingsJson(settings: JSONObject): JSONObject {
        return JSONObject().apply {
            settings.let {
                put("id", it.getString("id"))
                put("author_id", it.getString("author_id"))
                put("author_name", it.getString("author_name"))
                put("title", it.getString("title"))
                put("description", it.getString("description"))
                put("duration", it.getString("objective"))
                put("level", it.getString("level"))
                put("class", parseClassArray(JSONArray(it.getString("class"))))
                put("course", it.getString("course_id"))
                put("course_name", it.getString("course_name"))
                put("topic", it.getString("topic"))
                put("topic_id", it.getString("topic_id"))
                put("start_date", it.getString("start_date"))
                put("end_date", it.getString("end_date"))
                put("term", it.getString("term"))
            }
        }
    }


    // Parse the class JSON array
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


    // Parse the question JSON array
    private fun parseQuestionJson(jsonArray: JSONArray): JSONArray {
        return JSONArray().apply {
            jsonArray.getJSONArray(0).let { question ->
                for (i in 0 until question.length()) {
                    JSONObject().apply {
                        question.getJSONObject(i).let {
                            put("question_id", it.getString("id"))
                            put("question_title", it.getString("content"))
                            put("question_type", it.getString("type"))
                            put(
                                "question_files", parseFilesArray(
                                    JSONArray(it.getString("question_file"))
                                )
                            )

                            if (it.getString("answer") != "null") {
                                put(
                                    "options",
                                    parseOptionsJson(JSONArray(it.getString("answer")))
                                )
                            }

                            if (it.getString("correct") != "null")
                                put("correct", JSONObject(it.getString("correct")))
                        }
                    }.let {
                        put(it)
                    }
                }
            }
        }
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


    // Remove a specific text from the file name
    private fun trimText(text: String): String {
        return text.replace("../assets/elearning/practice/", "").ifEmpty { "" }
    }


    // Parse the options JSON array
    private fun parseOptionsJson(jsonArray: JSONArray): JSONArray {
        return JSONArray().apply {
            for (i in 0 until jsonArray.length()) {
                JSONObject().apply {
                    jsonArray.getJSONObject(i).let {
                        put("order", it.getString("order"))
                        put("text", it.getString("text"))
                        put(
                            "option_files",
                            parseFilesArray(JSONArray(it.getString("option_files")))
                        )
                    }
                }.let {
                    put(it)
                }
            }
        }
    }


}
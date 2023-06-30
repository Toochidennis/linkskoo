package com.digitaldream.linkskool.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.AdminELearningQuestionAdapter
import com.digitaldream.linkskool.dialog.AdminELearningQuestionDialog
import com.digitaldream.linkskool.models.GroupItem
import com.digitaldream.linkskool.models.MultiChoiceQuestion
import com.digitaldream.linkskool.models.QuestionItem
import com.digitaldream.linkskool.models.ShortAnswerModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception


private const val ARG_PARAM1 = "param1"


class AdminELearningQuestionFragment : Fragment(R.layout.fragment_admin_e_learning_question) {


    private lateinit var topicButton: RelativeLayout
    private lateinit var questionTitleTxt: TextView
    private lateinit var descriptionTxt: TextView
    private lateinit var questionRecyclerView: RecyclerView
    private lateinit var emptyQuestionTxt: TextView
    private lateinit var previewQuestionButton: LinearLayout
    private lateinit var submitQuestionButton: LinearLayout
    private lateinit var addQuestionButton: FloatingActionButton

    private lateinit var questionAdapter: AdminELearningQuestionAdapter
    private var groupItems: MutableList<GroupItem<String, QuestionItem?>> = mutableListOf()
    private val selectedClassId = hashMapOf<String, String>()

    private var jsonFromQuestionSettings: String? = null
    private var questionTitle: String? = null
    private var levelId: String? = null
    private var courseId: String? = null
    private var questionDescription: String? = null
    private var startDate: String? = null
    private var startTime: String? = null
    private var endDate: String? = null
    private var endTime: String? = null
    private var questionTopic: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            jsonFromQuestionSettings = it.getString(ARG_PARAM1)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String) =
            AdminELearningQuestionFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.apply {
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            topicButton = findViewById(R.id.topicButton)
            questionTitleTxt = findViewById(R.id.questionTitleTxt)
            descriptionTxt = findViewById(R.id.descriptionTxt)
            questionRecyclerView = findViewById(R.id.questionRecyclerView)
            emptyQuestionTxt = findViewById(R.id.emptyQuestionTxt)
            previewQuestionButton = findViewById(R.id.previewQuestionButton)
            submitQuestionButton = findViewById(R.id.submitQuestionButton)
            addQuestionButton = findViewById(R.id.add_question_btn)

            toolbar.apply {
                title = "Question"
                setNavigationIcon(R.drawable.arrow_left)
                setNavigationOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
            }
        }

        fromQuestionSettings()

        setupQuestionRecyclerView()

        addQuestionButton.setOnClickListener {
            addQuestion()
        }

        topicButton.setOnClickListener {
            toQuestionSettings()
        }
    }

    private fun addQuestion() {
        AdminELearningQuestionDialog(
            requireContext(),
            parentFragmentManager,
            MultiChoiceQuestion(),
            ShortAnswerModel()
        ) { question: MultiChoiceQuestion?, shortQuestion: ShortAnswerModel?, sectionTitle: String? ->

            val questionModel = when {
                question != null -> QuestionItem.MultiChoice(question)
                shortQuestion != null -> QuestionItem.ShortAnswer(shortQuestion)
                else -> null
            }

            if (groupItems.isNotEmpty() && groupItems.last().title != null) {
                //Existing Section
                groupItems.last().itemList.add(questionModel)
            } else if (groupItems.isNotEmpty() && groupItems.last().itemList.isNotEmpty() &&
                questionModel != null
            ) {
                //Existing question
                groupItems.last().itemList.add(questionModel)
            } else if (questionModel != null) {
                // New question without a section
                val newQuestionToAdd =
                    GroupItem(sectionTitle, mutableListOf<QuestionItem?>(questionModel))
                groupItems.add(newQuestionToAdd)
            } else {
                //New section without a question
                val newSectionItem = GroupItem(sectionTitle, mutableListOf<QuestionItem?>())
                groupItems.add(newSectionItem)
            }
            questionAdapter.notifyDataSetChanged()

        }.apply {
            setCancelable(true)
            show()
        }.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun setupQuestionRecyclerView() {
        questionAdapter = AdminELearningQuestionAdapter(requireContext(), groupItems)
        questionRecyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(requireContext())
            adapter = questionAdapter
        }
    }


    private fun fromQuestionSettings() {
        try {
            if (!jsonFromQuestionSettings.isNullOrEmpty()) {
                jsonFromQuestionSettings?.let {
                    JSONObject(it).run {
                        val settingsObject = getJSONObject("settings")
                        val classArray = getJSONArray("class")

                        questionTitle = settingsObject.getString("title")
                        questionDescription = settingsObject.getString("description")
                        startDate = settingsObject.getString("startDate")
                        endDate = settingsObject.getString("endDate")
                        startTime = settingsObject.getString("startTime")
                        endTime = settingsObject.getString("endTime")
                        questionTopic = settingsObject.getString("topic")
                        levelId = settingsObject.getString("levelId")
                        courseId = settingsObject.getString("courseId")

                        questionTitleTxt.text = questionTitle
                        descriptionTxt.text = questionDescription

                        for (i in 0 until classArray.length()) {
                            selectedClassId[classArray.getJSONObject(i).getString("id")] =
                                classArray.getJSONObject(i).getString("name")
                        }
                    }
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun toQuestionSettings() {
        val jsonObject = JSONObject()
        val classArray = JSONArray()

        selectedClassId.forEach { (key, value) ->
            if (key.isNotEmpty() && value.isNotEmpty()) {
                JSONObject().apply {
                    put("id", key)
                    put("name", value)
                }.let {
                    classArray.put(it)
                }
            }
        }

        JSONObject().apply {
            put("title", questionTitle)
            put("description", questionDescription)
            put("startDate", startDate)
            put("endDate", endDate)
            put("startTime", startTime)
            put("endTime", endTime)
            put("topic", questionTopic)
        }.let {
            jsonObject.put("settings", it)
            jsonObject.put("class", classArray)
        }

        requireActivity().supportFragmentManager.commit {
            replace(
                R.id.learning_container, AdminELearningQuestionSettingsFragment
                    .newInstance(levelId!!, "", jsonObject.toString())
            )
        }

    }

}

package com.digitaldream.linkskool.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.AdminQuestionAdapter
import com.digitaldream.linkskool.dialog.AdminELearningQuestionDialog
import com.digitaldream.linkskool.dialog.AdminELearningQuestionPreviewDialogFragment
import com.digitaldream.linkskool.utils.ItemTouchHelperCallback
import com.digitaldream.linkskool.models.MultiChoiceQuestion
import com.digitaldream.linkskool.models.QuestionItem
import com.digitaldream.linkskool.models.SectionModel
import com.digitaldream.linkskool.models.ShortAnswerModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import org.json.JSONObject


private const val ARG_PARAM1 = "param1"

class AdminELearningQuestionFragment : Fragment(R.layout.fragment_admin_e_learning_question) {


    private lateinit var topicButton: RelativeLayout
    private lateinit var questionTitleTxt: TextView
    private lateinit var descriptionTxt: TextView
    private lateinit var sectionRecyclerView: RecyclerView
    private lateinit var emptyQuestionTxt: TextView
    private lateinit var previewQuestionButton: LinearLayout
    private lateinit var submitQuestionButton: LinearLayout
    private lateinit var addQuestionButton: FloatingActionButton

    private lateinit var sectionAdapter: AdminQuestionAdapter
    private var sectionItems = mutableListOf<SectionModel>()
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
            sectionRecyclerView = findViewById(R.id.questionRecyclerView)
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

        previewQuestions()

        val sectionItemTouchHelperCallback = ItemTouchHelperCallback(sectionAdapter)
        val sectionItemTouchHelper = ItemTouchHelper(sectionItemTouchHelperCallback)
        sectionItemTouchHelper.attachToRecyclerView(sectionRecyclerView)
    }

    private fun addQuestion() {
        AdminELearningQuestionDialog(
            requireContext(),
            parentFragmentManager,
            MultiChoiceQuestion(),
            ShortAnswerModel()
        ) { question: MultiChoiceQuestion?, shortQuestion: ShortAnswerModel?, sectionTitle: String? ->

            val questionItem = when {
                question != null -> SectionModel(
                    sectionTitle, QuestionItem.MultiChoice(question), "option"
                )

                shortQuestion != null -> SectionModel(
                    sectionTitle, QuestionItem.ShortAnswer(shortQuestion), "short"
                )

                else -> null
            }

            if (sectionTitle.isNullOrEmpty()){
                questionItem?.let {
                    sectionItems.add(it)
                }
            }else{
                val newSection = SectionModel(sectionTitle, null, "section")
                sectionItems.add(newSection)
            }

            sectionAdapter.notifyDataSetChanged()

        }.apply {
            setCancelable(true)
            show()
        }.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun setupQuestionRecyclerView() {
        sectionAdapter =
            AdminQuestionAdapter(parentFragmentManager, sectionItems)

        sectionRecyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(requireContext())
            adapter = sectionAdapter
//            smoothScrollToPosition(groupItems.size - 1)
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

    private fun previewQuestions() {
        previewQuestionButton.setOnClickListener {
            if (sectionItems.isNotEmpty()) {
                AdminELearningQuestionPreviewDialogFragment.newInstance(sectionItems)
                    .show(parentFragmentManager, "")
            } else {
                Toast.makeText(requireContext(), "kkk", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //  api 'com.github.tcking:giraffeplayer2:0.1.25-lazyLoad'
}






package com.digitaldream.linkskool.fragments

import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.AdminQuestionAdapter
import com.digitaldream.linkskool.dialog.AdminELearningQuestionDialog
import com.digitaldream.linkskool.dialog.AdminELearningQuestionPreviewDialogFragment
import com.digitaldream.linkskool.utils.ItemTouchHelperCallback
import com.digitaldream.linkskool.models.MultiChoiceQuestion
import com.digitaldream.linkskool.models.MultipleChoiceOption
import com.digitaldream.linkskool.models.QuestionItem
import com.digitaldream.linkskool.models.SectionModel
import com.digitaldream.linkskool.models.ShortAnswerModel
import com.digitaldream.linkskool.utils.FunctionUtils.compareJsonObjects
import com.digitaldream.linkskool.utils.FunctionUtils.convertUriOrFileToBase64
import com.digitaldream.linkskool.utils.FunctionUtils.sendRequesToServer
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import org.json.JSONObject


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

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
    private var selectedClassArray = JSONArray()

    private var jsonFromQuestionSettings: String? = null
    private var questionTitle: String? = null
    private var levelId: String? = null
    private var courseId: String? = null
    private var courseName: String? = null
    private var questionDescription: String? = null
    private var startDate: String? = null
    private var endDate: String? = null
    private var questionTopic: String? = null
    private var year: String? = null
    private var term: String? = null
    private var userId: String? = null
    private var userName: String? = null
    private var from: String? = null

    private var questionObject: String? = null
    private var newAssessmentObject = JSONObject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            jsonFromQuestionSettings = it.getString(ARG_PARAM1)
            from = it.getString(ARG_PARAM2)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String = "", param2: String = "") =
            AdminELearningQuestionFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
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
                setNavigationOnClickListener { onExit() }
            }
        }

        val sharedPreferences =
            requireActivity().getSharedPreferences("loginDetail", Context.MODE_PRIVATE)
        year = sharedPreferences.getString("school_year", "")
        term = sharedPreferences.getString("term", "")
        userId = sharedPreferences.getString("user_id", "")
        userName = sharedPreferences.getString("user", "")
        questionObject = sharedPreferences.getString("question_object", "")

        fromQuestionSettings()

        setupQuestionRecyclerView()

        addQuestionButton.setOnClickListener {
            addQuestion()
        }

        topicButton.setOnClickListener {
            toQuestionSettings()

        }

        previewQuestions()

        submitQuestionButton.setOnClickListener {
            submitQuestions()
        }

        onTouchHelper()
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
                    sectionTitle, QuestionItem.MultiChoice(question), "multiple_choice"
                )

                shortQuestion != null -> SectionModel(
                    sectionTitle, QuestionItem.ShortAnswer(shortQuestion), "short_answer"
                )

                else -> null
            }

            if (sectionTitle.isNullOrEmpty()) {
                questionItem?.let {
                    sectionItems.add(it)
                }
            } else {
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
            smoothScrollToPosition(if (sectionItems.isNotEmpty()) sectionItems.size - 1 else 0)
        }
    }

    private fun fromQuestionSettings() {
        try {
            if (from == "settings") {
                jsonFromQuestionSettings?.let {
                    JSONObject(it).run {
                        val settingsObject = getJSONObject("settings")
                        selectedClassArray = getJSONArray("class")

                        questionTitle = settingsObject.getString("title")
                        questionDescription = settingsObject.getString("description")
                        startDate = settingsObject.getString("startDate")
                        endDate = settingsObject.getString("endDate")
                        questionTopic = settingsObject.getString("topic")
                        levelId = settingsObject.getString("levelId")
                        courseId = settingsObject.getString("courseId")
                        courseName = settingsObject.getString("courseName")

                        questionTitleTxt.text = questionTitle
                        descriptionTxt.text = questionDescription

                        setQuestionsIfExist()
                    }
                }
            } else {
                setQuestionsIfExist()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun toQuestionSettings() {
        val jsonObject = JSONObject()

        JSONObject().apply {
            put("title", questionTitle)
            put("description", questionDescription)
            put("startDate", startDate)
            put("endDate", endDate)
            put("topic", questionTopic)
        }.let {
            jsonObject.put("settings", it)
            jsonObject.put("class", selectedClassArray)
        }

        prepareQuestions()

        parentFragmentManager.commit {
            replace(
                R.id.learning_container,
                AdminELearningQuestionSettingsFragment
                    .newInstance(
                        levelId!!, courseId!!,
                        jsonObject.toString(),
                        "edit",
                        courseName!!
                    )
            )
        }
    }

    private fun onTouchHelper() {
        val sectionItemTouchHelperCallback = ItemTouchHelperCallback(sectionAdapter)
        val sectionItemTouchHelper = ItemTouchHelper(sectionItemTouchHelperCallback)
        sectionItemTouchHelper.attachToRecyclerView(sectionRecyclerView)
    }

    private fun previewQuestions() {
        previewQuestionButton.setOnClickListener {
            if (sectionItems.isNotEmpty()) {
                AdminELearningQuestionPreviewDialogFragment.newInstance(sectionItems)
                    .show(parentFragmentManager, "")
            } else {
                Toast.makeText(
                    requireContext(), "There are no questions to preview", Toast
                        .LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setQuestionsIfExist() {
        if (!questionObject.isNullOrEmpty()) {
            JSONObject(questionObject!!).run {
                val settingsObject = getJSONObject("settings")
                if (from != "settings") {
                    settingsObject.let {
                        questionTitle = it.getString("title")
                        questionDescription = it.getString("description")
                        startDate = it.getString("start_date")
                        endDate = it.getString("end_date")
                        questionTopic = it.getString("topic")
                        levelId = it.getString("level")
                        courseId = it.getString("course")
                        courseName = it.getString("course_name")
                        selectedClassArray = it.getJSONArray("class")

                        questionTitleTxt.text = questionTitle
                        descriptionTxt.text = questionDescription
                    }
                }

                if (has("questions")) {
                    val questionsArray = getJSONArray("questions")

                    for (i in 0 until questionsArray.length()) {
                        val questionObject = questionsArray.getJSONObject(i)

                        questionObject.let {
                            val questionTitle = it.getString("question_title")
                            val questionFileName = it.getString("question_file_name")
                            val questionImage = it.getString("question_image")
                            val questionOldFileName = it.getString("question_old_file_name")

                            when (val questionType = it.getString("question_type")) {
                                "section" -> {
                                    val section =
                                        SectionModel(questionTitle, null, questionType)
                                    sectionItems.add(section)
                                }

                                "multiple_choice" -> {
                                    val optionsArray = it.getJSONArray("options")
                                    val optionsList = mutableListOf<MultipleChoiceOption>()

                                    for (j in 0 until optionsArray.length()) {
                                        val optionsObject = optionsArray.getJSONObject(j)

                                        optionsObject.let { option ->
                                            val order = option.getString("order")
                                            val text = option.getString("text")
                                            val fileName = option.getString("file_name")
                                            val oldFileName = option.getString("old_file_name")
                                            val image = option.getString("image")

                                            val optionModel = MultipleChoiceOption(
                                                text, order, fileName, image, oldFileName
                                            )

                                            optionsList.add(optionModel)
                                        }
                                    }

                                    it.getJSONObject("correct").let { answer ->
                                        val answerOrder = answer.getString("order")
                                        val correctAnswer = answer.getString("text")

                                        val multiChoiceQuestion = MultiChoiceQuestion(
                                            questionTitle,
                                            questionFileName,
                                            questionImage,
                                            questionOldFileName,
                                            optionsList,
                                            answerOrder.toInt(),
                                            correctAnswer
                                        )

                                        val questionSection = SectionModel(
                                            "",
                                            QuestionItem.MultiChoice(multiChoiceQuestion),
                                            questionType
                                        )

                                        sectionItems.add(questionSection)
                                    }

                                }

                                else -> {
                                    it.getJSONObject("correct").let { answer ->
                                        val correctAnswer = answer.getString("text")

                                        val shortAnswerModel = ShortAnswerModel(
                                            questionTitle, questionFileName, questionImage,
                                            questionOldFileName, correctAnswer
                                        )

                                        val questionSection = SectionModel(
                                            "", QuestionItem.ShortAnswer(shortAnswerModel),
                                            questionType
                                        )

                                        sectionItems.add(questionSection)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun prepareQuestions() {
        if (sectionItems.isNotEmpty()) {
            val questionArray = JSONArray()
            val assessmentObject = JSONObject()

            sectionItems.forEach { sectionModel ->
                if (!sectionModel.sectionTitle.isNullOrEmpty()) {
                    JSONObject().apply {
                        put("question_title", sectionModel.sectionTitle)
                        put("question_type", sectionModel.viewType)
                        put("question_old_file_name", "")
                        put("question_file_name", "")
                        put("question_image", "")
                    }.let {
                        questionArray.put(it)
                    }
                } else {
                    if (sectionModel.questionItem != null) {
                        val questionItem = sectionModel.questionItem

                        if (questionItem is QuestionItem.MultiChoice) {
                            val multiChoice = questionItem.question
                            val optionsList = multiChoice.options

                            JSONObject().apply {
                                put("question_title", multiChoice.questionText)
                                put("question_type", sectionModel.viewType)
                                put("question_file_name", multiChoice.attachmentName)

                                if (multiChoice.attachmentUri != null) {
                                    val image =
                                        convertUriOrFileToBase64(
                                            multiChoice.attachmentUri,
                                            requireContext()
                                        )
                                    put("question_image", image)

                                    if (multiChoice.attachmentName !=
                                        multiChoice.previousAttachmentName &&
                                        multiChoice.previousAttachmentName.isNotBlank()
                                    )
                                        put(
                                            "question_old_file_name",
                                            multiChoice.previousAttachmentName
                                        )
                                    else
                                        put("question_old_file_name", "")
                                } else {
                                    put("question_image", "")
                                    put("question_old_file_name", "")
                                }

                                val optionsArray = JSONArray()
                                optionsList?.forEach { option ->
                                    JSONObject().apply {
                                        put("order", option.optionOrder)
                                        put("text", option.optionText)
                                        put("file_name", option.attachmentName)

                                        if (option.attachmentUri != null) {
                                            val image =
                                                convertUriOrFileToBase64(
                                                    option.attachmentUri,
                                                    requireContext()
                                                )
                                            put("image", image)

                                            if (option.attachmentName !=
                                                option.previousAttachmentName &&
                                                option.previousAttachmentName.isNotBlank()
                                            ) {
                                                put(
                                                    "old_file_name",
                                                    option.previousAttachmentName
                                                )
                                            } else {
                                                put("old_file_name", "")
                                            }
                                        } else {
                                            put("image", "")
                                            put("old_file_name", "")
                                        }

                                    }.let {
                                        optionsArray.put(it)
                                    }
                                }

                                put("options", optionsArray)

                                JSONObject().apply {
                                    put("order", multiChoice.checkedPosition)
                                    put("text", multiChoice.correctAnswer)
                                }.let {
                                    put("correct", it)
                                }
                            }.let {
                                questionArray.put(it)
                            }
                        } else if (questionItem is QuestionItem.ShortAnswer) {
                            val shortAnswer = questionItem.question

                            JSONObject().apply {
                                put("question_title", shortAnswer.questionText)
                                put("question_type", sectionModel.viewType)
                                put("question_file_name", shortAnswer.attachmentName)

                                if (shortAnswer.attachmentUri != null) {
                                    val image = convertUriOrFileToBase64(
                                        shortAnswer.attachmentUri,
                                        requireContext()
                                    )
                                    put("question_image", image)

                                    if (shortAnswer.attachmentName !=
                                        shortAnswer.previousAttachmentName &&
                                        shortAnswer.previousAttachmentName.isNotBlank()
                                    ) {
                                        put(
                                            "question_old_file_name",
                                            shortAnswer.previousAttachmentName
                                        )
                                    } else {
                                        put("question_old_file_name", "")
                                    }
                                } else {
                                    put("question_image", "")
                                    put("question_old_file_name", "")
                                }

                                JSONObject().apply {
                                    put("order", "")
                                    put("text", shortAnswer.answerText)
                                }.let {
                                    put("correct", it)
                                }
                            }.let {
                                questionArray.put(it)
                            }
                        }
                    }
                }
            }

            val settingsObject = JSONObject().apply {
                put("author_id", userId)
                put("author_name", userName)
                put("title", questionTitle)
                put("description", questionDescription)
                put("level", levelId)
                put("class", selectedClassArray)
                put("course", courseId)
                put("course_name", courseName)
                put("topic", questionTopic)
                put("start_date", "$startDate")
                put("end_date", "$endDate")
                put("year", year)
                put("term", term)
            }

            assessmentObject.apply {
                put("settings", settingsObject)
                put("questions", questionArray)
            }

            println("assessment: $assessmentObject")

            newAssessmentObject = assessmentObject

            requireContext()
                .getSharedPreferences("loginDetail", Context.MODE_PRIVATE)
                .edit()
                .apply {
                    putString("question_object", assessmentObject.toString())
                }.apply()

        } else {
            Toast.makeText(
                requireContext(), "There are no questions to submit",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun submitQuestions() {
        prepareQuestions()

        val url = "${getString(R.string.base_url)}/addQuiz.php"
        val hashMap = HashMap<String, String>().apply {
            put("assessment", newAssessmentObject.toString())
        }

        if (newAssessmentObject.length() != 0) {
            sendRequesToServer(Request.Method.POST, url, requireContext(), hashMap)
            SystemClock.sleep(1000)
            onBackPressed()
        }
    }

    private fun onExit() {
        try {
            val json1 = JSONObject(questionObject ?: "")
            prepareQuestions()

            if (json1.length() != 0) {
                val areContentSame = compareJsonObjects(json1, newAssessmentObject)
                if (areContentSame) {
                    exitWarning()
                } else {
                    onBackPressed()
                }
            } else {
                onBackPressed()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun exitWarning() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Are you sure to exit?")
            setMessage("Your unsaved changes will be lost")
            setPositiveButton("Yes") { _, _ ->
                onBackPressed()
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }.create()
    }

    private fun onBackPressed() {
        requireContext()
            .getSharedPreferences("loginDetail", Context.MODE_PRIVATE).edit()
            .apply {
                putString("question_object", "")
            }.apply()

        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

}
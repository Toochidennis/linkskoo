package com.digitaldream.linkskool.dialog

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.AdminELearningQuestionPreviewAdapter
import com.digitaldream.linkskool.models.MultiChoiceQuestion
import com.digitaldream.linkskool.models.MultipleChoiceOption
import com.digitaldream.linkskool.models.QuestionItem
import com.digitaldream.linkskool.models.SectionModel
import com.digitaldream.linkskool.models.SharedViewModel
import com.digitaldream.linkskool.models.ShortAnswerModel
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable

private const val ARG_PARAM1 = "section"
private const val ARG_PARAM2 = "settings"

class AdminELearningQuestionPreviewFragment :
    Fragment(R.layout.fragment_admin_e_learning_question_preview) {

    private lateinit var dismissBtn: ImageButton
    private lateinit var timeTxt: TextView
    private lateinit var timeProgressBar: ProgressBar
    private lateinit var questionCountTxt: TextView
    private lateinit var questionTotalCountTxt: TextView
    private lateinit var sectionTxt: TextView
    private lateinit var questionRecyclerView: RecyclerView
    private lateinit var previousBtn: Button
    private lateinit var nextBtn: Button

    // Initialise section items
    private lateinit var sectionItems: MutableList<SectionModel>

    // Variables to store data
    private var currentSectionIndex: Int = 0
    private var jsonData: String? = null
    private var settingsData = JSONObject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            sectionItems = it.getSerializable(ARG_PARAM1) as MutableList<SectionModel>
            jsonData = it.getString(ARG_PARAM2)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(sectionData: MutableList<SectionModel>, jsonData: String = "") =
            AdminELearningQuestionPreviewFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, sectionData as Serializable)
                    putString(ARG_PARAM2, jsonData)
                }
            }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        initialiseSectionItem()

        previousBtn.setOnClickListener {
            showPreviousQuestion()
        }

        nextBtn.setOnClickListener {
            showNextQuestion()
        }
    }


    private fun setUpViews(view: View) {
        view.apply {
            dismissBtn = findViewById(R.id.dismissBtn)
            timeTxt = findViewById(R.id.timeTxt)
            timeProgressBar = findViewById(R.id.timeProgressBar)
            questionCountTxt = findViewById(R.id.questionCount)
            questionTotalCountTxt = findViewById(R.id.questionTotalCount)
            sectionTxt = findViewById(R.id.sectionTxt)
            questionRecyclerView = findViewById(R.id.questionRecyclerView)
            previousBtn = findViewById(R.id.prevBtn)
            nextBtn = findViewById(R.id.nextBtn)
        }
    }

    private fun initialiseSectionItem() {
        if (::sectionItems.isInitialized && sectionItems.isNotEmpty()) {
            showQuestion()
        } else {
            sectionItems = setJsonDataIfExist()

            openIntroDialog()
        }

        questionTotalCountTxt.text = getQuestionCount().toString()
    }


    private fun showQuestion() {
        val currentSection = sectionItems.getOrNull(currentSectionIndex)
        if (currentSection != null) {
            sectionTxt.text = currentSection.sectionTitle
            sectionTxt.isVisible = !currentSection.sectionTitle.isNullOrEmpty()

            val questionItem = currentSection.questionItem
            questionRecyclerView.isVisible = questionItem != null

            if (questionItem != null) {
                showQuestionPreview(questionItem)
            }
            updateNavigationButtons()

            var questionCount = "Question $currentSectionIndex"
            if (currentSection.sectionTitle.isNullOrEmpty()) {
                questionCountTxt.text = questionCount
            }else{
                questionCount = "Question ${currentSectionIndex-1}"
                questionCountTxt.text = questionCount
            }

        }
    }

    private fun updateNavigationButtons() =
        if (sectionItems.size == 1) {
            disableNextButton()
            disablePreviousButton()
        } else if (currentSectionIndex == 0) {
            enableNextButton()
            disablePreviousButton()
        } else if (currentSectionIndex == sectionItems.size - 1) {
            disableNextButton()
            enablePreviousButton()
        } else {
            enableNextButton()
            enablePreviousButton()
        }

    private fun showNextQuestion() {
        if (currentSectionIndex < sectionItems.size) {
            currentSectionIndex++
            showQuestion()
        }
    }

    private fun showPreviousQuestion() {
        if (currentSectionIndex > 0) {
            currentSectionIndex--
            showQuestion()
        }
    }

    private fun showQuestionPreview(nextQuestion: QuestionItem?) {
        AdminELearningQuestionPreviewAdapter(mutableListOf(nextQuestion)).let {
            questionRecyclerView.apply {
                hasFixedSize()
                layoutManager = LinearLayoutManager(requireContext())
                adapter = it
            }
        }
    }

    private fun disablePreviousButton() {
        // Disable the previous button
        previousBtn.isEnabled = false
    }

    private fun enablePreviousButton() {
        // Enable the previous button
        previousBtn.isEnabled = true
    }

    private fun disableNextButton() {
        // Disable the next button
        nextBtn.isEnabled = false
    }

    private fun enableNextButton() {
        // Enable the next button
        nextBtn.isEnabled = true
    }

    private fun getQuestionCount(): Int {
        var questionCount = 0
        sectionItems.forEach { sectionModel ->
            if (sectionModel.sectionTitle == null && sectionModel.questionItem != null) {
                questionCount++
            }
        }

        return questionCount
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

    // Parse the settings JSON object
    private fun parseSettingsJson(settings: JSONObject): JSONObject {
        return JSONObject().apply {
            settings.let {
                put("title", it.getString("title"))
                put("description", it.getString("description"))
                put("duration", it.getString("objective"))
                put("start_date", it.getString("start_date"))
                put("end_date", it.getString("end_date"))
            }
        }
    }

    // Parse the JSON data from a given JSON string
    private fun parseJsonObject(json: String): JSONObject {
        return JSONObject().apply {
            JSONObject(json).let { jsonObject ->
                put("settings", parseSettingsJson(JSONObject(jsonObject.getString("e"))))
                put("questions", parseQuestionJson(JSONArray(jsonObject.getString("q"))))
            }
        }
    }

    // return list of questions
    private fun setJsonDataIfExist(): MutableList<SectionModel> {
        val sectionItems = mutableListOf<SectionModel>()

        if (jsonData?.isNotBlank() == true) {
            val questionData = parseJsonObject(jsonData!!)

            questionData.run {
                if (has("questions")) {
                    settingsData = getJSONObject("settings")
                    val questionsArray = getJSONArray("questions")

                    for (i in 0 until questionsArray.length()) {
                        with(questionsArray.getJSONObject(i)) {
                            val questionId = getString("question_id")
                            val questionTitle = getString("question_title")
                            var questionFileName: String
                            var questionImage: String?
                            var questionOldFileName: String

                            getJSONArray("question_files").let { filesArray ->
                                filesArray.getJSONObject(0).let { filesObject ->
                                    questionFileName = filesObject.getString("file_name")
                                    questionOldFileName = filesObject.getString("old_file_name")
                                    questionImage =
                                        filesObject.getString("file").ifEmpty { null }
                                }
                            }

                            when (val questionType = getString("question_type")) {
                                // Handle different question types
                                "section" -> {
                                    val section =
                                        SectionModel(
                                            questionId,
                                            questionTitle,
                                            null,
                                            questionType
                                        )
                                    sectionItems.add(section)
                                }

                                "multiple_choice" -> {
                                    val optionsArray = getJSONArray("options")
                                    val optionsList = mutableListOf<MultipleChoiceOption>()

                                    for (j in 0 until optionsArray.length()) {
                                        optionsArray.getJSONObject(j).let { option ->
                                            val order = option.getString("order")
                                            val text = option.getString("text")
                                            val fileName: String
                                            val oldFileName: String
                                            val file: String?

                                            with(option.getJSONArray("option_files")) {
                                                getJSONObject(0).let { filesObject ->
                                                    fileName =
                                                        filesObject.getString("file_name")
                                                    oldFileName =
                                                        filesObject.getString("old_file_name")
                                                    file = filesObject.getString("file")
                                                        .ifEmpty { null }
                                                }
                                            }

                                            val optionModel =
                                                MultipleChoiceOption(
                                                    text,
                                                    order,
                                                    fileName,
                                                    file,
                                                    oldFileName
                                                )

                                            optionsList.add(optionModel)
                                        }
                                    }

                                    getJSONObject("correct").let { answer ->
                                        val answerOrder = answer.getString("order")
                                        val correctAnswer = answer.getString("text")

                                        val multiChoiceQuestion =
                                            MultiChoiceQuestion(
                                                questionId,
                                                questionTitle,
                                                questionFileName,
                                                questionImage,
                                                questionOldFileName,
                                                optionsList,
                                                answerOrder.toInt(),
                                                correctAnswer
                                            )

                                        val questionSection =
                                            SectionModel(
                                                questionId, "",
                                                QuestionItem.MultiChoice(
                                                    multiChoiceQuestion
                                                ),
                                                questionType
                                            )

                                        sectionItems.add(questionSection)
                                    }
                                }

                                else -> {
                                    getJSONObject("correct").let { answer ->
                                        val correctAnswer = answer.getString("text")

                                        val shortAnswerModel =
                                            ShortAnswerModel(
                                                questionId,
                                                questionTitle,
                                                questionFileName,
                                                questionImage,
                                                questionOldFileName,
                                                correctAnswer
                                            )

                                        val questionSection =
                                            SectionModel(
                                                questionId,
                                                "",
                                                QuestionItem.ShortAnswer(
                                                    shortAnswerModel
                                                ),
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


        return sectionItems
    }

    private fun openIntroDialog() {
        AdminELearningQuestionPreviewIntroDialog(
            requireContext(),
            jsonData = settingsData.toString()
        ) { status ->

            if (status == "start") {
                showQuestion()
            } else {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

        }.apply {
            show()
            setCancelable(false)
        }.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }


}

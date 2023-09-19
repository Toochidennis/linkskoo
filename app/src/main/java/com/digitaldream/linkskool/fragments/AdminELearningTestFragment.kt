package com.digitaldream.linkskool.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.AdminELearningTestAdapter
import com.digitaldream.linkskool.dialog.AdminELearningQuestionTestIntroDialogFragment
import com.digitaldream.linkskool.models.MultiChoiceQuestion
import com.digitaldream.linkskool.models.MultipleChoiceOption
import com.digitaldream.linkskool.models.QuestionItem
import com.digitaldream.linkskool.models.SectionModel
import com.digitaldream.linkskool.models.ShortAnswerModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.util.Locale


private const val ARG_PARAM1 = "param1"

class AdminELearningTestFragment :
    Fragment(R.layout.fragment_admin_e_learning_test),
    AdminELearningTestAdapter.UserResponse {

    private lateinit var dismissBtn: ImageButton
    private lateinit var countDownTxt: TextView
    private lateinit var questionCountTxt: TextView
    private lateinit var questionTotalCountTxt: TextView
    private lateinit var sectionTxt: TextView
    private lateinit var questionRecyclerView: RecyclerView
    private lateinit var previousBtn: Button
    private lateinit var nextBtn: Button
    private lateinit var submitQuestionBtn: Button

    // Initialise section items
    private lateinit var sectionItems: MutableList<SectionModel>
    private lateinit var countDownJob: Job
    private lateinit var questionTestAdapter: AdminELearningTestAdapter
    private var userResponses = mutableMapOf<String, String>()

    // Variables to store data
    private var currentSectionIndex: Int = 0
    private var currentQuestionCount = 0
    private var jsonData: String? = null
    private var duration: String? = null
    private var settingsData = JSONObject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            jsonData = it.getString(ARG_PARAM1)
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String) =
            AdminELearningTestFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        initialiseSectionItem()

        showPreviousQuestion()

        nextBtn.setOnClickListener {
            showNextQuestion()
        }

        submitQuestionBtn.setOnClickListener {
            submitTest()
        }

    }

    private fun setUpViews(view: View) {
        view.apply {
            dismissBtn = findViewById(R.id.dismissBtn)
            countDownTxt = findViewById(R.id.durationTxt)
            questionCountTxt = findViewById(R.id.questionCount)
            questionTotalCountTxt = findViewById(R.id.questionTotalCount)
            sectionTxt = findViewById(R.id.sectionTxt)
            questionRecyclerView = findViewById(R.id.questionRecyclerView)
            submitQuestionBtn = findViewById(R.id.submitQuestionBtn)
            previousBtn = findViewById(R.id.prevBtn)
            nextBtn = findViewById(R.id.nextBtn)
        }
    }

    private fun initialiseSectionItem() {
        sectionItems = returnQuestionList()

        if (::sectionItems.isInitialized && sectionItems.isNotEmpty()) {
            introDialog()
        }
    }

    private fun showQuestion() {
        val currentSection = sectionItems.getOrNull(currentSectionIndex)
        if (currentSection != null) {
            sectionTxt.text = currentSection.sectionTitle
            sectionTxt.isVisible = !currentSection.sectionTitle.isNullOrEmpty()

            val questionItem = currentSection.questionItem
            questionRecyclerView.isVisible = questionItem != null

            if (currentSectionIndex == 0) {
                if (questionItem != null) {
                    currentQuestionCount = 1

                    val countString =
                        String.format(Locale.getDefault(), "Question %d", currentQuestionCount)
                    questionCountTxt.text = countString
                }
            }

            if (questionItem != null) {
                showQuestionPreview(questionItem)

                val countString =
                    String.format(Locale.getDefault(), "Question %d", currentQuestionCount)
                questionCountTxt.text = countString
            }

            updateNavigationButtons()

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

            // Decrement the question count only if there are questions
            if (sectionItems.getOrNull(currentSectionIndex)?.questionItem != null) {
                currentQuestionCount++
            }

            currentSectionIndex++

            showQuestion()
        }

    }

    private fun showPreviousQuestion() {
        previousBtn.setOnClickListener {
            if (currentSectionIndex > 0) {

                // Decrement the question count only if there are questions
                if (sectionItems.getOrNull(currentSectionIndex)?.questionItem != null) {
                    currentQuestionCount--
                } else {
                    currentQuestionCount--
                }

                currentSectionIndex--

                showQuestion()

            }
        }
    }


    private fun showQuestionPreview(nextQuestion: QuestionItem?) {
        questionTestAdapter = AdminELearningTestAdapter(
            mutableListOf(nextQuestion),
            userResponses,
            this
        )

        questionRecyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(requireContext())
            adapter = questionTestAdapter
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


    // Return list of questions
    private fun returnQuestionList(): MutableList<SectionModel> {
        val sectionItems = mutableListOf<SectionModel>()
        try {
            if (jsonData?.isNotBlank() == true) {
                val questionData = parseJsonObject(jsonData!!)

                questionData.run {
                    if (has("questions")) {
                        settingsData = getJSONObject("settings")

                        settingsData.let {
                            duration = it.getString("duration")
                        }

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

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return sectionItems
    }


    private fun introDialog() {
        AdminELearningQuestionTestIntroDialogFragment(
            jsonData = settingsData.toString()
        ) { status ->

            if (status == "start") {
                val totalCount = totalQuestionCount()
                val countString = String.format(Locale.getDefault(), "/%d", totalCount)

                questionTotalCountTxt.text = countString

                showQuestion()
                countDownTimer()

            } else {
                requireActivity().finish()
            }

        }.show(parentFragmentManager, "Intro")
    }


    private fun totalQuestionCount(): Int {
        return sectionItems.count { it.questionItem != null }
    }


    private fun countDownTimer() {
        if (duration.isNullOrBlank()) {
            return
        }

        val quizDurationSeconds = duration?.toInt()?.times(60)
        val quizDurationMillis = quizDurationSeconds?.times(1000)
        val countDownIntervalMillis = 1000

        countDownJob = CoroutineScope(Dispatchers.Default).launch {
            var remainingTimeMillis = quizDurationMillis

            if (remainingTimeMillis != null) {
                while (remainingTimeMillis > 0) {
                    val minutes = remainingTimeMillis / 1000 / 60
                    val seconds = (remainingTimeMillis / 1000) % 60
                    val timeString =
                        String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

                    withContext(Dispatchers.Main) {
                        countDownTxt.text = timeString

                        if (remainingTimeMillis <= 5 * 60 * 1000) {
                            countDownTxt.setTextColor(Color.RED)
                        }
                    }

                    delay(countDownIntervalMillis.toLong())
                    remainingTimeMillis -= countDownIntervalMillis
                }
            }

            submitTest()

            withContext(Dispatchers.Main) {
                countDownTxt.text = "00:00"
            }
        }
    }

    private fun submitTest() {
        parentFragmentManager.commit {
            replace(
                R.id.learning_container,
                AdminELearningTestSummaryFragment.newInstance(jsonData ?: "", userResponses)
            )
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onOptionSelected(questionId: String, selectedOption: String) {
        userResponses[questionId] = selectedOption

        if (currentSectionIndex == sectionItems.size - 1) {
            submitQuestionBtn.isVisible = true
        }

        GlobalScope.launch {
            delay(1000L)

            withContext(Dispatchers.Main) {
                showNextQuestion()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (::countDownJob.isInitialized)
            countDownJob.cancel()
    }
}
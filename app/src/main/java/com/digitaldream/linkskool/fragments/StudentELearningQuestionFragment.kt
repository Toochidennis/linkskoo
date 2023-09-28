package com.digitaldream.linkskool.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.models.MultiChoiceQuestion
import com.digitaldream.linkskool.models.MultipleChoiceOption
import com.digitaldream.linkskool.models.QuestionItem
import com.digitaldream.linkskool.models.SectionModel
import com.digitaldream.linkskool.models.ShortAnswerModel
import com.digitaldream.linkskool.utils.FunctionUtils.formatDate2
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class StudentELearningQuestionFragment : Fragment(R.layout.fragment_student_e_learning_question) {

    private lateinit var endDateTxt: TextView
    private lateinit var titleTxt: TextView
    private lateinit var questionCountTxt: TextView
    private lateinit var durationTxt: TextView
    private lateinit var descriptionTxt: TextView
    private lateinit var startDateTxt: TextView
    private lateinit var startBtn: Button

    private var questionList = mutableListOf<SectionModel>()

    private var jsonData: String? = null
    private var startDate: String? = null
    private var endDate: String? = null
    private var title: String? = null
    private var duration: String? = null
    private var description: String? = null
    private var questionCount: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            jsonData = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String = "") =
            StudentELearningQuestionFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        initializeQuestionsList()

    }

    private fun setUpViews(view: View) {
        view.apply {
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            endDateTxt = findViewById(R.id.dateTxt)
            titleTxt = findViewById(R.id.titleTxt)
            questionCountTxt = findViewById(R.id.questionCountTxt)
            durationTxt = findViewById(R.id.durationTxt)
            descriptionTxt = findViewById(R.id.descriptionTxt)
            startDateTxt = findViewById(R.id.startDateTxt)
            startBtn = findViewById(R.id.startBtn)

            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
            val actionBar = (requireActivity() as AppCompatActivity).supportActionBar

            actionBar?.apply {
                title = "Quiz"
                setHomeButtonEnabled(true)
                setDisplayHomeAsUpEnabled(true)
            }

            toolbar.setNavigationOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        }
    }

    private fun initializeQuestionsList() {
        questionList = returnQuestionList()

        questionCount = totalQuestionCount().toString()

        setTextOnDateFields()
        setTextOnOtherFields()
    }

    // Return list of questions
    private fun returnQuestionList(): MutableList<SectionModel> {
        val sectionItems = mutableListOf<SectionModel>()
        try {
            if (jsonData?.isNotBlank() == true) {
                val questionData = parseJsonObject(jsonData!!)

                questionData.run {
                    if (has("questions")) {
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

    // Parse the JSON data from a given JSON string
    private fun parseJsonObject(json: String): JSONObject {
        return JSONObject().apply {
            JSONObject(json).let { jsonObject ->
                put("questions", parseQuestionJson(JSONArray(jsonObject.getString("q"))))
                parseSettingsJson(JSONObject(jsonObject.getString("e")))
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

    // Parse the settings JSON object
    private fun parseSettingsJson(settings: JSONObject) {
        with(settings) {
            title = getString("title")
            description = getString("description")
            duration = getString("objective")
            startDate = getString("start_date")
            endDate = getString("end_date")
        }
    }

    private fun totalQuestionCount(): Int {
        return questionList.count { it.questionItem != null }
    }

    private fun setTextOnDateFields() {
        try {
            val serverDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val currentCalendar = Calendar.getInstance()
            val startCalendar = Calendar.getInstance()
            val endCalendar = Calendar.getInstance()

            disableStartBtn()

            if (startDate?.isNotBlank() == true && endDate?.isNotBlank() == true) {
                val startDateFromServer = serverDateFormat.parse(startDate!!)
                val endDateFromServer = serverDateFormat.parse(endDate!!)

                var dateString = ""

                if (endDateFromServer != null && startDateFromServer != null) {
                    startCalendar.time = endDateFromServer
                    endCalendar.time = startDateFromServer

                    var end = "Due ${formatDate2(endDate!!, "date time")}"

                    if (startCalendar == currentCalendar || startCalendar.after(currentCalendar)) {
                        if (endCalendar.after(currentCalendar)) {
                            dateString = "Quiz will last for $duration minutes"
                        } else if (endCalendar.before(currentCalendar)) {
                            dateString = "You can no longer take this quiz"
                          //  endDateTxt.setTextColor(Color.RED)
                        } else if (endCalendar == currentCalendar) {
                            end = "Due Today"
                            dateString = "Quiz will last for $duration minutes"
                        }

                        startDateTxt.text = dateString
                        endDateTxt.text = end

                        if (endCalendar.after(currentCalendar) || endCalendar == currentCalendar) {
                            enableStartBtn()
                        } else {
                            disableStartBtn()
                        }

                    } else {
                        dateString =
                            if (endCalendar.before(currentCalendar)) "You can no longer take this quiz"
                            else "Quiz will be available from ${
                                formatDate2(startDate!!, "date time")
                            } to ${formatDate2(endDate!!, "date time")}"

                        startDateTxt.text = dateString
                        endDateTxt.text = end

//                        if (endCalendar.before(currentCalendar))
//                            endDateTxt.setTextColor(Color.RED)

                        disableStartBtn()
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun disableStartBtn() {
        startBtn.apply {
            isEnabled = false
            setBackgroundResource(R.drawable.ripple_effect6)
            setTextColor(Color.WHITE)
        }
    }

    private fun enableStartBtn() {
        val animation = AnimationUtils.loadAnimation(context, R.anim.pulse)

        startBtn.apply {
            isEnabled = true
            startAnimation(animation)

            setOnClickListener {

            }
        }
    }


    private fun setTextOnOtherFields() {
        titleTxt.text = title
        durationTxt.text = duration ?: "0"
        questionCountTxt.text = questionCount
        descriptionTxt.text = description
    }

}
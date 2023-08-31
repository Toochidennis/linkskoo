package com.digitaldream.linkskool.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.AdminELearningQuestionAdapter
import com.digitaldream.linkskool.dialog.AdminELearningQuestionDialog
import com.digitaldream.linkskool.dialog.AdminELearningQuestionPreviewDialogFragment
import com.digitaldream.linkskool.models.MultiChoiceQuestion
import com.digitaldream.linkskool.models.MultipleChoiceOption
import com.digitaldream.linkskool.models.QuestionItem
import com.digitaldream.linkskool.models.SectionModel
import com.digitaldream.linkskool.models.ShortAnswerModel
import com.digitaldream.linkskool.utils.FunctionUtils.compareJsonObjects
import com.digitaldream.linkskool.utils.FunctionUtils.encodeUriOrFileToBase64
import com.digitaldream.linkskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.linkskool.utils.ItemTouchHelperCallback
import com.digitaldream.linkskool.utils.VolleyCallback
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AdminELearningQuestionFragment : Fragment(R.layout.fragment_admin_e_learning_question) {

    private lateinit var topicButton: CardView
    private lateinit var questionTitleTxt: TextView
    private lateinit var descriptionTxt: TextView
    private lateinit var sectionRecyclerView: RecyclerView
    private lateinit var emptyQuestionTxt: TextView
    private lateinit var previewQuestionButton: ImageButton
    private lateinit var submitQuestionButton: ImageButton
    private lateinit var addQuestionButton: ImageButton

    private lateinit var sectionAdapter: AdminELearningQuestionAdapter
    private var sectionItems = mutableListOf<SectionModel>()
    private var selectedClassArray = JSONArray()

    private var json: String? = null
    private var questionTitle: String? = null
    private var levelId: String? = null
    private var courseId: String? = null
    private var topicId: String? = null
    private var courseName: String? = null
    private var questionDescription: String? = null
    private var startDate: String? = null
    private var endDate: String? = null
    private var questionTopic: String? = null
    private var year: String? = null
    private var term: String? = null
    private var userId: String? = null
    private var userName: String? = null
    private var task: String? = null
    private var id: String? = null

    private var questionData: String? = null
    private var settingsObject = JSONObject()
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            json = it.getString(ARG_PARAM1)
            task = it.getString(ARG_PARAM2)
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onExit()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    companion object {

        @JvmStatic
        fun newInstance(json: String = "", task: String = "") =
            AdminELearningQuestionFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, json)
                    putString(ARG_PARAM2, task)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        sharedPreferences =
            requireActivity().getSharedPreferences("loginDetail", Context.MODE_PRIVATE)

        if (task == "edit" && !json.isNullOrBlank()) {
            try {

                parseJsonObject(json!!)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        loadJsonData()

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


    private fun setUpViews(view: View) {
        view.apply {
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            topicButton = findViewById(R.id.topicButton)
            questionTitleTxt = findViewById(R.id.questionTitleTxt)
            descriptionTxt = findViewById(R.id.descriptionTxt)
            sectionRecyclerView = findViewById(R.id.questionRecyclerView)
            emptyQuestionTxt = findViewById(R.id.emptyQuestionTxt)
            previewQuestionButton = findViewById(R.id.previewQuestionsButton)
            submitQuestionButton = findViewById(R.id.submitQuestionsButton)
            addQuestionButton = findViewById(R.id.addQuestionsButton)

            toolbar.apply {
                title = "Question"
                setNavigationIcon(R.drawable.arrow_left)
                setNavigationOnClickListener { onExit() }
            }
        }
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
                    "",
                    sectionTitle, QuestionItem.MultiChoice(question), "multiple_choice"
                )

                shortQuestion != null -> SectionModel(
                    "",
                    sectionTitle, QuestionItem.ShortAnswer(shortQuestion), "short_answer"
                )

                else -> null
            }

            if (sectionTitle.isNullOrEmpty()) {
                questionItem?.let {
                    sectionItems.add(it)
                }
            } else {
                val newSection = SectionModel("", sectionTitle, null, "section")
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
            AdminELearningQuestionAdapter(parentFragmentManager, sectionItems)

        sectionRecyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(requireContext())
            adapter = sectionAdapter
            smoothScrollToPosition(if (sectionItems.isNotEmpty()) sectionItems.size - 1 else 0)
        }
    }

    private fun fromQuestionSettings() {
        try {
            if (task == "settings") {
                json?.let {
                    parseJsonFromQuestionSettings(it)
                    setQuestionsIfExist()
                }
            } else {
                setQuestionsIfExist()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            showToast("Error loading settings")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun parseJsonFromQuestionSettings(json: String) {
        JSONObject(json).run {
            id = getString("id")
            questionTitle = getString("title")
            questionDescription = getString("description")
            startDate = getString("start_date")
            endDate = getString("end_date")
            questionTopic = getString("topic")
            topicId = getString("topic_id")
            levelId = getString("level")
            courseId = getString("course")
            courseName = getString("course_name")
            selectedClassArray = getJSONArray("class")

            questionTitleTxt.text = questionTitle
            descriptionTxt.text = questionDescription
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun toQuestionSettings() {
        createAssessmentObject()

        try {

            parentFragmentManager.commit {
                replace(
                    R.id.learning_container,
                    AdminELearningQuestionSettingsFragment.newInstance(
                        levelId!!,
                        courseId!!,
                        courseName!!,
                        "edit",
                        settingsObject.toString()
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showToast("Something went wrong")
        }
    }

    private fun setQuestionsIfExist() {
        if (!questionData.isNullOrEmpty()) {

            questionData?.let { data ->
                JSONObject(data).run {
                    val settingsObject = getJSONObject("settings")

                    if (task != "settings") {
                        parseJsonFromQuestionSettings(settingsObject.toString())
                    }

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
                                                    QuestionItem.MultiChoice(multiChoiceQuestion),
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
                                                    QuestionItem.ShortAnswer(shortAnswerModel),
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
                showToast("There are no questions to preview")
            }
        }
    }

    private fun createAssessmentObject(): JSONObject? {
        if (sectionItems.isEmpty()) return null

        val questionArray = JSONArray()
        val assessmentObject = JSONObject()

        sectionItems.forEach { sectionModel ->
            if (!sectionModel.sectionTitle.isNullOrEmpty()) {
                JSONObject().apply {
                    put("question_id", sectionModel.sectionId ?: "")
                    put("question_title", sectionModel.sectionTitle)
                    put("question_type", sectionModel.viewType)

                    JSONArray().apply {
                        put(
                            JSONObject().apply {
                                put("file_name", "")
                                put("old_file_name", "")
                                put("type", "")
                                put("file", "")
                            }
                        )
                    }.let {
                        put("question_files", it)
                    }
                }.let {
                    questionArray.put(it)
                }
            } else {
                questionArray.put(createQuestionsJsonObject(sectionModel) ?: "{}")
            }
        }

        settingsObject = createSettingsJsonObject()

        assessmentObject.apply {
            put("settings", settingsObject)
            put("questions", questionArray)
        }

        println("assessment: $assessmentObject")

        saveJsonData(assessmentObject.toString())

        return assessmentObject
    }

    private fun createSettingsJsonObject(): JSONObject {
        return JSONObject().apply {
            put("id", id ?: "")
            put("author_id", userId)
            put("author_name", userName)
            put("title", questionTitle)
            put("description", questionDescription)
            put("level", levelId)
            put("class", selectedClassArray)
            put("course", courseId)
            put("course_name", courseName)
            put("topic", questionTopic)
            put("topic_id", topicId)
            put("start_date", "$startDate")
            put("end_date", "$endDate")
            put("year", year)
            put("term", term)
        }
    }

    private fun createQuestionsJsonObject(sectionModel: SectionModel): JSONObject? {
        val questionItem = sectionModel.questionItem ?: return null

        when (questionItem) {
            is QuestionItem.MultiChoice -> {
                val multiChoice = questionItem.question
                val optionsList = multiChoice.options

                return JSONObject().apply {
                    put("question_id", multiChoice.questionId ?: "")
                    put("question_title", multiChoice.questionText)
                    put("question_type", sectionModel.viewType)

                    JSONArray().apply {
                        put(
                            JSONObject().apply {
                                put("file_name", multiChoice.attachmentName)
                                put(
                                    "old_file_name",
                                    if ((multiChoice.attachmentName !=
                                                multiChoice.previousAttachmentName) &&
                                        multiChoice.previousAttachmentName.isNotBlank()
                                    )
                                        multiChoice.previousAttachmentName
                                    else {
                                        ""
                                    }
                                )

                                put("type", "")

                                put(
                                    "file",
                                    if (multiChoice.attachmentUri != null) {
                                        convertFileOrUriToBase64(multiChoice.attachmentUri)
                                    } else {
                                        ""
                                    }
                                )
                            }
                        )
                    }.let {
                        put("question_files", it)
                    }

                    val optionsArray = JSONArray()
                    optionsList?.forEach { option ->
                        JSONObject().apply {
                            put("order", option.optionOrder)
                            put("text", option.optionText)

                            JSONArray().apply {
                                put(
                                    JSONObject().apply {
                                        put("file_name", option.attachmentName)
                                        put(
                                            "old_file_name",
                                            if ((option.attachmentName !=
                                                        option.previousAttachmentName) &&
                                                option.previousAttachmentName.isNotBlank()
                                            ) {
                                                option.previousAttachmentName
                                            } else {
                                                ""
                                            }
                                        )

                                        put("type", "")

                                        put(
                                            "file",
                                            if (option.attachmentUri != null) {
                                                convertFileOrUriToBase64(option.attachmentUri)
                                            } else {
                                                ""
                                            }
                                        )
                                    }
                                )
                            }.let {
                                put("option_files", it)
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
                }
            }

            is QuestionItem.ShortAnswer -> {
                val shortAnswer = questionItem.question

                return JSONObject().apply {
                    put("question_id", shortAnswer.questionId ?: "")
                    put("question_title", shortAnswer.questionText)
                    put("question_type", sectionModel.viewType)

                    JSONArray().apply {
                        put(
                            JSONObject().apply {

                                put("file_name", shortAnswer.attachmentName)

                                put(
                                    "old_file_name",
                                    if ((shortAnswer.attachmentName !=
                                                shortAnswer.previousAttachmentName) &&
                                        shortAnswer.previousAttachmentName.isNotBlank()
                                    ) {
                                        shortAnswer.previousAttachmentName
                                    } else {
                                        ""
                                    }
                                )

                                put("type", "")

                                put(
                                    "file",
                                    if (shortAnswer.attachmentUri != null) {
                                        convertFileOrUriToBase64(shortAnswer.attachmentUri)
                                    } else {
                                        ""
                                    }
                                )
                            }
                        )
                    }.let {
                        put("question_files", it)
                    }

                    JSONObject().apply {
                        put("order", "")
                        put("text", shortAnswer.answerText)
                    }.let {
                        put("correct", it)
                    }
                }
            }

            else -> {
                return null
            }
        }
    }

    private fun convertFileOrUriToBase64(fileUri: Any?): Any? {
        return if (fileUri is String) {
            fileUri
        } else {
            runBlocking(Dispatchers.IO) {
                encodeUriOrFileToBase64(
                    fileUri,
                    requireContext()
                )
            }
        }
    }

    private fun submitQuestions() {
        val assessmentObject = createAssessmentObject()

        val url = "${getString(R.string.base_url)}/addQuiz.php"
        val hashMap = HashMap<String, String>().apply {
            put("assessment", assessmentObject.toString())
        }

        if (assessmentObject != null && assessmentObject.length() != 0) {
            sendRequestToServer(Request.Method.POST, url, requireContext(), hashMap,
                object : VolleyCallback {
                    override fun onResponse(response: String) {
                        if (task == "edit" && !json.isNullOrBlank()) {
                            finishActivity()
                        } else {
                            showToast("Question added")
                            finishActivity()
                        }

                    }

                    override fun onError(error: VolleyError) {
                        showToast("Something went wrong please try again")
                    }
                }
            )

        } else {
            showToast("There are no questions to submit")
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun finishActivity() {
        GlobalScope.launch {
            delay(50L)
            onBackPressed()
        }
    }

    private fun parseJsonObject(json: String) {
        JSONObject().apply {
            JSONObject(json).let { jsonObject ->
                put("settings", parseSettingsJson(JSONObject(jsonObject.getString("e"))))
                put("questions", parseQuestionJson(JSONArray(jsonObject.getString("q"))))
            }
        }.let {
            saveJsonData(it.toString())
        }
    }

    private fun parseSettingsJson(settings: JSONObject): JSONObject {
        return JSONObject().apply {
            settings.let {
                put("id", it.getString("id"))
                put("author_id", it.getString("author_id"))
                put("author_name", it.getString("author_name"))
                put("title", it.getString("title"))
                put("description", it.getString("description"))
                put("level", it.getString("level"))
                put("class", parseClassArray(JSONArray(it.getString("class"))))
                put("course", it.getString("course_id"))
                put("course_name", it.getString("course_name"))
                put("topic", it.getString("topic"))
                put("topic_id", it.getString("topic_id"))
                put("start_date", it.getString("start_date"))
                put("end_date", it.getString("end_date"))
                put("year", year)
                put("term", it.getString("term"))
            }
        }
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


    private fun trimText(text: String): String {
        return text.replace("../assets/elearning/practice/", "").ifEmpty { "" }
    }


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


    private fun onExit() {
        try {
            val assessmentObject = createAssessmentObject()

            if (!questionData.isNullOrEmpty() && assessmentObject != null
                && assessmentObject.length() != 0
            ) {
                val json2 = JSONObject(questionData!!)
                val areContentSame = compareJsonObjects(assessmentObject, json2)

                if (areContentSame) {
                    onBackPressed()
                } else {
                    exitWithWarning()
                }
            } else if (assessmentObject != null && assessmentObject.length() != 0) {
                exitWithWarning()
            } else {
                onBackPressed()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun exitWithWarning() {
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
        deleteJsonData()
        requireActivity().finish()
    }

    private fun saveJsonData(data: String) {
        sharedPreferences.edit().apply {
            putString("question_data", data)
        }.apply()
    }

    private fun loadJsonData() {
        year = sharedPreferences.getString("school_year", "")
        term = sharedPreferences.getString("term", "")
        userId = sharedPreferences.getString("user_id", "")
        userName = sharedPreferences.getString("user", "")
        questionData = sharedPreferences.getString("question_data", "")
    }

    private fun deleteJsonData() {
        sharedPreferences.edit().apply {
            putString("question_data", "")
        }.apply()
    }

}
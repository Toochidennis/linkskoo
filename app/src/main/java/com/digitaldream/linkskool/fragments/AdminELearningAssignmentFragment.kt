package com.digitaldream.linkskool.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.AdminELearningQuestionSettingsAdapter
import com.digitaldream.linkskool.adapters.GenericAdapter
import com.digitaldream.linkskool.config.DatabaseHelper
import com.digitaldream.linkskool.dialog.AdminELearningAssignmentGradeDialog
import com.digitaldream.linkskool.dialog.AdminELearningAttachmentDialog
import com.digitaldream.linkskool.dialog.AdminELearningDatePickerDialog
import com.digitaldream.linkskool.models.AttachmentModel
import com.digitaldream.linkskool.models.ClassNameTable
import com.digitaldream.linkskool.models.TagModel
import com.digitaldream.linkskool.utils.FunctionUtils
import com.digitaldream.linkskool.utils.FunctionUtils.compareJsonObjects
import com.digitaldream.linkskool.utils.FunctionUtils.formatDate2
import com.digitaldream.linkskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.linkskool.utils.FunctionUtils.showSoftInput
import com.digitaldream.linkskool.utils.VolleyCallback
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import org.json.JSONArray
import org.json.JSONObject
import java.io.File


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"
private const val ARG_PARAM4 = "param4"

class AdminELearningAssignmentFragment :
    Fragment(R.layout.fragment_admin_e_learning_assignment) {

    private lateinit var mBackBtn: ImageView
    private lateinit var mAssignBtn: Button
    private lateinit var mAssignmentTitleEditText: EditText
    private lateinit var mClassRecyclerView: RecyclerView
    private lateinit var mSelectAllBtn: Button
    private lateinit var mEmptyClassTxt: TextView
    private lateinit var mDescriptionEditText: EditText
    private lateinit var mAttachmentTxt: TextView
    private lateinit var mAttachmentBtn: RelativeLayout
    private lateinit var mAttachmentRecyclerView: RecyclerView
    private lateinit var mAddAttachmentBtn: TextView
    private lateinit var mGradeBtn: RelativeLayout
    private lateinit var mGradeTxt: TextView
    private lateinit var mResetGradeBtn: ImageView
    private lateinit var mDateBtn: RelativeLayout
    private lateinit var mStartDateTxt: TextView
    private lateinit var mEndDateTxt: TextView
    private lateinit var mStartDateBtn: ImageView
    private lateinit var mEndDateBtn: ImageView
    private lateinit var mDateSeparator: LinearLayout
    private lateinit var mTopicTxt: TextView

    private var mClassList = mutableListOf<ClassNameTable>()
    private val selectedClassItems = hashMapOf<String, String>()
    private val mTagList = mutableListOf<TagModel>()

    private val mFileList = mutableListOf<AttachmentModel>()
    private lateinit var mAdapter: GenericAdapter<AttachmentModel>

    private var mLevelId: String? = null
    private var mCourseId: String? = null
    private var mStartDate: String? = null
    private var mEndDate: String? = null
    private var jsonFromTopic: String? = null
    private var mCourseName: String? = null
    private var updatedJson = JSONObject()
    private var newHashMap = mutableMapOf<Any?, Any?>()
    private var year: String? = null
    private var term: String? = null
    private var userId: String? = null
    private var userName: String? = null

    private var topicText: String? = null
    private var descriptionText: String? = null
    private var titleText: String? = null
    private var gradeText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            mLevelId = it.getString(ARG_PARAM1)
            mCourseId = it.getString(ARG_PARAM2)
            jsonFromTopic = it.getString(ARG_PARAM3)
            mCourseName = it.getString(ARG_PARAM4)
        }

        val callBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onExit()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, callBack)
    }

    companion object {

        @JvmStatic
        fun newInstance(levelId: String, courseId: String, json: String, courseName: String) =
            AdminELearningAssignmentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, levelId)
                    putString(ARG_PARAM2, courseId)
                    putString(ARG_PARAM3, json)
                    putString(ARG_PARAM4, courseName)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        val sharedPreferences =
            requireActivity().getSharedPreferences("loginDetail", Context.MODE_PRIVATE)
        year = sharedPreferences.getString("school_year", "")
        term = sharedPreferences.getString("term", "")
        userId = sharedPreferences.getString("user_id", "")
        userName = sharedPreferences.getString("user", "")

        setUpClassAdapter()

        setDate()

        setGrade()

        fileAttachment(mAttachmentBtn)
        fileAttachment(mAddAttachmentBtn)

        showSoftInput(requireContext(), mAssignmentTitleEditText)

        mAssignBtn.setOnClickListener {
            assignAssignment()
        }

        mBackBtn.setOnClickListener {
            onExit()
        }

        mTopicTxt.setOnClickListener {
            selectTopic()
        }

    }

    private fun setUpViews(view: View) {
        view.apply {
            mBackBtn = findViewById(R.id.close_btn)
            mAssignBtn = findViewById(R.id.assignBtn)
            mAssignmentTitleEditText = findViewById(R.id.assignmentTitle)
            mClassRecyclerView = findViewById(R.id.class_recyclerview)
            mSelectAllBtn = findViewById(R.id.selectAllBtn)
            mEmptyClassTxt = findViewById(R.id.emptyClassTxt)
            mDescriptionEditText = findViewById(R.id.description)
            mAttachmentTxt = findViewById(R.id.attachmentTxt)
            mAttachmentBtn = findViewById(R.id.attachmentBtn)
            mAttachmentRecyclerView = findViewById(R.id.attachment_recyclerview)
            mAddAttachmentBtn = findViewById(R.id.addAttachmentButton)
            mGradeBtn = findViewById(R.id.gradeBtn)
            mGradeTxt = findViewById(R.id.gradeTxt)
            mResetGradeBtn = findViewById(R.id.resetGradingBtn)
            mDateBtn = findViewById(R.id.dateBtn)
            mStartDateTxt = findViewById(R.id.startDateTxt)
            mEndDateTxt = findViewById(R.id.endDateTxt)
            mStartDateBtn = findViewById(R.id.startDateBtn)
            mEndDateBtn = findViewById(R.id.endDateBtn)
            mDateSeparator = findViewById(R.id.separator)
            mTopicTxt = findViewById(R.id.topicBtn)
        }

    }

    private fun setUpClassAdapter() {
        try {
            val mDatabaseHelper = DatabaseHelper(requireContext())
            val dao: Dao<ClassNameTable, Long> = DaoManager.createDao(
                mDatabaseHelper.connectionSource, ClassNameTable::class.java
            )
            mClassList = dao.queryBuilder().where().eq("level", mLevelId).query()
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
                mClassRecyclerView.isVisible = false
                mSelectAllBtn.isVisible = false
                mEmptyClassTxt.isVisible = true
            } else {
                AdminELearningQuestionSettingsAdapter(
                    selectedClassItems,
                    mTagList,
                    mSelectAllBtn
                ).let {
                    mClassRecyclerView.apply {
                        hasFixedSize()
                        layoutManager = LinearLayoutManager(
                            requireContext(),
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                        adapter = it
                        isVisible = true

                        mSelectAllBtn.isVisible = true
                        mEmptyClassTxt.isVisible = false
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setDate() {
        mDateBtn.setOnClickListener {
            AdminELearningDatePickerDialog(requireContext())
            { startDate, endDate ->

                mStartDate = startDate
                mEndDate = endDate

                val start = "Start ${formatDate2(startDate, "custom1")}"
                val end = "Due ${formatDate2(endDate, "custom1")}"
                mStartDateTxt.text = start
                mEndDateTxt.text = end

                showDate()
            }.apply {
                setCancelable(true)
                show()
            }.window?.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        mStartDateBtn.setOnClickListener {
            "Date".let { mStartDateTxt.text = it }
            mStartDateBtn.isVisible = false
            mDateSeparator.isVisible = false
        }

        mEndDateBtn.setOnClickListener {
            mEndDateTxt.isVisible = false
            mEndDateBtn.isVisible = false
            mDateSeparator.isVisible = false
        }
    }

    private fun showDate() {
        mStartDateBtn.isVisible = true
        mEndDateBtn.isVisible = true
        mEndDateTxt.isVisible = true
        mDateSeparator.isVisible = true
    }

    private fun setGrade() {
        mGradeBtn.setOnClickListener {
            AdminELearningAssignmentGradeDialog(requireContext()) { point ->

                when (point) {
                    "Unmarked" -> point
                    "1" -> "$point point"
                    else -> "$point points"
                }.let {
                    mGradeTxt.text = it
                }

                mResetGradeBtn.isVisible = true

            }.apply {
                show()
                setCancelable(true)
            }.window?.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        mResetGradeBtn.setOnClickListener {
            mResetGradeBtn.isVisible = false
            "Unmarked".let { mGradeTxt.text = it }
        }
    }

    private fun fileAttachment(button: View) {
        button.setOnClickListener {
            AdminELearningAttachmentDialog { type: String, name: String, uri: Any? ->
                mFileList.add(AttachmentModel(name, "", type, uri))
                setUpFilesAdapter()

            }.show(parentFragmentManager, "")
        }
    }

    private fun setUpFilesAdapter() {
        try {
            if (mFileList.isNotEmpty()) {
                mAdapter = GenericAdapter(
                    mFileList,
                    R.layout.fragment_admin_e_learning_assigment_attachment_item,
                    bindItem = { itemView, model, position ->
                        val itemTxt: TextView = itemView.findViewById(R.id.itemTxt)
                        val deleteButton: ImageView =
                            itemView.findViewById(R.id.deleteButton)

                        itemTxt.text = model.name

                        setCompoundDrawable(itemTxt, model.type)

                        deleteAttachment(deleteButton, position)

                    }, onItemClick = { position: Int ->
                        val itemPosition = mFileList[position]

                        previewAttachment(itemPosition.type, itemPosition.uri)
                    }
                )

                mAttachmentRecyclerView.apply {
                    hasFixedSize()
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = mAdapter
                    smoothScrollToPosition(mFileList.size - 1)

                    mAttachmentTxt.isVisible = false
                    mAddAttachmentBtn.isVisible = true
                    mAttachmentBtn.isClickable = false
                }
            } else {
                mAttachmentTxt.isVisible = true
                mAddAttachmentBtn.isVisible = false
                mAttachmentBtn.isClickable = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun previewAttachment(type: String, uri: Any?) {
        val fileUri = when (uri) {
            is File -> {
                val file = File(uri.absolutePath)
                FileProvider.getUriForFile(
                    requireContext(),
                    "${requireActivity().packageName}.provider",
                    file
                )
            }

            is String -> Uri.parse(uri)

            else -> uri
        }

        when (type) {
            "image" -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(fileUri as Uri?, "image/*")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(intent)
            }

            "video" -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(fileUri as Uri?, "video/*")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(intent)
            }

            "url" -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fileUri.toString()))
                startActivity(intent)
            }

            "pdf", "excel", "word" -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(fileUri as Uri?, "application/*")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(intent)
            }

            else -> {
                showText("Can't open file")
            }
        }
    }

    private fun deleteAttachment(deleteButton: ImageView, position: Int) {
        deleteButton.setOnClickListener {
            mFileList.removeAt(position)
            if (mFileList.isEmpty()) {
                mAttachmentTxt.isVisible = true
                mAddAttachmentBtn.isVisible = false
                mAttachmentBtn.isClickable = true
            }
            mAdapter.notifyDataSetChanged()
        }
    }

    private fun setCompoundDrawable(textView: TextView, type: String) {
        textView.setCompoundDrawablesWithIntrinsicBounds(
            when (type) {
                "image" -> R.drawable.ic_image24
                "video" -> R.drawable.ic_video24
                "pdf" -> R.drawable.ic_pdf24
                "unknown" -> R.drawable.ic_unknown_document24
                "url" -> R.drawable.ic_link
                else -> R.drawable.ic_document24
            }.let {
                ContextCompat.getDrawable(requireContext(), it)
            },
            null, null, null
        )
    }

    private fun assignAssignment() {
        getFieldsText()

        if (titleText.isNullOrBlank()) {
            mAssignmentTitleEditText.error = "Please enter assignment title"
        } else if (selectedClassItems.size == 0) {
            showText("Please select a class")
        } else if (descriptionText.isNullOrBlank()) {
            mDescriptionEditText.error = "Please enter a description"
        } else if (mStartDate.isNullOrEmpty() or mEndDate.isNullOrEmpty()) {
            showText("Please set date")
        }  else {
            postAssignment()
        }
    }


    private fun prepareAssignment(): HashMap<String, String> {
        val filesArray = JSONArray()
        val classArray = JSONArray()

        return HashMap<String, String>().apply {
            put("title", titleText!!)
            put("type", "3")
            put("description", descriptionText!!)
            put("topic", if (topicText == "Topic") "No topic" else topicText!!)
            put("objective", "")

            mFileList.isNotEmpty().let { isTrue ->
                if (isTrue) {
                    mFileList.forEach { attachment ->
                        JSONObject().apply {
                            put("file_name", attachment.name)

                            val oldFileName =
                                if (attachment.name != attachment.oldName &&
                                    attachment.oldName.isNotBlank()
                                ) {
                                    attachment.oldName
                                } else {
                                    ""
                                }

                            put("old_file_name", oldFileName)
                            put("type", attachment.type)

                            val image = FunctionUtils.convertUriOrFileToBase64(
                                attachment.uri,
                                requireContext()
                            )
                            put("image", image)
                        }.let {
                            filesArray.put(it)
                        }
                    }
                }
            }

            put("files", filesArray.toString())

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
            put("level", mLevelId!!)
            put("course", mCourseId!!)
            put("course_name", mCourseName!!)
            put("start_date", mStartDate!!)
            put("end_date", mEndDate!!)
            put("grade", gradeText!!)
            put("author_id", userId!!)
            put("author_name", userName!!)
            put("year", year!!)
            put("term", term!!)
        }
    }

    private fun getFieldsText() {
        titleText = mAssignmentTitleEditText.text.toString().trim()
        descriptionText = mDescriptionEditText.text.toString().trim()
        topicText = mTopicTxt.text.toString()
        gradeText = mGradeTxt.text.toString()
    }

    private fun postAssignment() {
        val url = "${getString(R.string.base_url)}/addContent.php"
        val hashMap = prepareAssignment()

        sendRequestToServer(
            Request.Method.POST,
            url,
            requireContext(),
            hashMap,
            object
                : VolleyCallback {
                override fun onResponse(response: String) {
//                    Toast.makeText(
//                        requireContext(), "Material submitted successfully",
//                        Toast.LENGTH_SHORT
//                    ).show()
//
//                    SystemClock.sleep(1000)
//                    onBackPressed()

                }

                override fun onError(error: VolleyError) {
                    showText("Something went wrong please try again")
                }
            })
    }

    private fun selectTopic() = if (selectedClassItems.isEmpty()) {
        showText("Please select a class")
    } else {
        AdminELearningSelectTopicDialogFragment(
            courseId = mCourseId!!,
            levelId = mLevelId!!,
            courseName = mCourseName!!,
            selectedClass = selectedClassItems
        ) { topic ->

            mTopicTxt.text = topic

            println("topic $topic")
        }.show(parentFragmentManager, "")
    }


    private fun showText(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }


    private fun onExit() {
        try {
            val json1 = JSONObject(jsonFromTopic!!)
            val json2 = updatedJson

            if (json2.length() != 0) {
                val areContentSame = compareJsonObjects(json1, json2)

                if (areContentSame) {
                    onBackPressed()
                } else {
                    exitWithWarning()
                }
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
        requireActivity().finish()
    }

}
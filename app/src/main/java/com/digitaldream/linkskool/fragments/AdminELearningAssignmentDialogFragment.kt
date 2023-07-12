package com.digitaldream.linkskool.fragments

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
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.digitaldream.linkskool.utils.FunctionUtils.compareJsonObjects
import com.digitaldream.linkskool.utils.FunctionUtils.formatDate2
import com.digitaldream.linkskool.utils.FunctionUtils.showSoftInput
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import org.json.JSONArray
import org.json.JSONObject
import java.io.File


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"

class AdminELearningAssignmentDialogFragment :
    DialogFragment(R.layout.fragment_admin_e_learning_assignment) {

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
    private lateinit var mTopicBtn: TextView

    private var mClassList = mutableListOf<ClassNameTable>()
    private val selectedClassItems = hashMapOf<String, String>()
    private val mTagList = mutableListOf<TagModel>()

    private val mFileList = mutableListOf<AttachmentModel>()
    private lateinit var mAdapter: GenericAdapter<AttachmentModel>

    private var mLevelId: String? = null
    private var mCourseId: String? = null
    private var mStartDate: String? = null
    private var mStartTime: String? = null
    private var mEndDate: String? = null
    private var mEndTime: String? = null
    private var jsonFromTopic: String? = null
    private var updatedJson: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mLevelId = it.getString(ARG_PARAM1)
            mCourseId = it.getString(ARG_PARAM2)
            jsonFromTopic = it.getString(ARG_PARAM3)
        }
    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String, param3: String) =
            AdminELearningAssignmentDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    putString(ARG_PARAM3, param3)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            mTopicBtn = findViewById(R.id.topicBtn)
        }

        classList()

        setDate()

        setGrade()

        onExit()

        fileAttachment(mAttachmentBtn)
        fileAttachment(mAddAttachmentBtn)

        showSoftInput(requireContext(), mAssignmentTitleEditText)

        mAssignBtn.setOnClickListener {
            assignAssignment()
        }

    }


    private fun classList() {
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
            { startDate, endDate, startTime, endTime ->

                mStartDate = startDate
                mEndDate = endDate
                mStartTime = startTime
                mEndTime = endTime

                val start = "Start ${formatDate2(startDate, "custom1")} $startTime"
                val end = "Due ${formatDate2(endDate, "custom1")} $endTime"
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
            mStartDateTxt.text = "Date"
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
            mGradeTxt.text = "Unmarked"
        }
    }

    private fun fileAttachment(button: View) {
        button.setOnClickListener {
            AdminELearningAttachmentDialog { type: String, name: String, uri: Any? ->
                mFileList.add(AttachmentModel(name, type, uri))
                setUpAdapter()

            }.show(parentFragmentManager, "")
        }
    }

    private fun setUpAdapter() {
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
                Toast.makeText(requireContext(), "Can't open file", Toast.LENGTH_SHORT).show()
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
        val titleText = mAssignmentTitleEditText.text.toString().trim()
        val descriptionText = mDescriptionEditText.text.toString().trim()
        val topicText = mTopicBtn.text.toString()

        if (titleText.isEmpty()) {
            mAssignmentTitleEditText.error = "Please enter assignment title"
        } else if (selectedClassItems.size == 0) {
            Toast.makeText(requireContext(), "Please select a class", Toast.LENGTH_SHORT).show()
        } else if (descriptionText.isEmpty()) {
            mDescriptionEditText.error = "Please enter a description"
        } else if (mStartDate.isNullOrEmpty() or mEndDate.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Please set date", Toast.LENGTH_SHORT).show()
        } else if (topicText.isEmpty()) {
            Toast.makeText(requireContext(), "Please select a topic", Toast.LENGTH_SHORT).show()
        } else {
            val assignmentObject = JSONObject()
            val classArray = JSONArray()
            val attachmentArray = JSONArray()
            val assignmentArray = JSONArray()

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

            mFileList.isNotEmpty().let { isTrue ->
                if (isTrue) {
                    mFileList.forEach { attachment ->
                        JSONObject().apply {
                            put("name", attachment.name)
                            put("type", attachment.type)
                            put("uri", attachment.uri)
                        }.let {
                            attachmentArray.put(it)
                        }
                    }
                }
            }

            JSONObject().apply {
                put("levelId", mLevelId)
                put("courseId", mCourseId)
                put("title", titleText)
                put("description", descriptionText)
                put("startDate", mStartDate)
                put("endDate", mEndDate)
                put("startTime", mStartTime)
                put("endTime", mEndTime)
                put("topic", topicText)
            }.let {
                assignmentArray.put(it)
            }

            assignmentObject.apply {
                put("assignment", assignmentArray)
                put("class", classArray)

                if (attachmentArray.length() != 0) {
                    put("attachment", attachmentArray)
                }
            }


            updatedJson = assignmentObject.toString()
        }
    }

    private fun onExit() {
        mBackBtn.setOnClickListener {
            val json1 = JSONObject(jsonFromTopic!!)
            val json2 = JSONObject(updatedJson!!)

            if (json1.length() != 0 && json2.length() != 0) {
                val areContentSame = compareJsonObjects(json1, json2)

                if (areContentSame) {
                    dismiss()
                } else {
                    exitWarning()
                }

            } else {
                dismiss()
            }
        }
    }

    private fun exitWarning() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Are you sure to exit?")
            setMessage("Your unsaved changes will be lost")
            setPositiveButton("Yes") { _, _ ->
                dismiss()
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }.create()
    }

}
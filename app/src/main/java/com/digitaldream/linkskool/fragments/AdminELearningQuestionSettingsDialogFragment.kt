package com.digitaldream.linkskool.fragments

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
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.AdminELearningQuestionSettingsAdapter
import com.digitaldream.linkskool.config.DatabaseHelper
import com.digitaldream.linkskool.dialog.AdminELearningDatePickerDialog
import com.digitaldream.linkskool.models.ClassNameTable
import com.digitaldream.linkskool.models.TagModel
import com.digitaldream.linkskool.utils.FunctionUtils.compareJsonObjects
import com.digitaldream.linkskool.utils.FunctionUtils.formatDate2
import com.digitaldream.linkskool.utils.FunctionUtils.showSoftInput
import com.digitaldream.linkskool.utils.FunctionUtils.smoothScrollEditText
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import org.json.JSONArray
import org.json.JSONObject


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"


class AdminELearningQuestionSettingsDialogFragment :
    DialogFragment(R.layout.fragment_admin_e_learning_question_settings) {


    private lateinit var mBackBtn: ImageView
    private lateinit var mApplyBtn: Button
    private lateinit var mQuestionTitleEditText: EditText
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mSelectAllBtn: Button
    private lateinit var mEmptyClassTxt: TextView
    private lateinit var mDescriptionEditText: EditText
    private lateinit var mDateBtn: RelativeLayout
    private lateinit var mStartDateTxt: TextView
    private lateinit var mEndDateTxt: TextView
    private lateinit var mStartDateBtn: ImageView
    private lateinit var mEndDateBtn: ImageView
    private lateinit var mTopicTxt: TextView
    private lateinit var mDateSeparator: LinearLayout

    private var mClassList = mutableListOf<ClassNameTable>()
    private val selectedItems = hashMapOf<String, String>()
    private val mTagList = mutableListOf<TagModel>()

    private var mLevelId: String? = null
    private var mCourseId: String? = null
    private var mQuestionTitle: String? = null
    private var mQuestionDescription: String? = null
    private var mStartDate: String? = null
    private var mStartTime: String? = null
    private var mEndDate: String? = null
    private var mEndTime: String? = null
    private var mQuestionTopic: String? = null
    private var jsonFromQuestion: String? = null
    private var updatedSettings: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mLevelId = it.getString(ARG_PARAM1)
            mCourseId = it.getString(ARG_PARAM2)
            jsonFromQuestion = it.getString(ARG_PARAM3)
        }

    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String, param3: String = "") =
            AdminELearningQuestionSettingsDialogFragment().apply {
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
            mApplyBtn = findViewById(R.id.apply_btn)
            mQuestionTitleEditText = findViewById(R.id.questionTitle)
            mRecyclerView = findViewById(R.id.class_recyclerview)
            mSelectAllBtn = findViewById(R.id.selectAllBtn)
            mEmptyClassTxt = findViewById(R.id.emptyClassTxt)
            mDescriptionEditText = findViewById(R.id.description)
            mDateBtn = findViewById(R.id.dateBtn)
            mStartDateTxt = findViewById(R.id.startDateTxt)
            mEndDateTxt = findViewById(R.id.endDateTxt)
            mStartDateBtn = findViewById(R.id.startDateBtn)
            mEndDateBtn = findViewById(R.id.endDateBtn)
            mTopicTxt = findViewById(R.id.topicBtn)
            mDateSeparator = findViewById(R.id.separator)
        }

        onEdit()

        classList()

        setDate()

        onExit()

        showSoftInput(requireContext(), mQuestionTitleEditText)
        smoothScrollEditText(mQuestionTitleEditText)
        smoothScrollEditText(mDescriptionEditText)

        mApplyBtn.setOnClickListener {
            applySettings()
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

            if (selectedItems.isNotEmpty()) {
                mTagList.forEach { tagModel ->
                    if (selectedItems[tagModel.tagId] == tagModel.tagName)
                        tagModel.isSelected = true
                }
            }

            if (mTagList.isEmpty()) {
                mRecyclerView.isVisible = false
                mSelectAllBtn.isVisible = false
                mEmptyClassTxt.isVisible = true
            } else {
                AdminELearningQuestionSettingsAdapter(
                    selectedItems,
                    mTagList,
                    mSelectAllBtn
                ).let {
                    mRecyclerView.apply {
                        hasFixedSize()
                        layoutManager = LinearLayoutManager(
                            requireContext(),
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                        adapter = it

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

    private fun applySettings() {
        val titleText = mQuestionTitleEditText.text.toString().trim()
        val descriptionText = mDescriptionEditText.text.toString().trim()
        val topicText = mTopicTxt.text.toString()

        if (titleText.isEmpty()) {
            mQuestionTitleEditText.error = "Please enter question title"
        } else if (selectedItems.size == 0) {
            Toast.makeText(requireContext(), "Please select a class", Toast.LENGTH_SHORT).show()
        } else if (descriptionText.isEmpty()) {
            mDescriptionEditText.error = "Please enter a description"
        } else if (mStartDate.isNullOrEmpty() or mEndDate.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Please set date", Toast.LENGTH_SHORT).show()
        } else if (topicText.isEmpty()) {
            Toast.makeText(requireContext(), "Please select a topic", Toast.LENGTH_SHORT).show()
        } else {
            val settingsObject = JSONObject()
            val classArray = JSONArray()

            selectedItems.forEach { (key, value) ->
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
                settingsObject.put("settings", it)
                settingsObject.put("class", classArray)


                updatedSettings = settingsObject.toString()

                requireActivity().supportFragmentManager.commit {
                    replace(
                        R.id.learning_container, AdminELearningQuestionFragment.newInstance
                            (settingsObject.toString())
                    )
                }
            }
        }

    }

    private fun onEdit() {
        try {
            if (!jsonFromQuestion.isNullOrEmpty()) {
                jsonFromQuestion?.let {
                    JSONObject(it).run {
                        val settingsObject = getJSONObject("settings")
                        val classArray = getJSONArray("class")


                        settingsObject.let {

                        }

                        mQuestionTitle = settingsObject.getString("title")
                        mQuestionDescription = settingsObject.getString("description")
                        mStartDate = settingsObject.getString("startDate")
                        mEndDate = settingsObject.getString("endDate")
                        mStartTime = settingsObject.getString("startTime")
                        mEndTime = settingsObject.getString("endTime")
                        mQuestionTopic = settingsObject.getString("topic")

                        for (i in 0 until classArray.length()) {
                            selectedItems[classArray.getJSONObject(i).getString("id")] =
                                classArray.getJSONObject(i).getString("name")
                        }

                    }
                }

                mQuestionTitleEditText.setText(mQuestionTitle)
                mDescriptionEditText.setText(mQuestionDescription)
                mTopicTxt.text = mQuestionTopic

                val start = "Start ${formatDate2(mStartDate!!, "custom1")} $mStartTime"
                val end = "Due ${formatDate2(mEndDate!!, "custom1")} $mEndTime"
                mStartDateTxt.text = start
                mEndDateTxt.text = end

                showDate()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showDate() {
        mStartDateBtn.isVisible = true
        mEndDateBtn.isVisible = true
        mEndDateTxt.isVisible = true
        mDateSeparator.isVisible = true
    }

    private fun onExit() {
        mBackBtn.setOnClickListener {
            val json1 = JSONObject(jsonFromQuestion!!)
            val json2 = JSONObject(updatedSettings!!)

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
        androidx.appcompat.app.AlertDialog.Builder(requireContext()).apply {
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
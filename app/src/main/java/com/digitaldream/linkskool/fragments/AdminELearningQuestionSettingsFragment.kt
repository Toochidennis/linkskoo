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
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.AdminELearningQuestionSettingsAdapter
import com.digitaldream.linkskool.config.DatabaseHelper
import com.digitaldream.linkskool.dialog.AdminELearningDatePickerDialog
import com.digitaldream.linkskool.models.ClassNameTable
import com.digitaldream.linkskool.models.TagModel
import com.digitaldream.linkskool.utils.FunctionUtils.formatDate2
import com.digitaldream.linkskool.utils.FunctionUtils.showSoftInput
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AdminELearningQuestionSettingsFragment :
    Fragment(R.layout.fragment_admin_e_learning_question_settings) {


    private lateinit var mBackBtn: ImageView
    private lateinit var mApplyBtn: Button
    private lateinit var mQuestionTitleEditText: EditText
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mSelectAllBtn: Button
    private lateinit var mDescriptionEditText: EditText
    private lateinit var mDateBtn: RelativeLayout
    private lateinit var mStartDateTxt: TextView
    private lateinit var mEndDateTxt: TextView
    private lateinit var mStartDateBtn: ImageView
    private lateinit var mEndDateBtn: ImageView
    private lateinit var mTopicBtn: TextView
    private lateinit var mDateSeparator: LinearLayout


    private var mClassList = mutableListOf<ClassNameTable>()
    private val selectedItems = hashMapOf<String, String>()
    private val mTagList = mutableListOf<TagModel>()

    private var mLevelId: String? = null
    private var mCourseId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mLevelId = it.getString(ARG_PARAM1)
            mCourseId = it.getString(ARG_PARAM2)
        }
    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdminELearningQuestionSettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
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
            mDescriptionEditText = findViewById(R.id.description)
            mDateBtn = findViewById(R.id.dateBtn)
            mStartDateTxt = findViewById(R.id.startDateTxt)
            mEndDateTxt = findViewById(R.id.endDateTxt)
            mStartDateBtn = findViewById(R.id.startDateBtn)
            mEndDateBtn = findViewById(R.id.endDateBtn)
            mTopicBtn = findViewById(R.id.topicBtn)
            mDateSeparator = findViewById(R.id.separator)
        }

        classList()

        setDate()

        showSoftInput(requireContext(), mQuestionTitleEditText)

        mApplyBtn.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                replace(
                    R.id.learning_container, AdminELearningQuestionFragment.newInstance
                        (mLevelId!!, mCourseId!!)
                )
                addToBackStack(null)
            }
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


            if (mTagList.isEmpty()) {
                mRecyclerView.isVisible = false
                mSelectAllBtn.isVisible = false
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
                        isAnimating
                        adapter = it
                        isVisible = true

                        mSelectAllBtn.isVisible = true
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

                val start = "Start ${formatDate2(startDate, "custom1")} $startTime"
                val end = "Due ${formatDate2(endDate, "custom1")} $endTime"
                mStartDateTxt.text = start
                mEndDateTxt.text = end

                mStartDateBtn.isVisible = true
                mEndDateBtn.isVisible = true
                mEndDateTxt.isVisible = true
                mDateSeparator.isVisible = true

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
}
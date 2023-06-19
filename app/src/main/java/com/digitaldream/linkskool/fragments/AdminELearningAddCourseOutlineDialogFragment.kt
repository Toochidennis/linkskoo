package com.digitaldream.linkskool.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.AdminELearningCourseOutlineAdapter
import com.digitaldream.linkskool.config.DatabaseHelper
import com.digitaldream.linkskool.models.ClassNameTable
import com.digitaldream.linkskool.models.TagModel
import com.digitaldream.linkskool.models.TeachersTable
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager


private const val ARG_PARAM1 = "param1"


class AdminELearningAddCourseOutlineDialogFragment :
    DialogFragment(R.layout.dialog_fragment_admin_e_learning_add_course_outline) {

    private lateinit var mBackBtn: ImageView
    private lateinit var mDoneBtn: Button
    private lateinit var mCourseOutlineEditText: EditText
    private lateinit var mTagClassBtn: RadioButton
    private lateinit var mTagTeacherBtn: RadioButton
    private lateinit var mSelectAllBtn: Button
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mErrorMessageTxt: TextView
    private lateinit var mTagView: RelativeLayout

    private var mClassList = mutableListOf<ClassNameTable>()
    private var mTeacherList = mutableListOf<TeachersTable>()
    private val selectedItems = hashMapOf<String, String>()
    private val mTagList = mutableListOf<TagModel>()

    private lateinit var mDatabaseHelper: DatabaseHelper

    private var levelId: String? = null
    private var selectedTag = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)

        arguments?.let {
            levelId = it.getString(ARG_PARAM1)
        }
    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String) =
            AdminELearningAddCourseOutlineDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)

                }
            }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.apply {
            mBackBtn = findViewById(R.id.backBtn)
            mDoneBtn = findViewById(R.id.doneBtn)
            mCourseOutlineEditText = findViewById(R.id.courseOutlineEditText)
            mTagClassBtn = findViewById(R.id.tagClassButton)
            mTagTeacherBtn = findViewById(R.id.tagTeacherButton)
            mSelectAllBtn = findViewById(R.id.selectAllBtn)
            mRecyclerView = findViewById(R.id.tagRecyclerView)
            mErrorMessageTxt = findViewById(R.id.errorTxt)
            mTagView = findViewById(R.id.tagView)
        }

        mDatabaseHelper = DatabaseHelper(requireContext())

        mTagClassBtn.setOnClickListener {
            tagList("class")
            selectedTag = 0
        }

        mTagTeacherBtn.setOnClickListener {
            tagList("teacher")
            selectedTag = 1
        }
    }


    private fun tagList(from: String) {
        try {
            selectedItems.clear()
            mTagList.clear()

            mSelectAllBtn.apply {
                setBackgroundResource(R.drawable.ripple_effect6)
                isSelected = false
                setTextColor(Color.BLACK)
            }

            if (from == "class") {
                val dao: Dao<ClassNameTable, Long> = DaoManager.createDao(
                    mDatabaseHelper.connectionSource, ClassNameTable::class.java
                )

                mClassList = dao.queryBuilder().where().eq("level", levelId).query()

                mClassList.sortBy { it.className }

                mClassList.forEach { item ->
                    mTagList.add(TagModel(item.classId, item.className))
                }
            } else {
                val dao: Dao<TeachersTable, Long> = DaoManager.createDao(
                    mDatabaseHelper
                        .connectionSource, TeachersTable::class.java
                )

                mTeacherList = dao.queryForAll()
                mTeacherList.sortBy { it.staffFirstname }

                mTeacherList.forEach { item ->
                    item.staffFullName =
                        "${item.staffSurname} ${item.staffMiddlename} ${item.staffFirstname}"
                    mTagList.add(TagModel(item.staffId, item.staffFullName))
                }
            }

            if (mTagList.isEmpty()) {
                mRecyclerView.isVisible = false
                mErrorMessageTxt.isVisible = true
                mSelectAllBtn.isVisible = false
                mTagView.isVisible = true
            } else {
                AdminELearningCourseOutlineAdapter(
                    requireContext(),
                    selectedItems,
                    mTagList,
                    mSelectAllBtn
                ).let {
                    mRecyclerView.apply {
                        hasFixedSize()
                        layoutManager = LinearLayoutManager(requireContext())
                        isAnimating
                        adapter = it
                        isVisible = true

                        mErrorMessageTxt.isVisible = false
                        mSelectAllBtn.isVisible = true
                        mTagView.isVisible = true
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun buttonAction() {

    }
}
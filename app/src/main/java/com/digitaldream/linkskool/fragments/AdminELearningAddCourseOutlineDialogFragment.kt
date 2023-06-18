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
import com.digitaldream.linkskool.adapters.OnTagItemListener
import com.digitaldream.linkskool.config.DatabaseHelper
import com.digitaldream.linkskool.models.ClassNameTable
import com.digitaldream.linkskool.models.TeachersTable
import com.digitaldream.linkskool.utils.FunctionUtils
import com.digitaldream.linkskool.utils.FunctionUtils.flipAnimation
import com.digitaldream.linkskool.utils.FunctionUtils.onItemClick2
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager


private const val ARG_PARAM1 = "param1"


class AdminELearningAddCourseOutlineDialogFragment :
    DialogFragment(R.layout.dialog_fragment_admin_e_learning_add_course_outline),
    OnTagItemListener {

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

    private var classItemPosition = ClassNameTable()
    private var teacherItemPosition = TeachersTable()

    private lateinit var mDatabaseHelper: DatabaseHelper
    private lateinit var mAdapter: AdminELearningCourseOutlineAdapter

    private var levelId: String? = null


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
            tagClass()


        }

        mTagTeacherBtn.setOnClickListener {
            tagTeacher()

        }

    }


    private fun tagClass() {
        try {
            selectedItems.clear()
            mTeacherList.clear()

            val dao: Dao<ClassNameTable, Long> = DaoManager.createDao(
                mDatabaseHelper.connectionSource, ClassNameTable::class.java
            )

            mClassList = dao.queryBuilder().where().eq("level", levelId).query()

            mClassList.sortBy { it.className }

            if (mClassList.isEmpty()) {
                mRecyclerView.isVisible = false
                mErrorMessageTxt.isVisible = true
                mSelectAllBtn.isVisible = false
                mTagView.isVisible = true
            } else {

                mAdapter = AdminELearningCourseOutlineAdapter(
                    mClassList,
                    null,
                    this
                )

                mRecyclerView.apply {
                    hasFixedSize()
                    layoutManager = LinearLayoutManager(requireContext())
                    isAnimating
                    adapter = mAdapter
                    isVisible = true

                    mErrorMessageTxt.isVisible = false
                    mSelectAllBtn.isVisible = true
                    mTagView.isVisible = true
                }

            }

        } catch (e: Exception) {
            e.printStackTrace()

        }
    }


    private fun tagTeacher() {
        try {
            selectedItems.clear()
            mClassList.clear()

            val dao: Dao<TeachersTable, Long> = DaoManager.createDao(
                mDatabaseHelper
                    .connectionSource, TeachersTable::class.java
            )

            mTeacherList = dao.queryForAll()

            mTeacherList.sortBy { it.staffFirstname }

            if (mTeacherList.isEmpty()) {
                mRecyclerView.isVisible = false
                mErrorMessageTxt.isVisible = true
                mSelectAllBtn.isVisible = false
                mTagView.isVisible = true
            } else {
                mAdapter = AdminELearningCourseOutlineAdapter(
                    null,
                    mTeacherList,
                    this
                )

                mRecyclerView.apply {
                    hasFixedSize()
                    layoutManager = LinearLayoutManager(requireContext())
                    isAnimating
                    adapter = mAdapter
                    isVisible = true

                    mErrorMessageTxt.isVisible = false
                    mSelectAllBtn.isVisible = true
                    mTagView.isVisible = true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onTagClick(holder: AdminELearningCourseOutlineAdapter.ViewHolder) {
        holder.itemView.setOnClickListener {
            if (mClassList.isNotEmpty()) {
                classItemPosition = mClassList[holder.adapterPosition]
                onItemClick2(
                    requireContext(),
                    classItemPosition,
                    selectedItems,
                    holder.itemTextLayout,
                    holder.itemImageLayout,
                    mSelectAllBtn,
                )
            } else {
                teacherItemPosition = mTeacherList[holder.adapterPosition]
                onItemClick2(
                    requireContext(),
                    teacherItemPosition,
                    selectedItems,
                    holder.itemTextLayout,
                    holder.itemImageLayout,
                    mSelectAllBtn,
                )
            }
        }

        mSelectAllBtn.setOnClickListener {
            selectAll(holder.itemTextLayout, holder.itemImageLayout)
        }
    }

    private fun selectAll(frontView: View, backView: View) {
        val isAllSelected: Boolean

        if (mClassList.isNotEmpty()) {
            isAllSelected = selectedItems.size == mClassList.size

            if (isAllSelected) {
                mSelectAllBtn.apply {
                    setBackgroundResource(R.drawable.ripple_effect6)
                    setTextColor(Color.BLACK)
                }

                for (position in 0 until mAdapter.itemCount) {
                    flipAnimation(requireContext(), frontView, backView, "left")
                }
                selectedItems.clear()
            } else {
                mSelectAllBtn.apply {
                    setBackgroundResource(R.drawable.ripple_effect10)
                    setTextColor(Color.WHITE)
                }

                for (position in 0 until mAdapter.itemCount) {
                    flipAnimation(requireContext(), frontView, backView, "right")
                    val itemPosition = mClassList[position]
                    val itemId=  itemPosition.classId
                    val itemName = itemPosition.className

                    selectedItems[itemId] = itemName
                    println(selectedItems)
                }
            }
        }

    }
}
package com.digitaldream.winskool.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.winskool.R
import com.digitaldream.winskool.activities.PaymentActivity
import com.digitaldream.winskool.adapters.OnItemClickListener
import com.digitaldream.winskool.adapters.ReceiptStudentNameAdapter
import com.digitaldream.winskool.config.DatabaseHelper
import com.digitaldream.winskool.models.StudentTable
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"


class ReceiptStudentNameFragment : Fragment(), OnItemClickListener {

    private lateinit var mMainView: NestedScrollView
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mErrorMessage: TextView
    private lateinit var mSearchBar: EditText

    private var classId: String? = null
    private var className: String? = null
    private var levelName: String? = null
    private var mStudentList = mutableListOf<StudentTable>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            classId = it.getString(ARG_PARAM1)
            className = it.getString(ARG_PARAM2)
            levelName = it.getString(ARG_PARAM3)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(sClassId: String, sClassName: String, sLevelName: String) =
            ReceiptStudentNameFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, sClassId)
                    putString(ARG_PARAM2, sClassName)
                    putString(ARG_PARAM3, sLevelName)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_receipt_student_name, container, false)

        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        mMainView = view.findViewById(R.id.student_name_view)
        mRecyclerView = view.findViewById(R.id.student_name_recycler)
        mErrorMessage = view.findViewById(R.id.student_error_message)
        mSearchBar = view.findViewById(R.id.search_bar)

        toolbar.apply {
            title = "Select Student from $className"
            setNavigationIcon(R.drawable.arrow_left)
            setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }


        getStudentNames()

        return view
    }

    private fun getStudentNames() {
        try {
            val databaseHelper =
                DatabaseHelper(requireContext())
            val mDao: Dao<StudentTable, Long> = DaoManager.createDao(
                databaseHelper.connectionSource, StudentTable::class.java
            )
            mStudentList = mDao.queryBuilder().where().eq("studentClass", classId).query()

            if (mStudentList.isEmpty()) {
                mMainView.isVisible = true
                mErrorMessage.isVisible = true
                mSearchBar.isVisible = false
            } else {
                val mAdapter = ReceiptStudentNameAdapter(mStudentList, this)
                mRecyclerView.hasFixedSize()
                mRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                mRecyclerView.adapter = mAdapter
                mMainView.isVisible = true
                mErrorMessage.isVisible = false
                mSearchBar.isVisible = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onItemClick(position: Int) {
        val studentTable = mStudentList[position]
        startActivity(
            Intent(context, PaymentActivity::class.java)
                .putExtra("levelId", studentTable.studentLevel)
                .putExtra("classId", studentTable.studentClass)
                .putExtra("studentId", studentTable.studentId)
                .putExtra("reg_no", studentTable.studentReg_no)
                .putExtra("level_name", levelName)
                .putExtra("from", "receipt_student_name")
        )

    }
}
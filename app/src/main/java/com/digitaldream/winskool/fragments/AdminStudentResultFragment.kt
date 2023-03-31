package com.digitaldream.winskool.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.winskool.R
import com.digitaldream.winskool.adapters.AdminStudentResultAdapter
import com.digitaldream.winskool.adapters.OnItemClickListener
import com.digitaldream.winskool.config.DatabaseHelper
import com.digitaldream.winskool.models.AdminResultDashboardModel
import com.digitaldream.winskool.models.ChartModel
import com.digitaldream.winskool.models.ClassNameTable
import com.digitaldream.winskool.models.StudentTable
import com.digitaldream.winskool.utils.FunctionUtils.capitaliseFirstLetter
import com.digitaldream.winskool.utils.FunctionUtils.plotLineChart2
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter


private const val ARG_PARAM1 = "param1"

class AdminStudentResultFragment : Fragment(), OnItemClickListener {


    private lateinit var mRefreshBtn: Button
    private lateinit var mAdapter: SectionedRecyclerViewAdapter

    private var mStudentList = mutableListOf<StudentTable>()
    private var mStudentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mStudentId = it.getString(ARG_PARAM1)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String) =
            AdminStudentResultFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_admin_student_result, container, false)

        val toolbar: Toolbar = view.findViewById(R.id.toolbar)

        toolbar.apply {
            title = "Student Result"
            setNavigationIcon(R.drawable.arrow_left)
            setNavigationOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        }
        mAdapter = SectionedRecyclerViewAdapter()

        studentProfile(view)

        return view
    }


    private fun studentProfile(sView: View) {
        val profileImage: ImageView = sView.findViewById(R.id.profile_image)
        val studentName: TextView = sView.findViewById(R.id.student_name)
        val studentClass: TextView = sView.findViewById(R.id.student_class)
        val studentId: TextView = sView.findViewById(R.id.student_id)

        try {
            val databaseHelper = DatabaseHelper(requireContext())
            val studentDao: Dao<StudentTable, Long> = DaoManager.createDao(
                databaseHelper.connectionSource, StudentTable::class.java
            )
            mStudentList = studentDao.queryBuilder().where().eq("studentId", mStudentId).query()

            val classDao: Dao<ClassNameTable, Long> = DaoManager.createDao(
                databaseHelper.connectionSource, ClassNameTable::class.java
            )
            val classList = classDao.queryBuilder().where().eq(
                "classId", mStudentList[0].studentClass
            ).query()


            if (mStudentList.isNotEmpty() && classList.isNotEmpty()) {
                val model = mStudentList[0]
                val surName = model.studentSurname
                val middleName = model.studentMiddlename
                val firstName = model.studentFirstname
                val name = "$surName $middleName $firstName"

                studentName.text = capitaliseFirstLetter(name)
                studentId.text = model.studentReg_no
                studentClass.text = classList[0].className
            }

            getTerms(sView)

        } catch (sE: java.lang.Exception) {
            sE.printStackTrace()
        }

    }

    private fun getTerms(sView: View) {
        val recyclerView: RecyclerView = sView.findViewById(R.id.term_recycler)
        val errorView: LinearLayout = sView.findViewById(R.id.error_view)
        val errorMessage: TextView = sView.findViewById(R.id.term_error_message)
        val errorImage: ImageView = sView.findViewById(R.id.error_image)
        val termList = mutableListOf<AdminResultDashboardModel>()

        termList.add(AdminResultDashboardModel("First Term", "2023"))
        termList.add(AdminResultDashboardModel("Second Term", "2023"))
        termList.add(AdminResultDashboardModel("Third Term", "2023"))

        mAdapter.removeAllSections()
        mAdapter.addSection(AdminStudentResultAdapter(termList, "2022/2023 Session", this))
        mAdapter.addSection(AdminStudentResultAdapter(termList, "2023/2024 Session", this))

        recyclerView.apply {
            hasFixedSize()
            isAnimating
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }

        recyclerView.isVisible = true
        errorView.isVisible = false
        errorMessage.isVisible = false
        errorImage.isVisible = false

        plotGraph(sView)
    }

    private fun plotGraph(sView: View) {
        val graph: LinearLayout = sView.findViewById(R.id.chart)
        val graphList = arrayListOf<ChartModel>()
        graphList.add(ChartModel("80.10", "First term"))
        graphList.add(ChartModel("60.76", "Second term"))
        graphList.add(ChartModel("75.46", "Third term"))

        val graphicalView = plotLineChart2(requireContext(), graphList, "Average", "Terms")
        graph.addView(graphicalView)
    }


    override fun onItemClick(position: Int) {
        Toast.makeText(requireContext(), ":)", Toast.LENGTH_SHORT).show()
    }

}
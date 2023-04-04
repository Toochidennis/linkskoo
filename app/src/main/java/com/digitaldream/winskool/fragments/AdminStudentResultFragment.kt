package com.digitaldream.winskool.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.winskool.R
import com.digitaldream.winskool.activities.Login
import com.digitaldream.winskool.adapters.AdminStudentResultAdapter
import com.digitaldream.winskool.adapters.OnItemClickListener
import com.digitaldream.winskool.config.DatabaseHelper
import com.digitaldream.winskool.dialog.AdminResultStudentNamesDialog
import com.digitaldream.winskool.dialog.OnInputListener
import com.digitaldream.winskool.models.AdminStudentResultFragmentModel
import com.digitaldream.winskool.models.ChartModel
import com.digitaldream.winskool.models.ClassNameTable
import com.digitaldream.winskool.models.StudentTable
import com.digitaldream.winskool.utils.FunctionUtils.capitaliseFirstLetter
import com.digitaldream.winskool.utils.FunctionUtils.plotLineChart2
import com.digitaldream.winskool.utils.FunctionUtils.requestToServer
import com.digitaldream.winskool.utils.VolleyCallback
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import org.json.JSONObject
import java.util.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AdminStudentResultFragment : Fragment(), OnItemClickListener {


    private lateinit var mRefreshBtn: Button
    private lateinit var mStudentNameBtn: Button
    private lateinit var mAdapter: SectionedRecyclerViewAdapter

    private var mStudentList = mutableListOf<StudentTable>()
    private var mTermList = mutableListOf<AdminStudentResultFragmentModel>()
    private var mStudentId: String? = null
    private var mClassId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mStudentId = it.getString(ARG_PARAM1)
            mClassId = it.getString(ARG_PARAM2)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdminStudentResultFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_admin_student_result, container, false)

        val backBtn: ImageView = view.findViewById(R.id.back_btn)
        val title: TextView = view.findViewById(R.id.toolbar_text)
        mStudentNameBtn = view.findViewById(R.id.student_name_btn)

        "Student Result".also { title.text = it }

        backBtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        mAdapter = SectionedRecyclerViewAdapter()

        studentProfile(view, mStudentId!!)

        changeStudent(view)

        refresh(view)

        return view
    }

    private fun changeStudent(sView: View) {
        mStudentNameBtn.setOnClickListener {
            AdminResultStudentNamesDialog(requireContext(), mClassId!!, "student_result", object :
                OnInputListener {
                override fun sendInput(input: String) {
                    studentProfile(sView, input)
                }

                override fun sendId(levelId: String) {

                }
            }).apply {
                setCancelable(true)
                show()
            }.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private fun studentProfile(sView: View, sStudentId: String) {
        val profileImage: ImageView = sView.findViewById(R.id.profile_image)
        val studentName: TextView = sView.findViewById(R.id.student_name)
        val studentClass: TextView = sView.findViewById(R.id.student_class)
        val studentId: TextView = sView.findViewById(R.id.student_id)

        try {
            mStudentList.clear()
            val databaseHelper = DatabaseHelper(requireContext())
            val studentDao: Dao<StudentTable, Long> = DaoManager.createDao(
                databaseHelper.connectionSource, StudentTable::class.java
            )
            mStudentList = studentDao.queryBuilder().where().eq("studentId", sStudentId).query()

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
                mStudentNameBtn.text = classList[0].className
            }

            getTerms(sView, sStudentId)

        } catch (sE: java.lang.Exception) {
            sE.printStackTrace()
        }

    }

    private fun getTerms(sView: View, sStudentId: String) {
        val recyclerView: RecyclerView = sView.findViewById(R.id.term_recycler)
        val errorMessage: TextView = sView.findViewById(R.id.term_error_message)
        val errorImage: ImageView = sView.findViewById(R.id.error_image)

        val url = "${Login.urlBase}/studentTerms.php"
        val hashMap = hashMapOf<String, String>()
        hashMap["id"] = sStudentId

        requestToServer(Request.Method.POST, url, requireContext(), hashMap,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    try {
                        mAdapter.removeAllSections()
                        mTermList.clear()

                        val graphList = arrayListOf<ChartModel>()

                        JSONObject(response).also {

                            val termObject = it.getJSONObject("terms")
                            val key = termObject.keys()
                            while (key.hasNext()) {
                                val year = key.next()
                                val sessionObject = termObject.getJSONObject(year)

                                val previousYear = year.toInt() - 1
                                val session = String.format(
                                    Locale.getDefault(), "%d/%s",
                                    previousYear, year
                                )

                                for (i in sessionObject.keys()) {
                                    val term = when (i) {
                                        "1" -> "First Term"
                                        "2" -> "Second Term"
                                        "3" -> "Third Term"
                                        else -> ""
                                    }
                                    mTermList.add(
                                        AdminStudentResultFragmentModel(
                                            term, year,
                                            sessionObject.getString(i)
                                        )
                                    )

                                    graphList.add(ChartModel(sessionObject.getString(i), term))
                                }

                                mTermList.sortBy { t -> t.term }
                                graphList.sortBy { v -> v.horizontalValues }

                                mAdapter.addSection(
                                    AdminStudentResultAdapter(
                                        mTermList,
                                        "$session Session", this@AdminStudentResultFragment
                                    )
                                )

                            }

                            plotGraph(sView, graphList)

                            recyclerView.apply {
                                hasFixedSize()
                                isAnimating
                                layoutManager = LinearLayoutManager(requireContext())
                                adapter = mAdapter
                            }

                            recyclerView.isVisible = true
                            errorMessage.isVisible = false
                            errorImage.isVisible = false
                            mRefreshBtn.isVisible = false

                        }

                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                        recyclerView.isVisible = false
                        errorMessage.isVisible = true
                        errorImage.isVisible = true
                        mRefreshBtn.isVisible = false
                    }

                }

                override fun onError(error: VolleyError) {
                    recyclerView.isVisible = false
                    errorMessage.isVisible = true
                    errorMessage.text = getString(R.string.can_not_retrieve)
                    errorImage.isVisible = true
                    mRefreshBtn.isVisible = true
                }
            })

    }

    private fun plotGraph(sView: View, graphValues: ArrayList<ChartModel>) {
        val graph: LinearLayout = sView.findViewById(R.id.chart)

        val graphicalView =
            plotLineChart2(
                requireContext(), graphValues,
                "Average", "Terms"
            )

        graph.addView(graphicalView)
    }

    private fun refresh(sView: View) {
        mRefreshBtn.setOnClickListener {
            studentProfile(sView, mStudentId!!)
        }
    }


    override fun onItemClick(position: Int) {
        Toast.makeText(requireContext(), ":)", Toast.LENGTH_SHORT).show()
    }

}
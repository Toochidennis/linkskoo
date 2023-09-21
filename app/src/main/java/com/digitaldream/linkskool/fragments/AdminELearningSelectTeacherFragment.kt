package com.digitaldream.linkskool.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.AdminELearningSelectTeachersAdapter
import com.digitaldream.linkskool.models.TagModel


class AdminELearningSelectTeacherFragment(
    private var selectedTeachers: HashMap<String, String>,
    private val onSelected: (HashMap<String, String>) -> Unit
) : DialogFragment(R.layout.fragment_admin_e_learning_select_teacher) {

    // Define UI elements
    private lateinit var backBtn: ImageButton
    private lateinit var doneBtn: Button
    private lateinit var selectAllLayout: RelativeLayout
    private lateinit var selectAllStateLayout: LinearLayout
    private lateinit var teacherRecyclerView: RecyclerView

    private val tagList = mutableListOf<TagModel>()
    private var selectedTeachersCopy = hashMapOf<String, String>()

    private lateinit var teachersAdapter: AdminELearningSelectTeachersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        setUpDataset()

        doneAction()

        backBtn.setOnClickListener {
            dismiss()
        }
    }

    private fun setUpViews(view: View) {
        view.apply {
            backBtn = findViewById(R.id.backBtn)
            doneBtn = findViewById(R.id.doneBtn)
            selectAllLayout = findViewById(R.id.selectAllLayout)
            selectAllStateLayout = findViewById(R.id.selectedStateLayout)
            teacherRecyclerView = findViewById(R.id.teacherRecyclerView)
        }
    }

    private fun setUpDataset() {
        try {
            selectAllLayout.isSelected = false
            selectedTeachersCopy = selectedTeachers

            selectedTeachersCopy.forEach { (teacherId, teacherName) ->
                tagList.add(TagModel(teacherId, teacherName))
            }

            setUpRecyclerView()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun setUpRecyclerView() {
        teachersAdapter = AdminELearningSelectTeachersAdapter(
            tagList, selectedTeachers,
            selectAllLayout, selectAllStateLayout
        )

        teacherRecyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(requireContext())
            adapter = teachersAdapter
        }
    }

    private fun doneAction(){
        doneBtn.setOnClickListener {
            onSelected(selectedTeachers)
            it.isEnabled = false
            dismiss()
        }
    }
}
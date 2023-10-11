package com.digitaldream.toyibatskool.fragments

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.digitaldream.toyibatskool.R
import com.digitaldream.toyibatskool.models.SharedViewModel


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AdminELearningAssignmentStudentWorkFragment :
    Fragment(R.layout.fragment_admin_e_learning_assignment_student_work) {

    private lateinit var markedLayout: LinearLayout
    private lateinit var markedButton: CheckBox

    private lateinit var sharedViewModel: SharedViewModel

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdminELearningAssignmentStudentWorkFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

    }

    private fun setUpViews(view: View) {
        view.apply {
            markedLayout = findViewById(R.id.markedLayout)
            markedButton = findViewById(R.id.markedButton)
        }

        markedLayout.setOnClickListener {
            if (markedButton.isChecked) {
                markedButton.isChecked = false
                sharedViewModel.hideCustomActionBar()
            } else {
                markedButton.isChecked = true
                sharedViewModel.showCustomActionBar()
            }
        }

    }


}
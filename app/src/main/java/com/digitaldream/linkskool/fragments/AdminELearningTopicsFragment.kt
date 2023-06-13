package com.digitaldream.linkskool.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.dialog.AdminELearningCreateDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AdminELearningTopicsFragment : Fragment(R.layout.fragment_admin_e_learning_topics) {

    private var param1: String? = null
    private var param2: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdminELearningTopicsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.apply {
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            val addBtn: FloatingActionButton = findViewById(R.id.add_btn)
            toolbar.apply {
                title = "Topics"
                setNavigationIcon(R.drawable.arrow_left)
                setNavigationOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
            }

            addBtn.setOnClickListener {
                AdminELearningCreateDialog(requireContext()).apply {
                    setCancelable(true)
                    show()
                }.window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
        }
    }
}
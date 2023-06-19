package com.digitaldream.linkskool.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.RelativeLayout
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.dialog.AdminELearningAttachmentDialog


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AdminELearningAssignmentFragment : Fragment(R.layout.fragment_admin_e_learning_assignment) {


    private var mLeveId: String? = null
    private var mCourseId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mLeveId = it.getString(ARG_PARAM1)
            mCourseId = it.getString(ARG_PARAM2)
        }
    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdminELearningAssignmentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.apply {
            val attach: RelativeLayout = findViewById(R.id.attachment_btn)

            attach.setOnClickListener {
                AdminELearningAttachmentDialog().show(parentFragmentManager, "")
            }
        }
    }
}
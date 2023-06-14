package com.digitaldream.linkskool.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.activities.ELearningActivity


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AdminELearningQuestionSettingsFragment : Fragment(R.layout.fragment_admin_e_learning_question_settings) {

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
            val btn: Button = findViewById(R.id.apply_btn)
            btn.setOnClickListener {
                requireActivity().supportFragmentManager.commit {
                    replace(
                        R.id.learning_container,
                        AdminELearningQuestionFragment()
                    )
                }


            }

        }

    }

}
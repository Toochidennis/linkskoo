package com.digitaldream.linkskool.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R

private const val ARG_PARAM1 = "param1"

class AdminELearningSelectTopicDialogFragment :
    DialogFragment(R.layout.fragment_admin_e_learning_select_topic) {

    private lateinit var backBtn: ImageView
    private lateinit var doneBtn: Button
    private lateinit var noTopicBtn: TextView
    private lateinit var newTopicEditText: EditText
    private lateinit var topicRecyclerView: RecyclerView


    private var existingTopic: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            existingTopic = it.getString(ARG_PARAM1)
        }
    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String) =
            AdminELearningSelectTopicDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.apply {
            backBtn = findViewById(R.id.backBtn)
            doneBtn = findViewById(R.id.doneBtn)
            noTopicBtn = findViewById(R.id.noTopicBtn)
            newTopicEditText = findViewById(R.id.newTopicEditText)
            topicRecyclerView = findViewById(R.id.topicRecyclerview)
        }

        if (!existingTopic.isNullOrEmpty()){
            newTopicEditText.setText(existingTopic)
            checkEditText()
        }

    }

    private fun newTopicAction() {
        if (newTopicEditText.isSelected){
            checkEditText()
        }
    }

    private fun checkEditText() {
        newTopicEditText.setCompoundDrawablesWithIntrinsicBounds(
            null, null,
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_check_),
            null
        )
    }

    private fun unCheckEditText() {
        newTopicEditText.setCompoundDrawablesWithIntrinsicBounds(
            null, null, null, null
        )
    }

    private fun setDrawableOnTextView(textView: TextView) {
        textView.setCompoundDrawablesWithIntrinsicBounds(
            null, null,
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_check_),
            null
        )
    }

    private fun removeDrawableOnTextView(textView: TextView) {
        textView.setCompoundDrawablesWithIntrinsicBounds(
            null, null, null, null
        )
    }


}
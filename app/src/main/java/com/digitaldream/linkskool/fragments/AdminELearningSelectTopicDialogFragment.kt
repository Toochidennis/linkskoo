package com.digitaldream.linkskool.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.GenericAdapter2
import com.digitaldream.linkskool.models.TopicModel


class AdminELearningSelectTopicDialogFragment(
    private val isTopicSelected: (topic: String?) -> Unit
) : DialogFragment(R.layout.fragment_admin_e_learning_select_topic) {

    private lateinit var backBtn: ImageView
    private lateinit var doneBtn: Button
    private lateinit var noTopicBtn: TextView
    private lateinit var newTopicEditText: EditText
    private lateinit var topicRecyclerView: RecyclerView

    private lateinit var topicAdapter: GenericAdapter2<TopicModel>
    private val topicList = mutableListOf<TopicModel>()

    private var existingTopic: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
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

        /*val sharedPreferences =
            requireContext().getSharedPreferences("loginDetail", Context.MODE_PRIVATE)*/

        newTopicEditText.apply {
            if (!existingTopic.isNullOrEmpty()) {
                newTopicEditText.setText(existingTopic)
                checkEditText()
            } else {
                unCheckEditText()
            }

        }

        backBtn.setOnClickListener {
            dismiss()
        }

        doneBtn.setOnClickListener {
            handleDoneButton()
        }

        topicAdapter()
        handleNewTopicSelection()

        handleNoTopicSelection()

    }

    private fun checkEditText() {
        newTopicEditText.apply {
            isSelected = true
            setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_add_black),
                null,
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_check_),
                null
            )
        }
    }

    private fun unCheckEditText() {
        newTopicEditText.apply {
            setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_add_black),
                null, null, null
            )
            isSelected = false
            clearFocus()
        }
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

    private fun topicAdapter() {
        topicAdapter = GenericAdapter2(
            topicList,
            R.layout.item_fragment_select_topic,
            bindItem = { itemView, model, _ ->
                val topicTxt: TextView = itemView.findViewById(R.id.topicTxt)
                topicTxt.text = model.topic

                val isSelected = existingTopic == model.topic
                itemView.isSelected = isSelected

                if (isSelected) {
                    setDrawableOnTextView(topicTxt)
                } else {
                    removeDrawableOnTextView(topicTxt)
                }

                itemView.setOnClickListener {
                    handleTopicSelection(model.topic)
                }

            }
        )

        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        topicRecyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(requireContext())
            adapter = topicAdapter
        }
    }

    private fun handleTopicSelection(newSelectedTopic: String) {
        if (newSelectedTopic != existingTopic) {
            existingTopic = newSelectedTopic
            removeDrawableOnTextView(noTopicBtn)
            unCheckEditText()
            topicAdapter.notifyDataSetChanged()
        }

    }

    private fun handleNewTopicSelection() {
        newTopicEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                removeDrawableOnTextView(noTopicBtn)
                existingTopic = null
                checkEditText()
                topicAdapter.notifyDataSetChanged()
            }
        }

    }

    private fun handleNoTopicSelection() {
        noTopicBtn.setOnClickListener {
            setDrawableOnTextView(noTopicBtn)
            existingTopic = noTopicBtn.text.toString()
            unCheckEditText()
            topicAdapter.notifyDataSetChanged()
        }
    }

    private fun handleDoneButton() {
        if (newTopicEditText.isSelected and newTopicEditText.text.toString().isNotBlank()) {
            existingTopic = newTopicEditText.text.toString()
            isTopicSelected(existingTopic)
            dismiss()
        } else if (!existingTopic.isNullOrEmpty()) {
            isTopicSelected(existingTopic)
            dismiss()
        } else {
            Toast.makeText(requireContext(), "Please select a topic", Toast.LENGTH_SHORT).show()
        }
    }

}
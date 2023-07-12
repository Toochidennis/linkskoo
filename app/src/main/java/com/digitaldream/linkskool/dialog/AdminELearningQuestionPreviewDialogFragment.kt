package com.digitaldream.linkskool.dialog

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.AdminELearningQuestionPreviewAdapter
import com.digitaldream.linkskool.models.QuestionItem
import com.digitaldream.linkskool.models.SectionModel
import java.io.Serializable

private const val ARG_PARAM = "section"

class AdminELearningQuestionPreviewDialogFragment :
    DialogFragment(R.layout.fragment_admin_e_learning_question_preview) {


    private lateinit var dismissBtn: ImageView
    private lateinit var timeTxt: TextView
    private lateinit var sectionTxt: TextView
    private lateinit var questionRecyclerView: RecyclerView
    private lateinit var previousBtn: Button
    private lateinit var nextBtn: Button
    private lateinit var sectionItems: MutableList<SectionModel>

    private var currentSectionIndex: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)

        arguments?.let {bundle->
            (bundle.getSerializable(ARG_PARAM) as? MutableList<SectionModel>
                ?: mutableListOf()).also { sectionItems = it }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(sectionItem: MutableList<SectionModel>) =
            AdminELearningQuestionPreviewDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM, sectionItem as Serializable)
                }
            }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.apply {
            dismissBtn = findViewById(R.id.dismissBtn)
            timeTxt = findViewById(R.id.timeTxt)
            sectionTxt = findViewById(R.id.sectionTxt)
            questionRecyclerView = findViewById(R.id.questionRecyclerView)
            previousBtn = findViewById(R.id.prevBtn)
            nextBtn = findViewById(R.id.nextBtn)
        }

        showQuestion()

        previousBtn.setOnClickListener {
            showPreviousQuestion()
        }

        nextBtn.setOnClickListener {
            showNextQuestion()
        }

    }


    private fun showQuestion() {
        val currentSection = sectionItems.getOrNull(currentSectionIndex)
        if (currentSection != null) {
            sectionTxt.text = currentSection.sectionTitle
            sectionTxt.isVisible = !currentSection.sectionTitle.isNullOrEmpty()

            val questionItem = currentSection.questionItem
            questionRecyclerView.isVisible = questionItem != null

            if (questionItem != null) {
                showQuestionPreview(questionItem)
            }
            updateNavigationButtons()

        }

    }

    private fun updateNavigationButtons() =
        if (sectionItems.size == 1) {
            disableNextButton()
            disablePreviousButton()
        } else if (currentSectionIndex == 0) {
            enableNextButton()
            disablePreviousButton()
        } else if (currentSectionIndex == sectionItems.size - 1) {
            disableNextButton()
            enablePreviousButton()
        } else {
            enableNextButton()
            enablePreviousButton()
        }


    private fun showNextQuestion() {
        if (currentSectionIndex < sectionItems.size) {
            currentSectionIndex++
            showQuestion()
        }
    }

    private fun showPreviousQuestion() {
        if (currentSectionIndex > 0) {
            currentSectionIndex--
            showQuestion()
        }
    }

    private fun showQuestionPreview(nextQuestion: QuestionItem?) {
        AdminELearningQuestionPreviewAdapter(mutableListOf(nextQuestion)).let {
            questionRecyclerView.apply {
                hasFixedSize()
                layoutManager = LinearLayoutManager(requireContext())
                adapter = it
            }
        }
    }

    private fun disablePreviousButton() {
        // Disable the previous button
        previousBtn.isEnabled = false
    }

    private fun enablePreviousButton() {
        // Enable the previous button
        previousBtn.isEnabled = true
    }

    private fun disableNextButton() {
        // Disable the next button
        nextBtn.isEnabled = false
    }

    private fun enableNextButton() {
        // Enable the next button
        nextBtn.isEnabled = true
    }


}

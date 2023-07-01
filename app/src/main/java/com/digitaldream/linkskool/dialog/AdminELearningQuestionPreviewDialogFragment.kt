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
import com.digitaldream.linkskool.models.GroupItem
import com.digitaldream.linkskool.models.QuestionItem

class AdminELearningQuestionPreviewDialogFragment(
    private val groupItems: MutableList<GroupItem<String, QuestionItem?>>,
) : DialogFragment(R.layout.fragment_admin_e_learning_question_preview) {


    private lateinit var dismissBtn: ImageView
    private lateinit var timeTxt: TextView
    private lateinit var sectionTxt: TextView
    private lateinit var questionRecyclerView: RecyclerView
    private lateinit var emptyQuestionTxt: TextView
    private lateinit var previousBtn: Button
    private lateinit var nextBtn: Button

    private var currentSectionIndex: Int = 0
    private var currentQuestionIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.apply {
            dismissBtn = findViewById(R.id.dismissBtn)
            timeTxt = findViewById(R.id.timeTxt)
            sectionTxt = findViewById(R.id.sectionTxt)
            questionRecyclerView = findViewById(R.id.questionRecyclerView)
            emptyQuestionTxt = findViewById(R.id.emptyQuestionTxt)
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
        val currentSection = groupItems.getOrNull(currentSectionIndex)
        if (currentSection != null) {
            val questionList = currentSection.itemList
            val currentQuestion = questionList.getOrNull(currentQuestionIndex)

            sectionTxt.text = currentSection.title
            sectionTxt.isVisible = !currentSection.title.isNullOrEmpty()

            if (currentQuestion != null) {

                if (questionList.size > 0)
                    enableNextButton()
                else disableNextButton()

                showQuestionPreview(currentQuestion)
                emptyQuestionTxt.isVisible = false
            } else {
                disableNextButton()
                emptyQuestionTxt.isVisible = true
            }
        }

    }

    private fun showNextQuestion() {
        currentQuestionIndex++
        val currentSection = groupItems.getOrNull(currentSectionIndex)
        if (currentSection != null) {
            val questionList = currentSection.itemList
            if (currentQuestionIndex >= questionList.size) {
                currentSectionIndex++
                currentQuestionIndex = 0
            }
            showQuestion()
            enablePreviousButton()
        }

    }

    private fun showPreviousQuestion() {
        currentQuestionIndex--
        val currentSection = groupItems.getOrNull(currentSectionIndex)
        if (currentSection != null) {
            if (currentQuestionIndex < 0) {
                currentSectionIndex--
                currentQuestionIndex =
                    (groupItems.getOrNull(currentSectionIndex)?.itemList?.size ?: 0) - 1
            }
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



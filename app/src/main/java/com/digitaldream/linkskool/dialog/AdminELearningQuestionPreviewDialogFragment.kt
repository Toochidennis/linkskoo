package com.digitaldream.linkskool.dialog

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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

            if (questionList.isNotEmpty()) {
                sectionTxt.text = currentSection.title
                sectionTxt.isVisible = !currentSection.title.isNullOrEmpty()

                val currentQuestion = questionList.getOrNull(currentQuestionIndex)

                if (currentQuestion != null) {
                    showQuestionPreview(currentQuestion)

                    if (groupItems.size == 1 && questionList.size == 1) {
                        // Only one section and one question
                        disablePreviousButton()
                        disableNextButton()
                    } else if (currentSectionIndex == 0 && currentQuestionIndex == 0) {
                        // First section and first question
                        disablePreviousButton()
                        enableNextButton()
                    } else if (currentSectionIndex == groupItems.size - 1 && currentQuestionIndex == questionList.size - 1) {
                        // Last section and last question
                        enablePreviousButton()
                        disableNextButton()
                    } else if (currentSectionIndex == 0 && currentQuestionIndex == questionList.size - 1) {
                        // First section and last question
                        enablePreviousButton()
                        enableNextButton()
                    } else if (currentSectionIndex == groupItems.size - 1 && currentQuestionIndex == 0) {
                        // Last section and first question
                        enablePreviousButton()
                        enableNextButton()
                    } else if (currentSectionIndex == 0 && currentQuestionIndex > 0) {
                        // First section
                        enablePreviousButton()
                        enableNextButton()
                    } else if (currentSectionIndex == groupItems.size - 1 &&
                        currentQuestionIndex >= questionList.size - 1
                    ) {
                        // Last section
                        enablePreviousButton()
                        disableNextButton()
                    } else if (currentQuestionIndex == 0) {
                        // First question in the section
                        enablePreviousButton()
                        enableNextButton()
                    } else if (currentQuestionIndex == questionList.size - 1) {
                        // Last question in the section
                        enablePreviousButton()
                        enableNextButton()
                    } else {
                        // In-between section and question
                        enablePreviousButton()
                        enableNextButton()
                    }
                }
            } else {
                if (groupItems[0].itemList.isNotEmpty()) {
                    enablePreviousButton()
                    disableNextButton()
                } else {
                    disablePreviousButton()
                    disableNextButton()
                    Toast.makeText(
                        requireContext(), "There is no question in this section",
                        Toast.LENGTH_SHORT
                    ).show()
                }

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
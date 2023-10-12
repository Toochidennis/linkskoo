package com.digitaldream.linkskool.dialog

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.AdminELearningQuizAdapter
import com.digitaldream.linkskool.adapters.GenericAdapter2
import com.digitaldream.linkskool.models.QuestionItem
import com.digitaldream.linkskool.models.QuizProgressModel
import com.digitaldream.linkskool.models.SectionModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.Locale

/**
 * Introduction:
 *
 * `StudentELearningQuizDialogFragment` is a component designed to facilitate quiz-taking functionality
 * in an e-learning application. It provides an interactive quiz interface, tracks user responses, and
 * offers a countdown timer for time-limited quizzes.
 *
 * Getting Started:
 *
 * To integrate `StudentELearningQuizDialogFragment` into your application, follow these steps:
 *
 * 1. Include the Library:
 *    Ensure that the necessary library containing `StudentELearningQuizDialogFragment` is added to your project.
 *
 * 2. Initialize the Component:
 *    Create an instance of `StudentELearningQuizDialogFragment` by providing the required parameters,
 *    such as quiz duration and a list of quiz items.
 *
 *    ```kotlin
 *    val quizDialogFragment = StudentELearningQuizDialogFragment(duration, quizItems)
 *    ```
 *
 * 3. Show the Dialog:
 *    Display the quiz dialog using the `show` method.
 *
 *    ```kotlin
 *    quizDialogFragment.show(supportFragmentManager, "QuizDialogFragment")
 *    ```
 *
 * Features:
 *
 * - Quiz Interface:
 *   The component provides an interactive interface for users to answer quiz questions using a ViewPager2.
 *
 * - Progress Tracking:
 *   A progress bar allows users to track their progress through the quiz. It dynamically updates as the user navigates through questions.
 *
 * - Countdown Timer:
 *   If a quiz has a time limit, a countdown timer is displayed, alerting users to the remaining time.
 *
 * - User Responses:
 *   User responses are tracked and stored for later evaluation.
 *
 * Usage:
 *
 * ### Quiz Initialization
 *
 * ```kotlin
 * val quizDialogFragment = StudentELearningQuizDialogFragment(duration, quizItems)
 * ```
 *
 * ### Display Quiz Dialog
 *
 * ```kotlin
 * quizDialogFragment.show(supportFragmentManager, "QuizDialogFragment")
 * ```
 *
 * API Reference:
 *
 * ### `StudentELearningQuizDialogFragment`
 *
 * #### Constructors
 *
 * - `StudentELearningQuizDialogFragment(duration: String, quizItems: MutableList<SectionModel>)`
 *   Creates a new instance of the quiz dialog.
 *
 * #### Public Methods
 *
 * - `onOptionSelected(questionId: String, selectedOption: String)`
 *   Callback when a user selects an option for a multiple-choice question.
 *
 * - `setTypedAnswer(questionId: String, typedAnswer: String)`
 *   Callback when a user enters a typed answer for a question.
 *
 */


private const val MAX_VISIBLE_QUESTIONS = 5

class StudentELearningQuizDialogFragment(
    private val duration: String,
    private val quizItems: MutableList<SectionModel>
) : DialogFragment(R.layout.fragment_student_e_learning_quiz),
    AdminELearningQuizAdapter.UserResponse {


    private lateinit var countDownTxt: TextView
    private lateinit var submitBtn: Button
    private lateinit var progressRecyclerView: RecyclerView
    private lateinit var quizViewPager: ViewPager2
    private lateinit var previousBtn: ImageButton
    private lateinit var nextBtn: ImageButton

    private lateinit var countDownJob: Job
    private lateinit var quizAdapter: AdminELearningQuizAdapter
    private lateinit var progressAdapter: GenericAdapter2<QuizProgressModel>
    private var userResponses = mutableMapOf<String, String>()

    private var progressList = mutableListOf<QuizProgressModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)

        isCancelable = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        disableSubmitButton()

        setUpProgressAdapter()

        countDownTimer()

        showPreviousQuestion()

        nextBtn.setOnClickListener {
            showNextQuestion()
        }

        showQuestion()

        submitQuiz()
    }

    private fun setUpViews(view: View) {
        view.apply {
            countDownTxt = findViewById(R.id.countDownTxt)
            submitBtn = findViewById(R.id.submitBtn)
            quizViewPager = findViewById(R.id.quizViewPager)
            progressRecyclerView = findViewById(R.id.progressRecyclerView)
            previousBtn = findViewById(R.id.prevBtn)
            nextBtn = findViewById(R.id.nextBtn)
        }
    }

    private fun showQuestion() {
        quizAdapter = AdminELearningQuizAdapter(quizItems, userResponses, this)
        quizViewPager.adapter = quizAdapter

        quizViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                updateNavigationButtons()

                updateProgressRecyclerView()
            }
        })

    }


    private fun showPreviousQuestion() {
        previousBtn.setOnClickListener {
            if (quizViewPager.currentItem > 0) {
                quizViewPager.currentItem--
            }
        }
    }

    private fun showNextQuestion() {
        if (quizViewPager.currentItem < quizItems.size - 1) {
            quizViewPager.currentItem++
        }
    }


    private fun updateNavigationButtons() =
        if (quizItems.size == 1) {
            disableNextButton()
            disablePreviousButton()
            enableSubmitBtn()
        } else if (quizViewPager.currentItem == 0) {
            enableNextButton()
            disablePreviousButton()
        } else if (quizViewPager.currentItem == quizItems.size - 1) {
            disableNextButton()
            enablePreviousButton()
            enableSubmitBtn()
        } else {
            enableNextButton()
            enablePreviousButton()
        }


    private fun totalQuestionCount(): Int {
        return quizItems.count { it.questionItem != null }
    }

    private fun setUpProgressAdapter() {
        for (i in 1..totalQuestionCount()) {
            progressList.add(QuizProgressModel("$i"))
        }

        progressAdapter = GenericAdapter2(
            progressList,
            R.layout.item_quiz_progress_layout,
            bindItem = { itemView, model, position ->
                val progressLayout: LinearLayout = itemView.findViewById(R.id.progressLayout)
                val progressTxt: TextView = itemView.findViewById(R.id.progressTxt)

                progressTxt.text = model.questionPosition

                if (model.isAnswered) {
                    progressLayout.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.circle5)
                } else {
                    progressLayout.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.circle7)
                }

                itemView.setOnClickListener {
                    scrollToPosition(position)
                }

            }
        )

        setUpProgressRecyclerView()

        updateProgressRecyclerView()
    }

    private fun setUpProgressRecyclerView() {
        progressRecyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = progressAdapter
        }
    }

    private fun updateProgressRecyclerView() {
        val currentPosition = quizViewPager.currentItem
        val totalQuestions = totalQuestionCount()

        val start = currentPosition - MAX_VISIBLE_QUESTIONS / 2
        val end = currentPosition + MAX_VISIBLE_QUESTIONS / 2

        val displayRange = if (totalQuestions <= MAX_VISIBLE_QUESTIONS) {
            0 until totalQuestions
        } else {
            start.coerceIn(0, totalQuestions - MAX_VISIBLE_QUESTIONS) until end.coerceIn(
                MAX_VISIBLE_QUESTIONS, totalQuestions
            )
        }

        progressList.forEachIndexed { index, model ->
            if (index in displayRange) {
                model.questionPosition = (index + 1).toString()
            } else {
                model.questionPosition = "..."
            }
        }

        progressAdapter.notifyDataSetChanged()
    }

    private fun scrollToPosition(progressAdapterPosition: Int) {
        val questionPosition = questionPosition(progressAdapterPosition)
        quizViewPager.currentItem = questionPosition
    }

    private fun questionPosition(progressAdapterPosition: Int): Int {
        var currentPosition = 0
        var questionPosition = 0

        for (i in 0 until quizItems.size) {
            if (quizItems[i].questionItem != null) {
                if (questionPosition == progressAdapterPosition) {
                    currentPosition = i
                    break
                }
                questionPosition++
            }
        }


        return currentPosition
    }

    private fun countDownTimer() {
        if (duration.isEmpty()) {
            return
        }

        val countDownIntervalMillis = 1000L
        val quizDurationMillis = duration.toLong().times(60).times(countDownIntervalMillis)
        val threshold = (quizDurationMillis * 0.10).toLong() // 10% of the duration

        countDownJob = CoroutineScope(Dispatchers.Default).launch {
            var remainingTimeMillis = quizDurationMillis

            while (remainingTimeMillis > 0) {
                val minutes = remainingTimeMillis / 1000 / 60
                val seconds = (remainingTimeMillis / 1000) % 60
                val timeString =
                    String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

                withContext(Dispatchers.Main) {
                    countDownTxt.text = timeString

                    if (remainingTimeMillis <= threshold) {
                        countDownTxt.setTextColor(Color.RED)
                    }
                }

                delay(countDownIntervalMillis)

                remainingTimeMillis -= countDownIntervalMillis
            }

            submitQuiz()

            withContext(Dispatchers.Main) {
                "00:00".let { countDownTxt.text = it }
            }
        }
    }

    private fun calculateScore(): Int {
        return userResponses.count { (questionId, userAnswer) ->
            val section = quizItems.find {
                it.questionItem?.let { questionItem ->
                    when (questionItem) {
                        is QuestionItem.MultiChoice -> questionItem.question.questionId
                        is QuestionItem.ShortAnswer -> questionItem.question.questionId
                    }
                } == questionId
            }

            val correctAnswer = section?.questionItem?.let { questionItem ->
                when (questionItem) {
                    is QuestionItem.MultiChoice -> questionItem.question.correctAnswer
                    is QuestionItem.ShortAnswer -> questionItem.question.answerText
                }
            }

            userAnswer.isNotBlank() && userAnswer.equals(correctAnswer, true)
        }
    }


    private fun submitQuiz() {
        submitBtn.setOnClickListener {
            StudentELearningQuizCompletionDialog(requireContext()) {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }.apply {
                show()
            }.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

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

    private fun disableSubmitButton() {
        submitBtn.isEnabled = false
    }

    private fun enableSubmitBtn() {
        submitBtn.isEnabled = true
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onOptionSelected(questionId: String, selectedOption: String) {
        userResponses[questionId] = selectedOption

        GlobalScope.launch {
            delay(1000L)

            withContext(Dispatchers.Main) {
                showNextQuestion()
            }
        }

        updateAnsweredQuestion()

        progressAdapter.notifyDataSetChanged()
    }

    override fun setTypedAnswer(questionId: String, typedAnswer: String) {
        userResponses[questionId] = typedAnswer

        updateAnsweredQuestion()

        progressAdapter.notifyDataSetChanged()
    }

    private fun updateAnsweredQuestion() {
        val currentQuestionPosition = questionPosition(quizViewPager.currentItem)
        progressList[currentQuestionPosition].isAnswered = true
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::countDownJob.isInitialized)
            countDownJob.cancel()
    }
}
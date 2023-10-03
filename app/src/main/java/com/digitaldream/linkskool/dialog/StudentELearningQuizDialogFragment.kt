package com.digitaldream.linkskool.dialog

import android.graphics.Color
import android.os.Bundle
import android.view.View
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
import com.digitaldream.linkskool.adapters.GenericAdapter3
import com.digitaldream.linkskool.models.SectionModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

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
    private lateinit var progressAdapter: GenericAdapter3<Int>
    private var userResponses = mutableMapOf<String, String>()

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

        updateNavigationButtons()
    }


    private fun showPreviousQuestion() {
        previousBtn.setOnClickListener {
            if (quizViewPager.currentItem > 0) {
                quizViewPager.currentItem--

                updateNavigationButtons()

             //   progressRecyclerView.smoothScrollToPosition(quizViewPager.currentItem)
            }
        }
    }

    private fun showNextQuestion() {
        if (quizViewPager.currentItem < quizItems.size - 1) {
            quizViewPager.currentItem++

            updateNavigationButtons()

          //  progressRecyclerView.smoothScrollToPosition(quizViewPager.currentItem)
        }
    }


    private fun updateNavigationButtons() =
        if (quizItems.size == 1) {
            disableNextButton()
            disablePreviousButton()
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
        val totalQuestions = totalQuestionCount()
        progressAdapter = GenericAdapter3(
            totalQuestions,
            R.layout.item_quiz_progress_layout,
            bindItem = { itemView, position ->
                val progressLayout: LinearLayout = itemView.findViewById(R.id.progressLayout)
                val progressTxt: TextView = itemView.findViewById(R.id.progressTxt)

                val circlePosition = position + 1
                progressTxt.text = (circlePosition).toString()

                val currentQuestionPosition = questionPosition(position)

                if (quizViewPager.currentItem == currentQuestionPosition) {
                    progressLayout.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.circle5)
                }
            }
        ) { position ->
            scrollToPosition(position)
        }

        setUpProgressRecyclerView()
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
            smoothScrollToPosition(quizViewPager.currentItem)
        }
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

            //submitTest()

            withContext(Dispatchers.Main) {
                "00:00".let { countDownTxt.text = it }
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

        progressAdapter.notifyDataSetChanged()
    }

    override fun setTypedAnswer(questionId: String, typedAnswer: String) {
        userResponses[questionId] = typedAnswer

        progressAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::countDownJob.isInitialized)
            countDownJob.cancel()
    }
}
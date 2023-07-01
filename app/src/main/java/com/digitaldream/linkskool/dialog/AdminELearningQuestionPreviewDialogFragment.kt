package com.digitaldream.linkskool.dialog

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.models.GroupItem
import com.digitaldream.linkskool.models.QuestionItem

class AdminELearningQuestionPreviewDialogFragment(
    private val groupItems: MutableList<GroupItem<String, QuestionItem?>>,
    private var startDate: String = "",
    private var endDate: String = "",
) : DialogFragment(R.layout.fragment_admin_e_learning_question_preview) {


    private lateinit var dismissBtn: ImageView
    private lateinit var timeTxt: TextView
    private lateinit var sectionTxt: TextView
    private lateinit var questionRecyclerView: RecyclerView

    private lateinit var groupItemsCopy: MutableList<GroupItem<String, QuestionItem?>>
    private var currentSectionIndex: Int = 0
    private var currentQuestionIndex: Int = 0
    private var startDateCopy: String? = null
    private var endDateCopy: String? = null

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
        }
    }


    private fun initialiseModel() {
        groupItemsCopy = groupItems
    }

    private fun showQuestion() {
        val currentGroup = groupItemsCopy[currentSectionIndex]
        val currentSection = currentGroup.title
        val currentQuestion = currentGroup.itemList[currentQuestionIndex]




    }

}
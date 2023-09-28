package com.digitaldream.linkskool.dialog

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R

class StudentELearningAssignmentSubmissionDialogFragment :
    DialogFragment(R.layout.fragment_student_e_learning_assignment_submission) {

    private lateinit var backBtn: ImageButton
    private lateinit var dateTxt: TextView
    private lateinit var attachmentRecyclerView: RecyclerView
    private lateinit var commentTitleTxt: TextView
    private lateinit var commentRecyclerView: RecyclerView
    private lateinit var addCommentTxt: TextView
    private lateinit var addWorkBtn: Button
    private lateinit var handInBtn: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)
    }

    private fun setUpViews(view: View) {
        view.apply {
            backBtn = findViewById(R.id.backBtn)
            dateTxt = findViewById(R.id.dateTxt)
            attachmentRecyclerView = findViewById(R.id.attachmentRecyclerView)
            commentTitleTxt = findViewById(R.id.commentTitleTxt)
            commentRecyclerView = findViewById(R.id.commentRecyclerView)
            addCommentTxt = findViewById(R.id.addCommentTxt)
            addWorkBtn = findViewById(R.id.addWorkBtn)
            handInBtn = findViewById(R.id.handInBtn)
        }
    }
}
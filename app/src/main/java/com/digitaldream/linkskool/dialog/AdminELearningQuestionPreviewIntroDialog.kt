package com.digitaldream.linkskool.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.digitaldream.linkskool.R

class AdminELearningQuestionPreviewIntroDialog(
    context: Context,
    private val jsonData: String,
    private val onStart: (status: String) -> Unit
) : Dialog(context) {

    // Define UI elements
    private lateinit var dismissBtn: ImageButton
    private lateinit var titleTxt: TextView
    private lateinit var descriptionTxt: TextView
    private lateinit var dateTxt: TextView
    private lateinit var durationTxt: TextView
    private lateinit var startBtn: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            attributes.windowAnimations = R.style.DialogAnimation
            setGravity(Gravity.BOTTOM)
        }

        setContentView(R.layout.dialog_admin_e_learning_question_preview_intro)

        setUpViews()

    }

    private fun setUpViews() {
        dismissBtn = findViewById(R.id.dismissBtn)
        titleTxt = findViewById(R.id.titleTxt)
        descriptionTxt = findViewById(R.id.descriptionTxt)
        dateTxt = findViewById(R.id.dateTxt)
        durationTxt = findViewById(R.id.durationTxt)
        startBtn = findViewById(R.id.startQuizButton)
    }


    private fun pulsateButton() {
        val animation = AnimationUtils.loadAnimation(context, R.anim.pulse)
        startBtn.startAnimation(animation)
    }
}
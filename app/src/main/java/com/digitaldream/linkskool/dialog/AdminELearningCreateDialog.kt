package com.digitaldream.linkskool.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.widget.RelativeLayout
import com.digitaldream.linkskool.R

class AdminELearningCreateDialog(
    context: Context,
) : Dialog(context) {

    private lateinit var assignmentBtn: RelativeLayout
    private lateinit var questionBtn: RelativeLayout
    private lateinit var materialBtn: RelativeLayout
    private lateinit var reuseBtn: RelativeLayout
    private lateinit var topicBtn: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            attributes.windowAnimations = R.style.DialogAnimation
            setGravity(Gravity.BOTTOM)
        }

        setContentView(R.layout.dialog_admin_elearning_create_dialog)

        assignmentBtn = findViewById(R.id.assignment_layout)
        questionBtn = findViewById(R.id.question_layout)
        materialBtn = findViewById(R.id.material_layout)
        reuseBtn = findViewById(R.id.reuse_layout)
        topicBtn = findViewById(R.id.topic_layout)


        


    }
}
package com.digitaldream.linkskool.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.widget.RelativeLayout
import android.widget.TextView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.activities.ELearningActivity

class AdminELearningCreateDialog(
    context: Context,
) : Dialog(context) {

    private lateinit var assignmentBtn: TextView
    private lateinit var questionBtn: TextView
    private lateinit var materialBtn: TextView
    private lateinit var reuseBtn: TextView
    private lateinit var topicBtn: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            attributes.windowAnimations = R.style.DialogAnimation
            setGravity(Gravity.BOTTOM)
        }

        setContentView(R.layout.dialog_admin_elearning_create)

        assignmentBtn = findViewById(R.id.assignment_btn)
        questionBtn = findViewById(R.id.question_btn)
        materialBtn = findViewById(R.id.material_btn)
        reuseBtn = findViewById(R.id.reuse_btn)
        topicBtn = findViewById(R.id.topic_btn)


        assignmentBtn.setOnClickListener {
            context.startActivity(Intent(context, ELearningActivity::class.java)
                .putExtra("from", "assignment"))
        }

        questionBtn.setOnClickListener {
            context.startActivity(Intent(context, ELearningActivity::class.java)
                .putExtra("from", "question"))
        }

        materialBtn.setOnClickListener {
            context.startActivity(Intent(context, ELearningActivity::class.java)
                .putExtra("from", "material"))
        }



    }
}
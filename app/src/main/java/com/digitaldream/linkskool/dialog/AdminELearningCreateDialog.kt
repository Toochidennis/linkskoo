package com.digitaldream.linkskool.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.activities.ELearningActivity
import com.digitaldream.linkskool.fragments.AdminELearningAssignmentDialogFragment
import com.digitaldream.linkskool.fragments.AdminELearningMaterialDialogFragment
import org.json.JSONObject

class AdminELearningCreateDialog(
    context: Context,
    private val fragmentManager: FragmentManager,
    private val levelId: String,
    private val courseId: String,
    private val jsonObject: JSONObject = JSONObject(),
    private val onCreateContent: (contentObject: JSONObject) -> Unit
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
            AdminELearningAssignmentDialogFragment
                .newInstance(levelId, courseId, "$jsonObject")
                .show(fragmentManager, "")

            dismiss()
        }

        questionBtn.setOnClickListener {
            AdminELearningAssignmentDialogFragment
                .newInstance(levelId, courseId, "$jsonObject")
                .show(fragmentManager, "")

            dismiss()
        }

        materialBtn.setOnClickListener {
            AdminELearningMaterialDialogFragment
                .newInstance(levelId, courseId, "$jsonObject")
                .show(fragmentManager, "")

            dismiss()
        }

        topicBtn.setOnClickListener {
            AdminELearningAddTopicDialog(context).apply {
                setCancelable(true)
                show()
            }.window?.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }


    }
}
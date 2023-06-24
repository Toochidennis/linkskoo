package com.digitaldream.linkskool.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.fragments.AdminELearningMultiChoiceDialogFragment
import com.digitaldream.linkskool.models.MultiChoiceQuestion

class AdminELearningQuestionDialog(
    context: Context,
    private val fragmentManager: FragmentManager
): Dialog(context) {


    private var mQuestionModel =  MultiChoiceQuestion()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            attributes.windowAnimations = R.style.DialogAnimation
            setGravity(Gravity.BOTTOM)
        }

        setContentView(R.layout.dialog_admin_e_learning_question)

        val shortAnswer:TextView = findViewById(R.id.shortAnswerBtn)
        val multiChoiceBtn:TextView = findViewById(R.id.multiChoiceBtn)
        val sectionBtn:TextView = findViewById(R.id.sectionBtn)

        multiChoiceBtn.setOnClickListener {

           val dialogA = AdminELearningMultiChoiceDialogFragment(mQuestionModel){

           }
            dialogA.isCancelable = true
            dialogA.show(fragmentManager, "")

            dismiss()
        }
    }
}
package com.digitaldream.linkskool.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.models.GroupItem
import com.digitaldream.linkskool.models.QuestionItem

class AdminELearningQuestionPreviewDialogFragment(
    private val groupItems: GroupItem<String, QuestionItem?>
) : DialogFragment(R.layout.fragment_admin_e_learning_question_preview) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
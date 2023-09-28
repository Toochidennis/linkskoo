package com.digitaldream.linkskool.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.digitaldream.linkskool.R

class StudentELearningQuizDialogFragment:DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
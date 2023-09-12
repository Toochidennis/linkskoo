package com.digitaldream.linkskool.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment

class AdminELearningFilesPreviewDialogFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    companion object{
        @JvmStatic
        fun newInstance() = AdminELearningFilesPreviewDialogFragment().apply {

        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
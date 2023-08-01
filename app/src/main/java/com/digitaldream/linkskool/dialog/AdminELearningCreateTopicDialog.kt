package com.digitaldream.linkskool.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import com.digitaldream.linkskool.R
import com.google.android.material.textfield.TextInputLayout

class AdminELearningCreateTopicDialog(context: Context) : Dialog(context) {

    private lateinit var topicInputLayout: TextInputLayout
    private lateinit var cancelButton: Button
    private lateinit var addButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        setContentView(R.layout.dialog_admin_e_learning_create_topic)

        topicInputLayout = findViewById(R.id.topicInputLayout)
        cancelButton = findViewById(R.id.cancelButton)
        addButton = findViewById(R.id.addButton)

    }

    private fun postTopic() {
        val topic = topicInputLayout.editText?.text.toString().trim()

        if (topic.isBlank()) {
            topicInputLayout.editText?.error = "Please provide a topic"
        }
    }
}
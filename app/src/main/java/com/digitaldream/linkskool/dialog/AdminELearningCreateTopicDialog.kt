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

class AdminELearningCreateTopicDialog(context: Context) : Dialog(context) {

    private lateinit var topicEditText: EditText
    private lateinit var cancelButton: Button
    private lateinit var addButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        setContentView(R.layout.dialog_admin_e_learning_create_topic)

        topicEditText = findViewById(R.id.topicEditText)
        cancelButton = findViewById(R.id.cancelButton)
        addButton = findViewById(R.id.addButton)

    }

    private fun postTopic() {
        val topic = topicEditText.text.toString().trim()

        if (topic.isBlank()) {
            topicEditText.error = "Please provide a topic"
        }
    }
}
package com.digitaldream.linkskool.models

import androidx.recyclerview.widget.RecyclerView

data class MultiChoiceQuestion(
    var questionText: String = "",
    var attachmentType: String = "",
    var attachmentName: String = "",
    var attachmentUri: Any? = null,
    var options: MutableList<MultiChoiceOption>? = null,
    var sShortAnswerModel: MutableList<ShortAnswerModel>? = null,
    var checkedPosition: Int = RecyclerView.NO_POSITION,
    var correctAnswer: String? = null,
)

data class MultiChoiceOption(
    var optionText: String = "",
    var optionOrder: String = "",
    var attachmentType: String = "",
    var attachmentName: String = "",
    var attachmentUri: Any? = null,
)

data class ShortAnswerModel(
    var questionText: String = "",
    var attachmentType: String = "",
    var attachmentName: String = "",
    var attachmentUri: Any? = null,
    var answerText: String = ""
)


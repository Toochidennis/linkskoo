package com.digitaldream.linkskool.models

import androidx.recyclerview.widget.RecyclerView

sealed class QuestionItem {
    data class MultiChoice(val question: MultiChoiceQuestion) : QuestionItem()
    data class ShortAnswer(val question: ShortAnswerModel) : QuestionItem()
}

data class MultiChoiceQuestion(
    var questionText: String = "",
    var attachmentType: String = "",
    var attachmentName: String = "",
    var attachmentUri: Any? = null,
    var options: MutableList<MultipleChoiceOption>? = null,
    var checkedPosition: Int = RecyclerView.NO_POSITION,
    var correctAnswer: String? = null,
)

data class ShortAnswerModel(
    var questionText: String = "",
    var attachmentType: String = "",
    var attachmentName: String = "",
    var attachmentUri: Any? = null,
    var answerText: String = ""
)

data class MultipleChoiceOption(
    var optionText: String = "",
    var optionOrder: String = "",
    var attachmentType: String = "",
    var attachmentName: String = "",
    var attachmentUri: Any? = null,
)


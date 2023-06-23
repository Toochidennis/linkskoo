package com.digitaldream.linkskool.models

class MultiChoiceOption {
    var optionOrder: String? = null
    var optionText: String? = null
    var optionImageType: String? = null
    var optionImageName: String? = null
    var optionImageUri: Any? = null
}

data class MultiChoiceQuestion(
    val questionText: String,
    val options: MultiChoiceOption,
    val correctAnswerOrder: String,
    val correctAnswer: String
)



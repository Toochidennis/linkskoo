package com.digitaldream.linkskool.models

data class SectionModel(
    var sectionTitle: String?,
    var questionItem: QuestionItem?,
    val viewType: String,
    var isHidden: Boolean = false,
)

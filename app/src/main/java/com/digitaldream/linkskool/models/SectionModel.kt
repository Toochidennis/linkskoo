package com.digitaldream.linkskool.models

import java.io.Serializable

data class SectionModel(
    var sectionTitle: String?,
    var questionItem: QuestionItem?,
    val viewType: String,
):Serializable

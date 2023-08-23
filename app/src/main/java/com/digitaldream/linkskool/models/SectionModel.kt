package com.digitaldream.linkskool.models

import java.io.Serializable

data class SectionModel(
    var sectionId: String? = null,
    var sectionTitle: String?,
    var questionItem: QuestionItem?,
    val viewType: String,
) : Serializable

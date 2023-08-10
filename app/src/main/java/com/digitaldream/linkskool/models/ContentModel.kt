package com.digitaldream.linkskool.models

data class ContentModel(
    val id: String,
    val title: String,
    val description: String,
    val courseId: String,
    val levelId: String,
    val authorId:String,
    val authorName:String,
    val date: String,
    val term: String,
    val type:String,
    val viewType: String
)

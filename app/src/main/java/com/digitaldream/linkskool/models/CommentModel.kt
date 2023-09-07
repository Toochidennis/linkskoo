package com.digitaldream.linkskool.models

data class CommentModel(
    val commentId:String,
    val authorId:String,
    val authorName:String,
    val commentText:String,
    val date:String
)

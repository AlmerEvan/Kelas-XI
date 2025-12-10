package com.example.blogapp.data.model

data class Blog(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val authorId: String = "",
    val likes: Int = 0,
    val isSaved: Boolean = false,
    val createdAt: Long = 0L
)

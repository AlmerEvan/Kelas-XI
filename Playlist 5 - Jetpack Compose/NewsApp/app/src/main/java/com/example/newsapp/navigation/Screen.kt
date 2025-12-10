package com.example.newsapp.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    object Home : Screen

    @Serializable
    data class ArticleDetail(val url: String) : Screen
}

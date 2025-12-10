package com.example.newsapp.data.model

data class Category(val name: String)

object CategoryConstants {
    val CATEGORIES = listOf(
        Category("general"),
        Category("business"),
        Category("entertainment"),
        Category("health"),
        Category("science"),
        Category("sports"),
        Category("technology")
    )
}

package com.example.newsapp.data.repository

import com.example.newsapp.data.model.Article
import com.example.newsapp.data.remote.RetrofitClient
import com.example.newsapp.utils.Constants

class NewsRepository {
    private val apiService = RetrofitClient.newsApiService

    suspend fun getTopHeadlines(category: String = "general"): List<Article> {
        return try {
            val response = apiService.getTopHeadlines(
                category = category.lowercase(),
                apiKey = Constants.API_KEY
            )
            response.articles ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getNewsByCategory(category: String): List<Article> {
        return try {
            val response = apiService.getNewsByCategory(category = category, apiKey = Constants.API_KEY)
            response.articles ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun searchEverything(query: String): List<Article> {
        return try {
            val response = apiService.searchEverything(
                query = query,
                language = "en",
                apiKey = Constants.API_KEY
            )
            response.articles ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}

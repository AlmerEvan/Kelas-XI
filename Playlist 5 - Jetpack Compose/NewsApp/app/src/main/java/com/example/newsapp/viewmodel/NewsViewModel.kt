package com.example.newsapp.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.data.model.Article
import com.example.newsapp.data.repository.NewsRepository
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {
    private val repository = NewsRepository()

    private val _articles = mutableStateOf<List<Article>>(emptyList())
    val articles: State<List<Article>> = _articles

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    val selectedCategory = mutableStateOf("general")
    val searchQuery = mutableStateOf("")
    val isSearchExpanded = mutableStateOf(false)

    init {
        fetchNewsTopHeadlines("general")
    }

    fun fetchNewsTopHeadlines(category: String = "general") {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getTopHeadlines(category)
                _articles.value = result
                _error.value = null
                selectedCategory.value = category
            } catch (e: Exception) {
                _error.value = e.message
                _articles.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchEverythingWithQuery(query: String) {
        if (query.isBlank()) {
            fetchNewsTopHeadlines(selectedCategory.value)
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.searchEverything(query)
                _articles.value = result
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
                _articles.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onCategorySelected(category: String) {
        isSearchExpanded.value = false
        searchQuery.value = ""
        fetchNewsTopHeadlines(category)
    }

    fun onSearchQueryChange(query: String) {
        searchQuery.value = query
    }

    fun onSearchToggle() {
        isSearchExpanded.value = !isSearchExpanded.value
        if (!isSearchExpanded.value) {
            searchQuery.value = ""
            fetchNewsTopHeadlines(selectedCategory.value)
        }
    }

    fun performSearch() {
        if (searchQuery.value.isNotBlank()) {
            fetchEverythingWithQuery(searchQuery.value)
            isSearchExpanded.value = false
        }
    }
}

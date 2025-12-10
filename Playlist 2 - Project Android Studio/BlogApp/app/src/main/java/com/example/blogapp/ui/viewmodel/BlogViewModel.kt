package com.example.blogapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.blogapp.data.model.Blog
import com.example.blogapp.data.repository.BlogRepository
import com.example.blogapp.data.repository.Result

class BlogViewModel : ViewModel() {
    private val blogRepository = BlogRepository()

    private val _blogs = MutableLiveData<List<Blog>>()
    val blogs: LiveData<List<Blog>> = _blogs

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _savedBlogIds = MutableLiveData<List<String>>()
    val savedBlogIds: LiveData<List<String>> = _savedBlogIds

    init {
        fetchBlogs()
    }

    fun fetchBlogs() {
        blogRepository.fetchBlogs().observeForever { result ->
            when (result) {
                is Result.Success -> {
                    _blogs.postValue(result.data)
                    _isLoading.postValue(false)
                    _error.postValue(null)
                }
                is Result.Error -> {
                    _isLoading.postValue(false)
                    _error.postValue(result.exception.message)
                }
                is Result.Loading -> {
                    _isLoading.postValue(true)
                }
            }
        }
    }

    fun saveBlog(title: String, content: String, authorId: String) {
        blogRepository.saveBlog(title, content, authorId).observeForever { result ->
            when (result) {
                is Result.Success -> {
                    _isLoading.postValue(false)
                    _error.postValue(null)
                    fetchBlogs() // Refresh blogs list
                }
                is Result.Error -> {
                    _isLoading.postValue(false)
                    _error.postValue(result.exception.message)
                }
                is Result.Loading -> {
                    _isLoading.postValue(true)
                }
            }
        }
    }

    fun updateLikes(blogId: String, increment: Int) {
        blogRepository.updateLikes(blogId, increment).observeForever { result ->
            when (result) {
                is Result.Success -> {
                    _error.postValue(null)
                    fetchBlogs() // Refresh blogs list
                }
                is Result.Error -> {
                    _error.postValue(result.exception.message)
                }
                is Result.Loading -> {}
            }
        }
    }

    fun saveBlogForUser(userId: String, blogId: String) {
        blogRepository.saveBlogForUser(userId, blogId).observeForever { result ->
            when (result) {
                is Result.Success -> {
                    _error.postValue(null)
                    fetchSavedBlogs(userId)
                }
                is Result.Error -> {
                    _error.postValue(result.exception.message)
                }
                is Result.Loading -> {}
            }
        }
    }

    fun removeSavedBlog(userId: String, blogId: String) {
        blogRepository.removeSavedBlog(userId, blogId).observeForever { result ->
            when (result) {
                is Result.Success -> {
                    _error.postValue(null)
                    fetchSavedBlogs(userId)
                }
                is Result.Error -> {
                    _error.postValue(result.exception.message)
                }
                is Result.Loading -> {}
            }
        }
    }

    fun fetchSavedBlogs(userId: String) {
        blogRepository.fetchSavedBlogs(userId).observeForever { result ->
            when (result) {
                is Result.Success -> {
                    _savedBlogIds.postValue(result.data)
                }
                is Result.Error -> {
                    _error.postValue(result.exception.message)
                }
                is Result.Loading -> {}
            }
        }
    }

    fun getSavedBlog(userId: String): LiveData<Result<List<Blog>>> {
        val liveData = MutableLiveData<Result<List<Blog>>>()
        liveData.postValue(Result.Loading)

        blogRepository.fetchSavedBlogs(userId).observeForever { result ->
            when (result) {
                is Result.Success -> {
                    val blogIds = result.data
                    val blogs = mutableListOf<Blog>()
                    var loadedCount = 0

                    if (blogIds.isEmpty()) {
                        liveData.postValue(Result.Success(blogs))
                        return@observeForever
                    }

                    blogIds.forEach { blogId ->
                        blogRepository.getBlogById(blogId).observeForever { blogResult ->
                            when (blogResult) {
                                is Result.Success -> {
                                    blogs.add(blogResult.data)
                                    loadedCount++
                                    if (loadedCount == blogIds.size) {
                                        liveData.postValue(Result.Success(blogs))
                                    }
                                }
                                is Result.Error -> {
                                    loadedCount++
                                }
                                is Result.Loading -> {}
                            }
                        }
                    }
                }
                is Result.Error -> {
                    liveData.postValue(Result.Error(result.exception))
                }
                is Result.Loading -> {}
            }
        }

        return liveData
    }
}

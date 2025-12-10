package com.example.blogapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.blogapp.data.model.User
import com.example.blogapp.data.repository.AuthRepository
import com.example.blogapp.data.repository.Result

class AuthViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    fun registerUser(name: String, email: String, password: String): LiveData<Result<User>> {
        return authRepository.registerUser(name, email, password)
    }

    fun loginUser(email: String, password: String): LiveData<Result<User>> {
        return authRepository.loginUser(email, password)
    }

    fun logout() {
        authRepository.logoutUser()
    }

    fun isUserLoggedIn(): Boolean {
        return authRepository.isUserLoggedIn()
    }

    fun getCurrentUserId(): String? {
        return authRepository.getCurrentUserId()
    }

    fun getCurrentUser(): User? {
        return authRepository.getCurrentUser()
    }
}

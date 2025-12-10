package com.example.blogapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.blogapp.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val TAG = "AuthRepository"

    fun registerUser(name: String, email: String, password: String): LiveData<Result<User>> {
        val liveData = MutableLiveData<Result<User>>()
        liveData.postValue(Result.Loading)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        val user = User(
                            uid = firebaseUser.uid,
                            name = name,
                            email = email
                        )
                        
                        // Save user to Firestore
                        firestore.collection("users")
                            .document(firebaseUser.uid)
                            .set(user)
                            .addOnSuccessListener {
                                Log.d(TAG, "User registered successfully")
                                liveData.postValue(Result.Success(user))
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Failed to save user: ${e.message}")
                                liveData.postValue(Result.Error(e as Exception))
                            }
                    }
                } else {
                    Log.e(TAG, "Registration failed: ${task.exception?.message}")
                    liveData.postValue(Result.Error(task.exception as Exception))
                }
            }

        return liveData
    }

    fun loginUser(email: String, password: String): LiveData<Result<User>> {
        val liveData = MutableLiveData<Result<User>>()
        liveData.postValue(Result.Loading)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        // Fetch user data from Firestore
                        firestore.collection("users")
                            .document(firebaseUser.uid)
                            .get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    val user = document.toObject(User::class.java)
                                    if (user != null) {
                                        Log.d(TAG, "User logged in successfully")
                                        liveData.postValue(Result.Success(user))
                                    }
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Failed to fetch user: ${e.message}")
                                liveData.postValue(Result.Error(e as Exception))
                            }
                    }
                } else {
                    Log.e(TAG, "Login failed: ${task.exception?.message}")
                    liveData.postValue(Result.Error(task.exception as Exception))
                }
            }

        return liveData
    }

    fun logoutUser() {
        auth.signOut()
    }

    fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser
        return if (firebaseUser != null) {
            User(
                uid = firebaseUser.uid,
                name = firebaseUser.displayName ?: "",
                email = firebaseUser.email ?: ""
            )
        } else {
            null
        }
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
}

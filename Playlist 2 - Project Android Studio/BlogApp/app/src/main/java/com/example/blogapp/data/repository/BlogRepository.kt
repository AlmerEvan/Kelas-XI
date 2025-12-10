package com.example.blogapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.blogapp.data.model.Blog
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID

class BlogRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val TAG = "BlogRepository"

    fun fetchBlogs(): LiveData<Result<List<Blog>>> {
        val liveData = MutableLiveData<Result<List<Blog>>>()
        liveData.postValue(Result.Loading)

        firestore.collection("blogs")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e(TAG, "Error fetching blogs: ${e.message}")
                    liveData.postValue(Result.Error(e as Exception))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val blogs = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Blog::class.java)?.copy(id = doc.id)
                    }
                    Log.d(TAG, "Blogs fetched: ${blogs.size}")
                    liveData.postValue(Result.Success(blogs))
                }
            }

        return liveData
    }

    fun saveBlog(title: String, content: String, authorId: String): LiveData<Result<String>> {
        val liveData = MutableLiveData<Result<String>>()
        liveData.postValue(Result.Loading)

        val blogId = UUID.randomUUID().toString()
        val blog = Blog(
            id = blogId,
            title = title,
            content = content,
            authorId = authorId,
            likes = 0,
            isSaved = false,
            createdAt = System.currentTimeMillis()
        )

        firestore.collection("blogs")
            .document(blogId)
            .set(blog)
            .addOnSuccessListener {
                Log.d(TAG, "Blog saved successfully")
                liveData.postValue(Result.Success(blogId))
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to save blog: ${e.message}")
                liveData.postValue(Result.Error(e as Exception))
            }

        return liveData
    }

    fun updateLikes(blogId: String, increment: Int): LiveData<Result<Boolean>> {
        val liveData = MutableLiveData<Result<Boolean>>()
        liveData.postValue(Result.Loading)

        firestore.runTransaction { transaction ->
            val blogRef = firestore.collection("blogs").document(blogId)
            val snapshot = transaction.get(blogRef)
            val currentLikes = snapshot.getLong("likes")?.toInt() ?: 0
            transaction.update(blogRef, "likes", currentLikes + increment)
        }
            .addOnSuccessListener {
                Log.d(TAG, "Likes updated successfully")
                liveData.postValue(Result.Success(true))
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to update likes: ${e.message}")
                liveData.postValue(Result.Error(e as Exception))
            }

        return liveData
    }

    fun saveBlogForUser(userId: String, blogId: String): LiveData<Result<Boolean>> {
        val liveData = MutableLiveData<Result<Boolean>>()
        liveData.postValue(Result.Loading)

        firestore.collection("users")
            .document(userId)
            .collection("saved_blogs")
            .document(blogId)
            .set(mapOf("savedAt" to System.currentTimeMillis()))
            .addOnSuccessListener {
                Log.d(TAG, "Blog saved for user successfully")
                liveData.postValue(Result.Success(true))
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to save blog for user: ${e.message}")
                liveData.postValue(Result.Error(e as Exception))
            }

        return liveData
    }

    fun removeSavedBlog(userId: String, blogId: String): LiveData<Result<Boolean>> {
        val liveData = MutableLiveData<Result<Boolean>>()
        liveData.postValue(Result.Loading)

        firestore.collection("users")
            .document(userId)
            .collection("saved_blogs")
            .document(blogId)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "Blog removed from saved successfully")
                liveData.postValue(Result.Success(true))
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to remove saved blog: ${e.message}")
                liveData.postValue(Result.Error(e as Exception))
            }

        return liveData
    }

    fun fetchSavedBlogs(userId: String): LiveData<Result<List<String>>> {
        val liveData = MutableLiveData<Result<List<String>>>()
        liveData.postValue(Result.Loading)

        firestore.collection("users")
            .document(userId)
            .collection("saved_blogs")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e(TAG, "Error fetching saved blogs: ${e.message}")
                    liveData.postValue(Result.Error(e as Exception))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val blogIds = snapshot.documents.map { it.id }
                    Log.d(TAG, "Saved blogs fetched: ${blogIds.size}")
                    liveData.postValue(Result.Success(blogIds))
                }
            }

        return liveData
    }

    fun getBlogById(blogId: String): LiveData<Result<Blog>> {
        val liveData = MutableLiveData<Result<Blog>>()
        liveData.postValue(Result.Loading)

        firestore.collection("blogs")
            .document(blogId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val blog = document.toObject(Blog::class.java)?.copy(id = document.id)
                    if (blog != null) {
                        Log.d(TAG, "Blog fetched: ${blog.id}")
                        liveData.postValue(Result.Success(blog))
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to fetch blog: ${e.message}")
                liveData.postValue(Result.Error(e as Exception))
            }

        return liveData
    }

    fun isUserSavedBlog(userId: String, blogId: String): LiveData<Result<Boolean>> {
        val liveData = MutableLiveData<Result<Boolean>>()

        firestore.collection("users")
            .document(userId)
            .collection("saved_blogs")
            .document(blogId)
            .get()
            .addOnSuccessListener { document ->
                liveData.postValue(Result.Success(document.exists()))
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to check saved blog: ${e.message}")
                liveData.postValue(Result.Error(e as Exception))
            }

        return liveData
    }
}

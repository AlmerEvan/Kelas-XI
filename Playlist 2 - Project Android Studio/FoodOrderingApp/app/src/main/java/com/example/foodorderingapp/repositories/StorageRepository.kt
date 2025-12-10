package com.example.foodorderingapp.repositories

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Repository untuk semua operasi Firebase Storage
 * Handling upload dan delete gambar
 */
class StorageRepository {
    private val storage = FirebaseStorage.getInstance()
    
    /**
     * Upload gambar menu ke Firebase Storage
     * @param imageUri URI dari image yang dipilih user
     * @return Download URL dari gambar, atau null jika gagal
     */
    suspend fun uploadFoodImage(imageUri: Uri): String? {
        return try {
            val fileName = "food_images/${UUID.randomUUID()}.jpg"
            val storageRef = storage.reference.child(fileName)
            
            storageRef.putFile(imageUri).await()
            
            // Dapatkan download URL
            storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Upload gambar profile user
     */
    suspend fun uploadProfileImage(imageUri: Uri, userId: String): String? {
        return try {
            val fileName = "profile_images/$userId.jpg"
            val storageRef = storage.reference.child(fileName)
            
            storageRef.putFile(imageUri).await()
            
            storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Hapus gambar dari Storage
     */
    suspend fun deleteImage(fileUrl: String): Boolean {
        return try {
            val fileRef = storage.getReferenceFromUrl(fileUrl)
            fileRef.delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }
}

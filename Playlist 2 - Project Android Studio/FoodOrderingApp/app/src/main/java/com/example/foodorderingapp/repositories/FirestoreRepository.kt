package com.example.foodorderingapp.repositories

import com.example.foodorderingapp.models.MenuItem
import com.example.foodorderingapp.models.User
import com.example.foodorderingapp.models.Order
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Repository untuk semua operasi CRUD dengan Firestore
 * Menghandle semua komunikasi dengan database
 */
class FirestoreRepository {
    private val firestore = FirebaseFirestore.getInstance()
    
    // ========== MENU OPERATIONS ==========
    
    /**
     * Ambil semua menu items dari Firestore
     */
    suspend fun getAllMenuItems(): List<MenuItem> {
        return try {
            firestore.collection("menu")
                .get()
                .await()
                .documents
                .mapNotNull { doc ->
                    doc.toObject(MenuItem::class.java)?.copy(id = doc.id)
                }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Ambil menu item berdasarkan ID
     */
    suspend fun getMenuItem(menuId: String): MenuItem? {
        return try {
            firestore.collection("menu")
                .document(menuId)
                .get()
                .await()
                .toObject(MenuItem::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Tambah menu item baru (untuk admin)
     */
    suspend fun addMenuItem(menuItem: MenuItem): Boolean {
        return try {
            firestore.collection("menu")
                .add(menuItem)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Update menu item (untuk admin)
     */
    suspend fun updateMenuItem(menuId: String, menuItem: MenuItem): Boolean {
        return try {
            firestore.collection("menu")
                .document(menuId)
                .set(menuItem)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Hapus menu item (untuk admin)
     */
    suspend fun deleteMenuItem(menuId: String): Boolean {
        return try {
            firestore.collection("menu")
                .document(menuId)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    // ========== USER OPERATIONS ==========
    
    /**
     * Simpan user profile setelah registrasi
     */
    suspend fun saveUserProfile(userId: String, user: User): Boolean {
        return try {
            firestore.collection("users")
                .document(userId)
                .set(user)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Ambil user profile berdasarkan user ID
     */
    suspend fun getUserProfile(userId: String): User? {
        return try {
            firestore.collection("users")
                .document(userId)
                .get()
                .await()
                .toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Update user profile
     */
    suspend fun updateUserProfile(userId: String, user: User): Boolean {
        return try {
            firestore.collection("users")
                .document(userId)
                .update(
                    mapOf(
                        "name" to user.name,
                        "phone" to user.phone,
                        "address" to user.address,
                        "city" to user.city,
                        "postalCode" to user.postalCode
                    )
                )
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    // ========== ORDER OPERATIONS ==========
    
    /**
     * Simpan order baru ke Firestore
     */
    suspend fun createOrder(order: Order): Boolean {
        return try {
            firestore.collection("orders")
                .add(order)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Ambil semua orders user
     */
    suspend fun getUserOrders(userId: String): List<Order> {
        return try {
            firestore.collection("orders")
                .whereEqualTo("userId", userId)
                .get()
                .await()
                .documents
                .mapNotNull { doc ->
                    doc.toObject(Order::class.java)?.copy(id = doc.id)
                }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Ambil order berdasarkan ID
     */
    suspend fun getOrder(orderId: String): Order? {
        return try {
            firestore.collection("orders")
                .document(orderId)
                .get()
                .await()
                .toObject(Order::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Update status order (untuk admin)
     */
    suspend fun updateOrderStatus(orderId: String, status: String): Boolean {
        return try {
            firestore.collection("orders")
                .document(orderId)
                .update("status", status)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
}

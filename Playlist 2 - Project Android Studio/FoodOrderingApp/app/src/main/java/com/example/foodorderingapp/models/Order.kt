package com.example.foodorderingapp.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Order(
    var id: String = "",
    var orderId: String = "",
    var userId: String = "",
    var items: List<CartItem> = emptyList(),
    var subtotal: Double = 0.0,
    var deliveryFee: Double = 5.0,
    var total: Double = 0.0,
    var status: String = "Pending", // Pending, Accepted, Completed, Cancelled
    var deliveryAddress: String = "",
    var notes: String = "",
    @ServerTimestamp
    var timestamp: Date? = null
)

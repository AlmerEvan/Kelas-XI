package com.example.foodorderingapp.models

data class CartItem(
    var menuItemId: String = "",
    var name: String = "",
    var price: Double = 0.0,
    var quantity: Int = 0,
    var image: String = ""
) {
    fun getSubtotal(): Double = price * quantity
}

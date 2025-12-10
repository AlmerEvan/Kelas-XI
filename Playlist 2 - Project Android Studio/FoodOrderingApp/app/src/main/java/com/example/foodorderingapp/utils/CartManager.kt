package com.example.foodorderingapp.utils

import com.example.foodorderingapp.models.CartItem

object CartManager {
    private val cart = mutableListOf<CartItem>()

    fun addToCart(item: CartItem) {
        val existingItem = cart.find { it.menuItemId == item.menuItemId }
        if (existingItem != null) {
            existingItem.quantity += item.quantity
        } else {
            cart.add(item)
        }
    }

    fun removeFromCart(position: Int) {
        if (position in cart.indices) {
            cart.removeAt(position)
        }
    }

    fun getCart(): List<CartItem> = cart.toList()

    fun getSubtotal(): Double = cart.sumOf { it.getSubtotal() }

    fun clearCart() {
        cart.clear()
    }
}

package com.example.foodorderingapp.models

import java.io.Serializable

data class MenuItem(
    var id: String = "",
    var name: String = "",
    var description: String = "",
    var price: Double = 0.0,
    var image: String = "",
    var category: String = "",
    var available: Boolean = true
) : Serializable

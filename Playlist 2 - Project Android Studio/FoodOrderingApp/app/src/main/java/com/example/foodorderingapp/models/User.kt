package com.example.foodorderingapp.models

data class User(
    var uid: String = "",
    var email: String = "",
    var name: String = "",
    var phone: String = "",
    var address: String = "",
    var city: String = "",
    var postalCode: String = "",
    var isAdmin: Boolean = false
)

package com.example.foodorderingapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.foodorderingapp.R
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var nameText: TextView
    private lateinit var emailText: TextView
    private lateinit var orderHistoryBtn: Button
    private lateinit var logoutBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()

        nameText = findViewById(R.id.nameText)
        emailText = findViewById(R.id.emailText)
        orderHistoryBtn = findViewById(R.id.orderHistoryBtn)
        logoutBtn = findViewById(R.id.logoutBtn)

        val currentUser = auth.currentUser
        if (currentUser != null) {
            nameText.text = "Nama: ${currentUser.displayName ?: "Pengguna"}"
            emailText.text = "Email: ${currentUser.email}"
        }

        orderHistoryBtn.setOnClickListener {
            startActivity(Intent(this, OrderHistoryActivity::class.java))
        }

        logoutBtn.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}

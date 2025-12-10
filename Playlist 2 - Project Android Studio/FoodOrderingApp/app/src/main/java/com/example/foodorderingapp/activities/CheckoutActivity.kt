package com.example.foodorderingapp.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.foodorderingapp.R
import com.example.foodorderingapp.models.Order
import com.example.foodorderingapp.utils.CartManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CheckoutActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var addressInput: EditText
    private lateinit var notesInput: EditText
    private lateinit var totalText: TextView
    private lateinit var confirmBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        addressInput = findViewById(R.id.addressInput)
        notesInput = findViewById(R.id.notesInput)
        totalText = findViewById(R.id.totalText)
        confirmBtn = findViewById(R.id.confirmBtn)

        val subtotal = CartManager.getSubtotal()
        val deliveryFee = 5.0
        val total = subtotal + deliveryFee

        totalText.text = "Total: Rp $total"

        confirmBtn.setOnClickListener {
            val address = addressInput.text.toString()
            val notes = notesInput.text.toString()

            if (address.isEmpty()) {
                Toast.makeText(this, "Alamat pengiriman tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentUser = auth.currentUser ?: return@setOnClickListener
            val order = Order(
                userId = currentUser.uid,
                items = CartManager.getCart(),
                subtotal = subtotal,
                deliveryFee = deliveryFee,
                total = total,
                status = "Pending",
                deliveryAddress = address,
                notes = notes
            )

            firestore.collection("orders").add(order)
                .addOnSuccessListener { docRef ->
                    Toast.makeText(this, "Pesanan berhasil dibuat", Toast.LENGTH_SHORT).show()
                    CartManager.clearCart()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Gagal membuat pesanan: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}

package com.example.foodorderingapp.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodorderingapp.R
import com.example.foodorderingapp.adapters.OrderAdapter
import com.example.foodorderingapp.models.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class OrderHistoryActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var orderRecyclerView: RecyclerView
    private lateinit var orderAdapter: OrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        orderRecyclerView = findViewById(R.id.orderRecyclerView)
        orderAdapter = OrderAdapter(emptyList())

        orderRecyclerView.layoutManager = LinearLayoutManager(this)
        orderRecyclerView.adapter = orderAdapter

        loadOrders()
    }

    private fun loadOrders() {
        val currentUser = auth.currentUser ?: return
        
        firestore.collection("orders")
            .whereEqualTo("userId", currentUser.uid)
            .get()
            .addOnSuccessListener { result ->
                val orders = result.documents.mapNotNull { doc ->
                    doc.toObject(Order::class.java)?.apply {
                        orderId = doc.id
                    }
                }
                orderAdapter.updateItems(orders)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal memuat pesanan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

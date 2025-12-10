package com.example.foodorderingapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodorderingapp.R
import com.example.foodorderingapp.adapters.CartAdapter
import com.example.foodorderingapp.utils.CartManager

class CartActivity : AppCompatActivity() {
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var subtotalText: TextView
    private lateinit var deliveryFeeText: TextView
    private lateinit var totalText: TextView
    private lateinit var checkoutBtn: Button
    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        cartRecyclerView = findViewById(R.id.cartRecyclerView)
        subtotalText = findViewById(R.id.subtotalText)
        deliveryFeeText = findViewById(R.id.deliveryFeeText)
        totalText = findViewById(R.id.totalText)
        checkoutBtn = findViewById(R.id.checkoutBtn)

        cartAdapter = CartAdapter(CartManager.getCart()) { position ->
            CartManager.removeFromCart(position)
            cartAdapter.updateItems(CartManager.getCart())
            updateTotals()
        }

        cartRecyclerView.layoutManager = LinearLayoutManager(this)
        cartRecyclerView.adapter = cartAdapter

        updateTotals()

        checkoutBtn.setOnClickListener {
            if (CartManager.getCart().isEmpty()) {
                Toast.makeText(this, "Keranjang kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            startActivity(Intent(this, CheckoutActivity::class.java))
        }
    }

    private fun updateTotals() {
        val subtotal = CartManager.getSubtotal()
        val deliveryFee = 5.0
        val total = subtotal + deliveryFee

        subtotalText.text = "Rp $subtotal"
        deliveryFeeText.text = "Rp $deliveryFee"
        totalText.text = "Rp $total"
    }

    override fun onResume() {
        super.onResume()
        cartAdapter.updateItems(CartManager.getCart())
        updateTotals()
    }
}

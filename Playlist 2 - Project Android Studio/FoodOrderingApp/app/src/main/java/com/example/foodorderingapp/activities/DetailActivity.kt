package com.example.foodorderingapp.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.foodorderingapp.R
import com.example.foodorderingapp.models.CartItem
import com.example.foodorderingapp.models.MenuItem
import com.example.foodorderingapp.utils.CartManager
import com.squareup.picasso.Picasso

class DetailActivity : AppCompatActivity() {
    private lateinit var menuItem: MenuItem
    private lateinit var quantityInput: EditText
    private lateinit var addToCartBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        menuItem = intent.getSerializableExtra("menuItem", MenuItem::class.java) ?: return

        val itemImage = findViewById<ImageView>(R.id.itemImage)
        val itemName = findViewById<TextView>(R.id.itemName)
        val itemPrice = findViewById<TextView>(R.id.itemPrice)
        val itemDescription = findViewById<TextView>(R.id.itemDescription)
        quantityInput = findViewById(R.id.quantityInput)
        addToCartBtn = findViewById(R.id.addToCartBtn)

        itemName.text = menuItem.name
        itemPrice.text = "Rp ${menuItem.price}"
        itemDescription.text = menuItem.description

        if (menuItem.image.isNotEmpty()) {
            Picasso.get().load(menuItem.image).into(itemImage)
        }

        addToCartBtn.setOnClickListener {
            val quantity = quantityInput.text.toString().toIntOrNull() ?: 1
            val cartItem = CartItem(
                menuItemId = menuItem.id,
                name = menuItem.name,
                price = menuItem.price,
                quantity = quantity,
                image = menuItem.image
            )
            CartManager.addToCart(cartItem)
            Toast.makeText(this, "${menuItem.name} ditambahkan ke keranjang", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}

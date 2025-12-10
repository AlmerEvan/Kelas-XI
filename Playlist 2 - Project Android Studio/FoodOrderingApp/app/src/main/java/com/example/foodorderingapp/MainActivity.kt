package com.example.foodorderingapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodorderingapp.activities.CartActivity
import com.example.foodorderingapp.activities.DetailActivity
import com.example.foodorderingapp.activities.ProfileActivity
import com.example.foodorderingapp.adapters.MenuAdapter
import com.example.foodorderingapp.models.MenuItem
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var menuRecyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var cartBtn: Button
    private lateinit var profileBtn: Button
    private lateinit var menuAdapter: MenuAdapter
    private lateinit var firestore: FirebaseFirestore
    private var allMenuItems: List<MenuItem> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firestore = FirebaseFirestore.getInstance()

        menuRecyclerView = findViewById(R.id.menuRecyclerView)
        searchView = findViewById(R.id.searchView)
        cartBtn = findViewById(R.id.cartBtn)
        profileBtn = findViewById(R.id.profileBtn)

        menuAdapter = MenuAdapter(emptyList()) { menuItem ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("menuItem", menuItem)
            startActivity(intent)
        }

        menuRecyclerView.layoutManager = GridLayoutManager(this, 2)
        menuRecyclerView.adapter = menuAdapter

        loadMenuItems()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                filterMenuItems(newText ?: "")
                return true
            }
        })

        cartBtn.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        profileBtn.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun loadMenuItems() {
        firestore.collection("menu")
            .get()
            .addOnSuccessListener { result ->
                allMenuItems = result.documents.mapNotNull { doc ->
                    doc.toObject(MenuItem::class.java)?.apply {
                        id = doc.id
                    }
                }
                menuAdapter.updateItems(allMenuItems)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal memuat menu: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun filterMenuItems(query: String) {
        val filtered = if (query.isEmpty()) {
            allMenuItems
        } else {
            allMenuItems.filter { item ->
                item.name.contains(query, ignoreCase = true) ||
                item.category.contains(query, ignoreCase = true)
            }
        }
        menuAdapter.updateItems(filtered)
    }
}
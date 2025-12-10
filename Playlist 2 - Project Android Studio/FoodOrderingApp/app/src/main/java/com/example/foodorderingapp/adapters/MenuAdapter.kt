package com.example.foodorderingapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodorderingapp.R
import com.example.foodorderingapp.models.MenuItem
import com.squareup.picasso.Picasso

class MenuAdapter(
    private var items: List<MenuItem>,
    private val onItemClick: (MenuItem) -> Unit
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    inner class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: MenuItem) {
            itemView.findViewById<TextView>(R.id.itemName).text = item.name
            itemView.findViewById<TextView>(R.id.itemPrice).text = "Rp ${item.price}"
            itemView.findViewById<TextView>(R.id.itemDescription).text = item.description

            val imageView = itemView.findViewById<ImageView>(R.id.itemImage)
            if (item.image.isNotEmpty()) {
                Picasso.get().load(item.image).into(imageView)
            }

            itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<MenuItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}

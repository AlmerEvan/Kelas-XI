package com.example.foodorderingapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodorderingapp.R
import com.example.foodorderingapp.models.CartItem
import com.squareup.picasso.Picasso

class CartAdapter(
    private var items: List<CartItem>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: CartItem, position: Int) {
            itemView.findViewById<TextView>(R.id.cartItemName).text = item.name
            itemView.findViewById<TextView>(R.id.cartItemPrice).text = "Rp ${item.price}"
            itemView.findViewById<TextView>(R.id.cartItemQuantity).text = "Qty: ${item.quantity}"
            itemView.findViewById<TextView>(R.id.cartItemSubtotal).text = "Rp ${item.getSubtotal()}"

            val imageView = itemView.findViewById<ImageView>(R.id.cartItemImage)
            if (item.image.isNotEmpty()) {
                Picasso.get().load(item.image).into(imageView)
            }

            itemView.findViewById<Button>(R.id.deleteBtn).setOnClickListener {
                onDeleteClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<CartItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}

package com.example.foodorderingapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodorderingapp.R
import com.example.foodorderingapp.models.Order

class OrderAdapter(
    private var items: List<Order>
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(order: Order) {
            itemView.findViewById<TextView>(R.id.orderIdText).text = "Order: ${order.orderId.take(8)}"
            itemView.findViewById<TextView>(R.id.orderStatusText).text = "Status: ${order.status}"
            itemView.findViewById<TextView>(R.id.orderTotalText).text = "Total: Rp ${order.total}"
            itemView.findViewById<TextView>(R.id.orderAddressText).text = "Alamat: ${order.deliveryAddress}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<Order>) {
        items = newItems
        notifyDataSetChanged()
    }
}

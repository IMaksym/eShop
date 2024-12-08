package com.example.eshop

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CartItemsAdapter(private val items: List<Item>, private val onQuantityChanged: (Item, Int) -> Unit) : RecyclerView.Adapter<CartItemsAdapter.CartViewHolder>() {

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.item_cart_image)
        val title: TextView = itemView.findViewById(R.id.item_cart_title)
        val price: TextView = itemView.findViewById(R.id.item_cart_price)
        val count: TextView = itemView.findViewById(R.id.item_cart_count)
        val btnDecrease: TextView = itemView.findViewById(R.id.btn_decrease)
        val btnIncrease: TextView = itemView.findViewById(R.id.btn_increase)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart_card, parent, false)
        return CartViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = items[position]

        holder.title.text = item.tittle
        holder.price.text = item.price
        holder.count.text = "${item.count}"

        Glide.with(holder.image.context).load(item.image).into(holder.image)

        holder.btnDecrease.setOnClickListener {
            if (item.count > 1) {
                val newCount = item.count - 1
                item.count = newCount
                holder.count.text = "$newCount"
                onQuantityChanged(item, newCount)
            }
        }

        holder.btnIncrease.setOnClickListener {
            val newCount = item.count + 1
            item.count = newCount
            holder.count.text = "$newCount"
            onQuantityChanged(item, newCount)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

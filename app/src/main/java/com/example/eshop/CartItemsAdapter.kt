package com.example.eshop

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CartItemsAdapter(
    private val onQuantityChanged: (Item, Int) -> Unit,
    private val onDeleteItem: (Item) -> Unit
) : RecyclerView.Adapter<CartItemsAdapter.CartViewHolder>() {

    private val _items = mutableListOf<Item>()
    val items: List<Item>
        get() = _items

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(newItems: List<Item>) {
        _items.clear()
        _items.addAll(newItems)
        notifyDataSetChanged()
    }

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.item_cart_image)
        val title: TextView = itemView.findViewById(R.id.item_cart_title)
        val price: TextView = itemView.findViewById(R.id.item_cart_price)
        val count: TextView = itemView.findViewById(R.id.item_cart_count)
        val btnDecrease: TextView = itemView.findViewById(R.id.btn_decrease)
        val btnIncrease: TextView = itemView.findViewById(R.id.btn_increase)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart_card, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = _items[position]

        holder.title.text = item.tittle
        holder.count.text = "${item.count}"
        holder.price.text = formatPrice(item.price, item.count)

        Glide.with(holder.image.context).load(item.image).into(holder.image)

        holder.btnDecrease.setOnClickListener {
            if (item.count > 1) {
                val newCount = item.count - 1
                item.count = newCount
                holder.count.text = "$newCount"
                holder.price.text = formatPrice(item.price, newCount)
                onQuantityChanged(item, newCount)
            }
        }

        holder.btnIncrease.setOnClickListener {
            val newCount = item.count + 1
            item.count = newCount
            holder.count.text = "$newCount"
            holder.price.text = formatPrice(item.price, item.count)
            onQuantityChanged(item, newCount)
        }

        holder.deleteButton.setOnClickListener {
            val itemToRemove = _items[position]
            onDeleteItem(itemToRemove)
            _items.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    override fun getItemCount(): Int = _items.size

    @SuppressLint("DefaultLocale")
    private fun formatPrice(price: String, quantity: Int): String {
        val priceWithoutCurrency = price.replace(" zł", "").toDouble()
        val totalPrice = priceWithoutCurrency * quantity
        return String.format("%.2f zł", totalPrice)
    }
}

package com.example.eshop

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ItemsAdapter(
    private var items: List<Item>,
    private var context: Context,
    private val onItemClicked: (Item) -> Unit,
    private val onItemAdded: (Item) -> Unit
) : RecyclerView.Adapter<ItemsAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.item_card_image)
        val tittle: TextView = view.findViewById(R.id.item_list_tittle)
        val price: TextView = view.findViewById(R.id.item_list_price)
        val buyButton: Button = view.findViewById(R.id.item_list_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = items[position]

        holder.tittle.text = item.tittle
        holder.price.text = item.price

        val imageUrl = item.image
        if (imageUrl.startsWith("http")) {
            Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .into(holder.image)
        }

        holder.itemView.setOnClickListener {
            onItemClicked(item)
        }

        holder.buyButton.setOnClickListener {
            addItemToCart(item) { success ->
                if (success) {
                    onItemAdded(item)
                } else {
                }
            }
        }
    }

    private fun addItemToCart(item: Item, callback: (Boolean) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        val userEmail = auth.currentUser?.email?.replace(".", ",")
        if (userEmail == null) {
            callback(false)
            return
        }

        val database = FirebaseDatabase.getInstance()
        val userCartRef = database.getReference("category/users/$userEmail/${item.id}")

        userCartRef.get().addOnSuccessListener { snapshot ->
            val currentCount = snapshot.value?.toString()?.toIntOrNull() ?: 0
            userCartRef.setValue(currentCount + 1).addOnSuccessListener {
                callback(true)
            }.addOnFailureListener {
                callback(false)
            }
        }.addOnFailureListener {
            callback(false)
        }
    }
}

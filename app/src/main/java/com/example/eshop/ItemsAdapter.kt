package com.example.eshop

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide


class ItemsAdapter(private var items: List<Item>, private var context: Context) : RecyclerView.Adapter<ItemsAdapter.MyViewHolder>() {





    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.item_card_image)
        val tittle: TextView = view.findViewById(R.id.item_list_tittle)
        val price: TextView = view.findViewById(R.id.item_list_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    @SuppressLint("DiscouragedApi")
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
    }

}
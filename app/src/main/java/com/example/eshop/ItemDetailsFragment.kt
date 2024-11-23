package com.example.eshop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide

class ItemDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_item_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageView: ImageView = view.findViewById(R.id.item_activity_image)
        val titleView: TextView = view.findViewById(R.id.item_activity_tittle)
        val priceView: TextView = view.findViewById(R.id.item_activity_price)

        arguments?.let {
            val itemImage = it.getString("item_image")
            val itemTitle = it.getString("item_title")
            val itemPrice = it.getString("item_price")

            Glide.with(requireContext())
                .load(itemImage)
                .into(imageView)

            titleView.text = itemTitle
            priceView.text = itemPrice
        }
    }
}


package com.example.eshop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.database.*

class Catalog : Fragment() {

    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_catalog, container, false)

        val womanCategoryImageView: ImageView = view.findViewById(R.id.woman_category)
        val menCategoryImageView: ImageView = view.findViewById(R.id.men_category)

        database = FirebaseDatabase.getInstance().reference

        loadImages(womanCategoryImageView, menCategoryImageView)

        womanCategoryImageView.setOnClickListener {
            openCatalogItems("woman")
        }

        menCategoryImageView.setOnClickListener {
            openCatalogItems("men")
        }

        return view
    }

    private fun loadImages(womanImageView: ImageView, menImageView: ImageView) {
        database.child("category/image/womencategoryimage").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val womanImageUrl = snapshot.getValue(String::class.java)
                womanImageUrl?.let {
                    Glide.with(requireContext())
                        .load(it)
                        .into(womanImageView)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        database.child("category/image/mancategoryimage").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val menImageUrl = snapshot.getValue(String::class.java)
                menImageUrl?.let {
                    Glide.with(requireContext())
                        .load(it)
                        .into(menImageView)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun openCatalogItems(category: String) {
        val bundle = Bundle().apply {
            putString("category", category)
        }
        findNavController().navigate(R.id.catalogItems, bundle)
    }
}

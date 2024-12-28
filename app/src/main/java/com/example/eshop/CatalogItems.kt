package com.example.eshop

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class CatalogItems : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var itemsAdapter: ItemsAdapter
    private val items = mutableListOf<Item>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_catalog_items, container, false)

        val itemsList: RecyclerView = view.findViewById(R.id.CatalogitemsList)
        itemsAdapter = ItemsAdapter(items, requireContext(), { item ->
            openItemDetails(item)
        }, { item ->
        })


        itemsList.layoutManager = GridLayoutManager(requireContext(), 2)
        itemsList.adapter = itemsAdapter

        val category = arguments?.getString("category") ?: "men"


        loadProductsFromDatabase(category)

        return view
    }

    private fun loadProductsFromDatabase(category: String) {
        database = FirebaseDatabase.getInstance().getReference("category/$category")

        database.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                items.clear()
                for (productSnapshot in snapshot.children) {
                    val item = productSnapshot.getValue(Item::class.java)
                    if (item != null) {
                        items.add(item)
                    }
                }
                itemsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error loading data: ${error.message}")
            }
        })
    }

    private fun openItemDetails(item: Item) {
        val bundle = Bundle().apply {
            putString("item_image", item.image)
            putString("item_title", item.tittle)
            putString("item_price", item.price)
        }
        findNavController().navigate(R.id.navigation_item_details, bundle)
    }
}

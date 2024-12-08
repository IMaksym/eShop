package com.example.eshop

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CartItemsAdapter
    private val items = mutableListOf<Item>()
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)
        recyclerView = view.findViewById(R.id.cartProducts)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = CartItemsAdapter(items) { item, newCount ->
            updateItemCountInDatabase(item, newCount)
        }
        recyclerView.adapter = adapter

        loadUserProducts()

        return view
    }

    private fun loadUserProducts() {
        val userId = auth.currentUser?.email?.replace(".", ",") ?: return
        val userProductsRef = database.getReference("category/users/$userId")

        userProductsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                items.clear()

                for (productSnapshot in snapshot.children) {
                    val key = productSnapshot.key ?: continue
                    val quantity = productSnapshot.value.toString().toInt()

                    val categoryCode = key.substring(0, 1)
                    val productId = key

                    val category = when (categoryCode) {
                        "3" -> "men"
                        "4" -> "woman"
                        else -> continue
                    }

                    val categoryRef = database.getReference("category/$category")
                    categoryRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        @SuppressLint("NotifyDataSetChanged")
                        override fun onDataChange(categorySnapshot: DataSnapshot) {
                            for (product in categorySnapshot.children) {
                                val productIdFromDb = product.child("id").value.toString()

                                if (productIdFromDb == productId) {
                                    val productData = product.getValue(Item::class.java)
                                    if (productData != null) {
                                        productData.count = quantity
                                        items.add(productData)
                                        adapter.notifyDataSetChanged()
                                        break
                                    }
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun updateItemCountInDatabase(item: Item, newCount: Int) {
        val userId = auth.currentUser?.email?.replace(".", ",") ?: return
        val userProductRef = database.getReference("category/users/$userId/${item.id}")

        userProductRef.setValue(newCount)
            .addOnSuccessListener {
            }
            .addOnFailureListener {
            }
    }
}

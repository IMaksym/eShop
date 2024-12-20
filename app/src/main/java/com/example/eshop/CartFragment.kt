package com.example.eshop

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CartItemsAdapter
    private lateinit var totalPriceTextView: TextView
    private val items = mutableListOf<Item>()
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    @SuppressLint("DefaultLocale")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)
        recyclerView = view.findViewById(R.id.cartProducts)
        totalPriceTextView = view.findViewById(R.id.totalPrice)
        val deleteAllButton: ImageView = view.findViewById(R.id.deleteAllButton)

        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = CartItemsAdapter(items, { item, newCount ->
            updateItemCountInDatabase(item, newCount)
        }) { totalPrice ->
            totalPriceTextView.text = String.format("%.2f zł", totalPrice)
        }

        recyclerView.adapter = adapter

        loadUserProducts()

        deleteAllButton.setOnClickListener {
            onDeleteAllItemsClicked()
        }

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

                                if (productIdFromDb == key) {
                                    val productData = product.getValue(Item::class.java)
                                    if (productData != null) {
                                        productData.count = quantity
                                        items.add(productData)
                                        adapter.notifyDataSetChanged()
                                        break
                                    }
                                }
                            }
                            updateTotalPrice()
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun updateItemCountInDatabase(item: Item, newCount: Int) {
        val userId = auth.currentUser?.email?.replace(".", ",") ?: return
        val userProductRef = database.getReference("category/users/$userId/${item.id}")

        userProductRef.setValue(newCount)
            .addOnSuccessListener {
                updateTotalPrice()
            }
            .addOnFailureListener {
            }
    }

    @SuppressLint("DefaultLocale")
    private fun updateTotalPrice() {
        val totalPrice = items.sumByDouble {
            val priceWithoutCurrency = it.price.replace(" zł", "").toDouble()
            priceWithoutCurrency * it.count
        }
        totalPriceTextView.text = String.format("%.2f zł", totalPrice)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun onDeleteAllItemsClicked() {
        val userId = auth.currentUser?.email?.replace(".", ",") ?: return
        val userProductsRef = database.getReference("category/users/$userId")

        userProductsRef.removeValue()
            .addOnSuccessListener {
                items.clear()
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
            }
        ensureUserExists()
    }


    private fun ensureUserExists() {
        val userId = auth.currentUser?.email?.replace(".", ",") ?: return
        val usersRef: DatabaseReference = database.getReference("category/users")

        usersRef.child(userId).setValue("")

    }

}

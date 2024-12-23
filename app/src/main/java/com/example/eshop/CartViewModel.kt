package com.example.eshop

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartViewModel : ViewModel() {

    private val _items = MutableLiveData<MutableList<Item>>(mutableListOf())
    val items: LiveData<MutableList<Item>> get() = _items

    private val _totalPrice = MutableLiveData(0.0)
    val totalPrice: LiveData<Double> get() = _totalPrice

    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    init {
        loadUserProducts()
    }

    private fun loadUserProducts() {
        val userId = auth.currentUser?.email?.replace(".", ",") ?: return
        val userProductsRef = database.getReference("category/users/$userId")

        userProductsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val loadedItems = mutableListOf<Item>()

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
                        override fun onDataChange(categorySnapshot: DataSnapshot) {
                            for (product in categorySnapshot.children) {
                                val productIdFromDb = product.child("id").value.toString()
                                if (productIdFromDb == key) {
                                    val productData = product.getValue(Item::class.java)
                                    if (productData != null) {
                                        productData.count = quantity
                                        loadedItems.add(productData)
                                    }
                                }
                            }
                            _items.value = loadedItems
                            updateTotalPrice()
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun updateItemCount(item: Item, newCount: Int) {
        val userId = auth.currentUser?.email?.replace(".", ",") ?: return
        val userProductRef = database.getReference("category/users/$userId/${item.id}")

        userProductRef.setValue(newCount).addOnSuccessListener {
            item.count = newCount
            updateTotalPrice()
        }
    }

    fun deleteItem(item: Item) {
        val userId = auth.currentUser?.email?.replace(".", ",") ?: return
        val userProductRef = database.getReference("category/users/$userId/${item.id}")

        userProductRef.removeValue().addOnSuccessListener {
            _items.value = _items.value?.filterNot { it.id == item.id }?.toMutableList()
            updateTotalPrice()
        }.addOnFailureListener { exception ->
            // Логирование ошибок (опционально)
            Log.e("DeleteError", "Ошибка удаления элемента: ${exception.message}")
        }
    }

    private fun updateTotalPrice() {
        val total = _items.value?.sumOf {
            val priceWithoutCurrency = it.price.replace(" zł", "").toDouble()
            priceWithoutCurrency * it.count
        } ?: 0.0
        _totalPrice.value = total
    }

    fun deleteAllItems() {
        val userId = auth.currentUser?.email?.replace(".", ",") ?: return
        val userProductsRef = database.getReference("category/users/$userId")

        userProductsRef.removeValue().addOnSuccessListener {
            _items.value = mutableListOf()
            _totalPrice.value = 0.0
            ensureUserExists()
        }
    }

    private fun ensureUserExists() {
        val userId = auth.currentUser?.email?.replace(".", ",") ?: return
        val usersRef: DatabaseReference = database.getReference("category/users")
        usersRef.child(userId).setValue("")
    }
}

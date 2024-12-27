package com.example.eshop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ItemDetailsFragment : Fragment() {

    private lateinit var item: Item

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
        val addToCartButton: Button = view.findViewById(R.id.addToCartButton)

        // Получаем данные о товаре из аргументов
        arguments?.let {
            val itemImage = it.getString("item_image")
            val itemTitle = it.getString("item_title")
            val itemPrice = it.getString("item_price")
            val itemId = it.getString("item_id")?.toIntOrNull() ?: 0 // Преобразуем ID в Int

            // Проверяем и создаем объект Item
            item = Item(itemId, itemTitle ?: "", itemPrice ?: "", itemImage ?: "")

            // Загружаем изображение товара
            Glide.with(requireContext())
                .load(itemImage)
                .into(imageView)

            titleView.text = itemTitle
            priceView.text = itemPrice
        }

        // Обработчик для добавления товара в корзину
        addToCartButton.setOnClickListener {
            addItemToCart(item) { success ->
                if (success) {
                    showToast("Товар добавлен в корзину")
                } else {
                    showToast("Ошибка при добавлении товара в корзину")
                }
            }
        }
    }

    // Метод для добавления товара в корзину
    private fun addItemToCart(item: Item, callback: (Boolean) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        val userEmail = auth.currentUser?.email?.replace(".", ",")
        if (userEmail == null) {
            callback(false)
            return
        }

        val database = FirebaseDatabase.getInstance()
        val userCartRef = database.getReference("category/users/$userEmail/cart/${item.id}")

        // Получаем текущие данные о товаре в корзине
        userCartRef.get().addOnSuccessListener { snapshot ->
            val currentCount = snapshot.value?.toString()?.toIntOrNull() ?: 0
            // Обновляем количество товара на +1
            userCartRef.setValue(currentCount + 1).addOnSuccessListener {
                callback(true)
            }.addOnFailureListener {
                callback(false)
            }
        }.addOnFailureListener {
            callback(false)
        }
    }

    // Утилита для показа тост-сообщений
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}


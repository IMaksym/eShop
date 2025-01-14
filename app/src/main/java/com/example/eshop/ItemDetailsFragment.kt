package com.example.eshop

import android.graphics.Color
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
    private lateinit var addToCartButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_item_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().findViewById<View>(R.id.bottom_menu)?.visibility = View.GONE

        val imageView: ImageView = view.findViewById(R.id.item_activity_image)
        val titleView: TextView = view.findViewById(R.id.item_activity_tittle)
        val priceView: TextView = view.findViewById(R.id.item_activity_price)
        addToCartButton = view.findViewById(R.id.addToCartButton)

        arguments?.let {
            val itemId = it.getInt("item_id", 0)
            val itemTitle = it.getString("item_title", "Unknown")
            val itemPrice = it.getString("item_price", "0 zł")
            val itemImage = it.getString("item_image", "")
            item = Item(itemId, itemTitle, itemPrice, itemImage)
            Glide.with(requireContext()).load(itemImage).into(imageView)
            titleView.text = itemTitle
            priceView.text = itemPrice
        }

        checkIfItemInCart { isInCart ->
            if (isInCart) {
                setButtonInCart()
            } else {
                setButtonNotInCart()
            }
        }

        addToCartButton.setOnClickListener {
            addItemToCart(item) { success ->
                if (success) {
                    showToast("Item added to cart")
                    setButtonInCart()
                } else {
                    showToast("Error adding item to cart")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<View>(R.id.bottom_menu)?.visibility = View.GONE
    }

    override fun onDestroyView() {
        requireActivity().findViewById<View>(R.id.bottom_menu)?.visibility = View.VISIBLE
        super.onDestroyView()
    }

    private fun setButtonInCart() {
        addToCartButton.text = "In cart"
        addToCartButton.setBackgroundColor(Color.GRAY)
    }

    private fun setButtonNotInCart() {
        addToCartButton.text = "Add to cart"
        addToCartButton.setBackgroundColor(Color.parseColor("#4CAF50"))
    }

    private fun checkIfItemInCart(callback: (Boolean) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        val userEmail = auth.currentUser?.email?.replace(".", ",")
        if (userEmail == null) {
            callback(false)
            return
        }
        val ref = FirebaseDatabase.getInstance()
            .getReference("category/users/$userEmail/${item.id}")
        ref.get().addOnSuccessListener { snapshot ->
            val count = snapshot.value?.toString()?.toIntOrNull() ?: 0
            callback(count > 0)
        }.addOnFailureListener {
            callback(false)
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
            val currentCountString = snapshot.value?.toString()?.trim()?.removeSurrounding("[", "]")
            val currentCount = currentCountString?.toIntOrNull() ?: 0
            userCartRef.setValue(currentCount + 1).addOnSuccessListener {
                callback(true)
            }.addOnFailureListener {
                callback(false)
            }
        }.addOnFailureListener {
            callback(false)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}

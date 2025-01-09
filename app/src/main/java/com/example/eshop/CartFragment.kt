package com.example.eshop

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CartFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var totalPriceTextView: TextView
    private lateinit var deleteAllButton: ImageView
    private val viewModel: CartViewModel by viewModels()
    private lateinit var adapter: CartItemsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)
        recyclerView = view.findViewById(R.id.cartProducts)
        totalPriceTextView = view.findViewById(R.id.totalPrice)
        deleteAllButton = view.findViewById(R.id.deleteAllButton)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = CartItemsAdapter(
            onQuantityChanged = { item, newCount ->
                viewModel.updateItemCount(item, newCount)
            },
            onDeleteItem = { item ->
                viewModel.deleteItem(item)
            },
            onItemClick = { item ->
                openItemDetails(item)
            }
        )
        recyclerView.adapter = adapter
        observeViewModel()
        deleteAllButton.setOnClickListener {
            viewModel.deleteAllItems()
        }
        return view
    }

    @SuppressLint("DefaultLocale")
    private fun observeViewModel() {
        viewModel.items.observe(viewLifecycleOwner) { items ->
            adapter.setItems(items)
        }
        viewModel.totalPrice.observe(viewLifecycleOwner) { totalPrice ->
            totalPriceTextView.text = String.format("%.2f zł", totalPrice)
        }
    }

    private fun openItemDetails(item: Item) {
        val bundle = bundleOf(
            "item_id" to item.id,
            "item_title" to item.tittle,
            "item_price" to item.price,
            "item_image" to item.image
        )
        requireActivity().findViewById<View>(R.id.bottom_menu)?.visibility = View.GONE
        findNavController().navigate(R.id.action_cart_to_itemDetails, bundle)
    }
}

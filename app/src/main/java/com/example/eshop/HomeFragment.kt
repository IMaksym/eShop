package com.example.eshop

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val itemsList: RecyclerView = view.findViewById(R.id.itemsList)
        val items = arrayListOf<Item>()

        items.add(Item(1, "white", "T-Shirt", "99.9 zł"))
        items.add(Item(2, "white1", "T-Shirt", "199.9 zł"))
        items.add(Item(3, "white2", "T-Shirt", "109.9 zł"))
        items.add(Item(4, "black", "T-Shirt", "199.9 zł"))
        items.add(Item(5, "black1", "T-Shirt", "129.9 zł"))
        items.add(Item(6, "black2", "T-Shirt", "125.9 zł"))
        items.add(Item(7, "blue", "T-Shirt", "299.9 zł"))
        items.add(Item(8, "blue1", "T-Shirt", "99.9 zł"))

        itemsList.layoutManager = GridLayoutManager(requireContext(), 2)
        itemsList.adapter = ItemsAdapter(items, requireContext())

        return view
    }
}

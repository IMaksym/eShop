package com.example.eshop

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuInflater
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val btnNavView = findViewById<BottomNavigationView>(R.id.menu_page)
        val controller = findNavController(R.id.fragmentContainerView)
        btnNavView.setupWithNavController(controller)



        supportActionBar?.apply {
            title = "ClothesShop"
            setDisplayShowTitleEnabled(true)
            setDisplayShowHomeEnabled(false)
        }
        toolbar.title = "ClothesShop"

        toolbar.post {
            val titleTextView = toolbar.findViewById<TextView>(androidx.appcompat.R.id.action_bar_title)
            titleTextView?.let {
                it.layoutParams.width = Toolbar.LayoutParams.MATCH_PARENT
                it.gravity = Gravity.CENTER
                it.setTextColor(Color.WHITE)
            }
        }

        val itemsList: RecyclerView = findViewById(R.id.itemsList)
        val items = arrayListOf<Item>()



        items.add(Item(1, "white", "T-Shirt", "99.9 zł"))
        items.add(Item(2, "white1", "T-Shirt",  "199.9 zł"))
        items.add(Item(3, "white2", "T-Shirt",  "109.9 zł"))
        items.add(Item(4, "black", "T-Shirt",  "199.9 zł"))
        items.add(Item(5, "black1", "T-Shirt",  "129.9 zł"))
        items.add(Item(6, "black2", "T-Shirt", "125.9 zł"))
        items.add(Item(7, "blue", "T-Shirt",  "299.9 zł"))
        items.add(Item(8, "blue1", "T-Shirt",  "99.9 zł"))

        itemsList.layoutManager = GridLayoutManager(this, 2)
        itemsList.adapter = ItemsAdapter(items, this)


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }


}
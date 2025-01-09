package com.example.eshop

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.eshop.databinding.ActivityMainBinding
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_catalog,
                R.id.navigation_cart
            )
        )
        navView.setupWithNavController(navController)
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    navController.navigate(R.id.navigation_home)
                    true
                }
                R.id.navigation_catalog -> {
                    navController.navigate(R.id.navigation_catalog)
                    true
                }
                R.id.navigation_cart -> {
                    navController.navigate(R.id.navigation_cart)
                    true
                }
                else -> false
            }
        }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.navigation_item_details) {
                navView.visibility = View.GONE
            } else {
                navView.visibility = View.VISIBLE
            }
        }
        observeCartItemCount { count ->
            if (count > 0) {
                val badge = navView.getOrCreateBadge(R.id.navigation_cart)
                badge.isVisible = true
                badge.number = count
                badge.badgeTextColor = ContextCompat.getColor(this, android.R.color.white)
            } else {
                navView.removeBadge(R.id.navigation_cart)
            }
        }
    }

    private fun observeCartItemCount(onCountChanged: (Int) -> Unit) {
        val userId = auth.currentUser?.email?.replace(".", ",") ?: return
        val userProductsRef = database.getReference("category/users/$userId")
        userProductsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalCount = 0
                for (productSnapshot in snapshot.children) {
                    val quantity = productSnapshot.value?.toString()?.toIntOrNull() ?: 0
                    totalCount += quantity
                }
                onCountChanged(totalCount)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}

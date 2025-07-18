package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.myapplication.databinding.ActivityMainBinding
import androidx.navigation.ui.NavigationUI
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val navController by lazy { findNavController(R.id.nav_host_fragment_activity_main) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase Realtime Database connection check
        val connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected")
        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                if (connected) {
                    Log.d("FIREBASE", "✅ Connected to Firebase Realtime Database")
                } else {
                    Log.d("FIREBASE", "❌ Not connected to Firebase Realtime Database")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FIREBASE", "Database error: ${error.message}")
            }
        })

        setupNavigation()
    }

    private fun setupNavigation() {
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_dashboard,
                R.id.navigation_prediction, // Added prediction fragment
                R.id.navigation_notifications
            )
        )

        // Setup bottom navigation with controller
        binding.navView.setupWithNavController(navController)

        // Handle navigation item selection
        binding.navView.setOnItemSelectedListener { item ->
            NavigationUI.onNavDestinationSelected(item, navController)
            true
        }

        // Optional: Handle item reselection if needed
        binding.navView.setOnItemReselectedListener { item ->
            // You can add scroll-to-top behavior here if needed
        }
    }

    override fun onResume() {
        super.onResume()
        binding.navView.selectedItemId = navController.currentDestination?.id
            ?: R.id.navigation_home
    }
}
package com.example.quanlych

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.quanlych.data.DatabaseHelper
import com.example.quanlych.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_content_main)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_cart, R.id.nav_slideshow, R.id.nav_login, R.id.nav_register
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Handle navigation item clicks
        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_login -> {
                    // Navigate to login screen and close the drawer
                    navController.navigate(R.id.nav_login)
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_logout -> {
                    // Clear user data from SharedPreferences
                    val sharedPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        clear()
                        apply()
                    }

                    // Update Navigation Header with empty user info
                    updateNavHeader()

                    // Navigate to login screen
                    navController.navigate(R.id.nav_login)

                    // Close the drawer
                    drawerLayout.closeDrawers()
                    true
                }
                else -> {
                    // Handle other menu items
                    navController.navigate(item.itemId)
                    drawerLayout.closeDrawers()
                    true
                }
            }
        }

        // Add a listener to change the toolbar visibility based on the destination
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.nav_login, R.id.nav_register, R.id.nav_admin_home,
                R.id.nav_admin_chart, R.id.nav_admin_user, R.id.nav_admin_product,
                R.id.nav_admin_order ,R.id.nav_category , R.id.nav_gallery,R.id.nav_cart-> {
                    binding.appBarMain.toolbar.visibility = View.GONE
                }
                else -> {
                    binding.appBarMain.toolbar.visibility = View.VISIBLE
                }
            }
        }

        // Show/hide FAB based on the destination
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val fab: View = findViewById(R.id.fab)
            fab.visibility = if (destination.id in listOf(
                    R.id.editProductFragment,R.id.addProductFragment,R.id.nav_register,R.id.nav_admin_user, R.id.nav_admin_product, R.id.nav_category, R.id.nav_login, R.id.nav_admin_home, R.id.nav_admin_chart, R.id.nav_admin_order
                )) View.GONE else View.VISIBLE
        }

        // Set up FloatingActionButton click listener
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            // Navigate to the CartFragment
            navController.navigate(R.id.nav_cart)
        }

        databaseHelper = DatabaseHelper(this)
//
//         Add a test product


        // Retrieve and log all products to check if the test product was added
        val products = databaseHelper.getAllProducts()
        products.forEach { product ->
            Log.d("MainActivity", "Product - ID: ${product.id}, Name: ${product.name}, Description: ${product.description}, Price: ${product.price}, Quantity: ${product.quantity}")
        }

        // Update Navigation Header with user info
        updateNavHeader()
    }

    fun updateNavHeader() {
        val headerView: View = binding.navView.getHeaderView(0)
        val navUsername: TextView = headerView.findViewById(R.id.nav_username)
        val navEmail: TextView = headerView.findViewById(R.id.nav_email)

        val sharedPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", "Username")
        val email = sharedPref.getString("email", "email@example.com")

        navUsername.text = username
        navEmail.text = email
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}

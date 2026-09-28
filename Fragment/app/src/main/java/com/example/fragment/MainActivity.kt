package com.example.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private var isLoggedIn = false

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkAndRequestPermissions()

        bottomNav = findViewById(R.id.bottom_navigation)

        // Populate menu programmatically since menu file is missing
        bottomNav.menu.apply {
            add(0, R.id.nav_back, 0, "Back").setIcon(android.R.drawable.ic_menu_revert)
            add(0, R.id.nav_login, 1, "Login").setIcon(android.R.drawable.ic_menu_set_as)
            add(0, R.id.nav_dashboard, 2, "Dashboard").setIcon(android.R.drawable.ic_dialog_info)
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_back -> {
                    onBackPressedDispatcher.onBackPressed()
                    false
                }
                R.id.nav_login -> {
                    isLoggedIn = false
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, LoginFragment())
                        .commit()
                    true
                }
                R.id.nav_dashboard -> {
                    if (isLoggedIn) {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, DashboardFragment())
                            .commit()
                        true
                    } else {
                        Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
                        false
                    }
                }
                else -> false
            }
        }

        if (savedInstanceState == null) {
            bottomNav.selectedItemId = R.id.nav_login
        }
    }

    private fun checkAndRequestPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        val needed = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (needed.isNotEmpty()) {
            requestPermissionsLauncher.launch(needed.toTypedArray())
        }
    }

    fun navigateToDashboard() {
        isLoggedIn = true
        bottomNav.selectedItemId = R.id.nav_dashboard
    }

    fun navigateToLab() {
        if (isLoggedIn) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FragmentLab())
                .addToBackStack(null)
                .commit()
        }
    }

    fun logout() {
        isLoggedIn = false
        supportFragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, LoginFragment())
            .commit()
        bottomNav.selectedItemId = R.id.nav_login
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
    }
}

package com.example.fasdd_android

import android.content.ContentValues.TAG
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.fasdd_android.databinding.ActivityMainBinding
import com.google.android.material.badge.BadgeDrawable
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    val db = FirebaseFirestore.getInstance()
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences("user_id", MODE_PRIVATE)
        val badgeDrawable = binding.bottomNavigation.getOrCreateBadge(R.id.nav_notif)
        badgeDrawable.isVisible = true
        lifecycleScope.launch {
            badgeDrawable.number = getUnreadNotifCount()
        }
        badgeDrawable.backgroundColor = getColor(R.color.green)
        badgeDrawable.badgeTextColor = getColor(R.color.white)


        replaceFragment(HomeFragment())

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    badgeDrawable.backgroundColor = getColor(R.color.green)
                    badgeDrawable.badgeTextColor = getColor(R.color.white)
                    replaceFragment(HomeFragment())
                }

                R.id.nav_scan -> {
                    badgeDrawable.backgroundColor = getColor(R.color.green)
                    badgeDrawable.badgeTextColor = getColor(R.color.white)
                    replaceFragment(ScanFragment())
                }
                R.id.nav_notif -> {
                    badgeDrawable.backgroundColor = getColor(R.color.white)
                    badgeDrawable.badgeTextColor = getColor(R.color.green)

                    replaceFragment(NotifFragment())
                }
                else -> {}
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragment)
        fragmentTransaction.commit()
    }

    private suspend fun getUnreadNotifCount(): Int {
        var unreadCount = 0
        try {
            val userId = sharedPreferences.getString("user_id", null)
            val userRef = db.collection("users").document(userId!!)
            val documents = withContext(Dispatchers.IO) {
                db.collection("notifications")
                    .whereEqualTo("user_id", userRef)
                    .get()
                    .await()
            }
            for (document in documents) {
                val alreadyRead = document.getBoolean("already_read") ?: false
                if (!alreadyRead) {
                    unreadCount++
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error getting documents: ", e)
        }
        return unreadCount
    }
}
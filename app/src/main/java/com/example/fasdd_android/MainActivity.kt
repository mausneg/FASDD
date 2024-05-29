package com.example.fasdd_android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.fasdd_android.databinding.ActivityMainBinding
import com.google.android.material.badge.BadgeDrawable

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val badgeDrawable = binding.bottomNavigation.getOrCreateBadge(R.id.nav_notif)
        badgeDrawable.isVisible = true
        badgeDrawable.number = 3
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
}
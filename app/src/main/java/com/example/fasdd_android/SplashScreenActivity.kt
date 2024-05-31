package com.example.fasdd_android

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fasdd_android.databinding.ActivitySplashscreenBinding

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashscreenBinding
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences("user_id", MODE_PRIVATE)
        binding.logofasdd.alpha = 0f
        binding.logofasdd.animate().setDuration(1500).alpha(1f).withEndAction {
            if (sharedPreferences.getString("user_id", null) == null) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()

        }
    }
}


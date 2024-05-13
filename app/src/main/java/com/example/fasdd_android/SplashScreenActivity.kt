package com.example.fasdd_android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fasdd_android.databinding.ActivityFstshowBinding

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFstshowBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFstshowBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.logofasdd.alpha = 0f
        binding.logofasdd.animate().setDuration(1500).alpha(1f).withEndAction {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        }
    }
}


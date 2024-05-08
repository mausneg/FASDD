package com.example.fasdd_android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fasdd_android.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    lateinit var binding : ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btnBackProfileListener()
        btnLogoutListener()
    }

    private fun btnBackProfileListener(){
        binding.backProfile.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun btnLogoutListener(){
        binding.btn4Profile.setOnClickListener {
            startActivity(Intent(this, FstShowActivity::class.java))
        }
    }
}
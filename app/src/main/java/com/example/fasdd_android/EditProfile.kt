package com.example.fasdd_android

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.fasdd_android.databinding.ActivityEditProfileBinding

class EditProfile : AppCompatActivity() {
    lateinit var binding : ActivityEditProfileBinding
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences("user_id", MODE_PRIVATE)
        val imageUri = sharedPreferences.getString("profile_url", null)
        val fullName = sharedPreferences.getString("full_name", null)
        val email = sharedPreferences.getString("email", null)
        binding.inputFullname.setText(fullName)
        binding.inputEmail.setText(email)
        try {
            Glide.with(this)
                .load(imageUri)
                .into(binding.profilePicture)
        } catch (e: Exception) {
            binding.profilePicture.setImageResource(R.drawable.profile)
        }
        btnBackEproListener()
    }

    private fun btnBackEproListener(){
        binding.backEpro.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}
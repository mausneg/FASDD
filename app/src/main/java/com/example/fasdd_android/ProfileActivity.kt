package com.example.fasdd_android

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.fasdd_android.databinding.ActivityProfileBinding
import com.google.firebase.ktx.Firebase

class ProfileActivity : AppCompatActivity() {
    lateinit var binding : ActivityProfileBinding
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences("user_id", MODE_PRIVATE)
        val imageUri = sharedPreferences.getString("profile_url", null)
        val fullName = sharedPreferences.getString("full_name", null)
        val email = sharedPreferences.getString("email", null)
        binding.profileFullname.text = fullName
        binding.profileEmail.text = email
        try {
            Glide.with(this)
                .load(imageUri)
                .into(binding.profilePicture)
        } catch (e: Exception) {
            binding.profilePicture.setImageResource(R.drawable.profile)
        }
        btnBackProfileListener()
        btnLogoutListener()
        btnEditListener()
    }

    private fun btnBackProfileListener(){
        binding.backProfile.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun btnLogoutListener(){
        binding.btn4Profile.setOnClickListener {
            val sharedPreferences = getSharedPreferences("user_id", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.remove("user_id")
            editor.remove("full_name")
            editor.remove("email")
            editor.remove("password")
            editor.apply()
            startActivity(Intent(this, SplashScreenActivity::class.java))
            finish()
        }
    }

    private fun btnEditListener(){
        binding.btn2Profile.setOnClickListener {
            startActivity(Intent(this, EditProfile::class.java))
            finish()
        }
    }
}
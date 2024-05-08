package com.example.fasdd_android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fasdd_android.databinding.ActivityEditProfileBinding
import com.example.fasdd_android.databinding.ActivityProfileBinding

class EditProfile : AppCompatActivity() {
    lateinit var binding : ActivityEditProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btnBackEproListener()
    }

    private fun btnBackEproListener(){
        binding.backEpro.setOnClickListener {
            startActivity(Intent(this, Profile::class.java))
        }
    }
}
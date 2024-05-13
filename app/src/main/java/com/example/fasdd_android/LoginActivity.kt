package com.example.fasdd_android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fasdd_android.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    lateinit var binding : ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btnBackLoginListener()
        txtRegisterListener()
        btnLoginListener()
    }

    private fun btnBackLoginListener(){
        binding.backLogin.setOnClickListener {
            startActivity(Intent(this, SplashScreenActivity::class.java))
        }
    }

    private fun txtRegisterListener(){
        binding.text4Login.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun btnLoginListener(){
        binding.btn1Login.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

}
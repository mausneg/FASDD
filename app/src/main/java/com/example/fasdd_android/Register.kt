package com.example.fasdd_android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fasdd_android.databinding.ActivityRegisterBinding

class Register : AppCompatActivity() {
    lateinit var binding : ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btnBackRegisterListener()
        txtLoginListener()
        btnRegisterListener()
    }

    private fun btnBackRegisterListener(){
        binding.backRegister.setOnClickListener {
            startActivity(Intent(this, FstShow::class.java))
        }
    }

    private fun txtLoginListener(){
        binding.text4Regis.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
        }
    }

    private fun btnRegisterListener(){
        binding.btn1Regis.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
        }
    }


}
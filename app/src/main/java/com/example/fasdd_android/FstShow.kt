package com.example.fasdd_android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fasdd_android.databinding.ActivityFstshowBinding

class FstShow : AppCompatActivity() {
    lateinit var binding : ActivityFstshowBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFstshowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btnLoginListener()
        btnRegisterListener()
    }

    private fun btnLoginListener(){
        binding.btn1Fstshow.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
        }
    }

    private fun btnRegisterListener(){
        binding.btn2Fstshow.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
        }
    }
}
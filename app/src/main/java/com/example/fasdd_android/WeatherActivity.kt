package com.example.fasdd_android

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import com.example.fasdd_android.databinding.ActivityWeatherBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

class WeatherActivity : AppCompatActivity() {
    private lateinit var runnable: Runnable
    private lateinit var binding: ActivityWeatherBinding
    private val handler = Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupOnClickListeners()
        setupWeatherCard()
        setupDateTimeUpdater()
    }
    private fun setupWeatherCard() {
        val weatherTypes = arrayOf("sunny", "cloudy", "rainy")
        val weather = weatherTypes[Random.nextInt(weatherTypes.size)]

        binding.ivWeather.setImageResource(
            when (weather) {
                "sunny" -> R.drawable.ic_card_weather_sunny
                "cloudy" -> R.drawable.ic_card_weather_cloudy
                "rainy" -> R.drawable.ic_card_weather_rainy
                else -> R.drawable.ic_card_weather_sunny
            }
        )
    }
    private fun setupDateTimeUpdater() {
        runnable = object : Runnable {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun run() {
                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")
                val formatted = current.format(formatter)
                binding.date.text = formatted

                handler.postDelayed(this, 1000)
            }
        }

        handler.post(runnable)
    }
    private fun setupOnClickListeners() {
        binding.backtohomebutton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.MoreWeatherButton.setOnClickListener {
            val intent = Intent(this, WeatherMoreActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}
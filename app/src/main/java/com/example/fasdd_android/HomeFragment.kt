package com.example.fasdd_android

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.example.fasdd_android.databinding.FragmentHomeBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.cardWeather.setOnClickListener{
            val intent = Intent(context, WeatherActivity::class.java)
            startActivity(intent)
        }
        binding.moreNews.setOnClickListener{
            val intent = Intent(context, NewsActivity::class.java)
            startActivity(intent)
        }
//        binding.profilePicture.setOnClickListener{
//            val intent = Intent(context, ProfileActivity::class.java)
//            startActivity(intent)
//        }
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
        binding.cardWeather.setBackgroundResource(
            when (weather) {
                "Sunny" -> R.drawable.bg_card_weather_sunny
                else -> R.drawable.bg_card_weather_sunny
            }
        )
        runnable = object : Runnable {
            override fun run() {
                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")
                val formatted = current.format(formatter)
                binding.date.text = formatted
                val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
                val timeFormatted = current.format(timeFormatter)
                binding.cardTime.text = timeFormatted
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(runnable)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }
}
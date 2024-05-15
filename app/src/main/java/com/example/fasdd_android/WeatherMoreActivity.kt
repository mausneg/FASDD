package com.example.fasdd_android

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fasdd_android.databinding.ActivityWeatherMoreBinding
import com.example.fasdd_android.databinding.FragmentHomeBinding
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class WeatherMoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWeatherMoreBinding
    private val weatherList = ArrayList<Weather>()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_more)
        binding = ActivityWeatherMoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupOnClickListeners()
        weatherList.addAll(getWeatherList())
        showNewsList()
    }
    private fun setupOnClickListeners() {
        binding.backtoweatherbutton.setOnClickListener {
            val intent = Intent(this, WeatherActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getWeatherList(): ArrayList<Weather> {
        val dataTemperature = resources.getStringArray(R.array.weather_temp)
        val dataLocation = resources.getStringArray(R.array.weather_place)
        val dataTime = resources.getStringArray(R.array.weather_time)
        val dataTipe = resources.getStringArray(R.array.weather_type)

        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val listweather = ArrayList<Weather>()
        for (position in dataLocation.indices) {
            val Weather = Weather(
                dataLocation[position],
                dataTemperature[position],
                LocalTime.parse(dataTime[position], formatter),
                dataTipe[position]
            )
            listweather.add(Weather)
        }
        return listweather
    }

    private fun showNewsList() {
        val weatherListAdapter = WeatherListAdapter(weatherList)
        binding.contentWeather.layoutManager = LinearLayoutManager(this)
        binding.contentWeather.adapter = weatherListAdapter
    }
}
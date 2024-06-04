package com.example.fasdd_android

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale
import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.annotation.RequiresApi
import com.example.fasdd_android.databinding.ActivityWeatherBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

class WeatherActivity : AppCompatActivity() {
    private lateinit var runnable: Runnable
    private lateinit var binding: ActivityWeatherBinding
    private val handler = Handler(Looper.getMainLooper())
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)

        setupOnClickListeners()
        setupWeatherCard()
        setupDateTimeUpdater()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            Log.d("check", "self permission allowed");
            getLocation()
        }
        setContentView(binding.root)
    }
    private fun getCityAndCountry(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        val address = addresses?.get(0)
        val city = address?.locality
        val country = address?.countryName
        val locationTextView = findViewById<TextView>(R.id.kota)
        locationTextView.text = "$city"
        val locationNBegTextView = findViewById<TextView>(R.id.negara)
        locationNBegTextView.text = "$country"
        Log.d("check", "location founded");
        Log.d("check", "$city");
    }
    private fun getLocation() {
        Log.d("check", "on getloc");
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("check", "Permission Denied");
            return
        }
        Log.d("check", "Permission Pass");
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            Log.d("check", "$location?");
            location?.let {
                val latitude = it.latitude
                val longitude = it.longitude
                getCityAndCountry(latitude, longitude)
            }
        }
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
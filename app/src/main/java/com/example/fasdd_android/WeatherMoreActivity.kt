package com.example.fasdd_android

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fasdd_android.databinding.ActivityWeatherMoreBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import kotlin.math.*
import kotlin.collections.ArrayList
data class LatLng(val latitude: Double, val longitude: Double)
const val EARTH_RADIUS = 6371.0
class WeatherMoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWeatherMoreBinding
    private val weatherList = ArrayList<Weather>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var weatherApiService: WeatherApiService
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_more)
        binding = ActivityWeatherMoreBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setupOnClickListeners()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setupRetrofit()

        getWeatherList()

    }
    private fun setupOnClickListeners() {
        binding.backtoweatherbutton.setOnClickListener {
            val intent = Intent(this, WeatherActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
    private fun setupRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.weatherapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        weatherApiService = retrofit.create(WeatherApiService::class.java)
    }
    private fun getLocation() {

    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getWeatherList() {
        val listweather = ArrayList<Weather>()
        var latitude =-8.6338301
        var longitude=116.1477596
        Log.d("check","gate 1")
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        )
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                latitude = it.latitude
                longitude = it.longitude
            }

        }
        Log.d("check","gate 2")
        val startLatLng = LatLng(latitude, longitude)
        val distanceKm = 20.0
        val points = generateLatLngPointsInDifferentDirections(startLatLng, distanceKm)
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("check","gate 3")
            val listweather = ArrayList<Weather>()
            Log.d("check","$points")
            points.forEach { point ->
                Log.d("check","gate 4")
//            println("Lat: ${point.latitude}, Lng: ${point.longitude}")
                try {
                    Log.d("check","gate 6")
                    val response = weatherApiService.getForecast(
                        apiKey = "f48c298d9a4a4833a3c113227241106",
                        location = "${point.latitude},${point.longitude}",
                        days = 1,
                        aqi = "no",
                        alerts = "no"
                    )
                    Log.d("check","gate 5")
                    val geocoder = Geocoder(this@WeatherMoreActivity, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1)
                    val address = addresses?.get(0)
                    val city = address?.locality
                    val formatter = DateTimeFormatter.ofPattern("HH:mm")
                    val hourData = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                    val hourString = String.format("%02d:00", hourData)
                    val localTime = LocalTime.parse(hourString, formatter)
                    Log.d("check","gate 0")

                    val weather = Weather(
                        location = city.toString(),
                        temperature = "${response.current.temp_c}°C",
                        time = localTime.toString(),
                        type = response.current.condition.icon
                    )

                    listweather.add(weather)
                    Log.d("check","$listweather")
                } catch (e: Exception) {
                    Log.e("check", "Error fetching weather data", e)
                }
            }
            Log.d("check","gate 7")
            withContext(Dispatchers.Main) {
                Log.d("check","gate 8")
                Log.d("check","$listweather")
                showNewsList(listweather)
            }
        }
    }

    fun generateLatLngPointsInDifferentDirections(startLatLng: LatLng, distanceKm: Double): List<LatLng> {
        val bearings = listOf(0.0, 90.0, 180.0, 270.0)
        return bearings.map { bearing ->
            calculateDestinationLatLng(startLatLng, distanceKm, bearing)
        }
    }
    fun calculateDestinationLatLng(startLatLng: LatLng, distanceKm: Double, bearingDegrees: Double): LatLng {
        val bearingRad = Math.toRadians(bearingDegrees)
        val startLatRad = Math.toRadians(startLatLng.latitude)
        val startLngRad = Math.toRadians(startLatLng.longitude)

        val newLatRad = asin(sin(startLatRad) * cos(distanceKm / EARTH_RADIUS) +
                cos(startLatRad) * sin(distanceKm / EARTH_RADIUS) * cos(bearingRad))

        val newLngRad = startLngRad + atan2(
            sin(bearingRad) * sin(distanceKm / EARTH_RADIUS) * cos(startLatRad),
            cos(distanceKm / EARTH_RADIUS) - sin(startLatRad) * sin(newLatRad)
        )

        return LatLng(Math.toDegrees(newLatRad), Math.toDegrees(newLngRad))
    }
    private fun showNewsList(weatherList:ArrayList<Weather>) {
        val weatherListAdapter = WeatherListAdapter(weatherList)
        binding.contentWeather.layoutManager = LinearLayoutManager(this)
        binding.contentWeather.adapter = weatherListAdapter
    }
}
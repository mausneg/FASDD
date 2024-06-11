package com.example.fasdd_android

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.fasdd_android.databinding.ActivityWeatherBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.await
import java.util.Locale
import kotlin.random.Random
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

data class Location(
    val name: String,
    val region: String,
    val country: String
)

data class Condition(
    val text: String,
    val icon: String
)

data class Current(
    val temp_c: Double,
    val condition: Condition,
    val uv: Double,
    val wind_kph: Double,
    val humidity: Int
)

data class Day(
    val maxtemp_c: Double,
    val mintemp_c: Double,
    val avgtemp_c: Double,
    val condition: Condition,
    val uv: Double
)
data class Hour(
    val time: String,
    val temp_c: Double,
    val condition: Condition
)

data class ForecastDay(
    val date: String,
    val day: Day,
    val hour: List<Hour>
)

data class Forecast(
    val forecastday: List<ForecastDay>
)

data class ForecastResponse(
    val location: Location,
    val current: Current,
    val forecast: Forecast
)
interface WeatherApiService {
    @GET("forecast.json")
    suspend fun getForecast(
        @Query("key") apiKey: String,
        @Query("q") location: String,
        @Query("days") days: Int,
        @Query("aqi") aqi: String,
        @Query("alerts") alerts: String
    ): ForecastResponse
}

class WeatherActivity : AppCompatActivity() {
    private lateinit var runnable: Runnable
    private lateinit var binding: ActivityWeatherBinding
    private val handler = Handler(Looper.getMainLooper())
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var weatherApiService: WeatherApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupOnClickListeners()
        setupWeatherCard()
        setupDateTimeUpdater()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setupRetrofit()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            Log.d("check", "self permission allowed")
            getLocation()
        }
    }

    private fun setupRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.weatherapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        weatherApiService = retrofit.create(WeatherApiService::class.java)
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
        Log.d("check", "location founded")
        Log.d("check", "$city")

        fetchWeatherData("$latitude,$longitude")
    }

    private fun fetchWeatherData(location: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = weatherApiService.getForecast(
                    apiKey = "f48c298d9a4a4833a3c113227241106",
                    location = location,
                    days = 1,
                    aqi = "no",
                    alerts = "no"
                )

                withContext(Dispatchers.Main) {
                    updateWeatherUI(response)
                }
            } catch (e: Exception) {
                Log.e("WeatherActivity", "Error fetching weather data", e)
            }
        }
    }

    private fun updateWeatherUI(forecast: ForecastResponse) {
        binding.temp.text = "${forecast.current.temp_c}°C"
//        binding.condition.text = forecast.current.condition.text
        binding.ivWeather.setImageResource(
            when (forecast.current.condition.text.toLowerCase()) {
                "sunny" -> R.drawable.ic_card_weather_sunny
                "cloudy" -> R.drawable.ic_card_weather_cloudy
                "rainy" -> R.drawable.ic_card_weather_rainy
                else -> R.drawable.ic_card_weather_sunny
            }
        )
        findViewById<TextView>(R.id.uvIndex).text = "${forecast.current.uv}"
        findViewById<TextView>(R.id.windSpeed).text = "${forecast.current.wind_kph} kph"
        findViewById<TextView>(R.id.humidity).text = "${forecast.current.humidity}%"

        val hourlyForecast = forecast.forecast.forecastday[0].hour.take(5)
        updateHourlyUI(hourlyForecast)
    }
    private fun updateHourlyUI(hourlyForecast: List<Hour>) {
        Log.d("check", "$hourlyForecast")
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        var startIndex = 0
        for (i in hourlyForecast.indices) {
            val hourData = hourlyForecast[i]
            val hour = hourData.time.split(" ")[1].substring(0, 2).toInt()

            if (hour >= currentHour) {
                startIndex = i
                break
            }
        }
        Log.d("check", "$startIndex")
        Log.d("check", hourlyForecast.size.toString())
        for (i in startIndex until hourlyForecast.size) {
            val hourData = hourlyForecast[i]

            val timeView = findViewById<TextView>(resources.getIdentifier("hour${i + 1}Time", "id", packageName))
            val tempView = findViewById<TextView>(resources.getIdentifier("hour${i + 1}Temp", "id", packageName))
            val conditionView = findViewById<TextView>(resources.getIdentifier("hour${i + 1}Condition", "id", packageName))

            val hour = hourData.time.split(" ")[1].substring(0, 5)
            Log.d("check", hour)
            Log.d("check", "hour${i + 1}Time")
            timeView.text = hour
            tempView.text = "${hourData.temp_c}°C"
//            conditionView.text = hourData.condition.text
            Log.d("check", "is made to last")
        }
    }
    private fun getLocation() {
        Log.d("check", "on getloc")
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("check", "Permission Denied")
            return
        }
        Log.d("check", "Permission Pass")
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            Log.d("check", "$location?")
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

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
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.collections.ArrayList

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
        val latitude = -8.6371
        val longitude = 116.1528

        val context: Context = this
        val provinceId = getProvinceId(context, latitude, longitude)
        Log.d("check", provinceId.toString())
        weatherList.addAll(getWeatherList())
        showNewsList()
    }
    fun getProvinceId(context: Context, latitude: Double, longitude: Double): String? {
        val geocoder = Geocoder(context)
        var provinceId: String? = null

        try {
            val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)!!
            Log.d("check", addresses.toString())
            if (addresses.isNotEmpty()) {
                val address = addresses[0]
                val province = address.adminArea
                provinceId = getProvinceIdFromName(province)
                Log.d("check", provinceId.toString())
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return provinceId
    }
    fun getProvinceIdFromName(provinceName: String): String? {
        return when (provinceName.toLowerCase()) {
            "aceh" -> "aceh"
            "sumatera utara" -> "sumatera-utara"
            "sumatera barat" -> "sumatera-barat"
            "riau" -> "riau"
            "jambi" -> "jambi"
            "sumatera selatan" -> "sumatera-selatan"
            "bengkulu" -> "bengkulu"
            "lampung" -> "lampung"
            "bangka belitung" -> "bangka-belitung"
            "kepulauan riau" -> "kepulauan-riau"
            "daerah khusus ibukota jakarta" -> "dki-jakarta"
            "jawa barat" -> "jawa-barat"
            "jawa tengah" -> "jawa-tengah"
            "di yogyakarta" -> "di-yogyakarta"
            "jawa timur" -> "jawa-timur"
            "banten" -> "banten"
            "bali" -> "bali"
            "west nusa tenggara" -> "nusa-tenggara-barat"
            "nusa tenggara timur" -> "nusa-tenggara-timur"
            "kalimantan barat" -> "kalimantan-barat"
            "kalimantan tengah" -> "kalimantan-tengah"
            "kalimantan selatan" -> "kalimantan-selatan"
            "kalimantan timur" -> "kalimantan-timur"
            "kalimantan utara" -> "kalimantan-utara"
            "sulawesi utara" -> "sulawesi-utara"
            "sulawesi tengah" -> "sulawesi-tengah"
            "sulawesi selatan" -> "sulawesi-selatan"
            "sulawesi tenggara" -> "sulawesi-tenggara"
            "gorontalo" -> "gorontalo"
            "sulawesi barat" -> "sulawesi-barat"
            "maluku" -> "maluku"
            "maluku utara" -> "maluku-utara"
            "papua barat" -> "papua-barat"
            "papua" -> "papua"
            else -> null
        }
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
        Log.d("check", "Gate 0 ")
        val latitude = -8.6371
        val longitude = 116.1528

        val context: Context = this
        val provinceId = getProvinceId(context, latitude, longitude)

        val districtURL = URL("https://bmkg-cuaca-api.vercel.app/districts?provinceId=$provinceId")

        try {
            val districtConnection = districtURL.openConnection() as HttpURLConnection
            districtConnection.requestMethod = "GET"

            if (districtConnection.responseCode == HttpURLConnection.HTTP_OK) {
                val districtResponse = districtConnection.inputStream.bufferedReader().use(BufferedReader::readText)
                // Process the district response here
                println(districtResponse)
            } else {
                // Handle non-OK response
                println("Failed to fetch district data. Response code: ${districtConnection.responseCode}")
            }
        } catch (e: Exception) {
            // Handle exceptions
            println("Error fetching district data: ${e.message}")
        }
        val districtConnection = districtURL.openConnection() as HttpURLConnection

        val districtResponse = districtConnection.inputStream.bufferedReader().use(BufferedReader::readText)
        Log.d("check", "Gate 0.4 ")
        val districtJsonArray = JSONArray(districtResponse)
        Log.d("check", "Gate 1 ")
        val listweather = ArrayList<Weather>()
        for (i in 0 until districtJsonArray.length()) {
            val district = districtJsonArray.getJSONObject(i)
            val districtId = district.getString("id")
            val districtName = district.getString("name")

            val weatherURL = URL("https://bmkg-cuaca-api.vercel.app/cuaca?provinceId=$provinceId&districtId=$districtId")
            val weatherConnection = weatherURL.openConnection() as HttpURLConnection
            val weatherResponse = weatherConnection.inputStream.bufferedReader().use(BufferedReader::readText)
            val weatherJsonObject = JSONObject(weatherResponse)
            var temperature = ""
            val temperatureData = weatherJsonObject.getJSONObject("data").getJSONObject("temperature").getJSONArray("data")
            for (i in 0 until temperatureData.length()) {
                val tempObject = temperatureData.getJSONObject(i)
                val hour = tempObject.getString("hour")
                if (hour == "12:00") {
                    temperature = tempObject.getString("celcius")
                    break
                }
            }
            val currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))

            val weather = Weather(districtName, temperature, currentTime, "")
            listweather.add(weather)
        }
//        val dataTemperature = resources.getStringArray(R.array.weather_temp)
//        val dataLocation = resources.getStringArray(R.array.weather_place)
//        val dataTime = resources.getStringArray(R.array.weather_time)
//        val dataTipe = resources.getStringArray(R.array.weather_type)
//
//        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
//        val listweather = ArrayList<Weather>()
//        for (position in dataLocation.indices) {
//            val Weather = Weather(
//                dataLocation[position],
//                dataTemperature[position],
//                LocalTime.parse(dataTime[position], formatter),
//                dataTipe[position]
//            )
//            listweather.add(Weather)
//        }
        Log.d("check", listweather.toString())
        return listweather
    }

    private fun showNewsList() {
        val weatherListAdapter = WeatherListAdapter(weatherList)
        binding.contentWeather.layoutManager = LinearLayoutManager(this)
        binding.contentWeather.adapter = weatherListAdapter
    }
}
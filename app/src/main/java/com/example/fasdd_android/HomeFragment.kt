package com.example.fasdd_android

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.example.fasdd_android.databinding.FragmentHomeBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.fasdd_android.newsdata.response.ResponseNews
import com.example.fasdd_android.newsdata.retrofit.ApiConfig
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.Call
import retrofit2.Response
import java.net.URL

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    private val newsList = ArrayList<News>()
    private lateinit var profilePict : URL
    private lateinit var sharedPreferences: SharedPreferences
    private var userId: String? = null
    val storage = FirebaseStorage.getInstance()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        sharedPreferences = requireActivity().getSharedPreferences("user_id", Context.MODE_PRIVATE)
        val imageUri = sharedPreferences.getString("profile_url", null)
        Log.d(TAG, "onCreateView: $imageUri")


        if(imageUri != null) {
            Glide.with(this)
                .load(imageUri)
                .into(binding.profilePicture)
        } else {
            binding.profilePicture.setImageResource(R.drawable.profile)
        }
        getNewsListNew()
        setupOnClickListeners()
        setupWeatherCard()
        setupDateTimeUpdater()
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }

    private fun setupOnClickListeners() {
        binding.cardWeather.setOnClickListener {
            val intent = Intent(context, WeatherActivity::class.java)
            startActivity(intent)
        }

        binding.moreNews.setOnClickListener {
            val intent = Intent(context, NewsActivity::class.java)
            startActivity(intent)
        }

        binding.profilePicture.setOnClickListener {
            val intent = Intent(context, ProfileActivity::class.java)
            startActivity(intent)
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

        binding.cardWeather.setBackgroundResource(
            when (weather) {
                "Sunny" -> R.drawable.bg_card_weather_sunny
                else -> R.drawable.bg_card_weather_sunny
            }
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupDateTimeUpdater() {
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
    }

    private fun getNewsListNew() {
        val q = "petani"
        val apikey = "65d438ffae89424393321f74b0be3786"
        val client = ApiConfig.getApiService().getEverything(q,apikey)
        client.enqueue(object:retrofit2.Callback<ResponseNews>{
            override fun onResponse(p0: Call<ResponseNews>, p1: Response<ResponseNews>) {
                if(p1.isSuccessful){
                    val result  = p1.body()!!.articles
                    val sort = result.sortedByDescending { it.publishedAt }
                    val adapter = NewsListAdapterr()
                    adapter.submitList(sort)
                    binding.contentNews.layoutManager = LinearLayoutManager(context)
                    binding.contentNews.adapter = adapter
                    binding.contentNews.isNestedScrollingEnabled = false

                }else{
                    Log.e("ERROR","OnFailure: ${p1.message()}")
                }
            }
            override fun onFailure(pcall: Call<ResponseNews>, p1: Throwable) {
                Log.e("ERROR","OnFailure: ${p1.message.toString()}")
            }
        })
    }
}
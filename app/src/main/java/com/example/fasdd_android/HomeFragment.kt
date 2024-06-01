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
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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
        try {
            Glide.with(this)
                .load(imageUri)
                .into(binding.profilePicture)
        } catch (e: Exception) {
            binding.profilePicture.setImageResource(R.drawable.profile)
        }

        newsList.addAll(getNewsList())
        showNewsList()
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getNewsList(): ArrayList<News> {
        val dataTitle = resources.getStringArray(R.array.news_titles)
        val dataExcerpt = resources.getStringArray(R.array.news_excerpt)
        val dataImage = resources.getStringArray(R.array.news_images)
        val dataDateTime = resources.getStringArray(R.array.news_datetimes)
        val dataContent = resources.getStringArray(R.array.news_contents)

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val listNews = ArrayList<News>()
        for (position in dataTitle.indices) {
            val news = News(
                dataTitle[position],
                dataContent[position],
                dataImage[position],
                LocalDateTime.parse(dataDateTime[position], formatter),
                dataExcerpt[position],
            )
            listNews.add(news)
        }
        return listNews
    }

    private fun showNewsList() {
        val newsListAdapter = NewsListAdapter(newsList)
        binding.contentNews.layoutManager = LinearLayoutManager(context)
        binding.contentNews.adapter = newsListAdapter
        binding.contentNews.isNestedScrollingEnabled = false
    }
}
package com.example.fasdd_android

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fasdd_android.databinding.ActivityNewsBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NewsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsBinding
    private val newsList = ArrayList<News>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        newsList.addAll(getNewsList())
        showNewsList()
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
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = newsListAdapter
    }
}
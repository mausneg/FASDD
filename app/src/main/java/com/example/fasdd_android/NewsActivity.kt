package com.example.fasdd_android

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fasdd_android.databinding.ActivityNewsBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NewsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsBinding
    private val newsList = ArrayList<News>()
    private lateinit var newsListAdapter: NewsListAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.findViewById<TextView>(R.id.back_button).setOnClickListener {
            finish()
        }

        newsList.addAll(getNewsList())
        showNewsList()

        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    if (newText.isEmpty()) {
                        newsListAdapter.updateNewsList(newsList)
                    } else {
                        filterNewsList(newText)
                    }
                }
                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
//            R.id.action_filter -> {
//                // Handle filter action here
//                return true
//            }
        }
        return super.onOptionsItemSelected(item)
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
        newsListAdapter = NewsListAdapter(newsList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = newsListAdapter
    }

    private fun filterNewsList(query: String) {
        val filteredNewsList = newsList.filter { news ->
            news.title.contains(query, ignoreCase = true)
        }
        newsListAdapter.updateNewsList(filteredNewsList)
    }
}
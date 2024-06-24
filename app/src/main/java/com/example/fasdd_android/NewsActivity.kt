package com.example.fasdd_android

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fasdd_android.databinding.ActivityNewsBinding
import com.example.fasdd_android.newsdata.response.ArticlesItem
import com.example.fasdd_android.newsdata.response.ResponseNews
import com.example.fasdd_android.newsdata.retrofit.ApiConfig
import com.google.android.material.tabs.TabLayout.TabGravity
import retrofit2.Call
import retrofit2.Response
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

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        getNewsListNew()

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

    private fun getNewsListNew(){
        val q = "petani "
        val apikey = "65d438ffae89424393321f74b0be3786"
        val client = ApiConfig.getApiService().getEverything(q,apikey)
        client.enqueue(object:retrofit2.Callback<ResponseNews>{
            override fun onResponse(p0: Call<ResponseNews>, p1: Response<ResponseNews>) {
                if(p1.isSuccessful){
                    val result  = p1.body()!!.articles
                    val sort = result.sortedByDescending { it.publishedAt }
                    val adapter = NewsListAdapterr()
                    adapter.submitList(sort)
                    binding.recyclerView.adapter = adapter
                }else{
                    Log.e("ERROR","OnFailure: ${p1.message()}")

                }
            }
            override fun onFailure(pcall: Call<ResponseNews>, p1: Throwable) {
                Log.e("ERROR","OnFailure: ${p1.message.toString()}")
            }
        })
    }

    private fun filterNewsList(query: String) {
        val filteredNewsList = newsList.filter { news ->
            news.title.contains(query, ignoreCase = true)
        }
        newsListAdapter.updateNewsList(filteredNewsList)
    }
}
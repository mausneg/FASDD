package com.example.fasdd_android

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class DetailNewsActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_news)

        val news = intent.getParcelableExtra<News>("News")

        val titleTextView = findViewById<TextView>(R.id.news_title)
        val contentTextView = findViewById<TextView>(R.id.news_excerpt)
        val imageView = findViewById<ImageView>(R.id.news_image)

        titleTextView.text = news?.title
        contentTextView.text = news?.content

        Glide.with(this)
            .load(news?.image)
            .into(imageView)
    }
}
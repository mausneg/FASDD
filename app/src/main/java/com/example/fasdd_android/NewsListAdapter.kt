package com.example.fasdd_android

import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.time.Duration
import java.time.LocalDateTime
import java.time.Period

class NewsListAdapter(private val newsList: ArrayList<News>): RecyclerView.Adapter<NewsListAdapter.ViewHolder>() {
    class ViewHolder(itemView: View,private val newsList: ArrayList<News>) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.news_title)
        val image: ImageView = itemView.findViewById(R.id.news_image)
        val dateTime: TextView = itemView.findViewById(R.id.news_datetime)
        val excerpt: TextView = itemView.findViewById(R.id.news_excerpt)

        init {
            itemView.setOnClickListener {
                val intent = Intent(it.context, NewsDetailActivity::class.java).apply {
                    putExtra("News", newsList[adapterPosition])
                }
                it.context.startActivity(intent)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_home_news, parent, false)
        return ViewHolder(view, newsList)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getRelativeTime(time: LocalDateTime): String {
        val now = LocalDateTime.now()
        val duration = Duration.between(time, now)
        val period = Period.between(time.toLocalDate(), now.toLocalDate())

        return when {
            period.years > 0 -> "${period.years} years ago"
            period.months > 0 -> "${period.months} months ago"
            period.days > 0 -> "${period.days} days ago"
            duration.toHours() > 0 -> "${duration.toHours()} hours ago"
            duration.toMinutes() > 0 -> "${duration.toMinutes()} minutes ago"
            else -> "Just now"
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val news = newsList[position]
        holder.title.text = news.title
        holder.dateTime.text = getRelativeTime(news.dateTime)
        holder.excerpt.text = news.excerpt
        Glide.with(holder.itemView.context)
            .load(news.image)
            .apply(RequestOptions().centerCrop())
            .into(holder.image)
    }
    override fun getItemCount(): Int {
        return newsList.size
    }
}
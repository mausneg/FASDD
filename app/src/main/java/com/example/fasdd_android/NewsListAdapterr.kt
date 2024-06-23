package com.example.fasdd_android

import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.fasdd_android.databinding.CardHomeNewsBinding
import com.example.fasdd_android.newsdata.response.ArticlesItem
import java.time.Duration
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter

class NewsListAdapterr : ListAdapter<ArticlesItem, NewsListAdapterr.MyViewHolder>(DIFF_CALLBACK) {
    class MyViewHolder(val binding: CardHomeNewsBinding ):RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun getRelativeTime(time: String): String {
            val formatter = DateTimeFormatter.ISO_DATE_TIME
            val parsedTime = LocalDateTime.parse(time, formatter)
            val now = LocalDateTime.now()
            val duration = Duration.between(parsedTime, now)
            val period = Period.between(parsedTime.toLocalDate(), now.toLocalDate())

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
        fun bind(news: ArticlesItem) {
            binding.newsTitle.text = news.title
            binding.newsDatetime.text = news.publishedAt?.let { getRelativeTime(it) }
            binding.newsExcerpt.text = news.content.toString()
            Glide.with(binding.newsImage)
                .load(news.urlToImage)
                .apply(RequestOptions().centerCrop())
                .into(binding.newsImage)

            binding.pencetini.setOnClickListener {
                val intent = Intent(it.context, NewsDetailActivity::class.java).apply {
                    putExtra(NewsDetailActivity.EXTRA_TITLE, news.title)
                    putExtra(NewsDetailActivity.EXTRA_CONTENT, news.content.toString())
                    putExtra(NewsDetailActivity.EXTRA_IMAGE, news.urlToImage.toString())
                    putExtra(NewsDetailActivity.EXTRA_URL, news.url.toString())
                }
                it.context.startActivity(intent)
            }
        }

    }

    companion object{
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ArticlesItem>(){
            override fun areItemsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = CardHomeNewsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val news = getItem(position)
        holder.bind(news)
    }

}
package com.example.fasdd_android

import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.fasdd_android.databinding.ActivityWeatherBinding
import java.time.Duration
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter

class WeatherListAdapter(private val weatherList: ArrayList<Weather>): RecyclerView.Adapter<WeatherListAdapter.ViewHolder>() {
    private lateinit var runnable: Runnable
    private lateinit var binding: ActivityWeatherBinding
    private val handler = Handler(Looper.getMainLooper())
    class ViewHolder(itemView: View,private val weatherList: ArrayList<Weather>) : RecyclerView.ViewHolder(itemView) {
        val temperature: TextView = itemView.findViewById(R.id.weather_temp)
        val location: TextView = itemView.findViewById(R.id.weather_place)
        val time: TextView = itemView.findViewById(R.id.weather_time)
        val type: ImageView = itemView.findViewById(R.id.weather_type)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_weather_more, parent, false)
        return ViewHolder(view, weatherList)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val weather = weatherList[position]
        holder.temperature.text = weather.temperature
        holder.time.text = weather.time.toString()
        holder.location.text = weather.location
        val iconUrl = "https:" + weather.type
        Glide.with(holder.itemView.context)
            .load(iconUrl)
            .into(holder.type)

    }
    override fun getItemCount(): Int {
        return weatherList.size
    }
}
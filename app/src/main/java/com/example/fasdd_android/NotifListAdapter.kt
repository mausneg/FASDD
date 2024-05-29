package com.example.fasdd_android

import android.annotation.SuppressLint
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

class NotifListAdapter(private val notifList: ArrayList<Notif>): RecyclerView.Adapter<NotifListAdapter.ViewHolder>() {
    class ViewHolder(itemView: View,private val notifList: ArrayList<Notif>) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.notif_title)
        val dateTime: TextView = itemView.findViewById(R.id.notif_datetime)
        val excerpt: TextView = itemView.findViewById(R.id.notif_excerpt)

        init {
            itemView.setOnClickListener {
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_notif, parent, false)
        return ViewHolder(view, notifList)
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
        val notif = notifList[position]
        holder.title.text = notif.title
        holder.dateTime.text = getRelativeTime(notif.dateTime)
        holder.excerpt.text = notif.excerpt
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateNotifList(newNotifList: List<Notif>) {
        notifList.clear()
        notifList.addAll(newNotifList)
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
        return notifList.size
    }
}
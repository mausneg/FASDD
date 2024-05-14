package com.example.fasdd_android

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HistoryAdapter(private val historyList: List<HistoryItem>) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.historyImageView)
        val titleTextView: TextView = view.findViewById(R.id.historyTitleTextView)
        val timeTextView: TextView = view.findViewById(R.id.historyTimeTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hist, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val historyItem = historyList[position]
        holder.imageView.setImageBitmap(historyItem.image)
        holder.titleTextView.text = historyItem.title
        holder.timeTextView.text = historyItem.time
    }

    override fun getItemCount(): Int {
        return historyList.size
    }
}


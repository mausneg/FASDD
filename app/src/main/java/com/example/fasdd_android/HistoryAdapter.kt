package com.example.fasdd_android

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage

class HistoryAdapter(
    private val historyList: List<HistoryItem>,
    private val onItemClick: (String, String, String) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hist, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val historyItem = historyList[position]
        holder.bind(historyItem)
        holder.itemView.setOnClickListener {
            onItemClick(historyItem.imageUrl, historyItem.plantName, historyItem.predictedClass)
        }
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val historyImageView: ImageView = itemView.findViewById(R.id.historyImageView)
        private val titleTextView: TextView = itemView.findViewById(R.id.historyTitleTextView)
        private val dateTimeTextView: TextView = itemView.findViewById(R.id.dateTimeTextView)

        fun bind(historyItem: HistoryItem) {
            titleTextView.text = "${historyItem.plantName}: ${historyItem.predictedClass}"
            dateTimeTextView.text = historyItem.dateTime

            // Load image from Firebase Storage
            val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(historyItem.imageUrl)
            storageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                historyImageView.setImageBitmap(bitmap)
            }.addOnFailureListener { e ->
                // Handle error
            }
        }
    }
}

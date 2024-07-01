package com.example.fasdd_android

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import java.time.Duration
import java.time.LocalDateTime
import java.time.Period
import java.util.Locale

class NotifListAdapter(private val notifList: ArrayList<Notif>): RecyclerView.Adapter<NotifListAdapter.ViewHolder>() {
    class ViewHolder(itemView: View,private val notifList: ArrayList<Notif>) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.notif_title)
        val dateTime: TextView = itemView.findViewById(R.id.notif_datetime)
        val message: TextView = itemView.findViewById(R.id.notif_message)
        var icon: ImageView = itemView.findViewById(R.id.notif_icon)
        val space: TextView = itemView.findViewById(R.id.notif_space)


        init {
            itemView.setOnClickListener {
                val notif = notifList[adapterPosition]
                if (!notif.already_read) {
                    notif.already_read = true
                    val db = FirebaseFirestore.getInstance()
                    db.collection("notifications").document(notif.id)
                        .update("already_read", true)
                        .addOnSuccessListener {
                            Log.d(TAG, "DocumentSnapshot successfully updated!")
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error updating document", e)
                        }
                }
                val intent = Intent(itemView.context, ImageResultActivity::class.java)
                val db = FirebaseFirestore.getInstance()

                db.collection("notifications")
                    .document(notif.id)
                    .get()
                    .addOnSuccessListener { document ->
                        val historyRef = document.getDocumentReference("history_id")
                        if (historyRef != null) {
                            db.document(historyRef.path).get()
                                .addOnSuccessListener { historyDocument ->
                                    val plantName = historyDocument.getString("plant_name") ?: ""
                                    val predictedClass = historyDocument.getString("predicted_class") ?: ""
                                    val imageUrl = historyDocument.getString("image_url") ?: ""
                                    val solution = historyDocument.getString("solution") ?: ""

                                    intent.putExtra("image", imageUrl)
                                    intent.putExtra("predictionClass", predictedClass)
                                    intent.putExtra("plantName", plantName)
                                    intent.putExtra("solution", solution)
                                    itemView.context.startActivity(intent)
                                }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("HistoryActivity", "Error getting documents: $exception")
                    }
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
        holder.icon.setImageResource(
            when (notif.type) {
                "scan" -> R.drawable.ic_scan
                else -> R.drawable.ic_scan
            }
        )
        holder.title.text = when (notif.type) {
            "scan" -> "Scan Result"
            else -> "Scan Result"
        }
        holder.message.text = when (notif.type) {
            "scan" -> "Hasil scanmu sudah keluar! Yuk lihat bagaimana kondisi kesehatan tanamanmu."
            else -> "Hasil scanmu sudah keluar! Yuk lihat bagaimana kondisi kesehatan tanamanmu."
        }
        holder.dateTime.text = getRelativeTime(notif.dateTime)
        holder.space.text = "•"

        if (notif.already_read) {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT)
        } else {
            holder.itemView.setBackgroundColor(Color.LTGRAY)
        }
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
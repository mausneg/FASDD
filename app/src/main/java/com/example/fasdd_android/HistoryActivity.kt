package com.example.fasdd_android

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val imageData: Bitmap? = intent.getParcelableExtra("image")
        val title: String? = intent.getStringExtra("plantName")
        val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val historyList = mutableListOf<HistoryItem>()
        if (imageData != null && title != null) {
            historyList.add(HistoryItem(imageData, title, currentTime))
        }

        val adapter = HistoryAdapter(historyList)
        recyclerView.adapter = adapter

        val emptyHistoryTextView: TextView = findViewById(R.id.emptyHistoryTextView)
        if (historyList.isEmpty()) {
            emptyHistoryTextView.visibility = TextView.VISIBLE
            recyclerView.visibility = RecyclerView.GONE
        } else {
            emptyHistoryTextView.visibility = TextView.GONE
            recyclerView.visibility = RecyclerView.VISIBLE
        }

        val backToCameraButton: Button = findViewById(R.id.backToCameraButton)
        backToCameraButton.setOnClickListener {
            navigateToScanFragment()
        }
    }

    private fun navigateToScanFragment() {
        onBackPressed()
    }
}
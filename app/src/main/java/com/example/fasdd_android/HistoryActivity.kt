package com.example.fasdd_android

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class HistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyHistoryTextView: TextView
    private lateinit var backToCameraButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        emptyHistoryTextView = findViewById(R.id.emptyHistoryTextView)
        backToCameraButton = findViewById(R.id.backToCameraButton)

        // Back button to camera
        backToCameraButton.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onStart() {
        super.onStart()
        fetchHistoryFromFirestore()
    }

    private fun fetchHistoryFromFirestore() {
        val db = FirebaseFirestore.getInstance()

        db.collection("histories")
            .get()
            .addOnSuccessListener { result ->
                val historyList = mutableListOf<HistoryItem>()

                for (document in result) {
                    val plantName = document.getString("plant_name") ?: ""
                    val predictedClass = document.getString("predicted_class") ?: ""
                    val dateTime = document.getDate("datetime")
                    val formattedDateTime = dateTime?.let {
                        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(it)
                    } ?: "Unknown"
                    val imageUrl = document.getString("image_url") ?: ""

                    val historyItem = HistoryItem(imageUrl, plantName, predictedClass, formattedDateTime)
                    historyList.add(historyItem)
                }

                updateRecyclerView(historyList)
            }
            .addOnFailureListener { exception ->
                Log.e("HistoryActivity", "Error getting documents: $exception")
            }
    }

    private fun updateRecyclerView(historyList: List<HistoryItem>) {
        val adapter = HistoryAdapter(historyList) { imageUrl, plantName, predictedClass ->
            navigateToImageResultActivity(imageUrl, plantName, predictedClass)
        }
        recyclerView.adapter = adapter

        // Show or hide empty view based on historyList
        if (historyList.isEmpty()) {
            emptyHistoryTextView.visibility = TextView.VISIBLE
            recyclerView.visibility = RecyclerView.GONE
        } else {
            emptyHistoryTextView.visibility = TextView.GONE
            recyclerView.visibility = RecyclerView.VISIBLE
        }
    }

    private fun navigateToImageResultActivity(imageUrl: String, plantName: String, predictedClass: String) {
        val intent = Intent(this, ImageResultActivity::class.java).apply {
            putExtra("image", imageUrl)
            putExtra("predictionClass", predictedClass)
            putExtra("plantName", plantName)
        }
        startActivity(intent)
    }
}

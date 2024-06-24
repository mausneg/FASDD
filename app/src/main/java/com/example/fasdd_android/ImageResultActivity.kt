package com.example.fasdd_android

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.graphics.BitmapFactory
import android.content.Intent
import android.util.Log


class ImageResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_result)

        val imageData = intent.getByteArrayExtra("image")
        val predictionClass = intent.getStringExtra("predictionClass") // Retrieve prediction class from intent
        val plantName = intent.getStringExtra("plantName")

        val imageView: ImageView = findViewById(R.id.imageView)
        if (imageData != null) {
            val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
            imageView.setImageBitmap(bitmap)
        } else {
            imageView.setImageResource(R.drawable.placeholder)
        }

        val titleTextView: TextView = findViewById(R.id.titleTextView)
        Log.d("ImageResultActivity", "Prediction class received: $predictionClass")
        titleTextView.text = predictionClass ?: "Prediction class not available"

        val backToCameraButton: Button = findViewById(R.id.backToCameraButton)
        backToCameraButton.setOnClickListener {
            onBackPressed()
        }

        val historyButton: Button = findViewById(R.id.historyButton)
        historyButton.setOnClickListener {
            navigateToHistoryActivity(imageData, plantName)
        }
    }

    private fun navigateToHistoryActivity(imageData: ByteArray?, plantName: String?) {
        val intent = Intent(this, HistoryActivity::class.java).apply {
            putExtra("image", imageData)
            putExtra("plantName", plantName)
        }
        startActivity(intent)
    }
}

package com.example.fasdd_android

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent


class ImageResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_result)

        val imageData = intent.getParcelableExtra<Bitmap>("image")
        val plantName = intent.getStringExtra("plantName")

        val imageView: ImageView = findViewById(R.id.imageView)
        imageView.setImageBitmap(imageData)

        val backToCameraButton: Button = findViewById(R.id.backToCameraButton)
        backToCameraButton.setOnClickListener {
            navigateToScanFragment()
        }

        val historyButton: Button = findViewById(R.id.historyButton)
        historyButton.setOnClickListener {
            navigateToHistoryActivity(imageData, plantName)
        }

        val titleTextView: TextView = findViewById(R.id.titleTextView)
        titleTextView.text = plantName ?: "FASDD"

    }

    private fun navigateToScanFragment() {
        onBackPressed()
    }

    private fun navigateToHistoryActivity(imageData: Bitmap?, plantName: String?) {
        val intent = Intent(this, HistoryActivity::class.java).apply {
            putExtra("image", imageData)
            putExtra("plantName", plantName)
        }
        startActivity(intent)
    }

}

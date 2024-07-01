package com.example.fasdd_android

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ImageResultActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var titleTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var backToCameraButton: Button
    private lateinit var historyButton: Button
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_result)

        // Initialize Firebase components
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Initialize views
        imageView = findViewById(R.id.imageView)
        titleTextView = findViewById(R.id.titleTextView)
        descriptionTextView = findViewById(R.id.descriptionTextView)
        backToCameraButton = findViewById(R.id.backToCameraButton)
        historyButton = findViewById(R.id.historyButton)

        // Retrieve image URL from intent extras
        val imageUrl = intent.getStringExtra("image")
        if (imageUrl != null) {
            fetchImageFromStorage(imageUrl)
        } else {
            Log.e("ImageResultActivity", "Image URL is null")
        }

        // Retrieve prediction class and plant name (optional)
        val predictionClass = intent.getStringExtra("predictionClass")
        titleTextView.text = predictionClass ?: "Prediction class not available"

        val solution = intent.getStringExtra("solution")
        descriptionTextView.text = solution ?: "Solution not available"

        // Back to camera button click listener
        backToCameraButton.setOnClickListener {
            onBackPressed()
        }

        // History button click listener
        historyButton.setOnClickListener {
            navigateToHistoryActivity()
        }
    }

    private fun fetchImageFromStorage(imageUrl: String) {
        val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
        storageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            imageView.setImageBitmap(bitmap)
        }.addOnFailureListener { e ->
            Log.e("ImageResultActivity", "Error downloading image: $e")
        }
    }

    private fun navigateToHistoryActivity() {
        val intent = Intent(this, HistoryActivity::class.java)
        startActivity(intent)
        finish() // Optional: close this activity when navigating to HistoryActivity
    }
}

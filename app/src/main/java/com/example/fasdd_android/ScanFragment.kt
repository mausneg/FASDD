package com.example.fasdd_android

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import org.tensorflow.lite.DataType
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.*

class ScanFragment : Fragment() {

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 1
        private const val IMAGE_SIZE = 150 // Change this to the size expected by your model
        private const val MODEL_INPUT_SIZE = IMAGE_SIZE * IMAGE_SIZE * 3 // RGB image
        private const val TAG = "ScanFragment"
    }

    private lateinit var selectedPlant: String

    private val takePicture = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageData = result.data?.extras?.get("data") as Bitmap
            uploadImageToStorage(imageData)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val plantSpinner: Spinner = view.findViewById(R.id.plantSpinner)
        val startCameraButton: Button = view.findViewById(R.id.startCameraButton)
        val historyButton: Button = view.findViewById(R.id.historyButton)

        val plants = arrayOf("Padi", "Jagung", "Strawberry")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, plants)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        plantSpinner.adapter = adapter

        plantSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedPlant = plants[position]
                downloadModel(getModelName(selectedPlant))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        startCameraButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                requestCameraPermission()
            }
        }

        historyButton.setOnClickListener {
            val intent = Intent(requireContext(), HistoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun downloadModel(modelName: String) {
        val conditions = CustomModelDownloadConditions.Builder()
            .requireWifi()
            .build()
        FirebaseModelDownloader.getInstance()
            .getModel(modelName, DownloadType.LOCAL_MODEL, conditions)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Model downloaded: $modelName", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Model download failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun getModelName(plantName: String): String {
        return when (plantName) {
            "Jagung" -> "Corn_Model"
            "Padi" -> "Rice_Model"
            "Strawberry" -> "Strawberry_Model"
            else -> "Unknown_Model"
        }
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.CAMERA),
            REQUEST_CAMERA_PERMISSION
        )
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePicture.launch(cameraIntent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Permission denied, can't open camera",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun uploadImageToStorage(image: Bitmap) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val imagesRef = storageRef.child("history/${UUID.randomUUID()}.png")

        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = imagesRef.putBytes(data)
        uploadTask.addOnSuccessListener {
            imagesRef.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()
                predictImage(image, imageUrl)
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun predictImage(image: Bitmap, imageUrl: String) {
        val modelName = getModelName(selectedPlant)
        FirebaseModelDownloader.getInstance()
            .getModel(modelName, DownloadType.LOCAL_MODEL, CustomModelDownloadConditions.Builder().requireWifi().build())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val modelFile = task.result?.file
                    if (modelFile != null) {
                        try {
                            val interpreter = Interpreter(modelFile)
                            val resizedImage = Bitmap.createScaledBitmap(image, IMAGE_SIZE, IMAGE_SIZE, true)
                            val byteBuffer = convertBitmapToByteBuffer(resizedImage)
                            val output = TensorBuffer.createFixedSize(intArrayOf(1, 4), DataType.FLOAT32)
                            interpreter.run(byteBuffer, output.buffer)
                            val prediction = output.floatArray
                            Log.d("ScanFragment", "Prediction: ${prediction.contentToString()}")

                            val maxIndex = prediction.indices.maxByOrNull { prediction[it] } ?: -1

                            val predictedClass = mapPredictionToClass(selectedPlant, maxIndex)

                            saveScanResult(image, predictedClass, imageUrl)
                        } catch (e: Exception) {
                            Log.e("ScanFragment", "Error running model inference: ${e.message}", e)
                        }
                    } else {
                        Toast.makeText(requireContext(), "Model file is null", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Model download failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(MODEL_INPUT_SIZE * 4)
        byteBuffer.order(ByteOrder.nativeOrder())
        val intValues = IntArray(IMAGE_SIZE * IMAGE_SIZE)
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        var pixel = 0
        for (i in 0 until IMAGE_SIZE) {
            for (j in 0 until IMAGE_SIZE) {
                val value = intValues[pixel++]
                byteBuffer.putFloat(((value shr 16) and 0xFF) / 255.0f)
                byteBuffer.putFloat(((value shr 8) and 0xFF) / 255.0f)
                byteBuffer.putFloat((value and 0xFF) / 255.0f)
            }
        }
        return byteBuffer
    }

    private fun saveScanResult(image: Bitmap, predictedClass: String, imageUrl: String) {
        val sharedPreferences = requireActivity().getSharedPreferences("user_id", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", null)
        val currentDateTime = Date()

        if (userId != null) {
            val db = FirebaseFirestore.getInstance()

            val userDocumentRef = db.document("users/$userId")

            // Query diseases collection to find matching disease by name_of_disease
            db.collection("diseases")
                .whereEqualTo("name_of_disease", predictedClass)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) {
                        Log.d(TAG, "No disease found for prediction: $predictedClass")
                        // Handle jika tidak ditemukan penyakit
                    } else {
                        // Ambil dokumen pertama yang cocok
                        val diseaseDocument = querySnapshot.documents[0]
                        val solution = diseaseDocument.getString("solution_of_diseases")

                        // Save scan result to 'histories' collection
                        val historyData = hashMapOf(
                            "user_id" to userDocumentRef,
                            "plant_name" to selectedPlant,
                            "predicted_class" to predictedClass,
                            "image_url" to imageUrl,
                            "datetime" to currentDateTime,
                            "solution" to solution  // tambahkan solution ke data history
                        )

                        db.collection("histories")
                            .add(historyData)
                            .addOnSuccessListener { historyDocumentReference ->
                                val historyDocumentRef = db.document("histories/${historyDocumentReference.id}")

                                // Save notification to 'notifications' collection
                                val notificationData = hashMapOf(
                                    "user_id" to userDocumentRef,
                                    "type" to predictedClass,
                                    "datetime" to currentDateTime,
                                    "image_url" to imageUrl,
                                    "already_read" to false,
                                    "history_id" to historyDocumentRef
                                )

                                db.collection("notifications")
                                    .add(notificationData)
                                    .addOnSuccessListener { documentReference ->
                                        AlertDialog.Builder(requireContext())
                                            .setTitle("Notification Added")
                                            .setMessage("Your scan result has been successfully added to notifications.")
                                            .setPositiveButton("OK") { dialog, _ ->
                                                dialog.dismiss()

                                                val intent = Intent(requireContext(), ImageResultActivity::class.java).apply {
                                                    putExtra("image", imageUrl)
                                                    putExtra("predictionClass", predictedClass)
                                                    putExtra("plantName", selectedPlant)
                                                }
                                                startActivity(intent)
                                            }
                                            .show()
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w(TAG, "Error adding notification document", e)
                                        Toast.makeText(requireContext(), "Failed to add notification", Toast.LENGTH_SHORT).show()
                                    }
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error adding history document", e)
                                Toast.makeText(requireContext(), "Failed to save scan result", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error querying diseases collection: $exception")
                    Toast.makeText(requireContext(), "Failed to query diseases", Toast.LENGTH_SHORT).show()
                }
        } else {
            Log.e(TAG, "User ID is null, cannot save notification.")
        }
    }


    private fun mapPredictionToClass(plantName: String, prediction: Int): String {
        return when (plantName) {
            "Jagung" -> when (prediction) {
                0 -> "Corn Common Rust"
                1 -> "Corn Gray Leaf Spot"
                2 -> "Corn Healthy"
                3 -> "Corn Northern Leaf Blight"
                else -> "Unknown"
            }
            "Padi" -> when (prediction) {
                0 -> "Rice Brown Spot"
                1 -> "Rice Healthy"
                2 -> "Rice Hispa"
                3 -> "Rice Leaf Blast"
                else -> "Unknown"
            }
            "Strawberry" -> when (prediction) {
                0 -> "Strawberry Healthy"
                1 -> "Strawberry Leaf Scorch"
                else -> "Unknown"
            }
            else -> "Unknown"
        }
    }
}

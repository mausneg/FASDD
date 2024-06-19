package com.example.fasdd_android

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.ByteArrayOutputStream

class ScanFragment : Fragment() {

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 1
    }

    private lateinit var selectedPlant: String

    private val takePicture = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageData = result.data?.extras?.get("data") as Bitmap
            predictImage(imageData)
            sendImageToResultActivity(imageData)
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

    private fun predictImage(image: Bitmap) {
        val modelName = getModelName(selectedPlant)
        FirebaseModelDownloader.getInstance()
            .getModel(modelName, DownloadType.LOCAL_MODEL, CustomModelDownloadConditions.Builder().requireWifi().build())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val modelFile = task.result?.file
                    if (modelFile != null) {
                        try {
                            val interpreter = Interpreter(modelFile)
                            val input = TensorImage.fromBitmap(image)
                            val output = TensorBuffer.createFixedSize(intArrayOf(1, 1), org.tensorflow.lite.DataType.FLOAT32)
                            interpreter.run(input.buffer, output.buffer)
                            val prediction = output.floatArray[0]
                            Log.d("ScanFragment", "Prediction: $prediction")
                            showPrediction(prediction)
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

    private fun showPrediction(prediction: Float) {
        val intent = Intent(requireContext(), ImageResultActivity::class.java).apply {
            putExtra("prediction", prediction.toString())
        }
        startActivity(intent)
    }

    private fun sendImageToResultActivity(imageData: Bitmap) {
        val intent = Intent(requireContext(), ImageResultActivity::class.java).apply {
            val stream = ByteArrayOutputStream()
            imageData.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray = stream.toByteArray()
            putExtra("image", byteArray)
        }
        startActivity(intent)
    }
}

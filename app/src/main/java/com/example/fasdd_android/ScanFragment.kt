package com.example.fasdd_android

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class ScanFragment : Fragment() {

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 1
        private const val REQUEST_IMAGE_CAPTURE = 2
    }

    private lateinit var selectedPlant: String

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

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.CAMERA),
            REQUEST_CAMERA_PERMISSION
        )
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageData = data?.extras?.get("data") as Bitmap
            val intent = Intent(requireContext(), ImageResultActivity::class.java).apply {
                putExtra("image", imageData)
                putExtra("plantName", selectedPlant)
            }
            startActivity(intent)
        }
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
}

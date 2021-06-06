package com.bangkit.farmacode.scanner

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.farmacode.databinding.ActivityScannerBinding
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ScannerActivity : AppCompatActivity() {

    companion object {
        val ID_PATIENT = null
        // Accessing camera
        private const val REQUEST_CODE = 42
    }

    private lateinit var binding: ActivityScannerBinding

    @SuppressLint("QueryPermissionsNeeded")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Camera button handle
        binding.scannerButton.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(this.packageManager) != null) {
                startActivityForResult(takePictureIntent, REQUEST_CODE)
            } else {
                Toast.makeText(this, "Unable to open camera", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        // Generate filename
        val fileName = generateFileName("uid")

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            binding.scannerImage.setImageBitmap(data?.extras?.get("data") as Bitmap)
            binding.scannerImage.isDrawingCacheEnabled = true
            binding.scannerImage.buildDrawingCache()
            val bitmap = (binding.scannerImage.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val dataBytes = baos.toByteArray()

            uploadImageToStorage(fileName, dataBytes)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    // Generate filename format(yyyyddMM-HHmmssSSS)
    @RequiresApi(Build.VERSION_CODES.O)
    private fun generateFileName(needs: String): String {
        val currentDate = LocalDateTime.now()
        val dateFormatter = DateTimeFormatter.ofPattern("yyyyddMM")
        val uniqueId = DateTimeFormatter.ofPattern("HHmmssSSS")

        return when (needs) {
            "dName" -> {
                currentDate.format(dateFormatter)
            }
            "uid" -> {
                currentDate.format(dateFormatter)+"-"+currentDate.format(uniqueId)
            }
            else -> ""
        }
    }

    //
    private fun uploadImageToStorage(fileName: String, data: ByteArray) {
        val storageRef = FirebaseStorage.getInstance().reference.child("$fileName.jpg")
        storageRef.putBytes(data)
            .addOnFailureListener {
                Toast.makeText(this, "Fail", Toast.LENGTH_SHORT).show()
            }
            .addOnSuccessListener {
                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
            }
    }
}
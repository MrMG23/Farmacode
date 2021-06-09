package com.bangkit.farmacode.scanner

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.farmacode.databinding.ActivityScannerBinding
import com.bangkit.farmacode.drug.DrugActivity
import com.bangkit.farmacode.patient.PatientActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ScannerActivity : AppCompatActivity() {

    companion object {
        val ID_PATIENT = "idPatient"

        // Accessing camera
        private const val REQUEST_CODE = 42
    }

    private lateinit var binding: ActivityScannerBinding
//    private lateinit var databaseReference: DatabaseReference
//    private var userId: String? = null

    @SuppressLint("QueryPermissionsNeeded")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        if (intent.getStringExtra(ID_PATIENT) != null){
//            userId = intent.getStringExtra(ID_PATIENT)!!
//            Log.d("userId : ", userId.toString())
//        }

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
        val docName = generateFileName("dName")
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            binding.scannerImage.setImageBitmap(data?.extras?.get("data") as Bitmap)
            binding.scannerImage.isDrawingCacheEnabled = true
            binding.scannerImage.buildDrawingCache()
            val bitmap = (binding.scannerImage.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val dataBytes = baos.toByteArray()

            uploadImageToStorage(fileName, dataBytes, docName)
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
                currentDate.format(dateFormatter) + "-" + currentDate.format(uniqueId)
            }
            else -> ""
        }
    }

    //
    private fun uploadImageToStorage(fileName: String, data: ByteArray, docName: String) {
        val storageRef = FirebaseStorage.getInstance().reference.child("$fileName.jpg")
        storageRef.putBytes(data)
            .addOnFailureListener {
                Toast.makeText(this, "Fail", Toast.LENGTH_SHORT).show()
            }
            .addOnSuccessListener {
                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                Log.d("upload : ", fileName)

                Handler().postDelayed({
                    getResponse(fileName, docName)
                }, 2000)

            }
    }

    private fun getResponse(fileName: String, docName: String) {
        val reference =
            FirebaseFirestore.getInstance().collection("farmacode-classification").document(docName)
        reference.get().addOnSuccessListener {
            val result = it.getString(fileName)
            Log.d("result : ", result.toString())
            if (result != null) {
                when {
                    result.substring(0, 1).equals("0") -> {
                        val intent = Intent(this, PatientActivity::class.java)
                        intent.putExtra(PatientActivity.EXTRA_DATA, result)
                        startActivity(intent)
                    }
                    result.substring(0, 1).equals("1") -> {
                        val idPatient = intent.getStringExtra(ID_PATIENT)
                        if (!idPatient.isNullOrEmpty()) {
                            Intent(this, PatientActivity::class.java).apply {
                                putExtra(PatientActivity.EXTRA_ID_DRUG, result)
                                putExtra(PatientActivity.EXTRA_DATA, idPatient)
                                startActivity(this)
                                finish()
                            }
                        } else {
                            val intent = Intent(this, DrugActivity::class.java)
                            intent.putExtra(DrugActivity.EXTRA_DATA, result)
                            startActivity(intent)
                        }
                    }
                    else -> {
                        Toast.makeText(this, "Incorrect Barcode", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
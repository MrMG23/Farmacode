package com.bangkit.farmacode.scanner

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.farmacode.R
import com.bangkit.farmacode.databinding.ActivityScannerBinding
import com.bangkit.farmacode.drug.DrugActivity
import com.bangkit.farmacode.patient.PatientActivity
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode

class ScannerActivity : AppCompatActivity() {

    companion object {
        const val IS_FROM_PATIENT = "false"
    }

    private lateinit var binding: ActivityScannerBinding

    // Scanner simulation
    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Scanner simulation
        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)

        codeScanner = CodeScanner(this, scannerView)

        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS
        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                when (it.text[0].toString()) {
                    "0" -> {
                        Intent(this, PatientActivity::class.java).apply {
                            putExtra(PatientActivity.EXTRA_DATA, it.text)
                            startActivity(this)
                        }
                    }
                    "1" -> {
                        Intent(this, DrugActivity::class.java).apply {
                            putExtra(DrugActivity.EXTRA_DATA, it.text)
                            startActivity(this)
                        }
                    }
                }
            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                Toast.makeText(
                    this, "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
}
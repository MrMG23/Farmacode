package com.bangkit.farmacode.patient

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.farmacode.R
import com.bangkit.farmacode.databinding.ActivityPatientBinding
import com.bangkit.farmacode.scanner.ScannerActivity
import com.bangkit.farmacode.utils.Formatter
import com.google.firebase.database.*

class PatientActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val EXTRA_DATA = "extra_data"
        private const val TAG = "PatientActivity"
    }

    private lateinit var binding: ActivityPatientBinding
    private lateinit var dbReference: DatabaseReference
    private var idPatient: String? = null
    private var drugSize: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPatientBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            hide()
        }

        idPatient = intent.getStringExtra(EXTRA_DATA)

        dbReference = FirebaseDatabase.getInstance().reference

        populateData(idPatient)

        binding.patientScanner.setOnClickListener(this)

        // For reset drug status
        binding.patientReset.setOnClickListener(this)
    }

    private fun populateData(idPatient: String?) {
        idPatient?.let { id -> dbReference.child("pasien").child(id).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.patientName.text = snapshot.child("nama").value.toString()
                binding.patientAge.text = snapshot.child("umur").value.toString()
                binding.patientGender.text = snapshot.child("gender").value.toString()

                // Formatter.change() -> change data default format to new format
                binding.patientAllergy.text = Formatter.change(snapshot.child("alergi").value.toString())
                binding.patientHistory.text = Formatter.change(snapshot.child("riwayatPenyakit").value.toString())
                binding.patientDiagnose.text = Formatter.change(snapshot.child("diagnosa").value.toString())

                // uncomment statement below if worst case happen
                // binding.patientDrug.text = Formatter.change(snapshot.child("daftarObat").value.toString())
                val drugs = snapshot.child("daftarObat")

                var drugStr = ""
                drugSize = drugs.childrenCount.toInt()
                for (i in 0 until drugSize) {
                    val index = i.toString()
                    drugStr += "${drugs.child(index).child("namaObat").value.toString()} (${drugs.child(index).child("status").value.toString()}), "
                }
                binding.patientDrug.text = Formatter.change(drugStr)

                binding.patientSchedule.text = Formatter.change(snapshot.child("jadwalObat").value.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, error.message)
            }
        }) }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.patient_scanner -> {
                Intent(this, ScannerActivity::class.java).apply {
                    putExtra(ScannerActivity.IS_FROM_PATIENT, true)
                    startActivity(this)
                }
            }
            // For reset drug status
            R.id.patient_reset -> {
                idPatient?.let { id ->
                    for (i in 0 until drugSize) {
                        val index = i.toString()
                        dbReference.child("pasien").child(id).child("daftarObat").child(index).child("status").setValue(false)
                    }
                }
            }
        }
    }
}
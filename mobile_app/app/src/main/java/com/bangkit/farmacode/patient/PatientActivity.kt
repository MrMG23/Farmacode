package com.bangkit.farmacode.patient

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.farmacode.R
import com.bangkit.farmacode.databinding.ActivityPatientBinding
import com.bangkit.farmacode.scanner.ScannerActivity
import com.bangkit.farmacode.scanner.ScannerActivity.Companion.ID_PATIENT
import com.bangkit.farmacode.utils.Formatter
import com.google.firebase.database.*

class PatientActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        // For patient id
        const val EXTRA_DATA = "extra_data"
        // For drug id (use if user click scanner button in patient activity)
        const val EXTRA_ID_DRUG = "id_drug"
        // For logging
        private const val TAG = "PatientActivity"
    }

    private lateinit var binding: ActivityPatientBinding

    // Declare variable to reference firebase realtime db
    private lateinit var dbReference: DatabaseReference

    // Support variable
    private var idPatient: String? = null
    private var idDrug: String? = null
    private var drugSize: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPatientBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            hide()
        }

        // Getting data from intent
        idPatient = intent.getStringExtra(EXTRA_DATA)
        idDrug = intent.getStringExtra(EXTRA_ID_DRUG)

        // Instance firebase db
        dbReference = FirebaseDatabase.getInstance().reference

        // For init data in ui
        populateData(idPatient)

        // For scanning drug to update the status
        binding.patientScanner.setOnClickListener(this)

        // For reset drug status
        binding.patientReset.setOnClickListener(this)
    }

    // To set the status of a drug
    private fun setDrugStatus(idDrug: String, listIdDrug: ArrayList<String>) {
        for (i in 0 until listIdDrug.size) {
            val index = i.toString()
            if (idDrug == listIdDrug[i]) {
                idPatient?.let {
                    dbReference.child("pasien").child(it).child("daftarObat").child(index).child("status").setValue(true)
                }
            }
        }
    }

    // To init data in ui
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

                // This variable is to store data that will be use for parameter in formatter
                var drugStr = ""
                // This variable is to store id drug data
                val listIdDrug = arrayListOf<String>()
                drugSize = drugs.childrenCount.toInt()

                for (i in 0 until drugSize) {
                    val index = i.toString()
                    listIdDrug.add(drugs.child(index).child("id").value.toString())

                    val status = if (drugs.child(index).child("status").value.toString() == "true") {
                        "Sudah diberikan"
                    } else {
                        "Belum diberikan"
                    }

                    drugStr += "($status) ${drugs.child(index).child("namaObat").value.toString()}, "
                }

                idDrug?.let { setDrugStatus(it, listIdDrug) }
                idDrug = null

                binding.patientDrug.text = Formatter.change(drugStr)

                binding.patientSchedule.text = Formatter.change(snapshot.child("jadwalObat").value.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, error.message)
            }
        }) }
    }

    // Button handle
    override fun onClick(p0: View?) {
        when (p0?.id) {
            // For navigate from patient to scanner
            R.id.patient_scanner -> {
                Intent(this, ScannerActivity::class.java).apply {
                    putExtra(ScannerActivity.ID_PATIENT, idPatient)
                    startActivity(this)
                    finish()
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
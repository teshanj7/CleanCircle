package com.example.ueeprojcleancircle.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.ueeprojcleancircle.R
import com.example.ueeprojcleancircle.databinding.CreateReportBinding
import com.example.ueeprojcleancircle.models.ReportModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class ReportInsertActivity : AppCompatActivity() {

    var reportImage: String? = ""
    var nodeId = ""
    private lateinit var dbRef: DatabaseReference
    private lateinit var binding: CreateReportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = CreateReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun insert_proof(view: View) {
        val myfileintent = Intent(Intent.ACTION_GET_CONTENT)
        myfileintent.type = "image/*"
        ActivityResultLauncher.launch(myfileintent)
    }

    fun insert_Location(view: View) {
        val fullName = binding.editName
        val estimateWeight = binding.editEstimate
        val reportCategory = binding.editType
        val reportDatePicker = binding.editDate
        val remarks = binding.editRemarks
        val reportCheckBox = binding.agreeCheckBox
        val btnPinLocation = binding.btnPin

        val fullNameValue = fullName.text.toString()
        val estimateWeightValue = estimateWeight.text.toString()
        val reportCategoryValue = reportCategory.selectedItem.toString()
        val reportDatePickerValue = "${reportDatePicker.dayOfMonth}/${reportDatePicker.month + 1}/${reportDatePicker.year}"
        val remarksValue = remarks.text.toString()
        val reportCheckBoxValue = reportCheckBox.isChecked

        // Create an Intent to open the PinLocationActivity
        val intent = Intent(this, PinLocationReportActivity::class.java)

        // Pass the report details as extras
        intent.putExtra("fullName", fullNameValue)
        intent.putExtra("estimateWeight", estimateWeightValue)
        intent.putExtra("reportCategory", reportCategoryValue)
        intent.putExtra("reportDatePicker", reportDatePickerValue)
        intent.putExtra("remarks", remarksValue)
        intent.putExtra("reportCheckBox", reportCheckBoxValue)
        intent.putExtra("reportImage", reportImage)

        // Start the PinLocationActivity
        startActivity(intent)

//        dbRef = FirebaseDatabase.getInstance().getReference("Reports")
//        val report = ReportModel(fullNameValue, estimateWeightValue, reportCategoryValue, reportDatePickerValue, reportImage, remarksValue)
//        val dbReference = FirebaseDatabase.getInstance().reference
//        val rId = dbReference.push().key!!
//
//        dbRef.child(rId.toString()).setValue(report)
//            .addOnCompleteListener {
//                Toast.makeText(this, "Data Inserted Successfully", Toast.LENGTH_LONG).show()
//                fullName.text.clear()
//                estimateWeight.text.clear()
//                reportImage = ""
//            }
//            .addOnFailureListener { err ->
//                Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
//            }
    }

    private val ActivityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val uri = result.data?.data
            try {
                val inputStream = contentResolver.openInputStream(uri!!)
                val myBitmap = BitmapFactory.decodeStream(inputStream)
                val stream = ByteArrayOutputStream()
                myBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val bytes = stream.toByteArray()
                reportImage = Base64.encodeToString(bytes, Base64.DEFAULT)
                binding.imageViewInsert.setImageBitmap(myBitmap)
                inputStream?.close()
                Toast.makeText(this, "Image Selected", Toast.LENGTH_SHORT).show()
            } catch (ex: Exception) {
                Toast.makeText(this, ex.message.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }

    private val reportResultLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == 2) {
            val intent = result.data
            if (intent != null) {
                nodeId = intent.getStringExtra("repo_id").toString()
            }
            dbRef = FirebaseDatabase.getInstance().getReference("Reports")
            dbRef.child(nodeId).get().addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    binding.editName.setText(dataSnapshot.child("fullName").value.toString())
                    binding.editEstimate.setText(dataSnapshot.child("estimateWeight").value.toString())
                    binding.editType.setSelection(getIndex(binding.editType, dataSnapshot.child("reportCategory").value.toString()))

                    // Check if the "ReportDatePicker" value exists and contains "/"
                    val reportDatePickerValue = dataSnapshot.child("ReportDatePicker").value?.toString()
                    if (reportDatePickerValue != null && reportDatePickerValue.contains("/")) {
                        val date = reportDatePickerValue.split("/")
                        val dateCalendar = Calendar.getInstance()
                        dateCalendar.set(date[2].toInt(), date[1].toInt() - 1, date[0].toInt())
                        binding.editDate.updateDate(dateCalendar.get(Calendar.YEAR), dateCalendar.get(Calendar.MONTH), dateCalendar.get(Calendar.DAY_OF_MONTH))
                    } else {
                        // Handle the case where the value is null or doesn't contain "/"
                        // You might want to set a default date or display an error message.
                    }

                    binding.editRemarks.setText(dataSnapshot.child("remarks").value.toString())


                    reportImage = dataSnapshot.child("reportImage").value.toString()
                    val bytes = Base64.decode(reportImage, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    binding.imageViewInsert.setImageBitmap(bitmap)
                    binding.btnDel.visibility = View.VISIBLE
                    binding.btnUPin.visibility = View.VISIBLE
                    binding.btnPin.visibility = View.INVISIBLE
                } else {
                    Toast.makeText(this, "Report Not Found", Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun getIndex(spinner: Spinner, value: String): Int {
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString() == value) {
                return i
            }
        }
        return 0 // Default to the first item if not found
    }

    fun showList(view: View) {
        val intent = Intent(this, ReportList::class.java)
        reportResultLauncher.launch(intent)
    }

    fun updateLocation(view: View) {
        // Retrieve the views
        val updatedName = binding.editName
        val updatedEstimate = binding.editEstimate
        val updatedType = binding.editType
        val updatedDate = binding.editDate
        val updatedRemarks = binding.editRemarks

        binding.btnDel.visibility = View.VISIBLE
        binding.btnUPin.visibility = View.VISIBLE
        binding.btnPin.visibility = View.INVISIBLE


        // Retrieve the values to be updated
        val updatedNameValue = updatedName.text.toString()
        val updatedEstimateValue = updatedEstimate.text.toString()
        val updatedTypeValue = updatedType.selectedItem.toString()
        val updatedDateValue = String.format("%02d/%02d/%04d", updatedDate.dayOfMonth, updatedDate.month + 1, updatedDate.year)
        val updatedRemarksValue = updatedRemarks.text.toString()

        // Update the item in the database
        val updatedReport = ReportModel(updatedNameValue, updatedEstimateValue, updatedTypeValue, updatedDateValue, reportImage, updatedRemarksValue,"Open",0.0,0.0)

        dbRef = FirebaseDatabase.getInstance().getReference("Reports")
        dbRef.child(nodeId).setValue(updatedReport)
            .addOnCompleteListener {
                updatedName.text.clear()
                updatedEstimate.text.clear()
                reportImage = ""

                Toast.makeText(this, "Data Updated Successfully", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { err ->
                Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
            }
        val intent = Intent(this, PinLocationActivity::class.java)
        startActivity(intent)
    }

    fun deleteReport(view: View) {
        dbRef = FirebaseDatabase.getInstance().getReference("Reports")
        dbRef.child(nodeId).removeValue()
    }
}

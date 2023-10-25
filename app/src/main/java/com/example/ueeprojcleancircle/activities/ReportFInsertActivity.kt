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
import com.example.ueeprojcleancircle.databinding.ActivityCreateReportUBinding
import com.example.ueeprojcleancircle.databinding.FragmentReportFormBinding
import com.example.ueeprojcleancircle.models.ReportModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class ReportFInsertActivity : AppCompatActivity() {

    var reportImage: String? = ""
    var nodeId = ""
    private lateinit var dbRef: DatabaseReference
    private lateinit var binding: FragmentReportFormBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentReportFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun insert_proof(view: View) {
        val myfileintent = Intent(Intent.ACTION_GET_CONTENT)
        myfileintent.type = "image/*"
        ActivityResultLauncher.launch(myfileintent)
    }

    fun insert_Location(view: View) {

        val estimateWeight = binding.repoFEstWaste
        val reportCategory = binding.repoFSelectWasterTypeSpinner
        val reportDatePicker = binding.repoFScheduleDate
        val remarks = binding.repoFRemarks
        val reportCheckBox = binding.agreeFTC
        val btnPinLocation = binding.btnFPinLocation


        val estimateWeightValue = estimateWeight.text.toString()
        val reportCategoryValue = reportCategory.selectedItem.toString()
        val reportDatePickerValue = "${reportDatePicker.dayOfMonth}/${reportDatePicker.month + 1}/${reportDatePicker.year}"
        val remarksValue = remarks.text.toString()
        val reportCheckBoxValue = reportCheckBox.isChecked

        //Create an Intent to open the PinLocationActivity
        val intent = Intent(this, PinLocationReportActivity::class.java)

         //Create a new instance of the ReportFPinLocation fragment
//        val reportFPinLocationFragment = ReportFPinLocation()

         //Pass the report details as extras

        intent.putExtra("estimateWeight", estimateWeightValue)
        intent.putExtra("reportCategory", reportCategoryValue)
        intent.putExtra("reportDatePicker", reportDatePickerValue)
        intent.putExtra("remarks", remarksValue)
        intent.putExtra("reportCheckBox", reportCheckBoxValue)
        intent.putExtra("reportImage", reportImage)
//        val args = Bundle()
//        args.putString("fullName", fullNameValue)
//        args.putString("estimateWeight", estimateWeightValue)
//        args.putString("reportCategory", reportCategoryValue)
//        args.putString("reportDatePicker", reportDatePickerValue)
//        args.putString("remarks", remarksValue)
//        args.putString("remarks", remarksValue)
//        args.putString("reportCheckBox", reportCheckBoxValue.toString())
//        reportFPinLocationFragment.arguments = args

         //Start the PinLocationActivity
        startActivity(intent)

        // Use the FragmentManager to replace the current fragment with the new one
//        val fragmentManager = supportFragmentManager
//        val transaction = fragmentManager.beginTransaction()
//        transaction.replace(R.id.ReportFormFragment, reportFPinLocationFragment)
//        transaction.addToBackStack(null)  // Optional: Add to back stack
//        transaction.commit()

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
                binding.InsertFImg.setImageBitmap(myBitmap)
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

                    binding.repoFEstWaste.setText(dataSnapshot.child("estimateWeight").value.toString())
                    binding.repoFSelectWasterTypeSpinner.setSelection(getIndex(binding.repoFSelectWasterTypeSpinner, dataSnapshot.child("reportCategory").value.toString()))

                    // Check if the "ReportDatePicker" value exists and contains "/"
                    val reportDatePickerValue = dataSnapshot.child("ReportDatePicker").value?.toString()
                    if (reportDatePickerValue != null && reportDatePickerValue.contains("/")) {
                        val date = reportDatePickerValue.split("/")
                        val dateCalendar = Calendar.getInstance()
                        dateCalendar.set(date[2].toInt(), date[1].toInt() - 1, date[0].toInt())
                        binding.repoFScheduleDate.updateDate(dateCalendar.get(Calendar.YEAR), dateCalendar.get(Calendar.MONTH), dateCalendar.get(Calendar.DAY_OF_MONTH))
                    } else {
                        // Handle the case where the value is null or doesn't contain "/"
                        // You might want to set a default date or display an error message.
                    }

                    binding.repoFRemarks.setText(dataSnapshot.child("remarks").value.toString())


                    reportImage = dataSnapshot.child("reportImage").value.toString()
                    val bytes = Base64.decode(reportImage, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    binding.InsertFImg.setImageBitmap(bitmap)
                    binding.btnFDelete.visibility = View.VISIBLE
                    binding.btnFUPinLocation.visibility = View.VISIBLE
                    binding.btnFPinLocation.visibility = View.INVISIBLE
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

        val updatedEstimate = binding.repoFEstWaste
        val updatedType = binding.repoFSelectWasterTypeSpinner
        val updatedDate = binding.repoFScheduleDate
        val updatedRemarks = binding.repoFRemarks

        binding.btnFDelete.visibility = View.VISIBLE
        binding.btnFUPinLocation.visibility = View.VISIBLE
        binding.btnFPinLocation.visibility = View.INVISIBLE


        // Retrieve the values to be updated
        val updatedNameValue = ""
        val updatedEstimateValue = updatedEstimate.text.toString()
        val updatedTypeValue = updatedType.selectedItem.toString()
        val updatedDateValue = String.format("%02d/%02d/%04d", updatedDate.dayOfMonth, updatedDate.month + 1, updatedDate.year)
        val updatedRemarksValue = updatedRemarks.text.toString()

        // Update the item in the database
        val updatedReport = ReportModel(updatedNameValue, updatedEstimateValue, updatedTypeValue, updatedDateValue, reportImage, updatedRemarksValue,"Open",0.0,0.0)

        dbRef = FirebaseDatabase.getInstance().getReference("Reports")
        dbRef.child(nodeId).setValue(updatedReport)
            .addOnCompleteListener {

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

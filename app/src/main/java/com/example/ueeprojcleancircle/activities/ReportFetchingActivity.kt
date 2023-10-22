package com.example.ueeprojcleancircle.activities
//
//import android.content.Intent
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.os.Bundle
//import android.util.Base64
//import android.view.View
//import android.widget.Toast
//import androidx.activity.result.ActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ueeprojcleancircle.R

//import com.example.ueeprojcleancircle.databinding.ActivityRudReportBinding
//import com.example.ueeprojcleancircle.databinding.CreateReportBinding
//import com.example.ueeprojcleancircle.models.ReportModel
//import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.FirebaseDatabase
//import java.io.ByteArrayOutputStream
//import java.lang.Exception
//
class ReportFetchingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ud_report)
    }
}
//class ReportFetchingActivity : AppCompatActivity() {
//
//    var reportImage:String? = ""
//    private lateinit var dbRef: DatabaseReference
//    private lateinit var binding : ActivityRudReportBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = ActivityRudReportBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//    }
//
//    fun insert_proof(view: View) {
//
//        var myfileintent = Intent(Intent.ACTION_GET_CONTENT)
//        myfileintent.setType("image/*")
//        ActivityResultLauncher.launch(myfileintent)
//
//    }
//    fun insert_Location(view: View) {
//
//
//        val fullName = binding.editName
//        val estimateWeight = binding.editEstimate
//        val reportCategory = binding.editType
//        val reportDatePicker = binding.editDate
//
//        val remarks = binding.editRemarks
//        val reportCheckBox = binding.agreeCheckBox
//        val btnPinLocation = binding.btnPin
//
//        val fullNameValue = fullName.text.toString()
//        val estimateWeightValue = estimateWeight.text.toString()
//        val reportCategoryValue = reportCategory.selectedItem.toString()
//        val reportDatePickerValue = "${reportDatePicker.dayOfMonth}/${reportDatePicker.month + 1}/${reportDatePicker.year}"
//        //val reportImageValue = captureImage(reportImage) // Implement the logic to capture the image
//        val remarksValue = remarks.text.toString()
//        val reportCheckBoxValue = reportCheckBox.isChecked
//
//
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
//                // remarks.text.clear()
//            }
//            .addOnFailureListener { err ->
//                Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
//            }
//
//    }
//
//    private val ActivityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
//        ActivityResultContracts.StartActivityForResult()
//    ){result: ActivityResult ->
//        if (result.resultCode == AppCompatActivity.RESULT_OK){
//            val uri = result.data!!.data
//            try {
//                val inputStream = contentResolver.openInputStream(uri!!)
//                val myBitmap = BitmapFactory.decodeStream(inputStream)
//                val stream = ByteArrayOutputStream()
//                myBitmap.compress(Bitmap.CompressFormat.PNG,100,stream)
//                val bytes = stream.toByteArray()
//                reportImage = Base64.encodeToString(bytes, Base64.DEFAULT)
//                binding.imageViewInsert.setImageBitmap(myBitmap)
//                inputStream!!.close()
//                Toast.makeText(this, "Image Selected", Toast.LENGTH_SHORT).show()
//
//            } catch (ex: Exception){
//                Toast.makeText(this, ex.message.toString(), Toast.LENGTH_LONG).show()
//            }
//        }
//
//    }
//
//    fun show_List(view: View) {
//
//        var i: Intent
//        i = Intent(this,ReportList::class.java)
//        startActivity(i)
//
//    }
//}
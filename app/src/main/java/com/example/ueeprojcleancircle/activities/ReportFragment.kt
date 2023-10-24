package com.example.ueeprojcleancircle.activities


import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.ueeprojcleancircle.R
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.ueeprojcleancircle.databinding.FragmentReportFormBinding
import com.example.ueeprojcleancircle.databinding.FragmentSchedulePickupBinding
import com.example.ueeprojcleancircle.models.ReportModel
import com.example.ueeprojcleancircle.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class ReportFragment : Fragment() {

    private var _binding: FragmentReportFormBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser

    private var wasteType: String = ""
    private lateinit var nic: String

    var reportImage: String? = ""
    var nodeId = ""

    private lateinit var dbRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReportFormBinding.inflate(inflater, container, false)
        val view = binding.root

        firebaseAuth = FirebaseAuth.getInstance()
        currentUser = firebaseAuth.currentUser!!

        getCurrentUser()

        binding.btnFPinLocation.setOnClickListener {
            handlePinLocationButton()
        }

        binding.btnFInsert.setOnClickListener {
            insert_proof(view)
        }

        binding.btnFReports.setOnClickListener {
            showList(view)
        }

        binding.btnFUPinLocation.setOnClickListener {
            updateLocation(view)
        }

        binding.btnFDelete.setOnClickListener {
            deleteReport(view)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getCurrentUser() {
        val dbRef = FirebaseDatabase.getInstance().getReference("Users")
        Log.i("Current Email", currentUser.email.toString())
        dbRef.orderByChild("email").equalTo(currentUser.email)
            .addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Get the user data from the snapshot
                        val user = snapshot.children.first().getValue(User::class.java)
                        Log.i("user", user.toString())
                        if (user != null) {
                            nic = user.nic.toString()
                            Log.i("NIC Number", nic)
                        }
                    } else {
                        // No user found with the given email
                        Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error
                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun handlePinLocationButton() {
        val fullName = binding.repoFName
        val estimateWeight = binding.repoFEstWaste
        val reportCategory = binding.repoFSelectWasterTypeSpinner
        val reportDatePicker = binding.repoFScheduleDate
        val remarks = binding.repoFRemarks
        val reportCheckBox = binding.agreeFTC

        val fullNameValue = fullName.text.toString()
        val estimateWeightValue = estimateWeight.text.toString()
        val reportCategoryValue = reportCategory.selectedItem.toString()
        val reportDatePickerValue =
            "${reportDatePicker.dayOfMonth}/${reportDatePicker.month + 1}/${reportDatePicker.year}"
        val remarksValue = remarks.text.toString()
        val reportCheckBoxValue = reportCheckBox.isChecked

        // Create a new instance of the ReportPinLocation fragment
        val reportPinLocationFragment = ReportPinLocation()

        val args = Bundle()
        args.putString("fullName", fullNameValue)
        args.putString("estimateWeight", estimateWeightValue)
        args.putString("reportCategory", reportCategoryValue)
        args.putString("reportDatePicker", reportDatePickerValue)
        args.putString("remarks", remarksValue)
        args.putString("reportCheckBox", reportCheckBoxValue.toString())

        // Pass the image data (reportImage) as well, if needed
        args.putString("reportImage", reportImage)

        reportPinLocationFragment.arguments = args

        // Replace the current fragment with the new one
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.scheduleFragmentContainerViewR, reportPinLocationFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }


    fun insert_proof(view: View) {
        val myfileintent = Intent(Intent.ACTION_GET_CONTENT)
        myfileintent.type = "image/*"
        ActivityResultLauncher.launch(myfileintent)
    }

    private val ActivityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val uri = result.data?.data
            try {
                val inputStream = requireActivity().contentResolver.openInputStream(uri!!)
                val myBitmap = BitmapFactory.decodeStream(inputStream)
                val stream = ByteArrayOutputStream()
                myBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val bytes = stream.toByteArray()
                reportImage = Base64.encodeToString(bytes, Base64.DEFAULT)
                binding.InsertFImg.setImageBitmap(myBitmap)
                inputStream?.close()
                Toast.makeText(requireContext(), "Image Selected", Toast.LENGTH_SHORT).show()
            } catch (ex: Exception) {
                Toast.makeText(requireContext(), ex.message.toString(), Toast.LENGTH_LONG).show()
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
                    binding.repoFName.setText(dataSnapshot.child("fullName").value.toString())
                    binding.repoFEstWaste.setText(dataSnapshot.child("estimateWeight").value.toString())
                    binding.repoFSelectWasterTypeSpinner.setSelection(
                        getIndex(
                            binding.repoFSelectWasterTypeSpinner,
                            dataSnapshot.child("reportCategory").value.toString()
                        )
                    )

                    // Check if the "ReportDatePicker" value exists and contains "/"
                    val reportDatePickerValue =
                        dataSnapshot.child("ReportDatePicker").value?.toString()
                    if (reportDatePickerValue != null && reportDatePickerValue.contains("/")) {
                        val date = reportDatePickerValue.split("/")
                        val dateCalendar = Calendar.getInstance()
                        dateCalendar.set(date[2].toInt(), date[1].toInt() - 1, date[0].toInt())
                        binding.repoFScheduleDate.updateDate(
                            dateCalendar.get(Calendar.YEAR), dateCalendar.get(
                                Calendar.MONTH
                            ), dateCalendar.get(Calendar.DAY_OF_MONTH)
                        )
                    } else {
                        val today = Calendar.getInstance()
                        binding.repoFScheduleDate.updateDate(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH))
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
                    Toast.makeText(requireContext(), "Report Not Found", Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_LONG).show()
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
        val intent = Intent(requireContext(), ReportList::class.java)
        reportResultLauncher.launch(intent)
    }

    fun updateLocation(view: View) {
        // Retrieve the views
        val updatedName = binding.repoFName
        val updatedEstimate = binding.repoFEstWaste
        val updatedType = binding.repoFSelectWasterTypeSpinner
        val updatedDate = binding.repoFScheduleDate
        val updatedRemarks = binding.repoFRemarks

        binding.btnFDelete.visibility = View.VISIBLE
        binding.btnFUPinLocation.visibility = View.VISIBLE
        binding.btnFPinLocation.visibility = View.INVISIBLE


        // Retrieve the values to be updated
        val updatedNameValue = updatedName.text.toString()
        val updatedEstimateValue = updatedEstimate.text.toString()
        val updatedTypeValue = updatedType.selectedItem.toString()
        val updatedDateValue = String.format("%02d/%02d/%04d", updatedDate.dayOfMonth, updatedDate.month + 1, updatedDate.year)
        val updatedRemarksValue = updatedRemarks.text.toString()
        val updatedNodeID = nodeId

        // Create a new instance of the ReportPinLocation fragment
        val reportUpdatePinLocationFragment = ReportUpdatePinLocation()

        val args = Bundle()
        args.putString("nodeID", updatedNodeID)
        args.putString("fullName", updatedNameValue)
        args.putString("estimateWeight", updatedEstimateValue)
        args.putString("reportCategory", updatedTypeValue)
        args.putString("reportDatePicker", updatedDateValue)
        args.putString("remarks", updatedRemarksValue)


        // Pass the image data (reportImage) as well, if needed
        args.putString("reportImage", reportImage)

        reportUpdatePinLocationFragment.arguments = args

        // Replace the current fragment with the new one
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.scheduleFragmentContainerViewR, reportUpdatePinLocationFragment)
        transaction.addToBackStack(null)
        transaction.commit()

    }

    fun deleteReport(view: View) {
        dbRef = FirebaseDatabase.getInstance().getReference("Reports")
        dbRef.child(nodeId).removeValue()
    }
}
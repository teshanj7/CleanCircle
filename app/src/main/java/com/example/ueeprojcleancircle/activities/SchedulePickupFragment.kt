package com.example.ueeprojcleancircle.activities


import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.ueeprojcleancircle.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ueeprojcleancircle.databinding.FragmentSchedulePickupBinding
import com.example.ueeprojcleancircle.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date

class SchedulePickupFragment : Fragment() {

    private var _binding: FragmentSchedulePickupBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser

    private var wasteType: String = ""
    private lateinit var nic: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSchedulePickupBinding.inflate(inflater, container, false)
        val view = binding.root

        firebaseAuth = FirebaseAuth.getInstance()
        currentUser = firebaseAuth.currentUser!!

        getCurrentUser()

        getWasteType()

        binding.btnPinLocation.setOnClickListener {
            handlePinLocationButton()
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
                        Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error
                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun getWasteType() {
        val wasteTypeSpinner = binding.selectWasterTypeSpinner
        val wasteTypeAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.waste_type, android.R.layout.simple_spinner_item
        )
        wasteTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        wasteTypeSpinner.adapter = wasteTypeAdapter
        wasteTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                wasteType = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }



    private fun handlePinLocationButton(){

        var estimatedWeight = binding.schEstWaste.text.toString()
        var dateStr = binding.scheduleDate.toString()
        var remarks = binding.schRemarks.text.toString()

        val dateFormat = SimpleDateFormat("yyyy-MM-dd") // Adjust the date format as per your input
        var date: Date? = null
        try {
            date = dateFormat.parse(dateStr)
        } catch (e: Exception) {
            // Handle the exception or show an error message if date parsing fails
        }

        val bundle = Bundle()
        bundle.putString("estimatedWeight", estimatedWeight)
        bundle.putString("wasteType", wasteType)
        bundle.putString("dateStr", dateStr)
        bundle.putString("remarks", remarks)
        bundle.putString("nic", nic)

        val schedulePinLocationFragment = SchedulePinLocation()
        schedulePinLocationFragment.arguments = bundle

        // Replace the current fragment with the new one
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.scheduleFragmentContainerView, schedulePinLocationFragment)
        transaction.addToBackStack(null)
        transaction.commit()

    }
}
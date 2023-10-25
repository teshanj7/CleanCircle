package com.example.ueeprojcleancircle.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.ueeprojcleancircle.R
import com.example.ueeprojcleancircle.databinding.ActivityActivePickupRequestBinding
import com.example.ueeprojcleancircle.models.ScheduleForm
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class ActivePickupRequestActivity : AppCompatActivity() {

    private lateinit var binding : ActivityActivePickupRequestBinding
    private lateinit var firebaseAuth: FirebaseAuth
    //db reference
    private lateinit var dbRef: DatabaseReference
    private lateinit var currentUser: FirebaseUser
    private var requestKey: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActivePickupRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Authentication and get the current user
        firebaseAuth = FirebaseAuth.getInstance()
        currentUser = firebaseAuth.currentUser!!

        if (currentUser != null) {
            val useremail = currentUser.email
            dbRef = FirebaseDatabase.getInstance().getReference("PickupRequests")
            dbRef.orderByChild("email").equalTo(useremail)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (childSnapshot in snapshot.children) {
                                val request = childSnapshot.getValue(ScheduleForm::class.java)
                                requestKey = childSnapshot.key
                                binding.actName.text = request?.email
                                binding.actWaste.text = request?.wasteType
                                binding.actDate.text = request?.date
                                binding.actRemark.text = request?.remarks
                            }
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Active Request not found",
                                Toast.LENGTH_LONG
                            ).show()
                            redirectToHomeActivity()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        //handle the error
                        Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
                        redirectToHomeActivity()
                    }
                })
        } else {
            redirectToHomeActivity()
        }

        binding.btnConfirmPickup.setOnClickListener {
            // Delete the request
            if (requestKey != null) {
                dbRef.child(requestKey!!).removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(
                            applicationContext,
                            "Waste Pickup Confirmed!",
                            Toast.LENGTH_LONG
                        ).show()
                        redirectToHomeActivity()
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            applicationContext,
                            "Failed to delete request",
                            Toast.LENGTH_SHORT
                        ).show()
                        redirectToHomeActivity()
                    }
            }
        }
    }

    private fun redirectToHomeActivity() {
        val intent = Intent(this, CitizenHomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}


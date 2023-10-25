package com.example.ueeprojcleancircle.activities

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.ueeprojcleancircle.R
import com.example.ueeprojcleancircle.databinding.ActivityActivePickupRequestBinding
import com.example.ueeprojcleancircle.databinding.ActivityUserProfileBinding
import com.example.ueeprojcleancircle.models.ScheduleForm
import com.example.ueeprojcleancircle.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserProfileActivity : AppCompatActivity() {

    private lateinit var binding : ActivityUserProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    //db reference
    private lateinit var dbRef: DatabaseReference
    private lateinit var currentUser: FirebaseUser
    private var requestKey: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Authentication and get the current user
        firebaseAuth = FirebaseAuth.getInstance()
        currentUser = firebaseAuth.currentUser!!

        var useremail = currentUser.email
        dbRef = FirebaseDatabase.getInstance().getReference("Users")

        dbRef.orderByChild("email").equalTo(useremail)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (childSnapshot in snapshot.children) {
                            val user = childSnapshot.getValue(User::class.java)
                            requestKey = childSnapshot.key
                            binding.userNameprofile.text = user?.fullName
                            binding.userEmailprofile.text = user?.email
                            binding.userNICprofile.text = user?.nic
                            binding.userADDprofile.text = user?.address
                            binding.userTELEprofile.text = user?.phone
                            binding.userPassprofile.text = user?.password

                            binding.btndelete.setOnClickListener {
                                showConfirmationDialog()
                            }
                        }
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Active Request not found",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    //handle the error
                    Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
                }
            })

        binding.btnBackViewProfile.setOnClickListener {
            val intent = Intent(this, CitizenHomeActivity::class.java)
            startActivity(intent)
        }

        binding.btnviewUpdate.setOnClickListener {
            val intent = Intent(this, UpdateProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Account")
        builder.setMessage("Are you sure you want to delete your account?")

        builder.setPositiveButton("Yes") { dialog, which ->
            // Delete the user account
            requestKey?.let { key ->
                dbRef.child(key).removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(
                            applicationContext,
                            "Account Deleted Successfully",
                            Toast.LENGTH_LONG
                        ).show()
                        firebaseAuth.signOut()
                        val intent = Intent(this, LoginPageActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            applicationContext,
                            "Failed to delete account",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }

        builder.setNegativeButton("No") { dialog, which ->
            // Do nothing
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}
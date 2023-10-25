package com.example.ueeprojcleancircle.activities

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.ueeprojcleancircle.R
import com.example.ueeprojcleancircle.databinding.ActivityCitizenHomeBinding
import com.example.ueeprojcleancircle.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CitizenHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCitizenHomeBinding
    private lateinit var dbRef: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Authentication and get the current user
        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser

        binding = ActivityCitizenHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.schedulePickup.setOnClickListener {
            val intent = Intent(this, SchedulePickupActivity::class.java)
            startActivity(intent)
        }
        binding.CitizenReport.setOnClickListener {
            val intent = Intent(this, ReportPageActivity::class.java)
            startActivity(intent)
        }

        binding.btnReqActive.setOnClickListener {
            val intent = Intent(this, ActivePickupRequestActivity::class.java)
            startActivity(intent)
        }

        binding.logOutCitizen.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Log Out")
            builder.setMessage("Are you sure you want to log out?")

            builder.setPositiveButton("Yes") { dialog, which ->
                firebaseAuth.signOut()
                Toast.makeText(this, "Logged Out Successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginPageActivity::class.java)
                startActivity(intent)
                finish()
            }

            builder.setNegativeButton("No") { dialog, which ->
                // Do nothing
            }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        binding.recycle.setOnClickListener{
            val intent = Intent(this, RecycleHomeActivity::class.java)
            startActivity(intent)
        }

        binding.buttonEditProfile.setOnClickListener {
            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
        }

        binding.citizenInfo.setOnClickListener {
            val intent = Intent(this, InfoHubActivity::class.java)
            startActivity(intent)
        }

        // Set the full name to the DBfullName TextView
        currentUser?.let { user ->
            val dbRef = FirebaseDatabase.getInstance().getReference("Users")
            dbRef.orderByChild("email").equalTo(user.email).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val user = snapshot.children.first().getValue(User::class.java)
                        user?.let {
                            binding.DBfullName.text = it.fullName
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@CitizenHomeActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }


    }
}
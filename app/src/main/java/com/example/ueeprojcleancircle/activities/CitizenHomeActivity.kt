package com.example.ueeprojcleancircle.activities

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.ueeprojcleancircle.R
import com.example.ueeprojcleancircle.databinding.ActivityCitizenHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

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


    }
}
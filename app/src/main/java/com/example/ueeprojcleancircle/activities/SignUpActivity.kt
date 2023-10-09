package com.example.ueeprojcleancircle.activities

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.example.models.User
import com.example.ueeprojcleancircle.R
import com.example.ueeprojcleancircle.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        dbRef = FirebaseDatabase.getInstance().getReference("Users")

        binding.signuptoLogin.setOnClickListener {
            val intent = Intent(this, LoginPageActivity::class.java)
            startActivity(intent)
        }

        val accountType = "User".toString()

        binding.btnSubmitC.setOnClickListener {
            val fullName = binding.userName.text.toString()
            val nic = binding.userNIC.text.toString()
            val address = binding.userADD.text.toString()
            val email = binding.userEmail.text.toString()
            val password = binding.userPASS.text.toString()
//            val phone = binding.phoneUM.text.toString()


            if (fullName.isNotEmpty() && email.isNotEmpty() && nic.isNotEmpty() && password.isNotEmpty()
                 && address.isNotEmpty()) {

                val user = User(fullName, email, nic, password, address, accountType)
                dbRef.child(nic).setValue(user)

                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {

                    if (it.isSuccessful) {
                        val intent = Intent(this, LoginPageActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Cannot leave empty fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
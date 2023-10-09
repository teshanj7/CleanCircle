package com.example.ueeprojcleancircle.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.ueeprojcleancircle.R
import com.example.ueeprojcleancircle.databinding.ActivityLoginPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginPageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginPageBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private var accountType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        binding.btnLogintosignup.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        val accountTypeSpinner = binding.spinner
        val accountTypeAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.user_type, android.R.layout.simple_spinner_item
        )
        accountTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        accountTypeSpinner.adapter = accountTypeAdapter
        accountTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                accountType = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.userName.text.toString()
            val password = binding.userPASS.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            // Check if user exists in database and has the correct account type
                            val currentUser = FirebaseAuth.getInstance().currentUser

                            Log.i("Email", email.toString())
                            Log.i("Account Type", accountType)
                            if (currentUser != null) {
                                if (accountType == "Wastage Worker"){
                                    if (email == "admin@gmail.com"){
                                        val intent = Intent(
                                            this@LoginPageActivity,
                                            AdminHomeActivity::class.java
                                        )
                                        startActivity(intent)
                                    }
                                    else{
                                        val intent = Intent(
                                            this@LoginPageActivity,
                                            WasteWorkerHomeActivity::class.java
                                        )
                                        startActivity(intent)
                                    }

                                }
                                else if (accountType == "Citizen"){
                                    val intent = Intent(
                                        this@LoginPageActivity,
                                        CitizenHomeActivity::class.java
                                    )
                                    startActivity(intent)
                                }
                                val userQuery = databaseReference.orderByChild("email").equalTo(email)
                                Log.i("TAG", userQuery.toString())

                            } else {
                                // Current user is null
                                Toast.makeText(
                                    this@LoginPageActivity,
                                    "User not found",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            // Login failed
                            Toast.makeText(
                                this@LoginPageActivity,
                                "Details Incorrect",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
            }
        }
    }

}
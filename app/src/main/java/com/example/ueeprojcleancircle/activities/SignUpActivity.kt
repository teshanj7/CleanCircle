package com.example.ueeprojcleancircle.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.ueeprojcleancircle.models.User
import com.example.ueeprojcleancircle.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    private lateinit var user: User

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

        binding.btnSubmitC.setOnClickListener {

            getFormData()

            user.nic?.let { it1 -> dbRef.child(it1).setValue(user) }

            user.email?.let { it1 ->
                user.password?.let { it2 ->
                    firebaseAuth.createUserWithEmailAndPassword(it1, it2).addOnCompleteListener {

                        if (it.isSuccessful) {
                            val intent = Intent(this, LoginPageActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun getFormData(){

        val fullName = binding.userName.text.toString()
        val nic = binding.userNIC.text.toString()
        val address = binding.userADD.text.toString()
        val email = binding.userEmail.text.toString()
        val password = binding.userPASS.text.toString()
        val phone = binding.userPhone.text.toString()

        if (fullName.isNotEmpty() && email.isNotEmpty() && nic.isNotEmpty() && password.isNotEmpty()
            && phone.isNotEmpty()) {

            user = User(fullName, email, nic, password, address, phone)

        } else {
            Toast.makeText(this, "Cannot leave empty fields", Toast.LENGTH_SHORT).show()
        }
    }
}
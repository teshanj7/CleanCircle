package com.example.ueeprojcleancircle.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.ueeprojcleancircle.R
import com.example.ueeprojcleancircle.databinding.ActivityUpdateProfileBinding
import com.example.ueeprojcleancircle.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UpdateProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    private lateinit var currentUser: FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateProfileBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_update_profile)
        setContentView(binding.root)

        //Firebase Authenticaton and get current user
        firebaseAuth = FirebaseAuth.getInstance()
        currentUser = firebaseAuth.currentUser!!

        //check user currently logged in
        if (currentUser != null){
            val useremail = currentUser.email
            dbRef = FirebaseDatabase.getInstance().getReference("Users")

            dbRef.orderByChild("email").equalTo(useremail).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        //get user data from the database
                        val user = snapshot.children.first().getValue(User::class.java)

                        //set user data
                        binding.userNameupdate.setText(user?.fullName)
                        binding.userEmailupdate.setText(user?.email)
                        binding.userNICupdate.setText(user?.nic)
                        binding.userADDupdate.setText(user?.address)
                        binding.userTELEupdate.setText(user?.phone)
                        binding.userPASSupdate.setText(user?.password)
                    }else{
                        Toast.makeText(applicationContext,"User not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    //handle the error
                    Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
                }
            })
        }else{
            //user is not logged in, so redirect to the login activity
            val intent = Intent(this, LoginPageActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnViewUpdate.setOnClickListener {
            saveUpdate()
        }

        binding.btnUpdateBack.setOnClickListener {
            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun saveUpdate() {
        val name = binding.userNameupdate.text.toString().trim()
        val email = binding.userEmailupdate.text.toString().trim()
        val nic = binding.userNICupdate.text.toString().trim()
        val address = binding.userADDupdate.text.toString().trim()
        val phone = binding.userTELEupdate.text.toString().trim()
        val pass = binding.userPASSupdate.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || nic.isEmpty() || address.isEmpty() || phone.isEmpty() || pass.isEmpty()){
            Toast.makeText(this,"Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        //update the user details in the database
        dbRef.orderByChild("email").equalTo(currentUser.email).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    snapshot.children.forEach { dataSnapshot ->
                        val user = dataSnapshot.getValue(User::class.java)
                        user?.let {
                            //update user details based on the email
                            it.fullName = name
                            it.email = email
                            it.nic = nic
                            it.address = address
                            it.phone = phone
                            it.password = pass
                            dbRef.child(dataSnapshot.key!!).setValue(user)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this@UpdateProfileActivity,
                                        "Updated Successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent = Intent(this@UpdateProfileActivity, UserProfileActivity::class.java)
                                    startActivity(intent)
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        this@UpdateProfileActivity,
                                        "Fail to save changes",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    }
                }else{
                    //user not found
                    Toast.makeText(applicationContext,"User not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //handle the error
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}
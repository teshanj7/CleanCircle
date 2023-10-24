package com.example.ueeprojcleancircle.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.ueeprojcleancircle.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.ueeprojcleancircle.databinding.ActivityPinLocationBinding
import com.example.ueeprojcleancircle.models.ReportModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.example.ueeprojcleancircle.models.ScheduleForm
import com.example.ueeprojcleancircle.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class PinLocationReportActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityPinLocationBinding

    // The entry point to the Fused Location Provider.
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var dbRef: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser

    private lateinit var reportModel: ReportModel

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var mapReady = false

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPinLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        currentUser = firebaseAuth.currentUser!!
        Log.d("User", currentUser.email.toString())


        val fullName = intent.getStringExtra("fullName")
        val estimateWeight = intent.getStringExtra("estimateWeight")
        val reportCategory = intent.getStringExtra("reportCategory")
        val reportDatePicker = intent.getStringExtra("reportDatePicker")
        val remarks = intent.getStringExtra("remarks")
        val reportImage = intent.getStringExtra("reportImage")

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        dbRef = FirebaseDatabase.getInstance().getReference("Reports")

        getCurrentLocation()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.pinLocation.setOnClickListener {
            val status = "Open"
            val userEmail = currentUser.email

            val reportModel = ReportModel(
                fullName,
                estimateWeight,
                reportCategory,
                reportDatePicker,
                reportImage,
                remarks,
                status,
                latitude,
                longitude
            )

            val dbReference = dbRef.push() // Create a new unique key
            dbReference.setValue(reportModel)
                .addOnCompleteListener {
                    Toast.makeText(this, "Data Inserted Successfully", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { err ->
                    Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
                }
        }




    }
    private fun getCurrentUser(){

        val dbRef = FirebaseDatabase.getInstance().getReference("Users")
        dbRef.orderByChild("email").equalTo(currentUser.email).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Get the user data from the snapshot
                    val user = snapshot.children.first().getValue(User::class.java)
                    if (user != null) {
                        var name = user.fullName.toString()
                    }
                } else {
                    // No user found with the given email
                    Toast.makeText(applicationContext, "User not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getCurrentLocation() {
        if (checkPermission()) {
            if (isLocationEnabled()) {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        Toast.makeText(this, "Unable to get Location", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                        latitude = location.latitude
                        longitude = location.longitude
                        updateMapLocation()
                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermission()
        }
    }

    private fun isLocationEnabled() : Boolean{
        val locationManager:LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    private fun checkPermission(): Boolean{
        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(
            this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(applicationContext, "Granted", Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            }
            else{
                Toast.makeText(applicationContext, "Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mapReady = true
        updateMapLocation()
    }

    private fun updateMapLocation() {
        if (mapReady) {
            val location = LatLng(latitude, longitude)
            mMap.addMarker(MarkerOptions().position(location).title("Your Location"))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
        }
    }
}
package com.example.ueeprojcleancircle.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.ueeprojcleancircle.R
import com.example.ueeprojcleancircle.databinding.ActivityPinLocationBinding
import com.example.ueeprojcleancircle.databinding.FragmentReportPinLocationBinding
import com.example.ueeprojcleancircle.databinding.FragmentSchedulePinLocationBinding
import com.example.ueeprojcleancircle.models.ReportModel
import com.example.ueeprojcleancircle.models.ScheduleForm
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ReportUpdatePinLocation : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var _binding: FragmentReportPinLocationBinding? = null
    private val binding get() = _binding!!

    // The entry point to the Fused Location Provider.
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var dbRef: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var mapReady = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReportPinLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        currentUser = firebaseAuth.currentUser!!
        Log.d("User", currentUser.email.toString())

        val UnodeID = arguments?.getString("nodeID")
        val fullName = arguments?.getString("fullName")
        val estimateWeight = arguments?.getString("estimateWeight")
        val reportCategory = arguments?.getString("reportCategory")
        val reportDatePicker = arguments?.getString("reportDatePicker")
        val remarks = arguments?.getString("remarks")
        val reportImage = arguments?.getString("reportImage")

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        dbRef = FirebaseDatabase.getInstance().getReference("Reports")

        getCurrentLocation()

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.pinLocation.setOnClickListener {
            val status = "Open"
            val userEmail = currentUser.email

            val updatedReport = ReportModel(
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

            dbRef = FirebaseDatabase.getInstance().getReference("Reports")
            dbRef.child(UnodeID.toString()).setValue(updatedReport)
                .addOnCompleteListener {
                    Toast.makeText(requireContext(), "Data Updated Successfully", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { err ->
                    Toast.makeText(requireContext(), "Error ${err.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getCurrentLocation() {
        if (checkPermission()) {
            if (isLocationEnabled()) {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        Toast.makeText(requireContext(), "Unable to get Location", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                        latitude = location.latitude
                        longitude = location.longitude
                        updateMapLocation()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Turn on location", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermission()
        }
    }


    private fun isLocationEnabled(): Boolean {
        val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }


    private fun checkPermission(): Boolean {
        val coarseLocationPermission = ActivityCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        val fineLocationPermission = ActivityCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        return coarseLocationPermission == PackageManager.PERMISSION_GRANTED &&
                fineLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
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
                Toast.makeText(requireContext(), "Granted", Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            }
            else{
                Toast.makeText(requireContext(), "Denied", Toast.LENGTH_SHORT).show()
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
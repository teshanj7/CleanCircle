package com.example.ueeprojcleancircle.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ueeprojcleancircle.R
import com.example.ueeprojcleancircle.databinding.ActivityCitizenHomeBinding

class CitizenHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCitizenHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCitizenHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.schedulePickup.setOnClickListener {
            val intent = Intent(this, SchedulePickupActivity::class.java)
            startActivity(intent)
        }
    }
}
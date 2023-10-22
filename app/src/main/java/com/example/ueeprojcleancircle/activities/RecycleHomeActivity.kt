package com.example.ueeprojcleancircle.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ueeprojcleancircle.R
import com.example.ueeprojcleancircle.databinding.ActivityRecycleHomeBinding

class RecycleHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecycleHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecycleHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnBackRecycle.setOnClickListener {
            val intent = Intent(this, CitizenHomeActivity::class.java)
            startActivity(intent)
        }

        binding.btnRecycleTypes.setOnClickListener {
            val intent = Intent(this, RecycleMethodsActivity::class.java)
            startActivity(intent)
        }

        binding.btnCompostGen.setOnClickListener {
            val intent = Intent(this, CompostTrackerActivity::class.java)
            startActivity(intent)
        }

    }
}

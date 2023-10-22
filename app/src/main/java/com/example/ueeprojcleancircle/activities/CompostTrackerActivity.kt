package com.example.ueeprojcleancircle.activities

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.ueeprojcleancircle.R
import com.example.ueeprojcleancircle.databinding.ActivityCompostTrackerBinding

class CompostTrackerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCompostTrackerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompostTrackerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnBackComp.setOnClickListener {
            val intent = Intent(this, RecycleHomeActivity::class.java)
            startActivity(intent)
        }

        binding.btnCompRedirect.setOnClickListener {
            val intent = Intent(this, RecycleMethodsActivity::class.java)
            startActivity(intent)
        }

        binding.btnCalc.setOnClickListener {
            val input = binding.comEstWaste.text.toString()
            if (input.isNotEmpty()) {
                val weight = input.toInt()
                val result = when (weight) {
                    in 0..5 -> "It will take 2 weeks to 1 month."
                    in 10..20 -> "It will take 1 month to 2 months."
                    else -> "It will take more than 3 months."
                }
                val dialogBuilder = AlertDialog.Builder(this)
                dialogBuilder.setMessage("$result")
                    .setCancelable(true)
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                val alert = dialogBuilder.create()
                alert.setTitle("Estimated Compost Generation Time")
                alert.show()
            } else {
                Toast.makeText(this, "Please enter a valid weight.", Toast.LENGTH_SHORT).show()
            }
        }


    }
}
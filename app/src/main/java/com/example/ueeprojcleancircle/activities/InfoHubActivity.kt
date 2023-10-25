package com.example.ueeprojcleancircle.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ueeprojcleancircle.R
import com.example.ueeprojcleancircle.databinding.ActivityInfoHubBinding
import com.example.ueeprojcleancircle.databinding.ActivityUpdateProfileBinding

class InfoHubActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInfoHubBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoHubBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnWebsite.setOnClickListener {
            goToUrl("https://www.sliit.lk")
        }

        binding.btnFacebook.setOnClickListener {
            goToUrl("https://web.facebook.com/sliit.lk/?_rdc=1&_rdr")
        }

        binding.btnInstagram.setOnClickListener {
            goToUrl("https://www.instagram.com/sliit.life/")
        }

        binding.btnInfoBack.setOnClickListener {
            val intent = Intent(this, CitizenHomeActivity::class.java)
            startActivity(intent)
        }
    }

    private fun goToUrl(website: String) {
        val uri = Uri.parse(website)
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }
}
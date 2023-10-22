package com.example.ueeprojcleancircle.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ueeprojcleancircle.R
import com.example.ueeprojcleancircle.databinding.ActivityRecycleMethodsBinding
import android.net.Uri

class RecycleMethodsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecycleMethodsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecycleMethodsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnViewComposting.setOnClickListener {
            goToUrl("https://youtu.be/egyNJ7xPyoQ?si=ovw9E6PDA6gYWerC")
        }

        binding.btnViewReuse.setOnClickListener {
            goToUrl("https://youtu.be/RJFSF_MHGYc?si=JFqAgTc5W2XXf16K")
        }

        binding.btnViewEco.setOnClickListener {
            goToUrl("https://youtube.com/shorts/fuVbz-jotJQ?si=Bul2afmKY-Y3r7V7")
        }

        binding.btnBackRecycleMethods.setOnClickListener {
            val intent = Intent(this, RecycleHomeActivity::class.java)
            startActivity(intent)
        }
    }
    private fun goToUrl(website: String) {
        val uri = Uri.parse(website)
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }
}
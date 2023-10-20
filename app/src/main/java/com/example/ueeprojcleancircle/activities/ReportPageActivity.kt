package com.example.ueeprojcleancircle.activities

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.example.ueeprojcleancircle.R


class ReportPageActivity : AppCompatActivity() {
    private lateinit var btnInsertData: Button
    private lateinit var btnFetchData: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_selection)

        //val firebase : DatabaseReference = FirebaseDatabase.getInstance().getReference()

        btnInsertData = findViewById(R.id.btnReportInsertData)
        btnFetchData = findViewById(R.id.btnJReportFetchData)

        btnInsertData.setOnClickListener {
            val intent = Intent(this, ReportInsertActivity::class.java)
            startActivity(intent)
        }
        btnFetchData.setOnClickListener{
            val intent = Intent(this, ReportList::class.java)
            startActivity(intent)
        }
    }
}
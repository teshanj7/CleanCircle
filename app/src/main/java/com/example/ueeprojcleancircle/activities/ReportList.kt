package com.example.ueeprojcleancircle.activities

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ueeprojcleancircle.R
import com.example.ueeprojcleancircle.adapters.ReportAdapter
import com.example.ueeprojcleancircle.models.ReportModel
import com.example.ueeprojcleancircle.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ReportList : AppCompatActivity() {

    private lateinit var db: DatabaseReference
    private lateinit var reportRecycleView: RecyclerView
    private lateinit var reportArrayList: ArrayList<ReportModel>

    private lateinit var firebaseAuth: FirebaseAuth
    private var currentUser: FirebaseUser? = null

    private var wasteType: String = ""
    private lateinit var nic: String
    private lateinit var getFullName: String

    private val nodeList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_list)
        reportRecycleView = findViewById(R.id.repoList)
        reportRecycleView.layoutManager = LinearLayoutManager(this)
        reportRecycleView.setHasFixedSize(true)
        reportArrayList = arrayListOf<ReportModel>()

        firebaseAuth = FirebaseAuth.getInstance()
        currentUser = firebaseAuth.currentUser

        if (currentUser != null) {
            getCurrentUserFullName()
        }
    }

    private fun getCurrentUserFullName() {
        val dbRef = FirebaseDatabase.getInstance().getReference("Users")
        dbRef.orderByChild("email").equalTo(currentUser?.email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val user = snapshot.children.first().getValue(User::class.java)
                        if (user != null) {
                            val fullName = user.fullName.toString()
                            Log.i("Full Name", fullName)
                            getReportData(fullName)
                        }
                    } else {
                        Toast.makeText(this@ReportList, "User not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ReportList, error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun getReportData(currentUserFullName: String) {
        val emptyReportAdapter = ReportAdapter(ArrayList())
        reportRecycleView.adapter = emptyReportAdapter

        db = FirebaseDatabase.getInstance().getReference("Reports")
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val filteredReports = ArrayList<ReportModel>()

                    for (reportsnapshot in snapshot.children) {
                        val report = reportsnapshot.getValue(ReportModel::class.java)
                        if (report != null && currentUserFullName == report.fullName) {
                            filteredReports.add(report)
                            nodeList.add(reportsnapshot.key.toString())
                        }
                    }

                    val reportAdapter = ReportAdapter(filteredReports)
                    reportRecycleView.adapter = reportAdapter
                    reportAdapter.setOnReportClickListner(object : ReportAdapter.onReportClickListner {
                        override fun onReportClick(position: Int) {
                            val nodePath: String = nodeList[position]
                            val intent = Intent()
                            intent.putExtra("repo_id", nodePath)
                            setResult(2, intent)
                            finish()
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Firebase data retrieval error: " + error.message)
            }
        })
    }
}

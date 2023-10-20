package com.example.ueeprojcleancircle.activities

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ueeprojcleancircle.R
import com.example.ueeprojcleancircle.adapters.ReportAdapter
import com.example.ueeprojcleancircle.models.ReportModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ReportList : AppCompatActivity() {

    private lateinit var db: DatabaseReference
    private lateinit var reportRecycleView: RecyclerView
    private lateinit var reportArrayList:ArrayList<ReportModel>

    private val nodeList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_list)
        reportRecycleView = findViewById(R.id.repoList)
        reportRecycleView.layoutManager = LinearLayoutManager(this)
        reportRecycleView.hasFixedSize()
        reportArrayList = arrayListOf<ReportModel>()
        getReportData()


    }

    private fun getReportData() {

        val emptyReportAdapter = ReportAdapter(ArrayList())
        reportRecycleView.adapter = emptyReportAdapter


        db = FirebaseDatabase.getInstance().getReference("Reports")
        db.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){
                    for(reportsnapshot in snapshot.children){
                        val report = reportsnapshot.getValue(ReportModel::class.java)
                        reportArrayList.add(report!!)
                        nodeList.add(reportsnapshot.key.toString())
                    }

                    val reportAdapter = ReportAdapter(reportArrayList)
                    reportRecycleView.adapter = reportAdapter
                    reportAdapter.setOnReportClickListner(object : ReportAdapter.onReportClickListner{
                        override fun onReportClick(position: Int) {
                            val nodePath:String = nodeList[position]
                            val intent = Intent()
                            intent.putExtra("repo_id", nodePath)
                            setResult(2,intent)
                            finish()
                        }

                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Firebase data retrieval error: " + error.getMessage());
            }

        })

    }


}
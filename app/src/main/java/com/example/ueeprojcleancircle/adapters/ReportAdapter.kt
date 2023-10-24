package com.example.ueeprojcleancircle.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.ueeprojcleancircle.R
import com.example.ueeprojcleancircle.models.ReportModel

class ReportAdapter(private val ReportList:ArrayList<ReportModel>):
    RecyclerView.Adapter<ReportAdapter.ReportHolder>() {

    private lateinit var mListner:onReportClickListner
    interface onReportClickListner{
        fun onReportClick(position: Int)
    }

    fun setOnReportClickListner(listner: onReportClickListner){
        mListner = listner
    }

    class ReportHolder(ReportView: View,listner: onReportClickListner):RecyclerView.ViewHolder(ReportView){
        val reportCategory:EditText = ReportView.findViewById(R.id.DType)
        val estimateWeight:EditText = ReportView.findViewById(R.id.DEWeight)
        val remarks:EditText = ReportView.findViewById(R.id.DRemark)
        val RepoImg:ImageView = ReportView.findViewById(R.id.vImg)

        init {
            ReportView.setOnClickListener{
                listner.onReportClick(adapterPosition)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportHolder {
        val reportView = LayoutInflater.from(parent.context).inflate(R.layout.report,parent,false)
        return ReportHolder(reportView,mListner)
    }

    override fun getItemCount(): Int {
        return ReportList.size
    }

    override fun onBindViewHolder(holder: ReportHolder, position: Int) {
        val currentReport = ReportList[position]
        holder.reportCategory.setText(currentReport.wasteType.toString())
        holder.estimateWeight.setText(currentReport.estimateWeight.toString())
        holder.remarks.setText(currentReport.remarks.toString())
        val bytes = android.util.Base64.decode(currentReport.reportImage,android.util.Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
        holder.RepoImg.setImageBitmap(bitmap)
    }
}
package com.example.ueeprojcleancircle.activities

import android.Manifest
import android.app.DownloadManager
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.ueeprojcleancircle.R
import com.itextpdf.text.BaseColor
import com.itextpdf.text.Document
import com.itextpdf.text.Font
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.content.Intent
import android.content.Context
import java.io.FileInputStream

class PdfDownloadFragment : Fragment() {
    private var pdfFilePath: String? = null
    private lateinit var reportData: Bundle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reportData = requireArguments()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pdf_download, container, false)

        pdfFilePath = reportData.getString("pdfFilePath")

        val downloadButton: Button = view.findViewById(R.id.downloadButton)

        downloadButton.setOnClickListener {
            generateAndDownloadPdf()
        }

        return view
    }

    private fun generateAndDownloadPdf() {
        // Create a new PDF document
        val document = Document()

        try {
            // Set up PDF writer and output stream
            val pdfFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "report.pdf")
            val outputStream = FileOutputStream(pdfFile)
            val writer = PdfWriter.getInstance(document, outputStream)
            document.open()

            // Create a font for the document
            val font = Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD, BaseColor.BLACK)

            // Create a paragraph for styling
            val paragraph = Paragraph()

            // Add content to the PDF using the data and apply styling
            val fullName = reportData.getString("fullName")
            val estimateWeight = reportData.getString("estimateWeight")
            val reportCategory = reportData.getString("reportCategory")
            val reportDatePicker = reportData.getString("reportDatePicker")
            val remarks = reportData.getString("remarks")

            // Set font for the paragraph
            paragraph.font = font

            // Add content with styling to the paragraph
            paragraph.add("Full Name: $fullName\n")
            paragraph.add("Estimate Weight: $estimateWeight\n")
            paragraph.add("Report Category: $reportCategory\n")
            paragraph.add("Report Date: $reportDatePicker\n")
            paragraph.add("Remarks: $remarks\n")

            // Add the styled paragraph to the document
            document.add(paragraph)

            document.close()

            // Offer the PDF for download using the code below
            offerPdfForDownload(pdfFile)

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun offerPdfForDownload(pdfFile: File) {
        // Check permission
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val request = DownloadManager.Request(Uri.fromFile(pdfFile))
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            request.setAllowedOverRoaming(false)
            request.setTitle("Report PDF")
            request.setDescription("Downloading PDF")
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "report.pdf" // File name for the downloaded PDF
            )

            val downloadManager =
                requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)

            Toast.makeText(
                requireContext(),
                "Downloading PDF to your phone...",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            // Request permission to write to external storage if not granted
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
            )
        }
    }


    companion object {
        private const val PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1
    }
}

package com.example.ueeprojcleancircle.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.ueeprojcleancircle.R
import com.itextpdf.text.BaseColor
import com.itextpdf.text.Chunk
import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.Paragraph
import com.itextpdf.text.Rectangle
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.draw.LineSeparator
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


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

            // Create a font for the heading
            val headingFont = Font(Font.FontFamily.HELVETICA, 20f, Font.BOLD, BaseColor.GREEN)

            // Create a paragraph for the heading
            val headingParagraph = Paragraph()
            headingParagraph.alignment = Element.ALIGN_CENTER // Center the heading

            // Create a Chunk for the heading text and set its font
            val headingText = Chunk("CleanCircle Report", headingFont)
            headingParagraph.add(headingText)

            // Add the heading to the document
            document.add(headingParagraph)

            // Create a LineSeparator
            val lineSeparator = LineSeparator()
            lineSeparator.lineColor = BaseColor.GREEN // Set the color of the line
            lineSeparator.lineWidth = 2f // Set the width of the line

            // Add the line separator below the heading
            document.add(Chunk(lineSeparator))

            // Add space between the line and report data
            document.add(Paragraph(" ")) // Empty paragraph for space

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
            paragraph.add("Full Name: $fullName\n\n")
            paragraph.add("Estimate Weight: $estimateWeight\n\n")
            paragraph.add("Report Category: $reportCategory\n\n")
            paragraph.add("Report Date: $reportDatePicker\n\n")
            paragraph.add("Remarks: $remarks\n")

            // Add the styled paragraph to the document
            document.add(paragraph)

            // Add a border to the entire content
            val contentByte = writer.directContent
            val rect = Rectangle(document.leftMargin(), document.bottomMargin(), document.right() - document.rightMargin(), document.top() - document.topMargin())
            rect.borderWidth = 4f
            rect.borderColor = BaseColor.BLACK
            contentByte.rectangle(rect)

            // Add a white background to the entire page
            val pageRect = Rectangle(document.pageSize)
            pageRect.backgroundColor = BaseColor.WHITE
            document.add(pageRect)

            document.close()

            // Offer the PDF for download using the code below
            offerPdfForDownload(pdfFile)

            activity?.runOnUiThread {
                Toast.makeText(
                    requireContext(),
                    "Downloading PDF to your phone...",
                    Toast.LENGTH_SHORT
                ).show()
            }

            val handler = Handler(Looper.getMainLooper())
            // After viewing the PDF, let's redirect to another activity after a 2-second delay
            handler.postDelayed({
                val redirectIntent = Intent(requireContext(), CitizenHomeActivity::class.java)
                startActivity(redirectIntent)
            }, 4000) // 3000 milliseconds (2 seconds) delay

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
            // Create an Intent to view the PDF file
            val intent = Intent(Intent.ACTION_VIEW)
            val uri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().applicationContext.packageName + ".provider",
                pdfFile
            )
            intent.setDataAndType(uri, "application/pdf")
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

            // Start an activity to view the PDF
            startActivity(intent)
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

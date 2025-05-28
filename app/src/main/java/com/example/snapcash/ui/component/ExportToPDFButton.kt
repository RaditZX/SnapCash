package com.example.snapcash.ui.component

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.content.FileProvider
import com.example.snapcash.data.Transaction
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.graphics.Color as Colors2

@Composable
fun ExportPdfButton(data: List<Transaction>, context: Context, periode: String, isPemasukan: Boolean, ) {
    // State to control dialog visibility
    val showDialog = remember { mutableStateOf(false) }

    // This will trigger the PDF export logic
    Button(modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(
        containerColor = Colors2(0xFF2D6CE9), contentColor = Colors2(0xFFFFFFFF)
    ),onClick = {
        // Trigger the PDF export
        showDialog.value = true
    }) {
        Text("Export PDF")
    }

    // Show the dialog when button is clicked
    if (showDialog.value) {
        ExportPdfDialog(data = data, context = context, onDismiss = { showDialog.value = false }, periode, isPemasukan)
    }
}

@Composable
fun ExportPdfDialog(data: List<Transaction>, context: Context, onDismiss: () -> Unit, periode: String, isPemasukan: Boolean) {
    // State for file name input
    val fileNameState = remember { mutableStateOf(TextFieldValue("")) }
    val defaultFileName = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val transactionType = if (data.any { it.isPengeluaran }) "Pengeluaran" else "Pemasukan"
    val initialFileName = "$defaultFileName _ $transactionType"

    fileNameState.value = TextFieldValue(initialFileName)

    // Dialog for file name input
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Masukkan Nama File") },
        text = {
            TextField(
                value = fileNameState.value,
                onValueChange = { fileNameState.value = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nama file") }
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    val userFileName = fileNameState.value.text.trim()
                    val finalFileName = if (userFileName.isNotEmpty()) userFileName else initialFileName

                    // Trigger PDF save outside the composable context
                    savePdf(context, finalFileName, data, periode, isPemasukan)
                    onDismiss() // Dismiss dialog after saving
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

fun savePdf(context: Context, fileName: String, data: List<Transaction>, periode: String, isPemasukan: Boolean) {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    var page = pdfDocument.startPage(pageInfo)
    var canvas = page.canvas

    val paint = Paint().apply {
        textSize = 12f
        color =  Color.BLACK
    }

    val boldPaint = Paint().apply {
        textSize = 14f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD) // <- ini benar
        color = Color.BLACK
    }



    var y = 50f

    // Judul Laporan
    y += 20f
    canvas.drawText("LAPORAN TRANSAKSI", 20f, y, boldPaint)
    y += 20f
    canvas.drawText("Periode: ${periode}", 20f, y, paint)
    y += 30f

    // Header Tabel
    canvas.drawText("No", 20f, y, boldPaint)
    canvas.drawText("Tanggal", 60f, y, boldPaint)
    canvas.drawText("Judul", 210f, y, boldPaint)
    canvas.drawText(if (isPemasukan) "Sumber" else "Toko", 340f, y, boldPaint)
    canvas.drawText("Jumlah", 450f, y, boldPaint)
    y += 15f
    canvas.drawLine(20f, y, 570f, y, paint)
    y += 15f

    var total = 0.0

    data.forEachIndexed { index, item ->
        if (y > 800) {
            pdfDocument.finishPage(page)
            val newPageInfo = PdfDocument.PageInfo.Builder(595, 842, pdfDocument.pages.size + 1).create()
            page = pdfDocument.startPage(newPageInfo)
            canvas = page.canvas
            y = 50f
        }

        val tipe = if (item.isPengeluaran) "Pengeluaran" else "Pemasukan"
        total += item.amount

        canvas.drawText("${index + 1}", 20f, y, paint)
        canvas.drawText(item.date, 60f, y, paint)
        canvas.drawText(item.title.take(20), 210f, y, paint)
        canvas.drawText(item.category.take(15), 340f, y, paint)
        canvas.drawText(formatCurrency(item.amount), 450f, y, paint)

        y += 20f
    }

    // Garis dan Total
    y += 10f
    canvas.drawLine(20f, y, 570f, y, paint)
    y += 20f
    canvas.drawText("Total", 380f, y, boldPaint)
    canvas.drawText(formatCurrency(total.toInt()), 450f, y, boldPaint) // ✅ BENAR

    pdfDocument.finishPage(page)

    // Simpan ke Downloads
    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val file = File(downloadsDir, "$fileName.pdf")

    try {
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()
        Toast.makeText(context, "PDF tersimpan: ${file.absolutePath}", Toast.LENGTH_LONG).show()

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Log.e("PDF_ERROR", "Gagal menyimpan file PDF", e)
        Toast.makeText(context, "Gagal menyimpan PDF", Toast.LENGTH_SHORT).show()
    }
}




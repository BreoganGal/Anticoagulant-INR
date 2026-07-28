package com.example.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.example.data.DoseEntry
import com.example.R
import java.io.OutputStream

object PdfGenerator {
    fun generatePdf(
        context: Context,
        outputStream: OutputStream,
        startDate: String,
        endDate: String,
        doses: List<DoseEntry>
    ) {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        var page = document.startPage(pageInfo)
        var canvas = page.canvas
        
        val paint = Paint()
        paint.color = Color.BLACK
        
        // Draw Logo
        val logoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.app_logo)
        if (logoBitmap != null) {
            val scaledLogo = Bitmap.createScaledBitmap(logoBitmap, 100, 100, false)
            canvas.drawBitmap(scaledLogo, 40f, 40f, null)
        }
        
        // Draw Header Text
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 24f
        canvas.drawText("Anticoagulant INR", 160f, 70f, paint)
        
        paint.textSize = 14f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("Registro de Tomas y Valores INR", 160f, 95f, paint)
        canvas.drawText("Periodo: $startDate a $endDate", 160f, 115f, paint)
        
        // Draw separator
        paint.color = Color.DKGRAY
        paint.strokeWidth = 2f
        canvas.drawLine(40f, 150f, 555f, 150f, paint)
        
        // Draw Table Header
        var currentY = 180f
        paint.color = Color.BLACK
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 12f
        canvas.drawText("Fecha", 40f, currentY, paint)
        canvas.drawText("Dosis", 150f, currentY, paint)
        canvas.drawText("Hora Prog.", 230f, currentY, paint)
        canvas.drawText("Estado", 330f, currentY, paint)
        canvas.drawText("INR", 450f, currentY, paint)
        
        currentY += 10f
        paint.strokeWidth = 1f
        canvas.drawLine(40f, currentY, 555f, currentY, paint)
        currentY += 20f
        
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        
        for (dose in doses) {
            if (currentY > 800f) {
                document.finishPage(page)
                page = document.startPage(pageInfo)
                canvas = page.canvas
                currentY = 50f
            }
            
            canvas.drawText(dose.date, 40f, currentY, paint)
            canvas.drawText(dose.prescribedFraction, 150f, currentY, paint)
            canvas.drawText(dose.scheduledTime, 230f, currentY, paint)
            
            val status = when (dose.punctuality) {
                "ON_TIME" -> "A tiempo"
                "DELAYED" -> "Retraso"
                "MISSED" -> "Falta"
                else -> "-"
            }
            canvas.drawText(status, 330f, currentY, paint)
            
            if (dose.inrValue != null) {
                canvas.drawText(dose.inrValue.toString(), 450f, currentY, paint)
            } else {
                canvas.drawText("-", 450f, currentY, paint)
            }
            
            currentY += 20f
        }
        
        document.finishPage(page)
        document.writeTo(outputStream)
        document.close()
    }
}

package com.example.mlkit

import android.graphics.Bitmap
import com.example.data.DoseEntry
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.util.regex.Pattern

data class ParsedPrescription(
    val nextInrDate: String? = null,
    val doses: List<ParsedDose> = emptyList()
)

data class ParsedDose(
    val date: String, // YYYY-MM-DD
    val dose: String
)

suspend fun analyzeImageWithMlKit(bitmap: Bitmap): ParsedPrescription {
    val image = InputImage.fromBitmap(bitmap, 0)
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    
    return try {
        val result = recognizer.process(image).await()
        val text = result.text
        
        parsePrescriptionText(text)
    } catch (e: Exception) {
        e.printStackTrace()
        ParsedPrescription()
    }
}

fun parsePrescriptionText(text: String): ParsedPrescription {
    val doses = mutableListOf<ParsedDose>()
    var nextInrDate: String? = null
    
    val datePattern = Pattern.compile("\\b(\\d{1,2})[/-](\\d{1,2})[/-](\\d{2,4})\\b")
    val matcher = datePattern.matcher(text)
    val foundDates = mutableListOf<LocalDate>()
    val currentYear = LocalDate.now().year
    
    while (matcher.find()) {
        try {
            val day = matcher.group(1)?.toIntOrNull() ?: 1
            val month = matcher.group(2)?.toIntOrNull() ?: 1
            var year = matcher.group(3)?.toIntOrNull() ?: currentYear
            if (year < 100) year += 2000 
            
            if (day in 1..31 && month in 1..12) {
                foundDates.add(LocalDate.of(year, month, day))
            }
        } catch (e: Exception) {}
    }
    
    if (foundDates.isNotEmpty()) {
        val maxDate = foundDates.maxOrNull()
        if (maxDate != null && maxDate.isAfter(LocalDate.now())) {
            nextInrDate = maxDate.toString()
        }
    }
    
    // Find doses: fractions or integers like 1/2, 1/4, 3/4, 1, 2
    val dosePattern = Pattern.compile("\\b(1/2|1/4|3/4|1|2|3)\\b")
    val doseMatcher = dosePattern.matcher(text)
    val today = LocalDate.now()
    var daysAdded = 0
    
    while (doseMatcher.find() && daysAdded < 28) {
        val frac = doseMatcher.group(1)
        if (frac != null) {
            doses.add(ParsedDose(
                date = today.plusDays(daysAdded.toLong() + 1).toString(),
                dose = frac
            ))
            daysAdded++
        }
    }
    
    // Fallback if no doses found (for demoing parsing success vs failure)
    // We will just let it be empty, and the UI can handle the fallback.
    
    return ParsedPrescription(nextInrDate, doses)
}

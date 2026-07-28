package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dose_entries")
data class DoseEntry(
    @PrimaryKey
    val date: String, // YYYY-MM-DD
    val prescribedFraction: String, // e.g., "3/4", "1/2", "1/4", "1", "1 1/4"
    val takenFraction: String? = null,
    val isTaken: Boolean = false,
    val takenTime: String? = null, // e.g. "19:32"
    val scheduledTime: String = "18:00", // e.g. "18:00"
    val inrValue: Float? = null, // e.g. 2.67f
    val punctuality: String = "PENDING", // "GREEN", "YELLOW", "RED", "MISSED", "PENDING"
    val notes: String? = null
)

@Entity(tableName = "inr_appointments")
data class InrAppointment(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String, // YYYY-MM-DD
    val title: String = "Revisión INR",
    val isDone: Boolean = false,
    val notes: String? = null
)

@Entity(tableName = "app_settings")
data class AppSettings(
    @PrimaryKey
    val id: Int = 1,
    val medicationName: String = "Sintrom", // Sintrom, Warfarina, Coumadin, Marevan, Marcoumar, Jantoven, Custom
    val customMedicationName: String = "",
    val targetInrMin: Float = 2.0f,
    val targetInrMax: Float = 3.5f,
    val reminderTime: String = "18:00",
    val notificationsEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val inrCheckFrequency: String = "AUTOMATIC", // "AUTOMATIC", "MONTHLY", "FORTNIGHTLY", "WEEKLY"
    val preferredDay: String = "Jueves",
    val language: String = "SYSTEM", // "SYSTEM", "ES", "GL", "CA", "EU", "FR", "EN", "DE", "IT"
    val dateFormat: String = "d/m/y", // "d/m/y", "DD/MM/YYYY", "YYYY-MM-DD", "MM/DD/YYYY"
    val exportPeriod: String = "Últimos 3 meses",
    val themeMode: String = "SYSTEM", // "SYSTEM", "LIGHT", "DARK"
    val termsAccepted: Boolean = false,
    val tourCompleted: Boolean = false
)

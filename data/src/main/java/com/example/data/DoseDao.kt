package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DoseDao {

    @Query("SELECT * FROM dose_entries ORDER BY date DESC")
    fun getAllDoses(): Flow<List<DoseEntry>>

    @Query("SELECT * FROM dose_entries WHERE date = :date LIMIT 1")
    suspend fun getDoseForDate(date: String): DoseEntry?

    @Query("SELECT * FROM dose_entries WHERE date <= :currentDate ORDER BY date DESC LIMIT 14")
    fun getRecent14Days(currentDate: String): Flow<List<DoseEntry>>

    @Query("SELECT * FROM dose_entries WHERE isTaken = 1 OR punctuality = 'MISSED' ORDER BY date DESC LIMIT 7")
    fun getRecent7Logs(): Flow<List<DoseEntry>>

    @Query("SELECT * FROM dose_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    suspend fun getDosesBetweenDates(startDate: String, endDate: String): List<DoseEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDose(entry: DoseEntry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoses(entries: List<DoseEntry>)

    @Update
    suspend fun updateDose(entry: DoseEntry)

    @androidx.room.Delete
    suspend fun deleteDose(entry: DoseEntry)

    // Appointments
    @Query("SELECT * FROM inr_appointments ORDER BY date ASC")
    fun getAllAppointments(): Flow<List<InrAppointment>>

    @Query("SELECT * FROM inr_appointments WHERE date >= :currentDate ORDER BY date ASC LIMIT 1")
    suspend fun getNextAppointment(currentDate: String): InrAppointment?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: InrAppointment)

    // App Settings
    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<AppSettings?>

    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsSync(): AppSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: AppSettings)
}

package com.example.data

import kotlinx.coroutines.flow.Flow

class SettingsRepository(private val dao: DoseDao) {

    val allDoses: Flow<List<DoseEntry>> = dao.getAllDoses()
    val allAppointments: Flow<List<InrAppointment>> = dao.getAllAppointments()
    val settings: Flow<AppSettings?> = dao.getSettings()

    fun getRecent14Days(currentDate: String): Flow<List<DoseEntry>> = dao.getRecent14Days(currentDate)
    fun getRecent7Logs(): Flow<List<DoseEntry>> = dao.getRecent7Logs()

    suspend fun insertDose(entry: DoseEntry) = dao.insertDose(entry)
    suspend fun insertDoses(entries: List<DoseEntry>) = dao.insertDoses(entries)
    suspend fun updateDose(entry: DoseEntry) = dao.updateDose(entry)
    suspend fun deleteDose(entry: DoseEntry) = dao.deleteDose(entry)

    suspend fun getDoseForDate(date: String): DoseEntry? = dao.getDoseForDate(date)
    suspend fun getDosesBetweenDates(start: String, end: String): List<DoseEntry> =
        dao.getDosesBetweenDates(start, end)

    suspend fun insertAppointment(appointment: InrAppointment) = dao.insertAppointment(appointment)
    suspend fun getNextAppointment(currentDate: String) = dao.getNextAppointment(currentDate)

    suspend fun saveSettings(settings: AppSettings) = dao.saveSettings(settings)
}

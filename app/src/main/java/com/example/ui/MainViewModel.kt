package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.AppSettings
import com.example.data.DoseEntry
import com.example.data.InrAppointment
import com.example.data.SampleData
import com.example.data.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SettingsRepository
    val todayDateStr: String
        get() = LocalDate.now().toString()

    val allDoses: StateFlow<List<DoseEntry>>
    val settings: StateFlow<AppSettings>
    val recent14Days: StateFlow<List<DoseEntry>>
    val recent7Logs: StateFlow<List<DoseEntry>>
    val appointments: StateFlow<List<InrAppointment>>

    init {
        val dao = AppDatabase.getDatabase(application).doseDao()
        repository = SettingsRepository(dao)

        allDoses = repository.allDoses.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        settings = repository.settings.combine(MutableStateFlow(AppSettings())) { saved, default ->
            saved ?: default
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettings()
        )

        recent14Days = allDoses.map { list ->
            list.filter { it.date <= todayDateStr }.sortedByDescending { it.date }.take(14)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        recent7Logs = allDoses.map { list ->
            list.filter { it.isTaken || it.punctuality == "MISSED" }.sortedByDescending { it.date }.take(7)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        appointments = repository.allAppointments.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        viewModelScope.launch {
            if (dao.getSettingsSync() == null) {
                dao.saveSettings(AppSettings())
            }

            // Run auto-missed dose check
            checkAndMarkAutoMissedDoses()
        }
    }

    fun getTodayDose(): DoseEntry {
        val found = allDoses.value.firstOrNull { it.date == todayDateStr }
        return found ?: DoseEntry(
            date = todayDateStr,
            prescribedFraction = "3/4",
            scheduledTime = settings.value.reminderTime
        )
    }

    fun getDaysUntilNextInr(): Int {
        val appts = appointments.value.filter { it.date >= todayDateStr }.sortedBy { it.date }
        if (appts.isNotEmpty()) {
            val apptDate = LocalDate.parse(appts.first().date)
            val today = LocalDate.now()
            val diff = ChronoUnit.DAYS.between(today, apptDate).toInt()
            return maxOf(0, diff)
        }
        // Fallback calculated from automatic check frequency or pauta end
        return 1
    }

    fun markTodayTaken(isTaken: Boolean = true) {
        viewModelScope.launch {
            val current = getTodayDose()
            val nowTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
            val updated = current.copy(
                isTaken = isTaken,
                takenFraction = if (isTaken) current.prescribedFraction else null,
                takenTime = if (isTaken) nowTime else null,
                punctuality = if (isTaken) calculatePunctuality(nowTime, current.scheduledTime) else "PENDING"
            )
            repository.insertDose(updated)
        }
    }

    fun deleteDose(dose: DoseEntry) {
        viewModelScope.launch {
            repository.deleteDose(dose)
        }
    }

    fun checkAndMarkAutoMissedDoses() {
        viewModelScope.launch {
            val now = LocalDate.now()
            val currentTime = LocalTime.now()
            val defaultReminder = settings.value.reminderTime

            val currentDoses = repository.getDosesBetweenDates("2020-01-01", now.toString())
            currentDoses.forEach { dose ->
                if (!dose.isTaken && dose.punctuality != "MISSED") {
                    val doseDate = try { LocalDate.parse(dose.date) } catch (e: Exception) { null }
                    if (doseDate != null) {
                        if (doseDate.isBefore(now)) {
                            // Past day untaken dose -> automatically mark as MISSED
                            val updated = dose.copy(punctuality = "MISSED")
                            repository.insertDose(updated)
                        } else if (doseDate == now) {
                            // Today's dose -> check if 4 hours past scheduled time
                            val schedTimeStr = dose.scheduledTime.ifBlank { defaultReminder }
                            val schedTime = try { LocalTime.parse(schedTimeStr) } catch (e: Exception) { LocalTime.of(18, 0) }
                            val cutoffTime = schedTime.plusHours(4)

                            val isPast4Hours = if (cutoffTime.isBefore(schedTime)) {
                                currentTime.isAfter(cutoffTime) && currentTime.isBefore(schedTime)
                            } else {
                                currentTime.isAfter(cutoffTime)
                            }

                            if (isPast4Hours) {
                                val updated = dose.copy(punctuality = "MISSED")
                                repository.insertDose(updated)
                            }
                        }
                    }
                }
            }
        }
    }

    fun updateDose(
        date: String,
        fraction: String,
        isTaken: Boolean,
        takenTime: String?,
        isMissed: Boolean
    ) {
        viewModelScope.launch {
            val existing = repository.getDoseForDate(date) ?: DoseEntry(
                date = date,
                prescribedFraction = fraction,
                scheduledTime = settings.value.reminderTime
            )

            val punctuality = when {
                isMissed -> "MISSED"
                isTaken -> calculatePunctuality(takenTime, existing.scheduledTime)
                else -> "PENDING"
            }

            val updated = existing.copy(
                prescribedFraction = fraction,
                takenFraction = if (isTaken) fraction else null,
                isTaken = isTaken && !isMissed,
                takenTime = if (isTaken) (takenTime ?: settings.value.reminderTime) else null,
                punctuality = punctuality
            )
            repository.insertDose(updated)
        }
    }

    fun updateInr(date: String, inrVal: Float?) {
        viewModelScope.launch {
            val existing = repository.getDoseForDate(date) ?: DoseEntry(
                date = date,
                prescribedFraction = "3/4",
                scheduledTime = settings.value.reminderTime
            )
            val updated = existing.copy(inrValue = inrVal)
            repository.insertDose(updated)

            // If INR test is recorded, ensure an appointment entry exists
            if (inrVal != null) {
                repository.insertAppointment(
                    InrAppointment(
                        date = date,
                        title = "Control INR: ${String.format("%.2f", inrVal)}",
                        isDone = true
                    )
                )
            }
        }
    }

    fun updateSettings(newSettings: AppSettings) {
        viewModelScope.launch {
            repository.saveSettings(newSettings)
        }
    }

    fun importPauta(doses: List<DoseEntry>, nextInrDate: String) {
        viewModelScope.launch {
            repository.insertDoses(doses)
            repository.insertAppointment(
                InrAppointment(
                    date = nextInrDate,
                    title = "Cita de revisión INR post pauta",
                    isDone = false
                )
            )
        }
    }

    fun exportDataToJson(): String {
        return try {
            val moshi = com.squareup.moshi.Moshi.Builder().add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory()).build()
            val type = com.squareup.moshi.Types.newParameterizedType(List::class.java, DoseEntry::class.java)
            val adapter = moshi.adapter<List<DoseEntry>>(type)
            adapter.toJson(allDoses.value)
        } catch (e: Exception) {
            "[]"
        }
    }

    fun importDataFromJson(jsonStr: String): Boolean {
        return try {
            val moshi = com.squareup.moshi.Moshi.Builder().add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory()).build()
            val type = com.squareup.moshi.Types.newParameterizedType(List::class.java, DoseEntry::class.java)
            val adapter = moshi.adapter<List<DoseEntry>>(type)
            val doses = adapter.fromJson(jsonStr)
            if (!doses.isNullOrEmpty()) {
                viewModelScope.launch {
                    val dao = AppDatabase.getDatabase(getApplication()).doseDao()
                    dao.insertDoses(doses)
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun calculatePunctuality(takenTime: String?, scheduledTime: String): String {
        if (takenTime.isNull_or_blank()) return "PENDING"
        return try {
            val taken = LocalTime.parse(takenTime)
            val scheduled = LocalTime.parse(scheduledTime)
            // If taken before scheduled time, it's green (on time)
            if (taken.isBefore(scheduled)) return "GREEN"
            
            val diffMinutes = java.time.temporal.ChronoUnit.MINUTES.between(scheduled, taken)
            when {
                diffMinutes <= 60 -> "GREEN"
                else -> "YELLOW" // Everything taken after 60 mins is a delay (YELLOW). Not "Falta" (RED) because it was taken.
            }
        } catch (e: Exception) {
            "GREEN"
        }
    }

    private fun String?.isNull_or_blank(): Boolean = this == null || this.isBlank()
}

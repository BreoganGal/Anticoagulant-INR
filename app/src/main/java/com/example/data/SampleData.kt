package com.example.data

import java.time.LocalDate

object SampleData {

    fun generateInitialData(): List<DoseEntry> {
        val today = LocalDate.now()
        val list = mutableListOf<DoseEntry>()

        // Generate past 30 days and next 10 days
        for (i in -30..10) {
            val date = today.plusDays(i.toLong())
            val dateStr = date.toString()

            val prescribedFraction = when ((Math.abs(date.dayOfMonth) % 4)) {
                0 -> "1/2"
                1 -> "3/4"
                2 -> "1"
                else -> "1/4"
            }

            if (i < 0) {
                // Past days: taken or missed
                val isMissed = (i == -3 || i == -11)
                val punctuality = if (isMissed) "MISSED" else if (i % 5 == 0) "YELLOW" else "GREEN"
                val takenTime = if (isMissed) null else if (punctuality == "YELLOW") "20:45" else "19:31"
                val inrVal = if (i == -1 || i == -14 || i == -28) 2.67f else if (i == -7) 3.1f else null

                list.add(
                    DoseEntry(
                        date = dateStr,
                        prescribedFraction = prescribedFraction,
                        takenFraction = if (isMissed) null else prescribedFraction,
                        isTaken = !isMissed,
                        takenTime = takenTime,
                        scheduledTime = "19:30",
                        inrValue = inrVal,
                        punctuality = punctuality
                    )
                )
            } else if (i == 0) {
                // Today: default scheduled dose
                list.add(
                    DoseEntry(
                        date = dateStr,
                        prescribedFraction = "3/4",
                        takenFraction = null,
                        isTaken = false,
                        takenTime = null,
                        scheduledTime = "19:30",
                        inrValue = null,
                        punctuality = "PENDING"
                    )
                )
            } else {
                // Future days pauta
                list.add(
                    DoseEntry(
                        date = dateStr,
                        prescribedFraction = prescribedFraction,
                        takenFraction = null,
                        isTaken = false,
                        takenTime = null,
                        scheduledTime = "19:30",
                        inrValue = null,
                        punctuality = "PENDING"
                    )
                )
            }
        }
        return list
    }

    fun generateInitialAppointment(): InrAppointment {
        val nextInrDate = LocalDate.now().plusDays(1).toString()
        return InrAppointment(
            date = nextInrDate,
            title = "Prueba de INR en centro médico",
            isDone = false
        )
    }
}

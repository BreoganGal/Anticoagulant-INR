import re

with open("app/src/main/java/com/example/ui/screens/InicioScreen.kt", "r") as f:
    content = f.read()

# We need to replace the CARD 1 content if todayDose is null.
card1_pattern = r'// CARD 1: Today\'s Intake.*?// CARD 2: Past 14 Days History Chart'
replacement = """// CARD 1: Today's Intake (Dosis de hoy)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1D5C58)),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                if (todayDose != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: Pill Fraction Illustration
                        PillFractionView(
                            fractionStr = todayDose.prescribedFraction,
                            size = 64.dp,
                            activeColor = Color(0xFF80CBC4),
                            inactiveColor = Color.White,
                            lineColor = Color(0xFF003731)
                        )

                        // Right: Dosage Number + Medication Name
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = todayDose.prescribedFraction,
                                fontSize = 38.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "${LanguageManager.getString("dosis_de_hoy", lang)} · ${settings.medicationName.uppercase()}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB2DFDB),
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Target intake time badge (non-editable, configured in Ajustes)
                    Surface(
                        color = Color(0x22FFFFFF),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Hora de la toma: ${settings.reminderTime}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Main Action Button: Register Intake
                    Button(
                        onClick = {
                            if (!todayDose.isTaken) {
                                onRegisterTodayTaken()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (todayDose.isTaken) Color(0xFF2E7D32) else Color.White
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        if (todayDose.isTaken) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = "✓ ${LanguageManager.getString("toma_registrada", lang)} (${todayDose.takenTime ?: "OK"})",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp
                            )
                        } else {
                            Text(
                                text = LanguageManager.getString("registrar_toma", lang),
                                color = Color(0xFF004D40),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp
                            )
                        }
                    }
                } else {
                    // No today dose
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = Color(0xFF80CBC4),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No hay dosis programada para hoy",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Añade tu pauta en la sección de Calendario.",
                            color = Color(0xFFB2DFDB),
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { /* Could navigate to Calendar or open Pauta Dialog, we'll just be passive for now, or use onNavigateToRegistro if it's the calendar? Actually just disable or navigate to Calendar */ },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f)),
                            enabled = false
                        ) {
                            Text("A LA ESPERA DE DATOS", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // CARD 2: Past 14 Days History Chart"""

content = re.sub(card1_pattern, replacement, content, flags=re.DOTALL)

with open("app/src/main/java/com/example/ui/screens/InicioScreen.kt", "w") as f:
    f.write(content)

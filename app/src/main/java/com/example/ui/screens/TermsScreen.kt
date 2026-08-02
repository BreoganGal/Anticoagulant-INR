package com.example.ui.screens

import androidx.compose.material3.MaterialTheme

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R


@Composable
fun TermsScreen(
    isFirstLaunch: Boolean,
    onAccept: () -> Unit,
    onClose: () -> Unit = {}
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
    ) {
        if (!isFirstLaunch) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.height(32.dp))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = com.example.core.common.R.drawable.app_logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Términos",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Anticoagulant INR",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(32.dp))

            TermsSection(
                title = "Avisos médicos legales",
                content = "ADVERTENCIA: Esta aplicación es solo para fines informativos y para ayudar en el seguimiento del INR. NO PUEDE reemplazar la atención médica profesional o la consulta con un médico.\n\n" +
                        "• No tomes ninguna decisión médica basándote únicamente en esta aplicación\n" +
                        "• Consulta siempre tus resultados y decisiones de tratamiento con un médico\n" +
                        "• En caso de emergencias médicas, contacta inmediatamente a un médico o a los servicios de emergencia\n" +
                        "• La aplicación no proporciona diagnósticos médicos ni recomendaciones terapéuticas\n" +
                        "• La precisión de los cálculos y análisis no está garantizada médicamente"
            )

            TermsSection(
                title = "Aviso legal",
                content = "Los creadores de la aplicación Anticoagulant INR no se hacen responsables de:\n\n" +
                        "• Daños resultantes del uso de la aplicación\n" +
                        "• Errores en los cálculos o análisis de datos\n" +
                        "• Decisiones médicas tomadas en base a la información de la aplicación\n" +
                        "• Pérdida de datos o problemas técnicos\n" +
                        "• Inexactitudes en las funciones de seguimiento\n\n" +
                        "El uso de la aplicación es bajo el propio riesgo del usuario."
            )

            TermsSection(
                title = "Política de privacidad",
                content = "• Todos los datos médicos se almacenan localmente en tu dispositivo\n" +
                        "• Los datos se pueden exportar en formatos PDF o JSON\n" +
                        "• No recopilamos datos personales con fines de marketing\n" +
                        "• Se pueden recopilar datos de uso anónimos para mejorar la aplicación"
            )

            TermsSection(
                title = "Consentimientos y aceptación",
                content = "Al usar la aplicación Anticoagulant INR, declaras que:\n\n" +
                        "• Has leído y entendido los avisos legales anteriores\n" +
                        "• Aceptas los términos de uso y la política de privacidad\n" +
                        "• Usarás la aplicación de acuerdo con su propósito previsto\n" +
                        "• No te basarás únicamente en la aplicación para tomar decisiones médicas\n" +
                        "• Eres consciente de las limitaciones y riesgos asociados con el uso de la aplicación"
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Text(
                    text = "Contacto",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Text(
                    text = "Si tienes alguna pregunta sobre la aplicación o la política de privacidad:\n",
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Correo electrónico: soporte@breogangal.eu",
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:soporte@breogangal.eu")
                        }
                        context.startActivity(intent)
                    }
                )
                Text(
                    text = "Repositorio: GitHub",
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/BreoganGal/Anticoagulant-INR"))
                        context.startActivity(intent)
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (isFirstLaunch) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onAccept,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "Acepto y continúo",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Para continuar, debes aceptar los términos anteriores",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun TermsSection(title: String, content: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Text(
            text = content,
            fontSize = 14.sp,
            lineHeight = 22.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

package com.erdem.shaman.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.draw.clip

@Composable
fun ResultScreen(navController: NavController, result: String) {
    val decoded = result.replace("+", " ")
    val lines = decoded.split("\n").filter { it.isNotBlank() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF1A2F5A), Color(0xFF243B6E))
                    )
                )
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🔍", fontSize = 36.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Analiz Sonucu",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Olası hastalıklar aşağıda listelendi",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 13.sp
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            lines.forEachIndexed { index, line ->
                val isWarning = line.contains("ÖNEMLİ") || line.contains("teşhis") || line.contains("doktor") || line.contains("Önemli")
                val isHeader = line.startsWith("#")

                when {
                    isWarning -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text("⚠️", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    line.replace("**", "").replace("*", "").replace("#", "").trim(),
                                    fontSize = 13.sp,
                                    color = Color(0xFF8B6914)
                                )
                            }
                        }
                    }
                    isHeader -> {
                        Text(
                            line.replace("#", "").trim(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A2F5A),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    line.matches(Regex("^\\d+\\..*")) -> {
                        val diseaseName = line.substringAfter(".").trim().substringBefore("-").trim()
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val encoded = java.net.URLEncoder.encode(diseaseName, "UTF-8")
                                    navController.navigate("disease/$encoded")
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(Color(0xFF1A2F5A), RoundedCornerShape(18.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        line.substringBefore("."),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    val parts = line.substringAfter(".").trim().split("-")
                                    Text(
                                        parts.firstOrNull()?.trim() ?: "",
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF1A2F5A),
                                        fontSize = 15.sp
                                    )
                                    if (parts.size > 1) {
                                        val percentage = parts.drop(1).joinToString("-").trim()
                                        val percentValue = percentage.replace("%", "").trim().toIntOrNull() ?: 0
                                        val barColor = when {
                                            percentValue >= 60 -> Color(0xFFE53935) // Kırmızı - yüksek ihtimal
                                            percentValue >= 35 -> Color(0xFFFFA726) // Turuncu - orta ihtimal
                                            else -> Color(0xFF43A047) // Yeşil - düşük ihtimal
                                        }
                                        Text(
                                            "Eşleşme oranı: $percentage",
                                            color = barColor,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        LinearProgressIndicator(
                                            progress = { percentValue / 100f },
                                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                            color = barColor,
                                            trackColor = Color(0xFFE0E0E0)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    line.isNotBlank() -> {
                        Text(
                            line.replace("**", "").replace("*", "").trim(),
                            fontSize = 14.sp,
                            color = Color(0xFF5A6478),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text("🏥", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Bu analiz yalnızca bilgilendirme amaçlıdır. Kesin teşhis için mutlaka bir doktora başvurun.",
                        fontSize = 13.sp,
                        color = Color(0xFF2E7D32)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BFA5))
            ) {
                Text("Yeni Analiz", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
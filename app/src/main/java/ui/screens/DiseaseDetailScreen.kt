package com.erdem.shaman.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.erdem.shaman.network.ClaudeRepository
import kotlinx.coroutines.launch

@Composable
fun DiseaseDetailScreen(navController: NavController, diseaseName: String) {
    val scope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(true) }
    var detail by remember { mutableStateOf("") }
    var prevalence by remember { mutableStateOf(0f) }
    var matchRate by remember { mutableStateOf(0f) }

    LaunchedEffect(diseaseName) {
        scope.launch {
            try {
                val result = ClaudeRepository.getDisaseDetail(diseaseName)
                detail = result.detail
                prevalence = result.prevalence
                matchRate = result.matchRate
            } catch (e: Exception) {
                detail = "Bilgi alınamadı: ${e.message}"
            } finally {
                loading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF1A2F5A), Color(0xFF243B6E))
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                TextButton(onClick = { navController.popBackStack() }) {
                    Text("← Geri", color = Color.White.copy(alpha = 0.7f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    diseaseName,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Hastalık Detayları",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 13.sp
                )
            }
        }

        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color(0xFF00BFA5))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Bilgiler yükleniyor...", color = Color.Gray)
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // İstatistik kartları
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Yaygınlık kartı
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Yaygınlık", fontSize = 12.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(
                                    progress = { prevalence / 100f },
                                    modifier = Modifier.size(80.dp),
                                    color = Color(0xFF00BFA5),
                                    trackColor = Color(0xFFE0F7F4),
                                    strokeWidth = 8.dp
                                )
                                Text(
                                    "${prevalence.toInt()}%",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF1A2F5A)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Toplumda görülme oranı",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Eşleşme kartı
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Semptom Eşleşmesi", fontSize = 12.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(
                                    progress = { matchRate / 100f },
                                    modifier = Modifier.size(80.dp),
                                    color = Color(0xFF1A2F5A),
                                    trackColor = Color(0xFFE8EBF5),
                                    strokeWidth = 8.dp
                                )
                                Text(
                                    "${matchRate.toInt()}%",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF1A2F5A)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Bu semptomlarda eşleşme",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Detay kartı
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        detail.split("\n").filter { it.isNotBlank() }.forEach { line ->
                            when {
                                line.startsWith("##") -> {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        line.replace("##", "").replace("#", "").trim(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color(0xFF1A2F5A)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Divider(color = Color(0xFFE0E0E0))
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                                line.startsWith("-") || line.startsWith("•") -> {
                                    Row(modifier = Modifier.padding(vertical = 2.dp)) {
                                        Text("•", color = Color(0xFF00BFA5), fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            line.removePrefix("-").removePrefix("•").trim(),
                                            fontSize = 14.sp,
                                            color = Color(0xFF5A6478)
                                        )
                                    }
                                }
                                else -> {
                                    Text(
                                        line.replace("**", "").trim(),
                                        fontSize = 14.sp,
                                        color = Color(0xFF5A6478),
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Uyarı kartı
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
                ) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Text("⚠️", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Bu bilgiler yalnızca genel bilgilendirme amaçlıdır. Kesin teşhis için doktora başvurun.",
                            fontSize = 13.sp,
                            color = Color(0xFF8B6914)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

data class HistoryItem(
    val result: String = "",
    val symptoms: String = "",
    val date: com.google.firebase.Timestamp? = null
)

@Composable
fun ProfileScreen(navController: NavController) {
    val user = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()
    var fullName by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var historyList by remember { mutableStateOf<List<HistoryItem>>(emptyList()) }

    LaunchedEffect(Unit) {
        user?.let {
            db.collection("users").document(it.uid).get()
                .addOnSuccessListener { doc ->
                    fullName = doc.getString("fullName") ?: ""
                    age = doc.getLong("age")?.toString() ?: ""
                    gender = doc.getString("gender") ?: ""
                }
            db.collection("users").document(it.uid)
                .collection("history")
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener { snap ->
                    historyList = snap.documents.map { doc ->
                        HistoryItem(
                            result = doc.getString("result") ?: "",
                            symptoms = doc.getString("symptoms") ?: "",
                            date = doc.getTimestamp("date")
                        )
                    }
                }
        }
    }

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
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color(0xFF00BFA5), RoundedCornerShape(40.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = fullName.firstOrNull()?.toString() ?: "?",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(fullName, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(user?.email ?: "", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Profil bilgileri
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Profil Bilgileri", fontWeight = FontWeight.Bold, color = Color(0xFF1A2F5A), fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    ProfileRow("Ad Soyad", fullName)
                    ProfileRow("Yaş", age)
                    ProfileRow("Cinsiyet", gender)
                    ProfileRow("E-posta", user?.email ?: "")
                }
            }

            // Geçmiş analizler
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Geçmiş Analizler", fontWeight = FontWeight.Bold, color = Color(0xFF1A2F5A), fontSize = 16.sp)
                        Text("${historyList.size} analiz", color = Color(0xFF00BFA5), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    if (historyList.isEmpty()) {
                        Text("Henüz analiz yapılmadı.", color = Color.Gray, fontSize = 14.sp)
                    } else {
                        historyList.forEach { item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F7FA))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    // Tarih
                                    item.date?.let {
                                        val sdf = java.text.SimpleDateFormat("dd MMM yyyy, HH:mm", java.util.Locale("tr"))
                                        Text(
                                            sdf.format(it.toDate()),
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                    }
                                    // Sonuç - ilk satırı göster
                                    val firstLine = item.result.lines().firstOrNull { it.isNotBlank() } ?: ""
                                    Text(
                                        firstLine.replace("+", " ").take(80),
                                        fontSize = 13.sp,
                                        color = Color(0xFF1A2F5A),
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    // Semptom özeti
                                    val symptomSummary = item.symptoms
                                        .substringAfter("Semptomlar:")
                                        .substringBefore("\n")
                                        .trim()
                                        .take(60)
                                    if (symptomSummary.isNotBlank()) {
                                        Text(
                                            "🔍 $symptomSummary",
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("login") { popUpTo(0) { inclusive = true } }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
            ) {
                Text("Çıkış Yap", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun ProfileRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(value, color = Color(0xFF1A2F5A), fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
    HorizontalDivider(color = Color(0xFFF0F0F0))
}
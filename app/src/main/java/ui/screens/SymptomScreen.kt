package com.erdem.shaman.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.erdem.shaman.network.ClaudeRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.net.URLEncoder

val symptomList = listOf(
    "Ateş", "Baş ağrısı", "Öksürük", "Boğaz ağrısı", "Burun akıntısı",
    "Nefes darlığı", "Göğüs ağrısı", "Karın ağrısı", "Bulantı", "Kusma",
    "İshal", "Kabızlık", "Baş dönmesi", "Yorgunluk", "İştah kaybı",
    "Kilo kaybı", "Gece terlemesi", "Eklem ağrısı", "Kas ağrısı", "Sırt ağrısı",
    "Boyun ağrısı", "Çarpıntı", "Şişlik", "Kaşıntı", "Döküntü",
    "Sarılık", "İdrar yaparken yanma", "Sık idrara çıkma", "Görme bozukluğu", "Kulak ağrısı"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymptomScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    var age by remember { mutableStateOf(25) }
    var gender by remember { mutableStateOf("Erkek") }
    var genderExpanded by remember { mutableStateOf(false) }
    val selectedSymptoms = remember { mutableStateListOf<String>() }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA)),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1A2F5A), RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        "Nasıl hissediyorsunuz?",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Bilgilerinizi ve semptomlarınızı seçin",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    TextButton(
                        onClick = {
                            FirebaseAuth.getInstance().signOut()
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF00BFA5))
                    ) {
                        Text("Hesap Değiştir", fontSize = 13.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Yaş: $age",
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1A2F5A)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Slider(
                        value = age.toFloat(),
                        onValueChange = { age = it.toInt() },
                        valueRange = 1f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF00BFA5),
                            activeTrackColor = Color(0xFF00BFA5)
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("1", fontSize = 12.sp, color = Color.Gray)
                        Text("100", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Cinsiyet",
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1A2F5A)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ExposedDropdownMenuBox(
                        expanded = genderExpanded,
                        onExpandedChange = { genderExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = gender,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF00BFA5),
                                unfocusedBorderColor = Color(0xFFE0E0E0)
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = genderExpanded,
                            onDismissRequest = { genderExpanded = false }
                        ) {
                            listOf("Erkek", "Kadın", "Diğer").forEach {
                                DropdownMenuItem(
                                    text = { Text(it) },
                                    onClick = { gender = it; genderExpanded = false }
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Semptomlar ${if (selectedSymptoms.isNotEmpty()) "(${selectedSymptoms.size} seçili)" else ""}",
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1A2F5A)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    symptomList.chunked(2).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            row.forEach { symptom ->
                                val selected = selectedSymptoms.contains(symptom)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(vertical = 4.dp)
                                        .background(
                                            if (selected) Color(0xFF00BFA5) else Color(0xFFF5F7FA),
                                            RoundedCornerShape(20.dp)
                                        )
                                        .border(
                                            1.dp,
                                            if (selected) Color(0xFF00BFA5) else Color(0xFFE0E0E0),
                                            RoundedCornerShape(20.dp)
                                        )
                                        .clickable {
                                            if (selected) selectedSymptoms.remove(symptom)
                                            else selectedSymptoms.add(symptom)
                                        }
                                        .padding(horizontal = 8.dp, vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        symptom,
                                        fontSize = 13.sp,
                                        color = if (selected) Color.White else Color(0xFF1A2F5A),
                                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                }
                            }
                            if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            if (error.isNotEmpty()) {
                Text(error, color = Color.Red, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
            }
            Button(
                onClick = {
                    if (selectedSymptoms.isEmpty()) {
                        error = "Lütfen en az bir semptom seçin"
                        return@Button
                    }
                    loading = true
                    error = ""
                    scope.launch {
                        try {
                            val result = ClaudeRepository.analyze(
                                age.toString(),
                                gender,
                                selectedSymptoms.joinToString(", ")
                            )
                            val encoded = java.net.URLEncoder.encode(result, "UTF-8").replace("+", "%20")
                            navController.navigate("result/$encoded")
                        } catch (e: Exception) {
                            error = "Analiz hatası: ${e.message}"
                        } finally {
                            loading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A2F5A)),
                enabled = !loading
            ) {
                if (loading) CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                else Text("Analiz Et", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
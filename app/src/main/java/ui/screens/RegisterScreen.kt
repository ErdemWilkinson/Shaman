package com.erdem.shaman.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var age by remember { mutableStateOf(25) }
    var gender by remember { mutableStateOf("Erkek") }
    var genderExpanded by remember { mutableStateOf(false) }
    var profileFor by remember { mutableStateOf("Kendim") }
    var profileForExpanded by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    val profileOptions = listOf("Kendim", "Eşim", "Oğlum", "Kızım", "Annem", "Babam", "Diğer")
    val genderOptions = listOf("Erkek", "Kadın")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF1A2F5A), Color(0xFF243B6E))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🩺", fontSize = 36.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Hesap Oluştur", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Ad Soyad
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Ad Soyad", fontWeight = FontWeight.SemiBold, color = Color(0xFF1A2F5A), fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            placeholder = { Text("Adınız ve soyadınız") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF00BFA5),
                                unfocusedBorderColor = Color(0xFFE0E0E0)
                            )
                        )
                    }
                }

                // E-posta
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("E-posta", fontWeight = FontWeight.SemiBold, color = Color(0xFF1A2F5A), fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = { Text("ornek@mail.com") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF00BFA5),
                                unfocusedBorderColor = Color(0xFFE0E0E0)
                            )
                        )
                    }
                }

                // Şifre
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Şifre", fontWeight = FontWeight.SemiBold, color = Color(0xFF1A2F5A), fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            placeholder = { Text("En az 6 karakter") },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF00BFA5),
                                unfocusedBorderColor = Color(0xFFE0E0E0)
                            )
                        )
                    }
                }

                // Bu profil kim için?
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Bu profil kim için?", fontWeight = FontWeight.SemiBold, color = Color(0xFF1A2F5A), fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Aile üyeleri için ayrı profil oluşturabilirsiniz", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        ExposedDropdownMenuBox(
                            expanded = profileForExpanded,
                            onExpandedChange = { profileForExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = profileFor,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF00BFA5),
                                    unfocusedBorderColor = Color(0xFFE0E0E0)
                                )
                            )
                            ExposedDropdownMenu(expanded = profileForExpanded, onDismissRequest = { profileForExpanded = false }) {
                                profileOptions.forEach {
                                    DropdownMenuItem(text = { Text(it) }, onClick = { profileFor = it; profileForExpanded = false })
                                }
                            }
                        }
                    }
                }

                // Cinsiyet
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Cinsiyet", fontWeight = FontWeight.SemiBold, color = Color(0xFF1A2F5A), fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            genderOptions.forEach { option ->
                                val selected = gender == option
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(
                                            if (selected) Color(0xFF00BFA5) else Color(0xFFF5F7FA),
                                            RoundedCornerShape(10.dp)
                                        )
                                        .border(1.dp, if (selected) Color(0xFF00BFA5) else Color(0xFFE0E0E0), RoundedCornerShape(10.dp))
                                        .clickable { gender = option }
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(option, color = if (selected) Color.White else Color(0xFF1A2F5A), fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }

                // Yaş
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Yaş: $age", fontWeight = FontWeight.SemiBold, color = Color(0xFF1A2F5A), fontSize = 14.sp)
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
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("1", fontSize = 12.sp, color = Color.Gray)
                            Text("100", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }

                if (error.isNotEmpty()) {
                    Text(error, color = Color.Red, fontSize = 13.sp)
                }

                Button(
                    onClick = {
                        if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
                            error = "Lütfen tüm alanları doldurun"
                            return@Button
                        }
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnSuccessListener { result ->
                                val user = result.user!!
                                user.updateProfile(
                                    UserProfileChangeRequest.Builder()
                                        .setDisplayName(fullName)
                                        .build()
                                )
                                db.collection("users").document(user.uid).set(
                                    mapOf(
                                        "fullName" to fullName,
                                        "email" to email,
                                        "age" to age,
                                        "gender" to gender,
                                        "profileFor" to profileFor
                                    )
                                )
                                navController.navigate("symptom") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                            .addOnFailureListener { error = it.message ?: "Hata oluştu" }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BFA5))
                ) {
                    Text("Kayıt Ol", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }

                TextButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Zaten hesabın var mı? Giriş yap", color = Color(0xFF1A2F5A), fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
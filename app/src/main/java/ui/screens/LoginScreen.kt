package com.erdem.shaman.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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

val NavyBlue = Color(0xFF1A2F5A)
val Teal = Color(0xFF00BFA5)

@Composable
fun LoginScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    var resetSent by remember { mutableStateOf(false) }

    // Zaten giriş yapmışsa direkt geç
    LaunchedEffect(Unit) {
        if (auth.currentUser != null) {
            navController.navigate("symptom") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(NavyBlue, Color(0xFF243B6E))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🩺", fontSize = 56.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Shaman", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Text("Sağlık rehberiniz", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("E-posta") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Teal,
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Şifre") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Teal,
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            colors = CheckboxDefaults.colors(checkedColor = Teal)
                        )
                        Text("Beni hatırla", fontSize = 13.sp, color = Color.Gray)
                    }
                    TextButton(onClick = {
                        if (email.isNotBlank()) {
                            auth.sendPasswordResetEmail(email)
                                .addOnSuccessListener { resetSent = true }
                                .addOnFailureListener { error = "Mail gönderilemedi" }
                        } else {
                            error = "Lütfen e-posta adresinizi girin"
                        }
                    }) {
                        Text("Şifremi unuttum", color = Teal, fontSize = 13.sp)
                    }
                }
                if (resetSent) {
                    Text("Şifre sıfırlama maili gönderildi!", color = Teal, fontSize = 13.sp)
                }
                if (error.isNotEmpty()) {
                    Text(error, color = Color.Red, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener {
                                navController.navigate("symptom") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                            .addOnFailureListener { error = it.message ?: "Hata oluştu" }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Teal)
                ) {
                    Text("Giriş Yap", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(onClick = { navController.navigate("register") }) {
                    Text("Hesabın yok mu? Kayıt ol", color = NavyBlue, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
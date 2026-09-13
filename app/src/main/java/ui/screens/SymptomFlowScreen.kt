package com.erdem.shaman.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.erdem.shaman.network.ClaudeRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.net.URLEncoder

val bodyRegionSymptoms = mapOf(
    "Baş / Boyun" to listOf(
        "Baş ağrısı", "Baş dönmesi", "Boyun ağrısı", "Kulak ağrısı",
        "Kulak çınlaması", "Görme bozukluğu", "Göz kızarıklığı", "Burun akıntısı",
        "Burun tıkanıklığı", "Boğaz ağrısı", "Yutma güçlüğü", "Çene ağrısı",
        "Saç dökülmesi", "Yüz şişliği", "Ağız yaraları"
    ),
    "Göğüs / Solunum" to listOf(
        "Göğüs ağrısı", "Nefes darlığı", "Öksürük", "Balgam",
        "Hırıltılı solunum", "Çarpıntı", "Göğüste sıkışma hissi",
        "Kan tükürme", "Gece terlemesi", "Nefes alırken ağrı"
    ),
    "Karın / Sindirim" to listOf(
        "Karın ağrısı", "Bulantı", "Kusma", "İshal",
        "Kabızlık", "Şişkinlik", "Gaz", "İştah kaybı",
        "Sarılık", "Kanlı dışkı", "Mide yanması", "Yutma güçlüğü",
        "Kilo kaybı", "Karın şişliği"
    ),
    "Sırt / Bel" to listOf(
        "Sırt ağrısı", "Bel ağrısı", "Belde sertlik",
        "Bacağa vuran ağrı", "Uyuşma", "Karıncalanma",
        "Hareket kısıtlılığı", "Kas spazmı"
    ),
    "Kollar / Bacaklar" to listOf(
        "Eklem ağrısı", "Kas ağrısı", "Şişlik", "Kızarıklık",
        "Uyuşma", "Karıncalanma", "Güçsüzlük", "Kramp",
        "Damar görünümü", "Yürüme güçlüğü", "El titremesi"
    ),
    "İdrar / Üreme" to listOf(
        "İdrar yaparken yanma", "Sık idrara çıkma", "Kanlı idrar",
        "İdrar rengi değişimi", "Kasık ağrısı", "Testis ağrısı",
        "Adet düzensizliği", "Vajinal akıntı", "Cinsel isteksizlik"
    ),
    "Cilt" to listOf(
        "Döküntü", "Kaşıntı", "Sivilce", "Kuruluk",
        "Sarılık", "Morarma", "Yara iyileşmemesi", "Ben değişimi",
        "Soyulma", "Şişlik", "Isı değişimi"
    ),
    "Genel / Tüm Vücut" to listOf(
        "Ateş", "Yorgunluk", "Halsizlik", "Kilo kaybı",
        "Kilo artışı", "Gece terlemesi", "Titreme", "İştah kaybı",
        "Uyku bozukluğu", "Konsantrasyon güçlüğü", "Depresif his"
    )
)

@Composable
fun SymptomFlowScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    var currentStep by remember { mutableStateOf(0) }
    var loading by remember { mutableStateOf(false) }
    var selectedBodyRegion by remember { mutableStateOf("") }
    val selectedSymptoms = remember { mutableStateListOf<String>() }
    var customSymptom by remember { mutableStateOf("") }
    var showCustomInput by remember { mutableStateOf(false) }
    var age by remember { mutableStateOf(25) }
    var gender by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var severity by remember { mutableStateOf("") }
    val familyHistory = remember { mutableStateListOf<String>() }
    val lifestyleHistory = remember { mutableStateListOf<String>() }
    var error by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.uid)
                .get()
                .addOnSuccessListener { doc ->
                    age = (doc.getLong("age") ?: 25).toInt()
                    gender = doc.getString("gender") ?: "Erkek"
                }
        }
    }

    val progress = (currentStep + 1).toFloat() / 6f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1A2F5A))
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentStep > 0) {
                        TextButton(onClick = { currentStep-- }) {
                            Text("← Geri", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                        }
                    } else {
                        Spacer(modifier = Modifier.width(60.dp))
                    }
                    Text("${currentStep + 1} / 6", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                    TextButton(onClick = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate("login") { popUpTo(0) { inclusive = true } }
                    }) {
                        Text("Çıkış", color = Color(0xFF00BFA5), fontSize = 13.sp)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                    color = Color(0xFF00BFA5),
                    trackColor = Color.White.copy(alpha = 0.2f)
                )
            }
        }

        AnimatedContent(
            targetState = currentStep,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
                } else {
                    slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
                }
            }
        ) { step ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                when (step) {

                    // ADIM 1 - Vücut Bölgesi
                    0 -> {
                        Text("🫀", fontSize = 48.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Şikayetiniz nerede?", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A2F5A), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                        Text("En çok rahatsızlık hissettiğiniz bölgeyi seçin", fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(24.dp))

                        bodyRegionSymptoms.keys.chunked(2).forEach { row ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                row.forEach { region ->
                                    val selected = selectedBodyRegion == region
                                    val emoji = when (region) {
                                        "Baş / Boyun" -> "🧠"
                                        "Göğüs / Solunum" -> "🫁"
                                        "Karın / Sindirim" -> "🫃"
                                        "Sırt / Bel" -> "🦴"
                                        "Kollar / Bacaklar" -> "🦵"
                                        "İdrar / Üreme" -> "🔬"
                                        "Cilt" -> "🩹"
                                        else -> "🌡️"
                                    }
                                    Box(
                                        modifier = Modifier.weight(1f).padding(vertical = 4.dp)
                                            .background(if (selected) Color(0xFF1A2F5A) else Color.White, RoundedCornerShape(12.dp))
                                            .border(1.5.dp, if (selected) Color(0xFF1A2F5A) else Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                                            .clickable {
                                                selectedBodyRegion = region
                                                selectedSymptoms.clear()
                                            }
                                            .padding(12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(emoji, fontSize = 24.sp)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(region, fontSize = 12.sp, color = if (selected) Color.White else Color(0xFF1A2F5A), fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal, textAlign = TextAlign.Center)
                                        }
                                    }
                                }
                                if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { if (selectedBodyRegion.isNotEmpty()) currentStep++ else error = "Lütfen bir bölge seçin" },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A2F5A)),
                            enabled = selectedBodyRegion.isNotEmpty()
                        ) {
                            Text("Devam Et", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                        }
                    }

                    // ADIM 2 - Semptomlar
                    1 -> {
                        Text("🔍", fontSize = 48.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Hangi semptomlar var?", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A2F5A), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                        Text("Birden fazla seçebilirsiniz ${if (selectedSymptoms.isNotEmpty()) "(${selectedSymptoms.size} seçili)" else ""}", fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(24.dp))

                        val symptoms = bodyRegionSymptoms[selectedBodyRegion] ?: emptyList()
                        symptoms.chunked(2).forEach { row ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                row.forEach { symptom ->
                                    val selected = selectedSymptoms.contains(symptom)
                                    Box(
                                        modifier = Modifier.weight(1f).padding(vertical = 4.dp)
                                            .background(if (selected) Color(0xFF00BFA5) else Color.White, RoundedCornerShape(12.dp))
                                            .border(1.5.dp, if (selected) Color(0xFF00BFA5) else Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                                            .clickable { if (selected) selectedSymptoms.remove(symptom) else selectedSymptoms.add(symptom) }
                                            .padding(12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(symptom, fontSize = 13.sp, color = if (selected) Color.White else Color(0xFF1A2F5A), fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal, textAlign = TextAlign.Center)
                                    }
                                }
                                if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val customSymptoms = selectedSymptoms.filter { !symptoms.contains(it) }
                        if (customSymptoms.isNotEmpty()) {
                            Text("Eklenen özel semptomlar:", fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(4.dp))
                            customSymptoms.forEach { custom ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                                        .background(Color(0xFF1A2F5A), RoundedCornerShape(20.dp))
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(custom, color = Color.White, fontSize = 13.sp)
                                    Text("✕", color = Color.White.copy(alpha = 0.7f), modifier = Modifier.clickable { selectedSymptoms.remove(custom) })
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        if (!showCustomInput) {
                            OutlinedButton(
                                onClick = { showCustomInput = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1A2F5A))
                            ) {
                                Text("+ Semptomumu listede bulamıyorum")
                            }
                        } else {
                            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Semptomunuzu tanımlayın", fontWeight = FontWeight.SemiBold, color = Color(0xFF1A2F5A))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = customSymptom,
                                        onValueChange = { customSymptom = it },
                                        placeholder = { Text("Örn: Sabahları el sertliği...") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF00BFA5), unfocusedBorderColor = Color(0xFFE0E0E0))
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = {
                                            if (customSymptom.isNotBlank()) {
                                                selectedSymptoms.add(customSymptom)
                                                customSymptom = ""
                                                showCustomInput = false
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BFA5))
                                    ) {
                                        Text("Ekle")
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { if (selectedSymptoms.isNotEmpty()) currentStep++ else error = "Lütfen en az bir semptom seçin" },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A2F5A)),
                            enabled = selectedSymptoms.isNotEmpty()
                        ) {
                            Text("Devam Et", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                        }
                    }

                    // ADIM 3 - Ne zamandır
                    2 -> {
                        Text("🕐", fontSize = 48.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Şikayetiniz ne zaman başladı?", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A2F5A), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(24.dp))

                        listOf("Bugün başladı", "2-3 gün önce", "1 haftadır", "1 aydan fazla", "Uzun süredir var").forEach { option ->
                            val selected = duration == option
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                    .background(if (selected) Color(0xFF00BFA5) else Color.White, RoundedCornerShape(12.dp))
                                    .border(1.5.dp, if (selected) Color(0xFF00BFA5) else Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                                    .clickable { duration = option; currentStep++ }
                                    .padding(16.dp)
                            ) {
                                Text(option, color = if (selected) Color.White else Color(0xFF1A2F5A), fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
                            }
                        }
                    }

                    // ADIM 4 - Şiddet
                    3 -> {
                        Text("📊", fontSize = 48.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Şikayetinizin şiddeti?", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A2F5A), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                        Text("1 = çok hafif, 10 = dayanılmaz", fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(24.dp))

                        listOf(
                            "Hafif (1-3)" to "😊",
                            "Orta (4-6)" to "😐",
                            "Şiddetli (7-8)" to "😣",
                            "Çok şiddetli (9-10)" to "😱"
                        ).forEach { (option, emoji) ->
                            val selected = severity == option
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                    .background(if (selected) Color(0xFF1A2F5A) else Color.White, RoundedCornerShape(12.dp))
                                    .border(1.5.dp, if (selected) Color(0xFF1A2F5A) else Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                                    .clickable { severity = option; currentStep++ }
                                    .padding(16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(emoji, fontSize = 24.sp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(option, color = if (selected) Color.White else Color(0xFF1A2F5A), fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
                                }
                            }
                        }
                    }

                    // ADIM 5 - Aile geçmişi
                    4 -> {
                        Text("🧬", fontSize = 48.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Aile geçmişiniz", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A2F5A), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                        Text("Birden fazla seçebilirsiniz", fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(24.dp))

                        listOf("Diyabet", "Kalp hastalığı", "Kanser", "Hipertansiyon", "Astım", "Yok / Bilmiyorum").chunked(2).forEach { row ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                row.forEach { option ->
                                    val selected = familyHistory.contains(option)
                                    Box(
                                        modifier = Modifier.weight(1f).padding(vertical = 4.dp)
                                            .background(if (selected) Color(0xFF00BFA5) else Color.White, RoundedCornerShape(12.dp))
                                            .border(1.5.dp, if (selected) Color(0xFF00BFA5) else Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                                            .clickable {
                                                if (option == "Yok / Bilmiyorum") {
                                                    familyHistory.clear()
                                                    familyHistory.add(option)
                                                } else {
                                                    familyHistory.remove("Yok / Bilmiyorum")
                                                    if (selected) familyHistory.remove(option) else familyHistory.add(option)
                                                }
                                            }
                                            .padding(12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(option, fontSize = 13.sp, color = if (selected) Color.White else Color(0xFF1A2F5A), fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal, textAlign = TextAlign.Center)
                                    }
                                }
                                if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { currentStep++ },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A2F5A))
                        ) {
                            Text("Devam Et", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                        }
                    }

                    // ADIM 6 - Yaşam tarzı + Analiz
                    5 -> {
                        Text("🚬", fontSize = 48.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Yaşam tarzınız", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A2F5A), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                        Text("Birden fazla seçebilirsiniz", fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(24.dp))

                        listOf(
                            "Sigara kullanıyorum",
                            "Alkol kullanıyorum",
                            "Uyuşturucu kullanıyorum",
                            "Düzenli egzersiz yapıyorum",
                            "Hiçbiri"
                        ).forEach { option ->
                            val selected = lifestyleHistory.contains(option)
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                    .background(if (selected) Color(0xFF00BFA5) else Color.White, RoundedCornerShape(12.dp))
                                    .border(1.5.dp, if (selected) Color(0xFF00BFA5) else Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                                    .clickable {
                                        if (option == "Hiçbiri") {
                                            lifestyleHistory.clear()
                                            lifestyleHistory.add(option)
                                        } else {
                                            lifestyleHistory.remove("Hiçbiri")
                                            if (selected) lifestyleHistory.remove(option) else lifestyleHistory.add(option)
                                        }
                                    }
                                    .padding(16.dp)
                            ) {
                                Text(option, color = if (selected) Color.White else Color(0xFF1A2F5A), fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        if (error.isNotEmpty()) {
                            Text(error, color = Color.Red, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        Button(
                            onClick = {
                                loading = true
                                error = ""
                                scope.launch {
                                    try {
                                        val result = ClaudeRepository.analyze(
                                            age = age.toString(),
                                            gender = gender,
                                            symptoms = """
                                                Vücut bölgesi: $selectedBodyRegion
                                                Semptomlar: ${selectedSymptoms.joinToString(", ")}
                                                Ne zamandır: $duration
                                                Şiddet: $severity
                                                Aile geçmişi: ${if (familyHistory.isEmpty()) "Belirtilmedi" else familyHistory.joinToString(", ")}
                                                Yaşam tarzı: ${if (lifestyleHistory.isEmpty()) "Belirtilmedi" else lifestyleHistory.joinToString(", ")}
                                            """.trimIndent()
                                        )
                                        val encoded = URLEncoder.encode(result, "UTF-8").replace("+", "%20")
                                        navController.navigate("result/$encoded")
                                    } catch (e: Exception) {
                                        error = "Analiz hatası: ${e.message}"
                                    } finally {
                                        loading = false
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A2F5A)),
                            enabled = !loading
                        ) {
                            if (loading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                            else Text("Analiz Et 🔍", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}
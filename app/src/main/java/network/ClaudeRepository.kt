package com.erdem.shaman.network

import com.erdem.shaman.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

object ClaudeRepository {
    private val API_KEY = BuildConfig.CLAUDE_API_KEY
    private val client = OkHttpClient()
    data class DiseaseDetail(
        val detail: String,
        val prevalence: Float,
        val matchRate: Float
    )

    suspend fun getDisaseDetail(diseaseName: String): DiseaseDetail {
        return withContext(Dispatchers.IO) {
            val prompt = """
            "$diseaseName" hastalığı hakkında Türkçe olarak şu formatta bilgi ver:

            ## Hastalık Nedir?
            [2-3 cümle açıklama]

            ## Belirtiler
            - [belirti 1]
            - [belirti 2]
            - [belirti 3]

            ## Nasıl Bulaşır / Oluşur?
            [açıklama]

            ## Tedavi
            [açıklama]

            ## Ne Zaman Doktora Gidilmeli?
            [açıklama]

            Son satırda şu formatta yaz (sadece sayılar):
            ISTATISTIK:yaygınlık_yüzdesi:eşleşme_yüzdesi
            Örnek: ISTATISTIK:35:72
        """.trimIndent()


            val json = JSONObject().apply {
                put("model", "claude-haiku-4-5-20251001")
                put("max_tokens", 1024)
                put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", prompt)
                    })
                })
            }

            val body = json.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("https://api.anthropic.com/v1/messages")
                .addHeader("x-api-key", API_KEY)
                .addHeader("anthropic-version", "2023-06-01")
                .addHeader("content-type", "application/json")
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: return@withContext DiseaseDetail("Bilgi alınamadı", 0f, 0f)

            val text = JSONObject(responseBody)
                .getJSONArray("content")
                .getJSONObject(0)
                .getString("text")

            val statsLine = text.lines().lastOrNull { it.startsWith("ISTATISTIK:") }
            val parts = statsLine?.split(":")
            val prevalence = parts?.getOrNull(1)?.trim()?.toFloatOrNull() ?: 30f
            val matchRate = parts?.getOrNull(2)?.trim()?.toFloatOrNull() ?: 60f
            val detail = text.lines().filter { !it.startsWith("ISTATISTIK:") }.joinToString("\n")

            DiseaseDetail(detail, prevalence, matchRate)
        }
    }
    suspend fun analyze(age: String, gender: String, symptoms: String): String {
        return withContext(Dispatchers.IO) {
            val prompt = """
                Bir hastanın bilgileri:
                Yaş: $age
                Cinsiyet: $gender
                Şikayetler: $symptoms
                
               Sadece aşağıdaki formatta yanıt ver, başka hiçbir şey yazma:
1. Hastalık adı (halk arasında kullanılan Türkçe isim) - %XX ihtimal

Önemli: Latince veya bilimsel isim kullanma. Örneğin "Viral Gastroenterit" yerine "Mide gribi", "Rhinitis" yerine "Nezle", "Otitis Media" yerine "Kulak iltihabı" yaz.
                2. Hastalık adı - %XX ihtimal"89+++++8
                3. Hastalık adı - %XX ihtimal
                
                En fazla 8 hastalık listele. Açıklama veya uyarı yazma.
                Türkçe yanıt ver.
            """.trimIndent()

            val json = JSONObject().apply {
                put("model", "claude-haiku-4-5-20251001")
                put("max_tokens", 1024)
                put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", prompt)
                    })
                })
            }

            val body = json.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("https://api.anthropic.com/v1/messages")
                .addHeader("x-api-key", API_KEY)
                .addHeader("anthropic-version", "2023-06-01")
                .addHeader("content-type", "application/json")
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: return@withContext "Sonuç alınamadı"

            if (!response.isSuccessful) {
                return@withContext "Hata: ${response.code} - $responseBody"
            }

            val jsonResponse = JSONObject(responseBody)
            jsonResponse.getJSONArray("content")
                .getJSONObject(0)
                .getString("text")
                .replace("+", " ")
                .replace("**", "")
                .replace("* ", "")
                .trim()
                .also { result ->
                    try {
                        val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                        if (user != null) {
                            com.google.firebase.firestore.FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(user.uid)
                                .collection("history")
                                .add(mapOf(
                                    "symptoms" to symptoms,
                                    "age" to age,
                                    "gender" to gender,
                                    "result" to result,
                                    "date" to com.google.firebase.Timestamp.now()
                                ))
                                .addOnSuccessListener {
                                    android.util.Log.d("Firestore", "Kayıt başarılı")
                                }
                                .addOnFailureListener { e ->
                                    android.util.Log.e("Firestore", "Kayıt hatası: ${e.message}")
                                }
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("Firestore", "Hata: ${e.message}")
                    }
                }
        }
    }
}



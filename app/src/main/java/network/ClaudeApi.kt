package com.erdem.shaman.network

import com.erdem.shaman.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface ClaudeApi {
    @POST("v1/messages")
    suspend fun analyze(@Body request: ClaudeRequest): ClaudeResponse
}

object RetrofitClient {
    private val API_KEY = BuildConfig.CLAUDE_API_KEY

    private val client = OkHttpClient.Builder()
        .addInterceptor(Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("anthropic-version", "2023-06-01")
                .addHeader("x-api-key", API_KEY)
                .build()
            chain.proceed(request)
        })
        .build()

    val api: ClaudeApi = Retrofit.Builder()
        .baseUrl("https://api.anthropic.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ClaudeApi::class.java)
}
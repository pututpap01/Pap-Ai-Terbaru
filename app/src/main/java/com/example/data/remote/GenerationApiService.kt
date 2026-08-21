package com.example.data.remote

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

class GenerationApiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun generateImageWithHuggingFace(
        prompt: String,
        negativePrompt: String,
        modelEndpoint: String,
        apiKey: String,
        guidanceScale: Float = 7.5f,
        steps: Int = 30,
        seed: Long = System.currentTimeMillis() % 100000
    ): Result<Bitmap> = withContext(Dispatchers.IO) {
        try {
            val jsonBody = JSONObject().apply {
                put("inputs", prompt)
                val params = JSONObject().apply {
                    if (negativePrompt.isNotEmpty()) {
                        put("negative_prompt", negativePrompt)
                    }
                    put("guidance_scale", guidanceScale)
                    put("num_inference_steps", steps)
                    put("seed", seed)
                }
                put("parameters", params)
            }

            val requestBuilder = Request.Builder()
                .url(modelEndpoint)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))

            if (apiKey.isNotBlank()) {
                requestBuilder.addHeader("Authorization", "Bearer ${apiKey.trim()}")
            }

            val response = client.newCall(requestBuilder.build()).execute()
            if (!response.isSuccessful) {
                val errorBody = response.body?.string() ?: "HTTP ${response.code}"
                return@withContext Result.failure(Exception("Hugging Face API Error ($errorBody)"))
            }

            val bytes = response.body?.bytes()
            if (bytes != null && bytes.isNotEmpty()) {
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                if (bitmap != null) {
                    return@withContext Result.success(bitmap)
                }
            }
            Result.failure(Exception("Failed to decode image response"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generateImageFastCloud(
        prompt: String,
        width: Int = 1024,
        height: Int = 1024,
        seed: Long = System.currentTimeMillis() % 100000,
        model: String = "flux"
    ): Result<Bitmap> = withContext(Dispatchers.IO) {
        try {
            val encodedPrompt = URLEncoder.encode(prompt, StandardCharsets.UTF_8.toString())
            val url = "https://image.pollinations.ai/prompt/$encodedPrompt?width=$width&height=$height&seed=$seed&model=$model&nologo=true&enhance=false"

            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Cloud Inference returned HTTP ${response.code}"))
            }

            val bytes = response.body?.bytes()
            if (bytes != null && bytes.isNotEmpty()) {
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                if (bitmap != null) {
                    return@withContext Result.success(bitmap)
                }
            }
            Result.failure(Exception("Could not render image bytes"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

package com.example.data.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class Part(
    val text: String? = null,
    val inlineData: InlineData? = null
)

@JsonClass(generateAdapter = true)
data class InlineData(
    val mimeType: String,
    val data: String // Base64 encoded string
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    val responseMimeType: String? = null, // e.g. "application/json"
    val temperature: Float? = null
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    val candidates: List<Candidate>?
)

@JsonClass(generateAdapter = true)
data class Candidate(
    val content: Content?
)

@JsonClass(generateAdapter = true)
data class GeminiOcrResult(
    val title: String,
    val date: String, // YYYY-MM-DD
    val mood: String, // "Vui vẻ" | "Hòa đồng" | "Mệt mỏi" | "Khóc nhè" | "Ngoan ngoãn" | "Bình thường"
    val teacherFeedback: String?,
    val activities: String?,
    val tags: List<String>,
    val isMilestone: Boolean,
    val milestoneTitle: String?
)

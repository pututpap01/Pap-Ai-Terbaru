package com.example.model

data class InferenceEngine(
    val id: String,
    val name: String,
    val badge: String,
    val description: String,
    val endpointUrl: String,
    val supportsNegativePrompt: Boolean = true,
    val defaultGuidanceScale: Float = 7.5f,
    val defaultSteps: Int = 30
) {
    companion object {
        val ENGINES = listOf(
            InferenceEngine(
                id = "sdxl_fast",
                name = "Stable Diffusion XL (Fast Cloud)",
                badge = "Recommended",
                description = "Ultra high detail 1024px generation with rich textures and lighting",
                endpointUrl = "https://api-inference.huggingface.co/models/stabilityai/stable-diffusion-xl-base-1.0",
                supportsNegativePrompt = true,
                defaultGuidanceScale = 7.5f,
                defaultSteps = 30
            ),
            InferenceEngine(
                id = "flux_schnell",
                name = "FLUX.1 Schnell (Instant Inference)",
                badge = "Ultra Fast",
                description = "Next-gen lightning fast AI model with photorealistic fidelity",
                endpointUrl = "https://api-inference.huggingface.co/models/black-forest-labs/FLUX.1-schnell",
                supportsNegativePrompt = false,
                defaultGuidanceScale = 4.0f,
                defaultSteps = 4
            ),
            InferenceEngine(
                id = "anime_diffusion",
                name = "Animagine XL 3.1",
                badge = "Anime/Manga",
                description = "Specialized anime model for pristine cel-shading and character art",
                endpointUrl = "https://api-inference.huggingface.co/models/cagliostrolab/animagine-xl-3.1",
                supportsNegativePrompt = true,
                defaultGuidanceScale = 7.0f,
                defaultSteps = 28
            )
        )
    }
}

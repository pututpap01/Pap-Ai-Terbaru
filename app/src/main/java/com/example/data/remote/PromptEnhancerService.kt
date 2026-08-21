package com.example.data.remote

import com.example.model.ArtStyle

object PromptEnhancerService {

    private val INSPIRATION_PROMPTS = listOf(
        "A mythical solar phoenix emerging from a swirling volcanic caldera with molten gold feathers and ember sparks",
        "Cyberpunk samurai guardian perched on a rain-slicked neon skyscraper rooftop in Neo-Tokyo, volumetric mist",
        "Ethereal crystal sanctuary nestled inside a giant ancient redwood hollow, glowing bioluminescent fauna",
        "Steampunk airship soaring through twilight storm clouds with brass gears, glowing glass vacuum tubes, and lightning",
        "A hyper-detailed mechanical dragon constructed from polished obsidian and pulsing cyan energy conduits",
        "Submerged sunken Atlantis temple with giant manta rays gliding through marble archways and sunbeams",
        "Cosmic greenhouse floating in deep outer space with vibrant alien botanical flowers under double ringed planets",
        "Tiny magical potion apothecary filled with glowing crystal vials, floating spellbooks, and a sleepy calico cat",
        "Lush Studio Ghibli inspired meadow with ancient mossy stone shrine, cherry blossom rain, and golden hour sunshine",
        "Futuristic cybernetic tiger with glowing fiber-optic stripes prowling through a misty bamboo forest"
    )

    private val LIGHTING_ENHANCERS = listOf(
        "dramatic volumetric god rays, rim lighting, soft ambient glow",
        "golden hour warm sunlight, cinematic dusk atmosphere",
        "glowing neon highlights, cybernetic bioluminescence, high contrast shadows",
        "ethereal soft studio lighting, subtle subsurface scattering, octane illumination"
    )

    private val DETAIL_ENHANCERS = listOf(
        "hyper-detailed intricate textures, 8k resolution, award-winning masterpiece",
        "sharp focus, trending on ArtStation, breathtaking composition, pristine rendering",
        "macro level fidelity, cinematic color grading, Unreal Engine 5 aesthetic"
    )

    fun getRandomPrompt(): String {
        return INSPIRATION_PROMPTS.random()
    }

    fun enhancePrompt(basePrompt: String, style: ArtStyle?): String {
        val trimmed = basePrompt.trim()
        if (trimmed.isEmpty()) return ""

        val lighting = LIGHTING_ENHANCERS.random()
        val details = DETAIL_ENHANCERS.random()
        val styleModifier = style?.promptModifier ?: "cinematic composition, beautiful lighting, highly detailed"

        return "$trimmed, $styleModifier, $lighting, $details"
    }
}

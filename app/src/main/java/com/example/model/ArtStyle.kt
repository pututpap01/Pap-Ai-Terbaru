package com.example.model

import androidx.annotation.DrawableRes
import com.example.R

data class ArtStyle(
    val id: String,
    val name: String,
    val description: String,
    val promptModifier: String,
    val negativePromptModifier: String,
    @DrawableRes val previewDrawableRes: Int? = null,
    val category: String = "Popular"
) {
    companion object {
        val PRESETS = listOf(
            ArtStyle(
                id = "cyberpunk",
                name = "Cyberpunk",
                description = "Neon glow, futuristic megacity, octane render",
                promptModifier = "cyberpunk aesthetic, vibrant neon lights, towering futuristic cityscape, highly detailed, octane render, 8k resolution, volumetric lighting, ray tracing",
                negativePromptModifier = "low quality, blurry, deformed, dull colors, oversaturated, text, watermark",
                previewDrawableRes = R.drawable.img_style_cyberpunk,
                category = "Futuristic"
            ),
            ArtStyle(
                id = "anime",
                name = "Anime",
                description = "Studio Ghibli / Makoto Shinkai aesthetic",
                promptModifier = "vibrant anime artwork, Makoto Shinkai style, lush scenery, glowing atmosphere, highly detailed illustration, 4k wallpaper, masterpiece",
                negativePromptModifier = "ugly, distorted face, extra limbs, bad anatomy, lowres, blurry, photorealistic",
                previewDrawableRes = R.drawable.img_style_anime,
                category = "Illustration"
            ),
            ArtStyle(
                id = "cinematic",
                name = "Cinematic 8K",
                description = "Photorealistic Hollywood movie still, 35mm lens",
                promptModifier = "cinematic film still, photorealistic 8k, shot on 35mm lens, dramatic natural lighting, shallow depth of field, IMAX quality, masterpiece",
                negativePromptModifier = "cartoon, drawing, anime, plastic, low contrast, oversaturated, amateur",
                previewDrawableRes = R.drawable.img_style_cinematic,
                category = "Realistic"
            ),
            ArtStyle(
                id = "watercolor",
                name = "Watercolor",
                description = "Soft pastel washes and delicate ink strokes",
                promptModifier = "ethereal watercolor painting, delicate ink outlines, soft pastel gradient wash, paper texture, expressive brushstrokes, artistic masterpiece",
                negativePromptModifier = "photorealistic, 3d render, plastic, sharp harsh lines, dark muddy colors",
                previewDrawableRes = R.drawable.img_style_watercolor,
                category = "Artistic"
            ),
            ArtStyle(
                id = "3d_render",
                name = "3D Digital Art",
                description = "Blender 3D, cute isometric, clay & gloss",
                promptModifier = "3D digital illustration, smooth clay materials, soft ambient occlusion, cute isometric perspective, trending on Artstation, clean lighting",
                negativePromptModifier = "flat 2d, noisy, blurry, pixelated, ugly textures",
                previewDrawableRes = null,
                category = "3D & Digital"
            ),
            ArtStyle(
                id = "pixel_art",
                name = "Pixel Art 16-bit",
                description = "Retro nostalgic gaming pixel aesthetic",
                promptModifier = "masterpiece 16-bit pixel art, vibrant color palette, nostalgic retro game aesthetic, clean pixel clusters, atmospheric dithering",
                negativePromptModifier = "smooth gradients, 3d, photorealistic, vector, blurry",
                previewDrawableRes = null,
                category = "Retro"
            ),
            ArtStyle(
                id = "oil_painting",
                name = "Classic Oil",
                description = "Rembrandt & Van Gogh impasto brushwork",
                promptModifier = "classical oil painting on canvas, rich impasto texture, dramatic chiaroscuro lighting, masterwork of fine art, expressive strokes",
                negativePromptModifier = "digital flat, CGI, plastic, low quality, anime",
                previewDrawableRes = null,
                category = "Artistic"
            ),
            ArtStyle(
                id = "fantasy_mythic",
                name = "Fantasy Mythic",
                description = "Epic high fantasy, magical aura, celestial clouds",
                promptModifier = "epic fantasy concept art, glowing magical particles, celestial skies, mythical atmosphere, intricate ornaments, D&D artwork",
                negativePromptModifier = "modern, contemporary, sci-fi, blurry, plain",
                previewDrawableRes = null,
                category = "Fantasy"
            ),
            ArtStyle(
                id = "synthwave",
                name = "Synthwave 80s",
                description = "Retro 80s grid, sunset, purple & cyan vibes",
                promptModifier = "synthwave retrowave aesthetic, wireframe grid, glowing sunset horizon, chrome reflections, vibrant magenta and cyan neon glow",
                negativePromptModifier = "grayscale, desaturated, daytime, medieval",
                previewDrawableRes = null,
                category = "Futuristic"
            )
        )
    }
}

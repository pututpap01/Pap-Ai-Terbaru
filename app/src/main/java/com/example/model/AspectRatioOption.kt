package com.example.model

enum class AspectRatioOption(val label: String, val ratioText: String, val width: Int, val height: Int, val floatRatio: Float) {
    SQUARE_1_1("1:1", "Square (Instagram)", 1024, 1024, 1.0f),
    PORTRAIT_9_16("9:16", "Story / Reels", 768, 1360, 9f / 16f),
    LANDSCAPE_16_9("16:9", "Landscape / Video", 1360, 768, 16f / 9f),
    STANDARD_4_3("4:3", "Classic Photo", 1024, 768, 4f / 3f)
}

enum class ZoomDirection(val displayName: String, val description: String) {
    INFINITE_IN("Infinite Zoom In", "Smooth continuous forward zoom into the center"),
    INFINITE_OUT("Infinite Zoom Out", "Smooth continuous backward zoom revealing the world"),
    SPIRAL_VORTEX("Hypnotic Spiral", "Zooming with a graceful rotational twist"),
    PARALLAX_PULSE("Parallax Pulse", "Oscillating zoom and dynamic depth breathing")
}

data class ZoomAnimationConfig(
    val direction: ZoomDirection = ZoomDirection.INFINITE_IN,
    val speed: Float = 1.0f, // 0.5f to 3.0f
    val depthStages: Int = 4, // Number of zoom layers (1 to 6)
    val loop: Boolean = true,
    val motionBlurEnabled: Boolean = true,
    val zoomFactorPerCycle: Float = 3.5f
)

package com.example.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RadialGradient
import android.graphics.Shader
import com.example.model.ZoomDirection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.cos
import kotlin.math.sin

object BitmapZoomEngine {

    /**
     * Generates a series of nested zoom keyframe Bitmaps (from outer context to deep zoom)
     */
    suspend fun generateZoomKeyframes(baseBitmap: Bitmap, stageCount: Int = 4): List<Bitmap> = withContext(Dispatchers.Default) {
        val keyframes = mutableListOf<Bitmap>()
        val width = baseBitmap.width
        val height = baseBitmap.height

        keyframes.add(baseBitmap)

        for (stage in 1 until stageCount) {
            val scaleFactor = 1.0f + (stage * 0.75f)
            val stageBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(stageBitmap)

            val matrix = Matrix().apply {
                postScale(scaleFactor, scaleFactor, width / 2f, height / 2f)
            }
            val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
            canvas.drawBitmap(baseBitmap, matrix, paint)

            // Add subtle atmospheric layer vignette to make zoom look cinematic and seamless
            val vignettePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = RadialGradient(
                    width / 2f, height / 2f,
                    width * 0.65f,
                    intArrayOf(Color.TRANSPARENT, Color.argb(40, 10, 15, 30), Color.argb(100, 5, 8, 18)),
                    floatArrayOf(0.4f, 0.8f, 1.0f),
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), vignettePaint)
            keyframes.add(stageBitmap)
        }

        keyframes
    }

    /**
     * Computes the current matrix transformation for a given normalized zoom progress [0.0..1.0]
     */
    fun computeTransform(
        progress: Float,
        direction: ZoomDirection,
        zoomFactor: Float = 3.5f,
        viewWidth: Float,
        viewHeight: Float
    ): Matrix {
        val matrix = Matrix()
        val centerX = viewWidth / 2f
        val centerY = viewHeight / 2f

        when (direction) {
            ZoomDirection.INFINITE_IN -> {
                val currentScale = 1.0f + (progress * (zoomFactor - 1.0f))
                matrix.postScale(currentScale, currentScale, centerX, centerY)
            }
            ZoomDirection.INFINITE_OUT -> {
                val currentScale = zoomFactor - (progress * (zoomFactor - 1.0f))
                matrix.postScale(currentScale, currentScale, centerX, centerY)
            }
            ZoomDirection.SPIRAL_VORTEX -> {
                val currentScale = 1.0f + (progress * (zoomFactor - 1.0f))
                val rotationAngle = progress * 45f // 45 degrees twist during cycle
                matrix.postScale(currentScale, currentScale, centerX, centerY)
                matrix.postRotate(rotationAngle, centerX, centerY)
            }
            ZoomDirection.PARALLAX_PULSE -> {
                val wave = sin(progress * Math.PI * 2.0).toFloat()
                val currentScale = 1.0f + ((wave + 1f) * 0.5f * (zoomFactor - 1.0f))
                val offsetX = cos(progress * Math.PI * 2.0).toFloat() * 15f
                val offsetY = sin(progress * Math.PI * 2.0).toFloat() * 15f
                matrix.postScale(currentScale, currentScale, centerX, centerY)
                matrix.postTranslate(offsetX, offsetY)
            }
        }
        return matrix
    }
}

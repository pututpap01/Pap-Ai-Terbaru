package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ZoomDirection
import com.example.ui.theme.ImmersiveOnPrimary
import com.example.ui.theme.ImmersivePrimary
import com.example.ui.theme.ImmersiveSurface
import com.example.ui.theme.NeonRose
import com.example.util.BitmapZoomEngine

@Composable
fun ZoomVideoPlayer(
    bitmaps: List<Bitmap>,
    direction: ZoomDirection,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    speed: Float,
    onSpeedChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    aspectRatio: Float = 1.0f
) {
    if (bitmaps.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(aspectRatio)
                .background(ImmersiveSurface, RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = null,
                    tint = ImmersivePrimary,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "No Zoom Animation Loaded",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    var progress by remember { mutableFloatStateOf(0.0f) }
    var showControls by remember { mutableStateOf(true) }

    // Smooth animation loop driving frame rendering
    LaunchedEffect(isPlaying, speed, bitmaps.size) {
        if (!isPlaying) return@LaunchedEffect
        var lastTime = 0L
        while (true) {
            withFrameNanos { frameTimeNanos ->
                if (lastTime != 0L) {
                    val deltaSeconds = (frameTimeNanos - lastTime) / 1_000_000_000f
                    val cycleDuration = 4.0f / speed.coerceAtLeast(0.2f)
                    progress = (progress + (deltaSeconds / cycleDuration)) % 1.0f
                }
                lastTime = frameTimeNanos
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { showControls = !showControls }
            )
            .testTag("zoom_video_player")
    ) {
        // Main canvas zoom rendering
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawIntoCanvas { canvas ->
                val nativeCanvas = canvas.nativeCanvas
                val width = size.width
                val height = size.height

                // Calculate which keyframe to display or blend
                val frameIndex = if (bitmaps.size > 1) {
                    val rawIndex = (progress * bitmaps.size).toInt() % bitmaps.size
                    rawIndex
                } else 0

                val currentBitmap = bitmaps.getOrNull(frameIndex) ?: bitmaps.first()

                // Calculate matrix transformation
                val subProgress = if (bitmaps.size > 1) {
                    (progress * bitmaps.size) % 1.0f
                } else progress

                val matrix = BitmapZoomEngine.computeTransform(
                    progress = subProgress,
                    direction = direction,
                    zoomFactor = 2.4f,
                    viewWidth = width,
                    viewHeight = height
                )

                // Scale bitmap to fill view
                val scaleX = width / currentBitmap.width.toFloat()
                val scaleY = height / currentBitmap.height.toFloat()
                val maxScale = maxOf(scaleX, scaleY)

                val baseMatrix = android.graphics.Matrix().apply {
                    postTranslate(-currentBitmap.width / 2f, -currentBitmap.height / 2f)
                    postScale(maxScale, maxScale)
                    postConcat(matrix)
                    postTranslate(width / 2f, height / 2f)
                }

                nativeCanvas.save()
                nativeCanvas.concat(baseMatrix)
                nativeCanvas.drawBitmap(currentBitmap, 0f, 0f, android.graphics.Paint(android.graphics.Paint.FILTER_BITMAP_FLAG or android.graphics.Paint.ANTI_ALIAS_FLAG))
                nativeCanvas.restore()
            }
        }

        // Top tag badge
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(if (isPlaying) ImmersivePrimary else NeonRose, CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = direction.displayName,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Overlay Controls
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.75f))
                    .padding(12.dp)
            ) {
                // Progress Bar
                Slider(
                    value = progress,
                    onValueChange = { progress = it },
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = ImmersivePrimary,
                        activeTrackColor = ImmersivePrimary,
                        inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.fillMaxWidth().height(24.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Play / Pause Button
                    IconButton(
                        onClick = onTogglePlay,
                        modifier = Modifier
                            .size(42.dp)
                            .background(ImmersivePrimary, CircleShape)
                            .testTag("btn_toggle_zoom_play")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = ImmersiveOnPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Speed controls
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(0.5f, 1.0f, 1.5f, 2.0f).forEach { speedOption ->
                            val isSelected = speed == speedOption
                            AssistChip(
                                onClick = { onSpeedChange(speedOption) },
                                label = { Text("${speedOption}x", fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = if (isSelected) ImmersivePrimary.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.1f),
                                    labelColor = if (isSelected) ImmersivePrimary else Color.White
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) ImmersivePrimary else Color.Transparent
                                ),
                                modifier = Modifier.height(28.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

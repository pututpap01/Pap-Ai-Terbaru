package com.example.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CreationEntity
import com.example.model.ArtStyle
import com.example.model.ZoomDirection
import com.example.ui.components.ArtStyleSelector
import com.example.ui.components.GenerationProgressView
import com.example.ui.components.PromptInputBox
import com.example.ui.components.ZoomVideoPlayer
import com.example.ui.theme.ImmersiveOnPrimary
import com.example.ui.theme.ImmersiveOutlineVariant
import com.example.ui.theme.ImmersivePrimary
import com.example.ui.theme.ImmersivePrimaryContainer
import com.example.ui.theme.ImmersiveSurface
import com.example.ui.theme.ImmersiveSurfaceElevated
import com.example.ui.viewmodel.GenerationUiState
import com.example.ui.viewmodel.StudioState

@Composable
fun ZoomVideoScreen(
    state: StudioState,
    onPromptChange: (String) -> Unit,
    onNegativePromptChange: (String) -> Unit,
    onStyleSelected: (ArtStyle) -> Unit,
    onDirectionSelected: (ZoomDirection) -> Unit,
    onSpeedChange: (Float) -> Unit,
    onStagesChange: (Int) -> Unit,
    onTogglePlay: () -> Unit,
    onRandomPrompt: () -> Unit,
    onToggleAutoEnhance: () -> Unit,
    onGenerateZoomVideo: () -> Unit,
    onOpenCreationDetail: (CreationEntity) -> Unit,
    onExport: (Bitmap?, String) -> Unit,
    onShare: (CreationEntity, Bitmap?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Screen Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "AI Zoom Studio",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Recursive Infinite Zoom Animation",
                            style = MaterialTheme.typography.bodySmall,
                            color = ImmersivePrimary.copy(alpha = 0.75f)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = ImmersivePrimaryContainer,
                        border = androidx.compose.foundation.BorderStroke(1.dp, ImmersivePrimary.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Movie,
                                contentDescription = null,
                                tint = ImmersivePrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AI Motion",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = ImmersivePrimary
                            )
                        }
                    }
                }
            }
        }

        // Live Zoom Player (If generated in this session)
        if (state.generationState is GenerationUiState.Success && (state.generationState as GenerationUiState.Success).creation.isVideo) {
            val genSuccess = state.generationState as GenerationUiState.Success
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "Live AI Zoom Player",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ImmersivePrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    ZoomVideoPlayer(
                        bitmaps = genSuccess.frames,
                        direction = state.zoomConfig.direction,
                        isPlaying = state.isPlayingZoom,
                        onTogglePlay = onTogglePlay,
                        speed = state.zoomPlaybackSpeed,
                        onSpeedChange = onSpeedChange
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onOpenCreationDetail(genSuccess.creation) },
                            modifier = Modifier.weight(1f).testTag("btn_view_zoom_detail"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ImmersivePrimary, contentColor = ImmersiveOnPrimary)
                        ) {
                            Text("Full Screen Studio", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { onExport(genSuccess.bitmap, genSuccess.creation.prompt) },
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveOutlineVariant)
                        ) {
                            Icon(imageVector = Icons.Default.Download, contentDescription = "Export")
                        }

                        OutlinedButton(
                            onClick = { onShare(genSuccess.creation, genSuccess.bitmap) },
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveOutlineVariant)
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // Prompt Input
        item {
            PromptInputBox(
                prompt = state.prompt,
                negativePrompt = state.negativePrompt,
                onPromptChange = onPromptChange,
                onNegativePromptChange = onNegativePromptChange,
                onRandomPrompt = onRandomPrompt,
                autoEnhanceEnabled = state.autoEnhancePrompt,
                onToggleAutoEnhance = onToggleAutoEnhance
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Art Style Selector
        item {
            ArtStyleSelector(
                styles = ArtStyle.PRESETS,
                selectedStyle = state.selectedStyle,
                onStyleSelected = onStyleSelected
            )
        }

        // Zoom Motion Direction Picker
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.SlowMotionVideo,
                        contentDescription = null,
                        tint = ImmersivePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CAMERA MOTION MODE",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = ImmersivePrimary,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ZoomDirection.values().forEach { direction ->
                        val isSelected = direction == state.zoomConfig.direction
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) ImmersivePrimary else ImmersiveOutlineVariant,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { onDirectionSelected(direction) }
                                .testTag("direction_${direction.name}"),
                            color = if (isSelected) ImmersivePrimaryContainer.copy(alpha = 0.4f) else ImmersiveSurface,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = direction.displayName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) ImmersivePrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = direction.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Depth Stages & Speed Controls
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(20.dp),
                color = ImmersiveSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveOutlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Zoom Stages (2 to 6)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recursive Zoom Layers",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${state.zoomConfig.depthStages} Stages",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = ImmersivePrimary
                        )
                    }
                    Slider(
                        value = state.zoomConfig.depthStages.toFloat(),
                        onValueChange = { onStagesChange(it.toInt()) },
                        valueRange = 2f..6f,
                        steps = 3,
                        colors = SliderDefaults.colors(
                            thumbColor = ImmersivePrimary,
                            activeTrackColor = ImmersivePrimary,
                            inactiveTrackColor = ImmersiveSurfaceElevated
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("slider_zoom_stages")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Playback Speed
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Animation Speed",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${state.zoomPlaybackSpeed}x",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = ImmersivePrimary
                        )
                    }
                    Slider(
                        value = state.zoomPlaybackSpeed,
                        onValueChange = onSpeedChange,
                        valueRange = 0.5f..3.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = ImmersivePrimary,
                            activeTrackColor = ImmersivePrimary,
                            inactiveTrackColor = ImmersiveSurfaceElevated
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("slider_zoom_speed")
                    )
                }
            }
        }

        // Generate Zoom Video Action Button
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Button(
                    onClick = onGenerateZoomVideo,
                    enabled = state.prompt.isNotBlank() && state.generationState !is GenerationUiState.Generating,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("btn_generate_zoom_video"),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ImmersivePrimary,
                        contentColor = ImmersiveOnPrimary,
                        disabledContainerColor = ImmersivePrimary.copy(alpha = 0.3f),
                        disabledContentColor = ImmersiveOnPrimary.copy(alpha = 0.5f)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.SlowMotionVideo,
                        contentDescription = null,
                        tint = ImmersiveOnPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "GENERATE AI ZOOM",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = ImmersiveOnPrimary,
                        letterSpacing = 1.5.sp
                    )
                }
            }
        }

        // Progress and Error Handling
        when (val genState = state.generationState) {
            is GenerationUiState.Generating -> {
                item {
                    GenerationProgressView(
                        message = genState.stepMessage,
                        progress = genState.progress
                    )
                }
            }
            is GenerationUiState.Error -> {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.errorContainer,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = "Error",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = genState.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
            else -> {}
        }
    }
}

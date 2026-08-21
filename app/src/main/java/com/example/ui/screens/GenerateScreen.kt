package com.example.ui.screens

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CreationEntity
import com.example.model.ArtStyle
import com.example.model.AspectRatioOption
import com.example.model.InferenceEngine
import com.example.ui.components.ArtStyleSelector
import com.example.ui.components.GenerationProgressView
import com.example.ui.components.PromptInputBox
import com.example.ui.theme.ImmersiveBackground
import com.example.ui.theme.ImmersiveOnPrimary
import com.example.ui.theme.ImmersiveOutline
import com.example.ui.theme.ImmersiveOutlineVariant
import com.example.ui.theme.ImmersivePrimary
import com.example.ui.theme.ImmersivePrimaryContainer
import com.example.ui.theme.ImmersiveSurface
import com.example.ui.theme.ImmersiveSurfaceElevated
import com.example.ui.theme.NeonCyan
import com.example.ui.viewmodel.GenerationUiState
import com.example.ui.viewmodel.StudioState

@Composable
fun GenerateScreen(
    state: StudioState,
    onPromptChange: (String) -> Unit,
    onNegativePromptChange: (String) -> Unit,
    onStyleSelected: (ArtStyle) -> Unit,
    onAspectRatioSelected: (AspectRatioOption) -> Unit,
    onEngineSelected: (InferenceEngine) -> Unit,
    onRandomPrompt: () -> Unit,
    onToggleAutoEnhance: () -> Unit,
    onGenerate: () -> Unit,
    onOpenCreationDetail: (CreationEntity) -> Unit,
    onNavigateToZoom: () -> Unit,
    onExport: (Bitmap?, String) -> Unit,
    onShare: (CreationEntity, Bitmap?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // App Header Banner
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Luminous Logo Avatar
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    Brush.linearGradient(
                                        listOf(ImmersivePrimary, ImmersivePrimaryContainer)
                                    ),
                                    RoundedCornerShape(14.dp)
                                )
                                .border(1.dp, ImmersiveOutline, RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "L",
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp,
                                color = ImmersiveOnPrimary
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Lumina AI Studio",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Stable Diffusion XL & Infinite Zoom",
                                style = MaterialTheme.typography.bodySmall,
                                color = ImmersivePrimary.copy(alpha = 0.75f)
                            )
                        }
                    }

                    // Engine Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = ImmersiveSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveOutlineVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudQueue,
                                contentDescription = null,
                                tint = ImmersivePrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = state.selectedEngine.badge,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = ImmersivePrimary
                            )
                        }
                    }
                }
            }
        }

        // Prompt Input Box
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

        // Art Style Carousel
        item {
            ArtStyleSelector(
                styles = ArtStyle.PRESETS,
                selectedStyle = state.selectedStyle,
                onStyleSelected = onStyleSelected
            )
        }

        // Aspect Ratio Selector
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AspectRatio,
                        contentDescription = null,
                        tint = ImmersivePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ASPECT RATIO",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = ImmersivePrimary,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AspectRatioOption.values().forEach { option ->
                        val isSelected = option == state.selectedAspectRatio
                        FilterChip(
                            selected = isSelected,
                            onClick = { onAspectRatioSelected(option) },
                            label = {
                                Text(
                                    option.label,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ImmersivePrimaryContainer,
                                selectedLabelColor = ImmersivePrimary,
                                containerColor = ImmersiveSurfaceElevated,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) ImmersivePrimary else ImmersiveOutlineVariant
                            ),
                            modifier = Modifier.weight(1f).testTag("ratio_${option.name}")
                        )
                    }
                }
            }
        }

        // Inference Engine Selector
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = ImmersivePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "INFERENCE ENGINE",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = ImmersivePrimary,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    InferenceEngine.ENGINES.forEach { engine ->
                        val isSelected = engine.id == state.selectedEngine.id
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) ImmersivePrimary else ImmersiveOutlineVariant,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { onEngineSelected(engine) },
                            color = if (isSelected) ImmersivePrimaryContainer.copy(alpha = 0.4f) else ImmersiveSurface,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = engine.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) ImmersivePrimary else MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (isSelected) ImmersivePrimary.copy(alpha = 0.2f) else ImmersiveSurfaceElevated
                                        ) {
                                            Text(
                                                text = engine.badge,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (isSelected) ImmersivePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 9.sp,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = engine.description,
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

        // Primary Action: Generate Button (Immersive Glowing Aesthetic)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Button(
                    onClick = onGenerate,
                    enabled = state.prompt.isNotBlank() && state.generationState !is GenerationUiState.Generating,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("btn_generate_image"),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ImmersivePrimary,
                        contentColor = ImmersiveOnPrimary,
                        disabledContainerColor = ImmersivePrimary.copy(alpha = 0.3f),
                        disabledContentColor = ImmersiveOnPrimary.copy(alpha = 0.5f)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = ImmersiveOnPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "GENERATE",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )
                }
            }
        }

        // Generation Progress State View
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
            is GenerationUiState.Success -> {
                if (genState.bitmap != null) {
                    item {
                        GeneratedResultCard(
                            creation = genState.creation,
                            bitmap = genState.bitmap,
                            onOpenDetail = { onOpenCreationDetail(genState.creation) },
                            onNavigateToZoom = onNavigateToZoom,
                            onExport = { onExport(genState.bitmap, genState.creation.prompt) },
                            onShare = { onShare(genState.creation, genState.bitmap) },
                            onRegenerate = onGenerate
                        )
                    }
                }
            }
            GenerationUiState.Idle -> {}
        }
    }
}

@Composable
fun GeneratedResultCard(
    creation: CreationEntity,
    bitmap: Bitmap,
    onOpenDetail: () -> Unit,
    onNavigateToZoom: () -> Unit,
    onExport: () -> Unit,
    onShare: () -> Unit,
    onRegenerate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("card_latest_generated_result"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveOutlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "✨ AI Result",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ImmersivePrimary
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ImmersiveSurfaceElevated
                ) {
                    Text(
                        text = creation.styleName,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = ImmersivePrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Image Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .clickable(onClick = onOpenDetail)
            ) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = creation.prompt,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = creation.prompt,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Animate into Zoom Video (AI Zoom Purple Action)
                Button(
                    onClick = onNavigateToZoom,
                    modifier = Modifier.weight(1f).testTag("btn_animate_zoom"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ImmersivePrimaryContainer,
                        contentColor = ImmersivePrimary
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ImmersivePrimary.copy(alpha = 0.5f))
                ) {
                    Icon(
                        imageVector = Icons.Default.SlowMotionVideo,
                        contentDescription = null,
                        tint = ImmersivePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("AI Zoom Video", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                // Share
                OutlinedButton(
                    onClick = onShare,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveOutlineVariant),
                    modifier = Modifier.testTag("btn_share_result")
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(16.dp))
                }

                // Export / Save
                OutlinedButton(
                    onClick = onExport,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveOutlineVariant),
                    modifier = Modifier.testTag("btn_export_result")
                ) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = "Save", modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

package com.example.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.local.CreationEntity
import com.example.model.ZoomDirection
import com.example.ui.components.ZoomVideoPlayer
import com.example.ui.theme.ImmersiveBackground
import com.example.ui.theme.ImmersiveOnPrimary
import com.example.ui.theme.ImmersiveOutlineVariant
import com.example.ui.theme.ImmersivePrimary
import com.example.ui.theme.ImmersivePrimaryContainer
import com.example.ui.theme.ImmersiveSurface
import com.example.ui.theme.ImmersiveSurfaceElevated
import com.example.ui.theme.NeonRose
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    creation: CreationEntity,
    bitmaps: List<Bitmap>,
    isPlayingZoom: Boolean,
    onToggleZoomPlay: () -> Unit,
    zoomSpeed: Float,
    onZoomSpeedChange: (Float) -> Unit,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit,
    onRemixPrompt: () -> Unit,
    onNavigateToZoom: () -> Unit,
    onExport: (Bitmap?, String) -> Unit,
    onShare: (CreationEntity, Bitmap?) -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val primaryBitmap = bitmaps.firstOrNull()
    val zoomDirection = try {
        if (!creation.videoType.isNullOrEmpty()) ZoomDirection.valueOf(creation.videoType) else ZoomDirection.INFINITE_IN
    } catch (e: Exception) {
        ZoomDirection.INFINITE_IN
    }

    Scaffold(
        containerColor = ImmersiveBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (creation.isVideo) "AI Zoom Animation" else "Artwork Detail",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("btn_detail_back")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ImmersivePrimary)
                    }
                },
                actions = {
                    IconButton(onClick = onToggleFavorite, modifier = Modifier.testTag("btn_detail_fav")) {
                        Icon(
                            imageVector = if (creation.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (creation.isFavorite) NeonRose else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(
                        onClick = { onShare(creation, primaryBitmap) },
                        modifier = Modifier.testTag("btn_detail_share")
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = MaterialTheme.colorScheme.onSurface)
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.testTag("btn_detail_delete")) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ImmersiveSurface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Media Display Area
            item {
                if (creation.isVideo && bitmaps.isNotEmpty()) {
                    ZoomVideoPlayer(
                        bitmaps = bitmaps,
                        direction = zoomDirection,
                        isPlaying = isPlayingZoom,
                        onTogglePlay = onToggleZoomPlay,
                        speed = zoomSpeed,
                        onSpeedChange = onZoomSpeedChange
                    )
                } else if (primaryBitmap != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(20.dp))
                    ) {
                        Image(
                            bitmap = primaryBitmap.asImageBitmap(),
                            contentDescription = creation.prompt,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                } else if (!creation.localFilePath.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(20.dp))
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(File(creation.localFilePath))
                                .crossfade(true)
                                .build(),
                            contentDescription = creation.prompt,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            // Quick Actions Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            onRemixPrompt()
                            onBack()
                        },
                        modifier = Modifier.weight(1f).testTag("btn_remix_prompt"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ImmersivePrimary, contentColor = ImmersiveOnPrimary)
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Remix Prompt", fontWeight = FontWeight.Bold)
                    }

                    if (!creation.isVideo) {
                        Button(
                            onClick = {
                                onRemixPrompt()
                                onNavigateToZoom()
                            },
                            modifier = Modifier.weight(1f).testTag("btn_zoom_from_this"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ImmersivePrimaryContainer, contentColor = ImmersivePrimary),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ImmersivePrimary.copy(alpha = 0.5f))
                        ) {
                            Icon(imageVector = Icons.Default.SlowMotionVideo, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Make Zoom", fontWeight = FontWeight.Bold)
                        }
                    }

                    OutlinedButton(
                        onClick = { onExport(primaryBitmap, creation.prompt) },
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveOutlineVariant),
                        modifier = Modifier.testTag("btn_detail_export")
                    ) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = "Export to Gallery")
                    }
                }
            }

            // Prompt Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveOutlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "PROMPT DETAILS",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = ImmersivePrimary,
                                letterSpacing = 1.sp
                            )
                            IconButton(
                                onClick = { clipboardManager.setText(AnnotatedString(creation.prompt)) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Prompt",
                                    tint = ImmersivePrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = creation.prompt,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (creation.enhancedPrompt.isNotBlank() && creation.enhancedPrompt != creation.prompt) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "AI Expanded Prompt:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = ImmersivePrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = creation.enhancedPrompt,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (creation.negativePrompt.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Negative Prompt:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = NeonRose
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = creation.negativePrompt,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Generation Parameters Meta Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveOutlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "GENERATION PARAMETERS",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = ImmersivePrimary,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        ParameterRow(label = "Art Style", value = creation.styleName)
                        ParameterRow(label = "Model Engine", value = creation.modelName)
                        ParameterRow(label = "Aspect Ratio", value = creation.aspectRatio)
                        ParameterRow(label = "Seed", value = "${creation.seed}")
                        if (creation.isVideo) {
                            ParameterRow(label = "Zoom Type", value = creation.videoType ?: "Infinite Zoom")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ParameterRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

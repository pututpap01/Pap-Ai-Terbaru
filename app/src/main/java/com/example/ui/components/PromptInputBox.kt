package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ImmersiveOutlineVariant
import com.example.ui.theme.ImmersivePrimary
import com.example.ui.theme.ImmersivePrimaryContainer
import com.example.ui.theme.ImmersiveSurface
import com.example.ui.theme.ImmersiveSurfaceElevated
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan

@Composable
fun PromptInputBox(
    prompt: String,
    negativePrompt: String,
    onPromptChange: (String) -> Unit,
    onNegativePromptChange: (String) -> Unit,
    onRandomPrompt: () -> Unit,
    autoEnhanceEnabled: Boolean,
    onToggleAutoEnhance: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showNegativePrompt by remember { mutableStateOf(negativePrompt.isNotEmpty()) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        color = ImmersiveSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveOutlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with action chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = ImmersivePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "PROMPT",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = ImmersivePrimary,
                        letterSpacing = 1.sp
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Random Idea Dice
                    AssistChip(
                        onClick = onRandomPrompt,
                        label = { Text("Inspire", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Casino,
                                contentDescription = "Random Prompt Idea",
                                modifier = Modifier.size(14.dp),
                                tint = NeonCyan
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = ImmersiveSurfaceElevated,
                            labelColor = MaterialTheme.colorScheme.onSurface
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveOutlineVariant),
                        modifier = Modifier.height(32.dp).testTag("btn_random_prompt")
                    )

                    // Auto Enhance Toggle
                    FilterChip(
                        selected = autoEnhanceEnabled,
                        onClick = onToggleAutoEnhance,
                        label = { Text("AI Magic", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI Enhance",
                                modifier = Modifier.size(14.dp),
                                tint = if (autoEnhanceEnabled) ImmersivePrimary else MaterialTheme.colorScheme.onSurfaceVariant
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
                            selected = autoEnhanceEnabled,
                            borderColor = if (autoEnhanceEnabled) ImmersivePrimary else ImmersiveOutlineVariant
                        ),
                        modifier = Modifier.height(32.dp).testTag("toggle_ai_magic")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Prompt Field
            OutlinedTextField(
                value = prompt,
                onValueChange = onPromptChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_prompt"),
                placeholder = {
                    Text(
                        "Describe what you want to see (e.g., A celestial fox leaping over cosmic nebula with star sparks...)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                minLines = 3,
                maxLines = 6,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ImmersivePrimary,
                    unfocusedBorderColor = ImmersiveOutlineVariant,
                    focusedContainerColor = ImmersiveSurfaceElevated,
                    unfocusedContainerColor = ImmersiveSurfaceElevated
                ),
                trailingIcon = {
                    if (prompt.isNotEmpty()) {
                        IconButton(onClick = { onPromptChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear prompt",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Negative prompt expander button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { showNegativePrompt = !showNegativePrompt }
                    .padding(vertical = 4.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.RemoveCircleOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Negative Prompt (Elements to avoid)",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (showNegativePrompt) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }

            AnimatedVisibility(
                visible = showNegativePrompt,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    OutlinedTextField(
                        value = negativePrompt,
                        onValueChange = onNegativePromptChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_negative_prompt"),
                        placeholder = {
                            Text(
                                "blurry, low quality, deformed anatomy, extra fingers, text watermark...",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        },
                        minLines = 2,
                        maxLines = 3,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                            unfocusedBorderColor = ImmersiveOutlineVariant,
                            focusedContainerColor = ImmersiveSurfaceElevated,
                            unfocusedContainerColor = ImmersiveSurfaceElevated
                        )
                    )
                }
            }
        }
    }
}

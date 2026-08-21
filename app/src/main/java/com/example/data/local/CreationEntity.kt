package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "creations")
data class CreationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val prompt: String,
    val enhancedPrompt: String = "",
    val negativePrompt: String = "",
    val styleId: String = "cyberpunk",
    val styleName: String = "Cyberpunk",
    val modelName: String = "Stable Diffusion XL",
    val imageUrl: String = "",
    val localFilePath: String? = null,
    val isVideo: Boolean = false,
    val videoType: String? = null,
    val zoomFramesPaths: String? = null,
    val aspectRatio: String = "1:1",
    val seed: Long = 0L,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)

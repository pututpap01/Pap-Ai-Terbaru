package com.example.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.example.data.local.CreationDao
import com.example.data.local.CreationEntity
import com.example.data.remote.GenerationApiService
import com.example.data.remote.PromptEnhancerService
import com.example.model.ArtStyle
import com.example.model.AspectRatioOption
import com.example.model.InferenceEngine
import com.example.model.ZoomAnimationConfig
import com.example.util.BitmapZoomEngine
import com.example.util.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray

class CreationRepository(
    private val context: Context,
    private val creationDao: CreationDao,
    private val apiService: GenerationApiService = GenerationApiService()
) {

    val allCreations: Flow<List<CreationEntity>> = creationDao.getAllCreations()
    val imageCreations: Flow<List<CreationEntity>> = creationDao.getImageCreations()
    val videoCreations: Flow<List<CreationEntity>> = creationDao.getVideoCreations()
    val favoriteCreations: Flow<List<CreationEntity>> = creationDao.getFavoriteCreations()

    suspend fun getCreationById(id: Long): CreationEntity? = creationDao.getCreationById(id)

    suspend fun toggleFavorite(id: Long, isFavorite: Boolean) {
        creationDao.updateFavorite(id, isFavorite)
    }

    suspend fun deleteCreation(creation: CreationEntity) {
        creationDao.deleteCreation(creation)
    }

    suspend fun deleteById(id: Long) {
        creationDao.deleteById(id)
    }

    /**
     * Generates a Text-to-Image creation and persists it to local Room database.
     */
    suspend fun generateImage(
        prompt: String,
        negativePrompt: String,
        style: ArtStyle,
        engine: InferenceEngine,
        aspectRatio: AspectRatioOption,
        apiKey: String,
        seed: Long,
        enhancePrompt: Boolean
    ): Result<Pair<CreationEntity, Bitmap>> = withContext(Dispatchers.IO) {
        try {
            val finalPrompt = if (enhancePrompt) {
                PromptEnhancerService.enhancePrompt(prompt, style)
            } else {
                if (style.promptModifier.isNotEmpty()) "$prompt, ${style.promptModifier}" else prompt
            }

            val finalNegative = if (negativePrompt.isNotEmpty()) {
                "$negativePrompt, ${style.negativePromptModifier}"
            } else {
                style.negativePromptModifier
            }

            val resultBitmap: Result<Bitmap> = if (apiKey.isNotBlank()) {
                apiService.generateImageWithHuggingFace(
                    prompt = finalPrompt,
                    negativePrompt = finalNegative,
                    modelEndpoint = engine.endpointUrl,
                    apiKey = apiKey,
                    guidanceScale = engine.defaultGuidanceScale,
                    steps = engine.defaultSteps,
                    seed = seed
                )
            } else {
                apiService.generateImageFastCloud(
                    prompt = finalPrompt,
                    width = aspectRatio.width,
                    height = aspectRatio.height,
                    seed = seed
                )
            }

            resultBitmap.fold(
                onSuccess = { bitmap ->
                    val localPath = FileUtils.saveBitmapToInternalStorage(context, bitmap, "img_${style.id}")
                    val entity = CreationEntity(
                        prompt = prompt,
                        enhancedPrompt = finalPrompt,
                        negativePrompt = finalNegative,
                        styleId = style.id,
                        styleName = style.name,
                        modelName = engine.name,
                        imageUrl = "",
                        localFilePath = localPath,
                        isVideo = false,
                        aspectRatio = aspectRatio.label,
                        seed = seed,
                        timestamp = System.currentTimeMillis()
                    )
                    val insertedId = creationDao.insertCreation(entity)
                    val savedEntity = entity.copy(id = insertedId)
                    Result.success(Pair(savedEntity, bitmap))
                },
                onFailure = { error ->
                    Result.failure(error)
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Generates a Text-to-Video / Infinite Zoom Animation creation
     */
    suspend fun generateZoomVideo(
        prompt: String,
        style: ArtStyle,
        engine: InferenceEngine,
        aspectRatio: AspectRatioOption,
        zoomConfig: ZoomAnimationConfig,
        apiKey: String,
        seed: Long
    ): Result<Pair<CreationEntity, List<Bitmap>>> = withContext(Dispatchers.IO) {
        try {
            val enhancedPrompt = PromptEnhancerService.enhancePrompt(prompt, style)
            val resultBitmap = if (apiKey.isNotBlank()) {
                apiService.generateImageWithHuggingFace(
                    prompt = enhancedPrompt,
                    negativePrompt = style.negativePromptModifier,
                    modelEndpoint = engine.endpointUrl,
                    apiKey = apiKey,
                    seed = seed
                )
            } else {
                apiService.generateImageFastCloud(
                    prompt = enhancedPrompt,
                    width = aspectRatio.width,
                    height = aspectRatio.height,
                    seed = seed
                )
            }

            resultBitmap.fold(
                onSuccess = { baseBitmap ->
                    // Generate multi-stage zoom keyframes
                    val keyframes = BitmapZoomEngine.generateZoomKeyframes(baseBitmap, zoomConfig.depthStages)
                    val framePaths = mutableListOf<String>()

                    keyframes.forEachIndexed { index, frameBmp ->
                        val path = FileUtils.saveBitmapToInternalStorage(context, frameBmp, "zoom_frame_${index}")
                        framePaths.add(path)
                    }

                    val jsonPaths = JSONArray(framePaths).toString()
                    val entity = CreationEntity(
                        prompt = prompt,
                        enhancedPrompt = enhancedPrompt,
                        negativePrompt = style.negativePromptModifier,
                        styleId = style.id,
                        styleName = style.name,
                        modelName = "${engine.name} (AI Zoom)",
                        imageUrl = "",
                        localFilePath = framePaths.firstOrNull(),
                        isVideo = true,
                        videoType = zoomConfig.direction.name,
                        zoomFramesPaths = jsonPaths,
                        aspectRatio = aspectRatio.label,
                        seed = seed,
                        timestamp = System.currentTimeMillis()
                    )

                    val insertedId = creationDao.insertCreation(entity)
                    val savedEntity = entity.copy(id = insertedId)
                    Result.success(Pair(savedEntity, keyframes))
                },
                onFailure = { error ->
                    Result.failure(error)
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

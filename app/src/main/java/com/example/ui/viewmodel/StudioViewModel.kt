package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.CreationEntity
import com.example.data.remote.PromptEnhancerService
import com.example.data.repository.CreationRepository
import com.example.model.ArtStyle
import com.example.model.AspectRatioOption
import com.example.model.InferenceEngine
import com.example.model.ZoomAnimationConfig
import com.example.model.ZoomDirection
import com.example.util.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import kotlin.random.Random

sealed interface GenerationUiState {
    object Idle : GenerationUiState
    data class Generating(val stepMessage: String, val progress: Float) : GenerationUiState
    data class Success(val creation: CreationEntity, val bitmap: Bitmap?, val frames: List<Bitmap> = emptyList()) : GenerationUiState
    data class Error(val message: String) : GenerationUiState
}

enum class GalleryFilter {
    ALL, IMAGES, VIDEOS, FAVORITES
}

data class StudioState(
    val prompt: String = "Cyberpunk neon samurai cat perched on a rooftop in Neo-Tokyo",
    val negativePrompt: String = "",
    val selectedStyle: ArtStyle = ArtStyle.PRESETS.first(),
    val selectedAspectRatio: AspectRatioOption = AspectRatioOption.SQUARE_1_1,
    val selectedEngine: InferenceEngine = InferenceEngine.ENGINES.first(),
    val zoomConfig: ZoomAnimationConfig = ZoomAnimationConfig(),
    val autoEnhancePrompt: Boolean = true,
    val seed: Long = Random.nextLong(1, 999999),
    val randomSeedEnabled: Boolean = true,
    val apiKey: String = "",
    val galleryFilter: GalleryFilter = GalleryFilter.ALL,
    val searchQuery: String = "",
    val generationState: GenerationUiState = GenerationUiState.Idle,
    val selectedCreationDetail: CreationEntity? = null,
    val detailBitmaps: List<Bitmap> = emptyList(),
    val isPlayingZoom: Boolean = true,
    val zoomPlaybackSpeed: Float = 1.0f
)

class StudioViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = CreationRepository(application, database.creationDao())

    private val _studioState = MutableStateFlow(StudioState())
    val studioState: StateFlow<StudioState> = _studioState.asStateFlow()

    val allCreations: StateFlow<List<CreationEntity>> = repository.allCreations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredCreations: StateFlow<List<CreationEntity>> = combine(
        allCreations,
        _studioState
    ) { creations, state ->
        var list = when (state.galleryFilter) {
            GalleryFilter.ALL -> creations
            GalleryFilter.IMAGES -> creations.filter { !it.isVideo }
            GalleryFilter.VIDEOS -> creations.filter { it.isVideo }
            GalleryFilter.FAVORITES -> creations.filter { it.isFavorite }
        }
        if (state.searchQuery.isNotBlank()) {
            list = list.filter {
                it.prompt.contains(state.searchQuery, ignoreCase = true) ||
                it.styleName.contains(state.searchQuery, ignoreCase = true)
            }
        }
        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onPromptChange(newPrompt: String) {
        _studioState.update { it.copy(prompt = newPrompt) }
    }

    fun onNegativePromptChange(newNegative: String) {
        _studioState.update { it.copy(negativePrompt = newNegative) }
    }

    fun onStyleSelected(style: ArtStyle) {
        _studioState.update { it.copy(selectedStyle = style) }
    }

    fun onAspectRatioSelected(ratio: AspectRatioOption) {
        _studioState.update { it.copy(selectedAspectRatio = ratio) }
    }

    fun onEngineSelected(engine: InferenceEngine) {
        _studioState.update { it.copy(selectedEngine = engine) }
    }

    fun onZoomDirectionChanged(direction: ZoomDirection) {
        _studioState.update {
            it.copy(zoomConfig = it.zoomConfig.copy(direction = direction))
        }
    }

    fun onZoomSpeedChanged(speed: Float) {
        _studioState.update {
            it.copy(
                zoomPlaybackSpeed = speed,
                zoomConfig = it.zoomConfig.copy(speed = speed)
            )
        }
    }

    fun onZoomStagesChanged(stages: Int) {
        _studioState.update {
            it.copy(zoomConfig = it.zoomConfig.copy(depthStages = stages))
        }
    }

    fun toggleZoomPlayback() {
        _studioState.update { it.copy(isPlayingZoom = !it.isPlayingZoom) }
    }

    fun onRandomPromptDice() {
        val randomPrompt = PromptEnhancerService.getRandomPrompt()
        _studioState.update { it.copy(prompt = randomPrompt) }
    }

    fun toggleAutoEnhance() {
        _studioState.update { it.copy(autoEnhancePrompt = !it.autoEnhancePrompt) }
    }

    fun onApiKeyChanged(key: String) {
        _studioState.update { it.copy(apiKey = key) }
    }

    fun setGalleryFilter(filter: GalleryFilter) {
        _studioState.update { it.copy(galleryFilter = filter) }
    }

    fun onSearchQueryChanged(query: String) {
        _studioState.update { it.copy(searchQuery = query) }
    }

    fun generateImage() {
        val currentState = _studioState.value
        val effectiveSeed = if (currentState.randomSeedEnabled) Random.nextLong(1, 999999) else currentState.seed

        _studioState.update {
            it.copy(
                generationState = GenerationUiState.Generating("Synthesizing prompt with AI diffusion...", 0.25f),
                seed = effectiveSeed
            )
        }

        viewModelScope.launch {
            _studioState.update {
                it.copy(generationState = GenerationUiState.Generating("Denoising latent pixels with ${currentState.selectedEngine.name}...", 0.65f))
            }

            val result = repository.generateImage(
                prompt = currentState.prompt,
                negativePrompt = currentState.negativePrompt,
                style = currentState.selectedStyle,
                engine = currentState.selectedEngine,
                aspectRatio = currentState.selectedAspectRatio,
                apiKey = currentState.apiKey,
                seed = effectiveSeed,
                enhancePrompt = currentState.autoEnhancePrompt
            )

            result.fold(
                onSuccess = { (entity, bitmap) ->
                    _studioState.update {
                        it.copy(generationState = GenerationUiState.Success(entity, bitmap))
                    }
                },
                onFailure = { error ->
                    _studioState.update {
                        it.copy(generationState = GenerationUiState.Error(error.localizedMessage ?: "Generation failed"))
                    }
                }
            )
        }
    }

    fun generateZoomVideo() {
        val currentState = _studioState.value
        val effectiveSeed = if (currentState.randomSeedEnabled) Random.nextLong(1, 999999) else currentState.seed

        _studioState.update {
            it.copy(
                generationState = GenerationUiState.Generating("Generating base landscape keyframe...", 0.3f),
                seed = effectiveSeed
            )
        }

        viewModelScope.launch {
            _studioState.update {
                it.copy(generationState = GenerationUiState.Generating("Synthesizing recursive infinite zoom depth layers...", 0.7f))
            }

            val result = repository.generateZoomVideo(
                prompt = currentState.prompt,
                style = currentState.selectedStyle,
                engine = currentState.selectedEngine,
                aspectRatio = currentState.selectedAspectRatio,
                zoomConfig = currentState.zoomConfig,
                apiKey = currentState.apiKey,
                seed = effectiveSeed
            )

            result.fold(
                onSuccess = { (entity, frames) ->
                    _studioState.update {
                        it.copy(
                            generationState = GenerationUiState.Success(
                                creation = entity,
                                bitmap = frames.firstOrNull(),
                                frames = frames
                            )
                        )
                    }
                },
                onFailure = { error ->
                    _studioState.update {
                        it.copy(generationState = GenerationUiState.Error(error.localizedMessage ?: "Zoom Video generation failed"))
                    }
                }
            )
        }
    }

    fun selectCreationDetail(creation: CreationEntity) {
        viewModelScope.launch {
            val bitmaps = mutableListOf<Bitmap>()
            if (creation.isVideo && !creation.zoomFramesPaths.isNullOrEmpty()) {
                try {
                    val jsonArray = JSONArray(creation.zoomFramesPaths)
                    for (i in 0 until jsonArray.length()) {
                        val path = jsonArray.getString(i)
                        val bmp = FileUtils.loadBitmapFromPath(path)
                        if (bmp != null) bitmaps.add(bmp)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if (!creation.localFilePath.isNullOrEmpty()) {
                val bmp = FileUtils.loadBitmapFromPath(creation.localFilePath)
                if (bmp != null) bitmaps.add(bmp)
            }

            _studioState.update {
                it.copy(
                    selectedCreationDetail = creation,
                    detailBitmaps = bitmaps,
                    isPlayingZoom = true
                )
            }
        }
    }

    fun clearCreationDetail() {
        _studioState.update {
            it.copy(selectedCreationDetail = null, detailBitmaps = emptyList())
        }
    }

    fun toggleFavorite(creation: CreationEntity) {
        viewModelScope.launch {
            val newFav = !creation.isFavorite
            repository.toggleFavorite(creation.id, newFav)
            if (_studioState.value.selectedCreationDetail?.id == creation.id) {
                _studioState.update {
                    it.copy(selectedCreationDetail = it.selectedCreationDetail?.copy(isFavorite = newFav))
                }
            }
        }
    }

    fun deleteCreation(creation: CreationEntity) {
        viewModelScope.launch {
            repository.deleteCreation(creation)
            if (_studioState.value.selectedCreationDetail?.id == creation.id) {
                clearCreationDetail()
            }
            Toast.makeText(getApplication(), "Creation removed", Toast.LENGTH_SHORT).show()
        }
    }

    fun remixPrompt(creation: CreationEntity) {
        val matchedStyle = ArtStyle.PRESETS.find { it.id == creation.styleId } ?: ArtStyle.PRESETS.first()
        _studioState.update {
            it.copy(
                prompt = creation.prompt,
                negativePrompt = creation.negativePrompt,
                selectedStyle = matchedStyle,
                seed = creation.seed,
                randomSeedEnabled = false
            )
        }
        Toast.makeText(getApplication(), "Prompt loaded into Studio", Toast.LENGTH_SHORT).show()
    }

    fun exportToGallery(bitmap: Bitmap?, title: String) {
        if (bitmap == null) return
        viewModelScope.launch {
            val uri = FileUtils.exportBitmapToGallery(getApplication(), bitmap, title)
            withContext(Dispatchers.Main) {
                if (uri != null) {
                    Toast.makeText(getApplication(), "Saved to Pictures/AI_Studio!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(getApplication(), "Export failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun shareCreation(creation: CreationEntity, bitmap: Bitmap?) {
        if (bitmap == null) return
        FileUtils.shareImage(getApplication(), bitmap, creation.prompt)
    }
}

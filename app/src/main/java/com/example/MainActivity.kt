package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SlowMotionVideo
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.DetailScreen
import com.example.ui.screens.GalleryScreen
import com.example.ui.screens.GenerateScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.ZoomVideoScreen
import com.example.ui.theme.ImmersiveBackground
import com.example.ui.theme.ImmersiveOutlineVariant
import com.example.ui.theme.ImmersivePrimary
import com.example.ui.theme.ImmersivePrimaryContainer
import com.example.ui.theme.ImmersiveSurface
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.StudioViewModel

enum class StudioTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    GENERATE("Studio", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome, "tab_generate"),
    ZOOM_VIDEO("AI Zoom", Icons.Filled.SlowMotionVideo, Icons.Outlined.SlowMotionVideo, "tab_zoom_video"),
    GALLERY("Gallery", Icons.Filled.Collections, Icons.Outlined.Collections, "tab_gallery"),
    SETTINGS("Settings", Icons.Filled.Settings, Icons.Outlined.Settings, "tab_settings")
}

class MainActivity : ComponentActivity() {

    private val viewModel: StudioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainApp(viewModel: StudioViewModel) {
    val state by viewModel.studioState.collectAsStateWithLifecycle()
    val filteredCreations by viewModel.filteredCreations.collectAsStateWithLifecycle()
    var currentTab by remember { mutableStateOf(StudioTab.GENERATE) }

    // If detail view is open, show DetailScreen
    if (state.selectedCreationDetail != null) {
        DetailScreen(
            creation = state.selectedCreationDetail!!,
            bitmaps = state.detailBitmaps,
            isPlayingZoom = state.isPlayingZoom,
            onToggleZoomPlay = viewModel::toggleZoomPlayback,
            zoomSpeed = state.zoomPlaybackSpeed,
            onZoomSpeedChange = viewModel::onZoomSpeedChanged,
            onBack = viewModel::clearCreationDetail,
            onToggleFavorite = { viewModel.toggleFavorite(state.selectedCreationDetail!!) },
            onDelete = { viewModel.deleteCreation(state.selectedCreationDetail!!) },
            onRemixPrompt = {
                viewModel.remixPrompt(state.selectedCreationDetail!!)
                currentTab = StudioTab.GENERATE
            },
            onNavigateToZoom = {
                currentTab = StudioTab.ZOOM_VIDEO
                viewModel.clearCreationDetail()
            },
            onExport = viewModel::exportToGallery,
            onShare = viewModel::shareCreation,
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        )
        return
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        containerColor = ImmersiveBackground,
        bottomBar = {
            NavigationBar(
                containerColor = ImmersiveSurface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = ImmersiveOutlineVariant
                    )
                    .testTag("main_bottom_nav")
            ) {
                StudioTab.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.title,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ImmersivePrimary,
                            selectedTextColor = ImmersivePrimary,
                            indicatorColor = ImmersivePrimaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag(tab.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            ImmersiveBackground,
                            Color(0xFF140F1D),
                            ImmersiveBackground
                        )
                    )
                )
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "tab_transition"
            ) { targetTab ->
                when (targetTab) {
                    StudioTab.GENERATE -> {
                        GenerateScreen(
                            state = state,
                            onPromptChange = viewModel::onPromptChange,
                            onNegativePromptChange = viewModel::onNegativePromptChange,
                            onStyleSelected = viewModel::onStyleSelected,
                            onAspectRatioSelected = viewModel::onAspectRatioSelected,
                            onEngineSelected = viewModel::onEngineSelected,
                            onRandomPrompt = viewModel::onRandomPromptDice,
                            onToggleAutoEnhance = viewModel::toggleAutoEnhance,
                            onGenerate = viewModel::generateImage,
                            onOpenCreationDetail = viewModel::selectCreationDetail,
                            onNavigateToZoom = { currentTab = StudioTab.ZOOM_VIDEO },
                            onExport = viewModel::exportToGallery,
                            onShare = viewModel::shareCreation
                        )
                    }
                    StudioTab.ZOOM_VIDEO -> {
                        ZoomVideoScreen(
                            state = state,
                            onPromptChange = viewModel::onPromptChange,
                            onNegativePromptChange = viewModel::onNegativePromptChange,
                            onStyleSelected = viewModel::onStyleSelected,
                            onDirectionSelected = viewModel::onZoomDirectionChanged,
                            onSpeedChange = viewModel::onZoomSpeedChanged,
                            onStagesChange = viewModel::onZoomStagesChanged,
                            onTogglePlay = viewModel::toggleZoomPlayback,
                            onRandomPrompt = viewModel::onRandomPromptDice,
                            onToggleAutoEnhance = viewModel::toggleAutoEnhance,
                            onGenerateZoomVideo = viewModel::generateZoomVideo,
                            onOpenCreationDetail = viewModel::selectCreationDetail,
                            onExport = viewModel::exportToGallery,
                            onShare = viewModel::shareCreation
                        )
                    }
                    StudioTab.GALLERY -> {
                        GalleryScreen(
                            creations = filteredCreations,
                            activeFilter = state.galleryFilter,
                            onFilterChange = viewModel::setGalleryFilter,
                            searchQuery = state.searchQuery,
                            onSearchChange = viewModel::onSearchQueryChanged,
                            onCreationClick = viewModel::selectCreationDetail,
                            onToggleFavorite = viewModel::toggleFavorite,
                            onNavigateToCreate = { currentTab = StudioTab.GENERATE }
                        )
                    }
                    StudioTab.SETTINGS -> {
                        SettingsScreen(
                            apiKey = state.apiKey,
                            onApiKeyChange = viewModel::onApiKeyChanged
                        )
                    }
                }
            }
        }
    }
}

package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.DesignServices
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.AodPreviewScreen
import com.example.ui.screens.DesignTab
import com.example.ui.screens.DetailScreens
import com.example.ui.screens.DetailsTab
import com.example.ui.screens.HistoryTab
import com.example.ui.screens.UsageTab
import com.example.ui.screens.SettingsTab
import com.example.ui.theme.AmpereFlowTheme
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.AmpereFlowViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: AmpereFlowViewModel by viewModels()

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // Collect the customized color selection flow to apply theme wide
            val speedometerColorName by viewModel.preferences.speedometerColor.collectAsState()

            AmpereFlowTheme(accentColorName = speedometerColorName) {
                // Main screen router
                var currentRoute by remember { mutableStateOf("main") }
                var selectedTab by remember { mutableStateOf("details") }
                var activeDetailParam by remember { mutableStateOf("") }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DarkBg)
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                top = if (currentRoute == "aod") 0.dp else innerPadding.calculateTopPadding(),
                                bottom = if (currentRoute == "aod") 0.dp else innerPadding.calculateBottomPadding()
                            )
                    ) {
                        AnimatedContent(
                            targetState = currentRoute,
                            transitionSpec = { fadeIn(animationSpec = tween(300)) with fadeOut(animationSpec = tween(300)) },
                            label = "screen_navigation"
                        ) { targetRoute ->
                            when {
                                targetRoute == "main" -> {
                                    MainTabHost(
                                        viewModel = viewModel,
                                        selectedTab = selectedTab,
                                        onTabSelected = { selectedTab = it },
                                        onNavigateToDetail = { paramName ->
                                            activeDetailParam = paramName
                                            currentRoute = "detail"
                                        },
                                        onNavigateToAodPreview = {
                                            currentRoute = "aod"
                                        }
                                    )
                                }
                                targetRoute == "detail" -> {
                                    DetailScreens(
                                        viewModel = viewModel,
                                        screenType = activeDetailParam,
                                        onBack = { currentRoute = "main" }
                                    )
                                }
                                targetRoute == "aod" -> {
                                    AodPreviewScreen(
                                        viewModel = viewModel,
                                        onExit = { currentRoute = "main" }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainTabHost(
    viewModel: AmpereFlowViewModel,
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToAodPreview: () -> Unit
) {
    val speedometerColorName by viewModel.preferences.speedometerColor.collectAsState()
    val accentColor = when (speedometerColorName.lowercase()) {
        "green" -> com.example.ui.theme.AccentGreen
        "purple" -> Color(0xFFBB86FC)
        "indigo" -> Color(0xFF3F51B5)
        "cyan" -> Color(0xFF00E5FF)
        "magenta" -> Color(0xFFFF4081)
        "yellow" -> Color(0xFFFFEB3B)
        else -> com.example.ui.theme.AccentGreen
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Tab display region
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                "details" -> DetailsTab(viewModel = viewModel, onNavigateToDetail = onNavigateToDetail)
                "design" -> DesignTab(viewModel = viewModel, onNavigateToAodPreview = onNavigateToAodPreview)
                "history" -> HistoryTab(viewModel = viewModel)
                "usage" -> UsageTab(viewModel = viewModel)
                "settings" -> SettingsTab(viewModel = viewModel)
            }
        }

        // Custom high-fidelity material design bottom tab bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(DarkSurface),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BottomTabItem(
                label = "Details",
                icon = Icons.Default.ElectricBolt,
                isSelected = selectedTab == "details",
                accentColor = accentColor,
                onClick = { onTabSelected("details") },
                modifier = Modifier.testTag("tab_details")
            )
            BottomTabItem(
                label = "Design",
                icon = Icons.Default.DesignServices,
                isSelected = selectedTab == "design",
                accentColor = accentColor,
                onClick = { onTabSelected("design") },
                modifier = Modifier.testTag("tab_design")
            )
            BottomTabItem(
                label = "History",
                icon = Icons.Default.History,
                isSelected = selectedTab == "history",
                accentColor = accentColor,
                onClick = { onTabSelected("history") },
                modifier = Modifier.testTag("tab_history")
            )
            BottomTabItem(
                label = "Usage",
                icon = Icons.Default.AutoGraph,
                isSelected = selectedTab == "usage",
                accentColor = accentColor,
                onClick = { onTabSelected("usage") },
                modifier = Modifier.testTag("tab_usage")
            )
            BottomTabItem(
                label = "Settings",
                icon = Icons.Default.Settings,
                isSelected = selectedTab == "settings",
                accentColor = accentColor,
                onClick = { onTabSelected("settings") },
                modifier = Modifier.testTag("tab_settings")
            )
        }
    }
}

@Composable
fun BottomTabItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) accentColor else TextSecondary,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) accentColor else TextSecondary
            )
        )
    }
}


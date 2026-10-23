package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MultilineChart
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.EntryMenuDialog
import com.example.ui.components.LongevityEmojiAvatar
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DataInputWizardScreen
import com.example.ui.screens.ForecastAndReportScreen
import com.example.ui.screens.InteractiveTestSuiteScreen
import com.example.ui.screens.NemotronOcrScreen
import com.example.ui.screens.UserProfileScreen
import com.example.ui.screens.WhitepaperArchitectureScreen
import com.example.ui.theme.AppTheme
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.LongevityViewModel

enum class LongevityScreen(val title: String, val icon: ImageVector, val isPrimaryTab: Boolean) {
    DASHBOARD("Twin 3D", Icons.Default.ViewInAr, true),
    BIOMARKERS("Biomarkers", Icons.Default.Biotech, true),
    TESTS("Live Tests", Icons.Default.OpenInFull, true),
    FORECAST_REPORT("Forecast", Icons.AutoMirrored.Filled.MultilineChart, true),
    PROFILE("Profile", Icons.Default.Person, true),
    OCR("AI Vision OCR", Icons.Default.Biotech, false),
    WHITEPAPER("Whitepaper 2026", Icons.AutoMirrored.Filled.MultilineChart, false)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val longevityViewModel: LongevityViewModel = viewModel()
            val isDarkMode by longevityViewModel.isDarkMode.collectAsState()
            val isEntryMenuOpen by longevityViewModel.isEntryMenuOpen.collectAsState()
            val currentUser by longevityViewModel.currentUserProfile.collectAsState()
            val allUsers by longevityViewModel.allUsers.collectAsState()
            val twinResult by longevityViewModel.twinResult.collectAsState()

            // Status / navigation bar icons follow the in-app theme, not the system one
            DisposableEffect(isDarkMode) {
                val transparent = android.graphics.Color.TRANSPARENT
                val barStyle = if (isDarkMode) {
                    SystemBarStyle.dark(transparent)
                } else {
                    SystemBarStyle.light(transparent, transparent)
                }
                enableEdgeToEdge(statusBarStyle = barStyle, navigationBarStyle = barStyle)
                onDispose { }
            }

            MyApplicationTheme(darkTheme = isDarkMode) {
                val colors = AppTheme.colors
                var currentScreen by rememberSaveable { mutableStateOf(LongevityScreen.DASHBOARD) }
                var previousScreen by rememberSaveable { mutableStateOf(LongevityScreen.DASHBOARD) }

                val primaryTabs = listOf(
                    LongevityScreen.DASHBOARD,
                    LongevityScreen.BIOMARKERS,
                    LongevityScreen.TESTS,
                    LongevityScreen.FORECAST_REPORT,
                    LongevityScreen.PROFILE
                )

                // Entry / Login Menu Dialog
                if (isEntryMenuOpen) {
                    EntryMenuDialog(
                        currentUser = currentUser,
                        allUsers = allUsers,
                        twinResult = twinResult,
                        isDarkMode = isDarkMode,
                        onToggleDarkMode = { longevityViewModel.toggleDarkMode() },
                        onSwitchUser = { userId ->
                            longevityViewModel.switchUser(userId)
                            longevityViewModel.closeEntryMenu()
                        },
                        onAddNewUser = { newUser ->
                            longevityViewModel.addNewUser(newUser)
                            longevityViewModel.closeEntryMenu()
                        },
                        onDismiss = { longevityViewModel.closeEntryMenu() },
                        onNavigateToProfile = {
                            previousScreen = currentScreen
                            currentScreen = LongevityScreen.PROFILE
                        }
                    )
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = if (isDarkMode) colors.background else Color(0xFFD4E5ED),
                    topBar = {
                        if (!currentScreen.isPrimaryTab) {
                            // Top navigation bar for sub-screens (OCR & Whitepaper)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(colors.surface)
                                    .statusBarsPadding()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(colors.surfaceVariant)
                                        .clickable { currentScreen = previousScreen },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = colors.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Text(
                                    text = currentScreen.title,
                                    color = colors.textPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            // High-Fidelity Glassmorphism Top Header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Transparent)
                                    .statusBarsPadding()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // App Branding with Colorful DNA Badge
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.clickable {
                                        longevityViewModel.openEntryMenu()
                                    }
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(if (isDarkMode) Color(0xD9171E2D) else Color.White.copy(alpha = 0.88f))
                                            .border(1.2.dp, if (isDarkMode) Color(0x33FFFFFF) else Color.White.copy(alpha = 0.9f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "🧬", fontSize = 18.sp)
                                    }
                                    Column {
                                        Text(
                                            text = "LIFEMAP HUMAN™",
                                            fontSize = 15.5.sp,
                                            fontWeight = FontWeight.Black,
                                            color = if (isDarkMode) Color.White else Color(0xFF0F172A),
                                            letterSpacing = 0.5.sp
                                        )
                                        Text(
                                            text = "Digital Twin Engine",
                                            fontSize = 10.5.sp,
                                            color = if (isDarkMode) Color(0xFF00E5FF) else Color(0xFF0284C7),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                // Quick Controls: Light/Dark Sun Switcher + Doctor Pill Badge
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Circular Sun / Moon Theme Switcher
                                    IconButton(
                                        onClick = { longevityViewModel.toggleDarkMode() },
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(if (isDarkMode) Color(0xD9171E2D) else Color.White.copy(alpha = 0.88f))
                                            .border(1.2.dp, if (isDarkMode) Color(0x33FFFFFF) else Color.White.copy(alpha = 0.9f), CircleShape)
                                    ) {
                                        Text(
                                            text = if (isDarkMode) "🌙" else "☀️",
                                            fontSize = 17.sp
                                        )
                                    }

                                    // Frosted Doctor / Account Pill Badge
                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(if (isDarkMode) Color(0xD9171E2D) else Color.White.copy(alpha = 0.88f))
                                            .border(1.2.dp, if (isDarkMode) Color(0x33FFFFFF) else Color.White.copy(alpha = 0.9f), RoundedCornerShape(20.dp))
                                            .clickable { longevityViewModel.openEntryMenu() }
                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "🧬",
                                            fontSize = 15.sp
                                        )
                                        Text(
                                            text = "Dr.",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                                        )
                                    }
                                }
                            }
                        }
                    },
                    bottomBar = {
                        // High-Fidelity 5-Tab Glassmorphic Dock with Floating Neon Accent Bars
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                                .background(if (isDarkMode) Color(0xF2101726) else Color.White.copy(alpha = 0.92f))
                                .border(1.2.dp, if (isDarkMode) colors.cardBorder else Color.White.copy(alpha = 0.85f), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                                .navigationBarsPadding()
                                .padding(horizontal = 6.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val indicatorGradients = listOf(
                                    listOf(Color(0xFF00E5FF), Color(0xFFD946EF)), // Twin 3D
                                    listOf(Color(0xFF00E5FF), Color(0xFF2563EB)), // Biomarkers
                                    listOf(Color(0xFF10B981), Color(0xFF06B6D4)), // Live Tests
                                    listOf(Color(0xFFF97316), Color(0xFFFACC15)), // Forecast
                                    listOf(Color(0xFFEC4899), Color(0xFF8B5CF6))  // Profile
                                )

                                primaryTabs.forEachIndexed { index, screen ->
                                    val isSelected = currentScreen == screen
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier
                                            .clickable {
                                                previousScreen = currentScreen
                                                currentScreen = screen
                                            }
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        // Top glowing gradient accent pill
                                        Box(
                                            modifier = Modifier
                                                .width(36.dp)
                                                .height(4.dp)
                                                .clip(RoundedCornerShape(2.dp))
                                                .background(
                                                    if (isSelected) {
                                                        Brush.horizontalGradient(indicatorGradients[index % indicatorGradients.size])
                                                    } else {
                                                        Brush.horizontalGradient(indicatorGradients[index % indicatorGradients.size].map { it.copy(alpha = 0.35f) })
                                                    }
                                                )
                                        )

                                        Spacer(modifier = Modifier.height(2.dp))

                                        // Active pill container or icon
                                        if (isSelected) {
                                            Box(
                                                modifier = Modifier
                                                    .size(44.dp)
                                                    .clip(CircleShape)
                                                    .background((if (isDarkMode) Color(0xFF00E5FF) else Color(0xFF0284C7)).copy(alpha = 0.22f))
                                                    .border(1.4.dp, if (isDarkMode) Color(0xFF00E5FF) else Color(0xFF0284C7), CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (screen == LongevityScreen.PROFILE) {
                                                    Text(text = "🧬", fontSize = 20.sp)
                                                } else {
                                                    Icon(
                                                        imageVector = screen.icon,
                                                        contentDescription = screen.title,
                                                        tint = if (isDarkMode) Color(0xFF00E5FF) else Color(0xFF0284C7),
                                                        modifier = Modifier.size(22.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = screen.title,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Black,
                                                color = if (isDarkMode) Color(0xFF00E5FF) else Color(0xFF0284C7)
                                            )
                                        } else {
                                            Box(
                                                modifier = Modifier.size(44.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (screen == LongevityScreen.PROFILE) {
                                                    Text(text = "🧬", fontSize = 20.sp)
                                                } else {
                                                    Icon(
                                                        imageVector = screen.icon,
                                                        contentDescription = screen.title,
                                                        tint = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                                                        modifier = Modifier.size(22.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = screen.title,
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    // Animated Screen Transition Container
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            (fadeIn(animationSpec = tween(220)) +
                                    slideInHorizontally(animationSpec = tween(240)) { fullWidth -> fullWidth / 4 })
                                .togetherWith(
                                    fadeOut(animationSpec = tween(180)) +
                                            slideOutHorizontally(animationSpec = tween(200)) { fullWidth -> -fullWidth / 4 }
                                )
                        },
                        label = "ScreenNavigationTransition",
                        modifier = Modifier.padding(innerPadding)
                    ) { targetScreen ->
                        when (targetScreen) {
                            LongevityScreen.DASHBOARD -> DashboardScreen(
                                viewModel = longevityViewModel,
                                onNavigateToInput = {
                                    previousScreen = LongevityScreen.DASHBOARD
                                    currentScreen = LongevityScreen.BIOMARKERS
                                },
                                onNavigateToTests = {
                                    previousScreen = LongevityScreen.DASHBOARD
                                    currentScreen = LongevityScreen.TESTS
                                },
                                onNavigateToForecast = {
                                    previousScreen = LongevityScreen.DASHBOARD
                                    currentScreen = LongevityScreen.FORECAST_REPORT
                                },
                                onNavigateToOcr = {
                                    previousScreen = LongevityScreen.DASHBOARD
                                    currentScreen = LongevityScreen.OCR
                                },
                                onNavigateToWhitepaper = {
                                    previousScreen = LongevityScreen.DASHBOARD
                                    currentScreen = LongevityScreen.WHITEPAPER
                                }
                            )

                            LongevityScreen.BIOMARKERS -> DataInputWizardScreen(
                                viewModel = longevityViewModel,
                                onNavigateToOcr = {
                                    previousScreen = LongevityScreen.BIOMARKERS
                                    currentScreen = LongevityScreen.OCR
                                }
                            )

                            LongevityScreen.TESTS -> InteractiveTestSuiteScreen(
                                viewModel = longevityViewModel
                            )

                            LongevityScreen.FORECAST_REPORT -> ForecastAndReportScreen(
                                viewModel = longevityViewModel
                            )

                            LongevityScreen.PROFILE -> UserProfileScreen(
                                viewModel = longevityViewModel,
                                onOpenEntryMenu = { longevityViewModel.openEntryMenu() }
                            )

                            LongevityScreen.OCR -> NemotronOcrScreen(
                                viewModel = longevityViewModel,
                                onNavigateToDashboard = { currentScreen = LongevityScreen.DASHBOARD }
                            )

                            LongevityScreen.WHITEPAPER -> WhitepaperArchitectureScreen()
                        }
                    }
                }
            }
        }
    }
}

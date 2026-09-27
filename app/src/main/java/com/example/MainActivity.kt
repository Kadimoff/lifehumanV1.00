package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MultilineChart
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    TESTS("Live Tests", Icons.Default.FitnessCenter, true),
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

                // Entry / Login Menu Dialog (Giriş Menüsü)
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
                    containerColor = colors.background,
                    topBar = {
                        if (!currentScreen.isPrimaryTab) {
                            // Top navigation bar for sub-screens (OCR & Whitepaper)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(colors.surface)
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
                            // Persistent Top App Header with Theme Mode Switch & Giriş Menüsü Profile Trigger
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(colors.surface)
                                    .drawBehind {
                                        drawLine(
                                            color = colors.cardBorder,
                                            start = Offset(0f, size.height),
                                            end = Offset(size.width, size.height),
                                            strokeWidth = 1.dp.toPx()
                                        )
                                    }
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // App Branding with Emoji
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.clickable {
                                        longevityViewModel.openEntryMenu()
                                    }
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(colors.primaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "🧬", fontSize = 16.sp)
                                    }
                                    Column {
                                        Text(
                                            text = "LIFEMAP HUMAN™",
                                            fontSize = 14.5.sp,
                                            fontWeight = FontWeight.Black,
                                            color = colors.textPrimary,
                                            letterSpacing = 0.5.sp
                                        )
                                        Text(
                                            text = "Digital Twin Engine",
                                            fontSize = 9.5.sp,
                                            color = colors.primary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                // Quick Controls: Light/Dark Mode Toggle + Giriş Menüsü Avatar
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Light/Dark Theme Switcher Button
                                    IconButton(
                                        onClick = { longevityViewModel.toggleDarkMode() },
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(colors.surfaceVariant)
                                    ) {
                                        Icon(
                                            imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                                            contentDescription = if (isDarkMode) "Aydınlık Moda Geç" else "Karanlık Moda Geç",
                                            tint = if (isDarkMode) colors.primary else Color(0xFFD97706),
                                            modifier = Modifier.size(17.dp)
                                        )
                                    }

                                    // Giriş Menüsü Avatar / Account Trigger
                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(colors.surfaceVariant)
                                            .clickable { longevityViewModel.openEntryMenu() }
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = currentUser.avatarEmoji,
                                            fontSize = 16.sp
                                        )
                                        Text(
                                            text = currentUser.name.split(" ").firstOrNull() ?: "Profil",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = colors.textPrimary
                                        )
                                    }
                                }
                            }
                        }
                    },
                    bottomBar = {
                        // 5-Tab Dynamic Navigation Bar
                        NavigationBar(
                            modifier = Modifier.drawBehind {
                                drawLine(
                                    color = colors.cardBorder,
                                    start = Offset(0f, 0f),
                                    end = Offset(size.width, 0f),
                                    strokeWidth = 1.dp.toPx()
                                )
                            },
                            containerColor = colors.surface,
                            contentColor = colors.textPrimary,
                            tonalElevation = 0.dp
                        ) {
                            primaryTabs.forEach { screen ->
                                val isSelected = currentScreen == screen
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = {
                                        previousScreen = currentScreen
                                        currentScreen = screen
                                    },
                                    icon = {
                                        if (screen == LongevityScreen.PROFILE) {
                                            Text(
                                                text = currentUser.avatarEmoji,
                                                fontSize = 18.sp
                                            )
                                        } else {
                                            Icon(
                                                imageVector = screen.icon,
                                                contentDescription = screen.title,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    },
                                    label = {
                                        Text(
                                            text = screen.title,
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            maxLines = 1
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = colors.primary,
                                        selectedTextColor = colors.primary,
                                        indicatorColor = colors.primaryContainer,
                                        unselectedIconColor = colors.textMuted,
                                        unselectedTextColor = colors.textMuted
                                    )
                                )
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

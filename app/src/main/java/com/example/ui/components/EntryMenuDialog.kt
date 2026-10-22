package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.BiologicalTwinResult
import com.example.data.model.UserProfile
import com.example.ui.theme.AppTheme
import java.util.UUID

/**
 * Streamlined, intuitive entry & profile management hub (Entry Menu Dialog)
 */
@Composable
fun EntryMenuDialog(
    currentUser: UserProfile,
    allUsers: List<UserProfile>,
    twinResult: BiologicalTwinResult,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    onSwitchUser: (String) -> Unit,
    onAddNewUser: (UserProfile) -> Unit,
    onDismiss: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val colors = AppTheme.colors

    // 3 Segmented Tabs: "PROFILE" (My Profile), "SWITCH" (Switch Account), "SETTINGS" (Settings)
    var activeTab by remember { mutableStateOf("PROFILE") }

    // New User form state
    var showCreateForm by remember { mutableStateOf(false) }
    var newUserName by remember { mutableStateOf("") }
    var newUserAge by remember { mutableStateOf("35") }
    var newUserGoal by remember { mutableStateOf("Reverse My Biological Age by 5 Years") }
    var newUserSex by remember { mutableStateOf("Male") }
    var selectedNewAvatar by remember { mutableStateOf("🧬") }
    var showEmojiPicker by remember { mutableStateOf(false) }

    if (showEmojiPicker) {
        LongevityEmojiPickerDialog(
            currentEmoji = selectedNewAvatar,
            onEmojiSelected = { selectedNewAvatar = it.emoji },
            onDismiss = { showEmojiPicker = false }
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(26.dp))
                .border(
                    width = 1.2.dp,
                    brush = Brush.linearGradient(listOf(colors.primary, colors.indigo)),
                    shape = RoundedCornerShape(26.dp)
                ),
            color = colors.surface,
            tonalElevation = 10.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. TOP HEADER ROW (Clean, uncluttered)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(colors.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🧬", fontSize = 18.sp)
                        }
                        Column {
                            Text(
                                text = "LIFEMAP HUMAN™",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = colors.textPrimary,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "User & Profile Center",
                                fontSize = 10.5.sp,
                                color = colors.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = colors.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2. SEGMENTED NAVIGATION TABS (simple, selectable menu)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.surfaceVariant)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val tabs = listOf(
                        Triple("PROFILE", "My Profile", Icons.Default.Person),
                        Triple("SWITCH", "Accounts", Icons.Default.SwapHoriz),
                        Triple("SETTINGS", "Settings", Icons.Default.Settings)
                    )

                    tabs.forEach { (tabId, label, icon) ->
                        val isSelected = activeTab == tabId
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(9.dp))
                                .background(if (isSelected) colors.primary else Color.Transparent)
                                .clickable { activeTab = tabId }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = if (isSelected) colors.onPrimary else colors.textSecondary
                                )
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) colors.onPrimary else colors.textSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. TAB CONTENT
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (activeTab) {
                        // TAB 1: MY ACTIVE PROFILE
                        "PROFILE" -> {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant),
                                border = androidx.compose.foundation.BorderStroke(1.dp, colors.cardBorder)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        LongevityEmojiAvatar(
                                            emoji = currentUser.avatarEmoji,
                                            size = 52.dp,
                                            fontSize = 26f,
                                            badgeText = "Active"
                                        )

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = currentUser.name,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = colors.textPrimary
                                            )
                                            Text(
                                                text = "${currentUser.avatarBadgeTitle} • ${currentUser.chronologicalAge.toInt()} y/o",
                                                fontSize = 11.sp,
                                                color = colors.primary,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                text = "${currentUser.biologicalSex} • Blood: ${currentUser.bloodType}",
                                                fontSize = 10.5.sp,
                                                color = colors.textSecondary
                                            )
                                        }
                                    }

                                    // Key Metrics Quick Grid
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(colors.surfaceHighlight)
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("Biological Gap", fontSize = 10.sp, color = colors.textMuted)
                                            Text(
                                                text = String.format("%.1f yrs", twinResult.ageDifference),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (twinResult.ageDifference <= 0) colors.green else colors.coral
                                            )
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("LRS Score", fontSize = 10.sp, color = colors.textMuted)
                                            Text(
                                                text = "${twinResult.longevityReserveScore.toInt()}/100",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = colors.primary
                                            )
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("Active Streak", fontSize = 10.sp, color = colors.textMuted)
                                            Text(
                                                text = "🔥 ${currentUser.activeStreakDays} Days",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = colors.amber
                                            )
                                        }
                                    }

                                    // Longevity Goal
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(colors.surfaceHighlight.copy(alpha = 0.5f))
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = "🎯 Goal: ${currentUser.primaryLongevityGoal}",
                                            fontSize = 11.sp,
                                            color = colors.textSecondary,
                                            lineHeight = 15.sp
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            onDismiss()
                                            onNavigateToProfile()
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(42.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = colors.primary,
                                            contentColor = colors.onPrimary
                                        )
                                    ) {
                                        Icon(imageVector = Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Open & Edit User Profile", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // TAB 2: ACCOUNTS & SWITCH PROFILE
                        "SWITCH" -> {
                            Text(
                                text = "Saved Profiles (${allUsers.size})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textMuted
                            )

                            allUsers.forEach { user ->
                                val isCurrent = user.id == currentUser.id
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(if (isCurrent) colors.primary.copy(alpha = 0.12f) else colors.surfaceVariant)
                                        .border(
                                            1.dp,
                                            if (isCurrent) colors.primary else colors.cardBorder,
                                            RoundedCornerShape(14.dp)
                                        )
                                        .clickable {
                                            if (!isCurrent) onSwitchUser(user.id)
                                        }
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(colors.surfaceHighlight),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = user.avatarEmoji, fontSize = 20.sp)
                                        }

                                        Column {
                                            Text(
                                                text = user.name,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = colors.textPrimary
                                            )
                                            Text(
                                                text = "${user.avatarBadgeTitle} • ${user.chronologicalAge.toInt()} y/o",
                                                fontSize = 10.5.sp,
                                                color = colors.textSecondary
                                            )
                                        }
                                    }

                                    if (isCurrent) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(colors.primary)
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text("Active", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = colors.onPrimary)
                                        }
                                    } else {
                                        OutlinedButton(
                                            onClick = { onSwitchUser(user.id) },
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Text("Select", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = colors.primary)
                                        }
                                    }
                                }
                            }

                            // Add Profile Accordion
                            if (!showCreateForm) {
                                OutlinedButton(
                                    onClick = { showCreateForm = true },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(40.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null, tint = colors.primary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("+ Add New Profile", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = colors.primary)
                                }
                            } else {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .animateContentSize(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.primary)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text("New Profile Details", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colors.primary)

                                        OutlinedTextField(
                                            value = newUserName,
                                            onValueChange = { newUserName = it },
                                            label = { Text("Full Name", fontSize = 11.sp) },
                                            singleLine = true,
                                            modifier = Modifier.fillMaxWidth()
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            OutlinedTextField(
                                                value = newUserAge,
                                                onValueChange = { newUserAge = it },
                                                label = { Text("Age", fontSize = 11.sp) },
                                                singleLine = true,
                                                modifier = Modifier.weight(1f)
                                            )
                                            OutlinedTextField(
                                                value = newUserSex,
                                                onValueChange = { newUserSex = it },
                                                label = { Text("Sex", fontSize = 11.sp) },
                                                singleLine = true,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            OutlinedButton(
                                                onClick = { showCreateForm = false },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text("Cancel", fontSize = 11.sp)
                                            }

                                            Button(
                                                onClick = {
                                                    if (newUserName.isNotBlank()) {
                                                        val ageDouble = newUserAge.toDoubleOrNull() ?: 35.0
                                                        val created = UserProfile(
                                                            id = "user_${UUID.randomUUID().toString().take(6)}",
                                                            name = newUserName.trim(),
                                                            title = "Longevity Pioneer",
                                                            email = "${newUserName.lowercase().replace(" ", "")}@lifemap.ai",
                                                            avatarEmoji = selectedNewAvatar,
                                                            avatarBadgeTitle = "Digital Twin",
                                                            chronologicalAge = ageDouble,
                                                            biologicalSex = newUserSex,
                                                            primaryLongevityGoal = newUserGoal.ifBlank { "Reverse My Biological Age" },
                                                            activeStreakDays = 1,
                                                            unlockedBadges = listOf("🧬", "⏳", selectedNewAvatar)
                                                        )
                                                        onAddNewUser(created)
                                                        showCreateForm = false
                                                    }
                                                },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = colors.primary,
                                                    contentColor = colors.onPrimary
                                                )
                                            ) {
                                                Text("Save", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // TAB 3: HIZLI AYARLAR
                        "SETTINGS" -> {
                            // Dark/Light Theme Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(colors.surfaceVariant)
                                    .border(1.dp, colors.cardBorder, RoundedCornerShape(14.dp))
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(if (isDarkMode) Color(0xFF1E2838) else Color(0xFFFEF3C7)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                                            contentDescription = null,
                                            tint = if (isDarkMode) colors.primary else Color(0xFFD97706),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = if (isDarkMode) "Dark Theme (OLED)" else "Light Theme",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = colors.textPrimary
                                        )
                                        Text(
                                            text = if (isDarkMode) "High-contrast space mode active" else "Daylight mode active",
                                            fontSize = 10.5.sp,
                                            color = colors.textMuted
                                        )
                                    }
                                }

                                Switch(
                                    checked = isDarkMode,
                                    onCheckedChange = { onToggleDarkMode() },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = colors.primary,
                                        checkedTrackColor = colors.primaryContainer,
                                        uncheckedThumbColor = colors.secondary,
                                        uncheckedTrackColor = colors.surfaceHighlight
                                    )
                                )
                            }

                            // Health Connect Live Sync Badge
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(colors.surfaceVariant)
                                    .border(1.dp, colors.cardBorder, RoundedCornerShape(14.dp))
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(colors.green.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Sync,
                                        contentDescription = null,
                                        tint = colors.green,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Health Connect Biometrics",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary
                                    )
                                    Text(
                                        text = "Heart rate, steps and calories sync automatically",
                                        fontSize = 10.5.sp,
                                        color = colors.textMuted
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

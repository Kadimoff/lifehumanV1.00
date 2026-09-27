package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LongevityEmojiCatalog
import com.example.data.model.LongevityEmojiItem
import com.example.data.model.UserProfile
import com.example.ui.components.LongevityBadgeCard
import com.example.ui.components.LongevityEmojiAvatar
import com.example.ui.components.LongevityEmojiCard
import com.example.ui.components.LongevityEmojiPickerDialog
import com.example.ui.theme.AppTheme
import com.example.ui.viewmodel.LongevityViewModel

/**
 * Kullanıcı Profili ve Longevity Emojileri / Rozetleri Bölümü
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UserProfileScreen(
    viewModel: LongevityViewModel,
    onOpenEntryMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val currentUser by viewModel.currentUserProfile.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val twinResult by viewModel.twinResult.collectAsState()
    val biomarkerProfile by viewModel.biomarkerProfile.collectAsState()

    var isEditMode by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(currentUser.name) }
    var editAge by remember { mutableStateOf(currentUser.chronologicalAge.toString()) }
    var editGoal by remember { mutableStateOf(currentUser.primaryLongevityGoal) }
    var editBio by remember { mutableStateOf(currentUser.bio) }
    var showEmojiPickerDialog by remember { mutableStateOf(false) }
    var selectedEmojiDetail by remember { mutableStateOf<LongevityEmojiItem?>(null) }

    if (showEmojiPickerDialog) {
        LongevityEmojiPickerDialog(
            currentEmoji = currentUser.avatarEmoji,
            onEmojiSelected = { emojiItem ->
                viewModel.selectAvatarEmoji(emojiItem.emoji, emojiItem.titleTr)
            },
            onDismiss = { showEmojiPickerDialog = false }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Profile Top Action Bar (Giriş Menüsü & Tema Butonu)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Kullanıcı Profili",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = colors.textPrimary,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Biyolojik Kimlik & Longevity Emojileri",
                    fontSize = 11.5.sp,
                    color = colors.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Giriş Menüsü Butonu
                Button(
                    onClick = onOpenEntryMenu,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primaryContainer,
                        contentColor = colors.onPrimaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Login,
                        contentDescription = "Giriş Menüsü",
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Giriş Menüsü", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // Dark/Light Mode Switch Icon Button
                IconButton(
                    onClick = { viewModel.toggleDarkMode() },
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(colors.surfaceVariant)
                ) {
                    Icon(
                        imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                        contentDescription = "Tema Değiştir",
                        tint = if (isDarkMode) colors.primary else Color(0xFFD97706),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // 2. Main Profile Hero Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.linearGradient(listOf(colors.primary, colors.indigo, colors.cyan)),
                width = 1.5.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Avatar with interactive edit badge
                    Box(contentAlignment = Alignment.BottomEnd) {
                        LongevityEmojiAvatar(
                            emoji = currentUser.avatarEmoji,
                            size = 72.dp,
                            fontSize = 36f,
                            onClick = { showEmojiPickerDialog = true }
                        )

                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(colors.primary)
                                .clickable { showEmojiPickerDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Emoji Değiştir",
                                tint = colors.onPrimary,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = currentUser.name,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Verified",
                                tint = colors.amber,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Text(
                            text = currentUser.avatarBadgeTitle,
                            fontSize = 12.sp,
                            color = colors.primary,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "${currentUser.chronologicalAge.toInt()} Yaş • ${currentUser.biologicalSex} • ${currentUser.bloodType}",
                            fontSize = 11.sp,
                            color = colors.textSecondary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (twinResult.ageDifference <= 0) colors.green.copy(alpha = 0.2f) else colors.coral.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "PhenoAge: ${String.format("%.1f", twinResult.phenoAge)} Yıl (${if (twinResult.ageDifference <= 0) "" else "+"}${String.format("%.1f", twinResult.ageDifference)})",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (twinResult.ageDifference <= 0) colors.green else colors.coral
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(colors.primaryContainer)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "🔥 ${currentUser.activeStreakDays} Gün Seri",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.onPrimaryContainer
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bio & Goal
                if (!isEditMode) {
                    Text(
                        text = "🎯 Ana Hedef: ${currentUser.primaryLongevityGoal}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = currentUser.bio,
                        fontSize = 11.sp,
                        color = colors.textSecondary,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                editName = currentUser.name
                                editAge = currentUser.chronologicalAge.toInt().toString()
                                editGoal = currentUser.primaryLongevityGoal
                                editBio = currentUser.bio
                                isEditMode = true
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Düzenle",
                                tint = colors.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Profili Düzenle", fontSize = 11.5.sp, color = colors.primary)
                        }

                        Button(
                            onClick = { showEmojiPickerDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.primary,
                                contentColor = colors.onPrimary
                            )
                        ) {
                            Text("🧬 Avatar Seç", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    // Profile Edit Form
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text("Ad Soyad", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colors.primary,
                                unfocusedBorderColor = colors.cardBorder
                            )
                        )

                        OutlinedTextField(
                            value = editAge,
                            onValueChange = { editAge = it },
                            label = { Text("Takvim Yaşı (Chrono Age)", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colors.primary,
                                unfocusedBorderColor = colors.cardBorder
                            )
                        )

                        OutlinedTextField(
                            value = editGoal,
                            onValueChange = { editGoal = it },
                            label = { Text("Longevity Hedefi", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colors.primary,
                                unfocusedBorderColor = colors.cardBorder
                            )
                        )

                        OutlinedTextField(
                            value = editBio,
                            onValueChange = { editBio = it },
                            label = { Text("Hakkımda / Protokol Notları", fontSize = 11.sp) },
                            maxLines = 3,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colors.primary,
                                unfocusedBorderColor = colors.cardBorder
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { isEditMode = false },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Vazgeç", fontSize = 11.sp, color = colors.textSecondary)
                            }

                            Button(
                                onClick = {
                                    val age = editAge.toDoubleOrNull() ?: currentUser.chronologicalAge
                                    val updated = currentUser.copy(
                                        name = editName.trim(),
                                        chronologicalAge = age,
                                        primaryLongevityGoal = editGoal.trim(),
                                        bio = editBio.trim()
                                    )
                                    viewModel.updateUserProfile(updated)
                                    isEditMode = false
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colors.primary,
                                    contentColor = colors.onPrimary
                                )
                            ) {
                                Text("Kaydet", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 3. Theme & Preferences Card (Aydınlık & Karanlık Mod)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.linearGradient(listOf(colors.cardBorder, colors.borderSubtle)),
                width = 1.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Ayarlar",
                        tint = colors.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Tema & Uygulama Tercihleri",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                }

                // Dark / Light Mode Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.surfaceVariant)
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
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (isDarkMode) Color(0xFF2C243B) else Color(0xFFFFF3CD)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = "Theme",
                                tint = if (isDarkMode) colors.primary else Color(0xFFD97706),
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Column {
                            Text(
                                text = if (isDarkMode) "Karanlık Mod (Dark Mode)" else "Aydınlık Mod (Light Mode)",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                            Text(
                                text = if (isDarkMode) "OLED Gece Koruma Teması" else "Parlak Gün Işığı Teması",
                                fontSize = 10.sp,
                                color = colors.textMuted
                            )
                        }
                    }

                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { viewModel.toggleDarkMode() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = colors.primary,
                            checkedTrackColor = colors.primaryContainer,
                            uncheckedThumbColor = colors.secondary,
                            uncheckedTrackColor = colors.surfaceHighlight
                        )
                    )
                }

                // Metric / Imperial unit toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.surfaceVariant)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Ölçü Birimleri",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = if (currentUser.isMetricUnits) "Metrik Sistem (kg, cm, mg/dL)" else "Imperial (lbs, in)",
                            fontSize = 10.sp,
                            color = colors.textMuted
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.primaryContainer)
                            .clickable {
                                viewModel.updateUserProfile(currentUser.copy(isMetricUnits = !currentUser.isMetricUnits))
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (currentUser.isMetricUnits) "METRİK" else "IMPERIAL",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.onPrimaryContainer
                        )
                    }
                }
            }
        }

        // 4. In-App Longevity Emojis Catalog (Uygulama İçi Emojiler Vitrini)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.linearGradient(listOf(colors.cardBorder, colors.borderSubtle)),
                width = 1.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "🧬", fontSize = 18.sp)
                        Column {
                            Text(
                                text = "Longevity Emojileri Vitrini",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                            Text(
                                text = "Avatarınızı değiştirmek için herhangi bir emojiye dokunun",
                                fontSize = 10.5.sp,
                                color = colors.textMuted
                            )
                        }
                    }

                    Text(
                        text = "${LongevityEmojiCatalog.allEmojis.size} Emoji",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary
                    )
                }

                // Horizontal Quick Emoji Carousel
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(LongevityEmojiCatalog.allEmojis) { emojiItem ->
                        val isSelected = currentUser.avatarEmoji == emojiItem.emoji
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) colors.primaryContainer else colors.surfaceVariant)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) colors.primary else colors.cardBorder,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    viewModel.selectAvatarEmoji(emojiItem.emoji, emojiItem.titleTr)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emojiItem.emoji, fontSize = 26.sp)
                        }
                    }
                }

                // Detailed Grid of In-App Emojis
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    maxItemsInEachRow = 2
                ) {
                    LongevityEmojiCatalog.allEmojis.take(6).forEach { emojiItem ->
                        LongevityEmojiCard(
                            item = emojiItem,
                            isSelected = currentUser.avatarEmoji == emojiItem.emoji,
                            onClick = {
                                viewModel.selectAvatarEmoji(emojiItem.emoji, emojiItem.titleTr)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Button(
                    onClick = { showEmojiPickerDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.surfaceVariant,
                        contentColor = colors.primary
                    )
                ) {
                    Text(
                        text = "Tüm ${LongevityEmojiCatalog.allEmojis.size} Longevity Emojisini Gör & Seç",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 5. Longevity Rozetleri & Başarılar (Milestone Badges)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.linearGradient(listOf(colors.cardBorder, colors.borderSubtle)),
                width = 1.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Başarılar",
                        tint = colors.amber,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Longevity Başarı Rozetleri",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                }

                LongevityEmojiCatalog.achievementBadges.forEach { badge ->
                    LongevityBadgeCard(badge = badge)
                }
            }
        }

        // 6. Hazır Biyo-Profil Şablonları Yükleyici
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.linearGradient(listOf(colors.cardBorder, colors.borderSubtle)),
                width = 1.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Hazır Profiller",
                        tint = colors.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Klinik Simülasyon Biyo-Profilleri",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                }

                viewModel.presetProfiles.forEach { preset ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { viewModel.applyPreset(preset.id) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = preset.title,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary
                                )
                                Text(
                                    text = preset.subtitle,
                                    fontSize = 10.5.sp,
                                    color = colors.textSecondary
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(colors.primaryContainer)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = preset.tag,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

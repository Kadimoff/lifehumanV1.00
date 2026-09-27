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
import com.example.data.LongevityEmojiCatalog
import com.example.data.model.BiologicalTwinResult
import com.example.data.model.UserProfile
import com.example.ui.theme.AppTheme
import java.util.UUID

/**
 * Giriş Menüsü (Login / Persona Switcher / Welcome Entry Screen)
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
    var showCreateForm by remember { mutableStateOf(false) }
    var newUserName by remember { mutableStateOf("") }
    var newUserAge by remember { mutableStateOf("35") }
    var newUserGoal by remember { mutableStateOf("Biyolojik Yaşımı 5 Yıl Gençleştirmek") }
    var newUserSex by remember { mutableStateOf("Erkek") }
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
                .clip(RoundedCornerShape(28.dp))
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(listOf(colors.primary, colors.indigo)),
                    shape = RoundedCornerShape(28.dp)
                ),
            color = colors.surface,
            tonalElevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header Row
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
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(colors.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🧬", fontSize = 18.sp)
                        }
                        Column {
                            Text(
                                text = "LIFEMAP HUMAN™",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = colors.textPrimary,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Giriş Menüsü & Profil Yönetimi",
                                fontSize = 11.sp,
                                color = colors.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Kapat",
                            tint = colors.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Active User Card (Giriş Yapılmış Profil)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colors.surfaceVariant
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(listOf(colors.primary, colors.cyan)),
                        width = 1.2.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            LongevityEmojiAvatar(
                                emoji = currentUser.avatarEmoji,
                                size = 52.dp,
                                fontSize = 26f,
                                badgeText = "Aktif"
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = currentUser.name,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary
                                    )
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = "Active",
                                        tint = colors.amber,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Text(
                                    text = currentUser.avatarBadgeTitle,
                                    fontSize = 11.sp,
                                    color = colors.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${currentUser.chronologicalAge.toInt()} Yaş • ${currentUser.biologicalSex} • ${currentUser.bloodType}",
                                    fontSize = 10.5.sp,
                                    color = colors.textSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Quick Twin Stats Row
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
                                Text(
                                    text = "Biyolojik Fark",
                                    fontSize = 10.sp,
                                    color = colors.textMuted
                                )
                                Text(
                                    text = String.format("%.1f yıl", twinResult.ageDifference),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (twinResult.ageDifference <= 0) colors.green else colors.coral
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Longevity Skoru",
                                    fontSize = 10.sp,
                                    color = colors.textMuted
                                )
                                Text(
                                    text = "${twinResult.longevityReserveScore.toInt()} / 100",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.primary
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Seri",
                                    fontSize = 10.sp,
                                    color = colors.textMuted
                                )
                                Text(
                                    text = "🔥 ${currentUser.activeStreakDays} Gün",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.amber
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                onDismiss()
                                onNavigateToProfile()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.primary,
                                contentColor = colors.onPrimary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profil",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Kullanıcı Profilini Aç",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Light / Dark Mode Switcher Row
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colors.surfaceVariant
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(listOf(colors.cardBorder, colors.borderSubtle)),
                        width = 1.dp
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
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
                                    .background(if (isDarkMode) Color(0xFF2C243B) else Color(0xFFFFF3CD)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                                    contentDescription = "Theme",
                                    tint = if (isDarkMode) colors.primary else Color(0xFFD97706),
                                    modifier = Modifier.size(18.dp)
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
                                    text = if (isDarkMode) "Aktif: OLED Göz Korumalı Tema" else "Aktif: Parlak Yüksek Kontrast Tema",
                                    fontSize = 10.sp,
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
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Profiles / Accounts List (Hesap Değiştir)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hazır Hesaplar & Profiller",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )

                    Text(
                        text = "${allUsers.size} Profil",
                        fontSize = 11.sp,
                        color = colors.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    allUsers.forEach { user ->
                        val isCurrent = user.id == currentUser.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    if (!isCurrent) {
                                        onSwitchUser(user.id)
                                    }
                                },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCurrent) colors.primaryContainer.copy(alpha = 0.45f) else colors.surfaceVariant
                            ),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.linearGradient(
                                    colors = if (isCurrent) listOf(colors.primary, colors.indigo)
                                    else listOf(colors.cardBorder, colors.borderSubtle)
                                ),
                                width = if (isCurrent) 1.5.dp else 1.dp
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
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

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = user.name,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary
                                    )
                                    Text(
                                        text = "${user.avatarBadgeTitle} • ${user.chronologicalAge.toInt()} Yaş",
                                        fontSize = 10.5.sp,
                                        color = colors.textSecondary
                                    )
                                }

                                if (isCurrent) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(colors.primary)
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "Giriş Yapıldı",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = colors.onPrimary
                                        )
                                    }
                                } else {
                                    OutlinedButton(
                                        onClick = { onSwitchUser(user.id) },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text(
                                            text = "Seç",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = colors.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Create New Profile Button / Form Toggle
                if (!showCreateForm) {
                    OutlinedButton(
                        onClick = { showCreateForm = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "Yeni Profil",
                            tint = colors.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "+ Yeni Dijital İkiz Profili Oluştur",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.primary
                        )
                    }
                } else {
                    // Create Form Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = colors.surfaceVariant
                        ),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.linearGradient(listOf(colors.primary, colors.indigo)),
                            width = 1.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Yeni Kullanıcı Profili",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.primary
                            )

                            // Avatar picker trigger
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(colors.primaryContainer)
                                        .clickable { showEmojiPicker = true },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = selectedNewAvatar, fontSize = 22.sp)
                                }
                                Column {
                                    Text(
                                        text = "Avatar Emojisi",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary
                                    )
                                    Text(
                                        text = "Emojiyi değiştirmek için dokunun",
                                        fontSize = 9.5.sp,
                                        color = colors.primary,
                                        modifier = Modifier.clickable { showEmojiPicker = true }
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = newUserName,
                                onValueChange = { newUserName = it },
                                label = { Text("Ad Soyad / Takma Ad", fontSize = 11.sp) },
                                singleLine = true,
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
                                OutlinedTextField(
                                    value = newUserAge,
                                    onValueChange = { newUserAge = it },
                                    label = { Text("Takvim Yaşı", fontSize = 11.sp) },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = colors.primary,
                                        unfocusedBorderColor = colors.cardBorder
                                    )
                                )

                                OutlinedTextField(
                                    value = newUserSex,
                                    onValueChange = { newUserSex = it },
                                    label = { Text("Biyolojik Cinsiyet", fontSize = 11.sp) },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = colors.primary,
                                        unfocusedBorderColor = colors.cardBorder
                                    )
                                )
                            }

                            OutlinedTextField(
                                value = newUserGoal,
                                onValueChange = { newUserGoal = it },
                                label = { Text("Ana Longevity Hedefi", fontSize = 11.sp) },
                                singleLine = true,
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
                                    onClick = { showCreateForm = false },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("İptal", fontSize = 11.sp, color = colors.textSecondary)
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
                                                avatarBadgeTitle = "Dijital İkiz",
                                                chronologicalAge = ageDouble,
                                                biologicalSex = newUserSex,
                                                primaryLongevityGoal = newUserGoal.ifBlank { "Biyolojik Yaşımı Gençleştirmek" },
                                                activeStreakDays = 1,
                                                unlockedBadges = listOf("🧬", "⏳", selectedNewAvatar)
                                            )
                                            onAddNewUser(created)
                                            showCreateForm = false
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = colors.primary,
                                        contentColor = colors.onPrimary
                                    )
                                ) {
                                    Text("Kaydet & Giriş Yap", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

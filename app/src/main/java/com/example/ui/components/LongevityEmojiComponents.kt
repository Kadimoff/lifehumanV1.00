package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.LongevityEmojiCatalog
import com.example.data.model.LongevityBadge
import com.example.data.model.LongevityEmojiItem
import com.example.ui.theme.AppTheme

/**
 * High-fidelity animated Emoji Avatar bubble with longevity glow effect.
 */
@Composable
fun LongevityEmojiAvatar(
    emoji: String,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    fontSize: Float = 32f,
    onClick: (() -> Unit)? = null,
    showGlow: Boolean = true,
    badgeText: String? = null
) {
    val colors = AppTheme.colors
    val infiniteTransition = rememberInfiniteTransition(label = "EmojiGlow")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )

    Box(
        modifier = modifier
            .size(size)
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        // Outer pulsing ring
        if (showGlow) {
            Box(
                modifier = Modifier
                    .size(size)
                    .scale(glowScale)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                colors.primary.copy(alpha = 0.35f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        // Inner circle container
        Box(
            modifier = Modifier
                .size(size * 0.88f)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            colors.surfaceVariant,
                            colors.surfaceHighlight
                        )
                    )
                )
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            colors.primary,
                            colors.indigo
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emoji,
                fontSize = fontSize.sp,
                textAlign = TextAlign.Center
            )
        }

        // Optional mini indicator badge
        if (badgeText != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .clip(RoundedCornerShape(6.dp))
                    .background(colors.primary)
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(
                    text = badgeText,
                    color = colors.onPrimary,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Interactive Longevity Emoji Item Card with bilingual labels and meaning.
 */
@Composable
fun LongevityEmojiCard(
    item: LongevityEmojiItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) colors.primaryContainer.copy(alpha = 0.5f) else colors.surfaceVariant
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                colors = if (isSelected) listOf(colors.primary, colors.indigo)
                else listOf(colors.cardBorder, colors.borderSubtle)
            ),
            width = if (isSelected) 2.dp else 1.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(colors.surfaceHighlight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.emoji,
                    fontSize = 26.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.titleTr,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Text(
                text = item.categoryTr,
                fontSize = 10.sp,
                color = colors.primary,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = item.meaningTr,
                fontSize = 9.5.sp,
                color = colors.textMuted,
                textAlign = TextAlign.Center,
                lineHeight = 12.sp,
                maxLines = 2
            )

            if (isSelected) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = colors.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Seçildi",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary
                    )
                }
            }
        }
    }
}

/**
 * Modal Dialog for picking a Longevity Avatar Emoji.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LongevityEmojiPickerDialog(
    currentEmoji: String,
    onEmojiSelected: (LongevityEmojiItem) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = AppTheme.colors
    var selectedCategory by remember { mutableStateOf("Tümü") }
    val categories = listOf("Tümü", "Hücresel", "Kardiyovasküler", "Metabolizma", "Bilişsel", "Bağışıklık", "Fiziksel", "Başarı")

    val filteredEmojis = remember(selectedCategory) {
        if (selectedCategory == "Tümü") LongevityEmojiCatalog.allEmojis
        else LongevityEmojiCatalog.allEmojis.filter { it.categoryTr.equals(selectedCategory, ignoreCase = true) }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp)),
            color = colors.surface,
            tonalElevation = 8.dp,
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.linearGradient(listOf(colors.primary, colors.indigo)),
                width = 1.5.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "🧬", fontSize = 22.sp)
                        Column {
                            Text(
                                text = "Longevity Avatar & Emojileri",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                            Text(
                                text = "Biyolojik dijital ikiz avatarınızı seçin",
                                fontSize = 11.sp,
                                color = colors.textMuted
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Kapat",
                            tint = colors.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Category Filter Pills
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { category ->
                        val isSelected = selectedCategory == category
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (isSelected) colors.primary else colors.surfaceVariant
                                )
                                .clickable { selectedCategory = category }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = category,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) colors.onPrimary else colors.textSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Emoji Grid
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        maxItemsInEachRow = 2
                    ) {
                        filteredEmojis.forEach { emojiItem ->
                            LongevityEmojiCard(
                                item = emojiItem,
                                isSelected = currentEmoji == emojiItem.emoji,
                                onClick = {
                                    onEmojiSelected(emojiItem)
                                    onDismiss()
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Achievement Badge Item with icon, title, and unlock status.
 */
@Composable
fun LongevityBadgeCard(
    badge: LongevityBadge,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val colors = AppTheme.colors

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        colors = CardDefaults.cardColors(
            containerColor = if (badge.isUnlocked) colors.surfaceVariant else colors.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                colors = if (badge.isUnlocked) listOf(colors.primary.copy(alpha = 0.6f), colors.indigo.copy(alpha = 0.4f))
                else listOf(colors.cardBorder, colors.borderSubtle)
            ),
            width = 1.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (badge.isUnlocked) colors.surfaceHighlight else colors.cardBorder.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                if (badge.isUnlocked) {
                    Text(text = badge.emoji, fontSize = 22.sp)
                } else {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Kilitli",
                        tint = colors.textMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = badge.titleTr,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (badge.isUnlocked) colors.textPrimary else colors.textMuted
                    )
                    if (badge.isUnlocked) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Açıldı",
                            tint = colors.amber,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = badge.descriptionTr,
                    fontSize = 11.sp,
                    color = colors.textSecondary,
                    lineHeight = 14.sp
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "Gereksinim: ${badge.requirement}",
                    fontSize = 9.5.sp,
                    color = if (badge.isUnlocked) colors.green else colors.textMuted,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

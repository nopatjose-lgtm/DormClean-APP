package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlaylistAddCheck
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PlaylistAddCheck
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AppMode
import com.example.data.MainTab
import com.example.ui.theme.CardSubtleBorder
import com.example.ui.theme.CardWhite
import com.example.ui.theme.ChampagneGlow
import com.example.ui.theme.FrostedHeaderWhite
import com.example.ui.theme.GCashBlue
import com.example.ui.theme.GlassWhite
import com.example.ui.theme.InkMuted
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.InkSecondary
import com.example.ui.theme.MaroonBright
import com.example.ui.theme.MaroonDeep
import com.example.ui.theme.MaroonPrimary
import com.example.ui.theme.MaroonSoftBorder
import com.example.ui.theme.MaroonTint
import com.example.ui.theme.PillTrackBg
import com.example.ui.theme.RoseMistGlow
import com.example.ui.theme.SoftInputBg
import com.example.ui.theme.SoftInputBorder
import com.example.ui.theme.SpecularWhiteBorder
import com.example.ui.theme.WarmCreamBg
import com.example.ui.theme.WarmPeachGlow

/**
 * Tactile Apple-style clickable modifier with spring press bounce, luminous border flash, and visible ripple.
 */
fun Modifier.appleTactileClickable(
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(20.dp),
    highlightColor: Color = MaroonPrimary,
    pressedScale: Float = 0.968f,
    onClick: () -> Unit
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) pressedScale else 1f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMedium),
        label = "applePressScale"
    )
    this
        .scale(scale)
        .clip(shape)
        .then(
            if (isPressed && enabled) {
                Modifier.border(1.5.dp, highlightColor.copy(alpha = 0.55f), shape)
            } else {
                Modifier
            }
        )
        .clickable(
            interactionSource = interactionSource,
            indication = ripple(bounded = true, color = highlightColor),
            enabled = enabled,
            onClick = onClick
        )
}

/**
 * Compact active highlight pill shown on selected cards/rows for unmistakable visual confirmation.
 */
@Composable
fun SelectedHighlightBadge(
    label: String = "Selected",
    lightOnDark: Boolean = true,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (lightOnDark) Color.White.copy(alpha = 0.22f) else MaroonPrimary,
        border = if (lightOnDark) BorderStroke(0.8.dp, Color.White.copy(alpha = 0.4f)) else null,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(11.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}

/**
 * Unified DormClean rounded text field with crisp contrast and consistent focus highlight.
 */
@Composable
fun DormCleanTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    modifier: Modifier = Modifier,
    label: String? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else 4,
    accentColor: Color = MaroonPrimary,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = label?.let { { Text(it, maxLines = 1, overflow = TextOverflow.Ellipsis) } },
        placeholder = {
            if (placeholder.isNotEmpty()) {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkMuted,
                    maxLines = if (singleLine) 1 else 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        leadingIcon = leadingIcon,
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = InkPrimary),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = CardWhite,
            unfocusedContainerColor = SoftInputBg,
            focusedBorderColor = accentColor,
            unfocusedBorderColor = SoftInputBorder
        ),
        modifier = modifier
    )
}

/**
 * Apple-style ambient multi-radial mesh backdrop behind all screens.
 */
@Composable
fun DormCleanAmbientBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(WarmCreamBg)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(WarmPeachGlow.copy(alpha = 0.85f), Color.Transparent),
                    center = Offset(size.width * 0.15f, size.height * 0.08f),
                    radius = size.width * 0.95f
                )
            )
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(RoseMistGlow.copy(alpha = 0.65f), Color.Transparent),
                    center = Offset(size.width * 0.92f, size.height * 0.22f),
                    radius = size.width * 0.85f
                )
            )
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(ChampagneGlow.copy(alpha = 0.70f), Color.Transparent),
                    center = Offset(size.width * 0.5f, size.height * 0.72f),
                    radius = size.width * 1.1f
                )
            )
        }
        content()
    }
}

/**
 * Tactile Apple-style glossy gradient CTA button with press spring, specular highlight & shadow.
 */
@Composable
fun AppleGradientButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    height: Dp = 52.dp,
    gradientColors: List<Color> = listOf(MaroonBright, MaroonPrimary, MaroonDeep),
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(26.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMedium),
        label = "buttonPressScale"
    )
    Button(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactionSource,
        shape = shape,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            contentColor = Color.White,
            disabledContentColor = Color.White.copy(alpha = 0.7f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        ),
        modifier = modifier
            .scale(scale)
            .height(height)
            .shadow(
                elevation = if (enabled) (if (isPressed) 4.dp else 10.dp) else 0.dp,
                shape = shape,
                ambientColor = gradientColors.first().copy(alpha = 0.45f),
                spotColor = gradientColors.first().copy(alpha = 0.45f)
            )
            .clip(shape)
            .background(
                brush = if (enabled) {
                    Brush.linearGradient(colors = gradientColors)
                } else {
                    Brush.linearGradient(
                        colors = listOf(
                            MaroonPrimary.copy(alpha = 0.42f),
                            MaroonDeep.copy(alpha = 0.42f)
                        )
                    )
                },
                shape = shape
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White.copy(alpha = 0.4f), Color.Transparent)
                ),
                shape = shape
            )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            content()
        }
    }
}

/**
 * Frosted Apple-style elevated card container with crisp hairline border and subtle ambient shadow.
 */
@Composable
fun AppleGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    containerBrush: Brush = Brush.verticalGradient(
        colors = listOf(CardWhite, GlassWhite)
    ),
    borderColor: Color = CardSubtleBorder,
    elevation: Dp = 6.dp,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = MaroonPrimary.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.07f)
            )
            .clip(shape)
            .background(containerBrush, shape)
            .border(1.dp, borderColor, shape)
    ) {
        content()
    }
}

@Composable
fun DormCleanTopChrome(
    firstName: String,
    userEmail: String,
    isAdminUser: Boolean,
    appMode: AppMode,
    onSelectMode: (AppMode) -> Unit,
    onProfilePillClick: () -> Unit
) {
    Surface(
        color = FrostedHeaderWhite,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .shadow(
                                elevation = 6.dp,
                                shape = RoundedCornerShape(11.dp),
                                spotColor = MaroonPrimary.copy(alpha = 0.35f)
                            )
                            .clip(RoundedCornerShape(11.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_dormclean_mark),
                            contentDescription = "DormClean Icon",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "DormClean",
                            style = MaterialTheme.typography.titleLarge,
                            color = InkPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1
                        )
                        Text(
                            text = "Daet, Cam. Norte",
                            style = MaterialTheme.typography.labelSmall,
                            color = InkSecondary,
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaroonTint,
                    border = BorderStroke(1.dp, MaroonSoftBorder),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .appleTactileClickable(
                            shape = RoundedCornerShape(16.dp),
                            highlightColor = MaroonPrimary,
                            onClick = onProfilePillClick
                        )
                        .testTag("top_profile_pill")
                ) {
                    Text(
                        text = firstName,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaroonPrimary,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                    )
                }
            }

            HorizontalDivider(color = CardSubtleBorder.copy(alpha = 0.7f), thickness = 0.8.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val leftBadge = if (isAdminUser) {
                    "Admin • $userEmail"
                } else {
                    "Student • $userEmail"
                }
                Text(
                    text = leftBadge,
                    style = MaterialTheme.typography.labelMedium,
                    color = InkSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                if (isAdminUser) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = PillTrackBg.copy(alpha = 0.9f),
                        border = BorderStroke(0.8.dp, CardSubtleBorder),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ModeSegmentChip(
                                label = "Student",
                                selected = appMode == AppMode.STUDENT,
                                activeBrush = Brush.verticalGradient(listOf(CardWhite, Color(0xFFFDFBFB))),
                                activeTextColor = MaroonPrimary,
                                onClick = { onSelectMode(AppMode.STUDENT) },
                                testTag = "mode_student_tab"
                            )
                            ModeSegmentChip(
                                label = "Admin",
                                selected = appMode == AppMode.ADMIN,
                                activeBrush = Brush.linearGradient(listOf(MaroonBright, MaroonPrimary, MaroonDeep)),
                                activeTextColor = Color.White,
                                onClick = { onSelectMode(AppMode.ADMIN) },
                                testTag = "mode_admin_tab"
                            )
                        }
                    }
                }
            }
            HorizontalDivider(color = CardSubtleBorder.copy(alpha = 0.6f), thickness = 0.8.dp)
        }
    }
}

@Composable
private fun ModeSegmentChip(
    label: String,
    selected: Boolean,
    activeBrush: Brush,
    activeTextColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = Modifier
            .then(
                if (selected) {
                    Modifier
                        .shadow(3.dp, shape, spotColor = MaroonPrimary.copy(alpha = 0.25f))
                        .background(activeBrush, shape)
                        .border(0.8.dp, MaroonSoftBorder.copy(alpha = 0.65f), shape)
                } else {
                    Modifier
                }
            )
            .appleTactileClickable(
                shape = shape,
                highlightColor = MaroonPrimary,
                pressedScale = 0.95f,
                onClick = onClick
            )
            .testTag(testTag)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) activeTextColor else InkSecondary,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 5.dp),
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
fun DormCleanFloatingBottomBar(
    currentTab: MainTab,
    isAdminViewActive: Boolean,
    onSelectTab: (MainTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        val dockShape = RoundedCornerShape(34.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 16.dp,
                    shape = dockShape,
                    ambientColor = MaroonPrimary.copy(alpha = 0.14f),
                    spotColor = Color.Black.copy(alpha = 0.14f)
                )
                .clip(dockShape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xF7FFFFFF), Color(0xEEFAF5F3))
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(SpecularWhiteBorder, CardSubtleBorder)
                    ),
                    shape = dockShape
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MainTab.entries.forEach { tab ->
                    val isSelected = !isAdminViewActive && currentTab == tab
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val scale by animateFloatAsState(
                        targetValue = when {
                            isPressed -> 0.93f
                            isSelected -> 1.02f
                            else -> 0.97f
                        },
                        animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMediumLow),
                        label = "tabScale"
                    )
                    val textColor by animateColorAsState(
                        targetValue = if (isSelected) Color.White else InkSecondary,
                        label = "tabTextColor"
                    )
                    val icon = when (tab) {
                        MainTab.HOME -> if (isSelected) Icons.Filled.Home else Icons.Outlined.Home
                        MainTab.SERVICES -> if (isSelected) Icons.Filled.PlaylistAddCheck else Icons.Outlined.PlaylistAddCheck
                        MainTab.BOOK -> if (isSelected) Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth
                        MainTab.BOOKINGS -> if (isSelected) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder
                    }
                    val pillShape = RoundedCornerShape(26.dp)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .scale(scale)
                            .minimumInteractiveComponentSize()
                            .then(
                                if (isSelected) {
                                    Modifier
                                        .shadow(
                                            elevation = 8.dp,
                                            shape = pillShape,
                                            spotColor = MaroonPrimary.copy(alpha = 0.45f)
                                        )
                                        .background(
                                            brush = Brush.linearGradient(
                                                colors = listOf(MaroonBright, MaroonPrimary, MaroonDeep)
                                            ),
                                            shape = pillShape
                                        )
                                        .border(
                                            width = 1.dp,
                                            brush = Brush.verticalGradient(
                                                colors = listOf(Color.White.copy(alpha = 0.45f), Color.Transparent)
                                            ),
                                            shape = pillShape
                                        )
                                } else {
                                    Modifier
                                }
                            )
                            .clip(pillShape)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = ripple(
                                    bounded = true,
                                    color = if (isSelected) Color.White else MaroonPrimary
                                ),
                                onClick = { onSelectTab(tab) }
                            )
                            .testTag("nav_${tab.name.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 7.dp, horizontal = 2.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = tab.label,
                                tint = textColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = tab.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = textColor,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DemoQrCodeMatrix(
    seedText: String,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(1.dp, CardSubtleBorder, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        val grid = 21
        val cell = size.width / grid
        val hash = seedText.hashCode()

        fun drawFinder(startX: Int, startY: Int) {
            drawRect(
                color = Color(0xFF0A1D3D),
                topLeft = Offset(startX * cell, startY * cell),
                size = Size(7 * cell, 7 * cell)
            )
            drawRect(
                color = Color.White,
                topLeft = Offset((startX + 1) * cell, (startY + 1) * cell),
                size = Size(5 * cell, 5 * cell)
            )
            drawRect(
                color = GCashBlue,
                topLeft = Offset((startX + 2) * cell, (startY + 2) * cell),
                size = Size(3 * cell, 3 * cell)
            )
        }

        drawFinder(0, 0)
        drawFinder(grid - 7, 0)
        drawFinder(0, grid - 7)

        for (y in 0 until grid) {
            for (x in 0 until grid) {
                val inTopLeft = x < 8 && y < 8
                val inTopRight = x >= grid - 8 && y < 8
                val inBottomLeft = x < 8 && y >= grid - 8
                if (inTopLeft || inTopRight || inBottomLeft) continue

                val bit = ((x * 31 + y * 17 + hash) and 0x7FFFFFFF) % 5
                if (bit == 0 || bit == 2 || (x == 6 && y % 2 == 0) || (y == 6 && x % 2 == 0)) {
                    drawRect(
                        color = Color(0xFF0A1D3D),
                        topLeft = Offset(x * cell, y * cell),
                        size = Size(cell * 0.92f, cell * 0.92f)
                    )
                }
            }
        }
    }
}

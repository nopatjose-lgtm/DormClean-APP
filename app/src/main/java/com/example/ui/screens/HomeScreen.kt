package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.CleanerAgent
import com.example.data.DormCleanCatalog
import com.example.data.MainTab
import com.example.ui.components.AppleGlassCard
import com.example.ui.components.appleTactileClickable
import com.example.ui.theme.AmberGold
import com.example.ui.theme.AmberText
import com.example.ui.theme.BlushPinkCard
import com.example.ui.theme.BlushPinkDeep
import com.example.ui.theme.CardSubtleBorder
import com.example.ui.theme.CardWhite
import com.example.ui.theme.EmeraldBg
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GCashBlue
import com.example.ui.theme.GCashBlueLight
import com.example.ui.theme.GlassWhite
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.InkSecondary
import com.example.ui.theme.JetBrainsMono
import com.example.ui.theme.LavenderTintCard
import com.example.ui.theme.LavenderTintDeep
import com.example.ui.theme.MaroonBright
import com.example.ui.theme.MaroonDarkest
import com.example.ui.theme.MaroonDeep
import com.example.ui.theme.MaroonPrimary
import com.example.ui.theme.MaroonSoftBorder
import com.example.ui.theme.MaroonTint
import com.example.ui.theme.SageMintCard
import com.example.ui.theme.SageMintDeep
import com.example.ui.theme.SpecularWhiteBorder
import com.example.ui.theme.WarmButterCard
import com.example.ui.theme.WarmButterDeep

@Composable
fun HomeScreen(
    firstName: String,
    onNavigateTab: (MainTab) -> Unit,
    onSelectPackageAndGo: (String, MainTab) -> Unit,
    onRebookClick: () -> Unit,
    onSelectCleanerAndBook: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
        contentPadding = PaddingValues(bottom = 114.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item(key = "hero") {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = GlassWhite,
                    border = BorderStroke(1.dp, MaroonSoftBorder.copy(alpha = 0.7f))
                ) {
                    Text(
                        text = "Hi, $firstName! • Daet, Cam. Norte",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaroonPrimary,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Dorm & house cleaning\nacross Daet.",
                    style = MaterialTheme.typography.displaySmall,
                    color = InkPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))

                AppleGlassCard(
                    shape = RoundedCornerShape(28.dp),
                    containerBrush = Brush.verticalGradient(
                        colors = listOf(CardWhite, Color(0xFFFDF8F7))
                    ),
                    borderColor = SpecularWhiteBorder,
                    elevation = 10.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Transparent Rates • Camarines Norte",
                            style = MaterialTheme.typography.bodySmall,
                            color = InkSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "₱300",
                            fontFamily = JetBrainsMono,
                            fontWeight = FontWeight.Bold,
                            fontSize = 44.sp,
                            color = MaroonPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Starts at ₱300 per 2-hr clean",
                            style = MaterialTheme.typography.titleMedium,
                            color = InkPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Quick ₱300 • Standard ₱500 • Add-ons ₱100",
                            fontFamily = JetBrainsMono,
                            fontSize = 11.sp,
                            color = InkSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        val heroImgShape = RoundedCornerShape(20.dp)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(178.dp)
                                .shadow(
                                    elevation = 8.dp,
                                    shape = heroImgShape,
                                    spotColor = Color.Black.copy(alpha = 0.14f)
                                )
                                .appleTactileClickable(
                                    shape = heroImgShape,
                                    highlightColor = MaroonPrimary,
                                    onClick = { onNavigateTab(MainTab.BOOK) }
                                )
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_dorm_hero),
                                contentDescription = "Bright student dorm room in Daet",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Transparent,
                                                Color(0xC2141218)
                                            )
                                        )
                                    )
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .padding(10.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xA6141218))
                                    .border(
                                        0.8.dp,
                                        Color.White.copy(alpha = 0.24f),
                                        RoundedCornerShape(14.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 9.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Any house or dorm in Daet",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "8AM–8PM • 2h",
                                        fontFamily = JetBrainsMono,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White.copy(alpha = 0.96f),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item(key = "quick_actions") {
            Row(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                QuickActionTile(
                    title = "Pricing Guide",
                    icon = Icons.Default.Sell,
                    containerBrush = Brush.verticalGradient(listOf(CardWhite, GlassWhite)),
                    iconWellBg = MaroonTint,
                    iconTint = MaroonPrimary,
                    textColor = InkPrimary,
                    isPrimary = false,
                    modifier = Modifier.weight(1.08f),
                    testTag = "home_pricing_guide_btn",
                    onClick = { onNavigateTab(MainTab.SERVICES) }
                )
                QuickActionTile(
                    title = "Rebook",
                    icon = Icons.Default.History,
                    containerBrush = Brush.linearGradient(listOf(MaroonBright, MaroonPrimary, MaroonDeep)),
                    iconWellBg = Color.White.copy(alpha = 0.2f),
                    iconTint = Color.White,
                    textColor = Color.White,
                    isPrimary = true,
                    modifier = Modifier.weight(0.92f),
                    testTag = "home_rebook_btn",
                    onClick = onRebookClick
                )
                QuickActionTile(
                    title = "GCash & Track",
                    icon = Icons.Default.QrCode2,
                    containerBrush = Brush.verticalGradient(listOf(CardWhite, GlassWhite)),
                    iconWellBg = GCashBlueLight,
                    iconTint = GCashBlue,
                    textColor = InkPrimary,
                    isPrimary = false,
                    modifier = Modifier.weight(1.08f),
                    testTag = "home_gcash_track_btn",
                    onClick = { onNavigateTab(MainTab.BOOKINGS) }
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        item(key = "bento_grid") {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BentoSummaryCard(
                        eyebrow = "₱300 • 2 hrs",
                        eyebrowColor = MaroonPrimary,
                        title = "Quick Reset",
                        subtitle = "Desk, floor & trash",
                        containerBrush = Brush.linearGradient(listOf(BlushPinkCard, BlushPinkDeep)),
                        arrowTint = MaroonPrimary,
                        modifier = Modifier.weight(1f),
                        testTag = "home_quick_reset_card",
                        onClick = { onSelectPackageAndGo("quick_reset", MainTab.SERVICES) }
                    )
                    BentoSummaryCard(
                        eyebrow = "₱500 • 2 hrs",
                        eyebrowColor = AmberText,
                        title = "Standard Clean",
                        subtitle = "Full room & under-bed",
                        containerBrush = Brush.linearGradient(listOf(WarmButterCard, WarmButterDeep)),
                        arrowTint = AmberText,
                        modifier = Modifier.weight(1f),
                        testTag = "home_standard_clean_card",
                        onClick = { onSelectPackageAndGo("standard_dorm", MainTab.SERVICES) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BentoSummaryCard(
                        eyebrow = "₱100 Each",
                        eyebrowColor = Color(0xFF4338CA),
                        title = "Optional Add-Ons",
                        subtitle = "Plus custom requests",
                        containerBrush = Brush.linearGradient(listOf(LavenderTintCard, LavenderTintDeep)),
                        arrowTint = Color(0xFF4338CA),
                        modifier = Modifier.weight(1f),
                        testTag = "home_addons_card",
                        onClick = { onNavigateTab(MainTab.SERVICES) }
                    )
                    BentoSummaryCard(
                        eyebrow = "5 Daily Slots",
                        eyebrowColor = EmeraldGreen,
                        title = "2-Hr Slots",
                        subtitle = "8AM–8PM • 12–1 break",
                        containerBrush = Brush.linearGradient(listOf(SageMintCard, SageMintDeep)),
                        arrowTint = EmeraldGreen,
                        modifier = Modifier.weight(1f),
                        testTag = "home_slots_card",
                        onClick = { onNavigateTab(MainTab.BOOK) }
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        item(key = "three_step") {
            val walletShape = RoundedCornerShape(26.dp)
            Box(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .shadow(
                        elevation = 14.dp,
                        shape = walletShape,
                        ambientColor = MaroonPrimary.copy(alpha = 0.35f),
                        spotColor = MaroonDeep.copy(alpha = 0.35f)
                    )
                    .clip(walletShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(MaroonBright, MaroonPrimary, MaroonDarkest)
                        )
                    )
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color.White.copy(alpha = 0.16f), Color.Transparent),
                                center = Offset(size.width * 0.88f, size.height * 0.12f),
                                radius = size.width * 0.6f
                            )
                        )
                    }
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.White.copy(alpha = 0.35f), Color.Transparent)
                        ),
                        shape = walletShape
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "Easy 3-step student booking",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    BookingStepRow(
                        stepNum = "01",
                        title = "Pick your plan",
                        body = "Quick Reset (₱300) or Standard Clean (₱500), plus ₱100 add-ons & custom requests."
                    )
                    HorizontalDivider(
                        color = Color.White.copy(alpha = 0.16f),
                        modifier = Modifier.padding(vertical = 11.dp)
                    )
                    BookingStepRow(
                        stepNum = "02",
                        title = "Choose slot & Daet address",
                        body = "Tap an open 2-hr window and share your house, boarding house, or room in Daet."
                    )
                    HorizontalDivider(
                        color = Color.White.copy(alpha = 0.16f),
                        modifier = Modifier.padding(vertical = 11.dp)
                    )
                    BookingStepRow(
                        stepNum = "03",
                        title = "Pay & clean",
                        body = "Pay with GCash QR (Demo) or cash on arrival. Supplies & mops included."
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item(key = "cleaners_header") {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                AppleGlassCard(
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Brush.linearGradient(listOf(BlushPinkCard, MaroonTint)))
                                .border(1.dp, MaroonSoftBorder, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = MaroonPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Verified Local Crew • Daet",
                                style = MaterialTheme.typography.titleMedium,
                                color = InkPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "ID-checked cleaners, supplies included. Tap any cleaner to book directly.",
                                style = MaterialTheme.typography.bodySmall,
                                color = InkSecondary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        items(
            items = DormCleanCatalog.agents,
            key = { it.id }
        ) { agent ->
            Box(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 5.dp)
            ) {
                HomeCleanerCard(
                    agent = agent,
                    onClick = { onSelectCleanerAndBook(agent.id) }
                )
            }
        }
    }
}

@Composable
private fun QuickActionTile(
    title: String,
    icon: ImageVector,
    containerBrush: Brush,
    iconWellBg: Color,
    iconTint: Color,
    textColor: Color,
    isPrimary: Boolean,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(22.dp)
    Box(
        modifier = modifier
            .height(82.dp)
            .minimumInteractiveComponentSize()
            .shadow(
                elevation = if (isPrimary) 10.dp else 6.dp,
                shape = shape,
                ambientColor = if (isPrimary) MaroonPrimary.copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.08f),
                spotColor = if (isPrimary) MaroonPrimary.copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.08f)
            )
            .background(containerBrush, shape)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = if (isPrimary) {
                        listOf(Color.White.copy(alpha = 0.35f), Color.Transparent)
                    } else {
                        listOf(SpecularWhiteBorder, CardSubtleBorder)
                    }
                ),
                shape = shape
            )
            .appleTactileClickable(
                shape = shape,
                highlightColor = if (isPrimary) Color.White else MaroonPrimary,
                onClick = onClick
            )
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(iconWellBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(17.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = textColor,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun BentoSummaryCard(
    eyebrow: String,
    eyebrowColor: Color,
    title: String,
    subtitle: String,
    containerBrush: Brush,
    arrowTint: Color,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(24.dp)
    Box(
        modifier = modifier
            .height(162.dp)
            .shadow(
                elevation = 6.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.06f),
                spotColor = Color.Black.copy(alpha = 0.06f)
            )
            .background(containerBrush, shape)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White.copy(alpha = 0.85f), CardSubtleBorder)
                ),
                shape = shape
            )
            .appleTactileClickable(
                shape = shape,
                highlightColor = arrowTint,
                onClick = onClick
            )
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = eyebrow,
                    fontFamily = JetBrainsMono,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = eyebrowColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = InkPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = InkSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.BottomEnd
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.94f),
                    shadowElevation = 2.dp,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.CallMade,
                            contentDescription = "Open $title",
                            tint = arrowTint,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BookingStepRow(stepNum: String, title: String, body: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.16f))
                .border(0.8.dp, Color.White.copy(alpha = 0.30f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stepNum,
                fontFamily = JetBrainsMono,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.88f)
            )
        }
    }
}

@Composable
private fun HomeCleanerCard(
    agent: CleanerAgent,
    onClick: () -> Unit
) {
    val baseAvatar = Color(agent.avatarColorHex)
    val shape = RoundedCornerShape(22.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, shape, spotColor = Color.Black.copy(alpha = 0.06f))
            .background(
                Brush.verticalGradient(listOf(CardWhite, Color(0xFFFCF9F8))),
                shape
            )
            .border(1.dp, CardSubtleBorder, shape)
            .appleTactileClickable(
                shape = shape,
                highlightColor = MaroonPrimary,
                onClick = onClick
            )
            .testTag("home_cleaner_${agent.id}")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .shadow(4.dp, CircleShape, spotColor = baseAvatar.copy(alpha = 0.35f))
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(baseAvatar, baseAvatar.copy(alpha = 0.78f))
                        )
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.45f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = agent.initials,
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = agent.fullName,
                        style = MaterialTheme.typography.titleSmall,
                        color = InkPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${agent.cleansCount} cleans",
                        fontFamily = JetBrainsMono,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaroonPrimary,
                        maxLines = 1
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = EmeraldBg
                    ) {
                        Text(
                            text = "• Available",
                            color = EmeraldGreen,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaroonTint,
                        border = BorderStroke(0.8.dp, MaroonSoftBorder)
                    ) {
                        Text(
                            text = "Book ↗",
                            color = MaroonPrimary,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = agent.zoneAndRole,
                    style = MaterialTheme.typography.bodySmall,
                    color = InkSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = AmberGold,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "${agent.rating} (${agent.reviewCount})",
                            fontFamily = JetBrainsMono,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = InkPrimary
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaroonTint,
                        border = BorderStroke(0.8.dp, MaroonSoftBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = MaroonPrimary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Tip ₱20–50",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaroonPrimary,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

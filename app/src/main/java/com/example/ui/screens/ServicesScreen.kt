package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.CleaningPackage
import com.example.data.DormCleanCatalog
import com.example.data.OptionalAddon
import com.example.ui.BookingDraftState
import com.example.ui.components.AppleGlassCard
import com.example.ui.components.AppleGradientButton
import com.example.ui.components.DormCleanTextField
import com.example.ui.components.SelectedHighlightBadge
import com.example.ui.components.appleTactileClickable
import com.example.ui.theme.CardSubtleBorder
import com.example.ui.theme.CardWhite
import com.example.ui.theme.CoolBlueCard
import com.example.ui.theme.CoolBlueDeep
import com.example.ui.theme.GlassWhite
import com.example.ui.theme.InkMuted
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
import com.example.ui.theme.SoftInputBg
import com.example.ui.theme.SoftInputBorder
import com.example.ui.theme.SoftLilacCard
import com.example.ui.theme.SoftLilacDeep
import com.example.ui.theme.SpecularWhiteBorder

@Composable
fun ServicesScreen(
    draft: BookingDraftState,
    onSelectPackage: (String) -> Unit,
    onSelectOccupancy: (String) -> Unit,
    onToggleAddon: (String) -> Unit,
    onUpdateOthers: (String) -> Unit,
    onProceedToBook: () -> Unit
) {
    val selectedPkg = remember(draft.selectedPackageId) {
        DormCleanCatalog.packages.firstOrNull { it.id == draft.selectedPackageId }
            ?: DormCleanCatalog.packages.last()
    }
    val selectedAddons = remember(draft.selectedAddonIds) {
        DormCleanCatalog.addons.filter { it.id in draft.selectedAddonIds }
    }
    val estimatedTotal = remember(selectedPkg, draft.occupancyType, selectedAddons) {
        val occupancyFee = if (draft.occupancyType == "shared") 100L else 0L
        selectedPkg.basePrice + occupancyFee + selectedAddons.sumOf { it.price }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
        contentPadding = PaddingValues(bottom = 116.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item(key = "header") {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Daet • Student Rates",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaroonPrimary,
                        maxLines = 1
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaroonTint,
                        border = BorderStroke(1.dp, MaroonSoftBorder)
                    ) {
                        Text(
                            text = "Est. Total: ₱$estimatedTotal",
                            fontFamily = JetBrainsMono,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaroonPrimary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = buildAnnotatedString {
                        append("Choose your ")
                        withStyle(SpanStyle(color = MaroonPrimary)) {
                            append("cleaning plan")
                        }
                    },
                    style = MaterialTheme.typography.displaySmall,
                    color = InkPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Transparent peso rates for dorms & homes in Daet. Cleaning supplies included.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkSecondary
                )
            }
        }

        item(key = "packages_header") {
            Row(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "01. Core Cleaning Packages",
                    style = MaterialTheme.typography.titleMedium,
                    color = InkPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "₱300 – ₱500",
                    fontFamily = JetBrainsMono,
                    fontSize = 12.sp,
                    color = InkSecondary
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        itemsIndexed(
            items = DormCleanCatalog.packages,
            key = { _, pkg -> "pkg_${pkg.id}" }
        ) { _, pkg ->
            Box(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                ServicePackageCard(
                    pkg = pkg,
                    selected = draft.selectedPackageId == pkg.id,
                    onSelect = { onSelectPackage(pkg.id) }
                )
            }
        }

        item(key = "occupancy") {
            Box(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                AppleGlassCard(
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Group,
                                    contentDescription = null,
                                    tint = MaroonPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Room Occupancy",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = InkPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(
                                text = "Fair area rate",
                                style = MaterialTheme.typography.bodySmall,
                                color = InkSecondary,
                                maxLines = 1
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OccupancyChipCard(
                                title = "Solo / 2-Pax",
                                priceLabel = "Included",
                                selected = draft.occupancyType == "solo",
                                onClick = { onSelectOccupancy("solo") },
                                modifier = Modifier.weight(1f),
                                testTag = "services_occupancy_solo"
                            )
                            OccupancyChipCard(
                                title = "3–4 Pax Shared",
                                priceLabel = "+₱100",
                                selected = draft.occupancyType == "shared",
                                onClick = { onSelectOccupancy("shared") },
                                modifier = Modifier.weight(1f),
                                testTag = "services_occupancy_shared"
                            )
                        }
                    }
                }
            }
        }

        item(key = "addons_header") {
            Row(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "02. Optional Add-Ons (₱100 each)",
                    style = MaterialTheme.typography.titleMedium,
                    color = InkPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${draft.selectedAddonIds.size}/5 selected",
                    fontFamily = JetBrainsMono,
                    fontSize = 12.sp,
                    color = MaroonPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        itemsIndexed(
            items = DormCleanCatalog.addons,
            key = { _, addon -> "addon_${addon.id}" }
        ) { idx, addon ->
            val (startTint, endTint) = when (idx) {
                0, 1 -> LavenderTintCard to LavenderTintDeep
                2 -> CoolBlueCard to CoolBlueDeep
                3 -> SageMintCard to SageMintDeep
                else -> SoftLilacCard to SoftLilacDeep
            }
            Box(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 5.dp)
            ) {
                AddonToggleRow(
                    addon = addon,
                    selected = addon.id in draft.selectedAddonIds,
                    unselectedBg = startTint,
                    unselectedBrush = Brush.linearGradient(listOf(startTint, endTint)),
                    onToggle = { onToggleAddon(addon.id) }
                )
            }
        }

        item(key = "banner_and_others") {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .shadow(6.dp, RoundedCornerShape(24.dp), spotColor = Color.Black.copy(alpha = 0.1f))
                        .clip(RoundedCornerShape(24.dp))
                        .border(1.dp, SpecularWhiteBorder, RoundedCornerShape(24.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_services_banner),
                        contentDescription = "Tidy study shelf and kitchenette",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                AppleGlassCard(
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ChatBubbleOutline,
                                contentDescription = null,
                                tint = MaroonPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Others (Custom Requests)",
                                style = MaterialTheme.typography.titleSmall,
                                color = InkPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Add any extra request on top of your selected plan:",
                            style = MaterialTheme.typography.bodySmall,
                            color = InkSecondary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        DormCleanTextField(
                            value = draft.othersRequest,
                            onValueChange = onUpdateOthers,
                            placeholder = "e.g., Wipe fan blades, sweep porch, tidy shoe rack...",
                            singleLine = false,
                            minLines = 3,
                            maxLines = 5,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("services_others_input")
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                            Text(
                                text = "${draft.othersRequest.length}/300",
                                fontFamily = JetBrainsMono,
                                fontSize = 11.sp,
                                color = InkMuted
                            )
                        }
                    }
                }
            }
        }

        item(key = "summary_cta") {
            Box(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                AppleGlassCard(
                    shape = RoundedCornerShape(26.dp),
                    borderColor = SpecularWhiteBorder,
                    elevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Selected Plan",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaroonPrimary
                            )
                            Text(
                                text = "2 hrs (120m)",
                                fontFamily = JetBrainsMono,
                                fontSize = 12.sp,
                                color = InkSecondary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = selectedPkg.title,
                            style = MaterialTheme.typography.headlineSmall,
                            color = InkPrimary
                        )

                        HorizontalDivider(
                            color = CardSubtleBorder,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )

                        SummaryLineItem(selectedPkg.title, "₱${selectedPkg.basePrice}")
                        if (draft.occupancyType == "shared") {
                            Spacer(modifier = Modifier.height(6.dp))
                            SummaryLineItem("3–4 Pax Shared Room", "+₱100")
                        }
                        selectedAddons.forEach { addon ->
                            Spacer(modifier = Modifier.height(6.dp))
                            SummaryLineItem("Optional: ${addon.title}", "+₱${addon.price}")
                        }

                        HorizontalDivider(
                            color = CardSubtleBorder,
                            modifier = Modifier.padding(vertical = 14.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Estimated Total",
                                style = MaterialTheme.typography.titleMedium,
                                color = InkPrimary
                            )
                            Text(
                                text = "₱$estimatedTotal",
                                fontFamily = JetBrainsMono,
                                fontWeight = FontWeight.Bold,
                                fontSize = 26.sp,
                                color = MaroonPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        AppleGradientButton(
                            onClick = onProceedToBook,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("services_proceed_book_button")
                        ) {
                            Text(
                                text = "Pick Time Slot & Location",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ServicePackageCard(
    pkg: CleaningPackage,
    selected: Boolean,
    onSelect: () -> Unit
) {
    val shape = RoundedCornerShape(26.dp)
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.01f else 1f,
        label = "pkgSelectedScale"
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(
                elevation = if (selected) 14.dp else 5.dp,
                shape = shape,
                ambientColor = if (selected) MaroonPrimary.copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.06f),
                spotColor = if (selected) MaroonDeep.copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.06f)
            )
            .then(
                if (selected) {
                    Modifier
                        .background(
                            Brush.linearGradient(colors = listOf(MaroonBright, MaroonPrimary, MaroonDarkest)),
                            shape
                        )
                        .drawBehind {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color.White.copy(alpha = 0.16f), Color.Transparent),
                                    center = Offset(size.width * 0.9f, size.height * 0.1f),
                                    radius = size.width * 0.55f
                                )
                            )
                        }
                        .border(
                            width = 2.dp,
                            brush = Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.72f), Color.White.copy(alpha = 0.2f))),
                            shape = shape
                        )
                } else {
                    Modifier
                        .background(Brush.verticalGradient(listOf(CardWhite, GlassWhite)), shape)
                        .border(1.dp, CardSubtleBorder, shape)
                }
            )
            .appleTactileClickable(
                shape = shape,
                highlightColor = if (selected) Color.White else MaroonPrimary,
                onClick = onSelect
            )
            .testTag("services_pkg_${pkg.id}")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${pkg.numberPrefix} ${pkg.durationLabel}",
                        fontFamily = JetBrainsMono,
                        fontSize = 11.5.sp,
                        color = if (selected) Color.White.copy(alpha = 0.9f) else InkSecondary
                    )
                    if (selected) {
                        SelectedHighlightBadge(label = "Selected", lightOnDark = true)
                    }
                }
                Surface(
                    shape = CircleShape,
                    color = CardWhite,
                    border = if (!selected) BorderStroke(1.dp, SoftInputBorder) else null,
                    shadowElevation = 2.dp,
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (selected) Icons.Default.Check else Icons.Default.Add,
                            contentDescription = if (selected) "Selected" else "Select",
                            tint = MaroonPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = pkg.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = if (selected) Color.White else InkPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "₱${pkg.basePrice}",
                    fontFamily = JetBrainsMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 23.sp,
                    color = if (selected) Color.White else MaroonPrimary
                )
            }

            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = pkg.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = if (selected) Color.White.copy(alpha = 0.88f) else InkSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = pkg.description,
                style = MaterialTheme.typography.bodyMedium,
                color = if (selected) Color.White.copy(alpha = 0.95f) else InkPrimary
            )

            HorizontalDivider(
                color = if (selected) Color.White.copy(alpha = 0.2f) else CardSubtleBorder,
                modifier = Modifier.padding(vertical = 11.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                pkg.bullets.forEach { bullet ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = if (selected) Color.White else MaroonPrimary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = bullet,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (selected) Color.White.copy(alpha = 0.94f) else InkPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OccupancyChipCard(
    title: String,
    priceLabel: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    val shape = RoundedCornerShape(18.dp)
    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .then(
                if (selected) {
                    Modifier
                        .shadow(8.dp, shape, spotColor = MaroonPrimary.copy(alpha = 0.4f))
                        .background(Brush.linearGradient(listOf(MaroonBright, MaroonPrimary, MaroonDeep)), shape)
                        .border(1.8.dp, Color.White.copy(alpha = 0.65f), shape)
                } else {
                    Modifier
                        .background(SoftInputBg, shape)
                        .border(1.dp, SoftInputBorder, shape)
                }
            )
            .appleTactileClickable(
                shape = shape,
                highlightColor = if (selected) Color.White else MaroonPrimary,
                onClick = onClick
            )
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 11.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (selected) Color.White else InkPrimary,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = priceLabel,
                    fontFamily = JetBrainsMono,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selected) Color.White.copy(alpha = 0.92f) else MaroonPrimary,
                    maxLines = 1
                )
            }
            if (selected) {
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    modifier = Modifier.size(22.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = MaroonPrimary,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddonToggleRow(
    addon: OptionalAddon,
    selected: Boolean,
    unselectedBg: Color,
    onToggle: () -> Unit,
    unselectedBrush: Brush? = null
) {
    val shape = RoundedCornerShape(22.dp)
    val bgBrush = if (selected) {
        Brush.linearGradient(listOf(MaroonBright, MaroonPrimary, MaroonDeep))
    } else {
        unselectedBrush ?: Brush.verticalGradient(listOf(unselectedBg, unselectedBg.copy(alpha = 0.85f)))
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .shadow(
                elevation = if (selected) 10.dp else 3.dp,
                shape = shape,
                spotColor = if (selected) MaroonPrimary.copy(alpha = 0.38f) else Color.Black.copy(alpha = 0.05f)
            )
            .background(bgBrush, shape)
            .border(
                width = if (selected) 1.8.dp else 1.dp,
                color = if (selected) Color.White.copy(alpha = 0.6f) else SpecularWhiteBorder,
                shape = shape
            )
            .appleTactileClickable(
                shape = shape,
                highlightColor = if (selected) Color.White else MaroonPrimary,
                onClick = onToggle
            )
            .testTag("addon_${addon.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = addon.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = if (selected) Color.White else InkPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (selected) {
                        SelectedHighlightBadge(label = "Added", lightOnDark = true)
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = addon.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (selected) Color.White.copy(alpha = 0.88f) else InkSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "+₱${addon.price}",
                    fontFamily = JetBrainsMono,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selected) Color.White else MaroonPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = CircleShape,
                    color = CardWhite,
                    shadowElevation = 2.dp,
                    border = if (!selected) BorderStroke(1.dp, MaroonSoftBorder) else null,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (selected) Icons.Default.Check else Icons.Default.Add,
                            contentDescription = if (selected) "Selected" else "Add",
                            tint = MaroonPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryLineItem(label: String, amount: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = InkSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = amount,
            fontFamily = JetBrainsMono,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = InkPrimary
        )
    }
}

package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PersonAddAlt1
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CleanerAgent
import com.example.data.DayConfig
import com.example.data.DormCleanCatalog
import com.example.data.ServiceDateOption
import com.example.data.SlotLock
import com.example.ui.BookingDraftState
import com.example.ui.components.AppleGlassCard
import com.example.ui.components.AppleGradientButton
import com.example.ui.components.DemoQrCodeMatrix
import com.example.ui.components.DormCleanTextField
import com.example.ui.components.SelectedHighlightBadge
import com.example.ui.components.appleTactileClickable
import com.example.ui.theme.AmberGold
import com.example.ui.theme.AmberText
import com.example.ui.theme.BlushPinkCard
import com.example.ui.theme.BlushPinkDeep
import com.example.ui.theme.CardSubtleBorder
import com.example.ui.theme.CardWhite
import com.example.ui.theme.CoolBlueCard
import com.example.ui.theme.CoolBlueDeep
import com.example.ui.theme.DangerBg
import com.example.ui.theme.DangerRed
import com.example.ui.theme.EmeraldBg
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GCashBlue
import com.example.ui.theme.GCashBlueDeep
import com.example.ui.theme.GCashBlueLight
import com.example.ui.theme.InkMuted
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.InkSecondary
import com.example.ui.theme.JetBrainsMono
import com.example.ui.theme.LavenderTintCard
import com.example.ui.theme.LavenderTintDeep
import com.example.ui.theme.MaroonBright
import com.example.ui.theme.MaroonDeep
import com.example.ui.theme.MaroonPrimary
import com.example.ui.theme.MaroonSoftBorder
import com.example.ui.theme.MaroonTint
import com.example.ui.theme.PillTrackBg
import com.example.ui.theme.SageMintCard
import com.example.ui.theme.SageMintDeep
import com.example.ui.theme.SoftInputBg
import com.example.ui.theme.SoftInputBorder
import com.example.ui.theme.SoftLilacCard
import com.example.ui.theme.SoftLilacDeep
import com.example.ui.theme.SpecularWhiteBorder

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BookScreen(
    draft: BookingDraftState,
    upcomingDates: List<ServiceDateOption>,
    dayConfigs: Map<String, DayConfig>,
    slotLocks: List<SlotLock>,
    isSubmitting: Boolean,
    computeDayOpenSlots: (String, String?) -> Int,
    computeDayPromoDiscount: (Long, DayConfig?) -> Pair<String, Long>,
    onSelectPackage: (String) -> Unit,
    onSelectOccupancy: (String) -> Unit,
    onToggleAddon: (String) -> Unit,
    onSelectDate: (String) -> Unit,
    onSelectAgent: (String) -> Unit,
    onSelectSlot: (String) -> Unit,
    onUpdateLocation: (String?, String?, String?, String?, String?, String?) -> Unit,
    onToggleArrivalPreset: (String) -> Unit,
    onUpdateContact: (String?, String?, String?) -> Unit,
    onUpdatePayment: (String?, String?, String?) -> Unit,
    onUpdateReferral: (String) -> Unit,
    onUseDaet50: () -> Unit,
    onUpdateOthers: (String) -> Unit,
    onSubmitBooking: () -> Unit
) {
    val selectedPkg = remember(draft.selectedPackageId) {
        DormCleanCatalog.packages.firstOrNull { it.id == draft.selectedPackageId }
            ?: DormCleanCatalog.packages.last()
    }
    val selectedAddons = remember(draft.selectedAddonIds) {
        DormCleanCatalog.addons.filter { it.id in draft.selectedAddonIds }
    }
    val subtotal = remember(selectedPkg, draft.occupancyType, selectedAddons) {
        val occupancyFee = if (draft.occupancyType == "shared") 100L else 0L
        selectedPkg.basePrice + occupancyFee + selectedAddons.sumOf { it.price }
    }
    val selectedDateOpt = remember(upcomingDates, draft.selectedDateKey) {
        upcomingDates.firstOrNull { it.dateKey == draft.selectedDateKey } ?: upcomingDates.first()
    }
    val currentDayCfg = dayConfigs[selectedDateOpt.dateKey]
    val (promoName, promoDiscount) = remember(subtotal, currentDayCfg) {
        computeDayPromoDiscount(subtotal, currentDayCfg)
    }
    val referralDiscount = remember(draft.referralCode) {
        if (draft.referralCode.trim().equals("DAET50", ignoreCase = true)) 50L else 0L
    }
    val finalTotal = (subtotal - promoDiscount - referralDiscount).coerceAtLeast(0L)

    val selectedAgent = remember(draft.selectedAgentId) {
        DormCleanCatalog.agents.firstOrNull { it.id == draft.selectedAgentId }
    }
    val selectedSlot = remember(draft.selectedSlotId) {
        DormCleanCatalog.timeSlots.firstOrNull { it.id == draft.selectedSlotId }
    }
    val selectedBarangayObj = remember(draft.barangay) {
        DormCleanCatalog.barangays.firstOrNull { it.name == draft.barangay }
            ?: DormCleanCatalog.barangays.first()
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            onUpdatePayment(null, null, "Receipt attached (${uri.lastPathSegment ?: "photo"})")
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
        contentPadding = PaddingValues(bottom = 116.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item(key = "header_and_live_strip") {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Daet • Live Slot Reservation",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaroonPrimary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = buildAnnotatedString {
                        append("Lock your ")
                        withStyle(SpanStyle(color = MaroonPrimary)) {
                            append("time slot")
                        }
                        append(" & location")
                    },
                    style = MaterialTheme.typography.displaySmall,
                    color = InkPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Pick an open 2-hr window, enter your Daet address, and choose GCash QR or cash.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Compact live selection status bar
                AppleGlassCard(
                    shape = RoundedCornerShape(18.dp),
                    containerBrush = Brush.linearGradient(listOf(BlushPinkCard, MaroonTint)),
                    borderColor = MaroonSoftBorder,
                    elevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${selectedPkg.shortTitle} • ${selectedDateOpt.dayLabel}",
                                style = MaterialTheme.typography.labelLarge,
                                color = InkPrimary,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (selectedAgent != null && selectedSlot != null) {
                                    "${selectedAgent.agentNumberLabel} • ${selectedSlot.label}"
                                } else {
                                    "Next: Pick agent & slot in Step 02"
                                },
                                fontFamily = JetBrainsMono,
                                fontSize = 11.sp,
                                color = MaroonPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaroonPrimary
                        ) {
                            Text(
                                text = "₱$finalTotal",
                                fontFamily = JetBrainsMono,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
        }

        item(key = "step_01_package") {
            Box(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                AppleGlassCard(
                    shape = RoundedCornerShape(26.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "01. Package & Room Size",
                                style = MaterialTheme.typography.titleMedium,
                                color = InkPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Add-ons ₱100",
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
                            DormCleanCatalog.packages.forEach { pkg ->
                                val isSel = draft.selectedPackageId == pkg.id
                                val pkgShape = RoundedCornerShape(20.dp)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .shadow(
                                            elevation = if (isSel) 10.dp else 2.dp,
                                            shape = pkgShape,
                                            spotColor = if (isSel) MaroonPrimary.copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.05f)
                                        )
                                        .background(
                                            brush = if (isSel) {
                                                Brush.linearGradient(listOf(MaroonBright, MaroonPrimary, MaroonDeep))
                                            } else {
                                                Brush.linearGradient(listOf(BlushPinkCard, BlushPinkDeep.copy(alpha = 0.7f)))
                                            },
                                            shape = pkgShape
                                        )
                                        .border(
                                            width = if (isSel) 2.dp else 1.dp,
                                            color = if (isSel) Color.White.copy(alpha = 0.72f) else SpecularWhiteBorder,
                                            shape = pkgShape
                                        )
                                        .appleTactileClickable(
                                            shape = pkgShape,
                                            highlightColor = if (isSel) Color.White else MaroonPrimary,
                                            onClick = { onSelectPackage(pkg.id) }
                                        )
                                        .testTag("book_pkg_${pkg.id}")
                                ) {
                                    Column(modifier = Modifier.padding(13.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "₱${pkg.basePrice}",
                                                fontFamily = JetBrainsMono,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp,
                                                color = if (isSel) Color.White else MaroonPrimary
                                            )
                                            Surface(
                                                shape = CircleShape,
                                                color = CardWhite,
                                                shadowElevation = 2.dp,
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        imageVector = if (isSel) Icons.Default.Check else Icons.Default.Add,
                                                        contentDescription = null,
                                                        tint = MaroonPrimary,
                                                        modifier = Modifier.size(15.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = if (pkg.id == "standard_dorm") "Standard Clean" else pkg.title,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = if (isSel) Color.White else InkPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = pkg.durationLabel,
                                                fontFamily = JetBrainsMono,
                                                fontSize = 10.5.sp,
                                                color = if (isSel) Color.White.copy(alpha = 0.85f) else InkSecondary,
                                                maxLines = 1
                                            )
                                        }
                                        if (isSel) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            SelectedHighlightBadge(label = "Selected", lightOnDark = true)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Group,
                                    contentDescription = null,
                                    tint = MaroonPrimary,
                                    modifier = Modifier.size(17.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Room Occupancy",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = InkPrimary
                                )
                            }
                            Text(
                                text = "Fair area rate",
                                style = MaterialTheme.typography.bodySmall,
                                color = InkSecondary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
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
                                testTag = "book_occupancy_solo"
                            )
                            OccupancyChipCard(
                                title = "3–4 Pax Shared",
                                priceLabel = "+₱100",
                                selected = draft.occupancyType == "shared",
                                onClick = { onSelectOccupancy("shared") },
                                modifier = Modifier.weight(1f),
                                testTag = "book_occupancy_shared"
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Optional Add-Ons (₱100 each)",
                                style = MaterialTheme.typography.titleSmall,
                                color = InkPrimary
                            )
                            Text(
                                text = "${draft.selectedAddonIds.size}/5",
                                fontFamily = JetBrainsMono,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaroonPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            DormCleanCatalog.addons.forEachIndexed { idx, addon ->
                                val (startTint, endTint) = when (idx) {
                                    0, 1 -> LavenderTintCard to LavenderTintDeep
                                    2 -> CoolBlueCard to CoolBlueDeep
                                    3 -> SageMintCard to SageMintDeep
                                    else -> SoftLilacCard to SoftLilacDeep
                                }
                                AddonToggleRow(
                                    addon = addon,
                                    selected = addon.id in draft.selectedAddonIds,
                                    unselectedBg = startTint,
                                    unselectedBrush = Brush.linearGradient(listOf(startTint, endTint)),
                                    onToggle = { onToggleAddon(addon.id) }
                                )
                            }
                        }
                    }
                }
            }
        }

        item(key = "step_02_schedule") {
            Box(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                AppleGlassCard(
                    shape = RoundedCornerShape(26.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "02. Date, Agent & 2-Hr Slot",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = InkPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = buildAnnotatedString {
                                        append("8AM–8PM • ")
                                        withStyle(SpanStyle(color = MaroonPrimary, fontWeight = FontWeight.SemiBold)) {
                                            append(selectedDateOpt.fullFormatted)
                                        }
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = InkSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = SoftInputBg,
                                border = BorderStroke(1.dp, SoftInputBorder),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = "Schedule Calendar",
                                        tint = MaroonPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            upcomingDates.forEach { dateOpt ->
                                val isSelected = dateOpt.dateKey == selectedDateOpt.dateKey
                                val cfg = dayConfigs[dateOpt.dateKey]
                                val openCount = computeDayOpenSlots(dateOpt.dateKey, null)
                                val stopped = cfg?.forceStopped == true
                                val dateShape = RoundedCornerShape(18.dp)
                                Box(
                                    modifier = Modifier
                                        .width(92.dp)
                                        .shadow(
                                            elevation = if (isSelected) 10.dp else 2.dp,
                                            shape = dateShape,
                                            spotColor = if (isSelected) MaroonPrimary.copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.05f)
                                        )
                                        .background(
                                            brush = if (isSelected) {
                                                Brush.linearGradient(listOf(MaroonBright, MaroonPrimary, MaroonDeep))
                                            } else {
                                                Brush.verticalGradient(listOf(CardWhite, SoftInputBg))
                                            },
                                            shape = dateShape
                                        )
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) Color.White.copy(alpha = 0.72f) else CardSubtleBorder,
                                            shape = dateShape
                                        )
                                        .appleTactileClickable(
                                            shape = dateShape,
                                            highlightColor = if (isSelected) Color.White else MaroonPrimary,
                                            onClick = { onSelectDate(dateOpt.dateKey) }
                                        )
                                        .testTag("book_date_${dateOpt.dateKey}")
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 10.dp, horizontal = 8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = dateOpt.topLabel,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isSelected) Color.White.copy(alpha = 0.88f) else InkSecondary,
                                            maxLines = 1
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = dateOpt.dayLabel,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = if (isSelected) Color.White else InkPrimary,
                                            maxLines = 1
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = if (stopped) "Closed" else "$openCount open",
                                            fontFamily = JetBrainsMono,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when {
                                                isSelected -> Color.White
                                                stopped || openCount == 0 -> DangerRed
                                                else -> MaroonPrimary
                                            },
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }

                        if (currentDayCfg?.forceStopped == true) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = DangerBg,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Paused on ${selectedDateOpt.fullFormatted}: ${currentDayCfg.stopReason.ifBlank { "Day Off" }}. Pick another date.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = DangerRed,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        } else if (currentDayCfg?.discountEnabled == true && currentDayCfg.discountValue > 0L) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = EmeraldBg,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                val promoDesc = if (currentDayCfg.discountType == "percent") {
                                    "${currentDayCfg.discountValue}% OFF"
                                } else {
                                    "₱${currentDayCfg.discountValue} OFF"
                                }
                                Text(
                                    text = "${currentDayCfg.promoName.ifBlank { "Student Promo" }}: $promoDesc automatically applied!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = EmeraldGreen,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "1. Choose Your Agent *",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = InkPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Tap a cleaner to view open 2-hr slots",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = InkSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (selectedAgent != null) MaroonPrimary else MaroonTint,
                                border = BorderStroke(1.dp, if (selectedAgent != null) MaroonPrimary else MaroonSoftBorder)
                            ) {
                                Text(
                                    text = selectedAgent?.agentNumberLabel ?: "Pick Agent",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (selectedAgent != null) Color.White else MaroonPrimary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            DormCleanCatalog.agents.forEach { agent ->
                                val agentOpen = computeDayOpenSlots(selectedDateOpt.dateKey, agent.id)
                                val isSel = draft.selectedAgentId == agent.id
                                BookAgentSelectorCard(
                                    agent = agent,
                                    openCount = agentOpen,
                                    selected = isSel,
                                    onClick = { onSelectAgent(agent.id) }
                                )
                            }
                        }

                        if (selectedAgent != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = CardSubtleBorder)
                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "2. Pick 2-Hr Arrival Window * (${selectedAgent.shortName})",
                                style = MaterialTheme.typography.titleSmall,
                                color = InkPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                DormCleanCatalog.timeSlots.forEach { slot ->
                                    val blockKey = "${selectedAgent.id}__${slot.id}"
                                    val blockReason = currentDayCfg?.blockedSlots?.get(blockKey)
                                    val isLocked = slotLocks.any {
                                        it.dateKey == selectedDateOpt.dateKey &&
                                            it.agentId == selectedAgent.id &&
                                            it.slotId == slot.id
                                    }
                                    val isClosedDay = currentDayCfg?.forceStopped == true
                                    val unavailable = isClosedDay || blockReason != null || isLocked
                                    val isSlotSel = draft.selectedSlotId == slot.id && !unavailable

                                    val slotShape = RoundedCornerShape(16.dp)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .minimumInteractiveComponentSize()
                                            .then(
                                                if (isSlotSel) {
                                                    Modifier.shadow(8.dp, slotShape, spotColor = MaroonPrimary.copy(alpha = 0.4f))
                                                } else {
                                                    Modifier
                                                }
                                            )
                                            .background(
                                                brush = when {
                                                    isSlotSel -> Brush.linearGradient(listOf(MaroonBright, MaroonPrimary, MaroonDeep))
                                                    unavailable -> Brush.verticalGradient(listOf(PillTrackBg, PillTrackBg))
                                                    else -> Brush.verticalGradient(listOf(CardWhite, SoftInputBg))
                                                },
                                                shape = slotShape
                                            )
                                            .border(
                                                width = if (isSlotSel) 2.dp else 1.dp,
                                                color = if (isSlotSel) Color.White.copy(alpha = 0.72f) else CardSubtleBorder,
                                                shape = slotShape
                                            )
                                            .appleTactileClickable(
                                                enabled = !unavailable,
                                                shape = slotShape,
                                                highlightColor = if (isSlotSel) Color.White else MaroonPrimary,
                                                onClick = { onSelectSlot(slot.id) }
                                            )
                                            .testTag("book_slot_${slot.id}")
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 14.dp, vertical = 12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = slot.label,
                                                fontFamily = JetBrainsMono,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = when {
                                                    isSlotSel -> Color.White
                                                    unavailable -> InkMuted
                                                    else -> InkPrimary
                                                }
                                            )
                                            if (isSlotSel) {
                                                SelectedHighlightBadge(label = "Selected", lightOnDark = true)
                                            } else {
                                                Text(
                                                    text = when {
                                                        isClosedDay -> "Day Closed"
                                                        blockReason != null -> "Blocked ($blockReason)"
                                                        isLocked -> "Reserved"
                                                        else -> "Available"
                                                    },
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = if (unavailable) InkMuted else EmeraldGreen,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
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
        }

        item(key = "step_03_location") {
            Box(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                AppleGlassCard(
                    shape = RoundedCornerShape(26.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "03. Daet Location Details",
                            style = MaterialTheme.typography.titleMedium,
                            color = InkPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Houses, boarding houses & dorms in Daet",
                            style = MaterialTheme.typography.bodySmall,
                            color = InkSecondary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Barangay / Area in Daet",
                            style = MaterialTheme.typography.labelLarge,
                            color = InkPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = PillTrackBg.copy(alpha = 0.72f),
                            border = BorderStroke(0.8.dp, CardSubtleBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                DormCleanCatalog.barangays.chunked(2).forEach { pair ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        pair.forEach { brgy ->
                                            val isSel = draft.barangay == brgy.name
                                            val brgyShape = RoundedCornerShape(14.dp)
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .minimumInteractiveComponentSize()
                                                    .then(
                                                        if (isSel) {
                                                            Modifier
                                                                .shadow(5.dp, brgyShape, spotColor = MaroonPrimary.copy(alpha = 0.35f))
                                                                .background(
                                                                    Brush.linearGradient(listOf(MaroonBright, MaroonPrimary, MaroonDeep)),
                                                                    brgyShape
                                                                )
                                                                .border(1.5.dp, Color.White.copy(alpha = 0.6f), brgyShape)
                                                        } else {
                                                            Modifier
                                                        }
                                                    )
                                                    .appleTactileClickable(
                                                        shape = brgyShape,
                                                        highlightColor = if (isSel) Color.White else MaroonPrimary,
                                                        onClick = {
                                                            onUpdateLocation(brgy.name, null, null, null, null, null)
                                                        }
                                                    )
                                                    .testTag("barangay_${brgy.name}")
                                            ) {
                                                Box(
                                                    contentAlignment = Alignment.Center,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 10.dp, horizontal = 6.dp)
                                                ) {
                                                    Text(
                                                        text = brgy.name,
                                                        style = MaterialTheme.typography.labelMedium,
                                                        color = if (isSel) Color.White else InkPrimary,
                                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = selectedBarangayObj.dispatchNote,
                            style = MaterialTheme.typography.bodySmall,
                            color = InkSecondary
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        FieldLabel("House, Boarding House, or Street Address *")
                        DormCleanTextField(
                            value = draft.streetAddress,
                            onValueChange = { onUpdateLocation(null, it, null, null, null, null) },
                            placeholder = "e.g., Santos Boarding House, F. Pimentel Ave",
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("book_address_input")
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                FieldLabel("Room / House # *")
                                DormCleanTextField(
                                    value = draft.roomNumber,
                                    onValueChange = { onUpdateLocation(null, null, it, null, null, null) },
                                    placeholder = "Room 4 / #18",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("book_room_input")
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                FieldLabel("Floor / Gate (opt.)")
                                DormCleanTextField(
                                    value = draft.floorGate,
                                    onValueChange = { onUpdateLocation(null, null, null, it, null, null) },
                                    placeholder = "2nd Flr / Blue Gate",
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        FieldLabel("Nearest Landmark (optional)")
                        DormCleanTextField(
                            value = draft.landmark,
                            onValueChange = { onUpdateLocation(null, null, null, null, it, null) },
                            placeholder = "e.g., Across convenience store, near terminal",
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        FieldLabel("Arrival & Gate Notes (optional)")
                        Spacer(modifier = Modifier.height(4.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            DormCleanCatalog.arrivalNotePresets.forEach { preset ->
                                val active = draft.arrivalNotes.contains(preset)
                                val chipShape = RoundedCornerShape(14.dp)
                                Surface(
                                    shape = chipShape,
                                    color = if (active) MaroonPrimary else MaroonTint,
                                    border = BorderStroke(if (active) 1.8.dp else 1.dp, if (active) Color.White.copy(alpha = 0.55f) else MaroonSoftBorder),
                                    shadowElevation = if (active) 4.dp else 0.dp,
                                    modifier = Modifier.appleTactileClickable(
                                        shape = chipShape,
                                        highlightColor = if (active) Color.White else MaroonPrimary,
                                        onClick = { onToggleArrivalPreset(preset) }
                                    )
                                ) {
                                    Text(
                                        text = if (active) "✓ $preset" else "+ $preset",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (active) Color.White else MaroonPrimary,
                                        fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                                    )
                                }
                            }
                        }
                        if (draft.arrivalNotes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            DormCleanTextField(
                                value = draft.arrivalNotes,
                                onValueChange = { onUpdateLocation(null, null, null, null, null, it) },
                                singleLine = false,
                                minLines = 1,
                                maxLines = 3,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        item(key = "step_04_contact") {
            Box(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                AppleGlassCard(
                    shape = RoundedCornerShape(26.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "04. Direct Contact",
                            style = MaterialTheme.typography.titleMedium,
                            color = InkPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "How our Daet crew updates you before arriving",
                            style = MaterialTheme.typography.bodySmall,
                            color = InkSecondary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        FieldLabel("Preferred Contact Channel")
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = PillTrackBg.copy(alpha = 0.72f),
                            border = BorderStroke(0.8.dp, CardSubtleBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                DormCleanCatalog.contactChannels.chunked(2).forEach { pair ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        pair.forEach { channel ->
                                            val isSel = draft.contactChannel == channel
                                            val chShape = RoundedCornerShape(14.dp)
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .minimumInteractiveComponentSize()
                                                    .then(
                                                        if (isSel) {
                                                            Modifier
                                                                .shadow(5.dp, chShape, spotColor = MaroonPrimary.copy(alpha = 0.35f))
                                                                .background(
                                                                    Brush.linearGradient(listOf(MaroonBright, MaroonPrimary, MaroonDeep)),
                                                                    chShape
                                                                )
                                                                .border(1.5.dp, Color.White.copy(alpha = 0.6f), chShape)
                                                        } else {
                                                            Modifier
                                                        }
                                                    )
                                                    .appleTactileClickable(
                                                        shape = chShape,
                                                        highlightColor = if (isSel) Color.White else MaroonPrimary,
                                                        onClick = { onUpdateContact(channel, null, null) }
                                                    )
                                            ) {
                                                Box(
                                                    contentAlignment = Alignment.Center,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 10.dp)
                                                ) {
                                                    Text(
                                                        text = channel,
                                                        style = MaterialTheme.typography.labelLarge,
                                                        color = if (isSel) Color.White else InkPrimary,
                                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                                        maxLines = 1
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        FieldLabel("Your Name *")
                        DormCleanTextField(
                            value = draft.customerName,
                            onValueChange = { onUpdateContact(null, it, null) },
                            placeholder = "Full name",
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("book_customer_name_input")
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        val handleLabel = if (draft.contactChannel == "Messenger") {
                            "Messenger Name or m.me Link *"
                        } else {
                            "${draft.contactChannel} Mobile Number *"
                        }
                        val handlePlaceholder = if (draft.contactChannel == "Messenger") {
                            "m.me/juan.delacruz or FB Name"
                        } else {
                            "09XX XXX XXXX"
                        }
                        FieldLabel(handleLabel)
                        DormCleanTextField(
                            value = draft.contactHandle,
                            onValueChange = { onUpdateContact(null, null, it) },
                            placeholder = handlePlaceholder,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("book_contact_handle_input")
                        )
                    }
                }
            }
        }

        item(key = "step_05_payment") {
            Box(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                AppleGlassCard(
                    shape = RoundedCornerShape(26.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "05. Payment & Verification",
                            style = MaterialTheme.typography.titleMedium,
                            color = InkPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Demo GCash QR with ref # or cash upon crew arrival",
                            style = MaterialTheme.typography.bodySmall,
                            color = InkSecondary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = PillTrackBg.copy(alpha = 0.72f),
                            border = BorderStroke(0.8.dp, CardSubtleBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(5.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val isGcash = draft.paymentMethod == "gcash_qr"
                                val payShape = RoundedCornerShape(14.dp)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .minimumInteractiveComponentSize()
                                        .then(
                                            if (isGcash) {
                                                Modifier
                                                    .shadow(6.dp, payShape, spotColor = GCashBlue.copy(alpha = 0.4f))
                                                    .background(Brush.linearGradient(listOf(GCashBlue, GCashBlueDeep)), payShape)
                                                    .border(1.5.dp, Color.White.copy(alpha = 0.65f), payShape)
                                            } else {
                                                Modifier
                                            }
                                        )
                                        .appleTactileClickable(
                                            shape = payShape,
                                            highlightColor = if (isGcash) Color.White else GCashBlue,
                                            onClick = { onUpdatePayment("gcash_qr", null, null) }
                                        )
                                        .testTag("pay_gcash_btn")
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 11.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.QrCode2,
                                            contentDescription = null,
                                            tint = if (isGcash) Color.White else InkPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "GCash QR",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = if (isGcash) Color.White else InkPrimary,
                                            fontWeight = if (isGcash) FontWeight.Bold else FontWeight.Medium,
                                            maxLines = 1
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .minimumInteractiveComponentSize()
                                        .then(
                                            if (!isGcash) {
                                                Modifier
                                                    .shadow(6.dp, payShape, spotColor = MaroonPrimary.copy(alpha = 0.4f))
                                                    .background(
                                                        Brush.linearGradient(listOf(MaroonBright, MaroonPrimary, MaroonDeep)),
                                                        payShape
                                                    )
                                                    .border(1.5.dp, Color.White.copy(alpha = 0.65f), payShape)
                                            } else {
                                                Modifier
                                            }
                                        )
                                        .appleTactileClickable(
                                            shape = payShape,
                                            highlightColor = if (!isGcash) Color.White else MaroonPrimary,
                                            onClick = { onUpdatePayment("cash_on_arrival", null, null) }
                                        )
                                        .testTag("pay_cash_btn")
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 11.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Payments,
                                            contentDescription = null,
                                            tint = if (!isGcash) Color.White else InkPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Cash Arrival",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = if (!isGcash) Color.White else InkPrimary,
                                            fontWeight = if (!isGcash) FontWeight.Bold else FontWeight.Medium,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (draft.paymentMethod == "gcash_qr") {
                            val passShape = RoundedCornerShape(22.dp)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(6.dp, passShape, spotColor = GCashBlue.copy(alpha = 0.16f))
                                    .clip(passShape)
                                    .background(
                                        Brush.verticalGradient(listOf(GCashBlueLight, CoolBlueDeep.copy(alpha = 0.7f))),
                                        passShape
                                    )
                                    .border(1.dp, GCashBlue.copy(alpha = 0.22f), passShape)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Brush.linearGradient(listOf(GCashBlue, GCashBlueDeep)))
                                            .padding(horizontal = 16.dp, vertical = 12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.PhoneAndroid,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "GCash QR",
                                                style = MaterialTheme.typography.titleSmall,
                                                color = Color.White
                                            )
                                        }
                                        Text(
                                            text = "Demo Mode",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White.copy(alpha = 0.92f)
                                        )
                                    }

                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "DormClean Daet",
                                                style = MaterialTheme.typography.titleSmall,
                                                color = InkPrimary
                                            )
                                            Column(horizontalAlignment = Alignment.End) {
                                                Text(
                                                    text = "Amount Due",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = InkSecondary
                                                )
                                                Text(
                                                    text = "₱$finalTotal",
                                                    fontFamily = JetBrainsMono,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 18.sp,
                                                    color = GCashBlue
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        DemoQrCodeMatrix(
                                            seedText = "DormCleanDaet_${selectedDateOpt.dateKey}_$finalTotal",
                                            modifier = Modifier.size(144.dp)
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        DormCleanTextField(
                                            value = draft.gcashRefNumber,
                                            onValueChange = { onUpdatePayment(null, it, null) },
                                            label = "GCash Reference # (optional)",
                                            placeholder = "e.g., 1002394812934",
                                            accentColor = GCashBlue,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("book_gcash_ref_input")
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        OutlinedButton(
                                            onClick = {
                                                photoPickerLauncher.launch(
                                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                )
                                            },
                                            shape = RoundedCornerShape(14.dp),
                                            border = BorderStroke(1.dp, GCashBlue.copy(alpha = 0.35f)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ReceiptLong,
                                                contentDescription = null,
                                                tint = GCashBlue,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = draft.receiptLabel.ifBlank { "Attach Demo Receipt Photo" },
                                                style = MaterialTheme.typography.labelMedium,
                                                color = GCashBlue,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(Brush.linearGradient(listOf(BlushPinkCard, BlushPinkDeep)))
                                    .border(1.dp, SpecularWhiteBorder, RoundedCornerShape(18.dp))
                            ) {
                                Text(
                                    text = "Pay ₱$finalTotal in cash directly to your Daet cleaner upon arrival. No online pre-payment required.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = InkPrimary,
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        item(key = "referral") {
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
                                    imageVector = Icons.Default.CardGiftcard,
                                    contentDescription = null,
                                    tint = MaroonPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Referral Code (₱50 off)",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = InkPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (referralDiscount > 0L) EmeraldBg else MaroonTint,
                                border = BorderStroke(1.dp, if (referralDiscount > 0L) EmeraldGreen else MaroonSoftBorder),
                                modifier = Modifier
                                    .appleTactileClickable(
                                        shape = RoundedCornerShape(10.dp),
                                        highlightColor = MaroonPrimary,
                                        onClick = onUseDaet50
                                    )
                                    .testTag("use_daet50_btn")
                            ) {
                                Text(
                                    text = if (referralDiscount > 0L) "DAET50 Active ✓" else "Use DAET50",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (referralDiscount > 0L) EmeraldGreen else MaroonPrimary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        DormCleanTextField(
                            value = draft.referralCode,
                            onValueChange = onUpdateReferral,
                            placeholder = "ENTER DORMMATE CODE (E.G., DAET50)",
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("book_referral_input")
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Share your code after booking: friend gets ₱50 off, you get ₱50 off next clean.",
                            style = MaterialTheme.typography.bodySmall,
                            color = InkSecondary
                        )
                    }
                }
            }
        }

        item(key = "others") {
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
                            text = "Anything else you want cleaned on top of your selected plan:",
                            style = MaterialTheme.typography.bodySmall,
                            color = InkSecondary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        DormCleanTextField(
                            value = draft.othersRequest,
                            onValueChange = onUpdateOthers,
                            placeholder = "e.g., Clean electric fan, wipe study table & shoe rack...",
                            singleLine = false,
                            minLines = 3,
                            maxLines = 4,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        item(key = "summary") {
            Box(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                AppleGlassCard(
                    shape = RoundedCornerShape(26.dp),
                    borderColor = SpecularWhiteBorder,
                    elevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Booking Summary",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaroonPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        val occText = if (draft.occupancyType == "shared") "3–4 Pax Shared" else "Solo / 2-Pax"
                        Text(
                            text = "${selectedPkg.shortTitle} • $occText",
                            style = MaterialTheme.typography.headlineSmall,
                            color = InkPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        HorizontalDivider(
                            color = CardSubtleBorder,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )

                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = MaroonPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = selectedDateOpt.fullFormatted,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = InkPrimary
                                )
                                Text(
                                    text = selectedSlot?.label ?: "No open slot selected",
                                    fontFamily = JetBrainsMono,
                                    fontSize = 12.sp,
                                    color = if (selectedSlot != null) EmeraldGreen else MaroonPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.PersonAddAlt1,
                                contentDescription = null,
                                tint = if (selectedAgent != null && selectedSlot != null) EmeraldGreen else AmberText,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (selectedAgent != null && selectedSlot != null) {
                                        selectedAgent.fullName
                                    } else {
                                        "Select your agent & slot in Step 02"
                                    },
                                    style = MaterialTheme.typography.titleSmall,
                                    color = if (selectedAgent != null && selectedSlot != null) InkPrimary else AmberText
                                )
                                Text(
                                    text = "Assigned Daet cleaner",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = InkSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val hasAddress = draft.streetAddress.trim().length >= 2 && draft.roomNumber.trim().isNotEmpty()
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = if (hasAddress) EmeraldGreen else AmberText,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (hasAddress) {
                                        "${draft.streetAddress.trim()}, ${draft.roomNumber.trim()}"
                                    } else {
                                        "Enter address & room # in Step 03"
                                    },
                                    style = MaterialTheme.typography.titleSmall,
                                    color = if (hasAddress) InkPrimary else AmberText
                                )
                                Text(
                                    text = "${draft.barangay}, Daet, Camarines Norte",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = InkSecondary
                                )
                            }
                        }

                        HorizontalDivider(
                            color = CardSubtleBorder,
                            modifier = Modifier.padding(vertical = 14.dp)
                        )

                        if (promoDiscount > 0L) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(promoName, style = MaterialTheme.typography.bodyMedium, color = EmeraldGreen)
                                Text("-₱$promoDiscount", fontFamily = JetBrainsMono, color = EmeraldGreen, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }

                        if (referralDiscount > 0L) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Referral (DAET50)", style = MaterialTheme.typography.bodyMedium, color = EmeraldGreen)
                                Text("-₱$referralDiscount", fontFamily = JetBrainsMono, color = EmeraldGreen, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total Due",
                                style = MaterialTheme.typography.titleMedium,
                                color = InkPrimary
                            )
                            Text(
                                text = "₱$finalTotal",
                                fontFamily = JetBrainsMono,
                                fontWeight = FontWeight.Bold,
                                fontSize = 26.sp,
                                color = MaroonPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        AppleGradientButton(
                            onClick = onSubmitBooking,
                            enabled = !isSubmitting,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("book_confirm_submit_btn")
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "Confirm & Reserve Slot",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = InkPrimary,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
private fun BookAgentSelectorCard(
    agent: CleanerAgent,
    openCount: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    val baseAvatar = Color(agent.avatarColorHex)
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (selected) 10.dp else 2.dp,
                shape = shape,
                ambientColor = if (selected) MaroonPrimary.copy(alpha = 0.25f) else Color.Black.copy(alpha = 0.05f),
                spotColor = if (selected) MaroonPrimary.copy(alpha = 0.25f) else Color.Black.copy(alpha = 0.05f)
            )
            .background(
                brush = if (selected) {
                    Brush.linearGradient(listOf(BlushPinkCard, MaroonTint))
                } else {
                    Brush.verticalGradient(listOf(CardWhite, SoftInputBg))
                },
                shape = shape
            )
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) MaroonPrimary else CardSubtleBorder,
                shape = shape
            )
            .appleTactileClickable(
                shape = shape,
                highlightColor = MaroonPrimary,
                onClick = onClick
            )
            .testTag("book_agent_${agent.id}")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .shadow(3.dp, CircleShape, spotColor = baseAvatar.copy(alpha = 0.3f))
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(baseAvatar, baseAvatar.copy(alpha = 0.78f)))
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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = agent.fullName,
                        style = MaterialTheme.typography.titleSmall,
                        color = InkPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (selected) {
                        SelectedHighlightBadge(label = "Selected", lightOnDark = false)
                    } else {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = EmeraldBg
                        ) {
                            Text(
                                text = "• Open",
                                color = EmeraldGreen,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "${agent.agentNumberLabel} • ${agent.zoneAndRole}",
                    style = MaterialTheme.typography.bodySmall,
                    color = InkSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(5.dp))
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
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = InkPrimary
                        )
                    }
                    Text(
                        text = "$openCount/5 open",
                        fontFamily = JetBrainsMono,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (openCount > 0) MaroonPrimary else DangerRed
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Surface(
                shape = CircleShape,
                color = if (selected) MaroonPrimary else MaroonTint,
                modifier = Modifier.size(34.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (selected) Icons.Default.Check else Icons.Default.Add,
                        contentDescription = null,
                        tint = if (selected) Color.White else MaroonPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

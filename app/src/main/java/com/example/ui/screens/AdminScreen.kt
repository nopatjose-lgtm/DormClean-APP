package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Booking
import com.example.data.DayConfig
import com.example.data.DormCleanCatalog
import com.example.data.FeedbackReport
import com.example.data.ServiceDateOption
import com.example.data.UiState
import com.example.ui.AdminControlUiState
import com.example.ui.components.AppleGlassCard
import com.example.ui.components.AppleGradientButton
import com.example.ui.components.DormCleanTextField
import com.example.ui.components.appleTactileClickable
import com.example.ui.theme.AmberBg
import com.example.ui.theme.AmberText
import com.example.ui.theme.BlushPinkCard
import com.example.ui.theme.BlushPinkDeep
import com.example.ui.theme.CardSubtleBorder
import com.example.ui.theme.DangerRed
import com.example.ui.theme.EmeraldBright
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GCashBlue
import com.example.ui.theme.InkMuted
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.InkSecondary
import com.example.ui.theme.JetBrainsMono
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
import com.example.ui.theme.SpecularWhiteBorder
import com.example.ui.theme.WarmButterCard
import com.example.ui.theme.WarmButterDeep

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminScreen(
    userDisplayName: String,
    userEmail: String,
    adminUi: AdminControlUiState,
    upcomingDates: List<ServiceDateOption>,
    bookingsState: UiState<List<Booking>>,
    dayConfigs: Map<String, DayConfig>,
    feedbackState: UiState<List<FeedbackReport>>,
    onSignOut: () -> Unit,
    onUpdateFilter: (String) -> Unit,
    onUpdateSearch: (String) -> Unit,
    onSelectDate: (String) -> Unit,
    onUpdateStopReason: (String) -> Unit,
    onToggleForceStop: (String, DayConfig?) -> Unit,
    onUpdateDiscountForm: (String?, Int?, String?) -> Unit,
    onSaveDiscount: (String, DayConfig?) -> Unit,
    onClearDiscount: (String, DayConfig?) -> Unit,
    onSelectAgent: (String) -> Unit,
    onUpdateBlockReason: (String) -> Unit,
    onToggleSlotBlock: (String, String, String, DayConfig?) -> Unit,
    onUpdateBookingStatus: (Booking, String) -> Unit,
    onVerifyPayment: (String) -> Unit,
    onDeleteBooking: (Booking) -> Unit,
    onResolveFeedback: (String, String) -> Unit
) {
    val allBookings = (bookingsState as? UiState.Success)?.data ?: emptyList()
    val allFeedback = (feedbackState as? UiState.Success)?.data ?: emptyList()
    val todayKey = upcomingDates.first().dateKey

    val (pendingCount, todayCount, doneCount) = remember(allBookings, todayKey) {
        Triple(
            allBookings.count { it.status == "pending_review" },
            allBookings.count { it.dateKey == todayKey && it.status != "cancelled" },
            allBookings.count { it.status == "completed" }
        )
    }

    val filteredBookings = remember(allBookings, adminUi.selectedFilter, adminUi.searchQuery) {
        val q = adminUi.searchQuery.trim().lowercase()
        allBookings.filter { b ->
            val matchesFilter = when (adminUi.selectedFilter) {
                "Pending Review" -> b.status == "pending_review"
                "Confirmed" -> b.status == "confirmed"
                "Crew On-Site" -> b.status == "crew_on_site"
                "Completed" -> b.status == "completed"
                else -> true
            }
            val matchesQuery = q.isEmpty() || listOf(
                b.id, b.streetAddress, b.barangay, b.customerName, b.agentName, b.packageName
            ).any { it.lowercase().contains(q) }
            matchesFilter && matchesQuery
        }
    }

    val selectedDateOpt = upcomingDates.firstOrNull { it.dateKey == adminUi.selectedDateKey } ?: upcomingDates.first()
    val selectedDayCfg = dayConfigs[selectedDateOpt.dateKey]
    val isDayStopped = selectedDayCfg?.forceStopped == true
    val openFeedbackCount = remember(allFeedback) { allFeedback.count { it.status == "open" } }

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
                Text(
                    text = "Daet, Camarines Norte",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaroonPrimary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Admin Dispatch & Crew",
                    style = MaterialTheme.typography.displaySmall,
                    color = InkPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Approve student slots, dispatch crew, and manage days off or promos.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkSecondary
                )
            }
        }

        item(key = "admin_identity_and_kpi") {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                AppleGlassCard(
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val initial = userDisplayName.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "A"
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .shadow(4.dp, CircleShape, spotColor = MaroonPrimary.copy(alpha = 0.35f))
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(MaroonBright, MaroonPrimary, MaroonDeep)))
                                .border(1.dp, Color.White.copy(alpha = 0.35f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initial,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = userDisplayName.ifBlank { "Sole Admin" },
                                    style = MaterialTheme.typography.titleSmall,
                                    color = InkPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Brush.linearGradient(listOf(MaroonBright, MaroonPrimary)))
                                ) {
                                    Text(
                                        text = "Admin",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = userEmail,
                                style = MaterialTheme.typography.bodySmall,
                                color = InkSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = onSignOut,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, SoftInputBorder),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text("Sign Out", style = MaterialTheme.typography.labelMedium, color = InkPrimary, maxLines = 1)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AdminKpiCard(
                        label = "Pending",
                        count = pendingCount,
                        brush = Brush.linearGradient(listOf(AmberBg, WarmButterCard, WarmButterDeep)),
                        countColor = AmberText,
                        modifier = Modifier.weight(1f)
                    )
                    AdminKpiCard(
                        label = "Today",
                        count = todayCount,
                        brush = Brush.linearGradient(listOf(BlushPinkCard, BlushPinkDeep)),
                        countColor = MaroonPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    AdminKpiCard(
                        label = "Done",
                        count = doneCount,
                        brush = Brush.linearGradient(listOf(SageMintCard, SageMintDeep)),
                        countColor = EmeraldGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        item(key = "logs_header_and_filters") {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Booking Logs (${filteredBookings.size})",
                    style = MaterialTheme.typography.titleMedium,
                    color = InkPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Grouped by date • Admin-only deletion",
                    style = MaterialTheme.typography.bodySmall,
                    color = InkSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))

                AppleGlassCard(
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(5.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("All", "Pending Review", "Confirmed", "Crew On-Site", "Completed").forEach { filter ->
                            val isSel = adminUi.selectedFilter == filter
                            val filterShape = RoundedCornerShape(14.dp)
                            Box(
                                modifier = Modifier
                                    .then(
                                        if (isSel) {
                                            Modifier
                                                .shadow(5.dp, filterShape, spotColor = MaroonPrimary.copy(alpha = 0.35f))
                                                .background(
                                                    Brush.linearGradient(listOf(MaroonBright, MaroonPrimary, MaroonDeep)),
                                                    filterShape
                                                )
                                                .border(1.5.dp, Color.White.copy(alpha = 0.6f), filterShape)
                                        } else {
                                            Modifier
                                        }
                                    )
                                    .appleTactileClickable(
                                        shape = filterShape,
                                        highlightColor = if (isSel) Color.White else MaroonPrimary,
                                        onClick = { onUpdateFilter(filter) }
                                    )
                                    .testTag("admin_filter_$filter")
                            ) {
                                Text(
                                    text = filter,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isSel) Color.White else InkPrimary,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                    maxLines = 1,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                DormCleanTextField(
                    value = adminUi.searchQuery,
                    onValueChange = onUpdateSearch,
                    placeholder = "Search ID, dorm, barangay, student, crew...",
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = InkMuted)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        if (filteredBookings.isEmpty()) {
            item(key = "logs_empty") {
                Box(
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    AppleGlassCard(
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Brush.linearGradient(listOf(BlushPinkCard, MaroonTint)))
                                    .border(1.dp, MaroonSoftBorder, RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AssignmentTurnedIn,
                                    contentDescription = null,
                                    tint = MaroonPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No matching booking logs",
                                style = MaterialTheme.typography.titleMedium,
                                color = InkPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Student bookings appear here live as soon as they are submitted.",
                                style = MaterialTheme.typography.bodySmall,
                                color = InkSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        } else {
            items(
                items = filteredBookings,
                key = { "admin_booking_${it.id}" }
            ) { booking ->
                Box(
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 5.dp)
                ) {
                    AdminBookingLogCard(
                        booking = booking,
                        onApprove = { onUpdateBookingStatus(booking, "confirmed") },
                        onDispatch = { onUpdateBookingStatus(booking, "crew_on_site") },
                        onComplete = { onUpdateBookingStatus(booking, "completed") },
                        onVerifyPayment = { onVerifyPayment(booking.id) },
                        onDelete = { onDeleteBooking(booking) }
                    )
                }
            }
        }

        item(key = "day_controls") {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    upcomingDates.forEach { dateOpt ->
                        val isSel = dateOpt.dateKey == selectedDateOpt.dateKey
                        val dShape = RoundedCornerShape(18.dp)
                        Box(
                            modifier = Modifier
                                .width(90.dp)
                                .shadow(
                                    elevation = if (isSel) 10.dp else 2.dp,
                                    shape = dShape,
                                    spotColor = if (isSel) MaroonPrimary.copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.05f)
                                )
                                .background(
                                    brush = if (isSel) {
                                        Brush.linearGradient(listOf(MaroonBright, MaroonPrimary, MaroonDeep))
                                    } else {
                                        Brush.verticalGradient(listOf(SoftInputBg, SoftInputBg))
                                    },
                                    shape = dShape
                                )
                                .border(
                                    width = if (isSel) 2.dp else 1.dp,
                                    color = if (isSel) Color.White.copy(alpha = 0.7f) else CardSubtleBorder,
                                    shape = dShape
                                )
                                .appleTactileClickable(
                                    shape = dShape,
                                    highlightColor = if (isSel) Color.White else MaroonPrimary,
                                    onClick = { onSelectDate(dateOpt.dateKey) }
                                )
                                .testTag("admin_date_${dateOpt.dateKey}")
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
                                    color = if (isSel) Color.White.copy(alpha = 0.88f) else InkSecondary,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = dateOpt.dayLabel,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = if (isSel) Color.White else InkPrimary,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                // Force Stop Operations Card
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.EventBusy,
                                    contentDescription = null,
                                    tint = MaroonPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Force Stop Operations",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = InkPrimary
                                )
                            }
                            Text(
                                text = if (isDayStopped) "Stopped" else "Open",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isDayStopped) DangerRed else EmeraldGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        DormCleanTextField(
                            value = adminUi.stopReasonInput,
                            onValueChange = onUpdateStopReason,
                            placeholder = "Reason for closing date",
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            DormCleanCatalog.dayStopPresets.forEach { preset ->
                                val isPresetSel = adminUi.stopReasonInput.equals(preset, ignoreCase = true)
                                val pShape = RoundedCornerShape(12.dp)
                                Surface(
                                    shape = pShape,
                                    color = if (isPresetSel) MaroonPrimary else SoftInputBg,
                                    border = BorderStroke(
                                        if (isPresetSel) 1.5.dp else 1.dp,
                                        if (isPresetSel) Color.White.copy(alpha = 0.6f) else SoftInputBorder
                                    ),
                                    modifier = Modifier.appleTactileClickable(
                                        shape = pShape,
                                        highlightColor = if (isPresetSel) Color.White else MaroonPrimary,
                                        onClick = { onUpdateStopReason(preset) }
                                    )
                                ) {
                                    Text(
                                        text = preset,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (isPresetSel) Color.White else InkPrimary,
                                        fontWeight = if (isPresetSel) FontWeight.Bold else FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        AppleGradientButton(
                            onClick = { onToggleForceStop(selectedDateOpt.dateKey, selectedDayCfg) },
                            height = 50.dp,
                            gradientColors = if (isDayStopped) {
                                listOf(EmeraldBright, EmeraldGreen, Color(0xFF065F46))
                            } else {
                                listOf(MaroonBright, MaroonPrimary, MaroonDeep)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_force_stop_btn")
                        ) {
                            Icon(Icons.Default.EventBusy, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isDayStopped) {
                                    "Reopen ${selectedDateOpt.dayLabel}"
                                } else {
                                    "Force Stop ${selectedDateOpt.dayLabel}"
                                },
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                }

                // Set Day Discount Card
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Percent,
                                    contentDescription = null,
                                    tint = MaroonPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Set Day Discount",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = InkPrimary
                                )
                            }
                            if (selectedDayCfg?.discountEnabled == true) {
                                TextButton(onClick = { onClearDiscount(selectedDateOpt.dateKey, selectedDayCfg) }) {
                                    Text("Clear Active", color = DangerRed, fontSize = 12.sp)
                                }
                            } else {
                                Text(
                                    text = "None Active",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = InkSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = PillTrackBg.copy(alpha = 0.72f),
                            border = BorderStroke(0.8.dp, CardSubtleBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                val isPercent = adminUi.discountType == "percent"
                                val segShape = RoundedCornerShape(13.dp)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .minimumInteractiveComponentSize()
                                        .then(
                                            if (isPercent) {
                                                Modifier
                                                    .shadow(5.dp, segShape, spotColor = MaroonPrimary.copy(alpha = 0.35f))
                                                    .background(
                                                        Brush.linearGradient(listOf(MaroonBright, MaroonPrimary, MaroonDeep)),
                                                        segShape
                                                    )
                                                    .border(1.5.dp, Color.White.copy(alpha = 0.6f), segShape)
                                            } else {
                                                Modifier
                                            }
                                        )
                                        .appleTactileClickable(
                                            shape = segShape,
                                            highlightColor = if (isPercent) Color.White else MaroonPrimary,
                                            onClick = { onUpdateDiscountForm("percent", 20, null) }
                                        )
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 10.dp)
                                    ) {
                                        Text(
                                            text = "% Percent Off",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = if (isPercent) Color.White else InkPrimary,
                                            fontWeight = if (isPercent) FontWeight.Bold else FontWeight.Medium,
                                            maxLines = 1
                                        )
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .minimumInteractiveComponentSize()
                                        .then(
                                            if (!isPercent) {
                                                Modifier
                                                    .shadow(5.dp, segShape, spotColor = MaroonPrimary.copy(alpha = 0.35f))
                                                    .background(
                                                        Brush.linearGradient(listOf(MaroonBright, MaroonPrimary, MaroonDeep)),
                                                        segShape
                                                    )
                                                    .border(1.5.dp, Color.White.copy(alpha = 0.6f), segShape)
                                            } else {
                                                Modifier
                                            }
                                        )
                                        .appleTactileClickable(
                                            shape = segShape,
                                            highlightColor = if (!isPercent) Color.White else MaroonPrimary,
                                            onClick = { onUpdateDiscountForm("peso", 50, null) }
                                        )
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 10.dp)
                                    ) {
                                        Text(
                                            text = "₱ Peso Off",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = if (!isPercent) Color.White else InkPrimary,
                                            fontWeight = if (!isPercent) FontWeight.Bold else FontWeight.Medium,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column(modifier = Modifier.weight(0.9f)) {
                                Text(
                                    text = if (adminUi.discountType == "percent") "Percent (5–80%)" else "Amount (₱10–500)",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = InkSecondary,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = SoftInputBg,
                                    border = BorderStroke(1.dp, SoftInputBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${adminUi.discountValueInput}",
                                            fontFamily = JetBrainsMono,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = InkPrimary
                                        )
                                        Column {
                                            Icon(
                                                imageVector = Icons.Default.KeyboardArrowUp,
                                                contentDescription = "Increase",
                                                tint = MaroonPrimary,
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .appleTactileClickable(
                                                        shape = CircleShape,
                                                        highlightColor = MaroonPrimary,
                                                        onClick = {
                                                            val step = if (adminUi.discountType == "percent") 5 else 25
                                                            val max = if (adminUi.discountType == "percent") 80 else 500
                                                            onUpdateDiscountForm(null, (adminUi.discountValueInput + step).coerceAtMost(max), null)
                                                        }
                                                    )
                                            )
                                            Icon(
                                                imageVector = Icons.Default.KeyboardArrowDown,
                                                contentDescription = "Decrease",
                                                tint = MaroonPrimary,
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .appleTactileClickable(
                                                        shape = CircleShape,
                                                        highlightColor = MaroonPrimary,
                                                        onClick = {
                                                            val step = if (adminUi.discountType == "percent") 5 else 25
                                                            val min = if (adminUi.discountType == "percent") 5 else 10
                                                            onUpdateDiscountForm(null, (adminUi.discountValueInput - step).coerceAtLeast(min), null)
                                                        }
                                                    )
                                            )
                                        }
                                    }
                                }
                            }
                            Column(modifier = Modifier.weight(1.3f)) {
                                Text(
                                    text = "Promo Name",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = InkSecondary,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                DormCleanTextField(
                                    value = adminUi.promoNameInput,
                                    onValueChange = { onUpdateDiscountForm(null, null, it) },
                                    placeholder = "e.g., Finals Promo",
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val presets = if (adminUi.discountType == "percent") listOf(10, 15, 20, 30) else listOf(50, 100, 150, 200)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            presets.forEach { v ->
                                val isPresetSel = adminUi.discountValueInput == v
                                val vShape = RoundedCornerShape(12.dp)
                                Surface(
                                    shape = vShape,
                                    color = if (isPresetSel) MaroonPrimary else MaroonTint,
                                    border = BorderStroke(
                                        if (isPresetSel) 1.5.dp else 1.dp,
                                        if (isPresetSel) Color.White.copy(alpha = 0.6f) else MaroonSoftBorder
                                    ),
                                    modifier = Modifier.appleTactileClickable(
                                        shape = vShape,
                                        highlightColor = if (isPresetSel) Color.White else MaroonPrimary,
                                        onClick = { onUpdateDiscountForm(null, v, null) }
                                    )
                                ) {
                                    Text(
                                        text = if (adminUi.discountType == "percent") "$v%" else "₱$v",
                                        fontFamily = JetBrainsMono,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isPresetSel) Color.White else MaroonPrimary,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val quickPreview = if (adminUi.discountType == "percent") {
                            300 - (300 * adminUi.discountValueInput / 100)
                        } else {
                            (300 - adminUi.discountValueInput).coerceAtLeast(0)
                        }
                        val stdPreview = if (adminUi.discountType == "percent") {
                            500 - (500 * adminUi.discountValueInput / 100)
                        } else {
                            (500 - adminUi.discountValueInput).coerceAtLeast(0)
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Brush.linearGradient(listOf(BlushPinkCard, MaroonTint)))
                                .border(1.dp, MaroonSoftBorder, RoundedCornerShape(14.dp))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Quick: ₱$quickPreview",
                                    fontFamily = JetBrainsMono,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaroonPrimary
                                )
                                Text(
                                    text = "Standard: ₱$stdPreview",
                                    fontFamily = JetBrainsMono,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaroonPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        AppleGradientButton(
                            onClick = { onSaveDiscount(selectedDateOpt.dateKey, selectedDayCfg) },
                            height = 50.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_save_discount_btn")
                        ) {
                            Text(
                                text = "Save Discount (${selectedDateOpt.dayLabel})",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                }

                // Agent Slot Availability Card
                AppleGlassCard(
                    shape = RoundedCornerShape(26.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Agent Slots (${selectedDateOpt.dayLabel})",
                            style = MaterialTheme.typography.titleMedium,
                            color = InkPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Pick an agent to block or reopen 2-hr windows",
                            style = MaterialTheme.typography.bodySmall,
                            color = InkSecondary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            DormCleanCatalog.agents.forEach { agent ->
                                val isSel = adminUi.selectedAgentId == agent.id
                                val agShape = RoundedCornerShape(14.dp)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .minimumInteractiveComponentSize()
                                        .then(
                                            if (isSel) {
                                                Modifier
                                                    .shadow(5.dp, agShape, spotColor = MaroonPrimary.copy(alpha = 0.35f))
                                                    .background(
                                                        Brush.linearGradient(listOf(MaroonBright, MaroonPrimary, MaroonDeep)),
                                                        agShape
                                                    )
                                                    .border(1.5.dp, Color.White.copy(alpha = 0.65f), agShape)
                                            } else {
                                                Modifier
                                                    .background(SoftInputBg, agShape)
                                                    .border(1.dp, SoftInputBorder, agShape)
                                            }
                                        )
                                        .appleTactileClickable(
                                            shape = agShape,
                                            highlightColor = if (isSel) Color.White else MaroonPrimary,
                                            onClick = { onSelectAgent(agent.id) }
                                        )
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 6.dp, vertical = 10.dp)
                                    ) {
                                        Text(
                                            text = agent.shortName,
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

                        Spacer(modifier = Modifier.height(12.dp))

                        DormCleanTextField(
                            value = adminUi.blockReasonInput,
                            onValueChange = onUpdateBlockReason,
                            placeholder = "Block reason (e.g., Break)",
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                            DormCleanCatalog.timeSlots.forEach { slot ->
                                val key = "${adminUi.selectedAgentId}__${slot.id}"
                                val blockReason = selectedDayCfg?.blockedSlots?.get(key)
                                val isBlocked = blockReason != null
                                val rowShape = RoundedCornerShape(14.dp)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .minimumInteractiveComponentSize()
                                        .background(if (isBlocked) Color(0xFFFFF0F3) else SoftInputBg, rowShape)
                                        .border(
                                            width = if (isBlocked) 1.5.dp else 1.dp,
                                            color = if (isBlocked) DangerRed.copy(alpha = 0.55f) else CardSubtleBorder,
                                            shape = rowShape
                                        )
                                        .appleTactileClickable(
                                            shape = rowShape,
                                            highlightColor = if (isBlocked) EmeraldGreen else MaroonPrimary,
                                            onClick = {
                                                onToggleSlotBlock(
                                                    selectedDateOpt.dateKey,
                                                    adminUi.selectedAgentId,
                                                    slot.id,
                                                    selectedDayCfg
                                                )
                                            }
                                        )
                                        .padding(horizontal = 12.dp, vertical = 10.dp)
                                        .testTag("admin_slot_${slot.id}"),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = slot.label,
                                        fontFamily = JetBrainsMono,
                                        fontSize = 13.sp,
                                        color = if (isBlocked) DangerRed else InkPrimary,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (isBlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = if (isBlocked) EmeraldGreen else InkPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isBlocked) "Reopen ($blockReason)" else "Block",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = if (isBlocked) EmeraldGreen else InkPrimary,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item(key = "feedback_header") {
            Box(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
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
                                modifier = Modifier.weight(1f),
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
                                        imageVector = Icons.Default.ChatBubbleOutline,
                                        contentDescription = null,
                                        tint = MaroonPrimary,
                                        modifier = Modifier.size(19.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Feedback & Crew Fixes",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = InkPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "Review student reports & log fixes",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = InkSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "$openFeedbackCount open",
                                fontFamily = JetBrainsMono,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaroonPrimary
                            )
                        }

                        if (allFeedback.isEmpty()) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "No student feedback reports submitted yet.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = InkSecondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        if (allFeedback.isNotEmpty()) {
            items(
                items = allFeedback,
                key = { "admin_fb_${it.id}" }
            ) { fb ->
                Box(
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 5.dp)
                ) {
                    AdminFeedbackItemCard(
                        report = fb,
                        onResolve = { note -> onResolveFeedback(fb.id, note) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminKpiCard(
    label: String,
    count: Int,
    brush: Brush,
    countColor: Color,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(22.dp)
    Box(
        modifier = modifier
            .shadow(5.dp, shape, spotColor = Color.Black.copy(alpha = 0.06f))
            .clip(shape)
            .background(brush, shape)
            .border(1.dp, SpecularWhiteBorder, shape)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 13.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = InkSecondary,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "$count",
                fontFamily = JetBrainsMono,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = countColor
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AdminBookingLogCard(
    booking: Booking,
    onApprove: () -> Unit,
    onDispatch: () -> Unit,
    onComplete: () -> Unit,
    onVerifyPayment: () -> Unit,
    onDelete: () -> Unit
) {
    AppleGlassCard(
        shape = RoundedCornerShape(22.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${booking.customerName} • ${booking.barangay}",
                    style = MaterialTheme.typography.titleSmall,
                    color = InkPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete log",
                        tint = DangerRed
                    )
                }
            }
            Text(
                text = "${booking.packageName} • ₱${booking.totalAmount} (${booking.status.replace('_', ' ')})",
                fontFamily = JetBrainsMono,
                fontSize = 12.sp,
                color = MaroonPrimary,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${booking.dateLabel} • ${booking.slotLabel} • ${booking.agentName}",
                style = MaterialTheme.typography.bodySmall,
                color = InkSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${booking.streetAddress}, ${booking.roomNumber} (${booking.contactChannel}: ${booking.contactHandle})",
                style = MaterialTheme.typography.bodySmall,
                color = InkSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (booking.gcashRefNumber.isNotBlank()) {
                Text(
                    text = "GCash Ref: ${booking.gcashRefNumber} (${booking.paymentStatus})",
                    fontFamily = JetBrainsMono,
                    fontSize = 11.sp,
                    color = GCashBlue
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (booking.status == "pending_review") {
                    Button(
                        onClick = onApprove,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaroonPrimary)
                    ) { Text("Approve", fontSize = 12.sp) }
                }
                if (booking.status == "confirmed") {
                    Button(
                        onClick = onDispatch,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GCashBlue)
                    ) { Text("Crew On-Site", fontSize = 12.sp) }
                }
                if (booking.status == "crew_on_site" || booking.status == "confirmed") {
                    Button(
                        onClick = onComplete,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                    ) { Text("Mark Completed", fontSize = 12.sp) }
                }
                if (booking.paymentMethod == "gcash_qr" && booking.paymentStatus != "verified") {
                    OutlinedButton(onClick = onVerifyPayment, shape = RoundedCornerShape(12.dp)) {
                        Text("Verify GCash", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminFeedbackItemCard(
    report: FeedbackReport,
    onResolve: (String) -> Unit
) {
    var resolutionInput by remember(report.id) { mutableStateOf(report.adminResolution) }
    AppleGlassCard(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${report.category} • ${report.userName}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaroonPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = report.status.uppercase(),
                    fontFamily = JetBrainsMono,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (report.status == "resolved") EmeraldGreen else AmberText
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = report.message,
                style = MaterialTheme.typography.bodySmall,
                color = InkPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (report.status == "resolved") {
                Text(
                    text = "Logged Fix: ${report.adminResolution}",
                    style = MaterialTheme.typography.bodySmall,
                    color = EmeraldGreen,
                    fontWeight = FontWeight.SemiBold
                )
            } else {
                DormCleanTextField(
                    value = resolutionInput,
                    onValueChange = { resolutionInput = it.take(500) },
                    placeholder = "Log what your crew fixed / learned...",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                AppleGradientButton(
                    onClick = { onResolve(resolutionInput) },
                    enabled = resolutionInput.trim().isNotEmpty(),
                    height = 46.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Log Lesson & Mark Fixed", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

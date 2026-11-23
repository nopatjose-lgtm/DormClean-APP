package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.mutableLongStateOf
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
import com.example.data.DormCleanCatalog
import com.example.data.FeedbackReport
import com.example.data.UiState
import com.example.ui.components.AppleGlassCard
import com.example.ui.components.AppleGradientButton
import com.example.ui.components.DormCleanTextField
import com.example.ui.components.appleTactileClickable
import com.example.ui.theme.AmberBg
import com.example.ui.theme.AmberGold
import com.example.ui.theme.AmberText
import com.example.ui.theme.BlushPinkCard
import com.example.ui.theme.BlushPinkDeep
import com.example.ui.theme.CardSubtleBorder
import com.example.ui.theme.CardWhite
import com.example.ui.theme.DangerBg
import com.example.ui.theme.DangerRed
import com.example.ui.theme.EmeraldBg
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GCashBlue
import com.example.ui.theme.GCashBlueLight
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.InkSecondary
import com.example.ui.theme.JetBrainsMono
import com.example.ui.theme.MaroonBright
import com.example.ui.theme.MaroonDeep
import com.example.ui.theme.MaroonPrimary
import com.example.ui.theme.MaroonSoftBorder
import com.example.ui.theme.MaroonTint
import com.example.ui.theme.PillTrackBg
import com.example.ui.theme.SoftInputBg
import com.example.ui.theme.SoftInputBorder
import com.example.ui.theme.SpecularWhiteBorder

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BookingsScreen(
    userDisplayName: String,
    userEmail: String,
    isAdminUser: Boolean,
    bookingsState: UiState<List<Booking>>,
    feedbackState: UiState<List<FeedbackReport>>,
    isSubmitting: Boolean,
    onSignOut: () -> Unit,
    onBookFirstSlot: () -> Unit,
    onRebookBooking: (Booking) -> Unit,
    onSubmitGcashRef: (String, String) -> Unit,
    onCancelBooking: (Booking) -> Unit,
    onSubmitRatingAndTip: (String, Long, Long, String) -> Unit,
    onSubmitFeedback: (String, String, String, String, () -> Unit) -> Unit
) {
    var selectedFeedbackCategory by remember { mutableStateOf(DormCleanCatalog.feedbackCategories.first()) }
    var relatedBookingId by remember { mutableStateOf("") }
    var relatedLabel by remember { mutableStateOf("General Service / DormClean App") }
    var feedbackDropdownExpanded by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf("") }

    val bookingsList = (bookingsState as? UiState.Success)?.data ?: emptyList()
    val feedbackList = (feedbackState as? UiState.Success)?.data ?: emptyList()

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
                    text = "My Bookings & Payments",
                    style = MaterialTheme.typography.displaySmall,
                    color = InkPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Track crew arrival, submit Demo GCash ref #, leave ratings & tips, or send feedback.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkSecondary
                )
            }
        }

        item(key = "account_card") {
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val initial = userDisplayName.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "D"
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
                                    text = userDisplayName.ifBlank { "DormClean Student" },
                                    style = MaterialTheme.typography.titleSmall,
                                    color = InkPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaroonTint,
                                    border = BorderStroke(1.dp, MaroonSoftBorder)
                                ) {
                                    Text(
                                        text = if (isAdminUser) "Admin" else "Student",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaroonPrimary,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
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
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("bookings_sign_out_btn")
                        ) {
                            Text(
                                text = "Sign Out",
                                style = MaterialTheme.typography.labelMedium,
                                color = InkPrimary,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        when (bookingsState) {
            is UiState.Loading -> {
                item(key = "bookings_loading") {
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
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = MaroonPrimary)
                            }
                        }
                    }
                }
            }

            is UiState.Error -> {
                item(key = "bookings_error") {
                    Box(
                        modifier = Modifier
                            .widthIn(max = 600.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        AppleGlassCard(
                            shape = RoundedCornerShape(24.dp),
                            containerBrush = Brush.verticalGradient(listOf(DangerBg, DangerBg)),
                            borderColor = DangerRed.copy(alpha = 0.25f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = bookingsState.message,
                                color = DangerRed,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }

            is UiState.Success -> {
                if (bookingsList.isEmpty()) {
                    item(key = "bookings_empty") {
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
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "No cleanings booked yet",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = InkPrimary,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Pick a 2-hr slot and enter your Daet house or boarding house address to reserve our crew.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = InkSecondary,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(18.dp))
                                    AppleGradientButton(
                                        onClick = onBookFirstSlot,
                                        height = 52.dp,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("bookings_first_slot_btn")
                                    ) {
                                        Text(
                                            text = "Book Your First Slot",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    items(
                        items = bookingsList,
                        key = { it.id }
                    ) { booking ->
                        Box(
                            modifier = Modifier
                                .widthIn(max = 600.dp)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            StudentBookingCard(
                                booking = booking,
                                onRebook = { onRebookBooking(booking) },
                                onSubmitGcashRef = { ref -> onSubmitGcashRef(booking.id, ref) },
                                onCancel = { onCancelBooking(booking) },
                                onSubmitRating = { r, tip, note ->
                                    onSubmitRatingAndTip(booking.id, r, tip, note)
                                }
                            )
                        }
                    }
                }
            }
        }

        item(key = "feedback_section") {
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
                        Row(verticalAlignment = Alignment.Top) {
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Feedback & Lessons Learned",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = InkPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Report a cleaning issue, timing delay, or bug so our crew can fix it.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = InkSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            DormCleanCatalog.feedbackCategories.forEach { cat ->
                                val isSel = selectedFeedbackCategory == cat
                                val catShape = RoundedCornerShape(14.dp)
                                Box(
                                    modifier = Modifier
                                        .shadow(
                                            elevation = if (isSel) 5.dp else 0.dp,
                                            shape = catShape,
                                            spotColor = MaroonPrimary.copy(alpha = 0.35f)
                                        )
                                        .background(
                                            brush = if (isSel) {
                                                Brush.linearGradient(listOf(MaroonBright, MaroonPrimary, MaroonDeep))
                                            } else {
                                                Brush.verticalGradient(listOf(SoftInputBg, SoftInputBg))
                                            },
                                            shape = catShape
                                        )
                                        .border(
                                            width = if (isSel) 1.5.dp else 1.dp,
                                            color = if (isSel) Color.White.copy(alpha = 0.6f) else SoftInputBorder,
                                            shape = catShape
                                        )
                                        .appleTactileClickable(
                                            shape = catShape,
                                            highlightColor = if (isSel) Color.White else MaroonPrimary,
                                            onClick = { selectedFeedbackCategory = cat }
                                        )
                                        .testTag("feedback_cat_$cat")
                                ) {
                                    Text(
                                        text = if (isSel) "✓ $cat" else cat,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (isSel) Color.White else InkPrimary,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Box {
                            val ddShape = RoundedCornerShape(16.dp)
                            Surface(
                                shape = ddShape,
                                color = SoftInputBg,
                                border = BorderStroke(1.dp, SoftInputBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .minimumInteractiveComponentSize()
                                    .appleTactileClickable(
                                        shape = ddShape,
                                        highlightColor = MaroonPrimary,
                                        onClick = { feedbackDropdownExpanded = true }
                                    )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 13.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = relatedLabel,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = InkPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Select booking",
                                        tint = InkSecondary
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = feedbackDropdownExpanded,
                                onDismissRequest = { feedbackDropdownExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("General Service / DormClean App") },
                                    onClick = {
                                        relatedBookingId = ""
                                        relatedLabel = "General Service / DormClean App"
                                        feedbackDropdownExpanded = false
                                    }
                                )
                                bookingsList.forEach { b ->
                                    val label = "${b.packageName} • ${b.dateLabel}"
                                    DropdownMenuItem(
                                        text = { Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                        onClick = {
                                            relatedBookingId = b.id
                                            relatedLabel = label
                                            feedbackDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        DormCleanTextField(
                            value = feedbackMessage,
                            onValueChange = { feedbackMessage = it.take(500) },
                            placeholder = "Share what happened or what we can improve next visit...",
                            singleLine = false,
                            minLines = 3,
                            maxLines = 5,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("feedback_message_input")
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        AppleGradientButton(
                            onClick = {
                                onSubmitFeedback(
                                    selectedFeedbackCategory,
                                    relatedBookingId,
                                    relatedLabel,
                                    feedbackMessage
                                ) {
                                    feedbackMessage = ""
                                }
                            },
                            enabled = !isSubmitting && feedbackMessage.trim().length >= 3,
                            height = 50.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("feedback_submit_btn")
                        ) {
                            Text(
                                text = "Submit Feedback",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (feedbackList.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = CardSubtleBorder)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Your Submitted Reports (${feedbackList.size})",
                                style = MaterialTheme.typography.titleSmall,
                                color = InkPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                feedbackList.forEach { fb ->
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = SoftInputBg,
                                        border = BorderStroke(1.dp, CardSubtleBorder),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = fb.category,
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = MaroonPrimary
                                                )
                                                Surface(
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = if (fb.status == "resolved") EmeraldBg else AmberBg
                                                ) {
                                                    Text(
                                                        text = if (fb.status == "resolved") "Resolved" else "Open",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = if (fb.status == "resolved") EmeraldGreen else AmberText,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = fb.message,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = InkPrimary
                                            )
                                            if (fb.adminResolution.isNotBlank()) {
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(
                                                    text = "Crew Fix: ${fb.adminResolution}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = EmeraldGreen,
                                                    fontWeight = FontWeight.SemiBold
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
    }
}

@Composable
private fun StudentBookingCard(
    booking: Booking,
    onRebook: () -> Unit,
    onSubmitGcashRef: (String) -> Unit,
    onCancel: () -> Unit,
    onSubmitRating: (Long, Long, String) -> Unit
) {
    var gcashRefInput by remember(booking.id) { mutableStateOf(booking.gcashRefNumber) }
    var ratingInput by remember(booking.id) { mutableLongStateOf(if (booking.rating > 0L) booking.rating else 5L) }
    var tipInput by remember(booking.id) { mutableLongStateOf(if (booking.tipAmount > 0L) booking.tipAmount else 20L) }
    var reviewNote by remember(booking.id) { mutableStateOf(booking.reviewComment) }

    val (statusLabel, statusBg, statusFg) = when (booking.status) {
        "confirmed" -> Triple("Confirmed", EmeraldBg, EmeraldGreen)
        "crew_on_site" -> Triple("Crew On-Site", GCashBlueLight, GCashBlue)
        "completed" -> Triple("Completed", EmeraldBg, EmeraldGreen)
        "cancelled" -> Triple("Cancelled", PillTrackBg, InkSecondary)
        else -> Triple("Pending Review", AmberBg, AmberText)
    }

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
                Surface(shape = RoundedCornerShape(10.dp), color = statusBg) {
                    Text(
                        text = statusLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = statusFg,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Text(
                    text = "₱${booking.totalAmount}",
                    fontFamily = JetBrainsMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaroonPrimary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "${booking.packageName} • ${booking.occupancyLabel}",
                style = MaterialTheme.typography.titleMedium,
                color = InkPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "${booking.dateLabel} • ${booking.slotLabel}",
                fontFamily = JetBrainsMono,
                fontSize = 12.sp,
                color = MaroonPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Cleaner: ${booking.agentName}",
                style = MaterialTheme.typography.bodySmall,
                color = InkSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${booking.streetAddress}, ${booking.roomNumber} (${booking.barangay})",
                style = MaterialTheme.typography.bodySmall,
                color = InkSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (booking.addons.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Add-ons: ${booking.addons.joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = InkPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SoftInputBg,
                border = BorderStroke(0.8.dp, CardSubtleBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                val payLabel = if (booking.paymentMethod == "gcash_qr") "GCash QR (Demo)" else "Cash on Arrival"
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(payLabel, style = MaterialTheme.typography.labelMedium, color = InkPrimary)
                    Text(
                        text = booking.paymentStatus.replaceFirstChar { it.uppercase() },
                        fontFamily = JetBrainsMono,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (booking.paymentStatus == "verified") EmeraldGreen else AmberText
                    )
                }
            }

            if (booking.paymentMethod == "gcash_qr" && booking.paymentStatus == "unpaid" && booking.status != "cancelled") {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DormCleanTextField(
                        value = gcashRefInput,
                        onValueChange = { gcashRefInput = it },
                        placeholder = "GCash Ref #",
                        accentColor = GCashBlue,
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = { onSubmitGcashRef(gcashRefInput) },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GCashBlue),
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Text("Submit Ref", fontSize = 12.sp, maxLines = 1)
                    }
                }
            }

            if (booking.status == "completed" && booking.rating == 0L) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Brush.linearGradient(listOf(BlushPinkCard, BlushPinkDeep)))
                        .border(1.dp, SpecularWhiteBorder, RoundedCornerShape(18.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Rate & Tip Your Cleaner",
                            style = MaterialTheme.typography.titleSmall,
                            color = InkPrimary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            (1L..5L).forEach { star ->
                                IconButton(onClick = { ratingInput = star }) {
                                    Icon(
                                        imageVector = if (star <= ratingInput) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                        contentDescription = "$star stars",
                                        tint = AmberGold
                                    )
                                }
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(0L, 20L, 30L, 50L).forEach { tip ->
                                val sel = tipInput == tip
                                val tipShape = RoundedCornerShape(12.dp)
                                Surface(
                                    shape = tipShape,
                                    color = if (sel) MaroonPrimary else CardWhite,
                                    border = BorderStroke(
                                        width = if (sel) 1.5.dp else 1.dp,
                                        color = if (sel) Color.White.copy(alpha = 0.6f) else SoftInputBorder
                                    ),
                                    shadowElevation = if (sel) 4.dp else 0.dp,
                                    modifier = Modifier.appleTactileClickable(
                                        shape = tipShape,
                                        highlightColor = if (sel) Color.White else MaroonPrimary,
                                        onClick = { tipInput = tip }
                                    )
                                ) {
                                    Text(
                                        text = if (tip == 0L) "No Tip" else "₱$tip",
                                        fontFamily = JetBrainsMono,
                                        fontSize = 11.5.sp,
                                        fontWeight = if (sel) FontWeight.Bold else FontWeight.Medium,
                                        color = if (sel) Color.White else InkPrimary,
                                        modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        DormCleanTextField(
                            value = reviewNote,
                            onValueChange = { reviewNote = it.take(300) },
                            placeholder = "Quick shoutout or note (optional)",
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        AppleGradientButton(
                            onClick = { onSubmitRating(ratingInput, tipInput, reviewNote) },
                            height = 48.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Send Rating & Tip", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else if (booking.rating > 0L) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Rated ${booking.rating}/5 ★ • Tip ₱${booking.tipAmount}",
                    fontFamily = JetBrainsMono,
                    fontSize = 12.sp,
                    color = EmeraldGreen,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onRebook,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, SoftInputBorder)
                ) {
                    Icon(Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Rebook")
                }
                if (booking.status == "pending_review") {
                    TextButton(onClick = onCancel) {
                        Text("Cancel Slot", color = DangerRed)
                    }
                }
            }
        }
    }
}

package com.example.data

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

enum class AppMode {
    STUDENT,
    ADMIN
}

enum class MainTab(val label: String) {
    HOME("Home"),
    SERVICES("Services"),
    BOOK("Book"),
    BOOKINGS("Bookings")
}

data class CleaningPackage(
    val id: String,
    val numberPrefix: String,
    val title: String,
    val shortTitle: String,
    val durationLabel: String,
    val basePrice: Long,
    val subtitle: String,
    val description: String,
    val bullets: List<String>
)

data class OptionalAddon(
    val id: String,
    val title: String,
    val description: String,
    val price: Long = 100L
)

data class CleanerAgent(
    val id: String,
    val shortName: String,
    val fullName: String,
    val initials: String,
    val agentNumberLabel: String,
    val zoneAndRole: String,
    val rating: String,
    val reviewCount: Int,
    val cleansCount: Int,
    val avatarColorHex: Long
)

data class TimeSlot(
    val id: String,
    val label: String
)

data class BarangayOption(
    val name: String,
    val dispatchNote: String
)

object DormCleanCatalog {
    val ADMIN_EMAILS = setOf("nopatjose@gmail.com", "antycepate@gmail.com")

    val packages = listOf(
        CleaningPackage(
            id = "quick_reset",
            numberPrefix = "01.",
            title = "Quick Reset",
            shortTitle = "Quick Reset",
            durationLabel = "2 hrs (120 min)",
            basePrice = 300L,
            subtitle = "2-hr dorm & boarding room reset",
            description = "Dedicated 2-hour desk and surface wipe, floor sweep and damp mop, bed straightening, and full trash haul.",
            bullets = listOf(
                "Full 2 hours dedicated for clean results",
                "Study desk, shelves, and nightstand wiped clean",
                "Full floor sweeping and damp microfiber mopping",
                "Trash emptied, relined & bed neatly made"
            )
        ),
        CleaningPackage(
            id = "standard_dorm",
            numberPrefix = "02.",
            title = "Standard Dorm Clean",
            shortTitle = "Standard Dorm Clean",
            durationLabel = "2 hrs (120 min)",
            basePrice = 500L,
            subtitle = "2-hr full room or house refresh around Daet",
            description = "Thorough 2-hour room and living space clean covering under-bed sweeping, high-touch switches, mirrors, fans, and fresh bed make-up.",
            bullets = listOf(
                "Full 2 hours dedicated deep surface clean",
                "Under-bed sweeping & dust-bunny removal",
                "High-touch switches, doorknobs, mirrors & fans",
                "Full floor sanitize, trash haul & crisp bed reset"
            )
        )
    )

    val addons = listOf(
        OptionalAddon(
            id = "inside_fridge",
            title = "Inside Fridge",
            description = "Empty shelves wiped & deodorized"
        ),
        OptionalAddon(
            id = "window_sills",
            title = "Window Sills",
            description = "Tracks, blinds & interior glass"
        ),
        OptionalAddon(
            id = "linen_change",
            title = "Linen Change",
            description = "Strip old sheets & fit fresh set"
        ),
        OptionalAddon(
            id = "desk_tidy",
            title = "Desk & Cable Tidy",
            description = "Organize books, chargers & study setup"
        ),
        OptionalAddon(
            id = "bathroom_scrub",
            title = "Bathroom Deep Scrub",
            description = "Sink, toilet & shower tiles scrubbed"
        )
    )

    val agents = listOf(
        CleanerAgent(
            id = "agent_nopat",
            shortName = "Agent 1: Nopat",
            fullName = "Nopat, Jose Antonio",
            initials = "NJ",
            agentNumberLabel = "Agent 1",
            zoneAndRole = "Lag-on & Centro • Lead Dorm Specialist",
            rating = "4.9",
            reviewCount = 38,
            cleansCount = 142,
            avatarColorHex = 0xFF7F122B
        ),
        CleanerAgent(
            id = "agent_orit",
            shortName = "Agent 2: Orit",
            fullName = "Orit, Marc Andrei Carlito",
            initials = "OM",
            agentNumberLabel = "Agent 2",
            zoneAndRole = "Magang & Bagasbas • Deep Clean & Bath",
            rating = "4.8",
            reviewCount = 29,
            cleansCount = 118,
            avatarColorHex = 0xFF1D6FF2
        ),
        CleanerAgent(
            id = "agent_guerrero",
            shortName = "Agent 3: Guerrero",
            fullName = "Guerrero, Jewellaine",
            initials = "GJ",
            agentNumberLabel = "Agent 3",
            zoneAndRole = "Camambugan & Centro • Study & Linen Reset",
            rating = "4.9",
            reviewCount = 24,
            cleansCount = 96,
            avatarColorHex = 0xFFD97706
        )
    )

    val timeSlots = listOf(
        TimeSlot("08_10", "08:00 - 10:00 AM"),
        TimeSlot("10_12", "10:00 AM - 12:00 PM"),
        TimeSlot("13_15", "01:00 - 03:00 PM"),
        TimeSlot("15_17", "03:30 - 05:30 PM"),
        TimeSlot("18_20", "06:00 - 08:00 PM")
    )

    val barangays = listOf(
        BarangayOption(
            name = "Daet Centro",
            dispatchNote = "Poblacion proper • Fast dispatch • Share street name or nearest store/landmark."
        ),
        BarangayOption(
            name = "Lag-on",
            dispatchNote = "CNSC & boarding house belt • Fast dispatch • Mention gate color or alley name."
        ),
        BarangayOption(
            name = "Magang",
            dispatchNote = "Magang residential & dorms • 10–15 min arrival • Share nearest sari-sari store."
        ),
        BarangayOption(
            name = "Bagasbas",
            dispatchNote = "Bagasbas road & apartments • Include subdivision or beach road landmark."
        ),
        BarangayOption(
            name = "Camambugan",
            dispatchNote = "Camambugan area • 10–15 min arrival • Share highway turn-off or landmark."
        ),
        BarangayOption(
            name = "Other Daet Area",
            dispatchNote = "Other Daet barangay • Share clear street directions and nearest landmark."
        )
    )

    val arrivalNotePresets = listOf(
        "Landlady holds the gate key",
        "Low water supply 1–3 PM",
        "Friendly dog in compound",
        "Message before knocking"
    )

    val contactChannels = listOf("Messenger", "SMS Text", "Phone Call", "WhatsApp")

    val feedbackCategories = listOf(
        "Clean Quality",
        "Late / Timing",
        "Crew Conduct",
        "App Error / Bug",
        "Suggestion"
    )

    val dayStopPresets = listOf(
        "Workers Day Off",
        "Public Holiday",
        "Crew Rest Day"
    )
}

@Keep
@IgnoreExtraProperties
data class Booking(
    @DocumentId val id: String = "",
    val userId: String = "",
    val userEmail: String = "",
    val customerName: String = "",
    val packageId: String = "standard_dorm",
    val packageName: String = "Standard Dorm Clean",
    val basePrice: Long = 500L,
    val occupancyType: String = "solo",
    val occupancyLabel: String = "Solo / 2-Pax Room",
    val occupancyFee: Long = 0L,
    val addons: List<String> = emptyList(),
    val addonsTotal: Long = 0L,
    val othersRequest: String = "",
    val dateKey: String = "",
    val dateLabel: String = "",
    val slotId: String = "",
    val slotLabel: String = "",
    val agentId: String = "",
    val agentName: String = "",
    val barangay: String = "Daet Centro",
    val streetAddress: String = "",
    val roomNumber: String = "",
    val floorGate: String = "",
    val landmark: String = "",
    val arrivalNotes: String = "",
    val contactChannel: String = "Messenger",
    val contactHandle: String = "",
    val paymentMethod: String = "gcash_qr",
    val paymentStatus: String = "unpaid",
    val gcashRefNumber: String = "",
    val referralCode: String = "",
    val referralDiscount: Long = 0L,
    val dayPromoName: String = "",
    val dayPromoDiscount: Long = 0L,
    val totalAmount: Long = 500L,
    val status: String = "pending_review",
    val rating: Long = 0L,
    val tipAmount: Long = 0L,
    val reviewComment: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class SlotLock(
    @DocumentId val id: String = "",
    val userId: String = "",
    val bookingId: String = "",
    val dateKey: String = "",
    val agentId: String = "",
    val slotId: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class DayConfig(
    @DocumentId val id: String = "",
    val dateKey: String = "",
    val forceStopped: Boolean = false,
    val stopReason: String = "",
    val discountEnabled: Boolean = false,
    val discountType: String = "percent",
    val discountValue: Long = 0L,
    val promoName: String = "",
    val blockedSlots: Map<String, String> = emptyMap(),
    val updatedBy: String = "",
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class FeedbackReport(
    @DocumentId val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val category: String = "Clean Quality",
    val relatedBookingId: String = "",
    val relatedLabel: String = "General Service / DormClean App",
    val message: String = "",
    val status: String = "open",
    val adminResolution: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

data class ServiceDateOption(
    val dateKey: String,
    val topLabel: String,
    val dayLabel: String,
    val fullFormatted: String
)

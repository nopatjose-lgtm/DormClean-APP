package com.example.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppMode
import com.example.data.Booking
import com.example.data.DayConfig
import com.example.data.DormCleanCatalog
import com.example.data.DormCleanRepository
import com.example.data.FeedbackReport
import com.example.data.MainTab
import com.example.data.ServiceDateOption
import com.example.data.SlotLock
import com.example.data.UiState
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class BookingDraftState(
    val selectedPackageId: String = "standard_dorm",
    val occupancyType: String = "shared",
    val selectedAddonIds: Set<String> = setOf("inside_fridge", "window_sills"),
    val othersRequest: String = "",
    val selectedDateKey: String = "",
    val selectedAgentId: String = "",
    val selectedSlotId: String = "",
    val barangay: String = "Daet Centro",
    val streetAddress: String = "",
    val roomNumber: String = "",
    val floorGate: String = "",
    val landmark: String = "",
    val arrivalNotes: String = "",
    val contactChannel: String = "Messenger",
    val customerName: String = "",
    val contactHandle: String = "",
    val paymentMethod: String = "gcash_qr",
    val gcashRefNumber: String = "",
    val receiptLabel: String = "",
    val referralCode: String = ""
)

data class AdminControlUiState(
    val selectedFilter: String = "All",
    val searchQuery: String = "",
    val selectedDateKey: String = "",
    val stopReasonInput: String = "Workers Day Off",
    val discountType: String = "percent",
    val discountValueInput: Int = 20,
    val promoNameInput: String = "Student Promo",
    val selectedAgentId: String = "agent_nopat",
    val blockReasonInput: String = "Crew Break"
)

class DormCleanViewModel(
    private val repository: DormCleanRepository,
    val currentUserId: String,
    val userDisplayName: String,
    val userEmail: String
) : ViewModel() {

    val isAdminUser: Boolean = userEmail.lowercase(Locale.ROOT) in DormCleanCatalog.ADMIN_EMAILS
    val firstName: String = userDisplayName.trim().split(" ").firstOrNull()?.takeIf { it.isNotBlank() } ?: "Student"

    val upcomingDates: List<ServiceDateOption> = buildUpcomingDates()

    private val _appMode = MutableStateFlow(AppMode.STUDENT)
    val appMode: StateFlow<AppMode> = _appMode.asStateFlow()

    private val _currentTab = MutableStateFlow(MainTab.HOME)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    private val _bookingDraft = MutableStateFlow(
        BookingDraftState(
            selectedDateKey = upcomingDates.getOrNull(2)?.dateKey ?: upcomingDates.first().dateKey,
            customerName = userDisplayName
        )
    )
    val bookingDraft: StateFlow<BookingDraftState> = _bookingDraft.asStateFlow()

    private val _adminUi = MutableStateFlow(
        AdminControlUiState(
            selectedDateKey = upcomingDates.first().dateKey
        )
    )
    val adminUi: StateFlow<AdminControlUiState> = _adminUi.asStateFlow()

    private val _statusBanner = MutableStateFlow<String?>(null)
    val statusBanner: StateFlow<String?> = _statusBanner.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private fun <T> Flow<T>.toUiStateFlow(tag: String, label: String): StateFlow<UiState<T>> =
        map<T, UiState<T>> { UiState.Success(it) }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val rootError = firestoreError ?: error
                Log.w(tag, "Error observing $label", rootError)
                emit(UiState.Error(rootError.message ?: "Failed to load $label"))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = UiState.Loading
            )

    val studentBookings: StateFlow<UiState<List<Booking>>> =
        repository.observeStudentBookings(currentUserId)
            .toUiStateFlow("DormCleanVM", "bookings")

    val adminBookings: StateFlow<UiState<List<Booking>>> =
        if (isAdminUser) {
            repository.observeAllBookingsForAdmin(currentUserId)
                .toUiStateFlow("DormCleanVM", "admin bookings")
        } else {
            MutableStateFlow(UiState.Success(emptyList()))
        }

    val slotLocks: StateFlow<UiState<List<SlotLock>>> =
        repository.observeSlotLocks(currentUserId)
            .toUiStateFlow("DormCleanVM", "slot locks")

    val dayConfigs: StateFlow<UiState<Map<String, DayConfig>>> =
        repository.observeDayConfigs(currentUserId)
            .toUiStateFlow("DormCleanVM", "day configs")

    val studentFeedback: StateFlow<UiState<List<FeedbackReport>>> =
        repository.observeStudentFeedback(currentUserId)
            .toUiStateFlow("DormCleanVM", "feedback")

    val adminFeedback: StateFlow<UiState<List<FeedbackReport>>> =
        if (isAdminUser) {
            repository.observeAllFeedbackForAdmin(currentUserId)
                .toUiStateFlow("DormCleanVM", "admin feedback")
        } else {
            MutableStateFlow(UiState.Success(emptyList()))
        }

    fun selectMode(mode: AppMode) {
        if (mode == AppMode.ADMIN && !isAdminUser) {
            _statusBanner.value = "Sole Admin access is restricted to authorized DormClean admin accounts."
            return
        }
        _appMode.value = mode
    }

    fun selectTab(tab: MainTab) {
        if (_appMode.value == AppMode.ADMIN) {
            _appMode.value = AppMode.STUDENT
        }
        _currentTab.value = tab
    }

    fun clearBanner() {
        _statusBanner.value = null
    }

    fun selectPackage(packageId: String, navigateTo: MainTab? = null) {
        _bookingDraft.update { it.copy(selectedPackageId = packageId) }
        navigateTo?.let { selectTab(it) }
    }

    fun selectOccupancy(occupancyType: String) {
        _bookingDraft.update { it.copy(occupancyType = occupancyType) }
    }

    fun toggleAddon(addonId: String) {
        _bookingDraft.update { draft ->
            val updated = draft.selectedAddonIds.toMutableSet()
            if (!updated.add(addonId)) updated.remove(addonId)
            draft.copy(selectedAddonIds = updated)
        }
    }

    fun updateOthersRequest(text: String) {
        _bookingDraft.update { it.copy(othersRequest = text.take(300)) }
    }

    fun selectBookingDate(dateKey: String) {
        _bookingDraft.update { it.copy(selectedDateKey = dateKey, selectedSlotId = "") }
    }

    fun selectBookingAgent(agentId: String, navigateToBook: Boolean = false) {
        _bookingDraft.update { it.copy(selectedAgentId = agentId, selectedSlotId = "") }
        if (navigateToBook) selectTab(MainTab.BOOK)
    }

    fun selectBookingSlot(slotId: String) {
        _bookingDraft.update { it.copy(selectedSlotId = slotId) }
    }

    fun updateLocation(
        barangay: String? = null,
        streetAddress: String? = null,
        roomNumber: String? = null,
        floorGate: String? = null,
        landmark: String? = null,
        arrivalNotes: String? = null
    ) {
        _bookingDraft.update { d ->
            d.copy(
                barangay = barangay ?: d.barangay,
                streetAddress = streetAddress ?: d.streetAddress,
                roomNumber = roomNumber ?: d.roomNumber,
                floorGate = floorGate ?: d.floorGate,
                landmark = landmark ?: d.landmark,
                arrivalNotes = arrivalNotes ?: d.arrivalNotes
            )
        }
    }

    fun toggleArrivalPreset(note: String) {
        _bookingDraft.update { d ->
            val current = d.arrivalNotes
            val updated = if (current.contains(note)) {
                current.replace(note, "").replace(" •  • ", " • ").trim(' ', '•')
            } else {
                if (current.isBlank()) note else "$current • $note"
            }
            d.copy(arrivalNotes = updated.take(250))
        }
    }

    fun updateContact(
        channel: String? = null,
        customerName: String? = null,
        contactHandle: String? = null
    ) {
        _bookingDraft.update { d ->
            d.copy(
                contactChannel = channel ?: d.contactChannel,
                customerName = customerName ?: d.customerName,
                contactHandle = contactHandle ?: d.contactHandle
            )
        }
    }

    fun updatePayment(
        paymentMethod: String? = null,
        gcashRefNumber: String? = null,
        receiptLabel: String? = null
    ) {
        _bookingDraft.update { d ->
            d.copy(
                paymentMethod = paymentMethod ?: d.paymentMethod,
                gcashRefNumber = gcashRefNumber ?: d.gcashRefNumber,
                receiptLabel = receiptLabel ?: d.receiptLabel
            )
        }
    }

    fun updateReferralCode(code: String) {
        _bookingDraft.update { it.copy(referralCode = code.uppercase(Locale.ROOT).take(20)) }
    }

    fun applyDefaultReferral() {
        _bookingDraft.update { it.copy(referralCode = "DAET50") }
    }

    fun rebookFromExisting(booking: Booking?) {
        if (booking != null) {
            val addonIds = DormCleanCatalog.addons
                .filter { it.title in booking.addons }
                .map { it.id }
                .toSet()
            _bookingDraft.update { d ->
                d.copy(
                    selectedPackageId = booking.packageId,
                    occupancyType = booking.occupancyType,
                    selectedAddonIds = addonIds,
                    othersRequest = booking.othersRequest,
                    selectedAgentId = booking.agentId,
                    selectedSlotId = "",
                    barangay = booking.barangay,
                    streetAddress = booking.streetAddress,
                    roomNumber = booking.roomNumber,
                    floorGate = booking.floorGate,
                    landmark = booking.landmark,
                    arrivalNotes = booking.arrivalNotes,
                    contactChannel = booking.contactChannel,
                    customerName = booking.customerName,
                    contactHandle = booking.contactHandle
                )
            }
        }
        selectTab(MainTab.BOOK)
    }

    fun computeDayPromoDiscount(baseAndExtras: Long, dayConfig: DayConfig?): Pair<String, Long> {
        if (dayConfig == null || !dayConfig.discountEnabled || dayConfig.discountValue <= 0L) {
            return "" to 0L
        }
        val discount = if (dayConfig.discountType == "percent") {
            (baseAndExtras * dayConfig.discountValue.coerceIn(5L, 80L)) / 100L
        } else {
            dayConfig.discountValue.coerceAtMost(baseAndExtras)
        }
        val name = dayConfig.promoName.ifBlank { "Day Promo" }
        return name to discount
    }

    fun computeOpenSlotCount(
        dateKey: String,
        agentId: String?,
        dayConfig: DayConfig?,
        locks: List<SlotLock>
    ): Int {
        if (dayConfig?.forceStopped == true) return 0
        val locksForDay = locks.filter { it.dateKey == dateKey }
        if (agentId == null) {
            // Total open windows on this day: max 5 if any cleaner has slots open, matching screenshot ("5 open")
            var availableSlotIds = 0
            for (slot in DormCleanCatalog.timeSlots) {
                val anyAgentFree = DormCleanCatalog.agents.any { agent ->
                    val blockKey = "${agent.id}__${slot.id}"
                    val blockedByAdmin = dayConfig?.blockedSlots?.containsKey(blockKey) == true
                    val takenByBooking = locksForDay.any { it.agentId == agent.id && it.slotId == slot.id }
                    !blockedByAdmin && !takenByBooking
                }
                if (anyAgentFree) availableSlotIds++
            }
            return availableSlotIds
        } else {
            var freeCount = 0
            for (slot in DormCleanCatalog.timeSlots) {
                val blockKey = "${agentId}__${slot.id}"
                val blockedByAdmin = dayConfig?.blockedSlots?.containsKey(blockKey) == true
                val takenByBooking = locksForDay.any { it.agentId == agentId && it.slotId == slot.id }
                if (!blockedByAdmin && !takenByBooking) freeCount++
            }
            return freeCount
        }
    }

    fun submitBooking(dayConfigsMap: Map<String, DayConfig>) {
        val draft = _bookingDraft.value
        val pkg = DormCleanCatalog.packages.firstOrNull { it.id == draft.selectedPackageId }
            ?: DormCleanCatalog.packages.last()
        val agent = DormCleanCatalog.agents.firstOrNull { it.id == draft.selectedAgentId }
        val slot = DormCleanCatalog.timeSlots.firstOrNull { it.id == draft.selectedSlotId }
        val dateOpt = upcomingDates.firstOrNull { it.dateKey == draft.selectedDateKey }

        if (dateOpt == null) {
            _statusBanner.value = "Please select a service date."
            return
        }
        val dayCfg = dayConfigsMap[dateOpt.dateKey]
        if (dayCfg?.forceStopped == true) {
            _statusBanner.value = "Operations are paused on ${dateOpt.fullFormatted} (${dayCfg.stopReason}). Please pick another day."
            return
        }
        if (agent == null || slot == null) {
            _statusBanner.value = "Required: Select your agent & 2-hr slot in Step 02."
            return
        }
        if (draft.streetAddress.trim().length < 2 || draft.roomNumber.trim().isEmpty()) {
            _statusBanner.value = "Required: Enter your Daet house/dorm address and room #."
            return
        }
        if (draft.customerName.trim().length < 2 || draft.contactHandle.trim().length < 2) {
            _statusBanner.value = "Required: Enter your name and contact handle in Step 04."
            return
        }

        val selectedAddons = DormCleanCatalog.addons.filter { it.id in draft.selectedAddonIds }
        val occupancyFee = if (draft.occupancyType == "shared") 100L else 0L
        val occupancyLabel = if (draft.occupancyType == "shared") "3–4 Pax Shared Room" else "Solo / 2-Pax Room"
        val addonsTotal = selectedAddons.sumOf { it.price }
        val subtotal = pkg.basePrice + occupancyFee + addonsTotal
        val (promoName, promoDiscount) = computeDayPromoDiscount(subtotal, dayCfg)
        val referralDiscount = if (draft.referralCode.trim().equals("DAET50", ignoreCase = true)) 50L else 0L
        val total = (subtotal - promoDiscount - referralDiscount).coerceAtLeast(0L)

        val booking = Booking(
            userId = currentUserId,
            userEmail = userEmail,
            customerName = draft.customerName.trim(),
            packageId = pkg.id,
            packageName = pkg.title,
            basePrice = pkg.basePrice,
            occupancyType = draft.occupancyType,
            occupancyLabel = occupancyLabel,
            occupancyFee = occupancyFee,
            addons = selectedAddons.map { it.title },
            addonsTotal = addonsTotal,
            othersRequest = draft.othersRequest.trim(),
            dateKey = dateOpt.dateKey,
            dateLabel = dateOpt.fullFormatted,
            slotId = slot.id,
            slotLabel = slot.label,
            agentId = agent.id,
            agentName = agent.fullName,
            barangay = draft.barangay,
            streetAddress = draft.streetAddress.trim(),
            roomNumber = draft.roomNumber.trim(),
            floorGate = draft.floorGate.trim(),
            landmark = draft.landmark.trim(),
            arrivalNotes = draft.arrivalNotes.trim(),
            contactChannel = draft.contactChannel,
            contactHandle = draft.contactHandle.trim(),
            paymentMethod = draft.paymentMethod,
            gcashRefNumber = draft.gcashRefNumber.trim(),
            referralCode = if (referralDiscount > 0L) "DAET50" else draft.referralCode.trim(),
            referralDiscount = referralDiscount,
            dayPromoName = promoName,
            dayPromoDiscount = promoDiscount,
            totalAmount = total
        )

        viewModelScope.launch {
            _isSubmitting.value = true
            val result = repository.createBooking(booking)
            _isSubmitting.value = false
            result.onSuccess {
                _bookingDraft.update { it.copy(selectedSlotId = "", gcashRefNumber = "") }
                _statusBanner.value = "Slot reserved for ${dateOpt.fullFormatted} (${slot.label})!"
                selectTab(MainTab.BOOKINGS)
            }.onFailure { err ->
                _statusBanner.value = err.localizedMessage ?: "Could not reserve slot. It may have just been booked."
            }
        }
    }

    fun submitGcashRef(bookingId: String, ref: String) {
        viewModelScope.launch {
            val result = repository.submitGcashReference(bookingId, ref)
            result.onSuccess {
                _statusBanner.value = "GCash reference submitted for admin verification."
            }.onFailure { err ->
                _statusBanner.value = err.localizedMessage ?: "Failed to update GCash reference."
            }
        }
    }

    fun cancelBooking(booking: Booking) {
        viewModelScope.launch {
            val result = repository.cancelBooking(booking)
            result.onSuccess {
                _statusBanner.value = "Booking cancelled and 2-hr slot reopened."
            }.onFailure { err ->
                _statusBanner.value = err.localizedMessage ?: "Failed to cancel booking."
            }
        }
    }

    fun submitRatingAndTip(bookingId: String, rating: Long, tip: Long, comment: String) {
        viewModelScope.launch {
            val result = repository.submitRatingAndTip(bookingId, rating, tip, comment)
            result.onSuccess {
                _statusBanner.value = "Thank you! Rating & ₱$tip tip saved for your cleaner."
            }.onFailure { err ->
                _statusBanner.value = err.localizedMessage ?: "Could not save rating."
            }
        }
    }

    fun submitFeedback(category: String, relatedBookingId: String, relatedLabel: String, message: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isSubmitting.value = true
            val result = repository.submitFeedback(category, relatedBookingId, relatedLabel, message)
            _isSubmitting.value = false
            result.onSuccess {
                _statusBanner.value = "Feedback sent to DormClean crew!"
                onSuccess()
            }.onFailure { err ->
                _statusBanner.value = err.localizedMessage ?: "Could not submit feedback."
            }
        }
    }

    // --- ADMIN ACTIONS ---

    fun updateAdminFilter(filter: String) {
        _adminUi.update { it.copy(selectedFilter = filter) }
    }

    fun updateAdminSearch(query: String) {
        _adminUi.update { it.copy(searchQuery = query) }
    }

    fun selectAdminDate(dateKey: String) {
        _adminUi.update { it.copy(selectedDateKey = dateKey) }
    }

    fun updateAdminStopReason(reason: String) {
        _adminUi.update { it.copy(stopReasonInput = reason.take(120)) }
    }

    fun updateAdminDiscountForm(
        discountType: String? = null,
        discountValue: Int? = null,
        promoName: String? = null
    ) {
        _adminUi.update { a ->
            a.copy(
                discountType = discountType ?: a.discountType,
                discountValueInput = discountValue ?: a.discountValueInput,
                promoNameInput = promoName ?: a.promoNameInput
            )
        }
    }

    fun selectAdminAgent(agentId: String) {
        _adminUi.update { it.copy(selectedAgentId = agentId) }
    }

    fun updateAdminBlockReason(reason: String) {
        _adminUi.update { it.copy(blockReasonInput = reason.take(80)) }
    }

    fun toggleForceStopDay(dateKey: String, existing: DayConfig?) {
        val currentlyStopped = existing?.forceStopped == true
        val reason = _adminUi.value.stopReasonInput.ifBlank { "Workers Day Off" }
        val updated = (existing ?: DayConfig(dateKey = dateKey)).copy(
            dateKey = dateKey,
            forceStopped = !currentlyStopped,
            stopReason = if (!currentlyStopped) reason else ""
        )
        viewModelScope.launch {
            repository.adminSaveDayConfig(updated).onSuccess {
                _statusBanner.value = if (updated.forceStopped) {
                    "Operations paused for $dateKey ($reason)"
                } else {
                    "Reopened $dateKey for student bookings."
                }
            }.onFailure { err ->
                _statusBanner.value = err.localizedMessage ?: "Failed to update day operations."
            }
        }
    }

    fun saveDayDiscount(dateKey: String, existing: DayConfig?) {
        val form = _adminUi.value
        val maxVal = if (form.discountType == "percent") 80 else 500
        val minVal = if (form.discountType == "percent") 5 else 10
        val clamped = form.discountValueInput.coerceIn(minVal, maxVal).toLong()
        val updated = (existing ?: DayConfig(dateKey = dateKey)).copy(
            dateKey = dateKey,
            discountEnabled = true,
            discountType = form.discountType,
            discountValue = clamped,
            promoName = form.promoNameInput.ifBlank { "Student Promo" }
        )
        viewModelScope.launch {
            repository.adminSaveDayConfig(updated).onSuccess {
                _statusBanner.value = "Saved ${updated.promoName} for $dateKey."
            }.onFailure { err ->
                _statusBanner.value = err.localizedMessage ?: "Failed to save discount."
            }
        }
    }

    fun clearDayDiscount(dateKey: String, existing: DayConfig?) {
        if (existing == null) return
        val updated = existing.copy(
            discountEnabled = false,
            discountValue = 0L,
            promoName = ""
        )
        viewModelScope.launch {
            repository.adminSaveDayConfig(updated).onSuccess {
                _statusBanner.value = "Cleared discount for $dateKey."
            }.onFailure { err ->
                _statusBanner.value = err.localizedMessage ?: "Failed to clear discount."
            }
        }
    }

    fun toggleAgentSlotBlock(dateKey: String, agentId: String, slotId: String, existing: DayConfig?) {
        val key = "${agentId}__${slotId}"
        val currentMap = existing?.blockedSlots?.toMutableMap() ?: mutableMapOf()
        val wasBlocked = currentMap.containsKey(key)
        if (wasBlocked) {
            currentMap.remove(key)
        } else {
            currentMap[key] = _adminUi.value.blockReasonInput.ifBlank { "Crew Break" }
        }
        val updated = (existing ?: DayConfig(dateKey = dateKey)).copy(
            dateKey = dateKey,
            blockedSlots = currentMap
        )
        viewModelScope.launch {
            repository.adminSaveDayConfig(updated).onSuccess {
                _statusBanner.value = if (wasBlocked) "Slot reopened." else "Slot blocked."
            }.onFailure { err ->
                _statusBanner.value = err.localizedMessage ?: "Failed to update slot."
            }
        }
    }

    fun adminUpdateStatus(booking: Booking, newStatus: String) {
        viewModelScope.launch {
            repository.adminUpdateBookingStatus(booking, newStatus).onSuccess {
                _statusBanner.value = "Booking updated to ${newStatus.replace('_', ' ')}."
            }.onFailure { err ->
                _statusBanner.value = err.localizedMessage ?: "Failed to update status."
            }
        }
    }

    fun adminVerifyPayment(bookingId: String) {
        viewModelScope.launch {
            repository.adminVerifyPayment(bookingId).onSuccess {
                _statusBanner.value = "GCash payment marked verified."
            }.onFailure { err ->
                _statusBanner.value = err.localizedMessage ?: "Failed to verify payment."
            }
        }
    }

    fun adminDeleteBooking(booking: Booking) {
        viewModelScope.launch {
            repository.adminDeleteBooking(booking).onSuccess {
                _statusBanner.value = "Booking log deleted."
            }.onFailure { err ->
                _statusBanner.value = err.localizedMessage ?: "Failed to delete booking log."
            }
        }
    }

    fun adminResolveFeedback(feedbackId: String, resolution: String) {
        viewModelScope.launch {
            repository.adminResolveFeedback(feedbackId, resolution).onSuccess {
                _statusBanner.value = "Lesson logged & feedback marked resolved."
            }.onFailure { err ->
                _statusBanner.value = err.localizedMessage ?: "Failed to resolve feedback."
            }
        }
    }

    private fun buildUpcomingDates(): List<ServiceDateOption> {
        val keyFmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val dayFmt = SimpleDateFormat("MMM d", Locale.US)
        val weekdayFmt = SimpleDateFormat("EEE", Locale.US)
        val fullFmt = SimpleDateFormat("EEE, MMM d, yyyy", Locale.US)
        val cal = Calendar.getInstance()
        return (0 until 7).map { offset ->
            val date = cal.time
            val top = when (offset) {
                0 -> "Today"
                1 -> "Tomorrow"
                else -> weekdayFmt.format(date)
            }
            val opt = ServiceDateOption(
                dateKey = keyFmt.format(date),
                topLabel = top,
                dayLabel = dayFmt.format(date),
                fullFormatted = fullFmt.format(date)
            )
            cal.add(Calendar.DAY_OF_YEAR, 1)
            opt
        }
    }
}

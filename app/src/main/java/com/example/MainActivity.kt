package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.auth.AuthScreen
import com.example.auth.attemptAutoSignIn
import com.example.auth.authStateFlow
import com.example.auth.signOut
import com.example.data.AppMode
import com.example.data.DormCleanRepository
import com.example.data.MainTab
import com.example.data.UiState
import com.example.ui.DormCleanViewModel
import com.example.ui.components.DormCleanAmbientBackground
import com.example.ui.components.DormCleanFloatingBottomBar
import com.example.ui.components.DormCleanTopChrome
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.BookScreen
import com.example.ui.screens.BookingsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ServicesScreen
import com.example.ui.theme.DormCleanTheme
import com.example.ui.theme.MaroonDeep
import com.example.ui.theme.MaroonPrimary
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DormCleanTheme {
                DormCleanAppRoot()
            }
        }
    }
}

@Composable
fun DormCleanAppRoot(auth: FirebaseAuth = Firebase.auth) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

    val currentUser by auth.authStateFlow()
        .collectAsStateWithLifecycle(initialValue = auth.currentUser)

    var autoSignInAttempted by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!autoSignInAttempted && auth.currentUser == null) {
            autoSignInAttempted = true
            attemptAutoSignIn(
                context = context,
                credentialManager = credentialManager,
                onAuthSuccess = {},
                onUnauthenticated = {},
                scope = scope
            )
        }
    }

    val user = currentUser
    if (user == null) {
        AuthScreen(onAuthSuccess = {})
    } else {
        key(user.uid) {
            DormCleanAuthenticatedApp(
                user = user,
                onSignOut = {
                    signOut(
                        credentialManager = credentialManager,
                        scope = scope
                    )
                }
            )
        }
    }
}

@Composable
private fun DormCleanAuthenticatedApp(
    user: FirebaseUser,
    onSignOut: () -> Unit
) {
    val uid = user.uid
    val displayName = user.displayName ?: "Jose Nopat"
    val email = user.email ?: ""

    val viewModel: DormCleanViewModel = viewModel(
        key = uid,
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                val databaseId = app.getString(R.string.firestore_database_id)
                val db = FirebaseFirestore.getInstance(databaseId)
                DormCleanViewModel(
                    repository = DormCleanRepository(db),
                    currentUserId = uid,
                    userDisplayName = displayName,
                    userEmail = email
                )
            }
        }
    )

    val appMode by viewModel.appMode.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val bookingDraft by viewModel.bookingDraft.collectAsStateWithLifecycle()
    val adminUi by viewModel.adminUi.collectAsStateWithLifecycle()
    val isSubmitting by viewModel.isSubmitting.collectAsStateWithLifecycle()
    val statusBanner by viewModel.statusBanner.collectAsStateWithLifecycle()

    val studentBookingsState by viewModel.studentBookings.collectAsStateWithLifecycle()
    val adminBookingsState by viewModel.adminBookings.collectAsStateWithLifecycle()
    val slotLocksState by viewModel.slotLocks.collectAsStateWithLifecycle()
    val dayConfigsState by viewModel.dayConfigs.collectAsStateWithLifecycle()
    val studentFeedbackState by viewModel.studentFeedback.collectAsStateWithLifecycle()
    val adminFeedbackState by viewModel.adminFeedback.collectAsStateWithLifecycle()

    val slotLocks = (slotLocksState as? UiState.Success)?.data ?: emptyList()
    val dayConfigs = (dayConfigsState as? UiState.Success)?.data ?: emptyMap()
    val studentBookings = (studentBookingsState as? UiState.Success)?.data ?: emptyList()

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(statusBanner) {
        val msg = statusBanner
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearBanner()
        }
    }

    // Handle system back cleanly across sub-tabs and Admin mode
    BackHandler(enabled = appMode == AppMode.ADMIN || currentTab != MainTab.HOME) {
        if (appMode == AppMode.ADMIN) {
            viewModel.selectMode(AppMode.STUDENT)
        } else {
            viewModel.selectTab(MainTab.HOME)
        }
    }

    DormCleanAmbientBackground {
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.padding(bottom = 86.dp)
                ) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = MaroonDeep,
                        contentColor = Color.White,
                        shape = RoundedCornerShape(18.dp)
                    )
                }
            }
        ) { _ ->
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize()) {
                DormCleanTopChrome(
                    firstName = viewModel.firstName,
                    userEmail = viewModel.userEmail,
                    isAdminUser = viewModel.isAdminUser,
                    appMode = appMode,
                    onSelectMode = viewModel::selectMode,
                    onProfilePillClick = {
                        viewModel.selectTab(MainTab.BOOKINGS)
                    }
                )

                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    if (appMode == AppMode.ADMIN && viewModel.isAdminUser) {
                        AdminScreen(
                            userDisplayName = viewModel.userDisplayName,
                            userEmail = viewModel.userEmail,
                            adminUi = adminUi,
                            upcomingDates = viewModel.upcomingDates,
                            bookingsState = adminBookingsState,
                            dayConfigs = dayConfigs,
                            feedbackState = adminFeedbackState,
                            onSignOut = onSignOut,
                            onUpdateFilter = viewModel::updateAdminFilter,
                            onUpdateSearch = viewModel::updateAdminSearch,
                            onSelectDate = viewModel::selectAdminDate,
                            onUpdateStopReason = viewModel::updateAdminStopReason,
                            onToggleForceStop = viewModel::toggleForceStopDay,
                            onUpdateDiscountForm = viewModel::updateAdminDiscountForm,
                            onSaveDiscount = viewModel::saveDayDiscount,
                            onClearDiscount = viewModel::clearDayDiscount,
                            onSelectAgent = viewModel::selectAdminAgent,
                            onUpdateBlockReason = viewModel::updateAdminBlockReason,
                            onToggleSlotBlock = viewModel::toggleAgentSlotBlock,
                            onUpdateBookingStatus = viewModel::adminUpdateStatus,
                            onVerifyPayment = viewModel::adminVerifyPayment,
                            onDeleteBooking = viewModel::adminDeleteBooking,
                            onResolveFeedback = viewModel::adminResolveFeedback
                        )
                    } else {
                        when (currentTab) {
                            MainTab.HOME -> HomeScreen(
                                firstName = viewModel.firstName,
                                onNavigateTab = viewModel::selectTab,
                                onSelectPackageAndGo = { pkgId, tab ->
                                    viewModel.selectPackage(pkgId, tab)
                                },
                                onRebookClick = {
                                    viewModel.rebookFromExisting(studentBookings.firstOrNull())
                                },
                                onSelectCleanerAndBook = { agentId ->
                                    viewModel.selectBookingAgent(agentId, navigateToBook = true)
                                }
                            )

                            MainTab.SERVICES -> ServicesScreen(
                                draft = bookingDraft,
                                onSelectPackage = { viewModel.selectPackage(it, null) },
                                onSelectOccupancy = viewModel::selectOccupancy,
                                onToggleAddon = viewModel::toggleAddon,
                                onUpdateOthers = viewModel::updateOthersRequest,
                                onProceedToBook = { viewModel.selectTab(MainTab.BOOK) }
                            )

                            MainTab.BOOK -> BookScreen(
                                draft = bookingDraft,
                                upcomingDates = viewModel.upcomingDates,
                                dayConfigs = dayConfigs,
                                slotLocks = slotLocks,
                                isSubmitting = isSubmitting,
                                computeDayOpenSlots = { dateKey, agentId ->
                                    viewModel.computeOpenSlotCount(
                                        dateKey = dateKey,
                                        agentId = agentId,
                                        dayConfig = dayConfigs[dateKey],
                                        locks = slotLocks
                                    )
                                },
                                computeDayPromoDiscount = viewModel::computeDayPromoDiscount,
                                onSelectPackage = { viewModel.selectPackage(it, null) },
                                onSelectOccupancy = viewModel::selectOccupancy,
                                onToggleAddon = viewModel::toggleAddon,
                                onSelectDate = viewModel::selectBookingDate,
                                onSelectAgent = { viewModel.selectBookingAgent(it, navigateToBook = false) },
                                onSelectSlot = viewModel::selectBookingSlot,
                                onUpdateLocation = viewModel::updateLocation,
                                onToggleArrivalPreset = viewModel::toggleArrivalPreset,
                                onUpdateContact = viewModel::updateContact,
                                onUpdatePayment = viewModel::updatePayment,
                                onUpdateReferral = viewModel::updateReferralCode,
                                onUseDaet50 = viewModel::applyDefaultReferral,
                                onUpdateOthers = viewModel::updateOthersRequest,
                                onSubmitBooking = { viewModel.submitBooking(dayConfigs) }
                            )

                            MainTab.BOOKINGS -> BookingsScreen(
                                userDisplayName = viewModel.userDisplayName,
                                userEmail = viewModel.userEmail,
                                isAdminUser = viewModel.isAdminUser,
                                bookingsState = studentBookingsState,
                                feedbackState = studentFeedbackState,
                                isSubmitting = isSubmitting,
                                onSignOut = onSignOut,
                                onBookFirstSlot = { viewModel.selectTab(MainTab.BOOK) },
                                onRebookBooking = { viewModel.rebookFromExisting(it) },
                                onSubmitGcashRef = viewModel::submitGcashRef,
                                onCancelBooking = viewModel::cancelBooking,
                                onSubmitRatingAndTip = viewModel::submitRatingAndTip,
                                onSubmitFeedback = viewModel::submitFeedback
                            )
                        }
                    }
                }
            }

            DormCleanFloatingBottomBar(
                currentTab = currentTab,
                isAdminViewActive = appMode == AppMode.ADMIN && viewModel.isAdminUser,
                onSelectTab = viewModel::selectTab,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
            }
        }
    }
}

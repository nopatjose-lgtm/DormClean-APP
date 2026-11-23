package com.example.data

import android.content.Context
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class DormCleanRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    private fun <T> wrapStreamError(
        flow: Flow<T>,
        path: String,
        uid: String
    ): Flow<T> = flow.catch { error ->
        val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
        if (firestoreError == null && error is CancellationException) throw error
        val exceptionToLog = firestoreError ?: (error as? Exception)
        if (exceptionToLog != null && auth.currentUser?.uid == uid) {
            handleFirestoreError(exceptionToLog, OperationType.LIST, path)
        }
        throw error
    }

    fun observeStudentBookings(userId: String): Flow<List<Booking>> = flow {
        require(userId.isNotBlank()) { "User ID must not be blank" }
        val path = "bookings"
        emitAll(
            wrapStreamError(
                db.collection(path)
                    .whereEqualTo("userId", userId)
                    .snapshots()
                    .map { snap ->
                        snap.toObjects(Booking::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                            .sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
                    },
                path,
                userId
            )
        )
    }

    fun observeAllBookingsForAdmin(userId: String): Flow<List<Booking>> = flow {
        require(userId.isNotBlank()) { "User ID must not be blank" }
        val path = "bookings"
        emitAll(
            wrapStreamError(
                db.collection(path)
                    .snapshots()
                    .map { snap ->
                        snap.toObjects(Booking::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                            .sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
                    },
                path,
                userId
            )
        )
    }

    fun observeSlotLocks(userId: String): Flow<List<SlotLock>> = flow {
        require(userId.isNotBlank()) { "User ID must not be blank" }
        val path = "slotLocks"
        emitAll(
            wrapStreamError(
                db.collection(path)
                    .snapshots()
                    .map { snap ->
                        snap.toObjects(SlotLock::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    },
                path,
                userId
            )
        )
    }

    fun observeDayConfigs(userId: String): Flow<Map<String, DayConfig>> = flow {
        require(userId.isNotBlank()) { "User ID must not be blank" }
        val path = "dayConfigs"
        emitAll(
            wrapStreamError(
                db.collection(path)
                    .snapshots()
                    .map { snap ->
                        snap.toObjects(DayConfig::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                            .associateBy { it.dateKey.ifBlank { it.id } }
                    },
                path,
                userId
            )
        )
    }

    fun observeStudentFeedback(userId: String): Flow<List<FeedbackReport>> = flow {
        require(userId.isNotBlank()) { "User ID must not be blank" }
        val path = "feedback"
        emitAll(
            wrapStreamError(
                db.collection(path)
                    .whereEqualTo("userId", userId)
                    .snapshots()
                    .map { snap ->
                        snap.toObjects(FeedbackReport::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                            .sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
                    },
                path,
                userId
            )
        )
    }

    fun observeAllFeedbackForAdmin(userId: String): Flow<List<FeedbackReport>> = flow {
        require(userId.isNotBlank()) { "User ID must not be blank" }
        val path = "feedback"
        emitAll(
            wrapStreamError(
                db.collection(path)
                    .snapshots()
                    .map { snap ->
                        snap.toObjects(FeedbackReport::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                            .sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
                    },
                path,
                userId
            )
        )
    }

    suspend fun createBooking(booking: Booking): Result<String> {
        var docPath = "bookings"
        return try {
            val uid = requireUserId()
            val email = auth.currentUser?.email ?: booking.userEmail
            require(booking.customerName.trim().length in 2..80) { "Please enter your name (at least 2 characters)." }
            require(booking.streetAddress.trim().length in 2..160) { "Please enter your house, boarding house, or street address." }
            require(booking.roomNumber.trim().length in 1..60) { "Please enter your room or house #." }
            require(booking.contactHandle.trim().length in 2..120) { "Please provide your contact link or number." }
            require(booking.dateKey.length == 10) { "Please select a valid date." }
            require(booking.slotId.isNotBlank() && booking.agentId.isNotBlank()) { "Please select an agent and 2-hr arrival window." }

            val bookingRef = db.collection("bookings").document()
            docPath = bookingRef.path
            val lockId = "${booking.dateKey}_${booking.agentId}_${booking.slotId}"
            val lockRef = db.collection("slotLocks").document(lockId)

            val cleanPaymentStatus = if (booking.paymentMethod == "gcash_qr" && booking.gcashRefNumber.trim().isNotEmpty()) {
                "submitted"
            } else {
                "unpaid"
            }

            val lockPayload = mapOf(
                "userId" to uid,
                "bookingId" to bookingRef.id,
                "dateKey" to booking.dateKey,
                "agentId" to booking.agentId,
                "slotId" to booking.slotId,
                "createdAt" to FieldValue.serverTimestamp()
            )

            val bookingPayload = mapOf(
                "userId" to uid,
                "userEmail" to email.take(120),
                "customerName" to booking.customerName.trim().take(80),
                "packageId" to booking.packageId,
                "packageName" to booking.packageName.take(60),
                "basePrice" to booking.basePrice,
                "occupancyType" to booking.occupancyType,
                "occupancyLabel" to booking.occupancyLabel.take(50),
                "occupancyFee" to booking.occupancyFee,
                "addons" to booking.addons.take(5),
                "addonsTotal" to booking.addonsTotal,
                "othersRequest" to booking.othersRequest.trim().take(300),
                "dateKey" to booking.dateKey,
                "dateLabel" to booking.dateLabel.take(40),
                "slotId" to booking.slotId,
                "slotLabel" to booking.slotLabel.take(30),
                "agentId" to booking.agentId,
                "agentName" to booking.agentName.take(60),
                "barangay" to booking.barangay,
                "streetAddress" to booking.streetAddress.trim().take(160),
                "roomNumber" to booking.roomNumber.trim().take(60),
                "floorGate" to booking.floorGate.trim().take(80),
                "landmark" to booking.landmark.trim().take(160),
                "arrivalNotes" to booking.arrivalNotes.trim().take(250),
                "contactChannel" to booking.contactChannel,
                "contactHandle" to booking.contactHandle.trim().take(120),
                "paymentMethod" to booking.paymentMethod,
                "paymentStatus" to cleanPaymentStatus,
                "gcashRefNumber" to booking.gcashRefNumber.trim().take(40),
                "referralCode" to booking.referralCode.trim().take(20),
                "referralDiscount" to booking.referralDiscount,
                "dayPromoName" to booking.dayPromoName.take(60),
                "dayPromoDiscount" to booking.dayPromoDiscount,
                "totalAmount" to booking.totalAmount.coerceAtLeast(0L),
                "status" to "pending_review",
                "rating" to 0L,
                "tipAmount" to 0L,
                "reviewComment" to "",
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )

            val batch = db.batch()
            batch.set(lockRef, lockPayload)
            batch.set(bookingRef, bookingPayload)
            batch.commit().await()

            Result.success(bookingRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun submitGcashReference(bookingId: String, refNumber: String): Result<Unit> {
        val docPath = "bookings/$bookingId"
        return try {
            requireUserId()
            require(bookingId.isNotBlank()) { "Booking ID is missing" }
            val trimmed = refNumber.trim().take(40)
            require(trimmed.length >= 4) { "Please enter a valid GCash reference number" }
            db.collection("bookings").document(bookingId).update(
                mapOf(
                    "gcashRefNumber" to trimmed,
                    "paymentStatus" to "submitted",
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun cancelBooking(booking: Booking): Result<Unit> {
        val docPath = "bookings/${booking.id}"
        return try {
            requireUserId()
            require(booking.id.isNotBlank()) { "Booking ID is missing" }
            val lockId = "${booking.dateKey}_${booking.agentId}_${booking.slotId}"
            val batch = db.batch()
            batch.update(
                db.collection("bookings").document(booking.id),
                mapOf(
                    "status" to "cancelled",
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
            batch.delete(db.collection("slotLocks").document(lockId))
            batch.commit().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun submitRatingAndTip(
        bookingId: String,
        rating: Long,
        tipAmount: Long,
        reviewComment: String
    ): Result<Unit> {
        val docPath = "bookings/$bookingId"
        return try {
            requireUserId()
            require(bookingId.isNotBlank()) { "Booking ID is missing" }
            require(rating in 1L..5L) { "Please pick a rating between 1 and 5 stars." }
            require(tipAmount in 0L..500L) { "Tip must be between ₱0 and ₱500." }
            db.collection("bookings").document(bookingId).update(
                mapOf(
                    "rating" to rating,
                    "tipAmount" to tipAmount,
                    "reviewComment" to reviewComment.trim().take(300),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun adminUpdateBookingStatus(booking: Booking, newStatus: String): Result<Unit> {
        val docPath = "bookings/${booking.id}"
        return try {
            requireUserId()
            require(booking.id.isNotBlank()) { "Booking ID is missing" }
            val batch = db.batch()
            batch.update(
                db.collection("bookings").document(booking.id),
                mapOf(
                    "status" to newStatus,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
            if (newStatus == "cancelled") {
                val lockId = "${booking.dateKey}_${booking.agentId}_${booking.slotId}"
                batch.delete(db.collection("slotLocks").document(lockId))
            }
            batch.commit().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun adminVerifyPayment(bookingId: String): Result<Unit> {
        val docPath = "bookings/$bookingId"
        return try {
            requireUserId()
            require(bookingId.isNotBlank()) { "Booking ID is missing" }
            db.collection("bookings").document(bookingId).update(
                mapOf(
                    "paymentStatus" to "verified",
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun adminDeleteBooking(booking: Booking): Result<Unit> {
        val docPath = "bookings/${booking.id}"
        return try {
            requireUserId()
            require(booking.id.isNotBlank()) { "Booking ID is missing" }
            val lockId = "${booking.dateKey}_${booking.agentId}_${booking.slotId}"
            val batch = db.batch()
            batch.delete(db.collection("bookings").document(booking.id))
            batch.delete(db.collection("slotLocks").document(lockId))
            batch.commit().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docPath)
            Result.failure(e)
        }
    }

    suspend fun adminSaveDayConfig(config: DayConfig): Result<Unit> {
        val docPath = "dayConfigs/${config.dateKey}"
        return try {
            val uid = requireUserId()
            require(config.dateKey.length == 10) { "Invalid date key" }
            val payload = mapOf(
                "dateKey" to config.dateKey,
                "forceStopped" to config.forceStopped,
                "stopReason" to config.stopReason.trim().take(120),
                "discountEnabled" to config.discountEnabled,
                "discountType" to config.discountType,
                "discountValue" to config.discountValue.coerceIn(0L, 500L),
                "promoName" to config.promoName.trim().take(60),
                "blockedSlots" to config.blockedSlots,
                "updatedBy" to uid,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            db.collection("dayConfigs").document(config.dateKey).set(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docPath)
            Result.failure(e)
        }
    }

    suspend fun submitFeedback(
        category: String,
        relatedBookingId: String,
        relatedLabel: String,
        message: String
    ): Result<Unit> {
        var docPath = "feedback"
        return try {
            val uid = requireUserId()
            val user = auth.currentUser
            val trimmed = message.trim()
            require(trimmed.length in 3..500) { "Please enter at least 3 characters of feedback." }
            val ref = db.collection("feedback").document()
            docPath = ref.path
            val payload = mapOf(
                "userId" to uid,
                "userName" to (user?.displayName?.takeIf { it.isNotBlank() } ?: "Student").take(80),
                "userEmail" to (user?.email ?: "").take(120),
                "category" to category,
                "relatedBookingId" to relatedBookingId.take(128),
                "relatedLabel" to relatedLabel.take(100),
                "message" to trimmed,
                "status" to "open",
                "adminResolution" to "",
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            ref.set(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun adminResolveFeedback(feedbackId: String, resolutionNote: String): Result<Unit> {
        val docPath = "feedback/$feedbackId"
        return try {
            requireUserId()
            require(feedbackId.isNotBlank()) { "Feedback ID is missing" }
            val trimmed = resolutionNote.trim().take(500)
            require(trimmed.isNotEmpty()) { "Please enter what your crew fixed or learned." }
            db.collection("feedback").document(feedbackId).update(
                mapOf(
                    "status" to "resolved",
                    "adminResolution" to trimmed,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }
}

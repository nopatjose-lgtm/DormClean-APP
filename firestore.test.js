const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const STUDENT_ALICE = "student_alice_123";
const STUDENT_BOB = "student_bob_456";
const ADMIN_UID = "admin_jose_789";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

function validBookingPayload(uid, overrides = {}) {
  const now = new Date(Date.now() - 2000);
  return {
    userId: uid,
    userEmail: "alice@example.com",
    customerName: "Alice Santos",
    packageId: "standard_dorm",
    packageName: "Standard Dorm Clean",
    basePrice: 500,
    occupancyType: "shared",
    occupancyLabel: "3–4 Pax Shared Room",
    occupancyFee: 100,
    addons: ["Inside Fridge", "Window Sills"],
    addonsTotal: 200,
    othersRequest: "Wipe electric fan blades",
    dateKey: "2026-11-25",
    dateLabel: "Wed, Nov 25, 2026",
    slotId: "08_10",
    slotLabel: "08:00 - 10:00 AM",
    agentId: "agent_nopat",
    agentName: "Nopat, Jose Antonio",
    barangay: "Daet Centro",
    streetAddress: "Santos Boarding House, F. Pimentel Ave",
    roomNumber: "Room 4",
    floorGate: "2nd Flr",
    landmark: "Near terminal",
    arrivalNotes: "Landlady holds the gate key",
    contactChannel: "Messenger",
    contactHandle: "m.me/alice.santos",
    paymentMethod: "gcash_qr",
    paymentStatus: "submitted",
    gcashRefNumber: "1002394812934",
    referralCode: "DAET50",
    referralDiscount: 50,
    dayPromoName: "",
    dayPromoDiscount: 0,
    totalAmount: 750,
    status: "pending_review",
    rating: 0,
    tipAmount: 0,
    reviewComment: "",
    createdAt: now,
    updatedAt: now,
    ...overrides,
  };
}

function validSlotLockPayload(uid, bookingId, dateKey = "2026-11-25", agentId = "agent_nopat", slotId = "08_10") {
  return {
    userId: uid,
    bookingId,
    dateKey,
    agentId,
    slotId,
    createdAt: new Date(Date.now() - 2000),
  };
}

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated users cannot read or write any collection", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("bookings").get());
  await assertFails(unauthDb.collection("slotLocks").get());
  await assertFails(unauthDb.collection("dayConfigs").get());
  await assertFails(unauthDb.collection("feedback").get());
  await assertFails(
    unauthDb.collection("bookings").doc("b1").set(validBookingPayload(STUDENT_ALICE))
  );
});

test("Student can atomically create booking + slotLock and read only own bookings", async () => {
  const aliceDb = testEnv.authenticatedContext(STUDENT_ALICE, {
    email: "alice@example.com",
    email_verified: true,
  }).firestore();

  const batch = aliceDb.batch();
  const bookingRef = aliceDb.collection("bookings").doc("booking_alice_1");
  const lockRef = aliceDb.collection("slotLocks").doc("2026-11-25_agent_nopat_08_10");
  batch.set(lockRef, validSlotLockPayload(STUDENT_ALICE, "booking_alice_1"));
  batch.set(bookingRef, validBookingPayload(STUDENT_ALICE));
  await assertSucceeds(batch.commit());

  // Matches StudentRepository query: where("userId", "==", uid)
  await assertSucceeds(
    aliceDb.collection("bookings").where("userId", "==", STUDENT_ALICE).get()
  );

  // Unfiltered query on bookings must fail for non-admin
  await assertFails(aliceDb.collection("bookings").get());

  // Cross-user PII read/write must fail
  const bobDb = testEnv.authenticatedContext(STUDENT_BOB, {
    email: "bob@example.com",
    email_verified: true,
  }).firestore();
  await assertFails(bobDb.collection("bookings").doc("booking_alice_1").get());
  await assertFails(bobDb.collection("bookings").where("userId", "==", STUDENT_ALICE).get());
  await assertFails(
    bobDb.collection("bookings").doc("booking_alice_1").update({
      status: "confirmed",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Slot lock can be read by Bob (PII-free) so UI disables booked slot
  await assertSucceeds(bobDb.collection("slotLocks").get());
});

test("Student cannot escalate booking status, inject @DocumentId id, or mutate immutable fields", async () => {
  const aliceDb = testEnv.authenticatedContext(STUDENT_ALICE, {
    email: "alice@example.com",
    email_verified: true,
  }).firestore();

  // Reject creating booking with 'id' field or pre-confirmed status
  const badBatch = aliceDb.batch();
  badBatch.set(
    aliceDb.collection("slotLocks").doc("2026-11-25_agent_nopat_10_12"),
    validSlotLockPayload(STUDENT_ALICE, "booking_bad", "2026-11-25", "agent_nopat", "10_12")
  );
  badBatch.set(
    aliceDb.collection("bookings").doc("booking_bad"),
    validBookingPayload(STUDENT_ALICE, { id: "booking_bad", slotId: "10_12" })
  );
  await assertFails(badBatch.commit());

  // Seed completed booking for Alice
  await testEnv.withSecurityRulesDisabled(async (ctx) => {
    await ctx.firestore().collection("bookings").doc("booking_done").set(
      validBookingPayload(STUDENT_ALICE, { status: "completed", paymentStatus: "verified" })
    );
  });

  // Student can rate & tip completed booking
  await assertSucceeds(
    aliceDb.collection("bookings").doc("booking_done").update({
      rating: 5,
      tipAmount: 50,
      reviewComment: "Sparkling clean desk and floor!",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Student cannot delete booking or change userId
  await assertFails(aliceDb.collection("bookings").doc("booking_done").delete());
  await assertFails(
    aliceDb.collection("bookings").doc("booking_done").update({
      userId: STUDENT_BOB,
      updatedAt: new Date(Date.now() - 1000),
    })
  );
});

test("Sole Admin (verified email) can manage bookings, dayConfigs, and feedback; spoofed unverified email fails", async () => {
  const spoofedDb = testEnv.authenticatedContext("spoof_uid", {
    email: "nopatjose@gmail.com",
    email_verified: false,
  }).firestore();

  await assertFails(
    spoofedDb.collection("dayConfigs").doc("2026-11-23").set({
      dateKey: "2026-11-23",
      forceStopped: true,
      stopReason: "Workers Day Off",
      discountEnabled: false,
      discountType: "percent",
      discountValue: 0,
      promoName: "",
      blockedSlots: {},
      updatedBy: "spoof_uid",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  const adminDb = testEnv.authenticatedContext(ADMIN_UID, {
    email: "nopatjose@gmail.com",
    email_verified: true,
  }).firestore();

  // Admin can list all bookings and manage dayConfigs
  await assertSucceeds(adminDb.collection("bookings").get());
  await assertSucceeds(
    adminDb.collection("dayConfigs").doc("2026-11-23").set({
      dateKey: "2026-11-23",
      forceStopped: true,
      stopReason: "Workers Day Off",
      discountEnabled: true,
      discountType: "percent",
      discountValue: 20,
      promoName: "Student Promo",
      blockedSlots: { "agent_nopat__08_10": "Crew Break" },
      updatedBy: ADMIN_UID,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Non-admin student cannot modify dayConfigs
  const aliceDb = testEnv.authenticatedContext(STUDENT_ALICE, {
    email: "alice@example.com",
    email_verified: true,
  }).firestore();
  await assertSucceeds(aliceDb.collection("dayConfigs").get());
  await assertFails(
    aliceDb.collection("dayConfigs").doc("2026-11-23").update({
      forceStopped: false,
      updatedAt: new Date(Date.now() - 1000),
    })
  );
});

test("Feedback: student creates open feedback; only Admin can resolve; cross-user read fails", async () => {
  const aliceDb = testEnv.authenticatedContext(STUDENT_ALICE, {
    email: "alice@example.com",
    email_verified: true,
  }).firestore();

  const now = new Date(Date.now() - 2000);
  await assertSucceeds(
    aliceDb.collection("feedback").doc("fb_1").set({
      userId: STUDENT_ALICE,
      userName: "Alice Santos",
      userEmail: "alice@example.com",
      category: "Clean Quality",
      relatedBookingId: "",
      relatedLabel: "General Service / DormClean App",
      message: "Please wipe under the study table drawer next time.",
      status: "open",
      adminResolution: "",
      createdAt: now,
      updatedAt: now,
    })
  );

  // Student cannot mark their own feedback resolved
  await assertFails(
    aliceDb.collection("feedback").doc("fb_1").update({
      status: "resolved",
      adminResolution: "Self approved",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Bob cannot list Alice's feedback
  const bobDb = testEnv.authenticatedContext(STUDENT_BOB, {
    email: "bob@example.com",
    email_verified: true,
  }).firestore();
  await assertFails(bobDb.collection("feedback").doc("fb_1").get());

  // Admin can read all feedback and resolve it
  const adminDb = testEnv.authenticatedContext(ADMIN_UID, {
    email: "antycepate@gmail.com",
    email_verified: true,
  }).firestore();
  await assertSucceeds(adminDb.collection("feedback").get());
  await assertSucceeds(
    adminDb.collection("feedback").doc("fb_1").update({
      status: "resolved",
      adminResolution: "Added under-drawer checklist to Standard Clean.",
      updatedAt: new Date(Date.now() - 1000),
    })
  );
});

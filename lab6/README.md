# Ticket Booking System - Design Document

## 1. Overview

This system implements a **concurrent ticket booking service** that supports:

- Holding seats temporarily
- Confirming bookings
- Cancelling holds
- Automatic expiry of holds

The system is designed to handle **high concurrency safely** using locks and atomic operations.

---

## 2. Core Components

### 2.1 TicketServiceImpl

Main service implementing the ticket booking logic.

**Responsibilities:**
- Handle seat holds, confirmations, and cancellations
- Maintain system metrics
- Coordinate with locking and expiry services

**Key Features:**
- Uses `AtomicLong` for ID generation
- Uses `ReentrantLock` for shared data protection
- Uses `CopyOnWriteArrayList` for thread-safe logs

**Important Methods:**
- `holdSeats(userId, count)`
- `confirm(holdId)`
- `cancel(holdId)`
- `seatState(seatId)`
- `metrics()`

---

### 2.2 Seat

Represents an individual seat.

**Fields:**
- `seatId`: Unique identifier
- `state`: FREE / HELD / BOOKED
- `holdId`: Associated hold ID
- `lock`: Per-seat lock

**Concurrency Design:**
- `volatile` fields ensure visibility across threads :contentReference[oaicite:0]{index=0}

---

### 2.3 SeatLockManager

Handles **deadlock-free locking of multiple seats**.

**Strategy:**
- Sort seats by `seatId`
- Lock in increasing order
- Unlock in reverse order

**Why?**
- Prevents circular wait → avoids deadlocks :contentReference[oaicite:1]{index=1}

---

### 2.4 HoldExpiryService

Manages automatic expiration of seat holds.

**Features:**
- Uses `ScheduledExecutorService`
- Schedules expiry tasks
- Allows cancellation of scheduled expiry

**Key Methods:**
- `scheduleExpiry(holdId, ttl, action)`
- `cancelExpiry(holdId)`
- `shutdown()`

**Concurrency:**
- Protects task map with `ReentrantLock` :contentReference[oaicite:2]{index=2}

---

## 3. Data Structures

| Component | Data Structure | Purpose |
|----------|--------------|--------|
| Seats | Array (`Seat[]`) | Fast indexed access |
| Holds | `HashMap<Long, Hold>` | Track active holds |
| Expiry Tasks | `HashMap<Long, ScheduledFuture>` | Manage timers |
| Metrics | `AtomicLong` | Thread-safe counters |

---

## 4. Workflow

### 4.1 Hold Seats

1. Scan for FREE seats
2. Select candidates
3. Lock seats (via `SeatLockManager`)
4. Re-check availability
5. Mark seats as HELD
6. Store hold
7. Schedule expiry

---

### 4.2 Confirm Booking

1. Retrieve hold
2. Cancel expiry task
3. Lock seats
4. Validate ownership
5. Mark seats as BOOKED
6. Generate receipt

---

### 4.3 Cancel Hold

1. Remove hold
2. Cancel expiry task
3. Lock seats
4. Release seats (FREE)

---

### 4.4 Expire Hold

1. Triggered by scheduler
2. Remove hold
3. Lock seats
4. Release seats if still HELD

---

## 5. Concurrency Design

### 5.1 Locking Strategy

- **Fine-grained locking** (per seat)
- **Global lock** for holds map
- **Ordered locking** prevents deadlocks

---

### 5.2 Visibility Guarantees

- `volatile` ensures latest state visibility
- No stale reads during seat scanning

---

### 5.3 Atomic Operations

- ID generation → `AtomicLong`
- Metrics tracking → `AtomicLong`

---

## 6. Failure Handling

| Scenario | Handling |
|--------|--------|
| Seats not available | Return `null` |
| Hold expired before confirm | Reject |
| Interrupted locking | Restore interrupt |
| System closed | Throw exception |

---

## 7. Metrics

Tracks:

- Successful bookings
- Expired holds
- Rejected requests

---

## 8. Design Highlights

- Deadlock-free seat locking
- Efficient read-heavy operations (lock-free scan)
- Automatic expiry with cancellation support
- Safe concurrent modifications

---

## 9. Limitations

- Busy-wait with `Thread.yield()` in hold logic
- HashMap for holds → may need scaling
- No persistence (in-memory only)

---

## 10. Possible Improvements

- Replace spin-wait with better retry/backoff
- Use `ConcurrentHashMap` for holds
- Add database persistence
- Add fairness in seat allocation
- Add distributed locking (for scaling)

---

## 11. Constants

- Hold TTL: `1500 ms` :contentReference[oaicite:3]{index=3}

---

## 12. Conclusion

This design ensures:

- Thread safety
- High concurrency
- Deadlock avoidance
- Correct seat lifecycle management

It is suitable for real-time booking systems with moderate scale.
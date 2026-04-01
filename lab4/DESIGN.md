# DESIGN

This document explains the design decisions used in the print spooler implementation, especially how race conditions and deadlocks are avoided.

---

## 1. Queue Design

The bounded queue is implemented using:
- `ArrayDeque` for storing job IDs in FIFO order.
- `synchronized` methods for mutual exclusion.
- `while(condition) wait()` for blocking behavior.
- `notifyAll()` after enqueue, dequeue, and remove operations.

### Capacity handling:
- `putBlocking()` waits while the queue is full.
- `putWithTimeout()` waits until space is available or timeout expires.
- `takeBlocking()` waits while the queue is empty.

### Why this is correct:
- All queue state is protected by a single monitor (`this`).
- No check-then-act race due to atomic critical sections.
- `while` loops prevent issues from spurious wakeups.
- `notifyAll()` ensures no thread remains stuck.

---

## 2. Job State Machine

Allowed transitions:
- `QUEUED → PRINTING → DONE`
- `QUEUED → CANCELLED`
- `PRINTING → CANCELLED`

All state transitions are handled in `JobRegistry`:
- `markPrinting()`
- `markDone()`
- `markCancelled()`

### Design:
- Entire registry is protected using `synchronized`.
- Each transition is atomic and linearizable.
- `markPrinting()` ensures cancelled jobs never start printing.

---

## 3. Cancellation Design

### Key ideas:
- `cancelRequested` (volatile) ensures visibility across threads.
- Cancellation is reflected immediately in `JobRegistry`.

### Queued job cancellation:
1. Job is marked `CANCELLED` in registry.
2. Attempt to remove from queue.
3. If removed, it never reaches worker.

### In-progress cancellation:
1. Job is marked `CANCELLED`.
2. Worker checks cancellation flag during execution.
3. Final state becomes `CANCELLED`.

### Correctness:
- Registry synchronization ensures no conflicting transitions.
- A job cannot transition to PRINTING after cancellation.

---

## 4. Thread Roles and Coordination

Threads involved:
- **Producers**: submit jobs.
- **Dispatcher**: moves jobs from queue to worker pool.
- **Workers**: execute print jobs.

### Coordination:
- Queue handles producer-consumer synchronization.
- Registry maintains authoritative job state.
- Metrics use `AtomicLong` for thread-safe updates.

---

## 5. Deadlock Avoidance

### Strategy:
- No cyclic lock dependencies.
- Queue and registry use separate monitors.
- No nested locking across components.

### Additional safeguards:
- Blocking (`wait`) only happens inside queue monitor.
- No long-running tasks inside synchronized blocks.
- `notifyAll()` avoids missed wakeups and starvation.

---

## 6. Race Condition Analysis

### 1. Submit vs Close
- `submit` checks `closed` before proceeding.
- Jobs after closure are rejected.

---

### 2. Multiple Producers
- Capacity check + enqueue done atomically.
- Queue never exceeds capacity.

---

### 3. Producer vs Consumer
- `while(wait)` + `notifyAll()` prevents missed wakeups.

---

### 4. Cancel vs Dequeue
- Cancel marks registry first, then removes from queue.
- If dequeue happens first, worker handles cancellation.

---

### 5. Cancel vs markPrinting
- Both operations synchronized.
- Cancelled jobs cannot transition to PRINTING.

---

### 6. Cancel vs markDone
- Both guarded by same lock.
- Final state depends on which occurs first, but always valid.

---

### 7. Status Visibility
- Registry methods synchronized → consistent reads.
- `cancelRequested` is volatile → visible to workers.

---

### 8. Metrics Updates
- `AtomicLong` ensures no lost updates.

---

## 7. Deadlock Analysis

### Why deadlocks are avoided:

1. **Single-lock usage**
   - Queue and registry use independent locks.
   - No nested lock acquisition.

2. **No blocking under multiple locks**
   - `wait()` only used in queue.
   - Registry does not block.

3. **Short critical sections**
   - Only small state updates inside locks.
   - Worker execution happens outside locks.

4. **Safe wakeups**
   - `notifyAll()` ensures correct thread scheduling.

---

## 8. Shutdown Behavior

`close()` performs:

1. Sets `closed = true` (stops new submissions)
2. Interrupts dispatcher (to break blocking wait)
3. Dispatcher exits only after queue is drained
4. Worker pool shutdown:
   - `shutdown()` → graceful
   - `awaitTermination()`
   - `shutdownNow()` fallback

### Why this is correct:
- No thread remains blocked indefinitely.
- All accepted jobs reach a terminal state.
- No resource leaks.

---

## 9. Summary of Correctness

### Race conditions handled by:
- `synchronized` queue operations
- `synchronized` registry transitions
- `AtomicLong` counters
- `volatile` cancellation flag

### Deadlocks avoided by:
- No circular lock dependencies
- Minimal lock scope
- Proper `wait/notifyAll` usage

### Guarantees:
- All jobs reach `DONE` or `CANCELLED`
- No deadlocks or hangs
- Clean and predictable shutdown
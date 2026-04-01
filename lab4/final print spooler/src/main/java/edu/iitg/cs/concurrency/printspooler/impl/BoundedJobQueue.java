package edu.iitg.cs.concurrency.printspooler.impl;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * TODO(STUDENT): Implement a correct bounded blocking queue using:
 * - synchronized
 * - while(condition) wait()
 * - notifyAll() after enqueue/dequeue/remove
 *
 * Implementation notes:
 * - This queue is a monitor (all state guarded by "this").
 * - Producers wait when full; consumers wait when empty.
 * - Every structural change wakes all waiters to re-check conditions.
 * - "maxDepth" tracks the highest observed queue size for metrics.
 */
final class BoundedJobQueue {
    private final int capacity;
    private final Deque<Long> q = new ArrayDeque<>();
    private int maxDepth = 0;

    BoundedJobQueue(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("capacity must be > 0");
        this.capacity = capacity;
    }

    public synchronized int maxDepthObserved() {
        return maxDepth;
    }

    /**
     * Blocks indefinitely until there is free capacity, then enqueues jobId.
     */
    public synchronized void putBlocking(long jobId) throws InterruptedException {
            // Wait while the buffer is full [cite: 84]
            while (q.size() >= capacity) {
                this.wait();
            }
            q.addLast(jobId);
            maxDepth = Math.max(maxDepth, q.size());
            // Notify the dispatcher that a new job is available [cite: 82, 149]
            this.notifyAll();
    }

    /**
     * Waits up to timeoutMs for free capacity.
     *
     * @return true if enqueued, false if timeout elapsed before space appeared.
     */
    public synchronized boolean putWithTimeout(long jobId, long timeoutMs) throws InterruptedException {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (q.size() >= capacity) {
            long remaining = deadline - System.currentTimeMillis();
            if (remaining <= 0) return false; // Timeout reached [cite: 86]
            this.wait(remaining);
        }
        q.addLast(jobId);
        maxDepth = Math.max(maxDepth, q.size());
        this.notifyAll();
        return true;
    }

    /**
     * Blocks indefinitely until an item exists, then dequeues and returns it.
     */
    public synchronized long takeBlocking() throws InterruptedException {
        // Wait while the buffer is empty [cite: 85]
        while (q.isEmpty()) {
            this.wait();
        }
        long id = q.removeFirst();
        // Notify producers that space is now available [cite: 82]
        this.notifyAll();
        return id;
    }

    /**
     * Removes jobId if it is still queued (used for queued-job cancellation).
     */
    public synchronized boolean removeIfPresent(long jobId) {
        boolean removed = q.remove(jobId);
        if (removed) {
            this.notifyAll(); // Wake producers blocked on a full queue [cite: 87]
        }
        return removed;
    }

    /**
     * Helper used by dispatcher shutdown logic to know whether draining is complete.
     */
    public synchronized boolean isEmpty() {
        return q.isEmpty();
    }
}

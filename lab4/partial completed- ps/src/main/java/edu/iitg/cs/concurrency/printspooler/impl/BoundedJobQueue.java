package edu.iitg.cs.concurrency.printspooler.impl;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * TODO(STUDENT): Implement a correct bounded blocking queue using:
 * - synchronized
 * - while(condition) wait()
 * - notifyAll() after enqueue/dequeue/remove
 */
final class BoundedJobQueue {
    private final int capacity;
    private final Deque<Long> q = new ArrayDeque<>();
    private int maxDepth = 0;

    BoundedJobQueue(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("capacity must be > 0");
        this.capacity = capacity;
    }

    int maxDepthObserved() {
        synchronized (this) { return maxDepth; }
    }

    void putBlocking(long jobId) throws InterruptedException {
        // TODO(STUDENT)
    }

    boolean putWithTimeout(long jobId, long timeoutMs) throws InterruptedException {
        synchronized (this) {
            // TODO(STUDENT)
            return true;
        }
    }

    long takeBlocking() throws InterruptedException {
        synchronized (this) {
            // TODO(STUDENT)
            return id;
        }
    }

    boolean removeIfPresent(long jobId) {
        synchronized (this) {
            boolean removed = q.remove(jobId);
            if (removed) notifyAll();
            return removed;
        }
    }
}

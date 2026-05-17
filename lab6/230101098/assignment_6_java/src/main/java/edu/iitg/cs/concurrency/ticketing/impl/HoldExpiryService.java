package edu.iitg.cs.concurrency.ticketing.impl;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

final class HoldExpiryService {

    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(2, r -> {
                Thread t = new Thread(r, "hold-expiry");
                t.setDaemon(true);
                return t;
            });

    private final Map<Long, ScheduledFuture<?>> futures = new HashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    void scheduleExpiry(long holdId, long ttlMs, Runnable action) {
        // TODO: schedule and keep cancellable handle
        ScheduledFuture<?> f = scheduler.schedule(action, ttlMs, TimeUnit.MILLISECONDS);
        lock.lock();
        try { futures.put(holdId, f); }
        finally { lock.unlock(); }
    }

    void cancelExpiry(long holdId) {
        // TODO: cancel scheduled task if present
        lock.lock();
        try {
            ScheduledFuture<?> f = futures.remove(holdId);
            if (f != null) f.cancel(false);
        } finally { lock.unlock(); }
    }

    void shutdown() {
        lock.lock();
        try {
            for (ScheduledFuture<?> f : futures.values()) f.cancel(false);
            futures.clear();
        } finally { lock.unlock(); }
        scheduler.shutdownNow();
    }
}
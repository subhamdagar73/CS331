package edu.iitg.cs.concurrency.ticketing.impl;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

final class HoldExpiryService {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    void scheduleExpiry(long holdId, long ttlMs, Runnable expireAction) {
        // TODO: schedule and keep cancellable handle
        scheduler.schedule(expireAction, ttlMs, TimeUnit.MILLISECONDS);
    }

    void cancelExpiry(long holdId) {
        // TODO: cancel scheduled task if present
    }

    void shutdown() {
        scheduler.shutdown();
    }
}

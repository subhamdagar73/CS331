package edu.iitg.cs.concurrency.ticketing.impl;

import edu.iitg.cs.concurrency.ticketing.api.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public final class TicketServiceImpl implements TicketService {

    public static final long HOLD_TTL_MS = 1500;

    private final Seat[] seats;
    private final SeatLockManager lockMgr = new SeatLockManager();
    private final HoldExpiryService expiry = new HoldExpiryService();

    private final Map<Long, Hold> holds = new HashMap<>();
    private final ReentrantLock holdsLock = new ReentrantLock();

    private final AtomicLong holdIdGen = new AtomicLong(1);
    private final AtomicLong receiptIdGen = new AtomicLong(1);

    private final CopyOnWriteArrayList<String> auditLog = new CopyOnWriteArrayList<>();

    private final AtomicLong successful = new AtomicLong();
    private final AtomicLong expired = new AtomicLong();
    private final AtomicLong rejected = new AtomicLong();

    private volatile boolean closed = false;

    public TicketServiceImpl(int seatCount) {
        this.seats = new Seat[seatCount];
        for (int i = 0; i < seatCount; i++) seats[i] = new Seat(i);
    }

    @Override
    public Hold holdSeats(String userId, int count) throws InterruptedException {
        if (closed) throw new IllegalStateException("closed");

        for (int spin = 0; spin < 500; spin++) {

            List<Seat> candidates = new ArrayList<>(count);
            int nonBooked = 0;

            for (Seat s : seats) {
                SeatState st = s.state;
                if (st != SeatState.BOOKED) nonBooked++;
                if (st == SeatState.FREE && candidates.size() < count) {
                    candidates.add(s);
                }
            }

            if (candidates.size() < count) {
                if (nonBooked < count) {
                    rejected.incrementAndGet();
                    return null;
                }
                Thread.yield();
                continue;
            }

            lockMgr.lockAll(candidates);
            try {
                // TODO: re-check they are FREE after locking, then mark HELD, record hold atomically
                boolean allFree = true;
                for (Seat s : candidates) {
                    if (s.state != SeatState.FREE) {
                        allFree = false;
                        break;
                    }
                }

                if (!allFree) continue;

                long hid = holdIdGen.getAndIncrement();

                for (Seat s : candidates) {
                    s.state = SeatState.HELD;
                    s.holdId = hid;
                }

                Hold h = new Hold(hid, userId,
                        candidates.stream().map(s -> s.seatId).toList(),
                        System.currentTimeMillis());

                holdsLock.lock();
                try { holds.put(hid, h); }
                finally { holdsLock.unlock(); }

                expiry.scheduleExpiry(hid, HOLD_TTL_MS, () -> expireHold(hid));
                return h;

            } finally {
                lockMgr.unlockAll(candidates);
            }
        }

        rejected.incrementAndGet();
        return null;
    }

    private void expireHold(long holdId) {
        // TODO: atomically release seats if still HELD for this holdId
        if (closed) return;

        Hold h;
        holdsLock.lock();
        try { h = holds.remove(holdId); }
        finally { holdsLock.unlock(); }

        if (h == null) return;

        List<Seat> ss = new ArrayList<>();
        for (int sid : h.seatIds()) ss.add(seats[sid]);

        try {
            lockMgr.lockAll(ss);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        try {
            for (Seat s : ss) {
                if (s.state == SeatState.HELD && s.holdId == holdId) {
                    s.state = SeatState.FREE;
                    s.holdId = -1;
                }
            }
        } finally {
            lockMgr.unlockAll(ss);
        }

        expired.incrementAndGet();
    }

    @Override
    public Receipt confirm(long holdId) throws InterruptedException {
        Hold h;
        holdsLock.lock();
        try { h = holds.remove(holdId); }
        finally { holdsLock.unlock(); }

        if (h == null) {
            rejected.incrementAndGet();
            return null;
        }

        expiry.cancelExpiry(holdId);

        List<Seat> ss = new ArrayList<>();
        for (int sid : h.seatIds()) ss.add(seats[sid]);

        lockMgr.lockAll(ss);
        try {
            // TODO: validate still held by this holdId, then book
            for (Seat s : ss) {
                if (s.state != SeatState.HELD || s.holdId != holdId) {
                    rejected.incrementAndGet();
                    return null;
                }
            }

            for (Seat s : ss) {
                s.state = SeatState.BOOKED;
                s.holdId = -1;
            }

            successful.incrementAndGet();
            return new Receipt(receiptIdGen.getAndIncrement(),
                    h.userId(), h.seatIds(), System.currentTimeMillis());

        } finally {
            lockMgr.unlockAll(ss);
        }
    }

    @Override
    public boolean cancel(long holdId) {
        Hold h;
        holdsLock.lock();
        try { h = holds.remove(holdId); }
        finally { holdsLock.unlock(); }

        if (h == null) return false;
        // TODO: lock seats and release atomically
        expiry.cancelExpiry(holdId);

        List<Seat> ss = new ArrayList<>();
        for (int sid : h.seatIds()) ss.add(seats[sid]);

        try {
            lockMgr.lockAll(ss);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }

        try {
            for (Seat s : ss) {
                if (s.state == SeatState.HELD && s.holdId == holdId) {
                    s.state = SeatState.FREE;
                    s.holdId = -1;
                }
            }
        } finally {
            lockMgr.unlockAll(ss);
        }

        return true;
    }

    @Override
    public SeatState seatState(int seatId) {
        return seats[seatId].state;
    }

    @Override
    public TicketMetrics metrics() {
        return new TicketMetrics(successful.get(), expired.get(), rejected.get());
    }

    @Override
    public void close() {
        closed = true;
        expiry.shutdown();
    }
}

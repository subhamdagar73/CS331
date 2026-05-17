package edu.iitg.cs.concurrency.ticketing.impl;

import edu.iitg.cs.concurrency.ticketing.api.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

public final class TicketServiceImpl implements TicketService {
    public static final long HOLD_TTL_MS = 1500;

    private final Seat[] seats;
    private final SeatLockManager lockMgr = new SeatLockManager();
    private final HoldExpiryService expiry = new HoldExpiryService();

    private final Map<Long, Hold> holds = new HashMap<>(); // TODO: protect with synchronized/locks
    private final AtomicLong holdIdGen = new AtomicLong(1);
    private final AtomicLong receiptIdGen = new AtomicLong(1);

    private final CopyOnWriteArrayList<String> auditLog = new CopyOnWriteArrayList<>();

    private final AtomicLong successful = new AtomicLong();
    private final AtomicLong expired = new AtomicLong();
    private final AtomicLong rejected = new AtomicLong();

    private volatile boolean closed = false;

    public TicketServiceImpl(int seatCount) {
        if (seatCount <= 0) throw new IllegalArgumentException("seatCount must be > 0");
        this.seats = new Seat[seatCount];
        for (int i = 0; i < seatCount; i++) seats[i] = new Seat(i);
    }

    @Override
    public Hold holdSeats(String userId, int count) throws InterruptedException {
        if (closed) throw new IllegalStateException("closed");
        if (count <= 0) throw new IllegalArgumentException("count must be > 0");

        List<Seat> chosen = new ArrayList<>();
        for (Seat s : seats) {
            if (chosen.size() == count) break;
            if (s.state == SeatState.FREE) chosen.add(s);
        }
        if (chosen.size() < count) {
            rejected.incrementAndGet();
            return null;
        }

        lockMgr.lockAll(chosen);
        try {
            // TODO: re-check they are FREE after locking, then mark HELD, record hold atomically
            long hid = holdIdGen.getAndIncrement();
            long now = System.currentTimeMillis();
            for (Seat s : chosen) {
                s.state = SeatState.HELD;
                s.holdId = hid;
            }
            Hold h = new Hold(hid, userId, chosen.stream().map(seat -> seat.seatId).toList(), now);
            holds.put(hid, h);
            auditLog.add("HOLD " + hid + " user=" + userId);

            expiry.scheduleExpiry(hid, HOLD_TTL_MS, () -> expireHold(hid));
            return h;
        } finally {
            lockMgr.unlockAll(chosen);
        }
    }

    private void expireHold(long holdId) {
        // TODO: atomically release seats if still HELD for this holdId
        expired.incrementAndGet();
        auditLog.add("EXPIRE " + holdId);
    }

    @Override
    public Receipt confirm(long holdId) throws InterruptedException {
        if (closed) throw new IllegalStateException("closed");
        Hold h = holds.get(holdId);
        if (h == null) {
            rejected.incrementAndGet();
            return null;
        }

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
            for (Seat s : ss) s.state = SeatState.BOOKED;
            expiry.cancelExpiry(holdId);
            holds.remove(holdId);
            successful.incrementAndGet();
            auditLog.add("CONFIRM " + holdId);
            return new Receipt(receiptIdGen.getAndIncrement(), h.userId(), h.seatIds(), System.currentTimeMillis());
        } finally {
            lockMgr.unlockAll(ss);
        }
    }

    @Override
    public boolean cancel(long holdId) {
        Hold h = holds.get(holdId);
        if (h == null) return false;

        // TODO: lock seats and release atomically
        expiry.cancelExpiry(holdId);
        holds.remove(holdId);
        auditLog.add("CANCEL " + holdId);
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
    public void close() throws Exception {
        closed = true;
        expiry.shutdown();
    }
}

package edu.iitg.cs.concurrency.ticketing.impl;

import java.util.*;

final class SeatLockManager {

    void lockAll(List<Seat> seats) throws InterruptedException {
        // TODO(STUDENT): implement deadlock-free locking.
        // Current naive strategy is NOT safe under concurrency.
        List<Seat> sorted = new ArrayList<>(seats);
        sorted.sort(Comparator.comparingInt(s -> s.seatId));
        for (Seat s : sorted) s.lock.lockInterruptibly();
    }

    void unlockAll(List<Seat> seats) {
        List<Seat> sorted = new ArrayList<>(seats);
        sorted.sort(Comparator.comparingInt(s -> s.seatId));
        for (int i = sorted.size() - 1; i >= 0; i--) {
            Seat s = sorted.get(i);
            if (s.lock.isHeldByCurrentThread()) s.lock.unlock();
        }
    }
}
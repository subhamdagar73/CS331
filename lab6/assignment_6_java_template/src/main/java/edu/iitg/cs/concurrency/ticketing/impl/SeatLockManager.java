package edu.iitg.cs.concurrency.ticketing.impl;

import java.util.List;

final class SeatLockManager {
    void lockAll(List<Seat> seats) throws InterruptedException {
        // TODO(STUDENT): implement deadlock-free locking.
        // Current naive strategy is NOT safe under concurrency.
        for (Seat s : seats) {
            s.lock.lock();
        }
    }

    void unlockAll(List<Seat> seats) {
        for (int i = seats.size()-1; i >= 0; i--) {
            Seat s = seats.get(i);
            if (s.lock.isHeldByCurrentThread()) s.lock.unlock();
        }
    }
}

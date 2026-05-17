package edu.iitg.cs.concurrency.ticketing.impl;

import edu.iitg.cs.concurrency.ticketing.api.SeatState;
import java.util.concurrent.locks.ReentrantLock;

final class Seat {
    final int seatId;
    final ReentrantLock lock = new ReentrantLock();

    // volatile ensures the lockless dirty-scan in holdSeats() always sees
    // the latest state written by any thread (expiry, confirm, cancel).
   
    volatile SeatState state = SeatState.FREE;
    volatile long holdId = -1;

    Seat(int seatId) { this.seatId = seatId; }
}
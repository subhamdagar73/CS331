package edu.iitg.cs.concurrency.ticketing;

import edu.iitg.cs.concurrency.ticketing.impl.TicketServiceImpl;
import edu.iitg.cs.concurrency.ticketing.api.Hold;
import edu.iitg.cs.concurrency.ticketing.api.SeatState;
import org.junit.jupiter.api.Test;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HiddenTest {

    /**
     * SCENARIO A: Expiry must not free seats that were confirmed just in time.
     * This tests for "lost updates" and race conditions between the timer and confirm().
     */
    @Test
    void testConfirmWinsAgainstExpiryRace() throws Exception {
        try (var svc = new TicketServiceImpl(10)) {
            Hold h = svc.holdSeats("user_race", 5);
            assertNotNull(h);

            // Wait until very close to the TTL (1500ms)
            Thread.sleep(TicketServiceImpl.HOLD_TTL_MS - 100);

            // Confirming now should cancel the expiry task
            var receipt = svc.confirm(h.holdId());
            assertNotNull(receipt);

            // Wait for the original expiry time to pass completely
            Thread.sleep(300);

            // Verify: Seats must stay BOOKED, not be reverted to FREE by a stale timer
            for (int sid : h.seatIds()) {
                assertEquals(SeatState.BOOKED, svc.seatState(sid), "Confirmed seats were wrongly freed by expiry!");
            }
        }
    }

    /**
     * SCENARIO B: Concurrency stress with high contention.
     * Checks for overbooking and state corruption (HELD + BOOKED <= Capacity).
     */
    @Test
    void testHighContentionBooking() throws Exception {
        int seatCount = 50;
        int threadCount = 32;
        int holdSize = 5;
        
        try (var svc = new TicketServiceImpl(seatCount)) {
            ExecutorService exec = Executors.newFixedThreadPool(threadCount);
            CountDownLatch startLatch = new CountDownLatch(1);
            AtomicInteger successCount = new AtomicInteger();

            for (int i = 0; i < 200; i++) {
                exec.submit(() -> {
                    try {
                        startLatch.await();
                        Hold h = svc.holdSeats("u", holdSize);
                        if (h != null) {
                            if (svc.confirm(h.holdId()) != null) {
                                successCount.incrementAndGet();
                            }
                        }
                    } catch (Exception ignored) {}
                });
            }

            startLatch.countDown();
            exec.shutdown();
            exec.awaitTermination(10, TimeUnit.SECONDS);

            // Total booked seats check
            int bookedSeats = 0;
            for (int i = 0; i < seatCount; i++) {
                if (svc.seatState(i) == SeatState.BOOKED) bookedSeats++;
            }

            assertEquals(successCount.get() * holdSize, bookedSeats, "Booked seat count mismatch!");
            assertTrue(bookedSeats <= seatCount, "Overbooked capacity!");
        }
    }

    /**
     * DEADLOCK TEST: Circular dependency check.
     * Threads requesting same seats in different orders should not hang.
     */
    @Test
    void testDeadlockAvoidance() throws Exception {
        try (var svc = new TicketServiceImpl(2)) {
            // Thread A wants [0, 1]
            // Thread B wants [1, 0]
            // Using try-lock rollback, they should eventually succeed or reject without hanging.
            
            Thread t1 = new Thread(() -> {
                try { for(int i=0; i<100; i++) svc.holdSeats("A", 2); } catch (Exception e) {}
            });
            Thread t2 = new Thread(() -> {
                try { for(int i=0; i<100; i++) svc.holdSeats("B", 2); } catch (Exception e) {}
            });

            t1.start(); t2.start();
            t1.join(5000); t2.join(5000);

            assertFalse(t1.isAlive(), "Thread 1 deadlocked!");
            assertFalse(t2.isAlive(), "Thread 2 deadlocked!");
        }
    }

    /**
     * SCENARIO C: Proper shutdown. 
     * Background scheduler should not prevent JVM exit.
     */
    @Test
    void testShutdownCleanliness() throws Exception {
        TicketServiceImpl svc = new TicketServiceImpl(10);
        svc.holdSeats("user_shutdown", 1);
        
        long start = System.currentTimeMillis();
        svc.close(); // Should trigger shutdownNow() in the expiry service
        long duration = System.currentTimeMillis() - start;

        assertTrue(duration < 1000, "close() hung for too long!");
    }
}

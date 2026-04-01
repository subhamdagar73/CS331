package edu.iitg.cs.concurrency.printspooler.impl;

import edu.iitg.cs.concurrency.printspooler.api.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * TODO(STUDENT):
 * - Ensure dispatcher does not die if queue empty.
 * - Ensure cancel() makes status CANCELLED immediately.
 * - Ensure close() shuts down threads correctly.
 *
 * Implementation notes:
 * - Producers only enqueue job IDs; workers read full job info from JobRegistry.
 * - A dedicated dispatcher thread bridges queue -> ExecutorService.
 * - close() stops new acceptance, drains already accepted queue items, then shuts the pool.
 */
public final class SpoolerImpl implements Spooler {
    private final AtomicLong idGen = new AtomicLong(1);
    private final AtomicLong submitted = new AtomicLong(0);
    private final AtomicLong completed = new AtomicLong(0);
    private final AtomicLong cancelled = new AtomicLong(0);

    private final JobRegistry registry = new JobRegistry();
    private final BoundedJobQueue queue;

    private final ExecutorService pool;
    private final Thread dispatcher;

    // Volatile so producer/dispatcher threads observe closure quickly.
    private volatile boolean closed = false;

    public SpoolerImpl(int capacity, int workers) {
        this.queue = new BoundedJobQueue(capacity);
        this.pool = Executors.newFixedThreadPool(workers);

        // Dispatcher is the only thread that dequeues IDs and submits worker tasks.
        this.dispatcher = new Thread(() -> {
            while (true) {
                try {
                    // On shutdown, exit only after accepted queued jobs are drained.
                    if (closed && queue.isEmpty()) break;
                    long jobId = queue.takeBlocking();
                    pool.submit(() -> {
                        // PrintWorker enforces printing/cancellation at per-page granularity.
                        new PrintWorker(jobId, registry).run();
                        JobStatus st = registry.status(jobId);
                        // Metrics are updated after job reaches a terminal status.
                        if (st == JobStatus.DONE) completed.incrementAndGet();
                        else if (st == JobStatus.CANCELLED) cancelled.incrementAndGet();
                    });
                } catch (InterruptedException e) {
                    if (closed) {
                        // Wake-up requested during close(); loop checks for drain completion.
                        continue;
                    }
                    Thread.currentThread().interrupt();
                    break;
                } catch (RuntimeException e) {
                    // TODO(STUDENT): avoid dispatcher death
                    // Keep dispatcher alive on transient runtime failures so spooler keeps serving.
                    System.err.println("Dispatcher encountered an error: " + e.getMessage());
                }
            }
        }, "spooler-dispatcher");
        this.dispatcher.start();
    }

    @Override
    public long submitBlocking(PrintJob job) throws InterruptedException {
        // Reject submissions after close() begins.
        if (closed) throw new IllegalStateException("spooler closed");
        long id = idGen.getAndIncrement();
        // Register first, then enqueue ID; workers lookup the record by ID.
        registry.create(id, job);
        queue.putBlocking(id);
        submitted.incrementAndGet();
        return id;
    }

    @Override
    public long trySubmit(PrintJob job, long timeoutMs) throws InterruptedException {
        if (closed) throw new IllegalStateException("spooler closed");
        long id = idGen.getAndIncrement();
        registry.create(id, job);
        boolean ok = queue.putWithTimeout(id, timeoutMs);
        if (!ok) {
            // Timeout means job never entered queue; mark cancelled terminally.
            registry.markCancelled(id);
            return -1;
        }
        submitted.incrementAndGet();
        return id;
    }

    @Override
    public boolean cancel(long jobId) {
        boolean removed = queue.removeIfPresent(jobId);
        boolean marked = registry.markCancelled(jobId); 
        
        // If it was removed from the queue, it never reaches the worker, so increment here [cite: 169]
        if (marked && removed) cancelled.incrementAndGet();
        return marked;
    }

    @Override
    public JobStatus status(long jobId) {
        JobStatus s = registry.status(jobId);
        // API expects a terminal-like response for unknown IDs in this scaffold.
        return s == null ? JobStatus.CANCELLED : s;
    }

    @Override
    public SpoolerMetrics metrics() {
        return new SpoolerMetrics(
                submitted.get(),
                completed.get(),
                cancelled.get(),
                queue.maxDepthObserved()
        );
    }

    @Override
    public void close() throws Exception {
        // TODO(STUDENT): implement clean shutdown
        // Phase 1: stop accepting new work and wake dispatcher if blocked on empty queue.
        closed = true;
        dispatcher.interrupt();
        // Wait for dispatcher to finish draining queue -> pool submissions.
        dispatcher.join();

        // ExecutorService shutdown contract: graceful first, then forceful fallback.
        pool.shutdown();
        if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
            pool.shutdownNow();
            pool.awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}

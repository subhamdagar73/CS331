package edu.iitg.cs.concurrency.printspooler.impl;

import edu.iitg.cs.concurrency.printspooler.api.JobStatus;
import edu.iitg.cs.concurrency.printspooler.api.PrintJob;

import java.util.HashMap;
import java.util.Map;

/**
 * STUDENT TODO:
 * Make this class thread-safe and "linearizable" (operations appear atomic).
 *
 * Allowed: synchronized methods/blocks OR ReentrantLock (if covered).
 *
 * Required state machine:
 *   QUEUED   -> PRINTING -> DONE
 *   QUEUED   -> CANCELLED
 *   PRINTING -> CANCELLED
 *
 * IMPORTANT:
 * - Cancellation must be visible quickly (cancelRequested is volatile in JobRecord).
 * - markPrinting must NOT start printing if a job was cancelled.
 *
 * Implementation notes:
 * - All methods are synchronized on the registry instance.
 * - This makes each transition/check linearizable and race-safe.
 * - Transition guards enforce the allowed state machine.
 */
final class JobRegistry {
    private final Map<Long, JobRecord> jobs = new HashMap<>();

    // TODO: make thread-safe
    synchronized JobRecord create(long jobId, PrintJob job) {
        // New jobs always start in QUEUED (see JobRecord default).
        JobRecord r = new JobRecord(jobId, job);
        jobs.put(jobId, r);
        return r;
    }

    // TODO: make thread-safe
    synchronized JobRecord get(long jobId) {
        return jobs.get(jobId);
    }

    // TODO: make thread-safe + enforce cancel rule
    synchronized boolean markPrinting(long jobId) {
        JobRecord record = jobs.get(jobId);
        // Only move to PRINTING if currently QUEUED and NOT cancelled [cite: 93, 98]
        if (record != null && record.status == JobStatus.QUEUED && !record.cancelRequested) {
            record.status = JobStatus.PRINTING;
            return true;
        }
        return false;
    }

    // TODO: make thread-safe
    synchronized void markDone(long jobId) {
        JobRecord record = jobs.get(jobId);
        if (record != null && record.status == JobStatus.PRINTING) {
            record.status = JobStatus.DONE; 
        }
    }

    // TODO: make thread-safe
    synchronized boolean markCancelled(long jobId) {
        JobRecord record = jobs.get(jobId);
        if (record == null || record.status == JobStatus.DONE) return false;
        
        record.cancelRequested = true; // Volatile flag for PrintWorker visibility [cite: 150]
        record.status = JobStatus.CANCELLED; 
        return true;
    }

    // TODO: make thread-safe
    synchronized JobStatus status(long jobId) {
        JobRecord record = jobs.get(jobId);
        return (record != null) ? record.status : null;
    }
}
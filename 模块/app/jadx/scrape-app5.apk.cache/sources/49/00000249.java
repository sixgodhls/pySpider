package okio;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public class Timeout {
    public static final Timeout NONE = new Timeout() { // from class: okio.Timeout.1
        @Override // okio.Timeout
        public Timeout timeout(long timeout, TimeUnit unit) {
            return this;
        }

        @Override // okio.Timeout
        public Timeout deadlineNanoTime(long deadlineNanoTime) {
            return this;
        }

        @Override // okio.Timeout
        public void throwIfReached() throws IOException {
        }
    };
    private long deadlineNanoTime;
    private boolean hasDeadline;
    private long timeoutNanos;

    public Timeout timeout(long timeout, TimeUnit unit) {
        if (timeout < 0) {
            throw new IllegalArgumentException("timeout < 0: " + timeout);
        } else if (unit == null) {
            throw new IllegalArgumentException("unit == null");
        } else {
            this.timeoutNanos = unit.toNanos(timeout);
            return this;
        }
    }

    public long timeoutNanos() {
        return this.timeoutNanos;
    }

    public boolean hasDeadline() {
        return this.hasDeadline;
    }

    public long deadlineNanoTime() {
        if (!this.hasDeadline) {
            throw new IllegalStateException("No deadline");
        }
        return this.deadlineNanoTime;
    }

    public Timeout deadlineNanoTime(long deadlineNanoTime) {
        this.hasDeadline = true;
        this.deadlineNanoTime = deadlineNanoTime;
        return this;
    }

    public final Timeout deadline(long duration, TimeUnit unit) {
        if (duration <= 0) {
            throw new IllegalArgumentException("duration <= 0: " + duration);
        } else if (unit == null) {
            throw new IllegalArgumentException("unit == null");
        } else {
            return deadlineNanoTime(System.nanoTime() + unit.toNanos(duration));
        }
    }

    public Timeout clearTimeout() {
        this.timeoutNanos = 0L;
        return this;
    }

    public Timeout clearDeadline() {
        this.hasDeadline = false;
        return this;
    }

    public void throwIfReached() throws IOException {
        if (Thread.interrupted()) {
            throw new InterruptedIOException("thread interrupted");
        }
        if (this.hasDeadline && this.deadlineNanoTime - System.nanoTime() <= 0) {
            throw new InterruptedIOException("deadline reached");
        }
    }

    public final void waitUntilNotified(Object monitor) throws InterruptedIOException {
        long deadlineNanos;
        try {
            boolean hasDeadline = hasDeadline();
            long timeoutNanos = timeoutNanos();
            if (!hasDeadline && timeoutNanos == 0) {
                monitor.wait();
                return;
            }
            long start = System.nanoTime();
            if (hasDeadline && timeoutNanos != 0) {
                long deadlineNanos2 = deadlineNanoTime() - start;
                deadlineNanos = Math.min(timeoutNanos, deadlineNanos2);
            } else if (hasDeadline) {
                deadlineNanos = deadlineNanoTime() - start;
            } else {
                deadlineNanos = timeoutNanos;
            }
            long elapsedNanos = 0;
            if (deadlineNanos > 0) {
                long waitMillis = deadlineNanos / 1000000;
                Long.signum(waitMillis);
                monitor.wait(waitMillis, (int) (deadlineNanos - (1000000 * waitMillis)));
                elapsedNanos = System.nanoTime() - start;
            }
            if (elapsedNanos >= deadlineNanos) {
                throw new InterruptedIOException("timeout");
            }
        } catch (InterruptedException e) {
            throw new InterruptedIOException("interrupted");
        }
    }
}
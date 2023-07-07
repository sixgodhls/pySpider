package okhttp3;

import android.support.v7.widget.ActivityChooserView;
import java.lang.ref.Reference;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import okhttp3.internal.Util;
import okhttp3.internal.connection.RealConnection;
import okhttp3.internal.connection.RouteDatabase;
import okhttp3.internal.connection.StreamAllocation;
import okhttp3.internal.platform.Platform;

/* loaded from: classes.dex */
public final class ConnectionPool {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final Executor executor = new ThreadPoolExecutor(0, (int) ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED, 60, TimeUnit.SECONDS, new SynchronousQueue(), Util.threadFactory("OkHttp ConnectionPool", true));
    private final Runnable cleanupRunnable;
    boolean cleanupRunning;
    private final Deque<RealConnection> connections;
    private final long keepAliveDurationNs;
    private final int maxIdleConnections;
    final RouteDatabase routeDatabase;

    public ConnectionPool() {
        this(5, 5L, TimeUnit.MINUTES);
    }

    public ConnectionPool(int maxIdleConnections, long keepAliveDuration, TimeUnit timeUnit) {
        this.cleanupRunnable = new Runnable() { // from class: okhttp3.ConnectionPool.1
            @Override // java.lang.Runnable
            public void run() {
                while (true) {
                    long waitNanos = ConnectionPool.this.cleanup(System.nanoTime());
                    if (waitNanos == -1) {
                        return;
                    }
                    if (waitNanos > 0) {
                        long waitMillis = waitNanos / 1000000;
                        long waitNanos2 = waitNanos - (1000000 * waitMillis);
                        synchronized (ConnectionPool.this) {
                            try {
                                ConnectionPool.this.wait(waitMillis, (int) waitNanos2);
                            } catch (InterruptedException e) {
                            }
                        }
                    }
                }
            }
        };
        this.connections = new ArrayDeque();
        this.routeDatabase = new RouteDatabase();
        this.maxIdleConnections = maxIdleConnections;
        this.keepAliveDurationNs = timeUnit.toNanos(keepAliveDuration);
        if (keepAliveDuration <= 0) {
            throw new IllegalArgumentException("keepAliveDuration <= 0: " + keepAliveDuration);
        }
    }

    public synchronized int idleConnectionCount() {
        int total;
        total = 0;
        for (RealConnection connection : this.connections) {
            if (connection.allocations.isEmpty()) {
                total++;
            }
        }
        return total;
    }

    public synchronized int connectionCount() {
        return this.connections.size();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public RealConnection get(Address address, StreamAllocation streamAllocation, Route route) {
        for (RealConnection connection : this.connections) {
            if (connection.isEligible(address, route)) {
                streamAllocation.acquire(connection, true);
                return connection;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public Socket deduplicate(Address address, StreamAllocation streamAllocation) {
        for (RealConnection connection : this.connections) {
            if (connection.isEligible(address, null) && connection.isMultiplexed() && connection != streamAllocation.connection()) {
                return streamAllocation.releaseAndAcquire(connection);
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void put(RealConnection connection) {
        if (!this.cleanupRunning) {
            this.cleanupRunning = true;
            executor.execute(this.cleanupRunnable);
        }
        this.connections.add(connection);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean connectionBecameIdle(RealConnection connection) {
        if (connection.noNewStreams || this.maxIdleConnections == 0) {
            this.connections.remove(connection);
            return true;
        }
        notifyAll();
        return false;
    }

    public void evictAll() {
        List<RealConnection> evictedConnections = new ArrayList<>();
        synchronized (this) {
            Iterator<RealConnection> i = this.connections.iterator();
            while (i.hasNext()) {
                RealConnection connection = i.next();
                if (connection.allocations.isEmpty()) {
                    connection.noNewStreams = true;
                    evictedConnections.add(connection);
                    i.remove();
                }
            }
        }
        for (RealConnection connection2 : evictedConnections) {
            Util.closeQuietly(connection2.socket());
        }
    }

    long cleanup(long now) {
        int inUseConnectionCount = 0;
        int idleConnectionCount = 0;
        RealConnection longestIdleConnection = null;
        long longestIdleDurationNs = Long.MIN_VALUE;
        synchronized (this) {
            for (RealConnection connection : this.connections) {
                if (pruneAndGetAllocationCount(connection, now) > 0) {
                    inUseConnectionCount++;
                } else {
                    idleConnectionCount++;
                    long idleDurationNs = now - connection.idleAtNanos;
                    if (idleDurationNs > longestIdleDurationNs) {
                        longestIdleDurationNs = idleDurationNs;
                        longestIdleConnection = connection;
                    }
                }
            }
            if (longestIdleDurationNs < this.keepAliveDurationNs && idleConnectionCount <= this.maxIdleConnections) {
                if (idleConnectionCount > 0) {
                    return this.keepAliveDurationNs - longestIdleDurationNs;
                } else if (inUseConnectionCount > 0) {
                    return this.keepAliveDurationNs;
                } else {
                    this.cleanupRunning = false;
                    return -1L;
                }
            }
            this.connections.remove(longestIdleConnection);
            Util.closeQuietly(longestIdleConnection.socket());
            return 0L;
        }
    }

    private int pruneAndGetAllocationCount(RealConnection connection, long now) {
        List<Reference<StreamAllocation>> references = connection.allocations;
        int i = 0;
        while (i < references.size()) {
            Reference<StreamAllocation> reference = references.get(i);
            if (reference.get() != null) {
                i++;
            } else {
                StreamAllocation.StreamAllocationReference streamAllocRef = (StreamAllocation.StreamAllocationReference) reference;
                String message = "A connection to " + connection.route().address().url() + " was leaked. Did you forget to close a response body?";
                Platform.get().logCloseableLeak(message, streamAllocRef.callStackTrace);
                references.remove(i);
                connection.noNewStreams = true;
                if (references.isEmpty()) {
                    connection.idleAtNanos = now - this.keepAliveDurationNs;
                    return 0;
                }
            }
        }
        int i2 = references.size();
        return i2;
    }
}

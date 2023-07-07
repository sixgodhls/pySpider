package io.reactivex.internal.operators.flowable;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.flowables.ConnectableFlowable;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.disposables.ResettableConnectable;
import io.reactivex.internal.disposables.SequentialDisposable;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/* loaded from: classes.dex */
public final class FlowableRefCount<T> extends Flowable<T> {
    RefConnection connection;

    /* renamed from: n */
    final int f126n;
    final Scheduler scheduler;
    final ConnectableFlowable<T> source;
    final long timeout;
    final TimeUnit unit;

    public FlowableRefCount(ConnectableFlowable<T> source) {
        this(source, 1, 0L, TimeUnit.NANOSECONDS, Schedulers.trampoline());
    }

    public FlowableRefCount(ConnectableFlowable<T> source, int n, long timeout, TimeUnit unit, Scheduler scheduler) {
        this.source = source;
        this.f126n = n;
        this.timeout = timeout;
        this.unit = unit;
        this.scheduler = scheduler;
    }

    @Override // io.reactivex.Flowable
    protected void subscribeActual(Subscriber<? super T> s) {
        RefConnection conn;
        boolean connect = false;
        synchronized (this) {
            conn = this.connection;
            if (conn == null) {
                conn = new RefConnection(this);
                this.connection = conn;
            }
            long c = conn.subscriberCount;
            if (c == 0 && conn.timer != null) {
                conn.timer.dispose();
            }
            conn.subscriberCount = c + 1;
            if (!conn.connected && 1 + c == this.f126n) {
                connect = true;
                conn.connected = true;
            }
        }
        this.source.subscribe((FlowableSubscriber) new RefCountSubscriber(s, this, conn));
        if (connect) {
            this.source.connect(conn);
        }
    }

    void cancel(RefConnection rc) {
        synchronized (this) {
            if (this.connection != null && this.connection == rc) {
                long c = rc.subscriberCount - 1;
                rc.subscriberCount = c;
                if (c == 0 && rc.connected) {
                    if (this.timeout == 0) {
                        timeout(rc);
                        return;
                    }
                    SequentialDisposable sd = new SequentialDisposable();
                    rc.timer = sd;
                    sd.replace(this.scheduler.scheduleDirect(rc, this.timeout, this.unit));
                }
            }
        }
    }

    void terminated(RefConnection rc) {
        synchronized (this) {
            if (this.connection != null && this.connection == rc) {
                this.connection = null;
                if (rc.timer != null) {
                    rc.timer.dispose();
                }
            }
            long j = rc.subscriberCount - 1;
            rc.subscriberCount = j;
            if (j == 0) {
                if (this.source instanceof Disposable) {
                    ((Disposable) this.source).dispose();
                } else if (this.source instanceof ResettableConnectable) {
                    ((ResettableConnectable) this.source).resetIf(rc.get());
                }
            }
        }
    }

    void timeout(RefConnection rc) {
        synchronized (this) {
            if (rc.subscriberCount == 0 && rc == this.connection) {
                this.connection = null;
                Disposable connectionObject = rc.get();
                DisposableHelper.dispose(rc);
                if (this.source instanceof Disposable) {
                    ((Disposable) this.source).dispose();
                } else if (this.source instanceof ResettableConnectable) {
                    ((ResettableConnectable) this.source).resetIf(connectionObject);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class RefConnection extends AtomicReference<Disposable> implements Runnable, Consumer<Disposable> {
        private static final long serialVersionUID = -4552101107598366241L;
        boolean connected;
        final FlowableRefCount<?> parent;
        long subscriberCount;
        Disposable timer;

        RefConnection(FlowableRefCount<?> parent) {
            this.parent = parent;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.parent.timeout(this);
        }

        @Override // io.reactivex.functions.Consumer
        public void accept(Disposable t) throws Exception {
            DisposableHelper.replace(this, t);
        }
    }

    /* loaded from: classes.dex */
    static final class RefCountSubscriber<T> extends AtomicBoolean implements FlowableSubscriber<T>, Subscription {
        private static final long serialVersionUID = -7419642935409022375L;
        final RefConnection connection;
        final Subscriber<? super T> downstream;
        final FlowableRefCount<T> parent;
        Subscription upstream;

        RefCountSubscriber(Subscriber<? super T> actual, FlowableRefCount<T> parent, RefConnection connection) {
            this.downstream = actual;
            this.parent = parent;
            this.connection = connection;
        }

        @Override // org.reactivestreams.Subscriber
        public void onNext(T t) {
            this.downstream.onNext(t);
        }

        @Override // org.reactivestreams.Subscriber
        public void onError(Throwable t) {
            if (compareAndSet(false, true)) {
                this.parent.terminated(this.connection);
                this.downstream.onError(t);
                return;
            }
            RxJavaPlugins.onError(t);
        }

        @Override // org.reactivestreams.Subscriber
        public void onComplete() {
            if (compareAndSet(false, true)) {
                this.parent.terminated(this.connection);
                this.downstream.onComplete();
            }
        }

        @Override // org.reactivestreams.Subscription
        public void request(long n) {
            this.upstream.request(n);
        }

        @Override // org.reactivestreams.Subscription
        public void cancel() {
            this.upstream.cancel();
            if (compareAndSet(false, true)) {
                this.parent.cancel(this.connection);
            }
        }

        @Override // io.reactivex.FlowableSubscriber, org.reactivestreams.Subscriber
        public void onSubscribe(Subscription s) {
            if (SubscriptionHelper.validate(this.upstream, s)) {
                this.upstream = s;
                this.downstream.onSubscribe(this);
            }
        }
    }
}
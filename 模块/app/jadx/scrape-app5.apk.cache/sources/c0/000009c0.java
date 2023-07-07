package io.reactivex.internal.operators.observable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.observers.QueueDrainObserver;
import io.reactivex.internal.queue.MpscLinkedQueue;
import io.reactivex.internal.util.NotificationLite;
import io.reactivex.observers.SerializedObserver;
import io.reactivex.subjects.UnicastSubject;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: classes.dex */
public final class ObservableWindowTimed<T> extends AbstractObservableWithUpstream<T, Observable<T>> {
    final int bufferSize;
    final long maxSize;
    final boolean restartTimerOnMaxSize;
    final Scheduler scheduler;
    final long timeskip;
    final long timespan;
    final TimeUnit unit;

    public ObservableWindowTimed(ObservableSource<T> source, long timespan, long timeskip, TimeUnit unit, Scheduler scheduler, long maxSize, int bufferSize, boolean restartTimerOnMaxSize) {
        super(source);
        this.timespan = timespan;
        this.timeskip = timeskip;
        this.unit = unit;
        this.scheduler = scheduler;
        this.maxSize = maxSize;
        this.bufferSize = bufferSize;
        this.restartTimerOnMaxSize = restartTimerOnMaxSize;
    }

    @Override // io.reactivex.Observable
    public void subscribeActual(Observer<? super Observable<T>> t) {
        SerializedObserver<Observable<T>> actual = new SerializedObserver<>(t);
        if (this.timespan == this.timeskip) {
            if (this.maxSize == Long.MAX_VALUE) {
                this.source.subscribe(new WindowExactUnboundedObserver(actual, this.timespan, this.unit, this.scheduler, this.bufferSize));
                return;
            } else {
                this.source.subscribe(new WindowExactBoundedObserver(actual, this.timespan, this.unit, this.scheduler, this.bufferSize, this.maxSize, this.restartTimerOnMaxSize));
                return;
            }
        }
        this.source.subscribe(new WindowSkipObserver(actual, this.timespan, this.timeskip, this.unit, this.scheduler.createWorker(), this.bufferSize));
    }

    /* loaded from: classes.dex */
    static final class WindowExactUnboundedObserver<T> extends QueueDrainObserver<T, Object, Observable<T>> implements Observer<T>, Disposable, Runnable {
        static final Object NEXT = new Object();
        final int bufferSize;
        final Scheduler scheduler;
        volatile boolean terminated;
        final AtomicReference<Disposable> timer = new AtomicReference<>();
        final long timespan;
        final TimeUnit unit;
        Disposable upstream;
        UnicastSubject<T> window;

        WindowExactUnboundedObserver(Observer<? super Observable<T>> actual, long timespan, TimeUnit unit, Scheduler scheduler, int bufferSize) {
            super(actual, new MpscLinkedQueue());
            this.timespan = timespan;
            this.unit = unit;
            this.scheduler = scheduler;
            this.bufferSize = bufferSize;
        }

        @Override // io.reactivex.Observer
        public void onSubscribe(Disposable d) {
            if (DisposableHelper.validate(this.upstream, d)) {
                this.upstream = d;
                this.window = UnicastSubject.create(this.bufferSize);
                Observer<? super V> observer = this.downstream;
                observer.onSubscribe(this);
                observer.onNext(this.window);
                if (!this.cancelled) {
                    Scheduler scheduler = this.scheduler;
                    long j = this.timespan;
                    Disposable task = scheduler.schedulePeriodicallyDirect(this, j, j, this.unit);
                    DisposableHelper.replace(this.timer, task);
                }
            }
        }

        @Override // io.reactivex.Observer
        public void onNext(T t) {
            if (this.terminated) {
                return;
            }
            if (fastEnter()) {
                this.window.onNext(t);
                if (leave(-1) == 0) {
                    return;
                }
            } else {
                this.queue.offer(NotificationLite.next(t));
                if (!enter()) {
                    return;
                }
            }
            drainLoop();
        }

        @Override // io.reactivex.Observer
        public void onError(Throwable t) {
            this.error = t;
            this.done = true;
            if (enter()) {
                drainLoop();
            }
            disposeTimer();
            this.downstream.onError(t);
        }

        @Override // io.reactivex.Observer
        public void onComplete() {
            this.done = true;
            if (enter()) {
                drainLoop();
            }
            disposeTimer();
            this.downstream.onComplete();
        }

        @Override // io.reactivex.disposables.Disposable
        public void dispose() {
            this.cancelled = true;
        }

        @Override // io.reactivex.disposables.Disposable
        public boolean isDisposed() {
            return this.cancelled;
        }

        void disposeTimer() {
            DisposableHelper.dispose(this.timer);
        }

        @Override // java.lang.Runnable
        public void run() {
            if (this.cancelled) {
                this.terminated = true;
                disposeTimer();
            }
            this.queue.offer(NEXT);
            if (enter()) {
                drainLoop();
            }
        }

        /* JADX WARN: Code restructure failed: missing block: B:10:0x0026, code lost:
            r2 = r2;
            r2.onError(r7);
         */
        /* JADX WARN: Code restructure failed: missing block: B:11:?, code lost:
            return;
         */
        /* JADX WARN: Code restructure failed: missing block: B:13:0x002a, code lost:
            r2 = r2;
            r2.onComplete();
         */
        /* JADX WARN: Code restructure failed: missing block: B:14:0x002d, code lost:
            return;
         */
        /* JADX WARN: Code restructure failed: missing block: B:8:0x0019, code lost:
            r8.window = null;
            r0.clear();
            disposeTimer();
            r7 = r8.error;
         */
        /* JADX WARN: Code restructure failed: missing block: B:9:0x0024, code lost:
            if (r7 == null) goto L13;
         */
        /* JADX WARN: Multi-variable type inference failed */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        void drainLoop() {
            /*
                r8 = this;
                io.reactivex.internal.fuseable.SimplePlainQueue<U> r0 = r8.queue
                io.reactivex.internal.queue.MpscLinkedQueue r0 = (io.reactivex.internal.queue.MpscLinkedQueue) r0
                io.reactivex.Observer<? super V> r1 = r8.downstream
                io.reactivex.subjects.UnicastSubject<T> r2 = r8.window
                r3 = 1
            L9:
                boolean r4 = r8.terminated
                boolean r5 = r8.done
                java.lang.Object r6 = r0.mo399poll()
                if (r5 == 0) goto L2e
                if (r6 == 0) goto L19
                java.lang.Object r7 = io.reactivex.internal.operators.observable.ObservableWindowTimed.WindowExactUnboundedObserver.NEXT
                if (r6 != r7) goto L2e
            L19:
                r7 = 0
                r8.window = r7
                r0.clear()
                r8.disposeTimer()
                java.lang.Throwable r7 = r8.error
                if (r7 == 0) goto L2a
                r2.onError(r7)
                goto L2d
            L2a:
                r2.onComplete()
            L2d:
                return
            L2e:
                if (r6 != 0) goto L3a
            L31:
                int r4 = -r3
                int r3 = r8.leave(r4)
                if (r3 != 0) goto L9
            L39:
                return
            L3a:
                java.lang.Object r7 = io.reactivex.internal.operators.observable.ObservableWindowTimed.WindowExactUnboundedObserver.NEXT
                if (r6 != r7) goto L55
                r2.onComplete()
                if (r4 != 0) goto L4f
                int r7 = r8.bufferSize
                io.reactivex.subjects.UnicastSubject r2 = io.reactivex.subjects.UnicastSubject.create(r7)
                r8.window = r2
                r1.onNext(r2)
                goto L9
            L4f:
                io.reactivex.disposables.Disposable r7 = r8.upstream
                r7.dispose()
                goto L9
            L55:
                java.lang.Object r7 = io.reactivex.internal.util.NotificationLite.getValue(r6)
                r2.onNext(r7)
                goto L9
            */
            throw new UnsupportedOperationException("Method not decompiled: io.reactivex.internal.operators.observable.ObservableWindowTimed.WindowExactUnboundedObserver.drainLoop():void");
        }
    }

    /* loaded from: classes.dex */
    static final class WindowExactBoundedObserver<T> extends QueueDrainObserver<T, Object, Observable<T>> implements Disposable {
        final int bufferSize;
        long count;
        final long maxSize;
        long producerIndex;
        final boolean restartTimerOnMaxSize;
        final Scheduler scheduler;
        volatile boolean terminated;
        final AtomicReference<Disposable> timer = new AtomicReference<>();
        final long timespan;
        final TimeUnit unit;
        Disposable upstream;
        UnicastSubject<T> window;
        final Scheduler.Worker worker;

        WindowExactBoundedObserver(Observer<? super Observable<T>> actual, long timespan, TimeUnit unit, Scheduler scheduler, int bufferSize, long maxSize, boolean restartTimerOnMaxSize) {
            super(actual, new MpscLinkedQueue());
            this.timespan = timespan;
            this.unit = unit;
            this.scheduler = scheduler;
            this.bufferSize = bufferSize;
            this.maxSize = maxSize;
            this.restartTimerOnMaxSize = restartTimerOnMaxSize;
            if (restartTimerOnMaxSize) {
                this.worker = scheduler.createWorker();
            } else {
                this.worker = null;
            }
        }

        @Override // io.reactivex.Observer
        public void onSubscribe(Disposable d) {
            Disposable task;
            if (DisposableHelper.validate(this.upstream, d)) {
                this.upstream = d;
                Observer<? super V> observer = this.downstream;
                observer.onSubscribe(this);
                if (this.cancelled) {
                    return;
                }
                UnicastSubject<T> w = UnicastSubject.create(this.bufferSize);
                this.window = w;
                observer.onNext(w);
                ConsumerIndexHolder consumerIndexHolder = new ConsumerIndexHolder(this.producerIndex, this);
                if (this.restartTimerOnMaxSize) {
                    Scheduler.Worker worker = this.worker;
                    long j = this.timespan;
                    task = worker.schedulePeriodically(consumerIndexHolder, j, j, this.unit);
                } else {
                    Scheduler scheduler = this.scheduler;
                    long j2 = this.timespan;
                    task = scheduler.schedulePeriodicallyDirect(consumerIndexHolder, j2, j2, this.unit);
                }
                DisposableHelper.replace(this.timer, task);
            }
        }

        @Override // io.reactivex.Observer
        public void onNext(T t) {
            if (this.terminated) {
                return;
            }
            if (fastEnter()) {
                UnicastSubject<T> w = this.window;
                w.onNext(t);
                long c = this.count + 1;
                if (c >= this.maxSize) {
                    this.producerIndex++;
                    this.count = 0L;
                    w.onComplete();
                    UnicastSubject<T> w2 = UnicastSubject.create(this.bufferSize);
                    this.window = w2;
                    this.downstream.onNext(w2);
                    if (this.restartTimerOnMaxSize) {
                        Disposable tm = this.timer.get();
                        tm.dispose();
                        Scheduler.Worker worker = this.worker;
                        ConsumerIndexHolder consumerIndexHolder = new ConsumerIndexHolder(this.producerIndex, this);
                        long j = this.timespan;
                        Disposable task = worker.schedulePeriodically(consumerIndexHolder, j, j, this.unit);
                        DisposableHelper.replace(this.timer, task);
                    }
                } else {
                    this.count = c;
                }
                if (leave(-1) == 0) {
                    return;
                }
            } else {
                this.queue.offer(NotificationLite.next(t));
                if (!enter()) {
                    return;
                }
            }
            drainLoop();
        }

        @Override // io.reactivex.Observer
        public void onError(Throwable t) {
            this.error = t;
            this.done = true;
            if (enter()) {
                drainLoop();
            }
            this.downstream.onError(t);
            disposeTimer();
        }

        @Override // io.reactivex.Observer
        public void onComplete() {
            this.done = true;
            if (enter()) {
                drainLoop();
            }
            this.downstream.onComplete();
            disposeTimer();
        }

        @Override // io.reactivex.disposables.Disposable
        public void dispose() {
            this.cancelled = true;
        }

        @Override // io.reactivex.disposables.Disposable
        public boolean isDisposed() {
            return this.cancelled;
        }

        void disposeTimer() {
            DisposableHelper.dispose(this.timer);
            Scheduler.Worker w = this.worker;
            if (w != null) {
                w.dispose();
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        void drainLoop() {
            MpscLinkedQueue<Object> q;
            Observer observer;
            MpscLinkedQueue<Object> q2 = (MpscLinkedQueue) this.queue;
            Observer observer2 = this.downstream;
            UnicastSubject<T> unicastSubject = this.window;
            int missed = 1;
            while (!this.terminated) {
                boolean d = this.done;
                Object o = q2.mo399poll();
                boolean empty = o == null;
                boolean isHolder = o instanceof ConsumerIndexHolder;
                if (d && (empty || isHolder)) {
                    this.window = null;
                    q2.clear();
                    disposeTimer();
                    Throwable err = this.error;
                    if (err != null) {
                        UnicastSubject<T> w = unicastSubject;
                        w.onError(err);
                        return;
                    }
                    UnicastSubject<T> w2 = unicastSubject;
                    w2.onComplete();
                    return;
                } else if (empty) {
                    missed = leave(-missed);
                    if (missed == 0) {
                        return;
                    }
                } else if (isHolder) {
                    ConsumerIndexHolder consumerIndexHolder = (ConsumerIndexHolder) o;
                    if (this.restartTimerOnMaxSize || this.producerIndex == consumerIndexHolder.index) {
                        UnicastSubject<T> w3 = unicastSubject;
                        w3.onComplete();
                        this.count = 0L;
                        UnicastSubject<T> w4 = UnicastSubject.create(this.bufferSize);
                        unicastSubject = w4;
                        this.window = unicastSubject;
                        observer2.onNext(unicastSubject);
                    }
                } else {
                    UnicastSubject<T> w5 = unicastSubject;
                    w5.onNext(NotificationLite.getValue(o));
                    long c = this.count + 1;
                    if (c >= this.maxSize) {
                        this.producerIndex++;
                        this.count = 0L;
                        UnicastSubject<T> w6 = unicastSubject;
                        w6.onComplete();
                        UnicastSubject<T> w7 = UnicastSubject.create(this.bufferSize);
                        unicastSubject = w7;
                        this.window = unicastSubject;
                        this.downstream.onNext(unicastSubject);
                        if (!this.restartTimerOnMaxSize) {
                            q = q2;
                            observer = observer2;
                        } else {
                            Disposable tm = this.timer.get();
                            tm.dispose();
                            Scheduler.Worker worker = this.worker;
                            q = q2;
                            observer = observer2;
                            ConsumerIndexHolder consumerIndexHolder2 = new ConsumerIndexHolder(this.producerIndex, this);
                            long j = this.timespan;
                            Disposable task = worker.schedulePeriodically(consumerIndexHolder2, j, j, this.unit);
                            if (!this.timer.compareAndSet(tm, task)) {
                                task.dispose();
                            }
                        }
                    } else {
                        q = q2;
                        observer = observer2;
                        this.count = c;
                    }
                    q2 = q;
                    observer2 = observer;
                }
            }
            this.upstream.dispose();
            q2.clear();
            disposeTimer();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public static final class ConsumerIndexHolder implements Runnable {
            final long index;
            final WindowExactBoundedObserver<?> parent;

            ConsumerIndexHolder(long index, WindowExactBoundedObserver<?> parent) {
                this.index = index;
                this.parent = parent;
            }

            @Override // java.lang.Runnable
            public void run() {
                WindowExactBoundedObserver<?> p = this.parent;
                if (!((WindowExactBoundedObserver) p).cancelled) {
                    ((WindowExactBoundedObserver) p).queue.offer(this);
                } else {
                    p.terminated = true;
                    p.disposeTimer();
                }
                if (p.enter()) {
                    p.drainLoop();
                }
            }
        }
    }

    /* loaded from: classes.dex */
    static final class WindowSkipObserver<T> extends QueueDrainObserver<T, Object, Observable<T>> implements Disposable, Runnable {
        final int bufferSize;
        volatile boolean terminated;
        final long timeskip;
        final long timespan;
        final TimeUnit unit;
        Disposable upstream;
        final List<UnicastSubject<T>> windows = new LinkedList();
        final Scheduler.Worker worker;

        WindowSkipObserver(Observer<? super Observable<T>> actual, long timespan, long timeskip, TimeUnit unit, Scheduler.Worker worker, int bufferSize) {
            super(actual, new MpscLinkedQueue());
            this.timespan = timespan;
            this.timeskip = timeskip;
            this.unit = unit;
            this.worker = worker;
            this.bufferSize = bufferSize;
        }

        @Override // io.reactivex.Observer
        public void onSubscribe(Disposable d) {
            if (DisposableHelper.validate(this.upstream, d)) {
                this.upstream = d;
                this.downstream.onSubscribe(this);
                if (this.cancelled) {
                    return;
                }
                UnicastSubject<T> w = UnicastSubject.create(this.bufferSize);
                this.windows.add(w);
                this.downstream.onNext(w);
                this.worker.schedule(new CompletionTask(w), this.timespan, this.unit);
                Scheduler.Worker worker = this.worker;
                long j = this.timeskip;
                worker.schedulePeriodically(this, j, j, this.unit);
            }
        }

        @Override // io.reactivex.Observer
        public void onNext(T t) {
            if (fastEnter()) {
                for (UnicastSubject<T> w : this.windows) {
                    w.onNext(t);
                }
                if (leave(-1) == 0) {
                    return;
                }
            } else {
                this.queue.offer(t);
                if (!enter()) {
                    return;
                }
            }
            drainLoop();
        }

        @Override // io.reactivex.Observer
        public void onError(Throwable t) {
            this.error = t;
            this.done = true;
            if (enter()) {
                drainLoop();
            }
            this.downstream.onError(t);
            disposeWorker();
        }

        @Override // io.reactivex.Observer
        public void onComplete() {
            this.done = true;
            if (enter()) {
                drainLoop();
            }
            this.downstream.onComplete();
            disposeWorker();
        }

        @Override // io.reactivex.disposables.Disposable
        public void dispose() {
            this.cancelled = true;
        }

        @Override // io.reactivex.disposables.Disposable
        public boolean isDisposed() {
            return this.cancelled;
        }

        void disposeWorker() {
            this.worker.dispose();
        }

        void complete(UnicastSubject<T> w) {
            this.queue.offer(new SubjectWork(w, false));
            if (enter()) {
                drainLoop();
            }
        }

        void drainLoop() {
            MpscLinkedQueue<Object> q = (MpscLinkedQueue) this.queue;
            Observer<? super V> observer = this.downstream;
            List<UnicastSubject<T>> ws = this.windows;
            int missed = 1;
            while (!this.terminated) {
                boolean d = this.done;
                T t = (T) q.mo399poll();
                boolean empty = t == null;
                boolean sw = t instanceof SubjectWork;
                if (d && (empty || sw)) {
                    q.clear();
                    Throwable e = this.error;
                    if (e != null) {
                        for (UnicastSubject<T> w : ws) {
                            w.onError(e);
                        }
                    } else {
                        for (UnicastSubject<T> w2 : ws) {
                            w2.onComplete();
                        }
                    }
                    disposeWorker();
                    ws.clear();
                    return;
                } else if (!empty) {
                    if (sw) {
                        SubjectWork<T> work = (SubjectWork) t;
                        if (work.open) {
                            if (!this.cancelled) {
                                UnicastSubject<T> w3 = UnicastSubject.create(this.bufferSize);
                                ws.add(w3);
                                observer.onNext(w3);
                                this.worker.schedule(new CompletionTask(w3), this.timespan, this.unit);
                            }
                        } else {
                            ws.remove(work.f168w);
                            work.f168w.onComplete();
                            if (ws.isEmpty() && this.cancelled) {
                                this.terminated = true;
                            }
                        }
                    } else {
                        for (UnicastSubject<T> w4 : ws) {
                            w4.onNext(t);
                        }
                    }
                } else {
                    missed = leave(-missed);
                    if (missed == 0) {
                        return;
                    }
                }
            }
            this.upstream.dispose();
            disposeWorker();
            q.clear();
            ws.clear();
        }

        @Override // java.lang.Runnable
        public void run() {
            UnicastSubject<T> w = UnicastSubject.create(this.bufferSize);
            SubjectWork<T> sw = new SubjectWork<>(w, true);
            if (!this.cancelled) {
                this.queue.offer(sw);
            }
            if (enter()) {
                drainLoop();
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public static final class SubjectWork<T> {
            final boolean open;

            /* renamed from: w */
            final UnicastSubject<T> f168w;

            SubjectWork(UnicastSubject<T> w, boolean open) {
                this.f168w = w;
                this.open = open;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public final class CompletionTask implements Runnable {

            /* renamed from: w */
            private final UnicastSubject<T> f167w;

            CompletionTask(UnicastSubject<T> w) {
                this.f167w = w;
            }

            @Override // java.lang.Runnable
            public void run() {
                WindowSkipObserver.this.complete(this.f167w);
            }
        }
    }
}
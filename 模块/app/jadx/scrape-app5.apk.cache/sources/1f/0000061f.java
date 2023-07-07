package io.reactivex.android.schedulers;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.TimeUnit;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class HandlerScheduler extends Scheduler {
    private final boolean async;
    private final Handler handler;

    /* JADX INFO: Access modifiers changed from: package-private */
    public HandlerScheduler(Handler handler, boolean async) {
        this.handler = handler;
        this.async = async;
    }

    @Override // io.reactivex.Scheduler
    public Disposable scheduleDirect(Runnable run, long delay, TimeUnit unit) {
        if (run == null) {
            throw new NullPointerException("run == null");
        }
        if (unit == null) {
            throw new NullPointerException("unit == null");
        }
        ScheduledRunnable scheduled = new ScheduledRunnable(this.handler, RxJavaPlugins.onSchedule(run));
        this.handler.postDelayed(scheduled, unit.toMillis(delay));
        return scheduled;
    }

    @Override // io.reactivex.Scheduler
    public Scheduler.Worker createWorker() {
        return new HandlerWorker(this.handler, this.async);
    }

    /* loaded from: classes.dex */
    private static final class HandlerWorker extends Scheduler.Worker {
        private final boolean async;
        private volatile boolean disposed;
        private final Handler handler;

        HandlerWorker(Handler handler, boolean async) {
            this.handler = handler;
            this.async = async;
        }

        @Override // io.reactivex.Scheduler.Worker
        @SuppressLint({"NewApi"})
        public Disposable schedule(Runnable run, long delay, TimeUnit unit) {
            if (run == null) {
                throw new NullPointerException("run == null");
            }
            if (unit == null) {
                throw new NullPointerException("unit == null");
            }
            if (this.disposed) {
                return Disposables.disposed();
            }
            ScheduledRunnable scheduled = new ScheduledRunnable(this.handler, RxJavaPlugins.onSchedule(run));
            Message message = Message.obtain(this.handler, scheduled);
            message.obj = this;
            if (this.async) {
                message.setAsynchronous(true);
            }
            this.handler.sendMessageDelayed(message, unit.toMillis(delay));
            if (this.disposed) {
                this.handler.removeCallbacks(scheduled);
                return Disposables.disposed();
            }
            return scheduled;
        }

        @Override // io.reactivex.disposables.Disposable
        public void dispose() {
            this.disposed = true;
            this.handler.removeCallbacksAndMessages(this);
        }

        @Override // io.reactivex.disposables.Disposable
        public boolean isDisposed() {
            return this.disposed;
        }
    }

    /* loaded from: classes.dex */
    private static final class ScheduledRunnable implements Runnable, Disposable {
        private final Runnable delegate;
        private volatile boolean disposed;
        private final Handler handler;

        ScheduledRunnable(Handler handler, Runnable delegate) {
            this.handler = handler;
            this.delegate = delegate;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                this.delegate.run();
            } catch (Throwable t) {
                RxJavaPlugins.onError(t);
            }
        }

        @Override // io.reactivex.disposables.Disposable
        public void dispose() {
            this.handler.removeCallbacks(this);
            this.disposed = true;
        }

        @Override // io.reactivex.disposables.Disposable
        public boolean isDisposed() {
            return this.disposed;
        }
    }
}
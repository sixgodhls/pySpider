package io.reactivex.internal.subscribers;

import io.reactivex.FlowableSubscriber;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.BackpressureHelper;
import java.util.concurrent.atomic.AtomicLong;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/* loaded from: classes.dex */
public abstract class SinglePostCompleteSubscriber<T, R> extends AtomicLong implements FlowableSubscriber<T>, Subscription {
    static final long COMPLETE_MASK = Long.MIN_VALUE;
    static final long REQUEST_MASK = Long.MAX_VALUE;
    private static final long serialVersionUID = 7917814472626990048L;
    protected final Subscriber<? super R> downstream;
    protected long produced;
    protected Subscription upstream;
    protected R value;

    public SinglePostCompleteSubscriber(Subscriber<? super R> downstream) {
        this.downstream = downstream;
    }

    @Override // io.reactivex.FlowableSubscriber, org.reactivestreams.Subscriber
    public void onSubscribe(Subscription s) {
        if (SubscriptionHelper.validate(this.upstream, s)) {
            this.upstream = s;
            this.downstream.onSubscribe(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void complete(R n) {
        long p = this.produced;
        if (p != 0) {
            BackpressureHelper.produced(this, p);
        }
        while (true) {
            long r = get();
            if ((r & COMPLETE_MASK) == 0) {
                if ((REQUEST_MASK & r) != 0) {
                    lazySet(-9223372036854775807L);
                    this.downstream.onNext(n);
                    this.downstream.onComplete();
                    return;
                }
                this.value = n;
                if (compareAndSet(0L, COMPLETE_MASK)) {
                    return;
                }
                this.value = null;
            } else {
                onDrop(n);
                return;
            }
        }
    }

    protected void onDrop(R n) {
    }

    @Override // org.reactivestreams.Subscription
    public final void request(long n) {
        long r;
        long u;
        if (SubscriptionHelper.validate(n)) {
            do {
                r = get();
                if ((r & COMPLETE_MASK) != 0) {
                    if (compareAndSet(COMPLETE_MASK, -9223372036854775807L)) {
                        this.downstream.onNext((R) this.value);
                        this.downstream.onComplete();
                        return;
                    }
                    return;
                }
                u = BackpressureHelper.addCap(r, n);
            } while (!compareAndSet(r, u));
            this.upstream.request(n);
        }
    }

    public void cancel() {
        this.upstream.cancel();
    }
}

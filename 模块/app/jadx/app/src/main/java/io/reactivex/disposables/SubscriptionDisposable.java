package io.reactivex.disposables;

import io.reactivex.annotations.NonNull;
import org.reactivestreams.Subscription;

/* loaded from: classes.dex */
final class SubscriptionDisposable extends ReferenceDisposable<Subscription> {
    private static final long serialVersionUID = -707001650852963139L;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SubscriptionDisposable(Subscription value) {
        super(value);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // io.reactivex.disposables.ReferenceDisposable
    public void onDisposed(@NonNull Subscription value) {
        value.cancel();
    }
}

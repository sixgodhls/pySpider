package com.jakewharton.rxbinding2.view;

import android.view.DragEvent;
import android.view.View;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;
import io.reactivex.functions.Predicate;

/* loaded from: classes.dex */
final class ViewDragObservable extends Observable<DragEvent> {
    private final Predicate<? super DragEvent> handled;
    private final View view;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ViewDragObservable(View view, Predicate<? super DragEvent> handled) {
        this.view = view;
        this.handled = handled;
    }

    @Override // io.reactivex.Observable
    protected void subscribeActual(Observer<? super DragEvent> observer) {
        if (!Preconditions.checkMainThread(observer)) {
            return;
        }
        Listener listener = new Listener(this.view, this.handled, observer);
        observer.onSubscribe(listener);
        this.view.setOnDragListener(listener);
    }

    /* loaded from: classes.dex */
    static final class Listener extends MainThreadDisposable implements View.OnDragListener {
        private final Predicate<? super DragEvent> handled;
        private final Observer<? super DragEvent> observer;
        private final View view;

        Listener(View view, Predicate<? super DragEvent> handled, Observer<? super DragEvent> observer) {
            this.view = view;
            this.handled = handled;
            this.observer = observer;
        }

        @Override // android.view.View.OnDragListener
        public boolean onDrag(View v, DragEvent event) {
            if (!isDisposed()) {
                try {
                    if (this.handled.test(event)) {
                        this.observer.onNext(event);
                        return true;
                    }
                    return false;
                } catch (Exception e) {
                    this.observer.onError(e);
                    dispose();
                    return false;
                }
            }
            return false;
        }

        @Override // io.reactivex.android.MainThreadDisposable
        protected void onDispose() {
            this.view.setOnDragListener(null);
        }
    }
}
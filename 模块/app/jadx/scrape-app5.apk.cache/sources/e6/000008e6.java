package com.jakewharton.rxbinding2.widget;

import android.view.View;
import android.widget.AdapterView;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;
import java.util.concurrent.Callable;

/* loaded from: classes.dex */
final class AdapterViewItemLongClickObservable extends Observable<Integer> {
    private final Callable<Boolean> handled;
    private final AdapterView<?> view;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AdapterViewItemLongClickObservable(AdapterView<?> view, Callable<Boolean> handled) {
        this.view = view;
        this.handled = handled;
    }

    @Override // io.reactivex.Observable
    protected void subscribeActual(Observer<? super Integer> observer) {
        if (!Preconditions.checkMainThread(observer)) {
            return;
        }
        Listener listener = new Listener(this.view, observer, this.handled);
        observer.onSubscribe(listener);
        this.view.setOnItemLongClickListener(listener);
    }

    /* loaded from: classes.dex */
    static final class Listener extends MainThreadDisposable implements AdapterView.OnItemLongClickListener {
        private final Callable<Boolean> handled;
        private final Observer<? super Integer> observer;
        private final AdapterView<?> view;

        Listener(AdapterView<?> view, Observer<? super Integer> observer, Callable<Boolean> handled) {
            this.view = view;
            this.observer = observer;
            this.handled = handled;
        }

        @Override // android.widget.AdapterView.OnItemLongClickListener
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            if (!isDisposed()) {
                try {
                    if (this.handled.call().booleanValue()) {
                        this.observer.onNext(Integer.valueOf(position));
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
            this.view.setOnItemLongClickListener(null);
        }
    }
}
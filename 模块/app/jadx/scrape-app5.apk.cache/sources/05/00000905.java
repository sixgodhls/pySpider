package com.jakewharton.rxbinding2.widget;

import android.view.View;
import android.widget.AdapterView;
import com.jakewharton.rxbinding2.internal.Preconditions;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;
import io.reactivex.functions.Predicate;

/* loaded from: classes.dex */
final class AdapterViewItemLongClickEventObservable extends Observable<AdapterViewItemLongClickEvent> {
    private final Predicate<? super AdapterViewItemLongClickEvent> handled;
    private final AdapterView<?> view;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AdapterViewItemLongClickEventObservable(AdapterView<?> view, Predicate<? super AdapterViewItemLongClickEvent> handled) {
        this.view = view;
        this.handled = handled;
    }

    @Override // io.reactivex.Observable
    protected void subscribeActual(Observer<? super AdapterViewItemLongClickEvent> observer) {
        if (!Preconditions.checkMainThread(observer)) {
            return;
        }
        Listener listener = new Listener(this.view, observer, this.handled);
        observer.onSubscribe(listener);
        this.view.setOnItemLongClickListener(listener);
    }

    /* loaded from: classes.dex */
    static final class Listener extends MainThreadDisposable implements AdapterView.OnItemLongClickListener {
        private final Predicate<? super AdapterViewItemLongClickEvent> handled;
        private final Observer<? super AdapterViewItemLongClickEvent> observer;
        private final AdapterView<?> view;

        Listener(AdapterView<?> view, Observer<? super AdapterViewItemLongClickEvent> observer, Predicate<? super AdapterViewItemLongClickEvent> handled) {
            this.view = view;
            this.observer = observer;
            this.handled = handled;
        }

        @Override // android.widget.AdapterView.OnItemLongClickListener
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            if (!isDisposed()) {
                AdapterViewItemLongClickEvent event = AdapterViewItemLongClickEvent.create(parent, view, position, id);
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
            this.view.setOnItemLongClickListener(null);
        }
    }
}
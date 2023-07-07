package com.jakewharton.rxbinding2.widget;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;
import com.jakewharton.rxbinding2.InitialValueObservable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

/* loaded from: classes.dex */
final class TextViewTextChangeEventObservable extends InitialValueObservable<TextViewTextChangeEvent> {
    private final TextView view;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TextViewTextChangeEventObservable(TextView view) {
        this.view = view;
    }

    @Override // com.jakewharton.rxbinding2.InitialValueObservable
    protected void subscribeListener(Observer<? super TextViewTextChangeEvent> observer) {
        Listener listener = new Listener(this.view, observer);
        observer.onSubscribe(listener);
        this.view.addTextChangedListener(listener);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.jakewharton.rxbinding2.InitialValueObservable
    /* renamed from: getInitialValue */
    public TextViewTextChangeEvent mo370getInitialValue() {
        TextView textView = this.view;
        return TextViewTextChangeEvent.create(textView, textView.getText(), 0, 0, 0);
    }

    /* loaded from: classes.dex */
    static final class Listener extends MainThreadDisposable implements TextWatcher {
        private final Observer<? super TextViewTextChangeEvent> observer;
        private final TextView view;

        Listener(TextView view, Observer<? super TextViewTextChangeEvent> observer) {
            this.view = view;
            this.observer = observer;
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!isDisposed()) {
                this.observer.onNext(TextViewTextChangeEvent.create(this.view, s, start, before, count));
            }
        }

        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable editable) {
        }

        @Override // io.reactivex.android.MainThreadDisposable
        protected void onDispose() {
            this.view.removeTextChangedListener(this);
        }
    }
}
package android.databinding;

import android.databinding.Observable;
import android.support.annotation.NonNull;

/* loaded from: classes.dex */
public class BaseObservable implements Observable {
    private transient PropertyChangeRegistry mCallbacks;

    @Override // android.databinding.Observable
    public void addOnPropertyChangedCallback(@NonNull Observable.OnPropertyChangedCallback callback) {
        synchronized (this) {
            if (this.mCallbacks == null) {
                this.mCallbacks = new PropertyChangeRegistry();
            }
        }
        this.mCallbacks.add(callback);
    }

    @Override // android.databinding.Observable
    public void removeOnPropertyChangedCallback(@NonNull Observable.OnPropertyChangedCallback callback) {
        synchronized (this) {
            if (this.mCallbacks == null) {
                return;
            }
            this.mCallbacks.remove(callback);
        }
    }

    public void notifyChange() {
        synchronized (this) {
            if (this.mCallbacks == null) {
                return;
            }
            this.mCallbacks.notifyCallbacks(this, 0, null);
        }
    }

    public void notifyPropertyChanged(int fieldId) {
        synchronized (this) {
            if (this.mCallbacks == null) {
                return;
            }
            this.mCallbacks.notifyCallbacks(this, fieldId, null);
        }
    }
}

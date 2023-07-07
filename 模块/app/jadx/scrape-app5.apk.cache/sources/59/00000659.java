package android.arch.lifecycle;

import android.arch.core.util.Function;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/* loaded from: classes.dex */
public class Transformations {
    private Transformations() {
    }

    @MainThread
    public static <X, Y> LiveData<Y> map(@NonNull LiveData<X> source, @NonNull final Function<X, Y> func) {
        final MediatorLiveData<Y> result = new MediatorLiveData<>();
        result.addSource(source, new Observer<X>() { // from class: android.arch.lifecycle.Transformations.1
            @Override // android.arch.lifecycle.Observer
            public void onChanged(@Nullable X x) {
                MediatorLiveData.this.setValue(func.apply(x));
            }
        });
        return result;
    }

    @MainThread
    public static <X, Y> LiveData<Y> switchMap(@NonNull LiveData<X> trigger, @NonNull final Function<X, LiveData<Y>> func) {
        final MediatorLiveData<Y> result = new MediatorLiveData<>();
        result.addSource(trigger, new Observer<X>() { // from class: android.arch.lifecycle.Transformations.2
            LiveData<Y> mSource;

            @Override // android.arch.lifecycle.Observer
            public void onChanged(@Nullable X x) {
                LiveData<Y> newLiveData = (LiveData) Function.this.apply(x);
                Object obj = this.mSource;
                if (obj == newLiveData) {
                    return;
                }
                if (obj != null) {
                    result.removeSource(obj);
                }
                this.mSource = newLiveData;
                Object obj2 = this.mSource;
                if (obj2 != null) {
                    result.addSource(obj2, new Observer<Y>() { // from class: android.arch.lifecycle.Transformations.2.1
                        @Override // android.arch.lifecycle.Observer
                        public void onChanged(@Nullable Y y) {
                            result.setValue(y);
                        }
                    });
                }
            }
        });
        return result;
    }
}
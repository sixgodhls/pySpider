package me.goldze.mvvmhabit.bus;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: classes.dex */
public class RxBus {
    private static volatile RxBus mDefaultInstance;
    private final Subject<Object> mBus = PublishSubject.create().toSerialized();
    private final Map<Class<?>, Object> mStickyEventMap = new ConcurrentHashMap();

    public static RxBus getDefault() {
        if (mDefaultInstance == null) {
            synchronized (RxBus.class) {
                if (mDefaultInstance == null) {
                    mDefaultInstance = new RxBus();
                }
            }
        }
        return mDefaultInstance;
    }

    public void post(Object event) {
        this.mBus.onNext(event);
    }

    public <T> Observable<T> toObservable(Class<T> eventType) {
        return (Observable<T>) this.mBus.ofType(eventType);
    }

    public boolean hasObservers() {
        return this.mBus.hasObservers();
    }

    public void reset() {
        mDefaultInstance = null;
    }

    public void postSticky(Object event) {
        synchronized (this.mStickyEventMap) {
            this.mStickyEventMap.put(event.getClass(), event);
        }
        post(event);
    }

    public <T> Observable<T> toObservableSticky(final Class<T> eventType) {
        synchronized (this.mStickyEventMap) {
            Observable<T> observable = (Observable<T>) this.mBus.ofType(eventType);
            final Object event = this.mStickyEventMap.get(eventType);
            if (event != null) {
                return Observable.merge(observable, Observable.create(new ObservableOnSubscribe<T>() { // from class: me.goldze.mvvmhabit.bus.RxBus.1
                    @Override // io.reactivex.ObservableOnSubscribe
                    public void subscribe(ObservableEmitter<T> emitter) throws Exception {
                        emitter.onNext(eventType.cast(event));
                    }
                }));
            }
            return observable;
        }
    }

    public <T> T getStickyEvent(Class<T> eventType) {
        T cast;
        synchronized (this.mStickyEventMap) {
            cast = eventType.cast(this.mStickyEventMap.get(eventType));
        }
        return cast;
    }

    public <T> T removeStickyEvent(Class<T> eventType) {
        T cast;
        synchronized (this.mStickyEventMap) {
            cast = eventType.cast(this.mStickyEventMap.remove(eventType));
        }
        return cast;
    }

    public void removeAllStickyEvents() {
        synchronized (this.mStickyEventMap) {
            this.mStickyEventMap.clear();
        }
    }
}

package android.support.v7.view.menu;

/* loaded from: classes.dex */
class BaseWrapper<T> {
    final T mWrappedObject;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BaseWrapper(T object) {
        if (object == null) {
            throw new IllegalArgumentException("Wrapped Object can not be null.");
        }
        this.mWrappedObject = object;
    }

    /* renamed from: getWrappedObject */
    public T mo156getWrappedObject() {
        return this.mWrappedObject;
    }
}

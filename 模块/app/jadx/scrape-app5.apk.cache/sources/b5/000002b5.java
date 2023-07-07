package com.jakewharton.rxbinding2.internal;

import android.support.annotation.RestrictTo;
import io.reactivex.functions.Predicate;
import java.util.concurrent.Callable;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
/* loaded from: classes.dex */
public final class Functions {
    private static final Always ALWAYS_TRUE = new Always(true);
    public static final Callable<Boolean> CALLABLE_ALWAYS_TRUE;
    public static final Predicate<Object> PREDICATE_ALWAYS_TRUE;

    static {
        Always always = ALWAYS_TRUE;
        CALLABLE_ALWAYS_TRUE = always;
        PREDICATE_ALWAYS_TRUE = always;
    }

    /* loaded from: classes.dex */
    private static final class Always implements Callable<Boolean>, Predicate<Object> {
        private final Boolean value;

        Always(Boolean value) {
            this.value = value;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.concurrent.Callable
        /* renamed from: call */
        public Boolean mo354call() {
            return this.value;
        }

        @Override // io.reactivex.functions.Predicate
        public boolean test(Object t) throws Exception {
            return this.value.booleanValue();
        }
    }

    private Functions() {
        throw new AssertionError("No instances.");
    }
}
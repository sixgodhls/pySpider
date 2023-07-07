package io.reactivex.exceptions;

import io.reactivex.annotations.NonNull;
import io.reactivex.internal.util.ExceptionHelper;

/* loaded from: classes.dex */
public final class Exceptions {
    private Exceptions() {
        throw new IllegalStateException("No instances!");
    }

    @NonNull
    public static RuntimeException propagate(@NonNull Throwable t) {
        throw ExceptionHelper.wrapOrThrow(t);
    }

    public static void throwIfFatal(@NonNull Throwable t) {
        if (t instanceof VirtualMachineError) {
            throw ((VirtualMachineError) t);
        }
        if (t instanceof ThreadDeath) {
            throw ((ThreadDeath) t);
        }
        if (t instanceof LinkageError) {
            throw ((LinkageError) t);
        }
    }
}

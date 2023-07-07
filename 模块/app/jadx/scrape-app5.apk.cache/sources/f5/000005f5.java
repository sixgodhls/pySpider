package retrofit2;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;
import retrofit2.CallAdapter;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class Platform {
    private static final Platform PLATFORM = findPlatform();

    Platform() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Platform get() {
        return PLATFORM;
    }

    private static Platform findPlatform() {
        try {
            Class.forName("android.os.Build");
            if (Build.VERSION.SDK_INT != 0) {
                return new Android();
            }
        } catch (ClassNotFoundException e) {
        }
        try {
            Class.forName("java.util.Optional");
            return new Java8();
        } catch (ClassNotFoundException e2) {
            return new Platform();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public Executor defaultCallbackExecutor() {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CallAdapter.Factory defaultCallAdapterFactory(@Nullable Executor callbackExecutor) {
        if (callbackExecutor != null) {
            return new ExecutorCallAdapterFactory(callbackExecutor);
        }
        return DefaultCallAdapterFactory.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isDefaultMethod(Method method) {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public Object invokeDefaultMethod(Method method, Class<?> declaringClass, Object object, @Nullable Object... args) throws Throwable {
        throw new UnsupportedOperationException();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @IgnoreJRERequirement
    /* loaded from: classes.dex */
    public static class Java8 extends Platform {
        Java8() {
        }

        @Override // retrofit2.Platform
        boolean isDefaultMethod(Method method) {
            return method.isDefault();
        }

        @Override // retrofit2.Platform
        Object invokeDefaultMethod(Method method, Class<?> declaringClass, Object object, @Nullable Object... args) throws Throwable {
            Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, Integer.TYPE);
            constructor.setAccessible(true);
            return constructor.newInstance(declaringClass, -1).unreflectSpecial(method, declaringClass).bindTo(object).invokeWithArguments(args);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class Android extends Platform {
        Android() {
        }

        @Override // retrofit2.Platform
        public Executor defaultCallbackExecutor() {
            return new MainThreadExecutor();
        }

        @Override // retrofit2.Platform
        CallAdapter.Factory defaultCallAdapterFactory(@Nullable Executor callbackExecutor) {
            if (callbackExecutor == null) {
                throw new AssertionError();
            }
            return new ExecutorCallAdapterFactory(callbackExecutor);
        }

        /* loaded from: classes.dex */
        static class MainThreadExecutor implements Executor {
            private final Handler handler = new Handler(Looper.getMainLooper());

            MainThreadExecutor() {
            }

            @Override // java.util.concurrent.Executor
            public void execute(Runnable r) {
                this.handler.post(r);
            }
        }
    }
}
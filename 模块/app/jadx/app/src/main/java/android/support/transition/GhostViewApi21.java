package android.support.transition;

import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RequiresApi(21)
/* loaded from: classes.dex */
class GhostViewApi21 implements GhostViewImpl {
    private static final String TAG = "GhostViewApi21";
    private static Method sAddGhostMethod;
    private static boolean sAddGhostMethodFetched;
    private static Class<?> sGhostViewClass;
    private static boolean sGhostViewClassFetched;
    private static Method sRemoveGhostMethod;
    private static boolean sRemoveGhostMethodFetched;
    private final View mGhostView;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static GhostViewImpl addGhost(View view, ViewGroup viewGroup, Matrix matrix) {
        fetchAddGhostMethod();
        Method method = sAddGhostMethod;
        if (method != null) {
            try {
                return new GhostViewApi21((View) method.invoke(null, view, viewGroup, matrix));
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e2) {
                throw new RuntimeException(e2.getCause());
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void removeGhost(View view) {
        fetchRemoveGhostMethod();
        Method method = sRemoveGhostMethod;
        if (method != null) {
            try {
                method.invoke(null, view);
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e2) {
                throw new RuntimeException(e2.getCause());
            }
        }
    }

    private GhostViewApi21(@NonNull View ghostView) {
        this.mGhostView = ghostView;
    }

    @Override // android.support.transition.GhostViewImpl
    public void setVisibility(int visibility) {
        this.mGhostView.setVisibility(visibility);
    }

    @Override // android.support.transition.GhostViewImpl
    public void reserveEndViewTransition(ViewGroup viewGroup, View view) {
    }

    private static void fetchGhostViewClass() {
        if (!sGhostViewClassFetched) {
            try {
                sGhostViewClass = Class.forName("android.view.GhostView");
            } catch (ClassNotFoundException e) {
                Log.i(TAG, "Failed to retrieve GhostView class", e);
            }
            sGhostViewClassFetched = true;
        }
    }

    private static void fetchAddGhostMethod() {
        if (!sAddGhostMethodFetched) {
            try {
                fetchGhostViewClass();
                sAddGhostMethod = sGhostViewClass.getDeclaredMethod("addGhost", View.class, ViewGroup.class, Matrix.class);
                sAddGhostMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                Log.i(TAG, "Failed to retrieve addGhost method", e);
            }
            sAddGhostMethodFetched = true;
        }
    }

    private static void fetchRemoveGhostMethod() {
        if (!sRemoveGhostMethodFetched) {
            try {
                fetchGhostViewClass();
                sRemoveGhostMethod = sGhostViewClass.getDeclaredMethod("removeGhost", View.class);
                sRemoveGhostMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                Log.i(TAG, "Failed to retrieve removeGhost method", e);
            }
            sRemoveGhostMethodFetched = true;
        }
    }
}

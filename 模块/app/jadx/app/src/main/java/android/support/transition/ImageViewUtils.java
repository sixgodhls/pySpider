package android.support.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Matrix;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
class ImageViewUtils {
    private static final String TAG = "ImageViewUtils";
    private static Method sAnimateTransformMethod;
    private static boolean sAnimateTransformMethodFetched;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void startAnimateTransform(ImageView view) {
        if (Build.VERSION.SDK_INT < 21) {
            ImageView.ScaleType scaleType = view.getScaleType();
            view.setTag(R.id.save_scale_type, scaleType);
            if (scaleType == ImageView.ScaleType.MATRIX) {
                view.setTag(R.id.save_image_matrix, view.getImageMatrix());
            } else {
                view.setScaleType(ImageView.ScaleType.MATRIX);
            }
            view.setImageMatrix(MatrixUtils.IDENTITY_MATRIX);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void animateTransform(ImageView view, Matrix matrix) {
        if (Build.VERSION.SDK_INT < 21) {
            view.setImageMatrix(matrix);
            return;
        }
        fetchAnimateTransformMethod();
        Method method = sAnimateTransformMethod;
        if (method == null) {
            return;
        }
        try {
            method.invoke(view, matrix);
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e2) {
            throw new RuntimeException(e2.getCause());
        }
    }

    private static void fetchAnimateTransformMethod() {
        if (!sAnimateTransformMethodFetched) {
            try {
                sAnimateTransformMethod = ImageView.class.getDeclaredMethod("animateTransform", Matrix.class);
                sAnimateTransformMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                Log.i(TAG, "Failed to retrieve animateTransform method", e);
            }
            sAnimateTransformMethodFetched = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void reserveEndAnimateTransform(final ImageView view, Animator animator) {
        if (Build.VERSION.SDK_INT < 21) {
            animator.addListener(new AnimatorListenerAdapter() { // from class: android.support.transition.ImageViewUtils.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    ImageView.ScaleType scaleType = (ImageView.ScaleType) view.getTag(R.id.save_scale_type);
                    view.setScaleType(scaleType);
                    view.setTag(R.id.save_scale_type, null);
                    if (scaleType == ImageView.ScaleType.MATRIX) {
                        ImageView imageView = view;
                        imageView.setImageMatrix((Matrix) imageView.getTag(R.id.save_image_matrix));
                        view.setTag(R.id.save_image_matrix, null);
                    }
                    animation.removeListener(this);
                }
            });
        }
    }

    private ImageViewUtils() {
    }
}

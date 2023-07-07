package me.zhanghai.android.materialprogressbar.internal;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.annotation.Size;
import android.util.Property;

/* loaded from: classes.dex */
class ObjectAnimatorCompatBase {
    private static final int NUM_POINTS = 201;

    private ObjectAnimatorCompatBase() {
    }

    public static ObjectAnimator ofArgb(Object target, String propertyName, int... values) {
        ObjectAnimator animator = ObjectAnimator.ofInt(target, propertyName, values);
        animator.setEvaluator(new ArgbEvaluator());
        return animator;
    }

    public static <T> ObjectAnimator ofArgb(T target, Property<T, Integer> property, int... values) {
        ObjectAnimator animator = ObjectAnimator.ofInt(target, property, values);
        animator.setEvaluator(new ArgbEvaluator());
        return animator;
    }

    public static ObjectAnimator ofFloat(Object target, String xPropertyName, String yPropertyName, Path path) {
        float[] xValues = new float[NUM_POINTS];
        float[] yValues = new float[NUM_POINTS];
        calculateXYValues(path, xValues, yValues);
        PropertyValuesHolder xPvh = PropertyValuesHolder.ofFloat(xPropertyName, xValues);
        PropertyValuesHolder yPvh = PropertyValuesHolder.ofFloat(yPropertyName, yValues);
        return ObjectAnimator.ofPropertyValuesHolder(target, xPvh, yPvh);
    }

    public static <T> ObjectAnimator ofFloat(T target, Property<T, Float> xProperty, Property<T, Float> yProperty, Path path) {
        float[] xValues = new float[NUM_POINTS];
        float[] yValues = new float[NUM_POINTS];
        calculateXYValues(path, xValues, yValues);
        PropertyValuesHolder xPvh = PropertyValuesHolder.ofFloat((Property<?, Float>) xProperty, xValues);
        PropertyValuesHolder yPvh = PropertyValuesHolder.ofFloat((Property<?, Float>) yProperty, yValues);
        return ObjectAnimator.ofPropertyValuesHolder(target, xPvh, yPvh);
    }

    public static ObjectAnimator ofInt(Object target, String xPropertyName, String yPropertyName, Path path) {
        int[] xValues = new int[NUM_POINTS];
        int[] yValues = new int[NUM_POINTS];
        calculateXYValues(path, xValues, yValues);
        PropertyValuesHolder xPvh = PropertyValuesHolder.ofInt(xPropertyName, xValues);
        PropertyValuesHolder yPvh = PropertyValuesHolder.ofInt(yPropertyName, yValues);
        return ObjectAnimator.ofPropertyValuesHolder(target, xPvh, yPvh);
    }

    public static <T> ObjectAnimator ofInt(T target, Property<T, Integer> xProperty, Property<T, Integer> yProperty, Path path) {
        int[] xValues = new int[NUM_POINTS];
        int[] yValues = new int[NUM_POINTS];
        calculateXYValues(path, xValues, yValues);
        PropertyValuesHolder xPvh = PropertyValuesHolder.ofInt((Property<?, Integer>) xProperty, xValues);
        PropertyValuesHolder yPvh = PropertyValuesHolder.ofInt((Property<?, Integer>) yProperty, yValues);
        return ObjectAnimator.ofPropertyValuesHolder(target, xPvh, yPvh);
    }

    private static void calculateXYValues(Path path, @Size(201) float[] xValues, @Size(201) float[] yValues) {
        PathMeasure pathMeasure = new PathMeasure(path, false);
        float pathLength = pathMeasure.getLength();
        float[] position = new float[2];
        for (int i = 0; i < NUM_POINTS; i++) {
            float distance = (i * pathLength) / 200.0f;
            pathMeasure.getPosTan(distance, position, null);
            xValues[i] = position[0];
            yValues[i] = position[1];
        }
    }

    private static void calculateXYValues(Path path, @Size(201) int[] xValues, @Size(201) int[] yValues) {
        PathMeasure pathMeasure = new PathMeasure(path, false);
        float pathLength = pathMeasure.getLength();
        float[] position = new float[2];
        for (int i = 0; i < NUM_POINTS; i++) {
            float distance = (i * pathLength) / 200.0f;
            pathMeasure.getPosTan(distance, position, null);
            xValues[i] = Math.round(position[0]);
            yValues[i] = Math.round(position[1]);
        }
    }
}
package android.support.p000v4.view.animation;

import android.graphics.Path;
import android.graphics.PathMeasure;
import android.view.animation.Interpolator;

/* renamed from: android.support.v4.view.animation.PathInterpolatorApi14 */
/* loaded from: classes.dex */
class PathInterpolatorApi14 implements Interpolator {
    private static final float PRECISION = 0.002f;

    /* renamed from: mX */
    private final float[] f19mX;

    /* renamed from: mY */
    private final float[] f20mY;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PathInterpolatorApi14(Path path) {
        PathMeasure pathMeasure = new PathMeasure(path, false);
        float pathLength = pathMeasure.getLength();
        int numPoints = ((int) (pathLength / PRECISION)) + 1;
        this.f19mX = new float[numPoints];
        this.f20mY = new float[numPoints];
        float[] position = new float[2];
        for (int i = 0; i < numPoints; i++) {
            float distance = (i * pathLength) / (numPoints - 1);
            pathMeasure.getPosTan(distance, position, null);
            this.f19mX[i] = position[0];
            this.f20mY[i] = position[1];
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PathInterpolatorApi14(float controlX, float controlY) {
        this(createQuad(controlX, controlY));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PathInterpolatorApi14(float controlX1, float controlY1, float controlX2, float controlY2) {
        this(createCubic(controlX1, controlY1, controlX2, controlY2));
    }

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float t) {
        if (t <= 0.0f) {
            return 0.0f;
        }
        if (t >= 1.0f) {
            return 1.0f;
        }
        int startIndex = 0;
        int endIndex = this.f19mX.length - 1;
        while (endIndex - startIndex > 1) {
            int midIndex = (startIndex + endIndex) / 2;
            if (t < this.f19mX[midIndex]) {
                endIndex = midIndex;
            } else {
                startIndex = midIndex;
            }
        }
        float[] fArr = this.f19mX;
        float xRange = fArr[endIndex] - fArr[startIndex];
        if (xRange == 0.0f) {
            return this.f20mY[startIndex];
        }
        float tInRange = t - fArr[startIndex];
        float fraction = tInRange / xRange;
        float[] fArr2 = this.f20mY;
        float startY = fArr2[startIndex];
        float endY = fArr2[endIndex];
        return ((endY - startY) * fraction) + startY;
    }

    private static Path createQuad(float controlX, float controlY) {
        Path path = new Path();
        path.moveTo(0.0f, 0.0f);
        path.quadTo(controlX, controlY, 1.0f, 1.0f);
        return path;
    }

    private static Path createCubic(float controlX1, float controlY1, float controlX2, float controlY2) {
        Path path = new Path();
        path.moveTo(0.0f, 0.0f);
        path.cubicTo(controlX1, controlY1, controlX2, controlY2, 1.0f, 1.0f);
        return path;
    }
}
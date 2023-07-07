package android.support.transition;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v4.graphics.PathParser;
import android.util.AttributeSet;
import org.xmlpull.v1.XmlPullParser;

/* loaded from: classes.dex */
public class PatternPathMotion extends PathMotion {
    private Path mOriginalPatternPath;
    private final Path mPatternPath;
    private final Matrix mTempMatrix;

    public PatternPathMotion() {
        this.mPatternPath = new Path();
        this.mTempMatrix = new Matrix();
        this.mPatternPath.lineTo(1.0f, 0.0f);
        this.mOriginalPatternPath = this.mPatternPath;
    }

    public PatternPathMotion(Context context, AttributeSet attrs) {
        this.mPatternPath = new Path();
        this.mTempMatrix = new Matrix();
        TypedArray a = context.obtainStyledAttributes(attrs, Styleable.PATTERN_PATH_MOTION);
        try {
            String pathData = TypedArrayUtils.getNamedString(a, (XmlPullParser) attrs, "patternPathData", 0);
            if (pathData == null) {
                throw new RuntimeException("pathData must be supplied for patternPathMotion");
            }
            Path pattern = PathParser.createPathFromPathData(pathData);
            setPatternPath(pattern);
        } finally {
            a.recycle();
        }
    }

    public PatternPathMotion(Path patternPath) {
        this.mPatternPath = new Path();
        this.mTempMatrix = new Matrix();
        setPatternPath(patternPath);
    }

    public Path getPatternPath() {
        return this.mOriginalPatternPath;
    }

    public void setPatternPath(Path patternPath) {
        PathMeasure pathMeasure = new PathMeasure(patternPath, false);
        float length = pathMeasure.getLength();
        float[] pos = new float[2];
        pathMeasure.getPosTan(length, pos, null);
        float endX = pos[0];
        float endY = pos[1];
        pathMeasure.getPosTan(0.0f, pos, null);
        float startX = pos[0];
        float startY = pos[1];
        if (startX == endX && startY == endY) {
            throw new IllegalArgumentException("pattern must not end at the starting point");
        }
        this.mTempMatrix.setTranslate(-startX, -startY);
        float dx = endX - startX;
        float dy = endY - startY;
        float distance = distance(dx, dy);
        float scale = 1.0f / distance;
        this.mTempMatrix.postScale(scale, scale);
        double angle = Math.atan2(dy, dx);
        this.mTempMatrix.postRotate((float) Math.toDegrees(-angle));
        patternPath.transform(this.mTempMatrix, this.mPatternPath);
        this.mOriginalPatternPath = patternPath;
    }

    @Override // android.support.transition.PathMotion
    public Path getPath(float startX, float startY, float endX, float endY) {
        float dx = endX - startX;
        float dy = endY - startY;
        float length = distance(dx, dy);
        double angle = Math.atan2(dy, dx);
        this.mTempMatrix.setScale(length, length);
        this.mTempMatrix.postRotate((float) Math.toDegrees(angle));
        this.mTempMatrix.postTranslate(startX, startY);
        Path path = new Path();
        this.mPatternPath.transform(this.mTempMatrix, path);
        return path;
    }

    private static float distance(float x, float y) {
        return (float) Math.sqrt((x * x) + (y * y));
    }
}

package android.support.transition;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.p000v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

@SuppressLint({"ViewConstructor"})
/* loaded from: classes.dex */
class GhostViewApi14 extends View implements GhostViewImpl {
    Matrix mCurrentMatrix;
    private int mDeltaX;
    private int mDeltaY;
    private final Matrix mMatrix = new Matrix();
    private final ViewTreeObserver.OnPreDrawListener mOnPreDrawListener = new ViewTreeObserver.OnPreDrawListener() { // from class: android.support.transition.GhostViewApi14.1
        @Override // android.view.ViewTreeObserver.OnPreDrawListener
        public boolean onPreDraw() {
            GhostViewApi14 ghostViewApi14 = GhostViewApi14.this;
            ghostViewApi14.mCurrentMatrix = ghostViewApi14.mView.getMatrix();
            ViewCompat.postInvalidateOnAnimation(GhostViewApi14.this);
            if (GhostViewApi14.this.mStartParent != null && GhostViewApi14.this.mStartView != null) {
                GhostViewApi14.this.mStartParent.endViewTransition(GhostViewApi14.this.mStartView);
                ViewCompat.postInvalidateOnAnimation(GhostViewApi14.this.mStartParent);
                GhostViewApi14 ghostViewApi142 = GhostViewApi14.this;
                ghostViewApi142.mStartParent = null;
                ghostViewApi142.mStartView = null;
                return true;
            }
            return true;
        }
    };
    int mReferences;
    ViewGroup mStartParent;
    View mStartView;
    final View mView;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static GhostViewImpl addGhost(View view, ViewGroup viewGroup) {
        GhostViewApi14 ghostView = getGhostView(view);
        if (ghostView == null) {
            FrameLayout frameLayout = findFrameLayout(viewGroup);
            if (frameLayout == null) {
                return null;
            }
            ghostView = new GhostViewApi14(view);
            frameLayout.addView(ghostView);
        }
        ghostView.mReferences++;
        return ghostView;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void removeGhost(View view) {
        GhostViewApi14 ghostView = getGhostView(view);
        if (ghostView != null) {
            ghostView.mReferences--;
            if (ghostView.mReferences <= 0) {
                ViewParent parent = ghostView.getParent();
                if (parent instanceof ViewGroup) {
                    ViewGroup group = (ViewGroup) parent;
                    group.endViewTransition(ghostView);
                    group.removeView(ghostView);
                }
            }
        }
    }

    private static FrameLayout findFrameLayout(ViewGroup viewGroup) {
        while (!(viewGroup instanceof FrameLayout)) {
            ViewParent parent = viewGroup.getParent();
            if (!(parent instanceof ViewGroup)) {
                return null;
            }
            viewGroup = (ViewGroup) parent;
        }
        return (FrameLayout) viewGroup;
    }

    GhostViewApi14(View view) {
        super(view.getContext());
        this.mView = view;
        setLayerType(2, null);
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setGhostView(this.mView, this);
        int[] location = new int[2];
        getLocationOnScreen(location);
        this.mView.getLocationOnScreen(viewLocation);
        int[] viewLocation = {(int) (viewLocation[0] - this.mView.getTranslationX()), (int) (viewLocation[1] - this.mView.getTranslationY())};
        this.mDeltaX = viewLocation[0] - location[0];
        this.mDeltaY = viewLocation[1] - location[1];
        this.mView.getViewTreeObserver().addOnPreDrawListener(this.mOnPreDrawListener);
        this.mView.setVisibility(4);
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        this.mView.getViewTreeObserver().removeOnPreDrawListener(this.mOnPreDrawListener);
        this.mView.setVisibility(0);
        setGhostView(this.mView, null);
        super.onDetachedFromWindow();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        this.mMatrix.set(this.mCurrentMatrix);
        this.mMatrix.postTranslate(this.mDeltaX, this.mDeltaY);
        canvas.setMatrix(this.mMatrix);
        this.mView.draw(canvas);
    }

    @Override // android.view.View, android.support.transition.GhostViewImpl
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        this.mView.setVisibility(visibility == 0 ? 4 : 0);
    }

    @Override // android.support.transition.GhostViewImpl
    public void reserveEndViewTransition(ViewGroup viewGroup, View view) {
        this.mStartParent = viewGroup;
        this.mStartView = view;
    }

    private static void setGhostView(@NonNull View view, GhostViewApi14 ghostView) {
        view.setTag(C0238R.C0241id.ghost_view, ghostView);
    }

    static GhostViewApi14 getGhostView(@NonNull View view) {
        return (GhostViewApi14) view.getTag(C0238R.C0241id.ghost_view);
    }
}
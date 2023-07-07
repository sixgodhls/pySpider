package com.lcodecore.tkrefreshlayout.processor;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.support.p003v7.widget.RecyclerView;
import android.view.animation.DecelerateInterpolator;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.utils.LogUtil;
import com.lcodecore.tkrefreshlayout.utils.ScrollingUtil;
import java.io.PrintStream;
import java.util.LinkedList;

/* loaded from: classes.dex */
public class AnimProcessor implements IAnimRefresh, IAnimOverScroll {
    private static final float animFraction = 1.0f;
    private LinkedList<Animator> animQueue;

    /* renamed from: cp */
    private TwinklingRefreshLayout.CoContext f84cp;
    private boolean scrollHeadLocked = false;
    private boolean scrollBottomLocked = false;
    private boolean isAnimHeadToRefresh = false;
    private boolean isAnimHeadBack = false;
    private boolean isAnimBottomToLoad = false;
    private boolean isAnimBottomBack = false;
    private boolean isAnimHeadHide = false;
    private boolean isAnimBottomHide = false;
    private boolean isAnimOsTop = false;
    private boolean isOverScrollTopLocked = false;
    private boolean isAnimOsBottom = false;
    private boolean isOverScrollBottomLocked = false;
    private ValueAnimator.AnimatorUpdateListener animHeadUpListener = new ValueAnimator.AnimatorUpdateListener() { // from class: com.lcodecore.tkrefreshlayout.processor.AnimProcessor.10
        {
            AnimProcessor.this = this;
        }

        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public void onAnimationUpdate(ValueAnimator animation) {
            int height = ((Integer) animation.getAnimatedValue()).intValue();
            if (!AnimProcessor.this.scrollHeadLocked || !AnimProcessor.this.f84cp.isEnableKeepIView()) {
                AnimProcessor.this.f84cp.getHeader().getLayoutParams().height = height;
                AnimProcessor.this.f84cp.getHeader().requestLayout();
                AnimProcessor.this.f84cp.getHeader().setTranslationY(0.0f);
                AnimProcessor.this.f84cp.onPullDownReleasing(height);
            } else {
                AnimProcessor.this.transHeader(height);
            }
            if (!AnimProcessor.this.f84cp.isOpenFloatRefresh()) {
                AnimProcessor.this.f84cp.getTargetView().setTranslationY(height);
                AnimProcessor.this.translateExHead(height);
            }
        }
    };
    private ValueAnimator.AnimatorUpdateListener animBottomUpListener = new ValueAnimator.AnimatorUpdateListener() { // from class: com.lcodecore.tkrefreshlayout.processor.AnimProcessor.11
        {
            AnimProcessor.this = this;
        }

        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public void onAnimationUpdate(ValueAnimator animation) {
            int height = ((Integer) animation.getAnimatedValue()).intValue();
            if (!AnimProcessor.this.scrollBottomLocked || !AnimProcessor.this.f84cp.isEnableKeepIView()) {
                AnimProcessor.this.f84cp.getFooter().getLayoutParams().height = height;
                AnimProcessor.this.f84cp.getFooter().requestLayout();
                AnimProcessor.this.f84cp.getFooter().setTranslationY(0.0f);
                AnimProcessor.this.f84cp.onPullUpReleasing(height);
            } else {
                AnimProcessor.this.transFooter(height);
            }
            AnimProcessor.this.f84cp.getTargetView().setTranslationY(-height);
        }
    };
    private ValueAnimator.AnimatorUpdateListener overScrollTopUpListener = new ValueAnimator.AnimatorUpdateListener() { // from class: com.lcodecore.tkrefreshlayout.processor.AnimProcessor.12
        {
            AnimProcessor.this = this;
        }

        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public void onAnimationUpdate(ValueAnimator animation) {
            int height = ((Integer) animation.getAnimatedValue()).intValue();
            if (AnimProcessor.this.f84cp.isOverScrollTopShow()) {
                if (AnimProcessor.this.f84cp.getHeader().getVisibility() != 0) {
                    AnimProcessor.this.f84cp.getHeader().setVisibility(0);
                }
            } else if (AnimProcessor.this.f84cp.getHeader().getVisibility() != 8) {
                AnimProcessor.this.f84cp.getHeader().setVisibility(8);
            }
            if (!AnimProcessor.this.scrollHeadLocked || !AnimProcessor.this.f84cp.isEnableKeepIView()) {
                AnimProcessor.this.f84cp.getHeader().setTranslationY(0.0f);
                AnimProcessor.this.f84cp.getHeader().getLayoutParams().height = height;
                AnimProcessor.this.f84cp.getHeader().requestLayout();
                AnimProcessor.this.f84cp.onPullDownReleasing(height);
            } else {
                AnimProcessor.this.transHeader(height);
            }
            AnimProcessor.this.f84cp.getTargetView().setTranslationY(height);
            AnimProcessor.this.translateExHead(height);
        }
    };
    private ValueAnimator.AnimatorUpdateListener overScrollBottomUpListener = new ValueAnimator.AnimatorUpdateListener() { // from class: com.lcodecore.tkrefreshlayout.processor.AnimProcessor.13
        {
            AnimProcessor.this = this;
        }

        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public void onAnimationUpdate(ValueAnimator animation) {
            int height = ((Integer) animation.getAnimatedValue()).intValue();
            if (AnimProcessor.this.f84cp.isOverScrollBottomShow()) {
                if (AnimProcessor.this.f84cp.getFooter().getVisibility() != 0) {
                    AnimProcessor.this.f84cp.getFooter().setVisibility(0);
                }
            } else if (AnimProcessor.this.f84cp.getFooter().getVisibility() != 8) {
                AnimProcessor.this.f84cp.getFooter().setVisibility(8);
            }
            if (!AnimProcessor.this.scrollBottomLocked || !AnimProcessor.this.f84cp.isEnableKeepIView()) {
                AnimProcessor.this.f84cp.getFooter().getLayoutParams().height = height;
                AnimProcessor.this.f84cp.getFooter().requestLayout();
                AnimProcessor.this.f84cp.getFooter().setTranslationY(0.0f);
                AnimProcessor.this.f84cp.onPullUpReleasing(height);
            } else {
                AnimProcessor.this.transFooter(height);
            }
            AnimProcessor.this.f84cp.getTargetView().setTranslationY(-height);
        }
    };
    private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator(8.0f);

    public AnimProcessor(TwinklingRefreshLayout.CoContext coProcessor) {
        this.f84cp = coProcessor;
    }

    @Override // com.lcodecore.tkrefreshlayout.processor.IAnimRefresh
    public void scrollHeadByMove(float moveY) {
        float offsetY = (this.decelerateInterpolator.getInterpolation((moveY / this.f84cp.getMaxHeadHeight()) / 2.0f) * moveY) / 2.0f;
        if (this.f84cp.isPureScrollModeOn() || (!this.f84cp.enableRefresh() && !this.f84cp.isOverScrollTopShow())) {
            if (this.f84cp.getHeader().getVisibility() != 8) {
                this.f84cp.getHeader().setVisibility(8);
            }
        } else if (this.f84cp.getHeader().getVisibility() != 0) {
            this.f84cp.getHeader().setVisibility(0);
        }
        if (this.scrollHeadLocked && this.f84cp.isEnableKeepIView()) {
            this.f84cp.getHeader().setTranslationY(offsetY - this.f84cp.getHeader().getLayoutParams().height);
        } else {
            this.f84cp.getHeader().setTranslationY(0.0f);
            this.f84cp.getHeader().getLayoutParams().height = (int) Math.abs(offsetY);
            this.f84cp.getHeader().requestLayout();
            this.f84cp.onPullingDown(offsetY);
        }
        if (!this.f84cp.isOpenFloatRefresh()) {
            this.f84cp.getTargetView().setTranslationY(offsetY);
            translateExHead((int) offsetY);
        }
    }

    @Override // com.lcodecore.tkrefreshlayout.processor.IAnimRefresh
    public void scrollBottomByMove(float moveY) {
        float offsetY = (this.decelerateInterpolator.getInterpolation((moveY / this.f84cp.getMaxBottomHeight()) / 2.0f) * moveY) / 2.0f;
        if (this.f84cp.isPureScrollModeOn() || (!this.f84cp.enableLoadmore() && !this.f84cp.isOverScrollBottomShow())) {
            if (this.f84cp.getFooter().getVisibility() != 8) {
                this.f84cp.getFooter().setVisibility(8);
            }
        } else if (this.f84cp.getFooter().getVisibility() != 0) {
            this.f84cp.getFooter().setVisibility(0);
        }
        if (this.scrollBottomLocked && this.f84cp.isEnableKeepIView()) {
            this.f84cp.getFooter().setTranslationY(this.f84cp.getFooter().getLayoutParams().height - offsetY);
        } else {
            this.f84cp.getFooter().setTranslationY(0.0f);
            this.f84cp.getFooter().getLayoutParams().height = (int) Math.abs(offsetY);
            this.f84cp.getFooter().requestLayout();
            this.f84cp.onPullingUp(-offsetY);
        }
        this.f84cp.getTargetView().setTranslationY(-offsetY);
    }

    public void dealPullDownRelease() {
        if (!this.f84cp.isPureScrollModeOn() && this.f84cp.enableRefresh() && getVisibleHeadHeight() >= this.f84cp.getHeadHeight() - this.f84cp.getTouchSlop()) {
            animHeadToRefresh();
        } else {
            animHeadBack(false);
        }
    }

    public void dealPullUpRelease() {
        if (!this.f84cp.isPureScrollModeOn() && this.f84cp.enableLoadmore() && getVisibleFootHeight() >= this.f84cp.getBottomHeight() - this.f84cp.getTouchSlop()) {
            animBottomToLoad();
        } else {
            animBottomBack(false);
        }
    }

    private int getVisibleHeadHeight() {
        LogUtil.m40i("header translationY:" + this.f84cp.getHeader().getTranslationY() + ",Visible head height:" + (this.f84cp.getHeader().getLayoutParams().height + this.f84cp.getHeader().getTranslationY()));
        return (int) (this.f84cp.getHeader().getLayoutParams().height + this.f84cp.getHeader().getTranslationY());
    }

    public int getVisibleFootHeight() {
        LogUtil.m40i("footer translationY:" + this.f84cp.getFooter().getTranslationY() + "");
        return (int) (this.f84cp.getFooter().getLayoutParams().height - this.f84cp.getFooter().getTranslationY());
    }

    public void transHeader(float offsetY) {
        this.f84cp.getHeader().setTranslationY(offsetY - this.f84cp.getHeader().getLayoutParams().height);
    }

    public void transFooter(float offsetY) {
        this.f84cp.getFooter().setTranslationY(this.f84cp.getFooter().getLayoutParams().height - offsetY);
    }

    @Override // com.lcodecore.tkrefreshlayout.processor.IAnimRefresh
    public void animHeadToRefresh() {
        LogUtil.m40i("animHeadToRefresh:");
        this.isAnimHeadToRefresh = true;
        animLayoutByTime(getVisibleHeadHeight(), this.f84cp.getHeadHeight(), this.animHeadUpListener, new AnimatorListenerAdapter() { // from class: com.lcodecore.tkrefreshlayout.processor.AnimProcessor.1
            {
                AnimProcessor.this = this;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                AnimProcessor.this.isAnimHeadToRefresh = false;
                if (AnimProcessor.this.f84cp.getHeader().getVisibility() != 0) {
                    AnimProcessor.this.f84cp.getHeader().setVisibility(0);
                }
                AnimProcessor.this.f84cp.setRefreshVisible(true);
                if (AnimProcessor.this.f84cp.isEnableKeepIView()) {
                    if (!AnimProcessor.this.scrollHeadLocked) {
                        AnimProcessor.this.f84cp.setRefreshing(true);
                        AnimProcessor.this.f84cp.onRefresh();
                        AnimProcessor.this.scrollHeadLocked = true;
                        return;
                    }
                    return;
                }
                AnimProcessor.this.f84cp.setRefreshing(true);
                AnimProcessor.this.f84cp.onRefresh();
            }
        });
    }

    @Override // com.lcodecore.tkrefreshlayout.processor.IAnimRefresh
    public void animHeadBack(final boolean isFinishRefresh) {
        LogUtil.m40i("animHeadBack：finishRefresh?->" + isFinishRefresh);
        this.isAnimHeadBack = true;
        if (isFinishRefresh && this.scrollHeadLocked && this.f84cp.isEnableKeepIView()) {
            this.f84cp.setPrepareFinishRefresh(true);
        }
        animLayoutByTime(getVisibleHeadHeight(), 0, this.animHeadUpListener, new AnimatorListenerAdapter() { // from class: com.lcodecore.tkrefreshlayout.processor.AnimProcessor.2
            {
                AnimProcessor.this = this;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                AnimProcessor.this.isAnimHeadBack = false;
                AnimProcessor.this.f84cp.setRefreshVisible(false);
                if (isFinishRefresh && AnimProcessor.this.scrollHeadLocked && AnimProcessor.this.f84cp.isEnableKeepIView()) {
                    AnimProcessor.this.f84cp.getHeader().getLayoutParams().height = 0;
                    AnimProcessor.this.f84cp.getHeader().requestLayout();
                    AnimProcessor.this.f84cp.getHeader().setTranslationY(0.0f);
                    AnimProcessor.this.scrollHeadLocked = false;
                    AnimProcessor.this.f84cp.setRefreshing(false);
                    AnimProcessor.this.f84cp.resetHeaderView();
                }
            }
        });
    }

    @Override // com.lcodecore.tkrefreshlayout.processor.IAnimRefresh
    public void animBottomToLoad() {
        LogUtil.m40i("animBottomToLoad");
        this.isAnimBottomToLoad = true;
        animLayoutByTime(getVisibleFootHeight(), this.f84cp.getBottomHeight(), this.animBottomUpListener, new AnimatorListenerAdapter() { // from class: com.lcodecore.tkrefreshlayout.processor.AnimProcessor.3
            {
                AnimProcessor.this = this;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                AnimProcessor.this.isAnimBottomToLoad = false;
                if (AnimProcessor.this.f84cp.getFooter().getVisibility() != 0) {
                    AnimProcessor.this.f84cp.getFooter().setVisibility(0);
                }
                AnimProcessor.this.f84cp.setLoadVisible(true);
                if (AnimProcessor.this.f84cp.isEnableKeepIView()) {
                    if (!AnimProcessor.this.scrollBottomLocked) {
                        AnimProcessor.this.f84cp.setLoadingMore(true);
                        AnimProcessor.this.f84cp.onLoadMore();
                        AnimProcessor.this.scrollBottomLocked = true;
                        return;
                    }
                    return;
                }
                AnimProcessor.this.f84cp.setLoadingMore(true);
                AnimProcessor.this.f84cp.onLoadMore();
            }
        });
    }

    @Override // com.lcodecore.tkrefreshlayout.processor.IAnimRefresh
    public void animBottomBack(final boolean isFinishLoading) {
        LogUtil.m40i("animBottomBack：finishLoading?->" + isFinishLoading);
        this.isAnimBottomBack = true;
        if (isFinishLoading && this.scrollBottomLocked && this.f84cp.isEnableKeepIView()) {
            this.f84cp.setPrepareFinishLoadMore(true);
        }
        animLayoutByTime(getVisibleFootHeight(), 0, new ValueAnimator.AnimatorUpdateListener() { // from class: com.lcodecore.tkrefreshlayout.processor.AnimProcessor.4
            {
                AnimProcessor.this = this;
            }

            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                int dy;
                int height = ((Integer) animation.getAnimatedValue()).intValue();
                if (!ScrollingUtil.isViewToBottom(AnimProcessor.this.f84cp.getTargetView(), AnimProcessor.this.f84cp.getTouchSlop()) && (dy = AnimProcessor.this.getVisibleFootHeight() - height) > 0) {
                    if (AnimProcessor.this.f84cp.getTargetView() instanceof RecyclerView) {
                        ScrollingUtil.scrollAViewBy(AnimProcessor.this.f84cp.getTargetView(), dy);
                    } else {
                        ScrollingUtil.scrollAViewBy(AnimProcessor.this.f84cp.getTargetView(), dy / 2);
                    }
                }
                AnimProcessor.this.animBottomUpListener.onAnimationUpdate(animation);
            }
        }, new AnimatorListenerAdapter() { // from class: com.lcodecore.tkrefreshlayout.processor.AnimProcessor.5
            {
                AnimProcessor.this = this;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                AnimProcessor.this.isAnimBottomBack = false;
                AnimProcessor.this.f84cp.setLoadVisible(false);
                if (isFinishLoading && AnimProcessor.this.scrollBottomLocked && AnimProcessor.this.f84cp.isEnableKeepIView()) {
                    AnimProcessor.this.f84cp.getFooter().getLayoutParams().height = 0;
                    AnimProcessor.this.f84cp.getFooter().requestLayout();
                    AnimProcessor.this.f84cp.getFooter().setTranslationY(0.0f);
                    AnimProcessor.this.scrollBottomLocked = false;
                    AnimProcessor.this.f84cp.resetBottomView();
                    AnimProcessor.this.f84cp.setLoadingMore(false);
                }
            }
        });
    }

    @Override // com.lcodecore.tkrefreshlayout.processor.IAnimRefresh
    public void animHeadHideByVy(int vy) {
        if (this.isAnimHeadHide) {
            return;
        }
        this.isAnimHeadHide = true;
        LogUtil.m40i("animHeadHideByVy：vy->" + vy);
        int vy2 = Math.abs(vy);
        if (vy2 < 5000) {
            vy2 = 8000;
        }
        animLayoutByTime(getVisibleHeadHeight(), 0, Math.abs((getVisibleHeadHeight() * 1000) / vy2) * 5, this.animHeadUpListener, new AnimatorListenerAdapter() { // from class: com.lcodecore.tkrefreshlayout.processor.AnimProcessor.6
            {
                AnimProcessor.this = this;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                AnimProcessor.this.isAnimHeadHide = false;
                AnimProcessor.this.f84cp.setRefreshVisible(false);
                if (!AnimProcessor.this.f84cp.isEnableKeepIView()) {
                    AnimProcessor.this.f84cp.setRefreshing(false);
                    AnimProcessor.this.f84cp.onRefreshCanceled();
                    AnimProcessor.this.f84cp.resetHeaderView();
                }
            }
        });
    }

    @Override // com.lcodecore.tkrefreshlayout.processor.IAnimRefresh
    public void animBottomHideByVy(int vy) {
        LogUtil.m40i("animBottomHideByVy：vy->" + vy);
        if (this.isAnimBottomHide) {
            return;
        }
        this.isAnimBottomHide = true;
        int vy2 = Math.abs(vy);
        if (vy2 < 5000) {
            vy2 = 8000;
        }
        animLayoutByTime(getVisibleFootHeight(), 0, ((getVisibleFootHeight() * 5) * 1000) / vy2, this.animBottomUpListener, new AnimatorListenerAdapter() { // from class: com.lcodecore.tkrefreshlayout.processor.AnimProcessor.7
            {
                AnimProcessor.this = this;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                AnimProcessor.this.isAnimBottomHide = false;
                AnimProcessor.this.f84cp.setLoadVisible(false);
                if (!AnimProcessor.this.f84cp.isEnableKeepIView()) {
                    AnimProcessor.this.f84cp.setLoadingMore(false);
                    AnimProcessor.this.f84cp.onLoadmoreCanceled();
                    AnimProcessor.this.f84cp.resetBottomView();
                }
            }
        });
    }

    @Override // com.lcodecore.tkrefreshlayout.processor.IAnimOverScroll
    public void animOverScrollTop(float vy, int computeTimes) {
        int i;
        LogUtil.m40i("animOverScrollTop：vy->" + vy + ",computeTimes->" + computeTimes);
        if (this.isOverScrollTopLocked) {
            return;
        }
        this.isOverScrollTopLocked = true;
        this.isAnimOsTop = true;
        this.f84cp.setStatePTD();
        int oh = (int) Math.abs((vy / computeTimes) / 2.0f);
        final int overHeight = oh > this.f84cp.getOsHeight() ? this.f84cp.getOsHeight() : oh;
        if (overHeight <= 50) {
            i = 115;
        } else {
            double d = overHeight;
            Double.isNaN(d);
            i = (int) ((d * 0.3d) + 100.0d);
        }
        final int time = i;
        animLayoutByTime(getVisibleHeadHeight(), overHeight, time, this.overScrollTopUpListener, new AnimatorListenerAdapter() { // from class: com.lcodecore.tkrefreshlayout.processor.AnimProcessor.8
            {
                AnimProcessor.this = this;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (AnimProcessor.this.scrollHeadLocked && AnimProcessor.this.f84cp.isEnableKeepIView() && AnimProcessor.this.f84cp.showRefreshingWhenOverScroll()) {
                    AnimProcessor.this.animHeadToRefresh();
                    AnimProcessor.this.isAnimOsTop = false;
                    AnimProcessor.this.isOverScrollTopLocked = false;
                    return;
                }
                AnimProcessor animProcessor = AnimProcessor.this;
                animProcessor.animLayoutByTime(overHeight, 0, time * 2, animProcessor.overScrollTopUpListener, new AnimatorListenerAdapter() { // from class: com.lcodecore.tkrefreshlayout.processor.AnimProcessor.8.1
                    {
                        C08798.this = this;
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation2) {
                        AnimProcessor.this.isAnimOsTop = false;
                        AnimProcessor.this.isOverScrollTopLocked = false;
                    }
                });
            }
        });
    }

    @Override // com.lcodecore.tkrefreshlayout.processor.IAnimOverScroll
    public void animOverScrollBottom(float vy, int computeTimes) {
        int i;
        LogUtil.m40i("animOverScrollBottom：vy->" + vy + ",computeTimes->" + computeTimes);
        if (this.isOverScrollBottomLocked) {
            return;
        }
        this.f84cp.setStatePBU();
        int oh = (int) Math.abs((vy / computeTimes) / 2.0f);
        final int overHeight = oh > this.f84cp.getOsHeight() ? this.f84cp.getOsHeight() : oh;
        if (overHeight <= 50) {
            i = 115;
        } else {
            double d = overHeight;
            Double.isNaN(d);
            i = (int) ((d * 0.3d) + 100.0d);
        }
        final int time = i;
        if (!this.scrollBottomLocked && this.f84cp.autoLoadMore()) {
            this.f84cp.startLoadMore();
            return;
        }
        this.isOverScrollBottomLocked = true;
        this.isAnimOsBottom = true;
        animLayoutByTime(0, overHeight, time, this.overScrollBottomUpListener, new AnimatorListenerAdapter() { // from class: com.lcodecore.tkrefreshlayout.processor.AnimProcessor.9
            {
                AnimProcessor.this = this;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (AnimProcessor.this.scrollBottomLocked && AnimProcessor.this.f84cp.isEnableKeepIView() && AnimProcessor.this.f84cp.showLoadingWhenOverScroll()) {
                    AnimProcessor.this.animBottomToLoad();
                    AnimProcessor.this.isAnimOsBottom = false;
                    AnimProcessor.this.isOverScrollBottomLocked = false;
                    return;
                }
                AnimProcessor animProcessor = AnimProcessor.this;
                animProcessor.animLayoutByTime(overHeight, 0, time * 2, animProcessor.overScrollBottomUpListener, new AnimatorListenerAdapter() { // from class: com.lcodecore.tkrefreshlayout.processor.AnimProcessor.9.1
                    {
                        C08819.this = this;
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation2) {
                        AnimProcessor.this.isAnimOsBottom = false;
                        AnimProcessor.this.isOverScrollBottomLocked = false;
                    }
                });
            }
        });
    }

    public void translateExHead(int offsetY) {
        if (!this.f84cp.isExHeadLocked()) {
            this.f84cp.getExHead().setTranslationY(offsetY);
        }
    }

    public void animLayoutByTime(int start, int end, long time, ValueAnimator.AnimatorUpdateListener listener, Animator.AnimatorListener animatorListener) {
        ValueAnimator va = ValueAnimator.ofInt(start, end);
        va.setInterpolator(new DecelerateInterpolator());
        va.addUpdateListener(listener);
        va.addListener(animatorListener);
        va.setDuration(time);
        va.start();
    }

    public void animLayoutByTime(int start, int end, long time, ValueAnimator.AnimatorUpdateListener listener) {
        ValueAnimator va = ValueAnimator.ofInt(start, end);
        va.setInterpolator(new DecelerateInterpolator());
        va.addUpdateListener(listener);
        va.setDuration(time);
        va.start();
    }

    public void animLayoutByTime(int start, int end, ValueAnimator.AnimatorUpdateListener listener, Animator.AnimatorListener animatorListener) {
        ValueAnimator va = ValueAnimator.ofInt(start, end);
        va.setInterpolator(new DecelerateInterpolator());
        va.addUpdateListener(listener);
        va.addListener(animatorListener);
        va.setDuration((int) (Math.abs(start - end) * animFraction));
        va.start();
    }

    private void offerToQueue(Animator animator) {
        if (animator == null) {
            return;
        }
        if (this.animQueue == null) {
            this.animQueue = new LinkedList<>();
        }
        this.animQueue.offer(animator);
        PrintStream printStream = System.out;
        printStream.println("Current Animators：" + this.animQueue.size());
        animator.addListener(new AnimatorListenerAdapter() { // from class: com.lcodecore.tkrefreshlayout.processor.AnimProcessor.14
            long startTime = 0;

            {
                AnimProcessor.this = this;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                this.startTime = System.currentTimeMillis();
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                AnimProcessor.this.animQueue.poll();
                if (AnimProcessor.this.animQueue.size() > 0) {
                    ((Animator) AnimProcessor.this.animQueue.getFirst()).start();
                }
                PrintStream printStream2 = System.out;
                printStream2.println("Anim end：start time->" + this.startTime + ",elapsed time->" + (System.currentTimeMillis() - this.startTime));
            }
        });
        if (this.animQueue.size() == 1) {
            animator.start();
        }
    }
}
package com.lcodecore.tkrefreshlayout.utils;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ScrollView;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/* loaded from: classes.dex */
public class ScrollingUtil {
    private ScrollingUtil() {
    }

    public static boolean canChildScrollUp(View mChildView) {
        if (mChildView == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT < 14) {
            if (!(mChildView instanceof AbsListView)) {
                return ViewCompat.canScrollVertically(mChildView, -1) || mChildView.getScrollY() > 0;
            }
            AbsListView absListView = (AbsListView) mChildView;
            if (absListView.getChildCount() <= 0) {
                return false;
            }
            return absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0).getTop() < absListView.getPaddingTop();
        }
        return ViewCompat.canScrollVertically(mChildView, -1);
    }

    public static boolean canChildScrollDown(View mChildView) {
        if (Build.VERSION.SDK_INT < 14) {
            if (!(mChildView instanceof AbsListView)) {
                return ViewCompat.canScrollVertically(mChildView, 1) || mChildView.getScrollY() < 0;
            }
            AbsListView absListView = (AbsListView) mChildView;
            return absListView.getChildCount() > 0 && (absListView.getLastVisiblePosition() < absListView.getChildCount() - 1 || absListView.getChildAt(absListView.getChildCount() - 1).getBottom() > absListView.getPaddingBottom());
        }
        return ViewCompat.canScrollVertically(mChildView, 1);
    }

    public static boolean isScrollViewOrWebViewToTop(View view) {
        return view != null && view.getScrollY() == 0;
    }

    public static boolean isViewToTop(View view, int mTouchSlop) {
        return view instanceof AbsListView ? isAbsListViewToTop((AbsListView) view) : view instanceof RecyclerView ? isRecyclerViewToTop((RecyclerView) view) : view != null && Math.abs(view.getScrollY()) <= mTouchSlop * 2;
    }

    public static boolean isViewToBottom(View view, int mTouchSlop) {
        if (view instanceof AbsListView) {
            return isAbsListViewToBottom((AbsListView) view);
        }
        if (view instanceof RecyclerView) {
            return isRecyclerViewToBottom((RecyclerView) view);
        }
        if (view instanceof WebView) {
            return isWebViewToBottom((WebView) view, mTouchSlop);
        }
        if (!(view instanceof ViewGroup)) {
            return false;
        }
        return isViewGroupToBottom((ViewGroup) view);
    }

    public static boolean isAbsListViewToTop(AbsListView absListView) {
        if (absListView != null) {
            int firstChildTop = 0;
            if (absListView.getChildCount() > 0) {
                firstChildTop = absListView.getChildAt(0).getTop() - absListView.getPaddingTop();
            }
            if (absListView.getFirstVisiblePosition() == 0 && firstChildTop == 0) {
                return true;
            }
        }
        return false;
    }

    public static boolean isRecyclerViewToTop(RecyclerView recyclerView) {
        if (recyclerView != null) {
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            if (manager == null || manager.getItemCount() == 0) {
                return true;
            }
            int firstChildTop = 0;
            if (recyclerView.getChildCount() > 0) {
                View firstVisibleChild = recyclerView.getChildAt(0);
                if (firstVisibleChild != null && firstVisibleChild.getMeasuredHeight() >= recyclerView.getMeasuredHeight()) {
                    if (Build.VERSION.SDK_INT >= 14) {
                        return !ViewCompat.canScrollVertically(recyclerView, -1);
                    }
                    return !ViewCompat.canScrollVertically(recyclerView, -1) && recyclerView.getScrollY() <= 0;
                }
                View firstChild = recyclerView.getChildAt(0);
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) firstChild.getLayoutParams();
                firstChildTop = ((firstChild.getTop() - layoutParams.topMargin) - getRecyclerViewItemTopInset(layoutParams)) - recyclerView.getPaddingTop();
            }
            if (manager instanceof LinearLayoutManager) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) manager;
                if (layoutManager.findFirstCompletelyVisibleItemPosition() < 1 && firstChildTop == 0) {
                    return true;
                }
            } else if (manager instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager layoutManager2 = (StaggeredGridLayoutManager) manager;
                int[] out = layoutManager2.findFirstCompletelyVisibleItemPositions(null);
                if (out[0] < 1 && firstChildTop == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private static int getRecyclerViewItemTopInset(RecyclerView.LayoutParams layoutParams) {
        try {
            Field field = RecyclerView.LayoutParams.class.getDeclaredField("mDecorInsets");
            field.setAccessible(true);
            Rect decorInsets = (Rect) field.get(layoutParams);
            return decorInsets.top;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static boolean isWebViewToBottom(WebView webview, int mTouchSlop) {
        return webview != null && (((float) webview.getContentHeight()) * webview.getScale()) - ((float) (webview.getHeight() + webview.getScrollY())) <= ((float) (mTouchSlop * 2));
    }

    public static boolean isViewGroupToBottom(ViewGroup viewGroup) {
        View subChildView = viewGroup.getChildAt(0);
        return subChildView != null && subChildView.getMeasuredHeight() <= viewGroup.getScrollY() + viewGroup.getHeight();
    }

    public static boolean isScrollViewToBottom(ScrollView scrollView) {
        if (scrollView != null) {
            int scrollContentHeight = ((scrollView.getScrollY() + scrollView.getMeasuredHeight()) - scrollView.getPaddingTop()) - scrollView.getPaddingBottom();
            int realContentHeight = scrollView.getChildAt(0).getMeasuredHeight();
            if (scrollContentHeight == realContentHeight) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAbsListViewToBottom(AbsListView absListView) {
        if (absListView == null || absListView.getAdapter() == null || absListView.getChildCount() <= 0 || absListView.getLastVisiblePosition() != ((ListAdapter) absListView.getAdapter()).getCount() - 1) {
            return false;
        }
        View lastChild = absListView.getChildAt(absListView.getChildCount() - 1);
        return lastChild.getBottom() <= absListView.getMeasuredHeight();
    }

    public static boolean isRecyclerViewToBottom(RecyclerView recyclerView) {
        RecyclerView.LayoutManager manager;
        if (recyclerView == null || (manager = recyclerView.getLayoutManager()) == null || manager.getItemCount() == 0) {
            return false;
        }
        if (manager instanceof LinearLayoutManager) {
            View lastVisibleChild = recyclerView.getChildAt(recyclerView.getChildCount() - 1);
            if (lastVisibleChild != null && lastVisibleChild.getMeasuredHeight() >= recyclerView.getMeasuredHeight()) {
                if (Build.VERSION.SDK_INT >= 14) {
                    return !ViewCompat.canScrollVertically(recyclerView, 1);
                }
                return !ViewCompat.canScrollVertically(recyclerView, 1) && recyclerView.getScrollY() >= 0;
            }
            LinearLayoutManager layoutManager = (LinearLayoutManager) manager;
            if (layoutManager.findLastCompletelyVisibleItemPosition() == layoutManager.getItemCount() - 1) {
                return true;
            }
        } else if (manager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager2 = (StaggeredGridLayoutManager) manager;
            int[] out = layoutManager2.findLastCompletelyVisibleItemPositions(null);
            int lastPosition = layoutManager2.getItemCount() - 1;
            for (int position : out) {
                if (position == lastPosition) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void scrollAViewBy(View view, int height) {
        if (!(view instanceof RecyclerView)) {
            if (!(view instanceof ScrollView)) {
                if (!(view instanceof AbsListView)) {
                    try {
                        Method method = view.getClass().getDeclaredMethod("smoothScrollBy", Integer.class, Integer.class);
                        method.invoke(view, 0, Integer.valueOf(height));
                        return;
                    } catch (Exception e) {
                        view.scrollBy(0, height);
                        return;
                    }
                }
                ((AbsListView) view).smoothScrollBy(height, 0);
                return;
            }
            ((ScrollView) view).smoothScrollBy(0, height);
            return;
        }
        ((RecyclerView) view).scrollBy(0, height);
    }

    public static void scrollToBottom(final ScrollView scrollView) {
        if (scrollView != null) {
            scrollView.post(new Runnable() { // from class: com.lcodecore.tkrefreshlayout.utils.ScrollingUtil.1
                @Override // java.lang.Runnable
                public void run() {
                    scrollView.fullScroll(130);
                }
            });
        }
    }

    public static void scrollToBottom(final AbsListView absListView) {
        if (absListView != null && absListView.getAdapter() != null && ((ListAdapter) absListView.getAdapter()).getCount() > 0) {
            absListView.post(new Runnable() { // from class: com.lcodecore.tkrefreshlayout.utils.ScrollingUtil.2
                @Override // java.lang.Runnable
                public void run() {
                    AbsListView absListView2 = absListView;
                    absListView2.setSelection(((ListAdapter) absListView2.getAdapter()).getCount() - 1);
                }
            });
        }
    }

    public static void scrollToBottom(final RecyclerView recyclerView) {
        if (recyclerView != null && recyclerView.getAdapter() != null && recyclerView.getAdapter().getItemCount() > 0) {
            recyclerView.post(new Runnable() { // from class: com.lcodecore.tkrefreshlayout.utils.ScrollingUtil.3
                @Override // java.lang.Runnable
                public void run() {
                    RecyclerView recyclerView2 = RecyclerView.this;
                    recyclerView2.smoothScrollToPosition(recyclerView2.getAdapter().getItemCount() - 1);
                }
            });
        }
    }

    public static void scrollToBottom(View view) {
        if (view instanceof RecyclerView) {
            scrollToBottom((RecyclerView) view);
        }
        if (view instanceof AbsListView) {
            scrollToBottom((AbsListView) view);
        }
        if (view instanceof ScrollView) {
            scrollToBottom((ScrollView) view);
        }
    }

    public static int getScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService("window");
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }
}

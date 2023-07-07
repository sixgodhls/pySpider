package me.majiajie.pagerbottomtabstrip;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import java.util.ArrayList;
import java.util.List;
import me.majiajie.pagerbottomtabstrip.internal.CustomItemLayout;
import me.majiajie.pagerbottomtabstrip.internal.CustomItemVerticalLayout;
import me.majiajie.pagerbottomtabstrip.internal.MaterialItemLayout;
import me.majiajie.pagerbottomtabstrip.internal.MaterialItemVerticalLayout;
import me.majiajie.pagerbottomtabstrip.internal.Utils;
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem;
import me.majiajie.pagerbottomtabstrip.item.MaterialItemView;
import me.majiajie.pagerbottomtabstrip.item.OnlyIconMaterialItemView;
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener;

/* loaded from: classes.dex */
public class PageNavigationView extends ViewGroup {
    private static final String INSTANCE_STATUS = "INSTANCE_STATUS";
    private final String STATUS_SELECTED;
    private boolean mEnableVerticalLayout;
    private NavigationController mNavigationController;
    private ViewPagerPageChangeListener mPageChangeListener;
    private OnTabItemSelectedListener mTabItemListener;
    private int mTabPaddingBottom;
    private int mTabPaddingTop;
    private ViewPager mViewPager;

    public PageNavigationView(Context context) {
        this(context, null);
    }

    public PageNavigationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mTabItemListener = new OnTabItemSelectedListener() { // from class: me.majiajie.pagerbottomtabstrip.PageNavigationView.1
            @Override // me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener
            public void onSelected(int index, int old) {
                if (PageNavigationView.this.mViewPager != null) {
                    PageNavigationView.this.mViewPager.setCurrentItem(index, false);
                }
            }

            @Override // me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener
            public void onRepeat(int index) {
            }
        };
        this.STATUS_SELECTED = "STATUS_SELECTED";
        setPadding(0, 0, 0, 0);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PageNavigationView);
        if (a.hasValue(R.styleable.PageNavigationView_NavigationPaddingTop)) {
            this.mTabPaddingTop = a.getDimensionPixelSize(R.styleable.PageNavigationView_NavigationPaddingTop, 0);
        }
        if (a.hasValue(R.styleable.PageNavigationView_NavigationPaddingBottom)) {
            this.mTabPaddingBottom = a.getDimensionPixelSize(R.styleable.PageNavigationView_NavigationPaddingBottom, 0);
        }
        a.recycle();
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        int maxWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        int maxHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                maxWidth = Math.max(maxWidth, child.getMeasuredWidth());
                maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
            }
        }
        setMeasuredDimension(maxWidth, maxHeight);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        int width = r - l;
        int height = b - t;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                child.layout(0, 0, width, height);
            }
        }
    }

    public MaterialBuilder material() {
        return new MaterialBuilder();
    }

    public CustomBuilder custom() {
        return new CustomBuilder();
    }

    /* loaded from: classes.dex */
    public class CustomBuilder {
        boolean enableVerticalLayout;
        List<BaseTabItem> items = new ArrayList();

        CustomBuilder() {
        }

        /* JADX WARN: Multi-variable type inference failed */
        public NavigationController build() {
            ItemController itemController;
            PageNavigationView.this.mEnableVerticalLayout = this.enableVerticalLayout;
            if (this.items.size() == 0) {
                return null;
            }
            if (this.enableVerticalLayout) {
                CustomItemVerticalLayout verticalItemLayout = new CustomItemVerticalLayout(PageNavigationView.this.getContext());
                verticalItemLayout.initialize(this.items);
                verticalItemLayout.setPadding(0, PageNavigationView.this.mTabPaddingTop, 0, PageNavigationView.this.mTabPaddingBottom);
                PageNavigationView.this.removeAllViews();
                PageNavigationView.this.addView(verticalItemLayout);
                itemController = verticalItemLayout;
            } else {
                CustomItemLayout customItemLayout = new CustomItemLayout(PageNavigationView.this.getContext());
                customItemLayout.initialize(this.items);
                customItemLayout.setPadding(0, PageNavigationView.this.mTabPaddingTop, 0, PageNavigationView.this.mTabPaddingBottom);
                PageNavigationView.this.removeAllViews();
                PageNavigationView.this.addView(customItemLayout);
                itemController = customItemLayout;
            }
            PageNavigationView pageNavigationView = PageNavigationView.this;
            pageNavigationView.mNavigationController = new NavigationController(new Controller(), itemController);
            PageNavigationView.this.mNavigationController.addTabItemSelectedListener(PageNavigationView.this.mTabItemListener);
            return PageNavigationView.this.mNavigationController;
        }

        public CustomBuilder addItem(BaseTabItem baseTabItem) {
            this.items.add(baseTabItem);
            return this;
        }

        public CustomBuilder enableVerticalLayout() {
            this.enableVerticalLayout = true;
            return this;
        }
    }

    /* loaded from: classes.dex */
    public class MaterialBuilder {
        int defaultColor;
        boolean enableVerticalLayout;
        List<ViewData> itemDatas = new ArrayList();
        int messageBackgroundColor;
        int messageNumberColor;
        int mode;

        MaterialBuilder() {
        }

        /* JADX WARN: Multi-variable type inference failed */
        public NavigationController build() {
            ItemController itemController;
            PageNavigationView.this.mEnableVerticalLayout = this.enableVerticalLayout;
            if (this.itemDatas.size() == 0) {
                return null;
            }
            if (this.defaultColor == 0) {
                this.defaultColor = 1442840576;
            }
            if (this.enableVerticalLayout) {
                List<BaseTabItem> items = new ArrayList<>();
                for (ViewData data : this.itemDatas) {
                    OnlyIconMaterialItemView materialItemView = new OnlyIconMaterialItemView(PageNavigationView.this.getContext());
                    materialItemView.initialization(data.title, data.drawable, data.checkedDrawable, this.defaultColor, data.chekedColor);
                    int i = this.messageBackgroundColor;
                    if (i != 0) {
                        materialItemView.setMessageBackgroundColor(i);
                    }
                    int i2 = this.messageNumberColor;
                    if (i2 != 0) {
                        materialItemView.setMessageNumberColor(i2);
                    }
                    items.add(materialItemView);
                }
                MaterialItemVerticalLayout materialItemVerticalLayout = new MaterialItemVerticalLayout(PageNavigationView.this.getContext());
                materialItemVerticalLayout.initialize(items);
                materialItemVerticalLayout.setPadding(0, PageNavigationView.this.mTabPaddingTop, 0, PageNavigationView.this.mTabPaddingBottom);
                PageNavigationView.this.removeAllViews();
                PageNavigationView.this.addView(materialItemVerticalLayout);
                itemController = materialItemVerticalLayout;
            } else {
                boolean changeBackground = (this.mode & 2) > 0;
                List<MaterialItemView> items2 = new ArrayList<>();
                List<Integer> checkedColors = new ArrayList<>();
                for (ViewData data2 : this.itemDatas) {
                    checkedColors.add(Integer.valueOf(data2.chekedColor));
                    MaterialItemView materialItemView2 = new MaterialItemView(PageNavigationView.this.getContext());
                    if (changeBackground) {
                        materialItemView2.initialization(data2.title, data2.drawable, data2.checkedDrawable, this.defaultColor, -1);
                    } else {
                        materialItemView2.initialization(data2.title, data2.drawable, data2.checkedDrawable, this.defaultColor, data2.chekedColor);
                    }
                    int i3 = this.messageBackgroundColor;
                    if (i3 != 0) {
                        materialItemView2.setMessageBackgroundColor(i3);
                    }
                    int i4 = this.messageNumberColor;
                    if (i4 != 0) {
                        materialItemView2.setMessageNumberColor(i4);
                    }
                    items2.add(materialItemView2);
                }
                MaterialItemLayout materialItemLayout = new MaterialItemLayout(PageNavigationView.this.getContext());
                materialItemLayout.initialize(items2, checkedColors, this.mode);
                materialItemLayout.setPadding(0, PageNavigationView.this.mTabPaddingTop, 0, PageNavigationView.this.mTabPaddingBottom);
                PageNavigationView.this.removeAllViews();
                PageNavigationView.this.addView(materialItemLayout);
                itemController = materialItemLayout;
            }
            PageNavigationView pageNavigationView = PageNavigationView.this;
            pageNavigationView.mNavigationController = new NavigationController(new Controller(), itemController);
            PageNavigationView.this.mNavigationController.addTabItemSelectedListener(PageNavigationView.this.mTabItemListener);
            return PageNavigationView.this.mNavigationController;
        }

        public MaterialBuilder addItem(@DrawableRes int drawableRes, String title) {
            addItem(drawableRes, drawableRes, title, Utils.getColorPrimary(PageNavigationView.this.getContext()));
            return this;
        }

        public MaterialBuilder addItem(@DrawableRes int drawableRes, @DrawableRes int checkedDrawableRes, String title) {
            addItem(drawableRes, checkedDrawableRes, title, Utils.getColorPrimary(PageNavigationView.this.getContext()));
            return this;
        }

        public MaterialBuilder addItem(@DrawableRes int drawableRes, String title, @ColorInt int chekedColor) {
            addItem(drawableRes, drawableRes, title, chekedColor);
            return this;
        }

        public MaterialBuilder addItem(@DrawableRes int drawableRes, @DrawableRes int checkedDrawableRes, String title, @ColorInt int chekedColor) {
            addItem(ContextCompat.getDrawable(PageNavigationView.this.getContext(), drawableRes), ContextCompat.getDrawable(PageNavigationView.this.getContext(), checkedDrawableRes), title, chekedColor);
            return this;
        }

        public MaterialBuilder addItem(Drawable drawable, String title) {
            addItem(drawable, Utils.newDrawable(drawable), title, Utils.getColorPrimary(PageNavigationView.this.getContext()));
            return this;
        }

        public MaterialBuilder addItem(Drawable drawable, Drawable checkedDrawable, String title) {
            addItem(drawable, checkedDrawable, title, Utils.getColorPrimary(PageNavigationView.this.getContext()));
            return this;
        }

        public MaterialBuilder addItem(Drawable drawable, String title, @ColorInt int chekedColor) {
            addItem(drawable, Utils.newDrawable(drawable), title, chekedColor);
            return this;
        }

        public MaterialBuilder addItem(Drawable drawable, Drawable checkedDrawable, String title, @ColorInt int chekedColor) {
            ViewData data = new ViewData();
            data.drawable = drawable;
            data.checkedDrawable = checkedDrawable;
            data.title = title;
            data.chekedColor = chekedColor;
            this.itemDatas.add(data);
            return this;
        }

        public MaterialBuilder setDefaultColor(@ColorInt int color) {
            this.defaultColor = color;
            return this;
        }

        public MaterialBuilder setMessageBackgroundColor(@ColorInt int color) {
            this.messageBackgroundColor = color;
            return this;
        }

        public MaterialBuilder setMessageNumberColor(@ColorInt int color) {
            this.messageNumberColor = color;
            return this;
        }

        public MaterialBuilder setMode(int mode) {
            this.mode = mode;
            return this;
        }

        public MaterialBuilder enableVerticalLayout() {
            this.enableVerticalLayout = true;
            return this;
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public class ViewData {
            Drawable checkedDrawable;
            @ColorInt
            int chekedColor;
            Drawable drawable;
            String title;

            private ViewData() {
            }
        }
    }

    /* loaded from: classes.dex */
    private class Controller implements BottomLayoutController {
        private ObjectAnimator animator;
        private boolean hide;

        private Controller() {
            this.hide = false;
        }

        @Override // me.majiajie.pagerbottomtabstrip.BottomLayoutController
        public void setupWithViewPager(ViewPager viewPager) {
            if (viewPager == null) {
                return;
            }
            PageNavigationView.this.mViewPager = viewPager;
            if (PageNavigationView.this.mPageChangeListener != null) {
                PageNavigationView.this.mViewPager.removeOnPageChangeListener(PageNavigationView.this.mPageChangeListener);
            } else {
                PageNavigationView pageNavigationView = PageNavigationView.this;
                pageNavigationView.mPageChangeListener = new ViewPagerPageChangeListener();
            }
            if (PageNavigationView.this.mNavigationController != null) {
                int n = PageNavigationView.this.mViewPager.getCurrentItem();
                if (PageNavigationView.this.mNavigationController.getSelected() != n) {
                    PageNavigationView.this.mNavigationController.setSelect(n);
                }
                PageNavigationView.this.mViewPager.addOnPageChangeListener(PageNavigationView.this.mPageChangeListener);
            }
        }

        @Override // me.majiajie.pagerbottomtabstrip.BottomLayoutController
        public void hideBottomLayout() {
            if (!this.hide) {
                this.hide = true;
                getAnimator().start();
            }
        }

        @Override // me.majiajie.pagerbottomtabstrip.BottomLayoutController
        public void showBottomLayout() {
            if (this.hide) {
                this.hide = false;
                getAnimator().reverse();
            }
        }

        private ObjectAnimator getAnimator() {
            if (this.animator == null) {
                if (PageNavigationView.this.mEnableVerticalLayout) {
                    PageNavigationView pageNavigationView = PageNavigationView.this;
                    this.animator = ObjectAnimator.ofFloat(pageNavigationView, "translationX", 0.0f, -pageNavigationView.getWidth());
                } else {
                    PageNavigationView pageNavigationView2 = PageNavigationView.this;
                    this.animator = ObjectAnimator.ofFloat(pageNavigationView2, "translationY", 0.0f, pageNavigationView2.getHeight());
                }
                this.animator.setDuration(300L);
                this.animator.setInterpolator(new AccelerateDecelerateInterpolator());
            }
            return this.animator;
        }
    }

    /* loaded from: classes.dex */
    private class ViewPagerPageChangeListener implements ViewPager.OnPageChangeListener {
        private ViewPagerPageChangeListener() {
        }

        @Override // android.support.v4.view.ViewPager.OnPageChangeListener
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override // android.support.v4.view.ViewPager.OnPageChangeListener
        public void onPageSelected(int position) {
            if (PageNavigationView.this.mNavigationController != null && PageNavigationView.this.mNavigationController.getSelected() != position) {
                PageNavigationView.this.mNavigationController.setSelect(position);
            }
        }

        @Override // android.support.v4.view.ViewPager.OnPageChangeListener
        public void onPageScrollStateChanged(int state) {
        }
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        if (this.mNavigationController == null) {
            return super.onSaveInstanceState();
        }
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATUS, super.onSaveInstanceState());
        bundle.putInt("STATUS_SELECTED", this.mNavigationController.getSelected());
        return bundle;
    }

    @Override // android.view.View
    protected void onRestoreInstanceState(Parcelable state) {
        NavigationController navigationController;
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            int selected = bundle.getInt("STATUS_SELECTED", 0);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATUS));
            if (selected != 0 && (navigationController = this.mNavigationController) != null) {
                navigationController.setSelect(selected);
                return;
            }
            return;
        }
        super.onRestoreInstanceState(state);
    }
}

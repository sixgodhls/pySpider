package com.goldze.mvvmhabit.databinding;

import android.databinding.DataBindingComponent;
import android.databinding.ObservableField;
import android.databinding.ViewDataBinding;
import android.databinding.adapters.TextViewBindingAdapter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import com.goldze.mvvmhabit.C0690R;
import com.goldze.mvvmhabit.entity.MovieEntity;
import com.goldze.mvvmhabit.p004ui.detail.DetailViewModel;
import me.goldze.mvvmhabit.binding.viewadapter.image.ViewAdapter;

/* loaded from: classes.dex */
public class FragmentDetailBindingImpl extends FragmentDetailBinding {
    @Nullable
    private static final ViewDataBinding.IncludedLayouts sIncludes = null;
    @Nullable
    private static final SparseIntArray sViewsWithIds = new SparseIntArray();
    private long mDirtyFlags;

    static {
        sViewsWithIds.put(C0690R.C0693id.categories_key, 8);
        sViewsWithIds.put(C0690R.C0693id.score_key, 9);
        sViewsWithIds.put(C0690R.C0693id.minute_key, 10);
        sViewsWithIds.put(C0690R.C0693id.published_at_key, 11);
        sViewsWithIds.put(C0690R.C0693id.drama_key, 12);
    }

    public FragmentDetailBindingImpl(@Nullable DataBindingComponent dataBindingComponent, @NonNull View view) {
        this(dataBindingComponent, view, mapBindings(dataBindingComponent, view, 13, sIncludes, sViewsWithIds));
    }

    private FragmentDetailBindingImpl(DataBindingComponent dataBindingComponent, View view, Object[] objArr) {
        super(dataBindingComponent, view, 1, (TextView) objArr[8], (TextView) objArr[3], (ImageView) objArr[1], (ScrollView) objArr[0], (TextView) objArr[12], (TextView) objArr[7], (TextView) objArr[10], (TextView) objArr[5], (TextView) objArr[11], (TextView) objArr[6], (TextView) objArr[9], (TextView) objArr[4], (TextView) objArr[2]);
        this.mDirtyFlags = -1L;
        this.categoriesValue.setTag(null);
        this.cover.setTag(null);
        this.detail.setTag(null);
        this.dramaValue.setTag(null);
        this.minuteValue.setTag(null);
        this.publishedAtValue.setTag(null);
        this.scoreValue.setTag(null);
        this.title.setTag(null);
        setRootTag(view);
        invalidateAll();
    }

    @Override // android.databinding.ViewDataBinding
    public void invalidateAll() {
        synchronized (this) {
            this.mDirtyFlags = 4L;
        }
        requestRebind();
    }

    @Override // android.databinding.ViewDataBinding
    public boolean hasPendingBindings() {
        synchronized (this) {
            return this.mDirtyFlags != 0;
        }
    }

    @Override // android.databinding.ViewDataBinding
    public boolean setVariable(int i, @Nullable Object obj) {
        if (2 == i) {
            setViewModel((DetailViewModel) obj);
            return true;
        }
        return false;
    }

    @Override // com.goldze.mvvmhabit.databinding.FragmentDetailBinding
    public void setViewModel(@Nullable DetailViewModel detailViewModel) {
        this.mViewModel = detailViewModel;
        synchronized (this) {
            this.mDirtyFlags |= 2;
        }
        notifyPropertyChanged(2);
        super.requestRebind();
    }

    @Override // android.databinding.ViewDataBinding
    protected boolean onFieldChange(int i, Object obj, int i2) {
        if (i != 0) {
            return false;
        }
        return onChangeViewModelEntity((ObservableField) obj, i2);
    }

    private boolean onChangeViewModelEntity(ObservableField<MovieEntity> observableField, int i) {
        if (i == 0) {
            synchronized (this) {
                this.mDirtyFlags |= 1;
            }
            return true;
        }
        return false;
    }

    @Override // android.databinding.ViewDataBinding
    protected void executeBindings() {
        long j;
        String str;
        String str2;
        String str3;
        String str4;
        String str5;
        String str6;
        String str7;
        String str8;
        String str9;
        synchronized (this) {
            j = this.mDirtyFlags;
            this.mDirtyFlags = 0L;
        }
        DetailViewModel detailViewModel = this.mViewModel;
        long j2 = j & 7;
        String str10 = null;
        if (j2 != 0) {
            ObservableField<MovieEntity> observableField = detailViewModel != null ? detailViewModel.entity : null;
            updateRegistration(0, observableField);
            MovieEntity movieEntity = observableField != null ? observableField.get() : null;
            if (movieEntity != null) {
                str10 = movieEntity.getPublishedAt();
                str2 = movieEntity.getCover();
                str3 = movieEntity.getDrama();
                str8 = movieEntity.getName();
                str5 = movieEntity.getScore();
                str9 = movieEntity.getCategories();
                str7 = movieEntity.getMinute();
            } else {
                str7 = null;
                str2 = null;
                str3 = null;
                str8 = null;
                str5 = null;
                str9 = null;
            }
            str = str7 + "分钟";
            String str11 = str8;
            str4 = str10;
            str10 = str9;
            str6 = str11;
        } else {
            str = null;
            str2 = null;
            str3 = null;
            str4 = null;
            str5 = null;
            str6 = null;
        }
        if (j2 != 0) {
            TextViewBindingAdapter.setText(this.categoriesValue, str10);
            ViewAdapter.setImageUri(this.cover, str2, C0690R.mipmap.ic_launcher_round);
            TextViewBindingAdapter.setText(this.dramaValue, str3);
            TextViewBindingAdapter.setText(this.minuteValue, str);
            TextViewBindingAdapter.setText(this.publishedAtValue, str4);
            TextViewBindingAdapter.setText(this.scoreValue, str5);
            TextViewBindingAdapter.setText(this.title, str6);
        }
    }
}
package android.databinding;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewStub;

/* loaded from: classes.dex */
public class ViewStubProxy {
    private ViewDataBinding mContainingBinding;
    private ViewStub.OnInflateListener mOnInflateListener;
    private ViewStub.OnInflateListener mProxyListener = new ViewStub.OnInflateListener() { // from class: android.databinding.ViewStubProxy.1
        @Override // android.view.ViewStub.OnInflateListener
        public void onInflate(ViewStub stub, View inflated) {
            ViewStubProxy.this.mRoot = inflated;
            ViewStubProxy viewStubProxy = ViewStubProxy.this;
            viewStubProxy.mViewDataBinding = DataBindingUtil.bind(viewStubProxy.mContainingBinding.mBindingComponent, inflated, stub.getLayoutResource());
            ViewStubProxy.this.mViewStub = null;
            if (ViewStubProxy.this.mOnInflateListener != null) {
                ViewStubProxy.this.mOnInflateListener.onInflate(stub, inflated);
                ViewStubProxy.this.mOnInflateListener = null;
            }
            ViewStubProxy.this.mContainingBinding.invalidateAll();
            ViewStubProxy.this.mContainingBinding.forceExecuteBindings();
        }
    };
    private View mRoot;
    private ViewDataBinding mViewDataBinding;
    private ViewStub mViewStub;

    public ViewStubProxy(@NonNull ViewStub viewStub) {
        this.mViewStub = viewStub;
        this.mViewStub.setOnInflateListener(this.mProxyListener);
    }

    public void setContainingBinding(@NonNull ViewDataBinding containingBinding) {
        this.mContainingBinding = containingBinding;
    }

    public boolean isInflated() {
        return this.mRoot != null;
    }

    public View getRoot() {
        return this.mRoot;
    }

    @Nullable
    public ViewDataBinding getBinding() {
        return this.mViewDataBinding;
    }

    @Nullable
    public ViewStub getViewStub() {
        return this.mViewStub;
    }

    public void setOnInflateListener(@Nullable ViewStub.OnInflateListener listener) {
        if (this.mViewStub != null) {
            this.mOnInflateListener = listener;
        }
    }
}

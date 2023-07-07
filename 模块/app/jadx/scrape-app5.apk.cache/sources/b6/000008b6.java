package me.goldze.mvvmhabit.base;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.p000v4.app.FragmentActivity;
import com.afollestad.materialdialogs.MaterialDialog;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.bus.Messenger;
import me.goldze.mvvmhabit.utils.MaterialDialogUtils;

/* loaded from: classes.dex */
public abstract class BaseActivity<V extends ViewDataBinding, VM extends BaseViewModel> extends RxAppCompatActivity implements IBaseView {
    protected V binding;
    private MaterialDialog dialog;
    protected VM viewModel;
    private int viewModelId;

    public abstract int initContentView(Bundle bundle);

    public abstract int initVariableId();

    @Override // com.trello.rxlifecycle2.components.support.RxAppCompatActivity, android.support.p003v7.app.AppCompatActivity, android.support.p000v4.app.FragmentActivity, android.support.p000v4.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initParam();
        initViewDataBinding(savedInstanceState);
        registorUIChangeLiveDataCallBack();
        initData();
        initViewObservable();
        this.viewModel.registerRxBus();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.trello.rxlifecycle2.components.support.RxAppCompatActivity, android.support.p003v7.app.AppCompatActivity, android.support.p000v4.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        Messenger.getDefault().unregister(this.viewModel);
        VM vm = this.viewModel;
        if (vm != null) {
            vm.removeRxBus();
        }
        V v = this.binding;
        if (v != null) {
            v.unbind();
        }
    }

    private void initViewDataBinding(Bundle savedInstanceState) {
        Class modelClass;
        this.binding = (V) DataBindingUtil.setContentView(this, initContentView(savedInstanceState));
        this.viewModelId = initVariableId();
        this.viewModel = initViewModel();
        if (this.viewModel == null) {
            Type type = getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                modelClass = (Class) ((ParameterizedType) type).getActualTypeArguments()[1];
            } else {
                modelClass = BaseViewModel.class;
            }
            this.viewModel = (VM) createViewModel(this, modelClass);
        }
        this.binding.setVariable(this.viewModelId, this.viewModel);
        this.binding.setLifecycleOwner(this);
        getLifecycle().addObserver(this.viewModel);
        this.viewModel.injectLifecycleProvider(this);
    }

    public void refreshLayout() {
        VM vm = this.viewModel;
        if (vm != null) {
            this.binding.setVariable(this.viewModelId, vm);
        }
    }

    protected void registorUIChangeLiveDataCallBack() {
        this.viewModel.getUC().getShowDialogEvent().observe(this, new Observer<String>() { // from class: me.goldze.mvvmhabit.base.BaseActivity.1
            {
                BaseActivity.this = this;
            }

            @Override // android.arch.lifecycle.Observer
            public void onChanged(@Nullable String title) {
                BaseActivity.this.showDialog(title);
            }
        });
        this.viewModel.getUC().getDismissDialogEvent().observe(this, new Observer<Void>() { // from class: me.goldze.mvvmhabit.base.BaseActivity.2
            {
                BaseActivity.this = this;
            }

            @Override // android.arch.lifecycle.Observer
            public void onChanged(@Nullable Void v) {
                BaseActivity.this.dismissDialog();
            }
        });
        this.viewModel.getUC().getStartActivityEvent().observe(this, new Observer<Map<String, Object>>() { // from class: me.goldze.mvvmhabit.base.BaseActivity.3
            {
                BaseActivity.this = this;
            }

            @Override // android.arch.lifecycle.Observer
            public void onChanged(@Nullable Map<String, Object> params) {
                Class<?> clz = (Class) params.get(BaseViewModel.ParameterField.CLASS);
                Bundle bundle = (Bundle) params.get(BaseViewModel.ParameterField.BUNDLE);
                BaseActivity.this.startActivity(clz, bundle);
            }
        });
        this.viewModel.getUC().getStartContainerActivityEvent().observe(this, new Observer<Map<String, Object>>() { // from class: me.goldze.mvvmhabit.base.BaseActivity.4
            {
                BaseActivity.this = this;
            }

            @Override // android.arch.lifecycle.Observer
            public void onChanged(@Nullable Map<String, Object> params) {
                String canonicalName = (String) params.get(BaseViewModel.ParameterField.CANONICAL_NAME);
                Bundle bundle = (Bundle) params.get(BaseViewModel.ParameterField.BUNDLE);
                BaseActivity.this.startContainerActivity(canonicalName, bundle);
            }
        });
        this.viewModel.getUC().getFinishEvent().observe(this, new Observer<Void>() { // from class: me.goldze.mvvmhabit.base.BaseActivity.5
            {
                BaseActivity.this = this;
            }

            @Override // android.arch.lifecycle.Observer
            public void onChanged(@Nullable Void v) {
                BaseActivity.this.finish();
            }
        });
        this.viewModel.getUC().getOnBackPressedEvent().observe(this, new Observer<Void>() { // from class: me.goldze.mvvmhabit.base.BaseActivity.6
            {
                BaseActivity.this = this;
            }

            @Override // android.arch.lifecycle.Observer
            public void onChanged(@Nullable Void v) {
                BaseActivity.this.onBackPressed();
            }
        });
    }

    public void showDialog(String title) {
        MaterialDialog materialDialog = this.dialog;
        if (materialDialog != null) {
            this.dialog = materialDialog.getBuilder().title(title).build();
            this.dialog.show();
            return;
        }
        MaterialDialog.Builder builder = MaterialDialogUtils.showIndeterminateProgressDialog(this, title, true);
        this.dialog = builder.show();
    }

    public void dismissDialog() {
        MaterialDialog materialDialog = this.dialog;
        if (materialDialog != null && materialDialog.isShowing()) {
            this.dialog.dismiss();
        }
    }

    public void startActivity(Class<?> clz) {
        startActivity(new Intent(this, clz));
    }

    public void startActivity(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    public void startContainerActivity(String canonicalName) {
        startContainerActivity(canonicalName, null);
    }

    public void startContainerActivity(String canonicalName, Bundle bundle) {
        Intent intent = new Intent(this, ContainerActivity.class);
        intent.putExtra(ContainerActivity.FRAGMENT, canonicalName);
        if (bundle != null) {
            intent.putExtra(ContainerActivity.BUNDLE, bundle);
        }
        startActivity(intent);
    }

    public void initParam() {
    }

    public VM initViewModel() {
        return null;
    }

    @Override // me.goldze.mvvmhabit.base.IBaseView
    public void initData() {
    }

    @Override // me.goldze.mvvmhabit.base.IBaseView
    public void initViewObservable() {
    }

    public <T extends ViewModel> T createViewModel(FragmentActivity activity, Class<T> cls) {
        return (T) ViewModelProviders.m59of(activity).get(cls);
    }
}
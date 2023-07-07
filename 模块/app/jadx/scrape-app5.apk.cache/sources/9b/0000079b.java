package me.goldze.mvvmhabit.base;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.p000v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.afollestad.materialdialogs.MaterialDialog;
import com.trello.rxlifecycle2.components.support.RxFragment;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.bus.Messenger;
import me.goldze.mvvmhabit.utils.MaterialDialogUtils;

/* loaded from: classes.dex */
public abstract class BaseFragment<V extends ViewDataBinding, VM extends BaseViewModel> extends RxFragment implements IBaseView {
    protected V binding;
    private MaterialDialog dialog;
    protected VM viewModel;
    private int viewModelId;

    public abstract int initContentView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle);

    public abstract int initVariableId();

    @Override // com.trello.rxlifecycle2.components.support.RxFragment, android.support.p000v4.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initParam();
    }

    @Override // com.trello.rxlifecycle2.components.support.RxFragment, android.support.p000v4.app.Fragment
    public void onDestroy() {
        super.onDestroy();
    }

    @Override // android.support.p000v4.app.Fragment
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.binding = (V) DataBindingUtil.inflate(inflater, initContentView(inflater, container, savedInstanceState), container, false);
        return this.binding.getRoot();
    }

    @Override // com.trello.rxlifecycle2.components.support.RxFragment, android.support.p000v4.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
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

    @Override // com.trello.rxlifecycle2.components.support.RxFragment, android.support.p000v4.app.Fragment
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewDataBinding();
        registorUIChangeLiveDataCallBack();
        initData();
        initViewObservable();
        this.viewModel.registerRxBus();
    }

    private void initViewDataBinding() {
        Class modelClass;
        this.viewModelId = initVariableId();
        this.viewModel = mo307initViewModel();
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

    protected void registorUIChangeLiveDataCallBack() {
        this.viewModel.getUC().getShowDialogEvent().observe(this, new Observer<String>() { // from class: me.goldze.mvvmhabit.base.BaseFragment.1
            @Override // android.arch.lifecycle.Observer
            public void onChanged(@Nullable String title) {
                BaseFragment.this.showDialog(title);
            }
        });
        this.viewModel.getUC().getDismissDialogEvent().observe(this, new Observer<Void>() { // from class: me.goldze.mvvmhabit.base.BaseFragment.2
            @Override // android.arch.lifecycle.Observer
            public void onChanged(@Nullable Void v) {
                BaseFragment.this.dismissDialog();
            }
        });
        this.viewModel.getUC().getStartActivityEvent().observe(this, new Observer<Map<String, Object>>() { // from class: me.goldze.mvvmhabit.base.BaseFragment.3
            @Override // android.arch.lifecycle.Observer
            public void onChanged(@Nullable Map<String, Object> params) {
                Class<?> clz = (Class) params.get(BaseViewModel.ParameterField.CLASS);
                Bundle bundle = (Bundle) params.get(BaseViewModel.ParameterField.BUNDLE);
                BaseFragment.this.startActivity(clz, bundle);
            }
        });
        this.viewModel.getUC().getStartContainerActivityEvent().observe(this, new Observer<Map<String, Object>>() { // from class: me.goldze.mvvmhabit.base.BaseFragment.4
            @Override // android.arch.lifecycle.Observer
            public void onChanged(@Nullable Map<String, Object> params) {
                String canonicalName = (String) params.get(BaseViewModel.ParameterField.CANONICAL_NAME);
                Bundle bundle = (Bundle) params.get(BaseViewModel.ParameterField.BUNDLE);
                BaseFragment.this.startContainerActivity(canonicalName, bundle);
            }
        });
        this.viewModel.getUC().getFinishEvent().observe(this, new Observer<Void>() { // from class: me.goldze.mvvmhabit.base.BaseFragment.5
            @Override // android.arch.lifecycle.Observer
            public void onChanged(@Nullable Void v) {
                BaseFragment.this.getActivity().finish();
            }
        });
        this.viewModel.getUC().getOnBackPressedEvent().observe(this, new Observer<Void>() { // from class: me.goldze.mvvmhabit.base.BaseFragment.6
            @Override // android.arch.lifecycle.Observer
            public void onChanged(@Nullable Void v) {
                BaseFragment.this.getActivity().onBackPressed();
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
        MaterialDialog.Builder builder = MaterialDialogUtils.showIndeterminateProgressDialog(getActivity(), title, true);
        this.dialog = builder.show();
    }

    public void dismissDialog() {
        MaterialDialog materialDialog = this.dialog;
        if (materialDialog != null && materialDialog.isShowing()) {
            this.dialog.dismiss();
        }
    }

    public void startActivity(Class<?> clz) {
        startActivity(new Intent(getContext(), clz));
    }

    public void startActivity(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent(getContext(), clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    public void startContainerActivity(String canonicalName) {
        startContainerActivity(canonicalName, null);
    }

    public void startContainerActivity(String canonicalName, Bundle bundle) {
        Intent intent = new Intent(getContext(), ContainerActivity.class);
        intent.putExtra(ContainerActivity.FRAGMENT, canonicalName);
        if (bundle != null) {
            intent.putExtra(ContainerActivity.BUNDLE, bundle);
        }
        startActivity(intent);
    }

    public void refreshLayout() {
        VM vm = this.viewModel;
        if (vm != null) {
            this.binding.setVariable(this.viewModelId, vm);
        }
    }

    public void initParam() {
    }

    /* renamed from: initViewModel */
    public VM mo307initViewModel() {
        return null;
    }

    public void initData() {
    }

    public void initViewObservable() {
    }

    public boolean isBackPressed() {
        return false;
    }

    public <T extends ViewModel> T createViewModel(Fragment fragment, Class<T> cls) {
        return (T) ViewModelProviders.m61of(fragment).get(cls);
    }
}
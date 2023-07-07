package com.goldze.mvvmhabit.ui.index;

import android.annotation.SuppressLint;
import android.app.Application;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.annotation.NonNull;
import com.goldze.mvvmhabit.R;
import com.goldze.mvvmhabit.data.MainRepository;
import com.goldze.mvvmhabit.data.source.HttpResponse;
import com.goldze.mvvmhabit.entity.MovieEntity;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.tatarka.bindingcollectionadapter2.ItemBinding;

/* loaded from: classes.dex */
public class IndexViewModel extends BaseViewModel<MainRepository> {
    public SingleLiveEvent<IndexItemViewModel> deleteItemLiveData = new SingleLiveEvent<>();
    public UIChangeObservable uc = new UIChangeObservable();
    private int page = 1;
    private int limit = 10;
    private int count = this.limit;
    public ObservableList<IndexItemViewModel> observableList = new ObservableArrayList();
    public ItemBinding<IndexItemViewModel> itemBinding = ItemBinding.of(2, R.layout.item);
    public BindingCommand onRefreshCommand = new BindingCommand(new BindingAction() { // from class: com.goldze.mvvmhabit.ui.index.IndexViewModel.1
        @Override // me.goldze.mvvmhabit.binding.command.BindingAction
        public void call() {
            IndexViewModel.this.requestNetWork();
        }
    });
    public BindingCommand onLoadMoreCommand = new BindingCommand(new BindingAction() { // from class: com.goldze.mvvmhabit.ui.index.IndexViewModel.2
        @Override // me.goldze.mvvmhabit.binding.command.BindingAction
        public void call() {
            if (IndexViewModel.this.page > IndexViewModel.this.count / IndexViewModel.this.limit) {
                IndexViewModel.this.uc.finishLoadMore.call();
            } else {
                IndexViewModel.this.requestNetWork();
            }
        }
    });

    /* loaded from: classes.dex */
    public class UIChangeObservable {
        public SingleLiveEvent finishRefreshing = new SingleLiveEvent();
        public SingleLiveEvent finishLoadMore = new SingleLiveEvent();

        public UIChangeObservable() {
        }
    }

    public IndexViewModel(@NonNull Application application, MainRepository mainRepository) {
        super(application, mainRepository);
    }

    @SuppressLint({"CheckResult"})
    public void requestNetWork() {
        ((MainRepository) this.model).index(this.page, this.limit).compose(RxUtils.schedulersTransformer()).compose(RxUtils.exceptionTransformer()).doOnSubscribe(this).doOnSubscribe(new Consumer<Disposable>() { // from class: com.goldze.mvvmhabit.ui.index.IndexViewModel.6
            @Override // io.reactivex.functions.Consumer
            public void accept(Disposable disposable) throws Exception {
            }
        }).subscribe(new Consumer<HttpResponse<MovieEntity>>() { // from class: com.goldze.mvvmhabit.ui.index.IndexViewModel.3
            @Override // io.reactivex.functions.Consumer
            public void accept(HttpResponse<MovieEntity> httpResponse) throws Exception {
                if (httpResponse.getCount() > 0) {
                    IndexViewModel.this.count = httpResponse.getCount();
                }
                if (httpResponse.getResults().size() > 0) {
                    for (MovieEntity movieEntity : httpResponse.getResults()) {
                        IndexViewModel.this.observableList.add(new IndexItemViewModel(IndexViewModel.this, movieEntity));
                    }
                    IndexViewModel.this.page++;
                    IndexViewModel.this.uc.finishLoadMore.call();
                    return;
                }
                IndexViewModel.this.uc.finishLoadMore.call();
                ToastUtils.showShort("数据错误");
            }
        }, new Consumer<Throwable>() { // from class: com.goldze.mvvmhabit.ui.index.IndexViewModel.4
            @Override // io.reactivex.functions.Consumer
            public void accept(Throwable th) throws Exception {
                IndexViewModel.this.uc.finishLoadMore.call();
                if (th instanceof ResponseThrowable) {
                    ToastUtils.showShort(((ResponseThrowable) th).message);
                }
            }
        }, new Action() { // from class: com.goldze.mvvmhabit.ui.index.IndexViewModel.5
            @Override // io.reactivex.functions.Action
            public void run() throws Exception {
                IndexViewModel.this.uc.finishLoadMore.call();
            }
        });
    }

    public int getItemPosition(IndexItemViewModel indexItemViewModel) {
        return this.observableList.indexOf(indexItemViewModel);
    }

    @Override // me.goldze.mvvmhabit.base.BaseViewModel, me.goldze.mvvmhabit.base.IBaseViewModel
    public void onDestroy() {
        super.onDestroy();
    }
}

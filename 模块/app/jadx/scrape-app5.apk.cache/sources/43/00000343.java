package me.goldze.mvvmhabit.binding.command;

import io.reactivex.functions.Function;

/* loaded from: classes.dex */
public class ResponseCommand<T, R> {
    private BindingFunction<Boolean> canExecute;
    private BindingFunction<R> execute;
    private Function<T, R> function;

    public ResponseCommand(BindingFunction<R> execute) {
        this.execute = execute;
    }

    public ResponseCommand(Function<T, R> execute) {
        this.function = execute;
    }

    public ResponseCommand(BindingFunction<R> execute, BindingFunction<Boolean> canExecute) {
        this.execute = execute;
        this.canExecute = canExecute;
    }

    public ResponseCommand(Function<T, R> execute, BindingFunction<Boolean> canExecute) {
        this.function = execute;
        this.canExecute = canExecute;
    }

    public R execute() {
        if (this.execute != null && canExecute()) {
            return this.execute.call();
        }
        return null;
    }

    private boolean canExecute() {
        BindingFunction<Boolean> bindingFunction = this.canExecute;
        if (bindingFunction == null) {
            return true;
        }
        return bindingFunction.call().booleanValue();
    }

    public R execute(T parameter) throws Exception {
        if (this.function != null && canExecute()) {
            return this.function.mo401apply(parameter);
        }
        return null;
    }
}
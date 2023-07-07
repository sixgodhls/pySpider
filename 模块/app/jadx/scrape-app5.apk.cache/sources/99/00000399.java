package me.goldze.mvvmhabit.binding.command;

/* loaded from: classes.dex */
public class BindingCommand<T> {
    private BindingFunction<Boolean> canExecute0;
    private BindingConsumer<T> consumer;
    private BindingAction execute;

    public BindingCommand(BindingAction execute) {
        this.execute = execute;
    }

    public BindingCommand(BindingConsumer<T> execute) {
        this.consumer = execute;
    }

    public BindingCommand(BindingAction execute, BindingFunction<Boolean> canExecute0) {
        this.execute = execute;
        this.canExecute0 = canExecute0;
    }

    public BindingCommand(BindingConsumer<T> execute, BindingFunction<Boolean> canExecute0) {
        this.consumer = execute;
        this.canExecute0 = canExecute0;
    }

    public void execute() {
        if (this.execute != null && canExecute0()) {
            this.execute.call();
        }
    }

    public void execute(T parameter) {
        if (this.consumer != null && canExecute0()) {
            this.consumer.call(parameter);
        }
    }

    private boolean canExecute0() {
        BindingFunction<Boolean> bindingFunction = this.canExecute0;
        if (bindingFunction == null) {
            return true;
        }
        return bindingFunction.call().booleanValue();
    }
}
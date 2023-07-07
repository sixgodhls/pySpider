package me.goldze.mvvmhabit.bus;

import java.lang.ref.WeakReference;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;

/* loaded from: classes.dex */
public class WeakAction<T> {
    private BindingAction action;
    private BindingConsumer<T> consumer;
    private boolean isLive;
    private WeakReference reference;
    private Object target;

    public WeakAction(Object target, BindingAction action) {
        this.reference = new WeakReference(target);
        this.action = action;
    }

    public WeakAction(Object target, BindingConsumer<T> consumer) {
        this.reference = new WeakReference(target);
        this.consumer = consumer;
    }

    public void execute() {
        if (this.action != null && isLive()) {
            this.action.call();
        }
    }

    public void execute(T parameter) {
        if (this.consumer != null && isLive()) {
            this.consumer.call(parameter);
        }
    }

    public void markForDeletion() {
        this.reference.clear();
        this.reference = null;
        this.action = null;
        this.consumer = null;
    }

    public BindingAction getBindingAction() {
        return this.action;
    }

    public BindingConsumer getBindingConsumer() {
        return this.consumer;
    }

    public boolean isLive() {
        WeakReference weakReference = this.reference;
        return (weakReference == null || weakReference.get() == null) ? false : true;
    }

    public Object getTarget() {
        WeakReference weakReference = this.reference;
        if (weakReference != null) {
            return weakReference.get();
        }
        return null;
    }
}
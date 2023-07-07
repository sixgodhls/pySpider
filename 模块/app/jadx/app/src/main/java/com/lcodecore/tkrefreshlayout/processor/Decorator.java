package com.lcodecore.tkrefreshlayout.processor;

import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

/* loaded from: classes.dex */
public abstract class Decorator implements IDecorator {
    protected TwinklingRefreshLayout.CoContext cp;
    protected IDecorator decorator;

    public Decorator(TwinklingRefreshLayout.CoContext processor, IDecorator decorator1) {
        this.cp = processor;
        this.decorator = decorator1;
    }
}

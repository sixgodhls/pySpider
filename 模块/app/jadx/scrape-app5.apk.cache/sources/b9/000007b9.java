package com.lcodecore.tkrefreshlayout.processor;

import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

/* loaded from: classes.dex */
public abstract class Decorator implements IDecorator {

    /* renamed from: cp */
    protected TwinklingRefreshLayout.CoContext f85cp;
    protected IDecorator decorator;

    public Decorator(TwinklingRefreshLayout.CoContext processor, IDecorator decorator1) {
        this.f85cp = processor;
        this.decorator = decorator1;
    }
}
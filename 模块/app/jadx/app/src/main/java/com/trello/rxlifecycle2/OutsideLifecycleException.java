package com.trello.rxlifecycle2;

import javax.annotation.Nullable;

/* loaded from: classes.dex */
public class OutsideLifecycleException extends IllegalStateException {
    public OutsideLifecycleException(@Nullable String detailMessage) {
        super(detailMessage);
    }
}

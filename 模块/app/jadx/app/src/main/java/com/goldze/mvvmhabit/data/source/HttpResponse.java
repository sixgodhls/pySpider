package com.goldze.mvvmhabit.data.source;

import java.util.List;

/* loaded from: classes.dex */
public class HttpResponse<T> {
    private int count;
    private List<T> results;

    public List<T> getResults() {
        return this.results;
    }

    public int getCount() {
        return this.count;
    }
}

package com.bumptech.glide.manager;

import android.support.annotation.NonNull;
import com.bumptech.glide.RequestManager;
import java.util.Set;

/* loaded from: classes.dex */
public interface RequestManagerTreeNode {
    @NonNull
    Set<RequestManager> getDescendants();
}
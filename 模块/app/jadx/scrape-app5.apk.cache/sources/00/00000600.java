package com.bumptech.glide.load.engine;

import com.bumptech.glide.load.Key;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public interface EngineJobListener {
    void onEngineJobCancelled(EngineJob<?> engineJob, Key key);

    void onEngineJobComplete(EngineJob<?> engineJob, Key key, EngineResource<?> engineResource);
}
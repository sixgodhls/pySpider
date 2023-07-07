package com.bumptech.glide.gifdecoder;

import android.support.annotation.ColorInt;

/* loaded from: classes.dex */
class GifFrame {
    static final int DISPOSAL_BACKGROUND = 2;
    static final int DISPOSAL_NONE = 1;
    static final int DISPOSAL_PREVIOUS = 3;
    static final int DISPOSAL_UNSPECIFIED = 0;
    int bufferFrameStart;
    int delay;
    int dispose;

    /* renamed from: ih */
    int f46ih;
    boolean interlace;

    /* renamed from: iw */
    int f47iw;

    /* renamed from: ix */
    int f48ix;

    /* renamed from: iy */
    int f49iy;
    @ColorInt
    int[] lct;
    int transIndex;
    boolean transparency;
}
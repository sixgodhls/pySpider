package com.bumptech.glide.gifdecoder;

import android.graphics.Bitmap;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import com.bumptech.glide.gifdecoder.GifDecoder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Iterator;

/* loaded from: classes.dex */
public class StandardGifDecoder implements GifDecoder {
    private static final int BYTES_PER_INTEGER = 4;
    @ColorInt
    private static final int COLOR_TRANSPARENT_BLACK = 0;
    private static final int INITIAL_FRAME_POINTER = -1;
    private static final int MASK_INT_LOWEST_BYTE = 255;
    private static final int MAX_STACK_SIZE = 4096;
    private static final int NULL_CODE = -1;
    private static final String TAG = StandardGifDecoder.class.getSimpleName();
    @ColorInt
    private int[] act;
    @NonNull
    private Bitmap.Config bitmapConfig;
    private final GifDecoder.BitmapProvider bitmapProvider;
    private byte[] block;
    private int downsampledHeight;
    private int downsampledWidth;
    private int framePointer;
    private GifHeader header;
    @Nullable
    private Boolean isFirstFrameTransparent;
    private byte[] mainPixels;
    @ColorInt
    private int[] mainScratch;
    private GifHeaderParser parser;
    @ColorInt
    private final int[] pct;
    private byte[] pixelStack;
    private short[] prefix;
    private Bitmap previousImage;
    private ByteBuffer rawData;
    private int sampleSize;
    private boolean savePrevious;
    private int status;
    private byte[] suffix;

    public StandardGifDecoder(@NonNull GifDecoder.BitmapProvider provider, GifHeader gifHeader, ByteBuffer rawData) {
        this(provider, gifHeader, rawData, 1);
    }

    public StandardGifDecoder(@NonNull GifDecoder.BitmapProvider provider, GifHeader gifHeader, ByteBuffer rawData, int sampleSize) {
        this(provider);
        setData(gifHeader, rawData, sampleSize);
    }

    public StandardGifDecoder(@NonNull GifDecoder.BitmapProvider provider) {
        this.pct = new int[256];
        this.bitmapConfig = Bitmap.Config.ARGB_8888;
        this.bitmapProvider = provider;
        this.header = new GifHeader();
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    public int getWidth() {
        return this.header.width;
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    public int getHeight() {
        return this.header.height;
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    @NonNull
    public ByteBuffer getData() {
        return this.rawData;
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    public int getStatus() {
        return this.status;
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    public void advance() {
        this.framePointer = (this.framePointer + 1) % this.header.frameCount;
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    public int getDelay(int n) {
        if (n < 0 || n >= this.header.frameCount) {
            return -1;
        }
        int delay = this.header.frames.get(n).delay;
        return delay;
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    public int getNextDelay() {
        int i;
        if (this.header.frameCount <= 0 || (i = this.framePointer) < 0) {
            return 0;
        }
        return getDelay(i);
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    public int getFrameCount() {
        return this.header.frameCount;
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    public int getCurrentFrameIndex() {
        return this.framePointer;
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    public void resetFrameIndex() {
        this.framePointer = -1;
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    @Deprecated
    public int getLoopCount() {
        if (this.header.loopCount == -1) {
            return 1;
        }
        return this.header.loopCount;
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    public int getNetscapeLoopCount() {
        return this.header.loopCount;
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    public int getTotalIterationCount() {
        if (this.header.loopCount == -1) {
            return 1;
        }
        if (this.header.loopCount == 0) {
            return 0;
        }
        return this.header.loopCount + 1;
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    public int getByteSize() {
        return this.rawData.limit() + this.mainPixels.length + (this.mainScratch.length * 4);
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    @Nullable
    public synchronized Bitmap getNextFrame() {
        if (this.header.frameCount <= 0 || this.framePointer < 0) {
            if (Log.isLoggable(TAG, 3)) {
                String str = TAG;
                Log.d(str, "Unable to decode frame, frameCount=" + this.header.frameCount + ", framePointer=" + this.framePointer);
            }
            this.status = 1;
        }
        if (this.status != 1 && this.status != 2) {
            this.status = 0;
            if (this.block == null) {
                this.block = this.bitmapProvider.obtainByteArray(255);
            }
            GifFrame currentFrame = this.header.frames.get(this.framePointer);
            GifFrame previousFrame = null;
            int previousIndex = this.framePointer - 1;
            if (previousIndex >= 0) {
                previousFrame = this.header.frames.get(previousIndex);
            }
            this.act = currentFrame.lct != null ? currentFrame.lct : this.header.gct;
            if (this.act == null) {
                if (Log.isLoggable(TAG, 3)) {
                    String str2 = TAG;
                    Log.d(str2, "No valid color table found for frame #" + this.framePointer);
                }
                this.status = 1;
                return null;
            }
            if (currentFrame.transparency) {
                System.arraycopy(this.act, 0, this.pct, 0, this.act.length);
                this.act = this.pct;
                this.act[currentFrame.transIndex] = 0;
            }
            return setPixels(currentFrame, previousFrame);
        }
        if (Log.isLoggable(TAG, 3)) {
            String str3 = TAG;
            Log.d(str3, "Unable to decode frame, status=" + this.status);
        }
        return null;
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    public int read(@Nullable InputStream is, int contentLength) {
        if (is != null) {
            int capacity = contentLength > 0 ? contentLength + 4096 : 16384;
            try {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream(capacity);
                byte[] data = new byte[16384];
                while (true) {
                    int nRead = is.read(data, 0, data.length);
                    if (nRead == -1) {
                        break;
                    }
                    buffer.write(data, 0, nRead);
                }
                buffer.flush();
                read(buffer.toByteArray());
            } catch (IOException e) {
                Log.w(TAG, "Error reading data from stream", e);
            }
        } else {
            this.status = 2;
        }
        if (is != null) {
            try {
                is.close();
            } catch (IOException e2) {
                Log.w(TAG, "Error closing stream", e2);
            }
        }
        return this.status;
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    public void clear() {
        this.header = null;
        byte[] bArr = this.mainPixels;
        if (bArr != null) {
            this.bitmapProvider.release(bArr);
        }
        int[] iArr = this.mainScratch;
        if (iArr != null) {
            this.bitmapProvider.release(iArr);
        }
        Bitmap bitmap = this.previousImage;
        if (bitmap != null) {
            this.bitmapProvider.release(bitmap);
        }
        this.previousImage = null;
        this.rawData = null;
        this.isFirstFrameTransparent = null;
        byte[] bArr2 = this.block;
        if (bArr2 != null) {
            this.bitmapProvider.release(bArr2);
        }
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    public synchronized void setData(@NonNull GifHeader header, @NonNull byte[] data) {
        setData(header, ByteBuffer.wrap(data));
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    public synchronized void setData(@NonNull GifHeader header, @NonNull ByteBuffer buffer) {
        setData(header, buffer, 1);
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    public synchronized void setData(@NonNull GifHeader header, @NonNull ByteBuffer buffer, int sampleSize) {
        if (sampleSize <= 0) {
            throw new IllegalArgumentException("Sample size must be >=0, not: " + sampleSize);
        }
        int sampleSize2 = Integer.highestOneBit(sampleSize);
        this.status = 0;
        this.header = header;
        this.framePointer = -1;
        this.rawData = buffer.asReadOnlyBuffer();
        this.rawData.position(0);
        this.rawData.order(ByteOrder.LITTLE_ENDIAN);
        this.savePrevious = false;
        Iterator<GifFrame> it = header.frames.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            GifFrame frame = it.next();
            if (frame.dispose == 3) {
                this.savePrevious = true;
                break;
            }
        }
        this.sampleSize = sampleSize2;
        this.downsampledWidth = header.width / sampleSize2;
        this.downsampledHeight = header.height / sampleSize2;
        this.mainPixels = this.bitmapProvider.obtainByteArray(header.width * header.height);
        this.mainScratch = this.bitmapProvider.obtainIntArray(this.downsampledWidth * this.downsampledHeight);
    }

    @NonNull
    private GifHeaderParser getHeaderParser() {
        if (this.parser == null) {
            this.parser = new GifHeaderParser();
        }
        return this.parser;
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    public synchronized int read(@Nullable byte[] data) {
        this.header = getHeaderParser().setData(data).parseHeader();
        if (data != null) {
            setData(this.header, data);
        }
        return this.status;
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    public void setDefaultBitmapConfig(@NonNull Bitmap.Config config) {
        if (config != Bitmap.Config.ARGB_8888 && config != Bitmap.Config.RGB_565) {
            throw new IllegalArgumentException("Unsupported format: " + config + ", must be one of " + Bitmap.Config.ARGB_8888 + " or " + Bitmap.Config.RGB_565);
        }
        this.bitmapConfig = config;
    }

    private Bitmap setPixels(GifFrame currentFrame, GifFrame previousFrame) {
        Bitmap bitmap;
        int[] dest = this.mainScratch;
        if (previousFrame == null) {
            Bitmap bitmap2 = this.previousImage;
            if (bitmap2 != null) {
                this.bitmapProvider.release(bitmap2);
            }
            this.previousImage = null;
            Arrays.fill(dest, 0);
        }
        if (previousFrame != null && previousFrame.dispose == 3 && this.previousImage == null) {
            Arrays.fill(dest, 0);
        }
        if (previousFrame != null && previousFrame.dispose > 0) {
            if (previousFrame.dispose != 2) {
                if (previousFrame.dispose == 3 && (bitmap = this.previousImage) != null) {
                    int i = this.downsampledWidth;
                    bitmap.getPixels(dest, 0, i, 0, 0, i, this.downsampledHeight);
                }
            } else {
                int c = 0;
                if (!currentFrame.transparency) {
                    c = this.header.bgColor;
                    if (currentFrame.lct != null && this.header.bgIndex == currentFrame.transIndex) {
                        c = 0;
                    }
                } else if (this.framePointer == 0) {
                    this.isFirstFrameTransparent = true;
                }
                int downsampledIH = previousFrame.ih / this.sampleSize;
                int downsampledIY = previousFrame.iy / this.sampleSize;
                int downsampledIW = previousFrame.iw / this.sampleSize;
                int downsampledIX = previousFrame.ix / this.sampleSize;
                int i2 = this.downsampledWidth;
                int topLeft = (downsampledIY * i2) + downsampledIX;
                int bottomLeft = (i2 * downsampledIH) + topLeft;
                int left = topLeft;
                while (left < bottomLeft) {
                    int right = left + downsampledIW;
                    for (int pointer = left; pointer < right; pointer++) {
                        dest[pointer] = c;
                    }
                    int right2 = this.downsampledWidth;
                    left += right2;
                }
            }
        }
        decodeBitmapData(currentFrame);
        if (currentFrame.interlace || this.sampleSize != 1) {
            copyCopyIntoScratchRobust(currentFrame);
        } else {
            copyIntoScratchFast(currentFrame);
        }
        if (this.savePrevious && (currentFrame.dispose == 0 || currentFrame.dispose == 1)) {
            if (this.previousImage == null) {
                this.previousImage = getNextBitmap();
            }
            Bitmap bitmap3 = this.previousImage;
            int i3 = this.downsampledWidth;
            bitmap3.setPixels(dest, 0, i3, 0, 0, i3, this.downsampledHeight);
        }
        Bitmap result = getNextBitmap();
        int i4 = this.downsampledWidth;
        result.setPixels(dest, 0, i4, 0, 0, i4, this.downsampledHeight);
        return result;
    }

    private void copyIntoScratchFast(GifFrame currentFrame) {
        GifFrame gifFrame = currentFrame;
        int[] dest = this.mainScratch;
        int downsampledIH = gifFrame.ih;
        int downsampledIY = gifFrame.iy;
        int downsampledIW = gifFrame.iw;
        int downsampledIX = gifFrame.ix;
        boolean isFirstFrame = this.framePointer == 0;
        int width = this.downsampledWidth;
        byte[] mainPixels = this.mainPixels;
        int[] act = this.act;
        byte transparentColorIndex = -1;
        int i = 0;
        while (i < downsampledIH) {
            int line = i + downsampledIY;
            int k = line * width;
            int dx = k + downsampledIX;
            int dlim = dx + downsampledIW;
            if (k + width < dlim) {
                dlim = k + width;
            }
            byte transparentColorIndex2 = transparentColorIndex;
            int sx = gifFrame.iw * i;
            int dx2 = dx;
            while (dx2 < dlim) {
                int downsampledIH2 = downsampledIH;
                byte downsampledIH3 = mainPixels[sx];
                int downsampledIY2 = downsampledIY;
                int downsampledIY3 = downsampledIH3 & 255;
                if (downsampledIY3 != transparentColorIndex2) {
                    int color = act[downsampledIY3];
                    if (color != 0) {
                        dest[dx2] = color;
                    } else {
                        transparentColorIndex2 = downsampledIH3;
                    }
                }
                sx++;
                dx2++;
                downsampledIH = downsampledIH2;
                downsampledIY = downsampledIY2;
            }
            i++;
            transparentColorIndex = transparentColorIndex2;
            gifFrame = currentFrame;
        }
        this.isFirstFrameTransparent = Boolean.valueOf(this.isFirstFrameTransparent == null && isFirstFrame && transparentColorIndex != -1);
    }

    private void copyCopyIntoScratchRobust(GifFrame currentFrame) {
        int downsampledIY;
        int downsampledIW;
        int downsampledIX;
        int[] dest = this.mainScratch;
        int downsampledIH = currentFrame.ih / this.sampleSize;
        int downsampledIY2 = currentFrame.iy / this.sampleSize;
        int downsampledIW2 = currentFrame.iw / this.sampleSize;
        int downsampledIX2 = currentFrame.ix / this.sampleSize;
        int iline = 0;
        boolean isFirstFrame = this.framePointer == 0;
        int sampleSize = this.sampleSize;
        int downsampledWidth = this.downsampledWidth;
        int downsampledHeight = this.downsampledHeight;
        byte[] mainPixels = this.mainPixels;
        int[] act = this.act;
        int pass = 1;
        Boolean isFirstFrameTransparent = this.isFirstFrameTransparent;
        Boolean isFirstFrameTransparent2 = isFirstFrameTransparent;
        int i = 0;
        int inc = 8;
        while (i < downsampledIH) {
            int line = i;
            Boolean isFirstFrameTransparent3 = isFirstFrameTransparent2;
            if (currentFrame.interlace) {
                if (iline >= downsampledIH) {
                    pass++;
                    switch (pass) {
                        case 2:
                            iline = 4;
                            break;
                        case 3:
                            iline = 2;
                            inc = 4;
                            break;
                        case 4:
                            iline = 1;
                            inc = 2;
                            break;
                    }
                }
                line = iline;
                iline += inc;
            }
            int line2 = line + downsampledIY2;
            int line3 = downsampledIH;
            boolean isNotDownsampling = sampleSize == 1;
            if (line2 < downsampledHeight) {
                int k = line2 * downsampledWidth;
                int dx = k + downsampledIX2;
                downsampledIY = downsampledIY2;
                int dlim = dx + downsampledIW2;
                downsampledIW = downsampledIW2;
                int downsampledIW3 = k + downsampledWidth;
                if (downsampledIW3 < dlim) {
                    dlim = k + downsampledWidth;
                }
                downsampledIX = downsampledIX2;
                int downsampledIX3 = currentFrame.iw;
                int sx = i * sampleSize * downsampledIX3;
                if (isNotDownsampling) {
                    int sx2 = sx;
                    int sx3 = dx;
                    while (sx3 < dlim) {
                        boolean isNotDownsampling2 = isNotDownsampling;
                        int currentColorIndex = mainPixels[sx2] & 255;
                        int averageColor = act[currentColorIndex];
                        if (averageColor != 0) {
                            dest[sx3] = averageColor;
                        } else if (isFirstFrame && isFirstFrameTransparent3 == null) {
                            isFirstFrameTransparent3 = true;
                        }
                        sx2 += sampleSize;
                        sx3++;
                        isNotDownsampling = isNotDownsampling2;
                    }
                    isFirstFrameTransparent2 = isFirstFrameTransparent3;
                } else {
                    int maxPositionInSource = ((dlim - dx) * sampleSize) + sx;
                    int sx4 = sx;
                    int sx5 = dx;
                    while (sx5 < dlim) {
                        int dlim2 = dlim;
                        int averageColor2 = averageColorsNear(sx4, maxPositionInSource, currentFrame.iw);
                        if (averageColor2 != 0) {
                            dest[sx5] = averageColor2;
                        } else if (isFirstFrame && isFirstFrameTransparent3 == null) {
                            isFirstFrameTransparent3 = true;
                        }
                        sx4 += sampleSize;
                        sx5++;
                        dlim = dlim2;
                    }
                    isFirstFrameTransparent2 = isFirstFrameTransparent3;
                }
            } else {
                downsampledIY = downsampledIY2;
                downsampledIW = downsampledIW2;
                downsampledIX = downsampledIX2;
                isFirstFrameTransparent2 = isFirstFrameTransparent3;
            }
            i++;
            downsampledIH = line3;
            downsampledIY2 = downsampledIY;
            downsampledIW2 = downsampledIW;
            downsampledIX2 = downsampledIX;
        }
        Boolean isFirstFrameTransparent4 = isFirstFrameTransparent2;
        if (this.isFirstFrameTransparent == null) {
            this.isFirstFrameTransparent = Boolean.valueOf(isFirstFrameTransparent4 == null ? false : isFirstFrameTransparent4.booleanValue());
        }
    }

    @ColorInt
    private int averageColorsNear(int positionInMainPixels, int maxPositionInMainPixels, int currentFrameIw) {
        int alphaSum = 0;
        int redSum = 0;
        int greenSum = 0;
        int blueSum = 0;
        int totalAdded = 0;
        for (int i = positionInMainPixels; i < this.sampleSize + positionInMainPixels; i++) {
            byte[] bArr = this.mainPixels;
            if (i >= bArr.length || i >= maxPositionInMainPixels) {
                break;
            }
            int currentColorIndex = bArr[i] & 255;
            int currentColor = this.act[currentColorIndex];
            if (currentColor != 0) {
                alphaSum += (currentColor >> 24) & 255;
                redSum += (currentColor >> 16) & 255;
                greenSum += (currentColor >> 8) & 255;
                blueSum += currentColor & 255;
                totalAdded++;
            }
        }
        for (int i2 = positionInMainPixels + currentFrameIw; i2 < positionInMainPixels + currentFrameIw + this.sampleSize; i2++) {
            byte[] bArr2 = this.mainPixels;
            if (i2 >= bArr2.length || i2 >= maxPositionInMainPixels) {
                break;
            }
            int currentColorIndex2 = bArr2[i2] & 255;
            int currentColor2 = this.act[currentColorIndex2];
            if (currentColor2 != 0) {
                alphaSum += (currentColor2 >> 24) & 255;
                redSum += (currentColor2 >> 16) & 255;
                greenSum += (currentColor2 >> 8) & 255;
                blueSum += currentColor2 & 255;
                totalAdded++;
            }
        }
        if (totalAdded == 0) {
            return 0;
        }
        return ((alphaSum / totalAdded) << 24) | ((redSum / totalAdded) << 16) | ((greenSum / totalAdded) << 8) | (blueSum / totalAdded);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void decodeBitmapData(GifFrame frame) {
        int i;
        int i2;
        StandardGifDecoder standardGifDecoder = this;
        if (frame != null) {
            standardGifDecoder.rawData.position(frame.bufferFrameStart);
        }
        if (frame == null) {
            i = standardGifDecoder.header.width;
            i2 = standardGifDecoder.header.height;
        } else {
            i = frame.iw;
            i2 = frame.ih;
        }
        int npix = i * i2;
        byte[] bArr = standardGifDecoder.mainPixels;
        if (bArr == null || bArr.length < npix) {
            standardGifDecoder.mainPixels = standardGifDecoder.bitmapProvider.obtainByteArray(npix);
        }
        byte[] mainPixels = standardGifDecoder.mainPixels;
        if (standardGifDecoder.prefix == null) {
            standardGifDecoder.prefix = new short[4096];
        }
        short[] prefix = standardGifDecoder.prefix;
        if (standardGifDecoder.suffix == null) {
            standardGifDecoder.suffix = new byte[4096];
        }
        byte[] suffix = standardGifDecoder.suffix;
        if (standardGifDecoder.pixelStack == null) {
            standardGifDecoder.pixelStack = new byte[FragmentTransaction.TRANSIT_FRAGMENT_OPEN];
        }
        byte[] pixelStack = standardGifDecoder.pixelStack;
        int dataSize = readByte();
        int clear = 1 << dataSize;
        int endOfInformation = clear + 1;
        int available = clear + 2;
        int code = -1;
        int codeSize = dataSize + 1;
        int codeMask = (1 << codeSize) - 1;
        int code2 = 0;
        while (code2 < clear) {
            prefix[code2] = 0;
            suffix[code2] = (byte) code2;
            code2++;
        }
        byte[] block = standardGifDecoder.block;
        int bi = 0;
        int i3 = 0;
        int top = 0;
        int count = 0;
        int pi = 0;
        int datum = 0;
        int codeSize2 = code2;
        int bits = 0;
        int codeSize3 = codeSize;
        int oldCode = 0;
        int codeMask2 = codeMask;
        int available2 = available;
        while (true) {
            if (bits < npix) {
                if (count == 0) {
                    count = readBlock();
                    if (count <= 0) {
                        standardGifDecoder.status = 3;
                        break;
                    }
                    bi = 0;
                }
                datum += (block[bi] & 255) << pi;
                int bits2 = pi + 8;
                bi++;
                int i4 = -1;
                count--;
                int first = top;
                int top2 = i3;
                int i5 = bits;
                int bits3 = bits2;
                int pi2 = oldCode;
                int oldCode2 = code;
                short code3 = codeSize2;
                int codeSize4 = codeSize3;
                while (true) {
                    if (bits3 < codeSize4) {
                        codeSize3 = codeSize4;
                        codeSize2 = code3;
                        code = oldCode2;
                        oldCode = pi2;
                        standardGifDecoder = this;
                        pi = bits3;
                        bits = i5;
                        i3 = top2;
                        top = first;
                        break;
                    }
                    code3 = datum & codeMask2;
                    datum >>= codeSize4;
                    bits3 -= codeSize4;
                    if (code3 == clear) {
                        codeSize4 = dataSize + 1;
                        codeMask2 = (1 << codeSize4) - 1;
                        available2 = clear + 2;
                        oldCode2 = -1;
                    } else if (code3 == endOfInformation) {
                        codeSize3 = codeSize4;
                        codeSize2 = code3;
                        code = oldCode2;
                        oldCode = pi2;
                        pi = bits3;
                        bits = i5;
                        i3 = top2;
                        top = first;
                        break;
                    } else if (oldCode2 == i4) {
                        mainPixels[pi2] = suffix[code3];
                        pi2++;
                        i5++;
                        oldCode2 = code3;
                        first = code3;
                    } else {
                        if (code3 >= available2) {
                            pixelStack[top2] = (byte) first;
                            top2++;
                            code3 = oldCode2;
                        }
                        while (code3 >= clear) {
                            pixelStack[top2] = suffix[code3];
                            top2++;
                            code3 = prefix[code3];
                        }
                        int first2 = suffix[code3] & 255;
                        mainPixels[pi2] = (byte) first2;
                        pi2++;
                        i5++;
                        while (top2 > 0) {
                            top2--;
                            mainPixels[pi2] = pixelStack[top2];
                            pi2++;
                            i5++;
                        }
                        if (available2 < 4096) {
                            prefix[available2] = (short) oldCode2;
                            suffix[available2] = (byte) first2;
                            available2++;
                            if ((available2 & codeMask2) == 0 && available2 < 4096) {
                                codeSize4++;
                                codeMask2 += available2;
                            }
                        }
                        oldCode2 = code3;
                        first = first2;
                        standardGifDecoder = this;
                        i4 = -1;
                    }
                }
            } else {
                break;
            }
        }
        Arrays.fill(mainPixels, oldCode, npix, (byte) 0);
    }

    private int readByte() {
        return this.rawData.get() & 255;
    }

    private int readBlock() {
        int blockSize = readByte();
        if (blockSize <= 0) {
            return blockSize;
        }
        ByteBuffer byteBuffer = this.rawData;
        byteBuffer.get(this.block, 0, Math.min(blockSize, byteBuffer.remaining()));
        return blockSize;
    }

    private Bitmap getNextBitmap() {
        Boolean bool = this.isFirstFrameTransparent;
        Bitmap.Config config = (bool == null || bool.booleanValue()) ? Bitmap.Config.ARGB_8888 : this.bitmapConfig;
        Bitmap result = this.bitmapProvider.obtain(this.downsampledWidth, this.downsampledHeight, config);
        result.setHasAlpha(true);
        return result;
    }
}

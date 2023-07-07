package android.support.p000v4.provider;

import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Handler;
import android.provider.BaseColumns;
import android.support.annotation.GuardedBy;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.annotation.VisibleForTesting;
import android.support.p000v4.content.res.FontResourcesParserCompat;
import android.support.p000v4.content.res.ResourcesCompat;
import android.support.p000v4.graphics.TypefaceCompat;
import android.support.p000v4.graphics.TypefaceCompatUtil;
import android.support.p000v4.provider.SelfDestructiveThread;
import android.support.p000v4.util.LruCache;
import android.support.p000v4.util.Preconditions;
import android.support.p000v4.util.SimpleArrayMap;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/* renamed from: android.support.v4.provider.FontsContractCompat */
/* loaded from: classes.dex */
public class FontsContractCompat {
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public static final String PARCEL_FONT_RESULTS = "font_results";
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    static final int RESULT_CODE_PROVIDER_NOT_FOUND = -1;
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    static final int RESULT_CODE_WRONG_CERTIFICATES = -2;
    private static final String TAG = "FontsContractCompat";
    static final LruCache<String, Typeface> sTypefaceCache = new LruCache<>(16);
    private static final int BACKGROUND_THREAD_KEEP_ALIVE_DURATION_MS = 10000;
    private static final SelfDestructiveThread sBackgroundThread = new SelfDestructiveThread("fonts", 10, BACKGROUND_THREAD_KEEP_ALIVE_DURATION_MS);
    static final Object sLock = new Object();
    @GuardedBy("sLock")
    static final SimpleArrayMap<String, ArrayList<SelfDestructiveThread.ReplyCallback<TypefaceResult>>> sPendingReplies = new SimpleArrayMap<>();
    private static final Comparator<byte[]> sByteArrayComparator = new Comparator<byte[]>() { // from class: android.support.v4.provider.FontsContractCompat.5
        @Override // java.util.Comparator
        public int compare(byte[] l, byte[] r) {
            if (l.length != r.length) {
                return l.length - r.length;
            }
            for (int i = 0; i < l.length; i++) {
                if (l[i] != r[i]) {
                    return l[i] - r[i];
                }
            }
            return 0;
        }
    };

    /* renamed from: android.support.v4.provider.FontsContractCompat$Columns */
    /* loaded from: classes.dex */
    public static final class Columns implements BaseColumns {
        public static final String FILE_ID = "file_id";
        public static final String ITALIC = "font_italic";
        public static final String RESULT_CODE = "result_code";
        public static final int RESULT_CODE_FONT_NOT_FOUND = 1;
        public static final int RESULT_CODE_FONT_UNAVAILABLE = 2;
        public static final int RESULT_CODE_MALFORMED_QUERY = 3;
        public static final int RESULT_CODE_OK = 0;
        public static final String TTC_INDEX = "font_ttc_index";
        public static final String VARIATION_SETTINGS = "font_variation_settings";
        public static final String WEIGHT = "font_weight";
    }

    private FontsContractCompat() {
    }

    @NonNull
    static TypefaceResult getFontInternal(Context context, FontRequest request, int style) {
        try {
            FontFamilyResult result = fetchFonts(context, null, request);
            int i = -3;
            if (result.getStatusCode() == 0) {
                Typeface typeface = TypefaceCompat.createFromFontInfo(context, null, result.getFonts(), style);
                if (typeface != null) {
                    i = 0;
                }
                return new TypefaceResult(typeface, i);
            }
            if (result.getStatusCode() == 1) {
                i = -2;
            }
            int resultCode = i;
            return new TypefaceResult(null, resultCode);
        } catch (PackageManager.NameNotFoundException e) {
            return new TypefaceResult(null, -1);
        }
    }

    /* renamed from: android.support.v4.provider.FontsContractCompat$TypefaceResult */
    /* loaded from: classes.dex */
    public static final class TypefaceResult {
        final int mResult;
        final Typeface mTypeface;

        TypefaceResult(@Nullable Typeface typeface, int result) {
            this.mTypeface = typeface;
            this.mResult = result;
        }
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public static void resetCache() {
        sTypefaceCache.evictAll();
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public static Typeface getFontSync(final Context context, final FontRequest request, @Nullable final ResourcesCompat.FontCallback fontCallback, @Nullable final Handler handler, boolean isBlockingFetch, int timeout, final int style) {
        final String id = request.getIdentifier() + "-" + style;
        Typeface cached = sTypefaceCache.get(id);
        if (cached != null) {
            if (fontCallback != null) {
                fontCallback.onFontRetrieved(cached);
            }
            return cached;
        } else if (isBlockingFetch && timeout == -1) {
            TypefaceResult typefaceResult = getFontInternal(context, request, style);
            if (fontCallback != null) {
                if (typefaceResult.mResult == 0) {
                    fontCallback.callbackSuccessAsync(typefaceResult.mTypeface, handler);
                } else {
                    fontCallback.callbackFailAsync(typefaceResult.mResult, handler);
                }
            }
            return typefaceResult.mTypeface;
        } else {
            Callable<TypefaceResult> fetcher = new Callable<TypefaceResult>() { // from class: android.support.v4.provider.FontsContractCompat.1
                @Override // java.util.concurrent.Callable
                /* renamed from: call */
                public TypefaceResult mo182call() throws Exception {
                    TypefaceResult typeface = FontsContractCompat.getFontInternal(context, request, style);
                    if (typeface.mTypeface != null) {
                        FontsContractCompat.sTypefaceCache.put(id, typeface.mTypeface);
                    }
                    return typeface;
                }
            };
            if (isBlockingFetch) {
                try {
                    return ((TypefaceResult) sBackgroundThread.postAndWait(fetcher, timeout)).mTypeface;
                } catch (InterruptedException e) {
                    return null;
                }
            }
            SelfDestructiveThread.ReplyCallback<TypefaceResult> reply = fontCallback == null ? null : new SelfDestructiveThread.ReplyCallback<TypefaceResult>() { // from class: android.support.v4.provider.FontsContractCompat.2
                @Override // android.support.p000v4.provider.SelfDestructiveThread.ReplyCallback
                public void onReply(TypefaceResult typeface) {
                    if (typeface == null) {
                        fontCallback.callbackFailAsync(1, handler);
                    } else if (typeface.mResult == 0) {
                        fontCallback.callbackSuccessAsync(typeface.mTypeface, handler);
                    } else {
                        fontCallback.callbackFailAsync(typeface.mResult, handler);
                    }
                }
            };
            synchronized (sLock) {
                if (sPendingReplies.containsKey(id)) {
                    if (reply != null) {
                        sPendingReplies.get(id).add(reply);
                    }
                    return null;
                }
                if (reply != null) {
                    ArrayList<SelfDestructiveThread.ReplyCallback<TypefaceResult>> pendingReplies = new ArrayList<>();
                    pendingReplies.add(reply);
                    sPendingReplies.put(id, pendingReplies);
                }
                sBackgroundThread.postAndReply(fetcher, new SelfDestructiveThread.ReplyCallback<TypefaceResult>() { // from class: android.support.v4.provider.FontsContractCompat.3
                    @Override // android.support.p000v4.provider.SelfDestructiveThread.ReplyCallback
                    public void onReply(TypefaceResult typeface) {
                        synchronized (FontsContractCompat.sLock) {
                            ArrayList<SelfDestructiveThread.ReplyCallback<TypefaceResult>> replies = FontsContractCompat.sPendingReplies.get(id);
                            if (replies == null) {
                                return;
                            }
                            FontsContractCompat.sPendingReplies.remove(id);
                            for (int i = 0; i < replies.size(); i++) {
                                replies.get(i).onReply(typeface);
                            }
                        }
                    }
                });
                return null;
            }
        }
    }

    /* renamed from: android.support.v4.provider.FontsContractCompat$FontInfo */
    /* loaded from: classes.dex */
    public static class FontInfo {
        private final boolean mItalic;
        private final int mResultCode;
        private final int mTtcIndex;
        private final Uri mUri;
        private final int mWeight;

        @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
        public FontInfo(@NonNull Uri uri, @IntRange(from = 0) int ttcIndex, @IntRange(from = 1, m54to = 1000) int weight, boolean italic, int resultCode) {
            this.mUri = (Uri) Preconditions.checkNotNull(uri);
            this.mTtcIndex = ttcIndex;
            this.mWeight = weight;
            this.mItalic = italic;
            this.mResultCode = resultCode;
        }

        @NonNull
        public Uri getUri() {
            return this.mUri;
        }

        @IntRange(from = 0)
        public int getTtcIndex() {
            return this.mTtcIndex;
        }

        @IntRange(from = 1, m54to = 1000)
        public int getWeight() {
            return this.mWeight;
        }

        public boolean isItalic() {
            return this.mItalic;
        }

        public int getResultCode() {
            return this.mResultCode;
        }
    }

    /* renamed from: android.support.v4.provider.FontsContractCompat$FontFamilyResult */
    /* loaded from: classes.dex */
    public static class FontFamilyResult {
        public static final int STATUS_OK = 0;
        public static final int STATUS_UNEXPECTED_DATA_PROVIDED = 2;
        public static final int STATUS_WRONG_CERTIFICATES = 1;
        private final FontInfo[] mFonts;
        private final int mStatusCode;

        @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
        public FontFamilyResult(int statusCode, @Nullable FontInfo[] fonts) {
            this.mStatusCode = statusCode;
            this.mFonts = fonts;
        }

        public int getStatusCode() {
            return this.mStatusCode;
        }

        public FontInfo[] getFonts() {
            return this.mFonts;
        }
    }

    /* renamed from: android.support.v4.provider.FontsContractCompat$FontRequestCallback */
    /* loaded from: classes.dex */
    public static class FontRequestCallback {
        public static final int FAIL_REASON_FONT_LOAD_ERROR = -3;
        public static final int FAIL_REASON_FONT_NOT_FOUND = 1;
        public static final int FAIL_REASON_FONT_UNAVAILABLE = 2;
        public static final int FAIL_REASON_MALFORMED_QUERY = 3;
        public static final int FAIL_REASON_PROVIDER_NOT_FOUND = -1;
        public static final int FAIL_REASON_SECURITY_VIOLATION = -4;
        public static final int FAIL_REASON_WRONG_CERTIFICATES = -2;
        @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
        public static final int RESULT_OK = 0;

        @Retention(RetentionPolicy.SOURCE)
        @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
        /* renamed from: android.support.v4.provider.FontsContractCompat$FontRequestCallback$FontRequestFailReason */
        /* loaded from: classes.dex */
        public @interface FontRequestFailReason {
        }

        public void onTypefaceRetrieved(Typeface typeface) {
        }

        public void onTypefaceRequestFailed(int reason) {
        }
    }

    public static void requestFont(@NonNull final Context context, @NonNull final FontRequest request, @NonNull final FontRequestCallback callback, @NonNull Handler handler) {
        final Handler callerThreadHandler = new Handler();
        handler.post(new Runnable() { // from class: android.support.v4.provider.FontsContractCompat.4
            @Override // java.lang.Runnable
            public void run() {
                try {
                    FontFamilyResult result = FontsContractCompat.fetchFonts(context, null, request);
                    if (result.getStatusCode() != 0) {
                        switch (result.getStatusCode()) {
                            case 1:
                                callerThreadHandler.post(new Runnable() { // from class: android.support.v4.provider.FontsContractCompat.4.2
                                    {
                                        RunnableC03724.this = this;
                                    }

                                    @Override // java.lang.Runnable
                                    public void run() {
                                        callback.onTypefaceRequestFailed(-2);
                                    }
                                });
                                return;
                            case 2:
                                callerThreadHandler.post(new Runnable() { // from class: android.support.v4.provider.FontsContractCompat.4.3
                                    {
                                        RunnableC03724.this = this;
                                    }

                                    @Override // java.lang.Runnable
                                    public void run() {
                                        callback.onTypefaceRequestFailed(-3);
                                    }
                                });
                                return;
                            default:
                                callerThreadHandler.post(new Runnable() { // from class: android.support.v4.provider.FontsContractCompat.4.4
                                    {
                                        RunnableC03724.this = this;
                                    }

                                    @Override // java.lang.Runnable
                                    public void run() {
                                        callback.onTypefaceRequestFailed(-3);
                                    }
                                });
                                return;
                        }
                    }
                    FontInfo[] fonts = result.getFonts();
                    if (fonts == null || fonts.length == 0) {
                        callerThreadHandler.post(new Runnable() { // from class: android.support.v4.provider.FontsContractCompat.4.5
                            {
                                RunnableC03724.this = this;
                            }

                            @Override // java.lang.Runnable
                            public void run() {
                                callback.onTypefaceRequestFailed(1);
                            }
                        });
                        return;
                    }
                    for (FontInfo font : fonts) {
                        if (font.getResultCode() != 0) {
                            final int resultCode = font.getResultCode();
                            if (resultCode < 0) {
                                callerThreadHandler.post(new Runnable() { // from class: android.support.v4.provider.FontsContractCompat.4.6
                                    {
                                        RunnableC03724.this = this;
                                    }

                                    @Override // java.lang.Runnable
                                    public void run() {
                                        callback.onTypefaceRequestFailed(-3);
                                    }
                                });
                                return;
                            } else {
                                callerThreadHandler.post(new Runnable() { // from class: android.support.v4.provider.FontsContractCompat.4.7
                                    {
                                        RunnableC03724.this = this;
                                    }

                                    @Override // java.lang.Runnable
                                    public void run() {
                                        callback.onTypefaceRequestFailed(resultCode);
                                    }
                                });
                                return;
                            }
                        }
                    }
                    final Typeface typeface = FontsContractCompat.buildTypeface(context, null, fonts);
                    if (typeface == null) {
                        callerThreadHandler.post(new Runnable() { // from class: android.support.v4.provider.FontsContractCompat.4.8
                            {
                                RunnableC03724.this = this;
                            }

                            @Override // java.lang.Runnable
                            public void run() {
                                callback.onTypefaceRequestFailed(-3);
                            }
                        });
                    } else {
                        callerThreadHandler.post(new Runnable() { // from class: android.support.v4.provider.FontsContractCompat.4.9
                            {
                                RunnableC03724.this = this;
                            }

                            @Override // java.lang.Runnable
                            public void run() {
                                callback.onTypefaceRetrieved(typeface);
                            }
                        });
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    callerThreadHandler.post(new Runnable() { // from class: android.support.v4.provider.FontsContractCompat.4.1
                        {
                            RunnableC03724.this = this;
                        }

                        @Override // java.lang.Runnable
                        public void run() {
                            callback.onTypefaceRequestFailed(-1);
                        }
                    });
                }
            }
        });
    }

    @Nullable
    public static Typeface buildTypeface(@NonNull Context context, @Nullable CancellationSignal cancellationSignal, @NonNull FontInfo[] fonts) {
        return TypefaceCompat.createFromFontInfo(context, cancellationSignal, fonts, 0);
    }

    @RequiresApi(19)
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public static Map<Uri, ByteBuffer> prepareFontData(Context context, FontInfo[] fonts, CancellationSignal cancellationSignal) {
        HashMap<Uri, ByteBuffer> out = new HashMap<>();
        for (FontInfo font : fonts) {
            if (font.getResultCode() == 0) {
                Uri uri = font.getUri();
                if (!out.containsKey(uri)) {
                    ByteBuffer buffer = TypefaceCompatUtil.mmap(context, cancellationSignal, uri);
                    out.put(uri, buffer);
                }
            }
        }
        return Collections.unmodifiableMap(out);
    }

    @NonNull
    public static FontFamilyResult fetchFonts(@NonNull Context context, @Nullable CancellationSignal cancellationSignal, @NonNull FontRequest request) throws PackageManager.NameNotFoundException {
        ProviderInfo providerInfo = getProvider(context.getPackageManager(), request, context.getResources());
        if (providerInfo == null) {
            return new FontFamilyResult(1, null);
        }
        FontInfo[] fonts = getFontFromProvider(context, request, providerInfo.authority, cancellationSignal);
        return new FontFamilyResult(0, fonts);
    }

    @VisibleForTesting
    @Nullable
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public static ProviderInfo getProvider(@NonNull PackageManager packageManager, @NonNull FontRequest request, @Nullable Resources resources) throws PackageManager.NameNotFoundException {
        String providerAuthority = request.getProviderAuthority();
        ProviderInfo info = packageManager.resolveContentProvider(providerAuthority, 0);
        if (info == null) {
            throw new PackageManager.NameNotFoundException("No package found for authority: " + providerAuthority);
        } else if (!info.packageName.equals(request.getProviderPackage())) {
            throw new PackageManager.NameNotFoundException("Found content provider " + providerAuthority + ", but package was not " + request.getProviderPackage());
        } else {
            PackageInfo packageInfo = packageManager.getPackageInfo(info.packageName, 64);
            List<byte[]> signatures = convertToByteArrayList(packageInfo.signatures);
            Collections.sort(signatures, sByteArrayComparator);
            List<List<byte[]>> requestCertificatesList = getCertificates(request, resources);
            for (int i = 0; i < requestCertificatesList.size(); i++) {
                List<byte[]> requestSignatures = new ArrayList<>(requestCertificatesList.get(i));
                Collections.sort(requestSignatures, sByteArrayComparator);
                if (equalsByteArrayList(signatures, requestSignatures)) {
                    return info;
                }
            }
            return null;
        }
    }

    private static List<List<byte[]>> getCertificates(FontRequest request, Resources resources) {
        if (request.getCertificates() != null) {
            return request.getCertificates();
        }
        int resourceId = request.getCertificatesArrayResId();
        return FontResourcesParserCompat.readCerts(resources, resourceId);
    }

    private static boolean equalsByteArrayList(List<byte[]> signatures, List<byte[]> requestSignatures) {
        if (signatures.size() != requestSignatures.size()) {
            return false;
        }
        for (int i = 0; i < signatures.size(); i++) {
            if (!Arrays.equals(signatures.get(i), requestSignatures.get(i))) {
                return false;
            }
        }
        return true;
    }

    private static List<byte[]> convertToByteArrayList(Signature[] signatures) {
        List<byte[]> shas = new ArrayList<>();
        for (Signature signature : signatures) {
            shas.add(signature.toByteArray());
        }
        return shas;
    }

    @VisibleForTesting
    @NonNull
    static FontInfo[] getFontFromProvider(Context context, FontRequest request, String authority, CancellationSignal cancellationSignal) {
        Uri fileUri;
        ArrayList<FontInfo> result = new ArrayList<>();
        Uri uri = new Uri.Builder().scheme("content").authority(authority).build();
        Uri fileBaseUri = new Uri.Builder().scheme("content").authority(authority).appendPath("file").build();
        Cursor cursor = null;
        try {
            if (Build.VERSION.SDK_INT > 16) {
                cursor = context.getContentResolver().query(uri, new String[]{"_id", Columns.FILE_ID, Columns.TTC_INDEX, Columns.VARIATION_SETTINGS, Columns.WEIGHT, Columns.ITALIC, Columns.RESULT_CODE}, "query = ?", new String[]{request.getQuery()}, null, cancellationSignal);
            } else {
                cursor = context.getContentResolver().query(uri, new String[]{"_id", Columns.FILE_ID, Columns.TTC_INDEX, Columns.VARIATION_SETTINGS, Columns.WEIGHT, Columns.ITALIC, Columns.RESULT_CODE}, "query = ?", new String[]{request.getQuery()}, null);
            }
            if (cursor != null && cursor.getCount() > 0) {
                int resultCodeColumnIndex = cursor.getColumnIndex(Columns.RESULT_CODE);
                result = new ArrayList<>();
                int idColumnIndex = cursor.getColumnIndex("_id");
                int fileIdColumnIndex = cursor.getColumnIndex(Columns.FILE_ID);
                int ttcIndexColumnIndex = cursor.getColumnIndex(Columns.TTC_INDEX);
                int weightColumnIndex = cursor.getColumnIndex(Columns.WEIGHT);
                int italicColumnIndex = cursor.getColumnIndex(Columns.ITALIC);
                while (cursor.moveToNext()) {
                    int resultCode = resultCodeColumnIndex != -1 ? cursor.getInt(resultCodeColumnIndex) : 0;
                    int ttcIndex = ttcIndexColumnIndex != -1 ? cursor.getInt(ttcIndexColumnIndex) : 0;
                    if (fileIdColumnIndex == -1) {
                        long id = cursor.getLong(idColumnIndex);
                        fileUri = ContentUris.withAppendedId(uri, id);
                    } else {
                        long id2 = cursor.getLong(fileIdColumnIndex);
                        fileUri = ContentUris.withAppendedId(fileBaseUri, id2);
                    }
                    int weight = weightColumnIndex != -1 ? cursor.getInt(weightColumnIndex) : 400;
                    boolean italic = italicColumnIndex != -1 && cursor.getInt(italicColumnIndex) == 1;
                    result.add(new FontInfo(fileUri, ttcIndex, weight, italic, resultCode));
                }
            }
            return (FontInfo[]) result.toArray(new FontInfo[0]);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
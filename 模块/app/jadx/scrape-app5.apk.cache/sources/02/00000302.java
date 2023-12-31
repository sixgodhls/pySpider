package com.bumptech.glide.load.engine;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Key;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public final class GlideException extends Exception {
    private static final StackTraceElement[] EMPTY_ELEMENTS = new StackTraceElement[0];
    private static final long serialVersionUID = 1;
    private final List<Throwable> causes;
    private Class<?> dataClass;
    private DataSource dataSource;
    private String detailMessage;
    private Key key;

    public GlideException(String message) {
        this(message, Collections.emptyList());
    }

    public GlideException(String detailMessage, Throwable cause) {
        this(detailMessage, Collections.singletonList(cause));
    }

    public GlideException(String detailMessage, List<Throwable> causes) {
        this.detailMessage = detailMessage;
        setStackTrace(EMPTY_ELEMENTS);
        this.causes = causes;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setLoggingDetails(Key key, DataSource dataSource) {
        setLoggingDetails(key, dataSource, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setLoggingDetails(Key key, DataSource dataSource, Class<?> dataClass) {
        this.key = key;
        this.dataSource = dataSource;
        this.dataClass = dataClass;
    }

    @Override // java.lang.Throwable
    public Throwable fillInStackTrace() {
        return this;
    }

    public List<Throwable> getCauses() {
        return this.causes;
    }

    public List<Throwable> getRootCauses() {
        List<Throwable> rootCauses = new ArrayList<>();
        addRootCauses(this, rootCauses);
        return rootCauses;
    }

    public void logRootCauses(String tag) {
        List<Throwable> causes = getRootCauses();
        int size = causes.size();
        for (int i = 0; i < size; i++) {
            Log.i(tag, "Root cause (" + (i + 1) + " of " + size + ")", causes.get(i));
        }
    }

    private void addRootCauses(Throwable throwable, List<Throwable> rootCauses) {
        if (throwable instanceof GlideException) {
            GlideException glideException = (GlideException) throwable;
            for (Throwable t : glideException.getCauses()) {
                addRootCauses(t, rootCauses);
            }
            return;
        }
        rootCauses.add(throwable);
    }

    @Override // java.lang.Throwable
    public void printStackTrace() {
        printStackTrace(System.err);
    }

    @Override // java.lang.Throwable
    public void printStackTrace(PrintStream err) {
        printStackTrace((Appendable) err);
    }

    @Override // java.lang.Throwable
    public void printStackTrace(PrintWriter err) {
        printStackTrace((Appendable) err);
    }

    private void printStackTrace(Appendable appendable) {
        appendExceptionMessage(this, appendable);
        appendCauses(getCauses(), new IndentedAppendable(appendable));
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        StringBuilder sb = new StringBuilder(71);
        sb.append(this.detailMessage);
        sb.append(this.dataClass != null ? ", " + this.dataClass : "");
        sb.append(this.dataSource != null ? ", " + this.dataSource : "");
        StringBuilder result = sb.append(this.key != null ? ", " + this.key : "");
        List<Throwable> rootCauses = getRootCauses();
        if (rootCauses.isEmpty()) {
            return result.toString();
        }
        if (rootCauses.size() == 1) {
            result.append("\nThere was 1 cause:");
        } else {
            result.append("\nThere were ");
            result.append(rootCauses.size());
            result.append(" causes:");
        }
        for (Throwable cause : rootCauses) {
            result.append('\n');
            result.append(cause.getClass().getName());
            result.append('(');
            result.append(cause.getMessage());
            result.append(')');
        }
        result.append("\n call GlideException#logRootCauses(String) for more detail");
        return result.toString();
    }

    private static void appendExceptionMessage(Throwable t, Appendable appendable) {
        try {
            appendable.append(t.getClass().toString()).append(": ").append(t.getMessage()).append('\n');
        } catch (IOException e) {
            throw new RuntimeException(t);
        }
    }

    private static void appendCauses(List<Throwable> causes, Appendable appendable) {
        try {
            appendCausesWrapped(causes, appendable);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void appendCausesWrapped(List<Throwable> causes, Appendable appendable) throws IOException {
        int size = causes.size();
        for (int i = 0; i < size; i++) {
            appendable.append("Cause (").append(String.valueOf(i + 1)).append(" of ").append(String.valueOf(size)).append("): ");
            Throwable cause = causes.get(i);
            if (cause instanceof GlideException) {
                GlideException glideCause = (GlideException) cause;
                glideCause.printStackTrace(appendable);
            } else {
                appendExceptionMessage(cause, appendable);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class IndentedAppendable implements Appendable {
        private static final String EMPTY_SEQUENCE = "";
        private static final String INDENT = "  ";
        private final Appendable appendable;
        private boolean printedNewLine = true;

        IndentedAppendable(Appendable appendable) {
            this.appendable = appendable;
        }

        @Override // java.lang.Appendable
        public Appendable append(char c) throws IOException {
            boolean z = false;
            if (this.printedNewLine) {
                this.printedNewLine = false;
                this.appendable.append(INDENT);
            }
            if (c == '\n') {
                z = true;
            }
            this.printedNewLine = z;
            this.appendable.append(c);
            return this;
        }

        @Override // java.lang.Appendable
        public Appendable append(@Nullable CharSequence charSequence) throws IOException {
            CharSequence charSequence2 = safeSequence(charSequence);
            return append(charSequence2, 0, charSequence2.length());
        }

        @Override // java.lang.Appendable
        public Appendable append(@Nullable CharSequence charSequence, int start, int end) throws IOException {
            CharSequence charSequence2 = safeSequence(charSequence);
            boolean z = false;
            if (this.printedNewLine) {
                this.printedNewLine = false;
                this.appendable.append(INDENT);
            }
            if (charSequence2.length() > 0 && charSequence2.charAt(end - 1) == '\n') {
                z = true;
            }
            this.printedNewLine = z;
            this.appendable.append(charSequence2, start, end);
            return this;
        }

        @NonNull
        private CharSequence safeSequence(@Nullable CharSequence sequence) {
            if (sequence == null) {
                return "";
            }
            return sequence;
        }
    }
}
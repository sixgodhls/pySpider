package io.reactivex.exceptions;

import io.reactivex.annotations.NonNull;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/* loaded from: classes.dex */
public final class CompositeException extends RuntimeException {
    private static final long serialVersionUID = 3026362227162912146L;
    private Throwable cause;
    private final List<Throwable> exceptions;
    private final String message;

    public CompositeException(@NonNull Throwable... exceptions) {
        this(exceptions == null ? Collections.singletonList(new NullPointerException("exceptions was null")) : Arrays.asList(exceptions));
    }

    public CompositeException(@NonNull Iterable<? extends Throwable> errors) {
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        List<Throwable> localExceptions = new ArrayList<>();
        if (errors != null) {
            for (Throwable ex : errors) {
                if (ex instanceof CompositeException) {
                    linkedHashSet.addAll(((CompositeException) ex).getExceptions());
                } else if (ex != null) {
                    linkedHashSet.add(ex);
                } else {
                    linkedHashSet.add(new NullPointerException("Throwable was null!"));
                }
            }
        } else {
            linkedHashSet.add(new NullPointerException("errors was null"));
        }
        if (linkedHashSet.isEmpty()) {
            throw new IllegalArgumentException("errors is empty");
        }
        localExceptions.addAll(linkedHashSet);
        this.exceptions = Collections.unmodifiableList(localExceptions);
        this.message = this.exceptions.size() + " exceptions occurred. ";
    }

    @NonNull
    public List<Throwable> getExceptions() {
        return this.exceptions;
    }

    @Override // java.lang.Throwable
    @NonNull
    public String getMessage() {
        return this.message;
    }

    @Override // java.lang.Throwable
    @NonNull
    public synchronized Throwable getCause() {
        if (this.cause == null) {
            Throwable localCause = new CompositeExceptionCausalChain();
            Set<Throwable> seenCauses = new HashSet<>();
            Throwable chain = localCause;
            Iterator<Throwable> it = this.exceptions.iterator();
            while (it.hasNext()) {
                Throwable e = it.next();
                if (!seenCauses.contains(e)) {
                    seenCauses.add(e);
                    List<Throwable> listOfCauses = getListOfCauses(e);
                    for (Throwable child : listOfCauses) {
                        if (seenCauses.contains(child)) {
                            e = new RuntimeException("Duplicate found in causal chain so cropping to prevent loop ...");
                        } else {
                            seenCauses.add(child);
                        }
                    }
                    try {
                        chain.initCause(e);
                    } catch (Throwable th) {
                    }
                    chain = getRootCause(chain);
                }
            }
            this.cause = localCause;
        }
        return this.cause;
    }

    @Override // java.lang.Throwable
    public void printStackTrace() {
        printStackTrace(System.err);
    }

    @Override // java.lang.Throwable
    public void printStackTrace(PrintStream s) {
        printStackTrace(new WrappedPrintStream(s));
    }

    @Override // java.lang.Throwable
    public void printStackTrace(PrintWriter s) {
        printStackTrace(new WrappedPrintWriter(s));
    }

    private void printStackTrace(PrintStreamOrWriter s) {
        StackTraceElement[] stackTrace;
        StringBuilder b = new StringBuilder(128);
        b.append(this);
        b.append('\n');
        for (StackTraceElement myStackElement : getStackTrace()) {
            b.append("\tat ");
            b.append(myStackElement);
            b.append('\n');
        }
        int i = 1;
        for (Throwable ex : this.exceptions) {
            b.append("  ComposedException ");
            b.append(i);
            b.append(" :\n");
            appendStackTrace(b, ex, "\t");
            i++;
        }
        s.println(b.toString());
    }

    private void appendStackTrace(StringBuilder b, Throwable ex, String prefix) {
        StackTraceElement[] stackTrace;
        b.append(prefix);
        b.append(ex);
        b.append('\n');
        for (StackTraceElement stackElement : ex.getStackTrace()) {
            b.append("\t\tat ");
            b.append(stackElement);
            b.append('\n');
        }
        if (ex.getCause() != null) {
            b.append("\tCaused by: ");
            appendStackTrace(b, ex.getCause(), "");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static abstract class PrintStreamOrWriter {
        abstract void println(Object obj);

        PrintStreamOrWriter() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class WrappedPrintStream extends PrintStreamOrWriter {
        private final PrintStream printStream;

        WrappedPrintStream(PrintStream printStream) {
            this.printStream = printStream;
        }

        @Override // io.reactivex.exceptions.CompositeException.PrintStreamOrWriter
        void println(Object o) {
            this.printStream.println(o);
        }
    }

    /* loaded from: classes.dex */
    static final class WrappedPrintWriter extends PrintStreamOrWriter {
        private final PrintWriter printWriter;

        WrappedPrintWriter(PrintWriter printWriter) {
            this.printWriter = printWriter;
        }

        @Override // io.reactivex.exceptions.CompositeException.PrintStreamOrWriter
        void println(Object o) {
            this.printWriter.println(o);
        }
    }

    /* loaded from: classes.dex */
    static final class CompositeExceptionCausalChain extends RuntimeException {
        static final String MESSAGE = "Chain of Causes for CompositeException In Order Received =>";
        private static final long serialVersionUID = 3875212506787802066L;

        CompositeExceptionCausalChain() {
        }

        @Override // java.lang.Throwable
        public String getMessage() {
            return MESSAGE;
        }
    }

    private List<Throwable> getListOfCauses(Throwable ex) {
        List<Throwable> list = new ArrayList<>();
        Throwable root = ex.getCause();
        if (root == null || root == ex) {
            return list;
        }
        while (true) {
            list.add(root);
            Throwable cause = root.getCause();
            if (cause == null || cause == root) {
                break;
            }
            root = cause;
        }
        return list;
    }

    public int size() {
        return this.exceptions.size();
    }

    Throwable getRootCause(Throwable e) {
        Throwable root = e.getCause();
        if (root == null || this.cause == root) {
            return e;
        }
        while (true) {
            Throwable cause = root.getCause();
            if (cause == null || cause == root) {
                break;
            }
            root = cause;
        }
        return root;
    }
}
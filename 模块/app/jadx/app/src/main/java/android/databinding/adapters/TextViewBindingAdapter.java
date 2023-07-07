package android.databinding.adapters;

import android.databinding.BindingAdapter;
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RestrictTo;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.DialerKeyListener;
import android.text.method.DigitsKeyListener;
import android.text.method.KeyListener;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TextKeyListener;
import android.util.Log;
import android.widget.TextView;
import com.android.databinding.library.baseAdapters.R;

@BindingMethods({@BindingMethod(attribute = "android:autoLink", method = "setAutoLinkMask", type = TextView.class), @BindingMethod(attribute = "android:drawablePadding", method = "setCompoundDrawablePadding", type = TextView.class), @BindingMethod(attribute = "android:editorExtras", method = "setInputExtras", type = TextView.class), @BindingMethod(attribute = "android:inputType", method = "setRawInputType", type = TextView.class), @BindingMethod(attribute = "android:scrollHorizontally", method = "setHorizontallyScrolling", type = TextView.class), @BindingMethod(attribute = "android:textAllCaps", method = "setAllCaps", type = TextView.class), @BindingMethod(attribute = "android:textColorHighlight", method = "setHighlightColor", type = TextView.class), @BindingMethod(attribute = "android:textColorHint", method = "setHintTextColor", type = TextView.class), @BindingMethod(attribute = "android:textColorLink", method = "setLinkTextColor", type = TextView.class), @BindingMethod(attribute = "android:onEditorAction", method = "setOnEditorActionListener", type = TextView.class)})
@RestrictTo({RestrictTo.Scope.LIBRARY})
/* loaded from: classes.dex */
public class TextViewBindingAdapter {
    public static final int DECIMAL = 5;
    public static final int INTEGER = 1;
    public static final int SIGNED = 3;
    private static final String TAG = "TextViewBindingAdapters";

    /* loaded from: classes.dex */
    public interface AfterTextChanged {
        void afterTextChanged(Editable editable);
    }

    /* loaded from: classes.dex */
    public interface BeforeTextChanged {
        void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3);
    }

    /* loaded from: classes.dex */
    public interface OnTextChanged {
        void onTextChanged(CharSequence charSequence, int i, int i2, int i3);
    }

    @BindingAdapter({"android:text"})
    public static void setText(TextView view, CharSequence text) {
        CharSequence oldText = view.getText();
        if (text != oldText) {
            if (text == null && oldText.length() == 0) {
                return;
            }
            if (text instanceof Spanned) {
                if (text.equals(oldText)) {
                    return;
                }
            } else if (!haveContentsChanged(text, oldText)) {
                return;
            }
            view.setText(text);
        }
    }

    @InverseBindingAdapter(attribute = "android:text", event = "android:textAttrChanged")
    public static String getTextString(TextView view) {
        return view.getText().toString();
    }

    @BindingAdapter({"android:autoText"})
    public static void setAutoText(TextView view, boolean autoText) {
        KeyListener listener = view.getKeyListener();
        TextKeyListener.Capitalize capitalize = TextKeyListener.Capitalize.NONE;
        int inputType = listener != null ? listener.getInputType() : 0;
        if ((inputType & 4096) != 0) {
            capitalize = TextKeyListener.Capitalize.CHARACTERS;
        } else if ((inputType & 8192) != 0) {
            capitalize = TextKeyListener.Capitalize.WORDS;
        } else if ((inputType & 16384) != 0) {
            capitalize = TextKeyListener.Capitalize.SENTENCES;
        }
        view.setKeyListener(TextKeyListener.getInstance(autoText, capitalize));
    }

    @BindingAdapter({"android:capitalize"})
    public static void setCapitalize(TextView view, TextKeyListener.Capitalize capitalize) {
        KeyListener listener = view.getKeyListener();
        int inputType = listener.getInputType();
        boolean autoText = (32768 & inputType) != 0;
        view.setKeyListener(TextKeyListener.getInstance(autoText, capitalize));
    }

    @BindingAdapter({"android:bufferType"})
    public static void setBufferType(TextView view, TextView.BufferType bufferType) {
        view.setText(view.getText(), bufferType);
    }

    @BindingAdapter({"android:digits"})
    public static void setDigits(TextView view, CharSequence digits) {
        if (digits != null) {
            view.setKeyListener(DigitsKeyListener.getInstance(digits.toString()));
        } else if (view.getKeyListener() instanceof DigitsKeyListener) {
            view.setKeyListener(null);
        }
    }

    @BindingAdapter({"android:numeric"})
    public static void setNumeric(TextView view, int numeric) {
        boolean z = true;
        boolean z2 = (numeric & 3) != 0;
        if ((numeric & 5) == 0) {
            z = false;
        }
        view.setKeyListener(DigitsKeyListener.getInstance(z2, z));
    }

    @BindingAdapter({"android:phoneNumber"})
    public static void setPhoneNumber(TextView view, boolean phoneNumber) {
        if (phoneNumber) {
            view.setKeyListener(DialerKeyListener.getInstance());
        } else if (view.getKeyListener() instanceof DialerKeyListener) {
            view.setKeyListener(null);
        }
    }

    private static void setIntrinsicBounds(Drawable drawable) {
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        }
    }

    @BindingAdapter({"android:drawableBottom"})
    public static void setDrawableBottom(TextView view, Drawable drawable) {
        setIntrinsicBounds(drawable);
        Drawable[] drawables = view.getCompoundDrawables();
        view.setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawable);
    }

    @BindingAdapter({"android:drawableLeft"})
    public static void setDrawableLeft(TextView view, Drawable drawable) {
        setIntrinsicBounds(drawable);
        Drawable[] drawables = view.getCompoundDrawables();
        view.setCompoundDrawables(drawable, drawables[1], drawables[2], drawables[3]);
    }

    @BindingAdapter({"android:drawableRight"})
    public static void setDrawableRight(TextView view, Drawable drawable) {
        setIntrinsicBounds(drawable);
        Drawable[] drawables = view.getCompoundDrawables();
        view.setCompoundDrawables(drawables[0], drawables[1], drawable, drawables[3]);
    }

    @BindingAdapter({"android:drawableTop"})
    public static void setDrawableTop(TextView view, Drawable drawable) {
        setIntrinsicBounds(drawable);
        Drawable[] drawables = view.getCompoundDrawables();
        view.setCompoundDrawables(drawables[0], drawable, drawables[2], drawables[3]);
    }

    @BindingAdapter({"android:drawableStart"})
    public static void setDrawableStart(TextView view, Drawable drawable) {
        if (Build.VERSION.SDK_INT < 17) {
            setDrawableLeft(view, drawable);
            return;
        }
        setIntrinsicBounds(drawable);
        Drawable[] drawables = view.getCompoundDrawablesRelative();
        view.setCompoundDrawablesRelative(drawable, drawables[1], drawables[2], drawables[3]);
    }

    @BindingAdapter({"android:drawableEnd"})
    public static void setDrawableEnd(TextView view, Drawable drawable) {
        if (Build.VERSION.SDK_INT < 17) {
            setDrawableRight(view, drawable);
            return;
        }
        setIntrinsicBounds(drawable);
        Drawable[] drawables = view.getCompoundDrawablesRelative();
        view.setCompoundDrawablesRelative(drawables[0], drawables[1], drawable, drawables[3]);
    }

    @BindingAdapter({"android:imeActionLabel"})
    public static void setImeActionLabel(TextView view, CharSequence value) {
        view.setImeActionLabel(value, view.getImeActionId());
    }

    @BindingAdapter({"android:imeActionId"})
    public static void setImeActionLabel(TextView view, int value) {
        view.setImeActionLabel(view.getImeActionLabel(), value);
    }

    @BindingAdapter({"android:inputMethod"})
    public static void setInputMethod(TextView view, CharSequence inputMethod) {
        try {
            Class<?> c = Class.forName(inputMethod.toString());
            view.setKeyListener((KeyListener) c.newInstance());
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "Could not create input method: " + ((Object) inputMethod), e);
        } catch (IllegalAccessException e2) {
            Log.e(TAG, "Could not create input method: " + ((Object) inputMethod), e2);
        } catch (InstantiationException e3) {
            Log.e(TAG, "Could not create input method: " + ((Object) inputMethod), e3);
        }
    }

    @BindingAdapter({"android:lineSpacingExtra"})
    public static void setLineSpacingExtra(TextView view, float value) {
        if (Build.VERSION.SDK_INT >= 16) {
            view.setLineSpacing(value, view.getLineSpacingMultiplier());
        } else {
            view.setLineSpacing(value, 1.0f);
        }
    }

    @BindingAdapter({"android:lineSpacingMultiplier"})
    public static void setLineSpacingMultiplier(TextView view, float value) {
        if (Build.VERSION.SDK_INT >= 16) {
            view.setLineSpacing(view.getLineSpacingExtra(), value);
        } else {
            view.setLineSpacing(0.0f, value);
        }
    }

    @BindingAdapter({"android:maxLength"})
    public static void setMaxLength(TextView view, int value) {
        InputFilter[] filters = view.getFilters();
        if (filters == null) {
            filters = new InputFilter[]{new InputFilter.LengthFilter(value)};
        } else {
            boolean foundMaxLength = false;
            int i = 0;
            while (true) {
                if (i >= filters.length) {
                    break;
                }
                InputFilter filter = filters[i];
                if (!(filter instanceof InputFilter.LengthFilter)) {
                    i++;
                } else {
                    foundMaxLength = true;
                    boolean replace = true;
                    if (Build.VERSION.SDK_INT >= 21) {
                        replace = ((InputFilter.LengthFilter) filter).getMax() != value;
                    }
                    if (replace) {
                        filters[i] = new InputFilter.LengthFilter(value);
                    }
                }
            }
            if (!foundMaxLength) {
                filters = new InputFilter[filters.length + 1];
                System.arraycopy(filters, 0, filters, 0, filters.length);
                filters[filters.length - 1] = new InputFilter.LengthFilter(value);
            }
        }
        view.setFilters(filters);
    }

    @BindingAdapter({"android:password"})
    public static void setPassword(TextView view, boolean password) {
        if (password) {
            view.setTransformationMethod(PasswordTransformationMethod.getInstance());
        } else if (view.getTransformationMethod() instanceof PasswordTransformationMethod) {
            view.setTransformationMethod(null);
        }
    }

    @BindingAdapter({"android:shadowColor"})
    public static void setShadowColor(TextView view, int color) {
        if (Build.VERSION.SDK_INT >= 16) {
            float dx = view.getShadowDx();
            float dy = view.getShadowDy();
            float r = view.getShadowRadius();
            view.setShadowLayer(r, dx, dy, color);
        }
    }

    @BindingAdapter({"android:shadowDx"})
    public static void setShadowDx(TextView view, float dx) {
        if (Build.VERSION.SDK_INT >= 16) {
            int color = view.getShadowColor();
            float dy = view.getShadowDy();
            float r = view.getShadowRadius();
            view.setShadowLayer(r, dx, dy, color);
        }
    }

    @BindingAdapter({"android:shadowDy"})
    public static void setShadowDy(TextView view, float dy) {
        if (Build.VERSION.SDK_INT >= 16) {
            int color = view.getShadowColor();
            float dx = view.getShadowDx();
            float r = view.getShadowRadius();
            view.setShadowLayer(r, dx, dy, color);
        }
    }

    @BindingAdapter({"android:shadowRadius"})
    public static void setShadowRadius(TextView view, float r) {
        if (Build.VERSION.SDK_INT >= 16) {
            int color = view.getShadowColor();
            float dx = view.getShadowDx();
            float dy = view.getShadowDy();
            view.setShadowLayer(r, dx, dy, color);
        }
    }

    @BindingAdapter({"android:textSize"})
    public static void setTextSize(TextView view, float size) {
        view.setTextSize(0, size);
    }

    private static boolean haveContentsChanged(CharSequence str1, CharSequence str2) {
        if ((str1 == null) != (str2 == null)) {
            return true;
        }
        if (str1 == null) {
            return false;
        }
        int length = str1.length();
        if (length != str2.length()) {
            return true;
        }
        for (int i = 0; i < length; i++) {
            if (str1.charAt(i) != str2.charAt(i)) {
                return true;
            }
        }
        return false;
    }

    @BindingAdapter(requireAll = false, value = {"android:beforeTextChanged", "android:onTextChanged", "android:afterTextChanged", "android:textAttrChanged"})
    public static void setTextWatcher(TextView view, final BeforeTextChanged before, final OnTextChanged on, final AfterTextChanged after, final InverseBindingListener textAttrChanged) {
        TextWatcher newValue;
        if (before == null && after == null && on == null && textAttrChanged == null) {
            newValue = null;
        } else {
            newValue = new TextWatcher() { // from class: android.databinding.adapters.TextViewBindingAdapter.1
                @Override // android.text.TextWatcher
                public void beforeTextChanged(CharSequence s, int start, int count, int after2) {
                    BeforeTextChanged beforeTextChanged = BeforeTextChanged.this;
                    if (beforeTextChanged != null) {
                        beforeTextChanged.beforeTextChanged(s, start, count, after2);
                    }
                }

                @Override // android.text.TextWatcher
                public void onTextChanged(CharSequence s, int start, int before2, int count) {
                    OnTextChanged onTextChanged = on;
                    if (onTextChanged != null) {
                        onTextChanged.onTextChanged(s, start, before2, count);
                    }
                    InverseBindingListener inverseBindingListener = textAttrChanged;
                    if (inverseBindingListener != null) {
                        inverseBindingListener.onChange();
                    }
                }

                @Override // android.text.TextWatcher
                public void afterTextChanged(Editable s) {
                    AfterTextChanged afterTextChanged = after;
                    if (afterTextChanged != null) {
                        afterTextChanged.afterTextChanged(s);
                    }
                }
            };
        }
        TextWatcher oldValue = (TextWatcher) ListenerUtil.trackListener(view, newValue, R.id.textWatcher);
        if (oldValue != null) {
            view.removeTextChangedListener(oldValue);
        }
        if (newValue != null) {
            view.addTextChangedListener(newValue);
        }
    }
}

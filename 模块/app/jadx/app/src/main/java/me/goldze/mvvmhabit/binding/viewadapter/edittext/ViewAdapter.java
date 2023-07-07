package me.goldze.mvvmhabit.binding.viewadapter.edittext;

import android.databinding.BindingAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import me.goldze.mvvmhabit.binding.command.BindingCommand;

/* loaded from: classes.dex */
public class ViewAdapter {
    @BindingAdapter(requireAll = false, value = {"requestFocus"})
    public static void requestFocusCommand(EditText editText, Boolean needRequestFocus) {
        if (needRequestFocus.booleanValue()) {
            editText.setSelection(editText.getText().length());
            editText.requestFocus();
            InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService("input_method");
            imm.showSoftInput(editText, 1);
        }
        editText.setFocusableInTouchMode(needRequestFocus.booleanValue());
    }

    @BindingAdapter(requireAll = false, value = {"textChanged"})
    public static void addTextChangedListener(EditText editText, final BindingCommand<String> textChanged) {
        editText.addTextChangedListener(new TextWatcher() { // from class: me.goldze.mvvmhabit.binding.viewadapter.edittext.ViewAdapter.1
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence text, int i, int i1, int i2) {
                BindingCommand bindingCommand = BindingCommand.this;
                if (bindingCommand != null) {
                    bindingCommand.execute(text.toString());
                }
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
            }
        });
    }
}

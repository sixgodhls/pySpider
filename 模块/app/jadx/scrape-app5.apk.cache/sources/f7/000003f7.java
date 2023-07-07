package me.goldze.mvvmhabit.binding.viewadapter.checkbox;

import android.databinding.BindingAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import me.goldze.mvvmhabit.binding.command.BindingCommand;

/* loaded from: classes.dex */
public class ViewAdapter {
    @BindingAdapter(requireAll = false, value = {"onCheckedChangedCommand"})
    public static void setCheckedChanged(CheckBox checkBox, final BindingCommand<Boolean> bindingCommand) {
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: me.goldze.mvvmhabit.binding.viewadapter.checkbox.ViewAdapter.1
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                BindingCommand.this.execute(Boolean.valueOf(b));
            }
        });
    }
}
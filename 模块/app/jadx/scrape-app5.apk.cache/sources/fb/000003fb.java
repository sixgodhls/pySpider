package me.goldze.mvvmhabit.binding.viewadapter.mswitch;

import android.databinding.BindingAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import me.goldze.mvvmhabit.binding.command.BindingCommand;

/* loaded from: classes.dex */
public class ViewAdapter {
    @BindingAdapter({"switchState"})
    public static void setSwitchState(Switch mSwitch, boolean isChecked) {
        mSwitch.setChecked(isChecked);
    }

    @BindingAdapter({"onCheckedChangeCommand"})
    public static void onCheckedChangeCommand(Switch mSwitch, final BindingCommand<Boolean> changeListener) {
        if (changeListener != null) {
            mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: me.goldze.mvvmhabit.binding.viewadapter.mswitch.ViewAdapter.1
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    BindingCommand.this.execute(Boolean.valueOf(isChecked));
                }
            });
        }
    }
}
package me.goldze.mvvmhabit.binding.viewadapter.spinner;

import android.databinding.BindingAdapter;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import java.util.ArrayList;
import java.util.List;
import me.goldze.mvvmhabit.binding.command.BindingCommand;

/* loaded from: classes.dex */
public class ViewAdapter {
    @BindingAdapter(requireAll = false, value = {"itemDatas", "valueReply", "resource", "dropDownResource", "onItemSelectedCommand"})
    public static void onItemSelectedCommand(Spinner spinner, final List<IKeyAndValue> itemDatas, String valueReply, int resource, int dropDownResource, final BindingCommand<IKeyAndValue> bindingCommand) {
        if (itemDatas == null) {
            throw new NullPointerException("this itemDatas parameter is null");
        }
        List<String> lists = new ArrayList<>();
        for (IKeyAndValue iKeyAndValue : itemDatas) {
            lists.add(iKeyAndValue.getKey());
        }
        if (resource == 0) {
            resource = 17367048;
        }
        if (dropDownResource == 0) {
            dropDownResource = 17367049;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(spinner.getContext(), resource, lists);
        adapter.setDropDownViewResource(dropDownResource);
        spinner.setAdapter((SpinnerAdapter) adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: me.goldze.mvvmhabit.binding.viewadapter.spinner.ViewAdapter.1
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                IKeyAndValue iKeyAndValue2 = (IKeyAndValue) itemDatas.get(position);
                bindingCommand.execute(iKeyAndValue2);
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        if (!TextUtils.isEmpty(valueReply)) {
            for (int i = 0; i < itemDatas.size(); i++) {
                IKeyAndValue iKeyAndValue2 = itemDatas.get(i);
                if (valueReply.equals(iKeyAndValue2.getValue())) {
                    spinner.setSelection(i);
                    return;
                }
            }
        }
    }
}
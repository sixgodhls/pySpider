package com.afollestad.materialdialogs;

import android.annotation.TargetApi;
import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import com.afollestad.materialdialogs.internal.MDTintHelper;
import com.afollestad.materialdialogs.util.DialogUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class DefaultRvAdapter extends RecyclerView.Adapter<DefaultVH> {
    private InternalListCallback callback;
    private final MaterialDialog dialog;
    private final GravityEnum itemGravity;
    @LayoutRes
    private final int layout;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface InternalListCallback {
        boolean onItemSelected(MaterialDialog materialDialog, View view, int i, CharSequence charSequence, boolean z);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultRvAdapter(MaterialDialog dialog, @LayoutRes int layout) {
        this.dialog = dialog;
        this.layout = layout;
        this.itemGravity = dialog.builder.itemsGravity;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCallback(InternalListCallback callback) {
        this.callback = callback;
    }

    @Override // android.support.v7.widget.RecyclerView.Adapter
    /* renamed from: onCreateViewHolder  reason: collision with other method in class */
    public DefaultVH mo195onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(this.layout, parent, false);
        DialogUtils.setBackgroundCompat(view, this.dialog.getListSelector());
        return new DefaultVH(view, this);
    }

    @Override // android.support.v7.widget.RecyclerView.Adapter
    public void onBindViewHolder(DefaultVH holder, int index) {
        View view = holder.itemView;
        boolean disabled = DialogUtils.isIn(Integer.valueOf(index), this.dialog.builder.disabledIndices);
        int itemTextColor = disabled ? DialogUtils.adjustAlpha(this.dialog.builder.itemColor, 0.4f) : this.dialog.builder.itemColor;
        holder.itemView.setEnabled(!disabled);
        switch (this.dialog.listType) {
            case SINGLE:
                RadioButton radio = (RadioButton) holder.control;
                boolean selected = this.dialog.builder.selectedIndex == index;
                if (this.dialog.builder.choiceWidgetColor != null) {
                    MDTintHelper.setTint(radio, this.dialog.builder.choiceWidgetColor);
                } else {
                    MDTintHelper.setTint(radio, this.dialog.builder.widgetColor);
                }
                radio.setChecked(selected);
                radio.setEnabled(!disabled);
                break;
            case MULTI:
                CheckBox checkbox = (CheckBox) holder.control;
                boolean selected2 = this.dialog.selectedIndicesList.contains(Integer.valueOf(index));
                if (this.dialog.builder.choiceWidgetColor != null) {
                    MDTintHelper.setTint(checkbox, this.dialog.builder.choiceWidgetColor);
                } else {
                    MDTintHelper.setTint(checkbox, this.dialog.builder.widgetColor);
                }
                checkbox.setChecked(selected2);
                checkbox.setEnabled(!disabled);
                break;
        }
        holder.title.setText(this.dialog.builder.items.get(index));
        holder.title.setTextColor(itemTextColor);
        this.dialog.setTypeface(holder.title, this.dialog.builder.regularFont);
        setupGravity((ViewGroup) view);
        if (this.dialog.builder.itemIds != null) {
            if (index < this.dialog.builder.itemIds.length) {
                view.setId(this.dialog.builder.itemIds[index]);
            } else {
                view.setId(-1);
            }
        }
        if (Build.VERSION.SDK_INT >= 21) {
            ViewGroup group = (ViewGroup) view;
            if (group.getChildCount() != 2) {
                return;
            }
            if (group.getChildAt(0) instanceof CompoundButton) {
                group.getChildAt(0).setBackground(null);
            } else if (!(group.getChildAt(1) instanceof CompoundButton)) {
            } else {
                group.getChildAt(1).setBackground(null);
            }
        }
    }

    @Override // android.support.v7.widget.RecyclerView.Adapter
    public int getItemCount() {
        if (this.dialog.builder.items != null) {
            return this.dialog.builder.items.size();
        }
        return 0;
    }

    @TargetApi(17)
    private void setupGravity(ViewGroup view) {
        LinearLayout itemRoot = (LinearLayout) view;
        int gravityInt = this.itemGravity.getGravityInt();
        itemRoot.setGravity(gravityInt | 16);
        if (view.getChildCount() == 2) {
            if (this.itemGravity == GravityEnum.END && !isRTL() && (view.getChildAt(0) instanceof CompoundButton)) {
                CompoundButton first = (CompoundButton) view.getChildAt(0);
                view.removeView(first);
                TextView second = (TextView) view.getChildAt(0);
                view.removeView(second);
                second.setPadding(second.getPaddingRight(), second.getPaddingTop(), second.getPaddingLeft(), second.getPaddingBottom());
                view.addView(second);
                view.addView(first);
            } else if (this.itemGravity == GravityEnum.START && isRTL() && (view.getChildAt(1) instanceof CompoundButton)) {
                CompoundButton first2 = (CompoundButton) view.getChildAt(1);
                view.removeView(first2);
                TextView second2 = (TextView) view.getChildAt(0);
                view.removeView(second2);
                second2.setPadding(second2.getPaddingRight(), second2.getPaddingTop(), second2.getPaddingRight(), second2.getPaddingBottom());
                view.addView(first2);
                view.addView(second2);
            }
        }
    }

    @TargetApi(17)
    private boolean isRTL() {
        if (Build.VERSION.SDK_INT < 17) {
            return false;
        }
        Configuration config = this.dialog.getBuilder().getContext().getResources().getConfiguration();
        return config.getLayoutDirection() == 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class DefaultVH extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        final DefaultRvAdapter adapter;
        final CompoundButton control;
        final TextView title;

        DefaultVH(View itemView, DefaultRvAdapter adapter) {
            super(itemView);
            this.control = (CompoundButton) itemView.findViewById(R.id.md_control);
            this.title = (TextView) itemView.findViewById(R.id.md_title);
            this.adapter = adapter;
            itemView.setOnClickListener(this);
            if (adapter.dialog.builder.listLongCallback != null) {
                itemView.setOnLongClickListener(this);
            }
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (this.adapter.callback != null && getAdapterPosition() != -1) {
                CharSequence text = null;
                if (this.adapter.dialog.builder.items != null && getAdapterPosition() < this.adapter.dialog.builder.items.size()) {
                    text = this.adapter.dialog.builder.items.get(getAdapterPosition());
                }
                this.adapter.callback.onItemSelected(this.adapter.dialog, view, getAdapterPosition(), text, false);
            }
        }

        @Override // android.view.View.OnLongClickListener
        public boolean onLongClick(View view) {
            if (this.adapter.callback != null && getAdapterPosition() != -1) {
                CharSequence text = null;
                if (this.adapter.dialog.builder.items != null && getAdapterPosition() < this.adapter.dialog.builder.items.size()) {
                    text = this.adapter.dialog.builder.items.get(getAdapterPosition());
                }
                return this.adapter.callback.onItemSelected(this.adapter.dialog, view, getAdapterPosition(), text, true);
            }
            return false;
        }
    }
}

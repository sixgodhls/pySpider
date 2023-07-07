package com.afollestad.materialdialogs.simplelist;

import android.graphics.PorterDuff;
import android.support.p003v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.commons.C0592R;
import com.afollestad.materialdialogs.internal.MDAdapter;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class MaterialSimpleListAdapter extends RecyclerView.Adapter<SimpleListVH> implements MDAdapter {
    private Callback callback;
    private MaterialDialog dialog;
    private List<MaterialSimpleListItem> items = new ArrayList(4);

    /* loaded from: classes.dex */
    public interface Callback {
        void onMaterialListItemSelected(MaterialDialog materialDialog, int i, MaterialSimpleListItem materialSimpleListItem);
    }

    public MaterialSimpleListAdapter(Callback callback) {
        this.callback = callback;
    }

    public void add(MaterialSimpleListItem item) {
        this.items.add(item);
        notifyItemInserted(this.items.size() - 1);
    }

    public void clear() {
        this.items.clear();
        notifyDataSetChanged();
    }

    public MaterialSimpleListItem getItem(int index) {
        return this.items.get(index);
    }

    @Override // com.afollestad.materialdialogs.internal.MDAdapter
    public void setDialog(MaterialDialog dialog) {
        this.dialog = dialog;
    }

    @Override // android.support.p003v7.widget.RecyclerView.Adapter
    /* renamed from: onCreateViewHolder  reason: collision with other method in class */
    public SimpleListVH mo257onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(C0592R.layout.md_simplelist_item, parent, false);
        return new SimpleListVH(view, this);
    }

    @Override // android.support.p003v7.widget.RecyclerView.Adapter
    public void onBindViewHolder(SimpleListVH holder, int position) {
        if (this.dialog != null) {
            MaterialSimpleListItem item = this.items.get(position);
            if (item.getIcon() != null) {
                holder.icon.setImageDrawable(item.getIcon());
                holder.icon.setPadding(item.getIconPadding(), item.getIconPadding(), item.getIconPadding(), item.getIconPadding());
                holder.icon.getBackground().setColorFilter(item.getBackgroundColor(), PorterDuff.Mode.SRC_ATOP);
            } else {
                holder.icon.setVisibility(8);
            }
            holder.title.setTextColor(this.dialog.getBuilder().getItemColor());
            holder.title.setText(item.getContent());
            this.dialog.setTypeface(holder.title, this.dialog.getBuilder().getRegularFont());
        }
    }

    @Override // android.support.p003v7.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.items.size();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class SimpleListVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        final MaterialSimpleListAdapter adapter;
        final ImageView icon;
        final TextView title;

        SimpleListVH(View itemView, MaterialSimpleListAdapter adapter) {
            super(itemView);
            this.icon = (ImageView) itemView.findViewById(16908294);
            this.title = (TextView) itemView.findViewById(16908310);
            this.adapter = adapter;
            itemView.setOnClickListener(this);
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (this.adapter.callback != null) {
                this.adapter.callback.onMaterialListItemSelected(this.adapter.dialog, getAdapterPosition(), this.adapter.getItem(getAdapterPosition()));
            }
        }
    }
}
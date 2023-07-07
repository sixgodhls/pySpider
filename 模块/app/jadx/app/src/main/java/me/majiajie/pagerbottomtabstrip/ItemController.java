package me.majiajie.pagerbottomtabstrip;

import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener;

/* loaded from: classes.dex */
public interface ItemController {
    void addTabItemSelectedListener(OnTabItemSelectedListener onTabItemSelectedListener);

    int getItemCount();

    String getItemTitle(int i);

    int getSelected();

    void setHasMessage(int i, boolean z);

    void setMessageNumber(int i, int i2);

    void setSelect(int i);
}

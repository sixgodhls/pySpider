package android.support.design.widget;

import android.app.Dialog;
import android.os.Bundle;
import android.support.p003v7.app.AppCompatDialogFragment;

/* loaded from: classes.dex */
public class BottomSheetDialogFragment extends AppCompatDialogFragment {
    @Override // android.support.p003v7.app.AppCompatDialogFragment, android.support.p000v4.app.DialogFragment
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new BottomSheetDialog(getContext(), getTheme());
    }
}
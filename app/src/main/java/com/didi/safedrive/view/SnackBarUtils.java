package com.didi.safedrive.view;

import android.support.design.widget.Snackbar;
import android.view.View;

import com.didi.safedrive.BaseApplication;
import com.didi.safedrive.R;

public class SnackBarUtils {
    private static Snackbar sSnackbar;

    public static void display(View view, String data) {
        if (sSnackbar == null) {
            sSnackbar = Snackbar.make(view, data, Snackbar.LENGTH_SHORT);
            sSnackbar.getView().setBackgroundColor(BaseApplication.getInst().getResources().getColor(R.color.color_common_bg_orange_dark));
            sSnackbar.setActionTextColor(BaseApplication.getInst().getResources().getColor(R.color.color_white));
        }
        sSnackbar.setText(data);
        sSnackbar.show();
    }
}

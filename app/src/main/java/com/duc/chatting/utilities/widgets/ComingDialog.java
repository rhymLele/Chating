package com.duc.chatting.utilities.widgets;

import android.app.Activity;
import android.app.Dialog;
import android.view.ViewGroup;

import com.duc.chatting.R;

public class ComingDialog extends Dialog {

    Activity prewedding_context;
    private int dpToPx(int dp) {
        return (int) (dp * getContext().getResources().getDisplayMetrics().density);
    }

    public ComingDialog(Activity activity) {
        super(activity);
        this.prewedding_context = activity;
        setContentView(R.layout.coming_dialog);
        getWindow().setLayout(dpToPx(350), ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawableResource(R.drawable.bg_loading);
    }

}


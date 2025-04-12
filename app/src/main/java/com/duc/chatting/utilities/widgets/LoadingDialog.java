package com.duc.chatting.utilities.widgets;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.duc.chatting.R;

import java.util.ArrayList;
import java.util.List;

public class LoadingDialog extends Dialog {

    Activity prewedding_context;
    private int dpToPx(int dp) {
        return (int) (dp * getContext().getResources().getDisplayMetrics().density);
    }

    public LoadingDialog(Activity activity) {
        super(activity);
        this.prewedding_context = activity;
        setContentView(R.layout.loading_dialog);
        getWindow().setLayout(dpToPx(350), ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawableResource(R.drawable.bg_loading);
    }

}

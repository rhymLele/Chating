package com.duc.chatting.utilities.widgets;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.duc.chatting.R;
import com.duc.chatting.utilities.services.NetworkLiveData;
import com.google.android.material.snackbar.Snackbar;

public abstract class BaseActivity extends AppCompatActivity {
    private Snackbar networkSnackbar;
    private boolean isNetworkConnected = true;
    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new NetworkLiveData(this).observe(this, isConnected -> {
            View rootView = findViewById(android.R.id.content);
            isNetworkConnected = isConnected;
            if (isConnected) {
                if (networkSnackbar != null && networkSnackbar.isShown()) {
                    networkSnackbar.dismiss();
                    Snackbar.make(rootView, " Đã kết nối lại", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                networkSnackbar = Snackbar.make(rootView, "⚠️ Mất kết nối mạng", Snackbar.LENGTH_INDEFINITE);
                networkSnackbar.setAction("Thử lại", v -> onRetryConnection());
                networkSnackbar.show();
            }
        });
    }
    protected abstract void onRetryConnection();
    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY; // Giữ chế độ fullscreen khi chạm vào màn hình
        decorView.setSystemUiVisibility(uiOptions);
    }
    public boolean isNetworkConnected() {
        return isNetworkConnected;
    }
}
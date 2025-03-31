package com.duc.chatting.messaging.views;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.duc.chatting.R;
import com.duc.chatting.databinding.ActivityNotificationBinding;

public class NotificationActivity extends AppCompatActivity {
    ActivityNotificationBinding binding;
    private static final String PREFS_NAME = "notification_prefs";
    private static final String NOTIFICATION_KEY = "allow_notifications";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        loadNotificationState(); //
        setListener();
    }
    void setListener(){
        binding.imageBack.setOnClickListener(v -> {
            this.onBackPressed();
        });
        binding.swCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this,"Allow",Toast.LENGTH_SHORT);
            saveNotificationState(isChecked);
        });
    }
    private void saveNotificationState(boolean isEnabled) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(NOTIFICATION_KEY, isEnabled);
        editor.apply();
    }

    private void loadNotificationState() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isEnabled = prefs.getBoolean(NOTIFICATION_KEY, true); // Mặc định là bật
        binding.swCheck.setChecked(isEnabled);
    }
}
package com.duc.chatting.chat.views;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.duc.chatting.R;
import com.duc.chatting.chat.adapters.ThemeAdapter;
import com.duc.chatting.chat.models.ThemeItem;
import com.duc.chatting.databinding.ActivityThemeBinding;

import java.util.ArrayList;
import java.util.List;

public class ThemeActivity extends AppCompatActivity {
    ActivityThemeBinding binding;
    ThemeAdapter adapter;
    private List<ThemeItem> themeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityThemeBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.themeList.setLayoutManager(new GridLayoutManager(this ,2));
        themeList = new ArrayList<>();
        themeList.add(new ThemeItem(R.color.AcliceBlue, "Default"));
        themeList.add(new ThemeItem(R.drawable.love, "Love"));
        themeList.add(new ThemeItem(R.drawable.star, "Star"));
        themeList.add(new ThemeItem(R.drawable.element, "Ocean"));
        themeList.add(new ThemeItem(R.drawable.kids, "Kids"));
        themeList.add(new ThemeItem(R.drawable.sun, "Sun"));
        adapter = new ThemeAdapter(this, themeList,this::onThemeClick);
        binding.themeList.setAdapter(adapter);
        binding.imageBack.setOnClickListener(v -> {
            finish();
        });
    }

    private void onThemeClick(ThemeItem themeItem) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("selectedTheme", themeItem.getName()); // Trả về theme
        setResult(RESULT_OK, resultIntent);
        finish();
    }


}
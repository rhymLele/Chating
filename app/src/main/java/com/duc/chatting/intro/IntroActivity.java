package com.duc.chatting.intro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.duc.chatting.R;
import com.duc.chatting.databinding.ActivityIntroBinding;
import com.duc.chatting.sign.view.MainSignActivity;
import com.duc.chatting.utilities.AppPreference;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {

ActivityIntroBinding binding;
    private int currentIndex = 0;
    AppPreference prefManager;
    private List<IntroScreen> screens;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        prefManager = AppPreference.getInstance(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        screens = new ArrayList<>();
        screens.add(new IntroScreen(R.drawable.intro1, getString(R.string.intro_title_1), getString(R.string.intro_message_1)));
        screens.add(new IntroScreen(R.drawable.intro2, getString(R.string.intro_title_2), getString(R.string.intro_message_2)));
        screens.add(new IntroScreen(R.drawable.intro3, getString(R.string.intro_title_3), getString(R.string.intro_message_3)));
        screens.add(new IntroScreen(R.drawable.intro4, getString(R.string.intro_title_4), getString(R.string.intro_message_4)));
        setupIntroScreen(currentIndex);
        setLis();
    }

    private void setLis() {
        binding.nextBtn.setOnClickListener(v -> {
            currentIndex++;
            if (currentIndex < screens.size()) {
                setupIntroScreen(currentIndex);
            } else {
                prefManager.setFirstTimeLaunch(false);
                startActivity(new Intent(this, MainSignActivity.class));
                finish(); // Close IntroActivity so user can't go back
            }
        });

        binding.skipText.setOnClickListener(v -> {
            setupIntroScreen(3);
        });
    }

    private void setupIntroScreen(int index) {

        IntroScreen screen = screens.get(index);
        binding.imageIntro.setImageResource(screen.imageResId);
        binding.titleIntro.setText(screen.title);
        binding.descIntro.setText(screen.description);

        binding.nextBtn.setText(index == screens.size() - 1 ? "Get started" : "Next");
        updateDots(index);
    }
    private void updateDots(int activeIndex) {
        binding.dotIndicator.removeAllViews();
        for (int i = 0; i < screens.size(); i++) {
            View dot = new View(this);
            dot.setLayoutParams(new LinearLayout.LayoutParams(20, 20));
            dot.setBackground(ContextCompat.getDrawable(this, i == activeIndex ? R.drawable.dot_active : R.drawable.dot_inactive));
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) dot.getLayoutParams();
            params.setMargins(8, 0, 8, 0);
            dot.setLayoutParams(params);
            binding.dotIndicator.addView(dot);
        }
    }
    static class IntroScreen {
        int imageResId;
        String title;
        String description;

        public IntroScreen(int imageResId, String title, String description) {
            this.imageResId = imageResId;
            this.title = title;
            this.description = description;
        }
    }
}
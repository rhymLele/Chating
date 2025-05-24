package com.duc.chatting.information_profile.views;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duc.chatting.R;
import com.duc.chatting.databinding.FragmentProfileBinding;
import com.duc.chatting.home.views.HomeActivity;
import com.duc.chatting.utilities.Contants;
import com.duc.chatting.utilities.PreferenceManager;


public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private NavController navController;
    private PreferenceManager preferenceManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        btnCLick();
        loadUserData();
        showSuggestions(view);
    }
    private void showSuggestions(View view) {
        String[] suggestions = {
                "✈️ Solo travel benefits",
                "🎓 Best schools in Europe",
                "💬 Quotes for workout",
                "🤖 Interview tips",
                "📚 Book recommendations 2025",
                "🍽️ Easy 15-min recipes",
                "💡 Daily motivation quotes",
                "💻 Learn coding with AI",
                "📈 How to invest safely",
                "🎵 Focus music playlist",
                "🧘 Breathe and meditate tips",
                "🌍 Eco-friendly lifestyle tips",
                "🏋️‍♀️ Home workout plan",
                "📝 Productivity hacks",
                "🎯 Goal setting strategies",
                "👨‍💼 Career development tips",
                "🧳 Travel checklist essentials",
                "💤 Sleep improvement habits",
                "🗣️ Public speaking tips",
                "📷 Instagram photo tips"
        };

        LinearLayout suggestionContainer = view.findViewById(R.id.suggestion_container);
        HorizontalScrollView scrollView = view.findViewById(R.id.suggestion_scroll);
        EditText messageEditText = view.findViewById(R.id.message_edit_text);

        suggestionContainer.removeAllViews(); // tránh trùng

        for (String suggestion : suggestions) {
            TextView chip = new TextView(getContext());
            chip.setText(suggestion);
            chip.setBackgroundResource(R.drawable.suggestion_chip_bg);
            chip.setPadding(40, 30, 40, 30);
            chip.setTextColor(Color.WHITE);
            chip.setTextSize(16);
            chip.setTypeface(Typeface.DEFAULT_BOLD);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(16, 8, 16, 8);
            chip.setLayoutParams(params);

            chip.setOnClickListener(v -> {
                messageEditText.setText(suggestion);
                messageEditText.setSelection(suggestion.length());
            });

            suggestionContainer.addView(chip);
        }

        // Bắt đầu scroll sau khi layout sẵn sàng
        scrollView.post(() -> startAutoScroll2(scrollView, suggestionContainer));
    }

    private Handler scrollHandler = new Handler();
    private Runnable scrollRunnable;
    private boolean isScrolling = true;
    private void stopAutoScrollAndHideSuggestions() {
        isScrolling = false;
        scrollHandler.removeCallbacks(scrollRunnable);

        HorizontalScrollView scrollView = requireView().findViewById(R.id.suggestion_scroll);
        scrollView.setVisibility(View.GONE);
    }
    private void startAutoScroll2(HorizontalScrollView scrollView, LinearLayout chipContainer) {
        int maxScrollX = chipContainer.getWidth() - scrollView.getWidth();

        ObjectAnimator animator = ObjectAnimator.ofInt(scrollView, "scrollX", 0, maxScrollX);
        animator.setDuration(80000); // 5 giây để scroll toàn bộ chiều dài
        animator.setInterpolator(new android.view.animation.LinearInterpolator()); // cuộn đều
        animator.setRepeatCount(ValueAnimator.INFINITE); // lặp vô hạn
        animator.setRepeatMode(ValueAnimator.REVERSE); // cuộn lại từ đầu

        animator.start();
    }


    private void btnCLick() {
        binding.imageToDetailProfile.setOnClickListener(v -> {
            navController.navigate(R.id.action_profileFragment_to_detailFragment);
        });
        binding.imageBack.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), HomeActivity.class);
            startActivity(i);
        });
    }

    private void loadUserData() {
        binding.textName.setText(preferenceManager.getString(Contants.KEY_NAME));
        if (preferenceManager.getString(Contants.KEY_STORY_HISTORY) != null) {
            binding.textStory.setText(preferenceManager.getString(Contants.KEY_STORY_HISTORY));
        }
        if (preferenceManager.getString(Contants.KEY_IMAGE) != null) {
            byte[] bytes = Base64.decode(preferenceManager.getString(Contants.KEY_IMAGE), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            binding.imgProfile.setImageBitmap(bitmap);

        }
        if (preferenceManager.getString(Contants.KEY_IMAGE_BANNER) != null) {
            byte[] bytes = Base64.decode(preferenceManager.getString(Contants.KEY_IMAGE_BANNER), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            binding.imgBanner.setImageBitmap(bitmap);

        }
    }
}
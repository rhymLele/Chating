package com.duc.chatting.information_profile.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
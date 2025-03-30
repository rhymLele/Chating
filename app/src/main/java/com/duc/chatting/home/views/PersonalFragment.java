package com.duc.chatting.home.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duc.chatting.R;
import com.duc.chatting.databinding.FragmentPersonalBinding;
import com.duc.chatting.home.viewmodels.ConservationViewModel;
import com.duc.chatting.information_profile.views.InformationProfileActivity;
import com.duc.chatting.sign.view.MainSignActivity;
import com.duc.chatting.utilities.Contants;
import com.duc.chatting.utilities.PreferenceManager;
import com.google.firebase.messaging.FirebaseMessaging;

import android.util.Base64;


public class PersonalFragment extends Fragment {

    private FragmentPersonalBinding binding;
    private ConservationViewModel viewModel;
    private PreferenceManager preferenceManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ConservationViewModel.class);
        preferenceManager = new PreferenceManager(getContext());
        viewModel.getIsCheckLogged().observe(this, isCheck -> {
            if (isCheck == Boolean.TRUE) {
                Intent intent = new Intent(getContext(), MainSignActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPersonalBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnClick();
        loadData();

    }

    private void loadData() {
        binding.textName.setText(preferenceManager.getString(Contants.KEY_NAME));
        if (preferenceManager.getString(Contants.KEY_IMAGE) != null) {
            byte[] bytes = Base64.decode(preferenceManager.getString(Contants.KEY_IMAGE), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            binding.imgProfile.setImageBitmap(bitmap);
        }
    }

    private void btnClick() {
        binding.imgProfile.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), InformationProfileActivity.class);
            startActivity(i);
        });
        binding.lnLogout.setOnClickListener(v -> {

            FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(task -> {
                if(task.isSuccessful())
                {
                    Log.d("LogOut","Delete FCM token");
                }else
                {
                    Log.d("LogOut","!Delete FCM token");
                }
            });
            viewModel.signOut();
        });
    }
}
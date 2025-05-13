package com.duc.chatting.home.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
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
import com.duc.chatting.messaging.views.NotificationActivity;
import com.duc.chatting.otherComponents.views.AboutActivity;
import com.duc.chatting.otherComponents.views.HelpActivity;
import com.duc.chatting.otherComponents.views.MyQrActivity;
import com.duc.chatting.poliso.views.BlockListActivity;
import com.duc.chatting.sign.view.MainSignActivity;
import com.duc.chatting.utilities.Contants;
import com.duc.chatting.utilities.PreferenceManager;
import com.google.firebase.messaging.FirebaseMessaging;

import android.util.Base64;
import android.widget.Toast;


public class PersonalFragment extends Fragment {

    private FragmentPersonalBinding binding;
    private ConservationViewModel viewModel;
    private PreferenceManager preferenceManager;
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private static final String PREFS_NAME = "mode_prefs";
    private static final String MODE_KEY = "mode";
    boolean isNightMode;    private boolean sw_fab;
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
        sw_fab=preferenceManager.getBoolean("s_fab");

        sharedPreferences= getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        isNightMode=sharedPreferences.getBoolean(MODE_KEY,false);
    }
    private void MyThemes(){
        if(isNightMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            editor.putBoolean("nightMode",false);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            editor.putBoolean("nightMode",true);
        }
        editor.apply();
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
        if(sw_fab)
        {
            binding.swFab.setChecked(false); // hoặc hide()
        }else          binding.swFab.setChecked(true);; // hoặc hide()

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
        binding.swMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(getContext(),"Switch Mode",Toast.LENGTH_SHORT);

        });
        binding.swFab.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (getActivity() instanceof HomeActivity) {
                ((HomeActivity) getActivity()).toggleFab(isChecked);
            }
        });
        binding.lnNotifcation.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), NotificationActivity.class);
            startActivity(i);
        });
        binding.lnSchedule.setOnClickListener(v -> {
            Toast.makeText(getContext(),"Go to storage",Toast.LENGTH_SHORT);
        });
        binding.lnNote.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), BlockListActivity.class);
            startActivity(i);
        });
        binding.lnHelp.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), HelpActivity.class);
            startActivity(i);
        });
        binding.lnAbout.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), AboutActivity.class);
            startActivity(i);
        });
        binding.imgQr.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), MyQrActivity.class);
            startActivity(i);
        });
    }
}
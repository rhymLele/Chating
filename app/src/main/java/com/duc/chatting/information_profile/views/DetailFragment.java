package com.duc.chatting.information_profile.views;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.duc.chatting.R;
import com.duc.chatting.databinding.FragmentDetailBinding;
import com.duc.chatting.information_profile.viewmodels.InformationProfileViewModel;
import com.duc.chatting.utilities.Contants;
import com.duc.chatting.utilities.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


public class DetailFragment extends Fragment {
    private FragmentDetailBinding binding;
    private NavController navController;
    private PreferenceManager preferenceManager;
    private InformationProfileViewModel viewModel;
    private String encodeImage = null;
    private String encodeImageBanner = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(InformationProfileViewModel.class);
        preferenceManager = new PreferenceManager(getContext());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDetailBinding.inflate(getLayoutInflater());    // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        loadUserDetails();
        binding.imgProfile.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            i.addFlags(i.FLAG_GRANT_READ_URI_PERMISSION);

//            i.setType("image/*");
            pickImage.launch(i);
        });
        binding.imgBanner.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            i.addFlags(i.FLAG_GRANT_READ_URI_PERMISSION);

//            i.setType("image/*");
            pickImageBanner.launch(i);
        });
        binding.imageBack.setOnClickListener(v ->
        {
            navController.navigate(R.id.action_detailFragment_to_profileFragment);
        });
        binding.textChangePassword.setOnClickListener(v -> {
            if (binding.llChangePassword.getVisibility() == View.GONE) {
                binding.llChangePassword.setVisibility(View.VISIBLE);
                binding.btnChangePassword.setVisibility(View.VISIBLE);
                binding.btnSave.setVisibility(View.GONE);
            } else if (binding.llChangePassword.getVisibility() == View.VISIBLE) {
                binding.llChangePassword.setVisibility(View.GONE);
                binding.btnChangePassword.setVisibility(View.GONE);
                binding.btnSave.setVisibility(View.VISIBLE);
            }
        });
        binding.btnSave.setOnClickListener(v -> {
            String name = binding.editName.getText().toString();
            String email = binding.editMail.getText().toString();
            String story = binding.editStory.getText().toString();
            String encodeImageOld = null;
            String encodeImageBannerOld = null;
            if (preferenceManager.getString(Contants.KEY_IMAGE) != null) {
                encodeImageOld = preferenceManager.getString(Contants.KEY_IMAGE);
            }
            if (preferenceManager.getString(Contants.KEY_IMAGE_BANNER) != null) {
                encodeImageBannerOld = preferenceManager.getString(Contants.KEY_IMAGE_BANNER);
            }

            if (encodeImage == null) {
                encodeImage = encodeImageOld;
            }
            if (encodeImageBanner == null) {
                encodeImageBanner = encodeImageBannerOld;
            }
            preferenceManager.putString(Contants.KEY_NAME, name);
            preferenceManager.putString(Contants.KEY_EMAIL, email);
            preferenceManager.putString(Contants.KEY_STORY_HISTORY, story);
            preferenceManager.putString(Contants.KEY_IMAGE, encodeImage);
            preferenceManager.putString(Contants.KEY_IMAGE_BANNER, encodeImageBanner);


            viewModel.saveDataToFirebase(name, email, story, encodeImage, encodeImageBanner);
            navController.navigate(R.id.action_detailFragment_to_profileFragment);
        });
        binding.btnChangePassword.setOnClickListener(v -> {
            if (isValidEmpty()) {
                Log.d("Pass","heree8");
                viewModel.checkPassword(preferenceManager.getString(Contants.KEY_USER_ID), binding.textOldPassword.getText().toString());
                viewModel.getIsCheckPasswordMutableLiveData().observe(getViewLifecycleOwner(), isCheck -> {
                    if (isCheck.equals(Boolean.TRUE)) {
                        binding.textCheckPassword.setVisibility(View.GONE);
                        Log.d("Pass","heree7");
                        if (isValid(binding.textNewPassword.getText().toString())) {
                            Log.d("Pass","heree6");
                            if(binding.textNewPassword.getText().toString().equals(binding.textNewConfPassword.getText().toString()))
                            {
                                Log.d("Pass","heree5");
                                viewModel.changePassword(binding.textNewPassword.getText().toString());
                                binding.llChangePassword.setVisibility(View.GONE);
                                binding.btnChangePassword.setVisibility(View.GONE);
                                binding.btnSave.setVisibility(View.VISIBLE);

                                binding.textCheckPassword.setVisibility(View.GONE);
                                binding.llCheckPassword.setVisibility(View.GONE);
                                binding.textNewConfPassword.setVisibility(View.GONE);

                                binding.textOldPassword.setText(null);
                                binding.textNewPassword.setText(null);
                                binding.textNewConfPassword.setText(null);
                                Log.d("Pass","heree44");

                            }else{
                                Log.d("Pass","heree3");
                                binding.textCheckConfirmPassword.setVisibility(View.VISIBLE);
                                binding.llCheckPassword.setVisibility(View.GONE);
                            }
                        } else {
                            Log.d("Pass","heree2");
                            binding.llCheckPassword.setVisibility(View.VISIBLE);
                            binding.textCheckPassword.setVisibility(View.GONE);
                        }
                    }else if(isCheck.equals(Boolean.FALSE)){
                        Log.d("Pass","heree1");
                        binding.textCheckPassword.setVisibility(View.VISIBLE);

                    }
                });
            }
        });
    }

    public boolean isValid(String passwordhere) {
        int f1 = 0, f2 = 0, f3 = 0;
        if (passwordhere.length() < 6) {
            return false;
        } else {
            for (int p = 0; p < passwordhere.length(); p++) {
                if (Character.isLetter(passwordhere.charAt(p))) {
                    f1 = 1;
                }
            }
            for (int r = 0; r < passwordhere.length(); r++) {
                if (Character.isDigit(passwordhere.charAt(r))) {
                    f2 = 1;
                }
            }
            for (int s = 0; s < passwordhere.length(); s++) {
                char c = passwordhere.charAt(s);
                if (c >= 33 && c <= 46 || c == 64) {
                    f3 = 1;
                }
            }
            if (f1 == 1 && f2 == 1 && f3 == 1) {
                return true;
            } else return false;
        }
    }

    private void loadUserDetails() {
        binding.textName.setText(preferenceManager.getString(Contants.KEY_NAME));
        binding.editName.setText(preferenceManager.getString(Contants.KEY_NAME));
        binding.editMail.setText(preferenceManager.getString(Contants.KEY_EMAIL));
        binding.editStory.setText(preferenceManager.getString(Contants.KEY_STORY_HISTORY));
        if (preferenceManager.getString(Contants.KEY_IMAGE) != null) {
            byte[] bytes = Base64.decode(preferenceManager.getString(Contants.KEY_IMAGE), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            binding.imgProfile.setImageBitmap(bitmap);
        }
        if (preferenceManager.getString(Contants.KEY_IMAGE_BANNER) != null) {
            byte[] bytes = Base64.decode(preferenceManager.getString(Contants.KEY_IMAGE), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            binding.imgBanner.setImageBitmap(bitmap);
        }
        if (preferenceManager.getString(Contants.KEY_STORY_HISTORY) != null) {
            binding.editStory.setText(preferenceManager.getString(Contants.KEY_STORY_HISTORY));
        }
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.imgProfile.setImageBitmap(bitmap);
                            encodeImage = encodeImage(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
    private final ActivityResultLauncher<Intent> pickImageBanner = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.imgBanner.setImageBitmap(bitmap);
                            encodeImageBanner = encodeImage(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public boolean isValidEmpty() {
        if (binding.textOldPassword.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Enter old password", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.textNewPassword.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Enter new password", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.textNewConfPassword.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Enter conf password", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
}
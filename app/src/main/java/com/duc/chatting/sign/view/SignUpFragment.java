package com.duc.chatting.sign.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.duc.chatting.R;
import com.duc.chatting.databinding.FragmentSignUpBinding;
import com.duc.chatting.sign.viewmodel.AuthenticationViewModel;


public class SignUpFragment extends Fragment {
    private FragmentSignUpBinding binding;
    private NavController navController;
    private AuthenticationViewModel viewModel;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel=new ViewModelProvider(this).get(AuthenticationViewModel.class);
        viewModel.getUserData().observe(this,firebaseUser->{
            if(firebaseUser!=null)
            {
                binding.textPhoneNumberCheckAlready.setVisibility(View.GONE);
                binding.llCheckPassword.setVisibility(View.GONE);
//                navController.navigate(R.id.action_signUpFragment_to_mainActivity);
                navController.navigate(R.id.action_signUpFragment_to_homeActivity);
            }
        });
        viewModel.getIsCheckPhoneNumberAlready().observe(this,isCheck->{
            if(isCheck.equals(Boolean.TRUE))
            {
                binding.textPhoneNumberCheckAlready.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        binding.tvHaveAccount.setOnClickListener(v -> {
            navController.navigate(R.id.action_signUpFragment_to_signInFragment);
        });
        binding.buttonSignIn.setOnClickListener(v -> {
            String email=binding.etSignEmail.getText().toString();
            String password=binding.etSignPassw.getText().toString();
            String phoneNumber=binding.etSignPhone.getText().toString();
            String name=binding.etSignName.getText().toString();
            if(isValidSignUpDetails())
            {
                if(isValid(password))
                {
                    Toast.makeText(getContext(), "Create Successfully.Hold on a minute!", Toast.LENGTH_SHORT).show();
                    viewModel.register(email,password,phoneNumber,name);
                }


            }else{
                binding.llCheckPassword.setVisibility(View.VISIBLE);
            }
        });
    }

    public Boolean isValidSignUpDetails() {
        if (binding.etSignEmail.getText().toString().trim().isEmpty()) {
            Toast.makeText(getContext(), "Enter email pls", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.etSignEmail.getText().toString()).matches()) {
            Toast.makeText(getContext(), "Email is invalid ...@gmail.com", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.etSignPhone.getText().toString().trim().isEmpty()) {
            Toast.makeText(getContext(), "Enter phone number pls", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.etSignEmail.length() < 6) {
            Toast.makeText(getContext(), "Phone number is invalid", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.etSignName.getText().toString().trim().isEmpty()) {
            Toast.makeText(getContext(), "Enter phone name pls", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.etSignPassw.getText().toString().trim().isEmpty()) {
            Toast.makeText(getContext(), "Enter password pls", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.etSignConf.getText().toString().trim().isEmpty()) {
            Toast.makeText(getContext(), "Enter confirm password pls", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!binding.etSignPassw.getText().toString().equals(binding.etSignConf.getText().toString())) {
            Toast.makeText(getContext(), "Password is not match", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }


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
            }else return false;

        }
    }
}
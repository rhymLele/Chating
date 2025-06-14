package com.duc.chatting.sign.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.duc.chatting.R;
import com.duc.chatting.call.repository.MainRepository;
import com.duc.chatting.databinding.FragmentSignInBinding;
import com.duc.chatting.sign.viewmodel.AuthenticationViewModel;
import com.duc.chatting.utilities.widgets.BaseActivity;
import com.duc.chatting.utilities.widgets.ComingDialog;
import com.duc.chatting.utilities.widgets.LoadingDialog;


public class SignInFragment extends Fragment {
    private FragmentSignInBinding binding;
    private NavController navController;
    private AuthenticationViewModel viewModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel=new ViewModelProvider(this).get(AuthenticationViewModel.class);
        ComingDialog loadingDialog=new ComingDialog(getActivity());
        viewModel.getUserData().observe(this,firebaseUser->{
            if(firebaseUser!=null)
            {
                loadingDialog.show();
                new Handler().postDelayed(() -> {
                    loadingDialog.dismiss();
                    binding.textCheckLogin.setVisibility(View.GONE);
//                navController.navigate(R.id.action_signInFragment_to_mainActivity);
                    navController.navigate(R.id.action_signInFragment_to_homeActivity);
                }, 2000);

            }else{
                Toast.makeText(getContext(),"Phone number or password is not correct",Toast.LENGTH_SHORT).show();
            }
        });
        viewModel.getIsCheckPhoneNumberAndPasswordLogin().observe(this,isChecked->{
            if(isChecked!=null)
            {
                binding.textCheckLogin.setVisibility(View.VISIBLE);

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        binding.tvCreateNewAccount.setOnClickListener(v -> {
            navController.navigate(R.id.action_signInFragment_to_signUpFragment);
        });
//        MainRepository mainRepository=MainRepository.getInstance();
        binding.buttonSignIn.setOnClickListener(v->{
            String pN=binding.etSignPhone.getText().toString();
            String pW=binding.etSignPassw.getText().toString();
            if (getActivity() instanceof BaseActivity) {
                BaseActivity baseActivity = (BaseActivity) getActivity();
                if (!baseActivity.isNetworkConnected()) {
                    Toast.makeText(getContext(), "No internet connection! Check your connection please!.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(isValidSignInDetails())
                {
                    viewModel.login(pN,pW);
//                mainRepository.login(pN,getActivity(),()->{

//                });
                }else{
                    Toast.makeText(getContext(), "Phone number of password is invalid!", Toast.LENGTH_SHORT).show();
                }
            }else {
                // Fallback in case the activity is not an instance of BaseActivity
                Toast.makeText(getContext(), "Lỗi: Activity không hỗ trợ kiểm tra mạng!", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private Boolean isValidSignInDetails() {
        if (binding.etSignPhone.getText().toString().trim().isEmpty()) {
            Toast.makeText(getContext(), "Enter phone number", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.etSignPassw.getText().toString().trim().isEmpty()) {
            Toast.makeText(getContext(), "Enter phone password", Toast.LENGTH_SHORT).show();
            return false;
        } else return true;
    }
}
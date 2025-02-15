package com.duc.chatting.home.views;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.duc.chatting.R;
import com.duc.chatting.databinding.FragmentConservationBinding;


public class ConservationFragment extends Fragment {
    private FragmentConservationBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       binding=FragmentConservationBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.addPerson)
        {
//            Intent intent=new Intent(getContext(),...);
//            startActivity(intent);
        }
        if(id==R.id.addGroup)
        {
//            Intent intent=new Intent(getContext(),...);
//            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
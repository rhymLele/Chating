package com.duc.chatting.home.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.duc.chatting.utilities.Contants;
import com.duc.chatting.utilities.PreferenceManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConservationViewModel extends AndroidViewModel {
    DatabaseReference databaseReference = FirebaseDatabase
            .getInstance()
            .getReferenceFromUrl("https://chatting-4faf6-default-rtdb.firebaseio.com/");
    private PreferenceManager preferenceManager;
    private MutableLiveData<Boolean> isCheckLogged;

    public ConservationViewModel(@NonNull Application application) {
        super(application);
        preferenceManager=new PreferenceManager(application);
        isCheckLogged=new MutableLiveData<>();
    }

    public MutableLiveData<Boolean> getIsCheckLogged() {
        return isCheckLogged;
    }

    public void signOut()
    {
        isCheckLogged.postValue(Boolean.TRUE);
        preferenceManager.clear();

    }

}

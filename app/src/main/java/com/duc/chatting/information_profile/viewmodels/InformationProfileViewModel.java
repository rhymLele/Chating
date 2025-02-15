package com.duc.chatting.information_profile.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.duc.chatting.utilities.Contants;
import com.duc.chatting.utilities.PreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InformationProfileViewModel extends AndroidViewModel {
    private PreferenceManager preferenceManager;
    private MutableLiveData<Boolean> isCheckPasswordMutableLiveData;
    DatabaseReference databaseReference = FirebaseDatabase
            .getInstance()
            .getReferenceFromUrl("https://chatting-4faf6-default-rtdb.firebaseio.com/");
    public MutableLiveData<Boolean> getIsCheckPasswordMutableLiveData() {
        return isCheckPasswordMutableLiveData;
    }

    public InformationProfileViewModel(@NonNull Application application) {
        super(application);
        isCheckPasswordMutableLiveData=new MutableLiveData<>();
        preferenceManager=new PreferenceManager(application);

    }
    public void saveDataToFirebase(String name,String email,String story,String encodeImage,String encodeImageBanner)
    {
        databaseReference.child(Contants.KEY_COLLECTION_USERS).child(preferenceManager.getString(Contants.KEY_USER_ID)).child(Contants.KEY_NAME).setValue(name);
        databaseReference.child(Contants.KEY_COLLECTION_USERS).child(preferenceManager.getString(Contants.KEY_USER_ID)).child(Contants.KEY_IMAGE).setValue(encodeImage);
        databaseReference.child(Contants.KEY_COLLECTION_USERS).child(preferenceManager.getString(Contants.KEY_USER_ID)).child(Contants.KEY_EMAIL).setValue(email);
        databaseReference.child(Contants.KEY_COLLECTION_USERS).child(preferenceManager.getString(Contants.KEY_USER_ID)).child(Contants.KEY_STORY_HISTORY).setValue(story);
        databaseReference.child(Contants.KEY_COLLECTION_USERS).child(preferenceManager.getString(Contants.KEY_USER_ID)).child(Contants.KEY_IMAGE_BANNER).setValue(encodeImageBanner);

        preferenceManager.putString(Contants.KEY_NAME,name);
        preferenceManager.putString(Contants.KEY_EMAIL,email);
        preferenceManager.putString(Contants.KEY_STORY_HISTORY,story);
        preferenceManager.putString(Contants.KEY_IMAGE,encodeImage);
        preferenceManager.putString(Contants.KEY_IMAGE_BANNER,encodeImageBanner);
    }
    public void checkPassword(String phoneNumber,String password1)
    {
        String password=String.valueOf(password1.hashCode());
        databaseReference.child(Contants.KEY_COLLECTION_USERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(phoneNumber))
                {
                    String getPassword=snapshot.child(phoneNumber).child(Contants.KEY_PASSWORD).getValue(String.class);
                    if(getPassword.equals(password))
                    {
                        isCheckPasswordMutableLiveData.postValue(Boolean.TRUE);

                    }else{
                        isCheckPasswordMutableLiveData.postValue(Boolean.FALSE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void changePassword(String password1)
    {
        String password=String.valueOf(password1.hashCode());
        databaseReference.child(Contants.KEY_COLLECTION_USERS).child(preferenceManager.getString(Contants.KEY_USER_ID)).child(Contants.KEY_PASSWORD).setValue(password);

    }

}

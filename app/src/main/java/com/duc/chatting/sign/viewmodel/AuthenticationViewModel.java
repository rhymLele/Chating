package com.duc.chatting.sign.viewmodel;

import android.app.Application;
import android.util.Log;

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
import com.google.firebase.firestore.FirebaseFirestore;

public class AuthenticationViewModel extends AndroidViewModel {
    private PreferenceManager preferenceManager;
    private MutableLiveData<String> userData;
    private MutableLiveData<Boolean> isCheckPhoneNumberAlready;
    private MutableLiveData<Boolean> isCheckPhoneNumberAndPasswordLogin;

    public MutableLiveData<String> getUserData() {
        return userData;
    }

    public MutableLiveData<Boolean> getIsCheckPhoneNumberAlready() {
        return isCheckPhoneNumberAlready;
    }

    public MutableLiveData<Boolean> getIsCheckPhoneNumberAndPasswordLogin() {
        return isCheckPhoneNumberAndPasswordLogin;
    }

    DatabaseReference databaseReference = FirebaseDatabase
            .getInstance()
            .getReferenceFromUrl("https://chatting-4faf6-default-rtdb.firebaseio.com/");

    public AuthenticationViewModel(Application application) {
        super(application);
        preferenceManager = new PreferenceManager(application);
        userData = new MutableLiveData<>();
        isCheckPhoneNumberAlready = new MutableLiveData<>();
        isCheckPhoneNumberAndPasswordLogin = new MutableLiveData<>();
    }
    public void login(String phoneNumber,String password){
        String password1=String.valueOf(password.hashCode());
        databaseReference.child(Contants.KEY_COLLECTION_USERS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild(phoneNumber))
                        {
                            String getPassword=snapshot.child(phoneNumber).child(Contants.KEY_PASSWORD).getValue(String.class);
                            Log.d("Auth", "User found, checking password...");
                            if(getPassword.equals(password1))
                            {    Log.d("Auth", "Password matched, setting userData...");
                                preferenceManager.putBoolean(Contants.KEY_IS_SIGNED_IN,true);
                                preferenceManager.putString(Contants.KEY_USER_ID,phoneNumber);
                                preferenceManager.putString(Contants.KEY_EMAIL,snapshot
                                        .child(phoneNumber)
                                        .child(Contants.KEY_EMAIL)
                                        .getValue(String.class));
                                preferenceManager.putString(Contants.KEY_PHONE_NUMBER,phoneNumber);

                                preferenceManager.putString(Contants.KEY_NAME,snapshot
                                        .child(phoneNumber)
                                        .child(Contants.KEY_NAME)
                                        .getValue(String.class));
                                preferenceManager.putString(Contants.KEY_IMAGE,snapshot
                                        .child(phoneNumber)
                                        .child(Contants.KEY_IMAGE)
                                        .getValue(String.class));
                                preferenceManager.putString(Contants.KEY_IMAGE_BANNER,snapshot
                                        .child(phoneNumber)
                                        .child(Contants.KEY_IMAGE_BANNER)
                                        .getValue(String.class));
                                preferenceManager.putString(Contants.KEY_STORY_HISTORY,snapshot
                                        .child(phoneNumber)
                                        .child(Contants.KEY_STORY_HISTORY)
                                        .getValue(String.class));
                                userData.postValue(snapshot
                                        .child(phoneNumber)
                                        .child(Contants.KEY_EMAIL)
                                        .getValue(String.class));

                            }else{
                                Log.d("Auth", "Password incorrect");
                                isCheckPhoneNumberAndPasswordLogin.postValue(Boolean.TRUE);

                            }
                        }
                        else{
                            Log.d("Auth", "Phone number not found");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    public void register(String email,String password1,String phoneNumber,String name){
        String password=String.valueOf(password1.hashCode());
        databaseReference.child(Contants.KEY_COLLECTION_USERS).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild(phoneNumber))
                        {
                            isCheckPhoneNumberAlready.postValue(Boolean.TRUE);

                        }else {
                            databaseReference.child(Contants.KEY_COLLECTION_USERS).child(phoneNumber).child(Contants.KEY_EMAIL).setValue(email);
                            databaseReference.child(Contants.KEY_COLLECTION_USERS).child(phoneNumber).child(Contants.KEY_PASSWORD).setValue(password);
                            databaseReference.child(Contants.KEY_COLLECTION_USERS).child(phoneNumber).child(Contants.KEY_PHONE_NUMBER).setValue(phoneNumber);
                            databaseReference.child(Contants.KEY_COLLECTION_USERS).child(phoneNumber).child(Contants.KEY_NAME).setValue(name);

                            preferenceManager.putBoolean(Contants.KEY_IS_SIGNED_IN,true);
                            preferenceManager.putString(Contants.KEY_USER_ID,phoneNumber);
                            preferenceManager.putString(Contants.KEY_EMAIL,email);
                            preferenceManager.putString(Contants.KEY_PHONE_NUMBER,phoneNumber);
                            preferenceManager.putString(Contants.KEY_NAME,name);
                            userData.postValue(email);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    }
}

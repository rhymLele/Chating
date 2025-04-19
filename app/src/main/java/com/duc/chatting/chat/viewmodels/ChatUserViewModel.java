package com.duc.chatting.chat.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.duc.chatting.chat.models.User;
import com.duc.chatting.utilities.Contants;
import com.duc.chatting.utilities.PreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatUserViewModel extends AndroidViewModel {
    private PreferenceManager preferenceManager;
    private MutableLiveData<List<User>> userMutableLiveData;

    public MutableLiveData<List<User>> getUserMutableLiveData() {
        return userMutableLiveData;
    }

    DatabaseReference databaseReference = FirebaseDatabase
            .getInstance()
            .getReferenceFromUrl("https://chatting-4faf6-default-rtdb.firebaseio.com/");

    public ChatUserViewModel(@NonNull Application application) {
        super(application);
        preferenceManager = new PreferenceManager(application);
        userMutableLiveData = new MutableLiveData<>();

    }

    public void getUsers(String phoneNumber) {
        databaseReference.child(Contants.KEY_COLLECTION_USERS).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<User> users = new ArrayList<>();
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            if (preferenceManager.getString(Contants.KEY_USER_ID).equals(postSnapshot.child(Contants.KEY_PHONE_NUMBER).getValue(String.class))) {
                                continue;
                            }
                            String phoneNumberTest = postSnapshot.child(Contants.KEY_PHONE_NUMBER).getValue(String.class);
                            if (phoneNumber.equals(phoneNumberTest)) {
                                String id = postSnapshot.child(Contants.KEY_PHONE_NUMBER).getValue(String.class);
                                String email = postSnapshot.child(Contants.KEY_EMAIL).getValue(String.class);
                                String name = postSnapshot.child(Contants.KEY_NAME).getValue(String.class);
                                String image = null, imageBanner = null, story = null;
                                if (postSnapshot.child(Contants.KEY_IMAGE).getValue(String.class) != null) {
                                    image = postSnapshot.child(Contants.KEY_IMAGE).getValue(String.class);
                                }
                                if (postSnapshot.child(Contants.KEY_IMAGE_BANNER).getValue(String.class) != null) {
                                    imageBanner = postSnapshot.child(Contants.KEY_IMAGE_BANNER).getValue(String.class);
                                }
                                if (postSnapshot.child(Contants.KEY_STORY_HISTORY).getValue(String.class) != null) {
                                    imageBanner = postSnapshot.child(Contants.KEY_STORY_HISTORY).getValue(String.class);
                                }
                                User user = new User(id, email, name, phoneNumber, image, imageBanner, story);
                                users.add(user);
                                userMutableLiveData.postValue(users);

                            }
                        }
                        //                        if(users.size()==0)
                        //                        {
                        //
                        //                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    }
    public void getUser(String phoneNumber, OnUserLoadedListener listener) {
        databaseReference.child(Contants.KEY_COLLECTION_USERS).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            if (preferenceManager.getString(Contants.KEY_USER_ID).equals(postSnapshot.child(Contants.KEY_PHONE_NUMBER).getValue(String.class))) {
                                continue;
                            }
                            String phoneNumberTest = postSnapshot.child(Contants.KEY_PHONE_NUMBER).getValue(String.class);
                            if (phoneNumber.equals(phoneNumberTest)) {
                                String id = postSnapshot.child(Contants.KEY_PHONE_NUMBER).getValue(String.class);
                                String email = postSnapshot.child(Contants.KEY_EMAIL).getValue(String.class);
                                String name = postSnapshot.child(Contants.KEY_NAME).getValue(String.class);
                                String image = postSnapshot.child(Contants.KEY_IMAGE).getValue(String.class);
                                String imageBanner = postSnapshot.child(Contants.KEY_IMAGE_BANNER).getValue(String.class);
                                String story = postSnapshot.child(Contants.KEY_STORY_HISTORY).getValue(String.class);

                                User user = new User(id, email, name, phoneNumber, image, imageBanner, story);
                                listener.onUserLoaded(user);
                                return;
                            }
                        }
                        // Không tìm thấy
                        listener.onUserLoaded(null);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.onUserLoaded(null); // hoặc handle lỗi
                    }
                }
        );
    }
    public interface OnUserLoadedListener {
        void onUserLoaded(User user);
    }

}

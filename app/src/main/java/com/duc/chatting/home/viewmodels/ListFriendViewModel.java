package com.duc.chatting.home.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.duc.chatting.chat.models.ChatMessage;
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

public class ListFriendViewModel extends AndroidViewModel {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://chatting-4faf6-default-rtdb.firebaseio.com/");
    private PreferenceManager preferenceManager;
    private List<User> users;
    private MutableLiveData<List<User>> friendsMutableLiveData;
    public ListFriendViewModel(@NonNull Application application) {

        super(application);
        preferenceManager=new PreferenceManager(application);
        friendsMutableLiveData=new MutableLiveData<>();
        users=new ArrayList<>();

    }
    public MutableLiveData<List<User>> getFriendsMutableLiveData() {
        return friendsMutableLiveData;
    }
    public void showListFriend(){
        String myId = preferenceManager.getString(Contants.KEY_USER_ID);
        databaseReference.child(Contants.KEY_COLLECTION_FRIEND).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot friendSnapshot : snapshot.getChildren()) {
                    String status = friendSnapshot.child("status").getValue(String.class);
                    String userID_1 = friendSnapshot.child("userID_1").getValue(String.class);
                    String userID_2 = friendSnapshot.child("userID_2").getValue(String.class);

                    if ("enable".equals(status)) {
                        String friendId = null;
                        if(myId.equals(userID_1))
                        {
                            friendId = userID_2;
                            Log.d("FriendList",userID_2);
                        }else if(myId.equals(userID_2))
                        {
                            friendId = userID_1;
                            Log.d("FriendList",userID_1);
                        }

                        if (friendId != null) {
                            getUsers(friendId);
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to load friends: " + error.getMessage());
            }
        });
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
                                Log.d("FriendList",name+" "+id);
                                User user = new User(id, email, name, phoneNumber, image, imageBanner, story);
                                users.add(user);
                            }

                        }
                        friendsMutableLiveData.postValue(users);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    }

}

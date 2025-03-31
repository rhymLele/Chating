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
    private List<User> usersRequest;
    private MutableLiveData<List<User>> friendsMutableLiveData;

    public MutableLiveData<List<User>> getFriendRequestMutableLiveData() {
        return friendRequestMutableLiveData;
    }

    private MutableLiveData<List<User>> friendRequestMutableLiveData;
    public ListFriendViewModel(@NonNull Application application) {

        super(application);
        preferenceManager=new PreferenceManager(application);
        friendsMutableLiveData=new MutableLiveData<>();
        friendRequestMutableLiveData=new MutableLiveData<>();
        users=new ArrayList<>();
        usersRequest = new ArrayList<>();

    }
    public MutableLiveData<List<User>> getFriendsMutableLiveData() {
        return friendsMutableLiveData;
    }
    public void getListFriendRequest()
    {
        String myId = preferenceManager.getString(Contants.KEY_USER_ID);
        databaseReference.child(Contants.KEY_COLLECTION_FRIEND).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersRequest.clear();
                for (DataSnapshot friendSnapshot : snapshot.getChildren()) {
                    String status = friendSnapshot.child("status").getValue(String.class);
                    String userID_2 = friendSnapshot.child("userID_2").getValue(String.class);
                    String senderID = friendSnapshot.child("userID_Send").getValue(String.class);
                    if(userID_2!=null&&senderID!=null)
                    if (userID_2.equals(myId) && status.equals("disable"))
                        if (senderID != null) {
                            getUsers(senderID, user -> {
                                if (user != null) {
                                    usersRequest.add(user);
                                    friendRequestMutableLiveData.postValue(usersRequest);
                                }
                            });

                    }
                }
                friendRequestMutableLiveData.postValue(usersRequest);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
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
                            getUsers(friendId, user -> {
                                if (user != null) {
                                    users.add(user);
                                    friendsMutableLiveData.postValue(users);
                                }
                            });
                        }

                    }
                }
                friendsMutableLiveData.postValue(users);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to load friends: " + error.getMessage());
            }
        });
    }
    User a;
    public void getUsers(String phoneNumber, UserCallback callback) {
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
                                Log.d("FriendList",name+" "+id);
                                callback.onUserRetrieved(user);
                                return;
                            }
                        }
//                        if(type==1) friendsMutableLiveData.postValue(users);
//                        else if(type==2) friendRequestMutableLiveData.postValue(users);
                        callback.onUserRetrieved(null);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onUserRetrieved(null);
                    }
                }
        );
    }
    public interface UserCallback {
        void onUserRetrieved(User user);
    }
}

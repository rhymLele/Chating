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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListFriendViewModel extends AndroidViewModel {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://chatting-4faf6-default-rtdb.firebaseio.com/");
    private PreferenceManager preferenceManager;
    private List<User> users;
    private List<User> usersRequest;
    private Set<User> usersRequester;
    private MutableLiveData<List<User>> friendsMutableLiveData;
    private MutableLiveData<Integer> frRequest;
    private MutableLiveData<List<User>> friendRequestMutableLiveData;
    private MutableLiveData<Integer> frAll;
    private String keyFriendId=null;
    public MutableLiveData<Integer> getFrAll() {
        return frAll;
    }

    public MutableLiveData<Integer> getFrRequest() {
        return frRequest;
    }



    public MutableLiveData<List<User>> getFriendRequestMutableLiveData() {
        return friendRequestMutableLiveData;
    }


    public ListFriendViewModel(@NonNull Application application) {

        super(application);
        preferenceManager=new PreferenceManager(application);
        friendsMutableLiveData=new MutableLiveData<>();
        friendRequestMutableLiveData=new MutableLiveData<>();
        frRequest=new MutableLiveData<>();
        frAll=new MutableLiveData<>();
        users=new ArrayList<>();
        usersRequest = new ArrayList<>();
        usersRequester = new HashSet<>();

    }
    public MutableLiveData<List<User>> getFriendsMutableLiveData() {
        return friendsMutableLiveData;
    }
    public void getListFriendRequest()
    {
        String myId = preferenceManager.getString(Contants.KEY_USER_ID);
        Set<String> processedSenderIds = new HashSet<>();
        databaseReference.child(Contants.KEY_COLLECTION_FRIEND).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersRequest.clear();
                processedSenderIds.clear();
                for (DataSnapshot friendSnapshot : snapshot.getChildren()) {
                    String status = friendSnapshot.child("status").getValue(String.class);
                    String userID_2 = friendSnapshot.child("userID_2").getValue(String.class);
                    String senderID = friendSnapshot.child("userID_Send").getValue(String.class);
                    if(userID_2!=null&&status!=null)
                    if (userID_2.equals(myId) && status.equals("disable"))
                        if (senderID != null) {
                            keyFriendId=friendSnapshot.getKey();
                            getUsers(senderID, user -> {
                                if (user != null&&!usersRequester.contains(user)) {
                                    usersRequester.add(user);
                                    usersRequest.add(user);
                                    friendRequestMutableLiveData.postValue(usersRequest);
                                    frRequest.postValue(usersRequest.size());
                                }
                            });
                    }
                }

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
                        }else if(myId.equals(userID_2))
                        {
                            friendId = userID_1;
                        }
                        if (friendId != null) {
                            getUsers(friendId, user -> {
                                if (user != null && !users.contains(user)) {  // Kiểm tra tránh trùng lặp
                                    users.add(user);
                                    friendsMutableLiveData.postValue(users);
                                    frAll.postValue(users.size());
                                }
                            });
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
    public void acceptFriend(User user) {
        databaseReference.child(Contants.KEY_COLLECTION_FRIEND).child(keyFriendId).child(Contants.KEY_STATUS).setValue("enable").addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Xóa khỏi danh sách yêu cầu kết bạn
                usersRequest.remove(user);
                friendRequestMutableLiveData.postValue(usersRequest);

                // Thêm vào danh sách bạn bè
//                users.add(user);
                friendsMutableLiveData.postValue(users);
                frRequest.postValue(usersRequest.size());
            }
        });
    }

    public void destroyAddFriend(User user) {
        databaseReference.child(Contants.KEY_COLLECTION_FRIEND).child(keyFriendId).child(Contants.KEY_STATUS).setValue("").addOnCompleteListener(v -> {
            usersRequest.remove(user);
            friendRequestMutableLiveData.postValue(usersRequest);
            frRequest.postValue(usersRequest.size());
        });
    }
}

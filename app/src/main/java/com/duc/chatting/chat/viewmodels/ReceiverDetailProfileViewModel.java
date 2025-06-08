package com.duc.chatting.chat.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.duc.chatting.chat.models.User;
import com.duc.chatting.utilities.Contants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReceiverDetailProfileViewModel extends AndroidViewModel {
    DatabaseReference databaseReference = FirebaseDatabase
            .getInstance()
            .getReferenceFromUrl("https://chatting-4faf6-default-rtdb.firebaseio.com/");
    private MutableLiveData<String> isChecked;
    private MutableLiveData<Boolean> isCheckedFriend;
    private MutableLiveData<User> userMutableLiveData;
    private String keyFriendId = null;

    public MutableLiveData<String> getIsChecked() {
        return isChecked;
    }

    public MutableLiveData<Boolean> getIsCheckedFriend() {
        return isCheckedFriend;
    }

    public MutableLiveData<User> getUserMutableLiveData() {
        return userMutableLiveData;
    }

    public ReceiverDetailProfileViewModel(@NonNull Application application) {
        super(application);
        isChecked = new MutableLiveData<>();
        isCheckedFriend = new MutableLiveData<>();
        userMutableLiveData = new MutableLiveData<>();
    }

    public void dataUser(String userID) {
        databaseReference.child(Contants.KEY_COLLECTION_USERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child(userID).child(Contants.KEY_NAME).getValue(String.class);
                String email = snapshot.child(userID).child(Contants.KEY_EMAIL).getValue(String.class);
                String imgProfile = snapshot.child(userID).child(Contants.KEY_IMAGE).getValue(String.class);
                String imgBanner = snapshot.child(userID).child(Contants.KEY_IMAGE_BANNER).getValue(String.class);
                String story = snapshot.child(userID).child(Contants.KEY_STORY_HISTORY).getValue(String.class);
                String phoneNumber = snapshot.child(userID).child(Contants.KEY_PHONE_NUMBER).getValue(String.class);

                User user = new User(userID, email, name, phoneNumber, imgProfile, imgBanner, story);
                userMutableLiveData.postValue(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void listenAddFriend(String userID1, String userID2) {
        databaseReference.child(Contants.KEY_COLLECTION_FRIEND).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String userID1F = dataSnapshot.child(Contants.KEY_USER_ID_1).getValue(String.class);
                    String userID2F = dataSnapshot.child(Contants.KEY_USER_ID_2).getValue(String.class);
                    String userIDSend = dataSnapshot.child(Contants.KEY_USER_ID_SEND).getValue(String.class);
                    String status = dataSnapshot.child(Contants.KEY_STATUS).getValue(String.class);
                    if (userID1F != null && userID2F != null && userIDSend != null && status != null) {
                        if ((userID1.equals(userID1F) && userID2.equals(userID2F)) ||( userID1.equals(userID2F) && userID2.equals(userID1F)))
                        {
                            keyFriendId = dataSnapshot.getKey();
                            if (status.equals("disable")) {
                                if (userIDSend.equals(userID1)) {
                                    isChecked.postValue("my send");
                                } else if (userIDSend.equals(userID2)) {
                                    isChecked.postValue("your send");
                                }
                            } else if (status.equals("enable")) {
                                isChecked.postValue("we friend");
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addFriend(String userID1, String userID2) {

        String keyID = databaseReference.child(Contants.KEY_COLLECTION_USERS).push().getKey();
        databaseReference.child(Contants.KEY_COLLECTION_FRIEND).child(keyID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (keyFriendId != null) {
                    databaseReference.child(Contants.KEY_COLLECTION_FRIEND).child(keyFriendId).child(Contants.KEY_USER_ID_1).setValue(userID1);
                    databaseReference.child(Contants.KEY_COLLECTION_FRIEND).child(keyFriendId).child(Contants.KEY_USER_ID_2).setValue(userID2);
                    databaseReference.child(Contants.KEY_COLLECTION_FRIEND).child(keyFriendId).child(Contants.KEY_USER_ID_SEND).setValue(userID1);
                    databaseReference.child(Contants.KEY_COLLECTION_FRIEND).child(keyFriendId).child(Contants.KEY_STATUS).setValue("disable");
                } else {
                    databaseReference.child(Contants.KEY_COLLECTION_FRIEND).child(keyID).child(Contants.KEY_USER_ID_1).setValue(userID1);
                    databaseReference.child(Contants.KEY_COLLECTION_FRIEND).child(keyID).child(Contants.KEY_USER_ID_2).setValue(userID2);
                    databaseReference.child(Contants.KEY_COLLECTION_FRIEND).child(keyID).child(Contants.KEY_USER_ID_SEND).setValue(userID1);
                    databaseReference.child(Contants.KEY_COLLECTION_FRIEND).child(keyID).child(Contants.KEY_STATUS).setValue("disable");
                    keyFriendId = keyID;


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void acceptFriend() {
        databaseReference.child(Contants.KEY_COLLECTION_FRIEND).child(keyFriendId).child(Contants.KEY_STATUS).setValue("enable").addOnCompleteListener(v -> {
            isCheckedFriend.postValue(Boolean.TRUE);
        });
    }

    public void destroyAddFriend() {
        databaseReference.child(Contants.KEY_COLLECTION_FRIEND).child(keyFriendId).child(Contants.KEY_STATUS).setValue("").addOnCompleteListener(v -> {
            isCheckedFriend.postValue(Boolean.FALSE);
        });
        keyFriendId = null;
    }
}

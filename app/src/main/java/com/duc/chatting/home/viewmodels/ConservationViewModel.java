package com.duc.chatting.home.viewmodels;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.duc.chatting.chat.models.ChatMessage;
import com.duc.chatting.utilities.Contants;
import com.duc.chatting.utilities.PreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConservationViewModel extends AndroidViewModel {
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://chatting-4faf6-default-rtdb.firebaseio.com/");
    private final PreferenceManager preferenceManager;
    private final MutableLiveData<List<ChatMessage>> conservationsMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isCheckLogged = new MutableLiveData<>();
    private final List<ChatMessage> conservations = new ArrayList<>();
    private final Set<String> listGroupID = new HashSet<>();

    public ConservationViewModel(@NonNull Application application) {
        super(application);
        preferenceManager = new PreferenceManager(application);
    }

    public MutableLiveData<List<ChatMessage>> getConservationsMutableLiveData() {
        return conservationsMutableLiveData;
    }

    public MutableLiveData<Boolean> getIsCheckLogged() {
        return isCheckLogged;
    }

    public void signOut() {
        isCheckLogged.postValue(true);
        String userId = preferenceManager.getString(Contants.KEY_USER_ID);
        databaseReference.child(Contants.KEY_COLLECTION_USERS).child(userId)
                .child(Contants.KEY_STATUS).setValue("Offline")
                .addOnSuccessListener(aVoid -> Log.i("Status", "Updated to Offline"))
                .addOnFailureListener(e -> Log.e("Status", "Failed to update", e));
        preferenceManager.clear();
    }

    public void listenConservations(String userID) {
        conservations.clear();
        listGroupID.clear();

        listenGroupMembership(userID);
        listenConversationChanges(userID);
    }

    private void listenGroupMembership(String userID) {
        databaseReference.child(Contants.KEY_COLLECTION_GROUP_MEMBER)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            String memberID = data.child(Contants.KEY_GROUP_MEMBER_TIME_USERID_ADD).getValue(String.class);
                            String altMemberID = data.child("userID").getValue(String.class);
                            if (userID.equals(memberID) || userID.equals(altMemberID)) {
                                String groupID = data.child(Contants.KEY_GROUP_CHAT_ID).getValue(String.class);
                                if (groupID != null) listGroupID.add(groupID);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("GroupMembership", "Error: " + error.getMessage());
                    }
                });
    }

    private void listenConversationChanges(String userID) {
        databaseReference.child(Contants.KEY_COLLECTION_CONVERSATIONS)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        conservations.clear();

                        for (DataSnapshot data : snapshot.getChildren()) {
                            ChatMessage message = extractChatMessage(data, userID);
                            if (message != null) {
                                conservations.add(message);
                            }
                        }

                        conservations.sort((a, b) -> b.getDateObject().compareTo(a.getDateObject()));
                        conservationsMutableLiveData.postValue(new ArrayList<>(conservations));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Conversations", "Error: " + error.getMessage());
                    }
                });
    }

    private ChatMessage extractChatMessage(DataSnapshot data, String userID) {
        String senderID = data.child(Contants.KEY_SENDER_ID).getValue(String.class);
        String receiverID = data.child(Contants.KEY_RECEIVER_ID).getValue(String.class);

        String lastMessage = data.child(Contants.KEY_LAST_MESSAGE).getValue(String.class);
        Date dateObject = data.child(Contants.KEY_TIMESTAMP).getValue(Date.class);

        if (userID.equals(senderID)) {
            return new ChatMessage(
                    senderID,
                    data.child(Contants.KEY_SENDER_NAME).getValue(String.class),
                    receiverID,
                    lastMessage,
                    dateObject,
                    data.child(Contants.KEY_RECEIVER_ID).getValue(String.class),
                    data.child(Contants.KEY_RECEIVER_NAME).getValue(String.class),
                    data.child(Contants.KEY_RECEIVER_IMAGE).getValue(String.class)
            );
        } else if (userID.equals(receiverID)) {
            return new ChatMessage(
                    senderID,
                    data.child(Contants.KEY_SENDER_NAME).getValue(String.class),
                    receiverID,
                    lastMessage,
                    dateObject,
                    data.child(Contants.KEY_SENDER_ID).getValue(String.class),
                    data.child(Contants.KEY_SENDER_NAME).getValue(String.class),
                    data.child(Contants.KEY_SENDER_IMAGE).getValue(String.class)
            );
        } else if (listGroupID.contains(receiverID)) {
            return new ChatMessage(
                    senderID,
                    data.child(Contants.KEY_SENDER_NAME).getValue(String.class),
                    receiverID,
                    lastMessage,
                    dateObject,
                    receiverID,
                    data.child(Contants.KEY_RECEIVER_NAME).getValue(String.class),
                    data.child(Contants.KEY_RECEIVER_IMAGE).getValue(String.class)
            );
        }
        return null;
    }
}


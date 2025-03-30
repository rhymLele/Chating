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
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://chatting-4faf6-default-rtdb.firebaseio.com/");
    private String userID = null;
    private MutableLiveData<List<ChatMessage>> conservationsMutableLiveData;
    private List<ChatMessage> conservations;

    private PreferenceManager preferenceManager;
    private MutableLiveData<Boolean> isCheckLogged;

    public MutableLiveData<List<ChatMessage>> getConservationsMutableLiveData() {
        return conservationsMutableLiveData;
    }

    public MutableLiveData<Boolean> getIsCheckLogged() {return isCheckLogged;}

    public ConservationViewModel(@NonNull Application application) {
        super(application);
        preferenceManager = new PreferenceManager(application);
        isCheckLogged = new MutableLiveData<>();
        conservationsMutableLiveData = new MutableLiveData<>();
        conservations = new ArrayList<>();
    }
    public void signOut() {
        isCheckLogged.postValue(Boolean.TRUE);
        databaseReference.child(Contants.KEY_COLLECTION_USERS).child(preferenceManager.getString(Contants.KEY_USER_ID))
                .child(Contants.KEY_STATUS).setValue("Offline")
                .addOnSuccessListener(aVoid -> System.out.println("Status updated!"))
                .addOnFailureListener(e -> System.err.println("Failed to update status: " + e.getMessage()));;
        preferenceManager.clear();
    }
    public void listenConservations(String userID) {
        conservations.clear();
        Set<String> listGroupID = new HashSet<>();

        databaseReference.child(Contants.KEY_COLLECTION_GROUP_MEMBER).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("ConservationViewModel","UseID:"+preferenceManager.getString(Contants.KEY_USER_ID));
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Log.d("ConservationViewModel","IDGR con: "+dataSnapshot.child(Contants.KEY_GROUP_CHAT_ID).getValue(String.class));
                    if (preferenceManager.getString(Contants.KEY_USER_ID)
                            .equals(dataSnapshot.child(Contants.KEY_GROUP_MEMBER_TIME_USERID_ADD).getValue(String.class))) {

                        String groupIDCon = dataSnapshot.child(Contants.KEY_GROUP_CHAT_ID).getValue(String.class);
                        listGroupID.add(groupIDCon);
                        Log.d("ConservationViewModel","IDGroup:"+dataSnapshot.child(Contants.KEY_GROUP_CHAT_ID).getValue(String.class));

                    } else if (preferenceManager.getString(Contants.KEY_USER_ID)
                            .equals(dataSnapshot.child("userID").getValue(String.class))) {

                        String groupIDCon = dataSnapshot.child(Contants.KEY_GROUP_CHAT_ID).getValue(String.class);
                        listGroupID.add(groupIDCon);
                        Log.d("ConservationViewModel","IDGroup2:"+dataSnapshot.child(Contants.KEY_GROUP_CHAT_ID).getValue(String.class));
                    }
                }
                for(String s : listGroupID){
                    Log.d("ConservationViewModel","IDgroup:"+ s);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.child(Contants.KEY_COLLECTION_CONVERSATIONS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("FirebaseData", "Snapshot exists: " + snapshot.exists());
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String senderID = dataSnapshot.child(Contants.KEY_SENDER_ID).getValue(String.class);
                    String receiverID = dataSnapshot.child(Contants.KEY_RECEIVER_ID).getValue(String.class);
                    String conservationImage = null;
                    String conservationName = null;
                    String conservationID = null;
                    String senderName = null;
                    if (userID.equals(senderID)) {
                        conservationImage = dataSnapshot.child(Contants.KEY_RECEIVER_IMAGE).getValue(String.class);
                        conservationName = dataSnapshot.child(Contants.KEY_RECEIVER_NAME).getValue(String.class);
                        conservationID = dataSnapshot.child(Contants.KEY_RECEIVER_ID).getValue(String.class);
                        String lastMessage = dataSnapshot.child(Contants.KEY_LAST_MESSAGE).getValue(String.class);
                        Date dateObject = dataSnapshot.child(Contants.KEY_TIMESTAMP).getValue(Date.class);
                        ChatMessage chatMessage = new ChatMessage(senderID, senderName, receiverID, lastMessage, dateObject,
                                conservationID, conservationName, conservationImage);
                        conservations.add(chatMessage);
                        Log.d("CVM", "Added chat message: " + chatMessage.toString());
                        continue;

                    }
                    else if (userID.equals(receiverID)) {
                        conservationImage = dataSnapshot.child(Contants.KEY_SENDER_IMAGE).getValue(String.class);
                        conservationName = dataSnapshot.child(Contants.KEY_SENDER_NAME).getValue(String.class);
                        conservationID = dataSnapshot.child(Contants.KEY_SENDER_ID).getValue(String.class);
                        senderName = dataSnapshot.child(Contants.KEY_SENDER_NAME).getValue(String.class);
                        String lastMessage = dataSnapshot.child(Contants.KEY_LAST_MESSAGE).getValue(String.class);
                        Date dateObject = dataSnapshot.child(Contants.KEY_TIMESTAMP).getValue(Date.class);
                        ChatMessage chatMessage = new ChatMessage(senderID, senderName, receiverID, lastMessage, dateObject,
                                conservationID, conservationName, conservationImage);
                        conservations.add(chatMessage);
                        Log.d("CVM", "Added chat message: " + chatMessage.toString());
                        continue;
                    }
                    else {
                        for (String s : listGroupID) {
                            if (s.equals(receiverID)) {
                                Log.d("receiverID",s);
                                conservationImage = dataSnapshot.child(Contants.KEY_RECEIVER_IMAGE).getValue(String.class);
                                conservationName = dataSnapshot.child(Contants.KEY_RECEIVER_NAME).getValue(String.class);
                                conservationID = dataSnapshot.child(Contants.KEY_RECEIVER_ID).getValue(String.class);
                                senderName = dataSnapshot.child(Contants.KEY_SENDER_NAME).getValue(String.class);
                                String lastMessage = dataSnapshot.child(Contants.KEY_LAST_MESSAGE).getValue(String.class);
                                Date dateObject = dataSnapshot.child(Contants.KEY_TIMESTAMP).getValue(Date.class);
                                ChatMessage chatMessage = new ChatMessage(senderID, senderName, receiverID, lastMessage, dateObject,
                                        conservationID, conservationName, conservationImage);
                                conservations.add(chatMessage);
                            }
                        }
                    }

                }
                Log.d("LiveData", "Updating LiveData with size: " + conservations.size());
                conservations.sort((obj1, obj2) -> obj2.getDateObject().compareTo(obj1.getDateObject()));
                conservationsMutableLiveData.postValue(conservations);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
        conservations.sort((obj1, obj2) -> obj2.getDateObject().compareTo(obj1.getDateObject()));
        conservationsMutableLiveData.postValue(conservations);
    }





}

package com.duc.chatting.chat.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.duc.chatting.chat.models.ChatMessage;
import com.duc.chatting.chat.models.Conservation;
import com.duc.chatting.utilities.Contants;
import com.duc.chatting.utilities.PreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivityViewModel extends AndroidViewModel {
    private PreferenceManager preferenceManager;
    private MutableLiveData<List<ChatMessage>> chatMessagesMutableLiveData;
    private MutableLiveData<Boolean> sentInputMessage;
    private MutableLiveData<Boolean> count0MutableLiveData;
    private MutableLiveData<Boolean> loadChatRecycleView;
    private MutableLiveData<Boolean> loadProgressBarMutableLiveData;
    private MutableLiveData<String> themeConservationMutableLiveData;
    private MutableLiveData<Boolean> isCheckActiveMutableLiveData;
    private MutableLiveData<String> conservationIDMutableLiveData;
    private List<ChatMessage> chatMessages;
    private String conservationID=null;
    private boolean isChecked=false;
    DatabaseReference databaseReference = FirebaseDatabase
            .getInstance()
            .getReferenceFromUrl("https://chatting-4faf6-default-rtdb.firebaseio.com/");

    public MutableLiveData<List<ChatMessage>> getChatMessagesMutableLiveData() {
        return chatMessagesMutableLiveData;
    }

    public MutableLiveData<Boolean> getSentInputMessage() {
        return sentInputMessage;
    }

    public MutableLiveData<Boolean> getCount0MutableLiveData() {
        return count0MutableLiveData;
    }

    public MutableLiveData<Boolean> getLoadChatRecycleView() {
        return loadChatRecycleView;
    }

    public MutableLiveData<Boolean> getLoadProgressBarMutableLiveData() {
        return loadProgressBarMutableLiveData;
    }

    public MutableLiveData<String> getThemeConservationMutableLiveData() {
        return themeConservationMutableLiveData;
    }

    public MutableLiveData<Boolean> getIsCheckActiveMutableLiveData() {
        return isCheckActiveMutableLiveData;
    }

    public MutableLiveData<String> getConservationIDMutableLiveData() {
        return conservationIDMutableLiveData;
    }

    public ChatActivityViewModel(@NonNull Application application) {
        super(application);
        preferenceManager=new PreferenceManager(application);
        chatMessages=new ArrayList<>();
        chatMessagesMutableLiveData = new MutableLiveData<>();
        sentInputMessage = new MutableLiveData<>();
        count0MutableLiveData = new MutableLiveData<>();
        loadChatRecycleView = new MutableLiveData<>();
        loadProgressBarMutableLiveData = new MutableLiveData<>();
        themeConservationMutableLiveData = new MutableLiveData<>();
        isCheckActiveMutableLiveData = new MutableLiveData<>();
        conservationIDMutableLiveData = new MutableLiveData<>();
    }
    public void sendMessage(String senderID,String senderName,String senderImage,String receiverID,String receiveName,String receiverImage,String messageChat){
        databaseReference.child(Contants.KEY_COLLECTION_CHAT).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ChatMessage c=new ChatMessage(senderID,senderName,senderImage,receiverID,messageChat,new Date());
                    databaseReference.child(Contants.KEY_COLLECTION_CHAT).push().setValue(c);
                    sentInputMessage.postValue(true);
                    if(conservationID!=null){
                        updateConservation(messageChat,senderID,senderName,senderImage,receiverID,receiveName,receiverImage);
                    }else if(conservationID==null){
                        Conservation conservation=new Conservation(senderID,senderName,senderImage,receiverID,receiveName,receiverImage,messageChat,new Date());
                        addConservation(conservation);
                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
public void sendMessageRepLocal(String senderID,String senderName,String senderImage,String receiverID,String receiverName,String receiverImage,String messageChat,String messageRepLocal,String urlFileRepLocal,String urlImageRepLocal){
        databaseReference.child(Contants.KEY_COLLECTION_CHAT).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ChatMessage c=new ChatMessage(senderID,senderName,senderImage,receiverID,messageChat,messageRepLocal,urlFileRepLocal,urlImageRepLocal,new Date());
                databaseReference.child(Contants.KEY_COLLECTION_CHAT).push().setValue(c);
                sentInputMessage.postValue(true);
                if(conservationID!=null){
                    updateConservation(messageChat,senderID,senderName,senderImage,receiverID,receiverName,receiverImage);
                }else if(conservationID==null){
                    Conservation conservation=new Conservation(senderID,senderName,senderImage,receiverID,receiverName,receiverImage,messageChat,new Date());
                    addConservation(conservation);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
}


    //save conservation
    private void addConservation(Conservation conservation){
        conservationID=databaseReference.child(Contants.KEY_COLLECTION_CONVERSATIONS).push().getKey();
        databaseReference.child(Contants.KEY_COLLECTION_CONVERSATIONS).child(conservationID).setValue(conservation);
        databaseReference.child(Contants.KEY_COLLECTION_CONVERSATIONS).child(conservationID).child(Contants.KEY_SEEN).setValue("disable");
    }
    //update last message for conservation
    private void updateConservation(String message,String senderID,String senderName,String senderImage,String receiverID,String receiverName,String receiverImage){
        databaseReference.child(Contants.KEY_COLLECTION_CONVERSATIONS).child(conservationID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                databaseReference.child(Contants.KEY_COLLECTION_CONVERSATIONS).child(conservationID).child(Contants.KEY_LAST_MESSAGE).setValue(message);
                databaseReference.child(Contants.KEY_COLLECTION_CONVERSATIONS).child(conservationID).child(Contants.KEY_TIMESTAMP).setValue(new Date());
                databaseReference.child(Contants.KEY_COLLECTION_CONVERSATIONS).child(conservationID).child(Contants.KEY_SENDER_ID).setValue(senderID);
                databaseReference.child(Contants.KEY_COLLECTION_CONVERSATIONS).child(conservationID).child(Contants.KEY_SENDER_NAME).setValue(senderName);
                databaseReference.child(Contants.KEY_COLLECTION_CONVERSATIONS).child(conservationID).child(Contants.KEY_SENDER_IMAGE).setValue(senderImage);
                databaseReference.child(Contants.KEY_COLLECTION_CONVERSATIONS).child(conservationID).child(Contants.KEY_RECEIVER_ID).setValue(receiverID);
                databaseReference.child(Contants.KEY_COLLECTION_CONVERSATIONS).child(conservationID).child(Contants.KEY_RECEIVER_NAME).setValue(receiverName);
                databaseReference.child(Contants.KEY_COLLECTION_CONVERSATIONS).child(conservationID).child(Contants.KEY_RECEIVER_IMAGE).setValue(receiverImage);
                databaseReference.child(Contants.KEY_COLLECTION_CONVERSATIONS).child(conservationID).child(Contants.KEY_SEEN).setValue("disable");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

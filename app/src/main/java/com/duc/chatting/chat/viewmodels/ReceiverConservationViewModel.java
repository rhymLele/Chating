package com.duc.chatting.chat.viewmodels;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.duc.chatting.chat.models.ImageClass;
import com.duc.chatting.chat.models.PDFClass;
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

public class ReceiverConservationViewModel extends AndroidViewModel {
    DatabaseReference databaseReference = FirebaseDatabase
            .getInstance()
            .getReferenceFromUrl("https://chatting-4faf6-default-rtdb.firebaseio.com/");
    private PreferenceManager preferenceManager;
    private MutableLiveData<User> receiverMutableLiveData;
    private MutableLiveData<Boolean> isCheckedGroupChatPersonalMutableLiveData;
    private MutableLiveData<List<User>> listUserMutableLiveData;
    private MutableLiveData<List<PDFClass>> listPDFMutableLiveData;
    private MutableLiveData<List<ImageClass>> listImageMutableLiveData;

    public MutableLiveData<Boolean> getIsUserInGroup() {
        return isUserInGroup;
    }

    private MutableLiveData<Boolean> isUserInGroup;

    private MutableLiveData<Boolean> isCheckedCon;
    private List<User> listUser;
    private List<PDFClass> listPDF;
    private List<ImageClass> listImage;

    public MutableLiveData<User> getReceiverMutableLiveData() {
        return receiverMutableLiveData;
    }
    public MutableLiveData<Boolean> getIsCheckedCon() {
        return isCheckedCon;
    }

    public MutableLiveData<Boolean> getIsCheckedGroupChatPersonalMutableLiveData() {
        return isCheckedGroupChatPersonalMutableLiveData;
    }

    public MutableLiveData<List<User>> getListUserMutableLiveData() {
        return listUserMutableLiveData;
    }

    public MutableLiveData<List<PDFClass>> getListPDFMutableLiveData() {
        return listPDFMutableLiveData;
    }

    public MutableLiveData<List<ImageClass>> getListImageMutableLiveData() {
        return listImageMutableLiveData;
    }

    public ReceiverConservationViewModel(@NonNull Application application) {
        super(application);
        preferenceManager = new PreferenceManager(application);
        receiverMutableLiveData = new MutableLiveData<>();
        isCheckedGroupChatPersonalMutableLiveData = new MutableLiveData<>();
        listUserMutableLiveData = new MutableLiveData<>();
        listImageMutableLiveData = new MutableLiveData<>();
        listPDFMutableLiveData = new MutableLiveData<>();
        isUserInGroup=new MutableLiveData<>();
        isCheckedCon=new MutableLiveData<>();
        listUser = new ArrayList<>();
        listPDF = new ArrayList<>();
        listImage = new ArrayList<>();
    }

    public void getReiverConservation(String conservationID, String receiverID) {
        isCheckedGroupChatPersonalMutableLiveData.postValue(Boolean.FALSE);
        databaseReference.child(Contants.KEY_COLLECTION_CONVERSATIONS).child(conservationID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String senderIDCon = snapshot.child(Contants.KEY_SENDER_ID).getValue(String.class);
                        String receiverIDCon = snapshot.child(Contants.KEY_RECEIVER_ID).getValue(String.class);
                        if(senderIDCon!=null&&receiverIDCon!=null)
                        {
                            isCheckedCon.postValue(Boolean.TRUE);
                            if (senderIDCon.equals(receiverID)) {
                                String receiverImage = snapshot.child(Contants.KEY_SENDER_IMAGE).getValue(String.class);
                                String receiverName = snapshot.child(Contants.KEY_SENDER_NAME).getValue(String.class);
                                User user = new User(receiverID, receiverName, receiverImage);
                                receiverMutableLiveData.postValue(user);
                            } else if (receiverIDCon.equals(receiverID)) {
                                String receiverImage = snapshot.child(Contants.KEY_RECEIVER_IMAGE).getValue(String.class);
                                String receiverName = snapshot.child(Contants.KEY_RECEIVER_NAME).getValue(String.class);
                                User user = new User(receiverID, receiverName, receiverImage);
                                receiverMutableLiveData.postValue(user);
                            }
                        }else
                        {
                            isCheckedCon.postValue(Boolean.FALSE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        databaseReference.child(Contants.KEY_COLLECTION_GROUP_CHAT).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String groupChatID = postSnapshot.getKey();
                    if (groupChatID.equals(receiverID)) {
                        isCheckedGroupChatPersonalMutableLiveData.postValue(Boolean.TRUE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void checkUserInGroupChat(String currentID, String groupID) {
        databaseReference.child(Contants.KEY_COLLECTION_GROUP_MEMBER)
                .addListenerForSingleValueEvent (new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean found = false;
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            String group = postSnapshot.child(Contants.KEY_GROUP_CHAT_ID).getValue(String.class);
                            String userID = postSnapshot.child("userID").getValue(String.class);
                            if (groupID.equals(group) && currentID.equals(userID)&&isFirebasePushKey(groupID)) {
                                Toast.makeText(getApplication(),groupID,Toast.LENGTH_SHORT).show();
                                found = true;
                                break;
                            }
                        }
                        isUserInGroup.postValue(found);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        isUserInGroup.postValue(false); // fallback nếu lỗi
                    }
                });
    }
    public void setNameGroup(String conservationID, String receiverID, String newName) {
        databaseReference.child(Contants.KEY_COLLECTION_CONVERSATIONS).child(conservationID).child(Contants.KEY_RECEIVER_NAME).setValue(newName);
        databaseReference.child(Contants.KEY_COLLECTION_GROUP_CHAT).child(receiverID).child(Contants.KEY_GROUP_CHAT_NAME).setValue(newName);
    }

    public void getMemeberGroupChat(String groupID) {
        listUser.clear();
        databaseReference.child(Contants.KEY_COLLECTION_GROUP_MEMBER).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    if (groupID.equals(postSnapshot.child(Contants.KEY_GROUP_CHAT_ID).getValue(String.class))) {
                        String userID = postSnapshot.child("userID").getValue(String.class);
                        String image = postSnapshot.child(Contants.KEY_IMAGE).getValue(String.class);
                        String name = postSnapshot.child(Contants.KEY_NAME).getValue(String.class);
                        String userIDAdd = postSnapshot.child(Contants.KEY_GROUP_MEMBER_TIME_USERID_ADD).getValue(String.class);
                        String userNameAdd = postSnapshot.child(Contants.KEY_GROUP_MEMBER_USER_NAME_ADD).getValue(String.class);
                        User user = new User(userID, name, image, userIDAdd, userNameAdd);
                        listUser.add(user);
                    }
                }
                listUserMutableLiveData.postValue(listUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void leaveGroup(String groupId, String userId) {
        databaseReference.child(Contants.KEY_COLLECTION_GROUP_MEMBER)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            String currentGroupId = postSnapshot.child(Contants.KEY_GROUP_CHAT_ID).getValue(String.class);
                            String currentUserId = postSnapshot.child("userID").getValue(String.class);

                            if (groupId.equals(currentGroupId) && userId.equals(currentUserId)) {
                                // Xóa node user khỏi group
                                postSnapshot.getRef().removeValue();
                                Toast.makeText(getApplication(),"Out",Toast.LENGTH_SHORT);
                                isUserInGroup.postValue(false);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Xử lý lỗi nếu cần
                    }
                });
    }
    public void getListFile(String conservationID) {
        listPDF.clear();
        databaseReference.child(Contants.KEY_COLLECTION_FILE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String conservationIdFile = postSnapshot.child("conservationID").getValue(String.class);
                    if (conservationID.equals(conservationIdFile)) {
                        String name = postSnapshot.child(Contants.KEY_FILENAME).getValue(String.class);
                        String url = postSnapshot.child(Contants.KEY_URL_FILE).getValue(String.class);
                        String statusFile = postSnapshot.child(Contants.KEY_STATUS_FILE).getValue(String.class);
                        PDFClass pdfClass = new PDFClass(name, url, statusFile);
                        listPDF.add(pdfClass);
                        Log.d(":asd",String.valueOf(listPDF.size()));
                    }
                }
                listPDFMutableLiveData.postValue(listPDF);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void getListImage(String conservationID) {
        listImage.clear();
        databaseReference.child(Contants.KEY_COLLECTION_IMAGE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String conservationIdImage = postSnapshot.child("conservationID").getValue(String.class);
                    if (conservationID.equals(conservationIdImage)) {
                        String imageUrl = postSnapshot.child(Contants.KEY_URL_IMAGE).getValue(String.class);
                        String statusImage = postSnapshot.child(Contants.KEY_STATUS_IMAGE).getValue(String.class);
                        ImageClass imageClass = new ImageClass(imageUrl, statusImage);
                        listImage.add(imageClass);
                    }
                }
                listImageMutableLiveData.postValue(listImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void setThemeConservation(String conservationId,String theme)
    {
        databaseReference.child(Contants.KEY_COLLECTION_CONVERSATIONS).child(conservationId).child(Contants.KEY_BACKGROUND_CONVERSATION).setValue(theme);
    }

    private boolean isFirebasePushKey(String key) {
        return key.startsWith("-") && key.length() == 20;
    }


}

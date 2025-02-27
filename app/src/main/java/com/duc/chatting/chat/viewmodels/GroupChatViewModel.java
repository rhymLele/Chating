package com.duc.chatting.chat.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.duc.chatting.chat.models.GroupMember;
import com.duc.chatting.chat.models.User;
import com.duc.chatting.utilities.Contants;
import com.duc.chatting.utilities.PreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class GroupChatViewModel extends AndroidViewModel {
    private MutableLiveData<List<User>> listUserGroupMutableLiveData;
    private MutableLiveData<User> userGroupMutableLiveData;
    private String groupID="";
    private List<User> userListGroup;
    private List<String> listGroupID;
    private String isCheckedUserId=null;
    private PreferenceManager preferenceManager;

    DatabaseReference databaseReference = FirebaseDatabase
            .getInstance()
            .getReferenceFromUrl("https://chatting-4faf6-default-rtdb.firebaseio.com/");


    public MutableLiveData<List<User>> getListUserGroupMutableLiveData() {
        return listUserGroupMutableLiveData;
    }

    public MutableLiveData<User> getUserGroupMutableLiveData() {
        return userGroupMutableLiveData;
    }
    public GroupChatViewModel(@NonNull Application application) {
        super(application);
        preferenceManager=new PreferenceManager(application);
        listUserGroupMutableLiveData=new MutableLiveData<>();
        userGroupMutableLiveData=new MutableLiveData<>();
        userListGroup=new ArrayList<>();
        listGroupID=new ArrayList<>();
    }
    public void getUserGroup(){
        userListGroup.clear();
        databaseReference.child(Contants.KEY_COLLECTION_CONVERSATIONS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot postSnapshot:snapshot.getChildren()){
                    String senderID=postSnapshot.child(Contants.KEY_SENDER_ID).getValue(String.class);
                    String receiverID=postSnapshot.child(Contants.KEY_RECEIVER_ID).getValue(String.class);
                    String conservationImage=null;
                    String conservationName=null;
                    String conservationID=null;
                    isCheckedUserId=null;
                    if(preferenceManager.getString(Contants.KEY_USER_ID).equals(senderID)){
                        conservationImage=postSnapshot.child(Contants.KEY_RECEIVER_IMAGE).getValue(String.class);
                        conservationName=postSnapshot.child(Contants.KEY_RECEIVER_NAME).getValue(String.class);
                        conservationID=postSnapshot.child(Contants.KEY_RECEIVER_ID).getValue(String.class);
                        User user=new User(conservationID,conservationName,conservationImage);
                        userListGroup.add(user);

                    }else if(preferenceManager.getString(Contants.KEY_USER_ID).equals(receiverID)){
                        conservationImage=postSnapshot.child(Contants.KEY_SENDER_IMAGE).getValue(String.class);
                        conservationName=postSnapshot.child(Contants.KEY_SENDER_NAME).getValue(String.class);
                        conservationID=postSnapshot.child(Contants.KEY_SENDER_ID).getValue(String.class);
                        User user=new User(conservationID,conservationName,conservationImage);
                        userListGroup.add(user);
                    }
                    databaseReference.child(Contants.KEY_COLLECTION_CHAT).child(receiverID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(!snapshot.hasChild(Contants.KEY_GROUP_CHAT_NAME)){
                                listUserGroupMutableLiveData.postValue(userListGroup);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void getUserGroupForPhoneNumber(String phoneNumber){
        userListGroup.clear();
        databaseReference.child(Contants.KEY_COLLECTION_USERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    if(preferenceManager.getString(Contants.KEY_USER_ID).equals(dataSnapshot.child(Contants.KEY_PHONE_NUMBER).getValue(String.class))){
                        continue;
                    }
                    String phoneNumberTest=dataSnapshot.child(Contants.KEY_PHONE_NUMBER).getValue(String.class);
                    if(phoneNumber.equals(phoneNumberTest))
                    {
                        String id=dataSnapshot.child(Contants.KEY_USER_ID).getValue(String.class);
                        String name=dataSnapshot.child(Contants.KEY_NAME).getValue(String.class);
                        String image=null;
                        if(dataSnapshot.child(Contants.KEY_IMAGE).getValue(String.class)!=null){
                            image=dataSnapshot.child(Contants.KEY_IMAGE).getValue(String.class);
                        }
                        User user=new User(id,name,image);
                        userListGroup.add(user);
                        listUserGroupMutableLiveData.postValue(userListGroup);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //create new group and add user click to group
    public void addUserToGroup(Set<User> userNumberGroup,String nameGroup){
        String nameGroup1="";
        if(!nameGroup.isEmpty())
        {
            nameGroup1=nameGroup;
        }else{
            for(User user:userNumberGroup){
                nameGroup1+=user.getName() +" ,";

            }
        }
        String groupChatID=databaseReference.child(Contants.KEY_COLLECTION_GROUP_CHAT).push().getKey();
        databaseReference.child(Contants.KEY_COLLECTION_GROUP_CHAT).child(groupChatID).child(Contants.KEY_GROUP_CHAT_NAME).setValue(nameGroup1);
        User userGroup =new User(groupChatID,nameGroup1);
        userGroupMutableLiveData.postValue(userGroup);
        for(User user:userNumberGroup){
            GroupMember groupMember=new GroupMember(groupChatID,
                    user.getId()
                    ,user.getName()
                    ,user.getImgProfile()
                    ,new Date()
                    ,preferenceManager.getString(Contants.KEY_USER_ID)
                    ,preferenceManager.getString(Contants.KEY_NAME));
            databaseReference.child(Contants.KEY_COLLECTION_GROUP_MEMBER).push().setValue(groupMember);
        }
    }
}

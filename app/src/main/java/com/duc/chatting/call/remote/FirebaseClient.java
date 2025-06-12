package com.duc.chatting.call.remote;

import androidx.annotation.NonNull;

import com.duc.chatting.call.interfaces.ErrorCallBack;
import com.duc.chatting.call.interfaces.NewEventCallBack;
import com.duc.chatting.call.interfaces.SuccessCallBack;
import com.duc.chatting.call.models.DataModel;
import com.duc.chatting.utilities.Contants;
import com.duc.chatting.utilities.PreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.Objects;

public class FirebaseClient {
    DatabaseReference databaseReference = FirebaseDatabase
            .getInstance()
            .getReferenceFromUrl("https://chatting-4faf6-default-rtdb.firebaseio.com/");
    private PreferenceManager preferenceManager;
    private final Gson  gson=new Gson();
    private static final String LATEST_EVENT_FIELD_NAME = "latest_event";
    private String currentId;
    public void login(String currentIds, SuccessCallBack callBack){
        databaseReference.child(Contants.KEY_CALL).child(currentIds).setValue("").addOnCompleteListener(task -> {
            currentId = currentIds;
            callBack.onSuccess();
        });
    }
    public void end(String currentIds, SuccessCallBack callBack){
        databaseReference.child(Contants.KEY_CALL).child(currentIds).setValue("").addOnCompleteListener(task -> {
            currentId = currentIds;
            callBack.onSuccess();
        });
    }
    public void sendMessageToOtherUser(DataModel dataModel, ErrorCallBack errorCallBack){
        databaseReference.child(Contants.KEY_CALL).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(dataModel.getTarget()).exists()){
                    //send the signal to other user
                    databaseReference.child(Contants.KEY_CALL).child(dataModel.getTarget()).child(LATEST_EVENT_FIELD_NAME)
                            .setValue(gson.toJson(dataModel));

                }else {
                    errorCallBack.onError();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorCallBack.onError();
            }
        });
    }

    public void observeIncomingLatestEvent(NewEventCallBack callBack){
        databaseReference.child(Contants.KEY_CALL).child(currentId).child(LATEST_EVENT_FIELD_NAME).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try{
                            String data= Objects.requireNonNull(snapshot.getValue()).toString();
                            DataModel dataModel = gson.fromJson(data,DataModel.class);
                            callBack.onNewEventReceived(dataModel);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );


    }
//    public void sư()
//    {
//        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
//        Query query = usersRef.orderByChild("isOnline").equalTo(true);
//
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//                for (DataSnapshot child : snapshot.getChildren()) {
//                    User user = child.getValue(User.class);
//                    Log.d("UserOnline", "User: " + user.getName());
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError error) {}
//        });
//
//
//    }
}

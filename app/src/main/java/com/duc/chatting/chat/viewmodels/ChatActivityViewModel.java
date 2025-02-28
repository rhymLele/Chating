package com.duc.chatting.chat.viewmodels;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.duc.chatting.chat.models.ChatMessage;
import com.duc.chatting.chat.models.Conservation;
import com.duc.chatting.chat.models.PDFClass;
import com.duc.chatting.utilities.Contants;
import com.duc.chatting.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.units.qual.C;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private String conservationID = null;
    private boolean isChecked = false;
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
        preferenceManager = new PreferenceManager(application);
        chatMessages = new ArrayList<>();
        chatMessagesMutableLiveData = new MutableLiveData<>();
        sentInputMessage = new MutableLiveData<>();
        count0MutableLiveData = new MutableLiveData<>();
        loadChatRecycleView = new MutableLiveData<>();
        loadProgressBarMutableLiveData = new MutableLiveData<>();
        themeConservationMutableLiveData = new MutableLiveData<>();
        isCheckActiveMutableLiveData = new MutableLiveData<>();
        conservationIDMutableLiveData = new MutableLiveData<>();
    }

    public void sendMessage(String senderID, String senderName, String senderImage, String receiverID, String receiveName, String receiverImage, String messageChat) {
        databaseReference.child(Contants.KEY_COLLECTION_CHAT).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ChatMessage c = new ChatMessage(senderID, senderName, senderImage, receiverID, messageChat, new Date());
                databaseReference.child(Contants.KEY_COLLECTION_CHAT).push().setValue(c);
                sentInputMessage.postValue(true);
                if (conservationID != null) {
                    updateConservation(messageChat, senderID, senderName, senderImage, receiverID, receiveName, receiverImage);
                } else if (conservationID == null) {
                    Log.d("ChatAct", "create new Message");
                    Conservation conservation = new Conservation(senderID, senderName, senderImage, receiverID, receiveName, receiverImage, messageChat, new Date());
                    addConservation(conservation);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void sendMessageRepLocal(String senderID, String senderName, String senderImage, String receiverID, String receiverName, String receiverImage, String messageChat, String messageRepLocal, String urlFileRepLocal, String urlImageRepLocal) {
        databaseReference.child(Contants.KEY_COLLECTION_CHAT).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ChatMessage c = new ChatMessage(senderID, senderName, senderImage, receiverID, messageChat, messageRepLocal, urlFileRepLocal, urlImageRepLocal, new Date());
                databaseReference.child(Contants.KEY_COLLECTION_CHAT).push().setValue(c);
                sentInputMessage.postValue(true);
                if (conservationID != null) {
                    updateConservation(messageChat, senderID, senderName, senderImage, receiverID, receiverName, receiverImage);
                } else if (conservationID == null) {
                    Conservation conservation = new Conservation(senderID, senderName, senderImage, receiverID, receiverName, receiverImage, messageChat, new Date());
                    addConservation(conservation);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void sendMesageFile(String senderID, String senderName, String senderImage, String receiverID, String receiverName, String reiceiverImage, Uri data, String fileName) {

        String base64File = encodeFileToBase64(data, getApplication());
        databaseReference.child(Contants.KEY_COLLECTION_CHAT).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ChatMessage chatMessage = new ChatMessage(senderID, senderName, senderImage, receiverID, fileName, base64File, new Date());
                String chatID = databaseReference.child(Contants.KEY_COLLECTION_CHAT).push().getKey();
                sentInputMessage.postValue(Boolean.TRUE);
                if (conservationID != null) {
                    PDFClass pdfClass = new PDFClass(conservationID, chatID, senderID, receiverID, fileName, base64File, new Date(), "enable");
                    databaseReference.child(Contants.KEY_COLLECTION_FILE).push().setValue(pdfClass);
                    updateConservation(fileName, senderID, senderName, senderImage, receiverID, receiverName, reiceiverImage);
                } else if (conservationID == null) {
                    Conservation conservation = new Conservation(senderID, senderName, senderImage, receiverID, receiverName, reiceiverImage, fileName, new Date());
                    addConservation(conservation);
                    PDFClass pdfClass = new PDFClass(conservationID, chatID, senderID, receiverID, fileName, base64File, new Date(), "enable");
                    databaseReference.child(Contants.KEY_COLLECTION_FILE).push().setValue(pdfClass);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void sendMessageImage(String senderID, String senderName, String senderImage, String receiverID, String receiverName, String receiverImage, Uri data) {
//            String fileImage=encodeImage(Uri.c)
        databaseReference.child(Contants.KEY_COLLECTION_CHAT).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String chatID = databaseReference.child(Contants.KEY_COLLECTION_CHAT).push().getKey();
//                    ChatMessage chatMessage=new ChatMessage(senderID,senderName,senderImage,receiverID,new Date(),)
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void listenMessages(String senderID, String receiverID) {
        //group chat
        Set<String> listenSenderID = new HashSet<>();
        databaseReference.child(Contants.KEY_COLLECTION_GROUP_MEMBER).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (receiverID.equals(dataSnapshot.child(Contants.KEY_GROUP_CHAT_ID).getValue(String.class))) {
                        String userID = dataSnapshot.child(Contants.KEY_USER_ID).getValue(String.class);
                        String userIDAdd = dataSnapshot.child(Contants.KEY_GROUP_MEMBER_TIME_USERID_ADD).getValue(String.class);
                        listenSenderID.add(userID);
                        listenSenderID.add(userIDAdd);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        databaseReference.child(Contants.KEY_COLLECTION_CHAT).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatMessages.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String senderIDL = dataSnapshot.child(Contants.KEY_SENDER_ID).getValue(String.class);
                    String receiverIDL = dataSnapshot.child(Contants.KEY_RECEIVER_ID).getValue(String.class);
                    String fileName = dataSnapshot.child(Contants.KEY_FILENAME).getValue(String.class);
                    String urlImage = dataSnapshot.child(Contants.KEY_URL_IMAGE).getValue(String.class);
                    String statusMessage = dataSnapshot.child(Contants.KEY_STATUS_MESSAGE).getValue(String.class);
                    //for sender
                    if (senderID.equals(senderIDL) && receiverID.equals(receiverIDL)) {
                        //file pdf message
                        if (fileName != null) {
                            String messageID = dataSnapshot.getKey();
                            String urlFile = dataSnapshot.child(Contants.KEY_URL_FILE).getValue(String.class);
                            Date dateTime = dataSnapshot.child("dateTime").getValue(Date.class);
                            Date dateObject = dataSnapshot.child("dateTime").getValue(Date.class);
                            ChatMessage chatMessage = new ChatMessage(messageID, statusMessage, senderIDL, receiverIDL, fileName, urlFile, dateTime, dateObject);
                            chatMessages.add(chatMessage);
                            //message image
                        } else if (urlImage != null) {
                            String messageID = dataSnapshot.getKey();
                            Date date = dataSnapshot.child("dateTime").getValue(Date.class);
                            Date dateObject = dataSnapshot.child("dateTime").getValue(Date.class);
                            ChatMessage chatMessage = new ChatMessage(messageID, senderIDL, receiverIDL, date, dateObject, urlImage, statusMessage);
                            chatMessages.add(chatMessage);
                        }
                        //text message
                        else {
                            String messageID = dataSnapshot.getKey();
                            String message = dataSnapshot.child(Contants.KEY_MESSAGE).getValue(String.class);
                            Date date = dataSnapshot.child("dateTime").getValue(Date.class);
                            Date dateObject = dataSnapshot.child("dateTime").getValue(Date.class);
                            String messageRepLocal = dataSnapshot.child(Contants.KEY_MESSAGE_REP).getValue(String.class);
                            String urlFileRepLocal = dataSnapshot.child(Contants.KEY_URL_FILE_REP).getValue(String.class);
                            String urlImageRepLocal = dataSnapshot.child(Contants.KEY_URL_IMAGE_REP).getValue(String.class);

                            ChatMessage chatMessage = new ChatMessage(messageID, statusMessage, senderIDL, receiverIDL, message, date, dateObject, messageRepLocal, urlFileRepLocal, urlImageRepLocal);
                            chatMessages.add(chatMessage);
                        }
                    }//for receiver
                    else if (senderID.equals(receiverIDL) && receiverID.equals(senderIDL)) {
                        //file pdf message
                        if (fileName != null) {
                            String messageID = dataSnapshot.getKey();
                            String urlFile = dataSnapshot.child(Contants.KEY_URL_FILE).getValue(String.class);
                            Date date = dataSnapshot.child("dateTime").getValue(Date.class);
                            Date dateObject = dataSnapshot.child("dateTime").getValue(Date.class);
                            ChatMessage chatMessage = new ChatMessage(messageID, statusMessage, senderIDL, receiverIDL, fileName, urlFile, date, dateObject);
                            chatMessages.add(chatMessage);
                            //message image
                        } else if (urlImage != null) {
                            String messageID = dataSnapshot.getKey();
                            Date date = dataSnapshot.child("dateTime").getValue(Date.class);
                            Date dateObject = dataSnapshot.child("dateTime").getValue(Date.class);
                            ChatMessage chatMessage = new ChatMessage(messageID, senderIDL, receiverIDL, date, dateObject, urlImage, statusMessage);
                            chatMessages.add(chatMessage);
                        }
                        //text message
                        else {
                            String messageID = dataSnapshot.getKey();
                            String message = dataSnapshot.child(Contants.KEY_MESSAGE).getValue(String.class);
                            Date date = dataSnapshot.child("dateTime").getValue(Date.class);
                            Date dateObject = dataSnapshot.child("dateTime").getValue(Date.class);
                            String messageRepLocal = dataSnapshot.child(Contants.KEY_MESSAGE_REP).getValue(String.class);
                            String urlFileRepLocal = dataSnapshot.child(Contants.KEY_URL_FILE_REP).getValue(String.class);
                            String urlImageRepLocal = dataSnapshot.child(Contants.KEY_URL_IMAGE_REP).getValue(String.class);

                            ChatMessage chatMessage = new ChatMessage(messageID, statusMessage, senderIDL, receiverIDL, message, date, dateObject, messageRepLocal, urlFileRepLocal, urlImageRepLocal);
                            chatMessages.add(chatMessage);
                        }
                    }
                    //for groupchat
                    else if (receiverID.equals(receiverIDL) && listenSenderID.contains(senderID)) {
                        if (fileName != null) {
                            String messageID = dataSnapshot.getKey();
                            String urlFile = dataSnapshot.child(Contants.KEY_URL_FILE).getValue(String.class);
                            Date date = dataSnapshot.child("dateTime").getValue(Date.class);
                            Date dateObject = dataSnapshot.child("dateTime").getValue(Date.class);
                            String senderName = dataSnapshot.child(Contants.KEY_SENDER_NAME).getValue(String.class);
                            String senderImage = dataSnapshot.child(Contants.KEY_SENDER_IMAGE).getValue(String.class);

                            ChatMessage chatMessage = new ChatMessage(messageID, statusMessage, senderIDL, senderName, senderImage, receiverIDL, fileName, urlFile, date, dateObject);
                            chatMessages.add(chatMessage);

                        } else if (urlImage != null) {
                            String messageID = dataSnapshot.getKey();
                            Date date = dataSnapshot.child("dateTime").getValue(Date.class);
                            Date dateObject = dataSnapshot.child("dateTime").getValue(Date.class);
                            String senderName = dataSnapshot.child(Contants.KEY_SENDER_NAME).getValue(String.class);
                            String senderImage = dataSnapshot.child(Contants.KEY_SENDER_IMAGE).getValue(String.class);
                            ChatMessage chatMessage = new ChatMessage(messageID, senderIDL, senderName, senderImage, receiverIDL, date, dateObject, urlImage, statusMessage);
                            chatMessages.add(chatMessage);
                        } else {
                            String messageID = dataSnapshot.getKey();
                            String message = dataSnapshot.child(Contants.KEY_MESSAGE).getValue(String.class);
                            Date date = dataSnapshot.child("dateTime").getValue(Date.class);
                            Date dateObject = dataSnapshot.child("dateTime").getValue(Date.class);
                            String senderName = dataSnapshot.child(Contants.KEY_SENDER_NAME).getValue(String.class);
                            String senderImage = dataSnapshot.child(Contants.KEY_SENDER_IMAGE).getValue(String.class);

                            String messageRepLocal = dataSnapshot.child(Contants.KEY_MESSAGE_REP).getValue(String.class);
                            String urlFileRepLocal = dataSnapshot.child(Contants.KEY_URL_FILE_REP).getValue(String.class);
                            String urlImageRepLocal = dataSnapshot.child(Contants.KEY_URL_IMAGE_REP).getValue(String.class);
                            ChatMessage chatMessage = new ChatMessage(messageID, statusMessage, senderIDL, senderName, senderImage,
                                    receiverIDL, message, date, dateObject, messageRepLocal, urlFileRepLocal, urlImageRepLocal);
                            chatMessages.add(chatMessage);
                        }
                    }

                    chatMessagesMutableLiveData.postValue(chatMessages);
                }
                if (conservationID == null) {
                    checkForConservation(senderID, receiverID);
                }
                chatMessages.sort(Comparator.comparing(ChatMessage::getDateObject));
                if (chatMessages.size() == 0) {
                    count0MutableLiveData.postValue(Boolean.TRUE);
                } else {
                    count0MutableLiveData.postValue(Boolean.FALSE);
                }
                loadChatRecycleView.postValue(Boolean.TRUE);
                loadProgressBarMutableLiveData.postValue(Boolean.TRUE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkForConservation(String senderID, String receiverID) {
        if (chatMessages.size() != 0) {
            checkForConservationRemotely(senderID, receiverID);
        }
    }

    private void checkForConservationRemotely(String senderID, String receiverID) {
        //for group chat
        isChecked = false;
        Set<String> listenSenderID = new HashSet<>();
        databaseReference.child(Contants.KEY_COLLECTION_GROUP_MEMBER).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (receiverID.equals(dataSnapshot.child(Contants.KEY_GROUP_CHAT_ID).getValue(String.class))) {
                        String userID = dataSnapshot.child(Contants.KEY_USER_ID).getValue(String.class);
                        String userIDAdd = dataSnapshot.child(Contants.KEY_GROUP_MEMBER_TIME_USERID_ADD).getValue(String.class);
                        listenSenderID.add(userID);
                        listenSenderID.add(userIDAdd);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.child(Contants.KEY_COLLECTION_CONVERSATIONS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String senderIDcon = dataSnapshot.child(Contants.KEY_SENDER_ID).getValue(String.class);
                    String receiverIDcon = dataSnapshot.child(Contants.KEY_RECEIVER_ID).getValue(String.class);
                    if (senderID.equals(senderIDcon) && receiverID.equals(receiverIDcon)) {
                        conservationID = dataSnapshot.getKey();
                        conservationIDMutableLiveData.postValue(conservationID);
                        String theme = dataSnapshot.child(Contants.KEY_BACKGROUND_CONVERSATION).getValue(String.class);
                        themeConservationMutableLiveData.postValue(theme);
                        continue;
                    } else if (senderID.equals(receiverIDcon) && receiverID.equals(senderIDcon)) {
                        conservationID = dataSnapshot.getKey();
                        conservationIDMutableLiveData.postValue(conservationID);

                        String theme = dataSnapshot.child(Contants.KEY_BACKGROUND_CONVERSATION).getValue(String.class);
                        themeConservationMutableLiveData.postValue(theme);
                        continue;
                    }
                    //get conservationID of group
                    else if (receiverID.equals(receiverIDcon) && listenSenderID.contains(senderID)) {
                        conservationID = dataSnapshot.getKey();
                        conservationIDMutableLiveData.postValue(conservationID);
                        isChecked = true;
                        String theme = dataSnapshot.child(Contants.KEY_BACKGROUND_CONVERSATION).getValue(String.class);
                        themeConservationMutableLiveData.postValue(theme);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void setStatusMessage(String messageID, String status) {
        databaseReference.child(Contants.KEY_COLLECTION_CHAT).child(messageID).child(Contants.KEY_STATUS_MESSAGE).setValue(status);
        databaseReference.child(Contants.KEY_COLLECTION_FILE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (messageID.equals(dataSnapshot.child(Contants.KEY_CHAT_ID).getValue(String.class))) {
                        String key = dataSnapshot.getKey();
                        databaseReference.child(Contants.KEY_COLLECTION_FILE).child(key).child(Contants.KEY_STATUS_FILE).setValue("disable");

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference.child(Contants.KEY_COLLECTION_IMAGE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (messageID.equals(dataSnapshot.child(Contants.KEY_CHAT_ID).getValue(String.class))) {
                        String key = dataSnapshot.getKey();
                        databaseReference.child(Contants.KEY_COLLECTION_IMAGE).child(key).child(Contants.KEY_STATUS_IMAGE).setValue("disable");

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        String lastMessage = preferenceManager.getString(Contants.KEY_NAME) + " invalid message ";
        databaseReference.child(Contants.KEY_COLLECTION_CONVERSATIONS).child(conservationID).child(Contants.KEY_LAST_MESSAGE).setValue(lastMessage);

    }

    private void decodeBase64ToFile(String base64String, String fileName, Context context) {
        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        File file = new File(context.getFilesDir(), fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(decodedBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String encodeFileToBase64(Uri fileUri, Context context) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(fileUri);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Bitmap decodeImage(String image) {
        byte[] bytes = Base64.decode(image, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }

    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    //save conservation
    private void addConservation(Conservation conservation) {
        conservationID = databaseReference.child(Contants.KEY_COLLECTION_CONVERSATIONS).push().getKey();
        databaseReference.child(Contants.KEY_COLLECTION_CONVERSATIONS).child(conservationID).setValue(conservation);
        databaseReference.child(Contants.KEY_COLLECTION_CONVERSATIONS).child(conservationID).child(Contants.KEY_SEEN).setValue("disable");
    }

    //update last message for conservation
    private void updateConservation(String message, String senderID, String senderName, String senderImage, String receiverID, String receiverName, String receiverImage) {
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

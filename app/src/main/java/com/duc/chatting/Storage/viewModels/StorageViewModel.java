package com.duc.chatting.Storage.viewModels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.duc.chatting.chat.models.ImageClass;
import com.duc.chatting.chat.models.PDFClass;
import com.duc.chatting.utilities.Contants;
import com.duc.chatting.utilities.PreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StorageViewModel extends AndroidViewModel {
    DatabaseReference databaseReference = FirebaseDatabase
            .getInstance()
            .getReferenceFromUrl("https://chatting-4faf6-default-rtdb.firebaseio.com/");
    private PreferenceManager preferenceManager;
    private MutableLiveData<List<PDFClass>> listPDFMutableLiveData;
    private MutableLiveData<List<ImageClass>> listImageMutableLiveData;
    private List<PDFClass> listPDF;
    private List<ImageClass> listImage;
    public MutableLiveData<List<PDFClass>> getListPDFMutableLiveData() {
        return listPDFMutableLiveData;
    }

    public MutableLiveData<List<ImageClass>> getListImageMutableLiveData() {
        return listImageMutableLiveData;
    }
    public StorageViewModel(@NonNull Application application) {
        super(application);
        preferenceManager = new PreferenceManager(application);
        listImageMutableLiveData = new MutableLiveData<>();
        listPDFMutableLiveData = new MutableLiveData<>();
        listPDF = new ArrayList<>();
        listImage = new ArrayList<>();
    }
    public void getListFile(String userId) {
        listPDF.clear();
        databaseReference.child(Contants.KEY_COLLECTION_FILE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String senderID = postSnapshot.child("senderID").getValue(String.class);
                    if (userId.equals(senderID)) {
                        String name = postSnapshot.child(Contants.KEY_FILENAME).getValue(String.class);
                        String url = postSnapshot.child(Contants.KEY_URL_FILE).getValue(String.class);
                        String statusFile = postSnapshot.child(Contants.KEY_STATUS_FILE).getValue(String.class);
                        PDFClass pdfClass = new PDFClass(name, url, statusFile);
                        listPDF.add(pdfClass);

                        Log.d("SenderImD",String.valueOf(listPDF.size() ));
                    }
                }
                listPDFMutableLiveData.postValue(listPDF);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void getListImage() {
        String userID=preferenceManager.getString(Contants.KEY_USER_ID);
        listImage.clear();
        databaseReference.child(Contants.KEY_COLLECTION_IMAGE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String senderId = postSnapshot.child("senderID").getValue(String.class);
                    Log.d("SenderIm",senderId);
                    if (userID.equals(senderId)) {
                        String imageUrl = postSnapshot.child(Contants.KEY_URL_IMAGE).getValue(String.class);
                        String statusImage = postSnapshot.child(Contants.KEY_STATUS_IMAGE).getValue(String.class);
                        ImageClass imageClass = new ImageClass(imageUrl, statusImage);
                        listImage.add(imageClass);
                        Log.d("SenderIm",String.valueOf(listImage.size() ));

                    }
                }
                listImageMutableLiveData.postValue(listImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}

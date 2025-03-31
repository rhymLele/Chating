package com.duc.chatting.home.views;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.duc.chatting.R;
import com.duc.chatting.chat.models.ChatMessage;
import com.duc.chatting.chat.models.User;
import com.duc.chatting.chat.views.ChatActivity;
import com.duc.chatting.databinding.FragmentConservationBinding;
import com.duc.chatting.home.adapters.RecentConservationAdapter;
import com.duc.chatting.home.viewmodels.ConservationViewModel;
import com.duc.chatting.utilities.Contants;
import com.duc.chatting.utilities.PreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ConservationFragment extends Fragment {
    private FragmentConservationBinding binding;
    private ConservationViewModel viewModel;
    private RecentConservationAdapter adapter;
    private PreferenceManager preferenceManager;
    private List<ChatMessage> mListChatMessage;
    DatabaseReference databaseReference = FirebaseDatabase
            .getInstance()
            .getReferenceFromUrl("https://chatting-4faf6-default-rtdb.firebaseio.com/");
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       binding=FragmentConservationBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel=new ViewModelProvider(this).get(ConservationViewModel.class);
        preferenceManager=new PreferenceManager(getContext());
        mListChatMessage=new ArrayList<>();
        viewModel.getConservationsMutableLiveData().observe(this, chatMessages -> {
            Log.d("ConservationFragment", "LiveData updated, siz e: " + chatMessages.size());
            adapter=new RecentConservationAdapter(chatMessages,this::onConservationClicked);
            Log.d("Consetvation","Mesaage");
            binding.conservationRecyclerView.setAdapter(adapter);
            binding.conservationRecyclerView.smoothScrollToPosition(0);
            binding.conservationRecyclerView.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
            Log.d("ConservationFragment", "RecyclerView visibility: " + binding.conservationRecyclerView.getVisibility());
        });

    }
    public void handleDeleteConservation()
    {}

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.listenConservations(preferenceManager.getString(Contants.KEY_USER_ID));
    }
    public void onConservationClicked(User user){
        Intent intent=new Intent(getContext(), ChatActivity.class);
        intent.putExtra(Contants.KEY_USER,user);
        startActivity(intent);
    }
    public void reportUser(String postId, String userId) {
        String currentUserId = preferenceManager.getString(Contants.KEY_USER_ID);
        Map<String, Object> report = new HashMap<>();
        report.put("reportedBy", currentUserId);
        report.put("messageId", postId);
        report.put("messageOwnerId", userId);
        report.put("timestamp", FieldValue.serverTimestamp());

        FirebaseFirestore.getInstance().collection("Reports")
                .add(report)
                .addOnSuccessListener(documentReference ->
                        Log.d("Firebase", "Report submitted successfully"))
                .addOnFailureListener(e ->
                        Log.e("Firebase", "Failed to submit report", e));
    }
    public void blockUser(String userId) {
        String currentUserId = preferenceManager.getString(Contants.KEY_USER_ID);
        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(currentUserId)
                .collection("BlockedUsers")
                .document(userId)
                .set(new HashMap<>())  // Lưu một document rỗng
                .addOnSuccessListener(aVoid ->
                        Log.d("Firebase", "User blocked successfully"))
                .addOnFailureListener(e ->
                        Log.e("Firebase", "Failed to block user", e));
    }
    public void unblockUser(String blockUserId) {
        String currentUserId = preferenceManager.getString(Contants.KEY_USER_ID);
        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(currentUserId)
                .collection("BlockedUsers")
                .document(blockUserId)
                .delete()
                .addOnSuccessListener(aVoid ->
                        Log.d("Firebase", "User unblocked successfully"))
                .addOnFailureListener(e ->
                        Log.e("Firebase", "Failed to unblock user", e));
    }
    public void getBlockedUsers() {
        String currentUserId = preferenceManager.getString(Contants.KEY_USER_ID);
        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(currentUserId)
                .collection("BlockedUsers")
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<String> blockedUsers = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        blockedUsers.add(doc.getId());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Failed to fetch blocked users", e);
                });
    }
    public void deleteAccount(String uid) {
        String currentUserId = preferenceManager.getString(Contants.KEY_USER_ID);
        // Xóa user
        databaseReference.child("users").child(currentUserId).removeValue()
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "User deleted"))
                .addOnFailureListener(e -> Log.e("Firebase", "Failed to delete user", e));
    }


}
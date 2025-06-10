package com.duc.chatting.home.views;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.duc.chatting.R;
import com.duc.chatting.chat.models.ChatMessage;
import com.duc.chatting.chat.models.User;
import com.duc.chatting.chat.views.ChatActivity;
import com.duc.chatting.databinding.FragmentConservationBinding;
import com.duc.chatting.home.adapters.RecentConservationAdapter;
import com.duc.chatting.home.viewmodels.ConservationViewModel;
import com.duc.chatting.utilities.Contants;
import com.duc.chatting.utilities.PreferenceManager;
import com.duc.chatting.utilities.SwipeHelper;
import com.duc.chatting.utilities.widgets.ReportDialogManager;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ConservationFragment extends Fragment {
    private FragmentConservationBinding binding;
    private ConservationViewModel viewModel;
    private RecentConservationAdapter adapter;
    private PreferenceManager preferenceManager;
    private List<ChatMessage> mListChatMessage;
    Map<String, List<String>> reportOptions = new HashMap<>();

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
        reportOptions.put("Selling or promoting restricted items", Arrays.asList("Drugs", "Weapons", "Animals", "Counterfeit goods", "Stolen property", "Tobacco products"));
        reportOptions.put("Violent, hateful or disturbing content", Arrays.asList("Violence", "Hate speech", "Graphic content", "Terrorism", "Self-harm", "Bullying"));
        reportOptions.put("Scam or fraud", Arrays.asList("Phishing", "Investment scams", "Lottery scams", "Romance scams", "Fake charities"));
        reportOptions.put("Adult content", Arrays.asList("Pornography", "Sexual services", "Nudity", "Sexual exploitation", "Child exploitation"));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeHelper(getContext(), new SwipeHelper.SwipeListener() {
            @Override
            public void onEdit(int position) {

            }

            @Override
            public void onReport(int position) {
                String conservationId = adapter.getConservationIdAt(position);
                ReportDialogManager reportDialog = new ReportDialogManager(requireActivity(), reportOptions);
                reportDialog.show(conservationId, (conversationId, reason) -> {
                    reportUser(conversationId, reason);
                    Toast.makeText(getContext(), "Reported: " + reason, Toast.LENGTH_SHORT).show();
                });
            }
        }));
        viewModel.getConservationsMutableLiveData().observe(this, chatMessages -> {
            if (chatMessages.isEmpty()) {
                binding.emptyTextView.setVisibility(View.VISIBLE);
                binding.conservationRecyclerView.setVisibility(View.GONE);
            } else {
                adapter = new RecentConservationAdapter(chatMessages, this::onConservationClicked);
                binding.conservationRecyclerView.setAdapter(adapter);
                binding.conservationRecyclerView.smoothScrollToPosition(0);
                binding.conservationRecyclerView.setVisibility(View.VISIBLE);
                binding.emptyTextView.setVisibility(View.GONE);
                itemTouchHelper.attachToRecyclerView(binding.conservationRecyclerView);
            }
            binding.progressBar.setVisibility(View.GONE);
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
    public void reportUser(String postId,String message) {
        String currentUserId = preferenceManager.getString(Contants.KEY_USER_ID);
        Map<String, Object> report = new HashMap<>();
        report.put("reportedBy", currentUserId);
        report.put("conservationId", postId);
        report.put("timestamp", FieldValue.serverTimestamp());
        report.put("messageReport", message);

        FirebaseFirestore.getInstance().collection("Reports")
                .add(report)
                .addOnSuccessListener(documentReference ->
                        Log.d("Firebase", "Report submitted successfully"))
                .addOnFailureListener(e ->
                        Log.e("Firebase", "Failed to submit report", e));
    }



    public void deleteAccount(String uid) {
        String currentUserId = preferenceManager.getString(Contants.KEY_USER_ID);
        // Xóa user
        databaseReference.child("users").child(currentUserId).removeValue()
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "User deleted"))
                .addOnFailureListener(e -> Log.e("Firebase", "Failed to delete user", e));
    }


}
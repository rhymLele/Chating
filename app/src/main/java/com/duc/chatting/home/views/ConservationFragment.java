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

import java.util.ArrayList;
import java.util.List;


public class ConservationFragment extends Fragment {
    private FragmentConservationBinding binding;
    private ConservationViewModel viewModel;
    private RecentConservationAdapter adapter;
    private PreferenceManager preferenceManager;
    private List<ChatMessage> mListChatMessage;

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

//    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        int id=item.getItemId();
//        if(id==R.id.addPerson)
//        {
////            Intent intent=new Intent(getContext(),...);
////            startActivity(intent);
//        }
//        if(id==R.id.addGroup)
//        {
////            Intent intent=new Intent(getContext(),...);
////            startActivity(intent);
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
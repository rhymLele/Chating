package com.duc.chatting.home.views;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duc.chatting.R;
import com.duc.chatting.chat.adapters.UserAdapter;
import com.duc.chatting.chat.models.User;
import com.duc.chatting.chat.views.ReceiverDetailProfileActivity;
import com.duc.chatting.databinding.FragmentFriendBinding;
import com.duc.chatting.home.adapters.FriendRequestAdapter;
import com.duc.chatting.home.viewmodels.ListFriendViewModel;
import com.duc.chatting.utilities.Contants;
import com.duc.chatting.utilities.PreferenceManager;


public class FriendFragment extends Fragment {
    FragmentFriendBinding binding;
    ListFriendViewModel viewModel;
    UserAdapter userAdapter;
    FriendRequestAdapter requestAdapter;
    private PreferenceManager preferenceManager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ListFriendViewModel.class);
        preferenceManager=new PreferenceManager(getContext());
        viewModel.getFriendsMutableLiveData().observe(this,users -> {
            userAdapter=new UserAdapter(users,this::onUserClicked);
            Log.d("FriendFragment", String.valueOf(users.size()));
            binding.friendRecyclerView.setAdapter(userAdapter);
            binding.friendRecyclerView.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        });
        viewModel.getFriendRequestMutableLiveData().observe(this,users -> {
            requestAdapter=new FriendRequestAdapter(users,this::onUserClicked,this::onRequestAccept,this::onRequestReject);
            binding.friendRequestRecyclerView.setAdapter(requestAdapter);
            if(!users.isEmpty()) binding.friendRequestRecyclerView.setVisibility(View.VISIBLE);
            else binding.friendRequestRecyclerView.setVisibility(View.GONE);
        });
        viewModel.getFrAll().observe(this,ob->{
            binding.frAll.setText(String.valueOf(ob));
        });
        viewModel.getFrRequest().observe(this,ob->{
            binding.frRequest.setText(String.valueOf(ob));
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadFriendList();
    }

    private void loadFriendList(){
        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.showListFriend();
        viewModel.getListFriendRequest();

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentFriendBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }
    public void onUserClicked(User user){
        Intent intent=new Intent(getActivity(), ReceiverDetailProfileActivity.class);
        intent.putExtra(Contants.KEY_USER,user);
        startActivity(intent);
    }
    public void onRequestAccept(User user){
    viewModel.acceptFriend(user);
    }
    public void onRequestReject(User user){
    viewModel.destroyAddFriend(user);
    }
}
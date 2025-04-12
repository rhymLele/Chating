package com.duc.chatting.poliso.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.duc.chatting.R;
import com.duc.chatting.chat.adapters.UserAdapter;
import com.duc.chatting.chat.models.User;
import com.duc.chatting.chat.viewmodels.BlockUserViewModel;
import com.duc.chatting.chat.views.ReceiverDetailProfileActivity;
import com.duc.chatting.databinding.ActivityBlockListBinding;
import com.duc.chatting.home.viewmodels.ListFriendViewModel;
import com.duc.chatting.poliso.adapters.BlockUserAdapter;
import com.duc.chatting.utilities.Contants;
import com.duc.chatting.utilities.PreferenceManager;

public class BlockListActivity extends AppCompatActivity {
    ActivityBlockListBinding binding;
    BlockUserAdapter userAdapter;
    BlockUserViewModel viewModel;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityBlockListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        viewModel = new ViewModelProvider(this).get(BlockUserViewModel.class);
        preferenceManager=new PreferenceManager(this);
        loadBlockList();

        setListeners();
        viewModel.getFriendsMutableLiveData().observe(this,users -> {
            userAdapter=new BlockUserAdapter(users,this::onUserClicked,this::onRequestReject);
            binding.listBlockUsers.setAdapter(userAdapter);
            binding.listBlockUsers.setVisibility(View.VISIBLE);
        });

    }

    private void loadBlockList() {
        viewModel.getBlockedUsers();
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> {
            finish();
        });
    }
    public void onUserClicked(User user){
        Intent intent=new Intent(this, ReceiverDetailProfileActivity.class);
        intent.putExtra(Contants.KEY_USER,user);
        startActivity(intent);
    }
    public void onRequestReject(User user){
        viewModel.unblockUser(user.getId());
    }
}
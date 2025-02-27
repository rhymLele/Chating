package com.duc.chatting.chat.views;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.duc.chatting.R;
import com.duc.chatting.chat.adapters.GroupChatAdapter;
import com.duc.chatting.chat.models.User;
import com.duc.chatting.chat.viewmodels.ChatActivityViewModel;
import com.duc.chatting.chat.viewmodels.GroupChatViewModel;
import com.duc.chatting.databinding.ActivityGroupChatBinding;
import com.duc.chatting.home.views.HomeActivity;
import com.duc.chatting.utilities.Contants;
import com.duc.chatting.utilities.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

public class GroupChatActivity extends AppCompatActivity {
    private ActivityGroupChatBinding binding;
    private GroupChatViewModel viewModel;
    private PreferenceManager preferenceManager;
    private ChatActivityViewModel viewModelChat;
    private GroupChatAdapter adapter;
    private Set<User> memberUserGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        preferenceManager=new PreferenceManager(getApplicationContext());
        memberUserGroup=new HashSet<>();

        viewModel=new ViewModelProvider(this).get(GroupChatViewModel.class);
        viewModelChat=new ViewModelProvider(this).get(ChatActivityViewModel.class);
        viewModel.getListUserGroupMutableLiveData().observe(this,users -> {
           adapter=new GroupChatAdapter(users,this::onUserClicked);
           binding.usersRecyclerView.setAdapter(adapter);
           binding.usersRecyclerView.setVisibility(View.VISIBLE);
           binding.progressBar.setVisibility(View.GONE);

        });
        binding.imageBack.setOnClickListener(v -> {
            Intent intent=new Intent(this, HomeActivity.class);
            startActivity(intent);
        });
        viewModel.getUserGroup();
        binding.searchAddUserr.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().isEmpty()){
                    viewModel.getUserGroup();
                }else{
                    viewModel.getUserGroupForPhoneNumber(s.toString());
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.imageAdd.setOnClickListener(v -> {
            if(memberUserGroup.size()==0){
                Toast.makeText(this,"Please select user",Toast.LENGTH_SHORT).show();
            }else if(memberUserGroup.size()==1)
            {
                for(User user:memberUserGroup){
                    Intent intent=new Intent(this, ChatActivity.class);
                    intent.putExtra(Contants.KEY_USER,user);
                    startActivity(intent);
                }
            }else{
                User user1=new User(preferenceManager.getString(Contants.KEY_USER_ID),
                        preferenceManager.getString(Contants.KEY_NAME),
                        preferenceManager.getString(Contants.KEY_IMAGE));
                memberUserGroup.add(user1);
                viewModel.addUserToGroup(memberUserGroup,binding.edtNameGroup.getText().toString());
                viewModel.getUserGroupMutableLiveData().observe(this,user -> {
                    String message=preferenceManager.getString(Contants.KEY_NAME)+" Create group successfully";
                    viewModelChat.sendMessage(preferenceManager.getString(Contants.KEY_USER_ID),
                            preferenceManager.getString(Contants.KEY_NAME),
                            preferenceManager.getString(Contants.KEY_IMAGE),
                            user.getId(),
                            user.getName(),
                            user.getImgProfile(),
                            message);
                    Intent intent=new Intent(this,ChatActivity.class);
                    intent.putExtra(Contants.KEY_USER,user);
                    startActivity(intent);
                });

            }
        });
    }
    public void onUserClicked(User user){
        if(user.getChecked()==true){
            memberUserGroup.add(user);
        }else if(user.getChecked()==false){
            memberUserGroup.remove(user);
        }
    }
}
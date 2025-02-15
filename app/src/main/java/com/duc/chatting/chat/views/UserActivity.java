package com.duc.chatting.chat.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.duc.chatting.R;
import com.duc.chatting.chat.UserAdapter;
import com.duc.chatting.chat.models.User;
import com.duc.chatting.chat.viewmodels.ChatUserViewModel;
import com.duc.chatting.databinding.ActivityUserBinding;
import com.duc.chatting.home.views.HomeActivity;
import com.duc.chatting.utilities.Contants;

public class UserActivity extends AppCompatActivity {
    ActivityUserBinding binding;
    private ChatUserViewModel viewModel;
    UserAdapter userAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_user);
        binding=ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel=new ViewModelProvider(this).get(ChatUserViewModel.class);



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        viewModel.getUserMutableLiveData().observe(this,users -> {
            userAdapter=new UserAdapter(users,this::onUserClicked);
            binding.usersRecyclerView.setAdapter(userAdapter);
            binding.usersRecyclerView.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        });
        binding.imageBack.setOnClickListener(v->{
            Intent i=new Intent(this, HomeActivity.class);
            startActivity(i);

        });
        binding.searchAddUser.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.getUsers(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                viewModel.getUsers(newText);
                return false;
            }
        });

    }
    public void onUserClicked(User user){
        Intent intent=new Intent(getApplicationContext(), ReceiverDetailProfileActivity.class);
        intent.putExtra(Contants.KEY_USER,user);
        startActivity(intent);
    }
}
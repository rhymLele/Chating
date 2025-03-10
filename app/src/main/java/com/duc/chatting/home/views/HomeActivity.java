package com.duc.chatting.home.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.duc.chatting.R;
import com.duc.chatting.chat.views.GroupChatActivity;
import com.duc.chatting.chat.views.UserActivity;
import com.duc.chatting.databinding.ActivityHomeBinding;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity {
    ActivityHomeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_home);
        binding=ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setSupportActionBar(binding.header.myToolBar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Zalo");
        if (savedInstanceState == null) { // Chỉ load lần đầu tiên khi Activity được tạo
            replaceFragment(new ConservationFragment());
            binding.bottomNavigationView.setSelectedItemId(R.id.conservation);
        }
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id=item.getItemId();
            if(id==R.id.conservation)
            {
                replaceFragment(new ConservationFragment());
            }else if(id==R.id.personal){
                replaceFragment(new PersonalFragment());
            }
            else if(id==R.id.chatgpt){
                replaceFragment(new BotFragment());
            }
            return true;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.item_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.addPerson)
        {
            Intent intent=new Intent(this, UserActivity.class);
            startActivity(intent);
        }
        if(id==R.id.addGroup)
        {
            Intent intent=new Intent(this, GroupChatActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void replaceFragment(Fragment fragment)
    {
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
        if (fragment instanceof ConservationFragment) {
            Objects.requireNonNull(getSupportActionBar()).show();
        } else {
            Objects.requireNonNull(getSupportActionBar()).hide();
        }
    }

}
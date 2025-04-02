package com.duc.chatting.home.views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
import com.duc.chatting.utilities.Contants;
import com.duc.chatting.utilities.PreferenceManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity {
    ActivityHomeBinding binding;
    DatabaseReference databaseReference = FirebaseDatabase
            .getInstance()
            .getReferenceFromUrl("https://chatting-4faf6-default-rtdb.firebaseio.com/");
    PreferenceManager preferenceManager;
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
        preferenceManager=new PreferenceManager(getApplicationContext());
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
                Objects.requireNonNull(getSupportActionBar()).setTitle("Zalo");
            }else if(id==R.id.personal){
                replaceFragment(new PersonalFragment());
            }
            else if(id==R.id.chatgpt){
                replaceFragment(new BotFragment());
            }else if(id==R.id.friend)
            {
                replaceFragment(new FriendFragment());
                Objects.requireNonNull(getSupportActionBar()).setTitle("Friend");
            }
            return true;
        });
        getFCMtoken();
        databaseReference.child(Contants.KEY_COLLECTION_USERS).child(preferenceManager.getString(Contants.KEY_USER_ID))
                .child(Contants.KEY_STATUS).setValue("Online")
                .addOnSuccessListener(aVoid -> System.out.println("Status updated!"))
                .addOnFailureListener(e -> System.err.println("Failed to update status: " + e.getMessage()));;
    }

    private void getFCMtoken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener( task -> {

            if(task.isSuccessful())
            {
                String token= task.getResult();
                Log.d("MyToken of "+preferenceManager.getString(Contants.KEY_USER_ID),": "+token);

                databaseReference.child(Contants.KEY_COLLECTION_USERS)
                        .child(preferenceManager.getString(Contants.KEY_USER_ID))
                        .child("fcmToken").setValue(token)
                        .addOnSuccessListener(aVoid -> System.out.println("FCM Token updated!"))
                        .addOnFailureListener(e -> System.err.println("Failed to update FCM Token: " + e.getMessage()));;
            }
        } );
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
        if (fragment instanceof ConservationFragment||fragment instanceof FriendFragment) {
            Objects.requireNonNull(getSupportActionBar()).show();
        } else {
            Objects.requireNonNull(getSupportActionBar()).hide();
        }
    }
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                } else {
                    //
                }
            });
    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {

            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
}
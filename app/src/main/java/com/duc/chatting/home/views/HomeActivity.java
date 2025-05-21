package com.duc.chatting.home.views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

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
import com.duc.chatting.call.interfaces.SuccessCallBack;
import com.duc.chatting.call.repository.MainRepository;
import com.duc.chatting.chat.views.GroupChatActivity;
import com.duc.chatting.chat.views.UserActivity;
import com.duc.chatting.databinding.ActivityHomeBinding;
import com.duc.chatting.messaging.service.WebSocketService;
import com.duc.chatting.newfeature.views.FeatureActivity;
import com.duc.chatting.utilities.Contants;
import com.duc.chatting.utilities.PreferenceManager;
import com.duc.chatting.utilities.widgets.BaseActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class HomeActivity extends BaseActivity {
    ActivityHomeBinding binding;
    DatabaseReference databaseReference = FirebaseDatabase
            .getInstance()
            .getReferenceFromUrl("https://chatting-4faf6-default-rtdb.firebaseio.com/");
    PreferenceManager preferenceManager;
    private KeyPair keyPair;
    private float dX, dY;
    private boolean sw_fab;
    private MainRepository mainRepository;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        preferenceManager=new PreferenceManager(getApplicationContext());
        setSupportActionBar(binding.header.myToolBar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("WeLog");
        sw_fab=preferenceManager.getBoolean("s_fab");
        if(sw_fab)
        {
            binding.fab.setVisibility(View.GONE); // hoặc hide()
        }else          binding.fab.setVisibility(View.VISIBLE); // hoặc hide()
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
        mainRepository=MainRepository.getInstance();
        mainRepository.login(preferenceManager.getString(Contants.KEY_USER_ID), getApplication(), () -> {

        });
        databaseReference.child(Contants.KEY_COLLECTION_USERS).child(preferenceManager.getString(Contants.KEY_USER_ID))
                .child(Contants.KEY_STATUS).setValue("Online")
                .addOnSuccessListener(aVoid -> System.out.println("Status updated!"))
                .addOnFailureListener(e -> System.err.println("Failed to update status: " + e.getMessage()));
        binding.fab.setOnClickListener(v -> {
            startActivity(new Intent(this, FeatureActivity.class));
        });

        binding.fab.setOnTouchListener((view, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    dX = view.getX() - event.getRawX();
                    dY = view.getY() - event.getRawY();
                    return true;

                case MotionEvent.ACTION_MOVE:
                    view.animate()
                            .x(event.getRawX() + dX)
                            .y(event.getRawY() + dY)
                            .setDuration(0)
                            .start();
                    return true;
                case MotionEvent.ACTION_UP:
                    view.performClick(); // 👈 thêm dòng này
                    return true;
                default:
                    return false;
            }
        });
//        startService();
    }
    public void startService(){
        Intent intent = new Intent(this, WebSocketService.class);
        intent.putExtra("user_id", preferenceManager.getString(Contants.KEY_USER_ID));
        startService(intent);
    }
    public void toggleFab(boolean show) {
        if (show) {
            binding.fab.setVisibility(View.VISIBLE);
            preferenceManager.putBoolean("s_fab",false);// hoặc dùng show() nếu là FloatingActionButton
        } else {
            binding.fab.setVisibility(View.GONE); // hoặc hide()
            preferenceManager.putBoolean("s_fab",true);// hoặc dùng sh
        }
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
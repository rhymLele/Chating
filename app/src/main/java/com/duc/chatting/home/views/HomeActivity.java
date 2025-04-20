package com.duc.chatting.home.views;

import android.Manifest;
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

public class HomeActivity extends AppCompatActivity {
    ActivityHomeBinding binding;
    DatabaseReference databaseReference = FirebaseDatabase
            .getInstance()
            .getReferenceFromUrl("https://chatting-4faf6-default-rtdb.firebaseio.com/");
    PreferenceManager preferenceManager;
    private KeyPair keyPair;
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
        Objects.requireNonNull(getSupportActionBar()).setTitle("WeLog");

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
                .addOnFailureListener(e -> System.err.println("Failed to update status: " + e.getMessage()));
//        generateAndSaveKeyPair(this);
//        if (keyPair != null) {
//            encryptAndBackupPrivateKey(this, keyPair.getPrivate(), preferenceManager.getString(Contants.KEY_PASSWORD));
//        }

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
    public void generateAndSaveKeyPair(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("RSA_KEYS", MODE_PRIVATE);
        String existingPrivateKey = preferences.getString("privateKey", null);
        if (existingPrivateKey != null) {
            return;
        }

        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair(); // <-- gán vào biến toàn cục

            String publicKey = Base64.encodeToString(keyPair.getPublic().getEncoded(), Base64.DEFAULT);
            String privateKey = Base64.encodeToString(keyPair.getPrivate().getEncoded(), Base64.DEFAULT);

            String userId = preferenceManager.getString(Contants.KEY_USER_ID);
            FirebaseDatabase.getInstance().getReference(Contants.KEY_COLLECTION_USERS)
                    .child(userId)
                    .child("publicKey")
                    .setValue(publicKey);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("privateKey", privateKey);
            editor.apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public SecretKey getAESKeyFromPassword(String password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }
    public void encryptAndBackupPrivateKey(Context context, PrivateKey privateKey, String password) {
        try {
            byte[] salt = new byte[16];
            new SecureRandom().nextBytes(salt); // tạo salt mới

            SecretKey aesKey = getAESKeyFromPassword(password, salt);

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = cipher.doFinal(privateKey.getEncoded());

            // Encode để lưu lên Firebase
            String encryptedKey = Base64.encodeToString(encrypted, Base64.DEFAULT);
            String saltString = Base64.encodeToString(salt, Base64.DEFAULT);

            String userId = preferenceManager.getString(Contants.KEY_USER_ID);
            FirebaseDatabase.getInstance().getReference(Contants.KEY_COLLECTION_USERS)
                    .child(userId)
                    .child("backupPrivateKey").setValue(encryptedKey);
            FirebaseDatabase.getInstance().getReference(Contants.KEY_COLLECTION_USERS)
                    .child(userId)
                    .child("backupSalt").setValue(saltString);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void restorePrivateKey(Context context, String password, String encryptedKeyStr, String saltStr) {
        try {
            byte[] encryptedKey = Base64.decode(encryptedKeyStr, Base64.DEFAULT);
            byte[] salt = Base64.decode(saltStr, Base64.DEFAULT);

            SecretKey aesKey = getAESKeyFromPassword(password, salt);

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            byte[] decodedKey = cipher.doFinal(encryptedKey);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(decodedKey));

            // Lưu lại vào SharedPreferences
            String privateKeyString = Base64.encodeToString(decodedKey, Base64.DEFAULT);
            SharedPreferences.Editor editor = context.getSharedPreferences("RSA_KEYS", MODE_PRIVATE).edit();
            editor.putString("privateKey", privateKeyString);
            editor.apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
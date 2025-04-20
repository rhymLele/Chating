package com.duc.chatting.chat.views;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.duc.chatting.R;
import com.duc.chatting.chat.models.User;
import com.duc.chatting.chat.viewmodels.BlockUserViewModel;
import com.duc.chatting.chat.viewmodels.ReceiverDetailProfileViewModel;
import com.duc.chatting.databinding.ActivityReceiverDetailProfileBinding;
import com.duc.chatting.utilities.Contants;
import com.duc.chatting.utilities.PreferenceManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class ReceiverDetailProfileActivity extends AppCompatActivity {
    private ActivityReceiverDetailProfileBinding binding;
    private ReceiverDetailProfileViewModel viewModel;
    private PreferenceManager preferenceManager;
    private User receiverUser;
    private User dataUser;
    Dialog dialog;
    Button btnDialogCancel,getBtnDialogConfirm;
    private BlockUserViewModel blockUserViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityReceiverDetailProfileBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        viewModel=new ViewModelProvider(this).get(ReceiverDetailProfileViewModel.class);
        blockUserViewModel=new  ViewModelProvider(this).get(BlockUserViewModel.class);
        preferenceManager=new PreferenceManager(getApplicationContext());
        loadReceiverDetails();


        dialog=new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_box_unfriend);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_background));
        dialog.setCancelable(false);
        btnDialogCancel=dialog.findViewById(R.id.btnDialogCancel);
        getBtnDialogConfirm=dialog.findViewById(R.id.btnDialogConfirm);


        btnDialogCancel.setOnClickListener(v -> {
                dialog.dismiss();
        });
        getBtnDialogConfirm.setOnClickListener(v -> {
            viewModel.destroyAddFriend();
            binding.buttonAddFriend.setText("Add Friend");
            dialog.dismiss();
        });
        binding.imageBack.setOnClickListener(v -> {
            finish();
        });
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_menu, null);
        bottomSheetDialog.setContentView(sheetView);

        TextView report = sheetView.findViewById(R.id.reportOption);
        TextView block = sheetView.findViewById(R.id.blockOption);
        blockUserViewModel.observeBlockStatusRealtime(receiverUser.getId());
        blockUserViewModel.getBlockStatusLiveData().observe(this, status -> {
            if (status == null) return;
            if (status.youBlockedThem) {
                block.setText("Unblock User");
            }
        });
        binding.moreRes.setOnClickListener(v -> {
            report.setOnClickListener(view -> {
                Toast.makeText(this, "Reported!", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            });

            block.setOnClickListener(view -> {
                blockUserViewModel.blockUser(receiverUser.getId());
                Toast.makeText(this, "Blocked!", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            });

            bottomSheetDialog.show();
        });

        viewModel.dataUser(receiverUser.getId());
        viewModel.getUserMutableLiveData().observe(this,user -> {
                dataUser=user;
             String name=user.getName();
             String story=user.getStory();
             String imageProfile=user.getImgProfile();
             String imageBanner=user.getImgBanner();
             binding.textName.setText(name);
             binding.textStory.setText(story);
             if(imageProfile!=null){
                 binding.imgProfile.setImageBitmap(getBitmapFromEncodeString(imageProfile));

             }
            if(imageBanner!=null){
                binding.imgBanner.setImageBitmap(getBitmapFromEncodeString(imageBanner));

            }
        });
        viewModel.getIsChecked().observe(this,s -> {
            if(s.equals("my send")){
                binding.buttonAddFriend.setText("Cancel");
            }else if(s.equals("your send")){
                binding.buttonAddFriend.setText("Accept");
            }else if(s.equals("we friend")){
                binding.buttonAddFriend.setText("Unfriend");
            }
        });

        viewModel.listenAddFriend(preferenceManager.getString(Contants.KEY_USER_ID),receiverUser.getId());


        binding.buttonAddFriend.setOnClickListener(v->{
            String s=binding.buttonAddFriend.getText().toString();
            if(s.equals("Add Friend")){
                viewModel.addFriend(preferenceManager.getString(Contants.KEY_USER_ID),receiverUser.getId());
                Toast.makeText(this,"Sent friend request",Toast.LENGTH_SHORT).show();
                binding.buttonAddFriend.setText("Cancel");
            }else  if(s.equals("Cancel")){
                viewModel.destroyAddFriend();
                binding.buttonAddFriend.setText("Add Friend");
            }else  if(s.equals("Accept")){
                viewModel.acceptFriend();
                binding.buttonAddFriend.setText("Unfriend");
            }else  if(s.equals("Unfriend")){
               dialog.show();
            }
        });
        binding.buttonInbox.setOnClickListener(v -> {
            if (receiverUser != null) {
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra(Contants.KEY_USER, receiverUser);
                startActivity(intent);
            } else {
                Log.e("ReceiverDetailProfile", "receiverUser is null!");
            }

        });
    }
    private void loadReceiverDetails(){
        receiverUser=(User)getIntent().getSerializableExtra(Contants.KEY_USER);
    }
    private Bitmap getBitmapFromEncodeString(String encodeImage){
        byte[] bytes= Base64.decode(encodeImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);

    }
}
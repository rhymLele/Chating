package com.duc.chatting.chat.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.duc.chatting.R;
import com.duc.chatting.chat.adapters.FileListAdapter;
import com.duc.chatting.chat.adapters.ImageListAdapter;
import com.duc.chatting.chat.adapters.UserGroupAdapter;
import com.duc.chatting.chat.models.Conservation;
import com.duc.chatting.chat.models.PDFClass;
import com.duc.chatting.chat.models.User;
import com.duc.chatting.chat.viewmodels.ReceiverConservationViewModel;
import com.duc.chatting.databinding.ActivityReceiverConservationBinding;
import com.duc.chatting.utilities.Contants;

public class ReceiverConservationActivity extends AppCompatActivity {

    private ActivityReceiverConservationBinding binding;
    private ReceiverConservationViewModel viewModel;
    private UserGroupAdapter userGroupAdapter;
    private FileListAdapter fileListAdapter;
    private ImageListAdapter imageListAdapter;
    private Conservation conservation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityReceiverConservationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        viewModel = new ViewModelProvider(this).get(ReceiverConservationViewModel.class);
        viewModel.getReceiverMutableLiveData().observe(this, user -> {
            binding.profileName.setText(user.getName());
            if (user.getImgProfile() != null) {
                binding.profileImage.setImageBitmap(getBitmapFromEncodeString(user.getImgProfile()));
            }
        });

        viewModel.getIsCheckedGroupChatPersonalMutableLiveData().observe(this, isChecked -> {
            if (isChecked.equals(Boolean.TRUE)) {
                binding.personChatView.setVisibility(View.GONE);
                binding.groupChatView.setVisibility(View.VISIBLE);
                binding.groupMemberChat.setVisibility(View.VISIBLE);
            }
        });
        viewModel.getListUserMutableLiveData().observe(this, users -> {
            userGroupAdapter = new UserGroupAdapter(users, this::onUserClicked);
            binding.recyclerViewUserGroupChat.setAdapter(userGroupAdapter);

        });
        viewModel.getListPDFMutableLiveData().observe(this, pdfs -> {
            fileListAdapter = new FileListAdapter(pdfs, this::onUserClickedFileMessage);
            binding.recyclerViewFile.setAdapter(fileListAdapter);

        });
        viewModel.getListImageMutableLiveData().observe(this, images -> {
            GridLayoutManager gridLayoutManager=new GridLayoutManager(this,3);
            binding.recyclerViewFileImage.setLayoutManager(gridLayoutManager);

            imageListAdapter = new ImageListAdapter(images);
            binding.recyclerViewFileImage.setAdapter(imageListAdapter);

        });

        setClicked();
        loadReceiverConservationDetail();
        viewModel.getReiverConservation(conservation.getConservationID(),conservation.getReceiverID());

    }

    private void setClicked()
    {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.theme.setOnClickListener(v -> {
            if(binding.listThemeBackground.getVisibility()==View.GONE)
            {
                binding.listThemeBackground.setVisibility(View.VISIBLE);
            }else if(binding.listThemeBackground.getVisibility()==View.VISIBLE)
            {
                binding.listThemeBackground.setVisibility(View.GONE);
            }
        });
        binding.themLove.setOnClickListener(v -> {
            String theme="love";
            viewModel.setThemeConservation(conservation.getConservationID(),theme);
        });
        binding.themFriend.setOnClickListener(v -> {
            String theme="friend";
            viewModel.setThemeConservation(conservation.getConservationID(),theme);
        });
        binding.textFileAndImage.setOnClickListener(v -> {
            if(binding.groupFileAndImage.getVisibility()==View.GONE)
            {
                binding.groupFileAndImage.setVisibility(View.VISIBLE);
            }else if(binding.groupFileAndImage.getVisibility()==View.VISIBLE)
            {
                binding.groupFileAndImage.setVisibility(View.GONE);
            }
        });
        binding.personChatView.setOnClickListener(v -> {
            User user=new User(conservation.getReceiverID());
            Intent intent=new Intent(this,ReceiverDetailProfileActivity.class);
            intent.putExtra(Contants.KEY_USER,user);
            startActivity(intent);
        });
        binding.textNewName.setOnClickListener(v -> {
            String newName=binding.editTextNewName.getText().toString();
            if(!newName.isEmpty())
            {
                viewModel.setNameGroup(conservation.getConservationID(),conservation.getReceiverID(),newName);
                binding.profileName.setText(newName);
                binding.editTextNewName.setText("");
            }

        });
        binding.textUserGroupChat.setOnClickListener(v -> {
            if(binding.recyclerViewUserGroupChat.getVisibility()==View.GONE)
            {
                binding.recyclerViewUserGroupChat.setVisibility(View.VISIBLE);
                viewModel.getMemeberGroupChat(conservation.getReceiverID());
            }else if(binding.recyclerViewUserGroupChat.getVisibility()==View.VISIBLE)
            {
                binding.recyclerViewUserGroupChat.setVisibility(View.GONE);
            }
        });
        binding.textFile.setOnClickListener(v -> {
            if(binding.recyclerViewFile.getVisibility()==View.GONE)
            {
                binding.recyclerViewFile.setVisibility(View.VISIBLE);
                viewModel.getListFile(conservation.getReceiverID());
            }else if(binding.recyclerViewFile.getVisibility()==View.VISIBLE)
            {
                binding.recyclerViewFile.setVisibility(View.GONE);
            }
        });
        binding.textFileImage.setOnClickListener(v -> {
            if(binding.recyclerViewFileImage.getVisibility()==View.GONE)
            {
                binding.recyclerViewFileImage.setVisibility(View.VISIBLE);
                viewModel.getListImage(conservation.getReceiverID());
            }else if(binding.recyclerViewFileImage.getVisibility()==View.VISIBLE)
            {
                binding.recyclerViewFileImage.setVisibility(View.GONE);
            }
        });
    }


    public void onUserClicked(User user)
    {
            Intent intent=new Intent(this, ReceiverDetailProfileActivity.class);
            intent.putExtra(Contants.KEY_USER,user);
            startActivity(intent);
    }
    @SuppressLint("IntentReset")
    public void onUserClickedFileMessage(PDFClass pdfClass)
    {
        Intent intent=new Intent(Intent.ACTION_VIEW);
        intent.setType("application/pdf");
        intent.setData(Uri.parse(pdfClass.getUrlFile()));
        startActivity(intent);
    }
    private void loadReceiverConservationDetail() {
        conservation = (Conservation) getIntent().getSerializableExtra(Contants.KEY_CONVERSATION);

    }

    private Bitmap getBitmapFromEncodeString(String encodeImage) {
        byte[] bytes = Base64.decode(encodeImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}
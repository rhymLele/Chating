package com.duc.chatting.chat.views;

import android.annotation.SuppressLint;
import android.app.ComponentCaller;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.duc.chatting.R;
import com.duc.chatting.chat.adapters.ChatAdapter;
import com.duc.chatting.chat.models.ChatMessage;
import com.duc.chatting.chat.models.PDFClass;
import com.duc.chatting.chat.models.User;
import com.duc.chatting.chat.viewmodels.ChatActivityViewModel;
import com.duc.chatting.databinding.ActivityChatBinding;
import com.duc.chatting.home.views.HomeActivity;
import com.duc.chatting.utilities.Contants;
import com.duc.chatting.utilities.PreferenceManager;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;
    private User receiverUser;
    private String conservationID = "";
    private PreferenceManager preferenceManager;
    private ChatActivityViewModel viewModel;

    //for dialog able and disable or rep ibox

    private Dialog dialog;
    private boolean isCheckedButton = false;
    private TextView textRepInbox, textDisableInboxForMe, textDisableInboxForAll, textDestroy;
    private String encodeImageSend = null;
    private ChatAdapter chatAdapter;
    private Uri dataFile = null;
    private Uri dataImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_chat);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        preferenceManager = new PreferenceManager(getApplicationContext());
        viewModel = new ViewModelProvider(this).get(ChatActivityViewModel.class);
        loadReceiverDetails();
        viewModel.getChatMessagesMutableLiveData().observe(this, chatMessages -> {
            if (receiverUser.getImgProfile() == null) {
                chatAdapter = new ChatAdapter(chatMessages
                        , receiverUser.getName()
                        , preferenceManager.getString(Contants.KEY_USER_ID)
                        , this::onUserClickedFileMessage
                        , this::onUserClicked
                        , this::onUserClickedMessageStatus);
            } else {
                chatAdapter = new ChatAdapter(
                        chatMessages,
                        receiverUser.getName(),
                        getBitmapFromEncodeString(receiverUser.getImgProfile())
                        , preferenceManager.getString(Contants.KEY_USER_ID)
                        , this::onUserClickedFileMessage
                        , this::onUserClicked
                        , this::onUserClickedMessageStatus);


            }
            binding.chatRecyclerView.setAdapter(chatAdapter);
        });
        viewModel.getSentInputMessage().observe(this, Boolean -> {
            if (Boolean == java.lang.Boolean.TRUE) {
                binding.inputMessage.setText(null);
            }
        });
        viewModel.getLoadChatRecycleView().observe(this, Boolean -> {
            if (Boolean == java.lang.Boolean.TRUE) {
                binding.chatRecyclerView.setVisibility(View.VISIBLE);
            }
        });
        viewModel.getLoadProgressBarMutableLiveData().observe(this, Boolean -> {
            if (Boolean == java.lang.Boolean.TRUE) {
                binding.progressBar.setVisibility(View.GONE);
            }
        });
        viewModel.getThemeConservationMutableLiveData().observe(this, theme -> {
            if (theme != null) {
                if (theme.equals("Love")) {
                    binding.chatRecyclerView.setBackgroundResource(R.color.redAccent);
                }
                if (theme.equals("Friend")) {
                    binding.chatRecyclerView.setBackgroundResource(R.color.greyLight);
                }
            }
        });
        setListener();

        viewModel.listenMessages(preferenceManager.getString(Contants.KEY_USER_ID), receiverUser.getId());

        //dialog for status message

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_setonlongclick_message);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        textRepInbox = dialog.findViewById(R.id.textRepInbox);
        textDisableInboxForMe = dialog.findViewById(R.id.textDisableInboxForMe);
        textDisableInboxForAll = dialog.findViewById(R.id.textDisableInboxForAll);
        textDestroy = dialog.findViewById(R.id.textDestroy);
        textDestroy.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    private void setListener() {
        binding.imageBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        });
        binding.inputMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    binding.imgSend.setVisibility(View.VISIBLE);
                    binding.fileSend.setVisibility(View.VISIBLE);
                } else {
                    binding.imgSend.setVisibility(View.GONE);
                    binding.fileSend.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.layoutSendIb.setOnClickListener(v -> {
            if (dataFile != null) {
                viewModel.sendMesageFile(
                        preferenceManager.getString(Contants.KEY_USER_ID),
                        preferenceManager.getString(Contants.KEY_NAME),
                        preferenceManager.getString(Contants.KEY_IMAGE),
                                receiverUser.getId(),
                                receiverUser.getId(),
                                receiverUser.getImgProfile(),
                                dataFile,
                                binding.inputMessage.getText().toString()
                        );
                dataFile=null;
            }else if(dataImage!=null){
                viewModel.sendMessageImage(
                        preferenceManager.getString(Contants.KEY_USER_ID),
                        preferenceManager.getString(Contants.KEY_NAME),
                        preferenceManager.getString(Contants.KEY_IMAGE),
                        receiverUser.getId(),
                        receiverUser.getName(),
                        receiverUser.getImgProfile(),
                        dataImage
                );
                dataImage=null;
                binding.roundedImageViewSend.setVisibility(View.GONE);

            }else if(!binding.inputMessage.getText().toString().isEmpty()){
                viewModel.sendMessage(
                        preferenceManager.getString(Contants.KEY_USER_ID),
                        preferenceManager.getString(Contants.KEY_NAME),
                        preferenceManager.getString(Contants.KEY_IMAGE),
                        receiverUser.getId(),
                        receiverUser.getName(),
                        receiverUser.getImgProfile(),
                        binding.inputMessage.getText().toString()
                );
            }
        });
        //click select file on device
        binding.fileSend.setOnClickListener(v -> {
            selectFile();
        });
    }
    //open folder pdf on device
    private void selectFile() {
        Intent intent=new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select PDF"),1);
    }
    //get data from device after select pdf file

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data, @NonNull ComponentCaller caller) {
        super.onActivityResult(requestCode, resultCode, data, caller);
    }

    private void loadReceiverDetails() {
        receiverUser = (User) getIntent().getSerializableExtra(Contants.KEY_USER);
        binding.textName.setText(receiverUser.getName());

    }

    private Bitmap getBitmapFromEncodeString(String encodeImage) {
        byte[] bytes = Base64.decode(encodeImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    @SuppressLint("IntentReset")
    public void onUserClickedFileMessage(PDFClass pdfClass) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setType("application/pdf");
        intent.setData(Uri.parse(pdfClass.getUrlFile()));
        startActivity(intent);
    }

    public void onUserClicked(User user) {
        Intent intent = new Intent(getApplicationContext(), ReceiverDetailProfileActivity.class);
        intent.putExtra(Contants.KEY_USER, user);
        startActivity(intent);
    }

    public void onUserClickedMessageStatus(ChatMessage chatMessage) {
        //process status of message
    }
}
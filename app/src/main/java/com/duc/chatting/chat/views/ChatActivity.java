package com.duc.chatting.chat.views;

import android.annotation.SuppressLint;
import android.app.ComponentCaller;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.duc.chatting.R;
import com.duc.chatting.call.models.DataModelType;
import com.duc.chatting.call.repository.MainRepository;
import com.duc.chatting.call.views.CallActivity;
import com.duc.chatting.chat.adapters.ChatAdapter;
import com.duc.chatting.chat.models.ChatMessage;
import com.duc.chatting.chat.models.Conservation;
import com.duc.chatting.chat.models.PDFClass;
import com.duc.chatting.chat.models.User;
import com.duc.chatting.chat.viewmodels.BlockUserViewModel;
import com.duc.chatting.chat.viewmodels.ChatActivityViewModel;
import com.duc.chatting.databinding.ActivityChatBinding;
import com.duc.chatting.home.views.ConservationFragment;
import com.duc.chatting.home.views.HomeActivity;
import com.duc.chatting.utilities.AppPreference;
import com.duc.chatting.utilities.Contants;
import com.duc.chatting.utilities.PreferenceManager;
import com.permissionx.guolindev.PermissionX;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.units.qual.C;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity{
    private ActivityChatBinding binding;
    private User receiverUser;
    private String conservationID = "";
    private PreferenceManager preferenceManager;
    private ChatActivityViewModel viewModel;
    private BlockUserViewModel blockUserViewModel;
    //for dialog able and disable or rep ibox

    private Dialog dialog;
    private boolean isCheckedButton = false;
    private TextView textRepInbox, textDisableInboxForMe, textDisableInboxForAll, textDestroy;
    private String encodeImageSend = null;
    private ChatAdapter chatAdapter;
    private Uri dataFile = null;
    private String encodeFileSend=null;
    private Uri dataImage = null;

    private MainRepository mainRepository;
    private Boolean isCameraMuted = false;
    private Boolean isMicrophoneMuted = false;
    AppPreference prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Log.d("ChatActivity", "OnCreated");
        preferenceManager = new PreferenceManager(getApplicationContext());
        viewModel = new ViewModelProvider(this).get(ChatActivityViewModel.class);
        blockUserViewModel= new ViewModelProvider(this).get(BlockUserViewModel.class);
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
        //observe all
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
        viewModel.getIsUserActive().observe(this, isActive  -> {
            if (isActive  == Boolean.TRUE) {
                binding.checkUserOn.setBackgroundResource(R.drawable.user_active);
                Log.d("ChatActivity","Stattus"+isActive );
            }else   {
                binding.checkUserOn.setBackgroundResource(R.drawable.user_inactive);
            }
        });
        viewModel.getConservationIDMutableLiveData().observe(this, mutConservationId -> {
            conservationID = mutConservationId;
        });
        viewModel.getThemeConservationMutableLiveData().observe(this, theme -> {
            if (theme != null) {
                if (theme.equals("Love")) {
                    binding.chatRecyclerView.setBackgroundResource(R.drawable.love);
                }
                if (theme.equals("Default")) {
                    binding.chatRecyclerView.setBackgroundResource(R.color.AcliceBlue);
                }
                if (theme.equals("Star")) {
                    binding.chatRecyclerView.setBackgroundResource(R.drawable.star);
                }
                if (theme.equals("Ocean")) {
                    binding.chatRecyclerView.setBackgroundResource(R.drawable.element);
                }
                if (theme.equals("Kids")) {
                    binding.chatRecyclerView.setBackgroundResource(R.drawable.kids);
                }
                if (theme.equals("Sun")) {
                    binding.chatRecyclerView.setBackgroundResource(R.drawable.sun);
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
        blockUserViewModel.observeBlockStatusRealtime(receiverUser.getId());

        blockUserViewModel.getBlockStatusLiveData().observe(this, status -> {
            if (status == null) return;

            if (status.youBlockedThem) {
                // A chặn B
                binding.textBlock.setText("You blocked this person.Go to list block to unblock");
                binding.llGrSend.setVisibility(View.GONE);
                binding.layoutSend.setVisibility(View.GONE);
                binding.textBlockedNotice.setVisibility(View.VISIBLE);
            } else if (status.theyBlockedYou) {
                // B chặn A
                binding.textBlock.setText("This person is not available!");
                binding.llGrSend.setVisibility(View.GONE);
                binding.layoutSend.setVisibility(View.GONE);
                binding.textBlockedNotice.setVisibility(View.VISIBLE);
            } else {
                // Không ai chặn ai
                binding.llGrSend.setVisibility(View.VISIBLE);
                binding.layoutSend.setVisibility(View.VISIBLE);
                binding.textBlockedNotice.setVisibility(View.GONE);
            }
        });


    }

    


    private void setListener() {
        binding.textName.setOnClickListener(v -> {
            Conservation conservation= new Conservation(conservationID,receiverUser.getId());
            Intent i=new Intent(this, ReceiverConservationActivity.class);
            i.putExtra(Contants.KEY_CONVERSATION,conservation);
            startActivity(i);
        });
        binding.llNameAndVisible.setOnClickListener(v -> {
            Conservation conservation= new Conservation(conservationID,receiverUser.getId());
            Intent i=new Intent(this, ReceiverConservationActivity.class);
            i.putExtra(Contants.KEY_USER,receiverUser);
            i.putExtra(Contants.KEY_CONVERSATION,conservation);
            startActivity(i);
        });
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
                    binding.voiceSend.setVisibility(View.VISIBLE);
                } else {
                    binding.imgSend.setVisibility(View.GONE);
                    binding.fileSend.setVisibility(View.GONE);
                    binding.voiceSend.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.layoutSendIb.setOnClickListener(v -> {
            if (encodeFileSend != null) {

                viewModel.sendMesageFile(
                        preferenceManager.getString(Contants.KEY_USER_ID),
                        preferenceManager.getString(Contants.KEY_NAME),
                        preferenceManager.getString(Contants.KEY_IMAGE),
                        receiverUser.getId(),
                        receiverUser.getName(),
                        receiverUser.getImgProfile(),
                        encodeFileSend,
                        binding.inputMessage.getText().toString()
                );
                encodeFileSend = null;
            } else if (encodeImageSend != null) {
                viewModel.sendMessageImage(
                        preferenceManager.getString(Contants.KEY_USER_ID),
                        preferenceManager.getString(Contants.KEY_NAME),
                        preferenceManager.getString(Contants.KEY_IMAGE),
                        receiverUser.getId(),
                        receiverUser.getName(),
                        receiverUser.getImgProfile(),
                        encodeImageSend
                );
                encodeImageSend = null;
                binding.roundedImageViewSend.setVisibility(View.GONE);

            } else if (!binding.inputMessage.getText().toString().isEmpty()) {
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
        //select image
        binding.imgSend.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImageBanner.launch(intent);
        });
        binding.imageCall.setOnClickListener(v -> {
            PermissionX.init(this)
                    .permissions(android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO)
                    .request((allGranted, grantedList, deniedList) -> {
                        if (allGranted) {
                            //login to firebase here
                            Intent intent=new Intent(this, CallActivity.class);
                            intent.putExtra("Target",receiverUser.getPhoneNumber());
                            startActivity(intent);
                        }
                    });

//            if(binding.chatScreen.getVisibility()==View.VISIBLE)
//            {
//                binding.chatScreen.setVisibility(View.GONE);
//                binding.callScreen.setVisibility(View.VISIBLE);
//                mainRepository.sendCallRequest(receiverUser.getId(),()->{
//                    Toast.makeText(this, "couldnt find the target", Toast.LENGTH_SHORT).show();
//                });
//            }
        });
    }

    private final ActivityResultLauncher<Intent> pickImageBanner = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
//                        dataImage = result.getData().getData();
                        Uri imageUri = result.getData().getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                            binding.roundedImageViewSend.setImageBitmap(bitmap);
                            binding.roundedImageViewSend.setVisibility(View.VISIBLE);
                            encodeImageSend = encodeImage(bitmap);
                        } catch (IOException ioException) {
                            throw new RuntimeException();
                        }
                    }
                }
            });

    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }


    //open folder pdf on device
    private void selectFile() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), 1);
    }
    //get data from device after select pdf file

    @SuppressLint("Range")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            dataFile = data.getData();

            Uri uri = data.getData();
            String uriString = uri.toString();
//            File myFile = new File(uriString);
            String displayName = null;
            try {
                encodeFileSend=encodeUriToBase64(this,uri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (uriString.startsWith("content://")) {
                Cursor cursor = null;
                try {
                    cursor = this.getContentResolver().query(uri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        binding.inputMessage.setText(displayName);
                    }
                } finally {
                    cursor.close();
                }
            }
        }
    }

    private void loadReceiverDetails() {
        receiverUser = (User) getIntent().getSerializableExtra(Contants.KEY_USER);
        viewModel.countMemberOnline();
//        Log.d("User",receiverUser.getPhoneNumber().toString());
        if (!binding.textName.toString().isEmpty()) {
            binding.textName.setText(receiverUser.getName());
        }
    }

    private Bitmap getBitmapFromEncodeString(String encodeImage) {
        byte[] bytes = Base64.decode(encodeImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
    public Uri decodeBase64ToPdfAndGetUri(Context context, String base64, String fileName) throws IOException {
        byte[] decodedBytes = Base64.decode(base64, Base64.DEFAULT);
        File file = new File(context.getExternalFilesDir(null), fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(decodedBytes);
        fos.close();

        return FileProvider.getUriForFile(
                context,
                context.getPackageName() + ".provider",
                file
        );
    }
    public void openPdf(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(intent, "Xem file PDF"));
    }
    @SuppressLint("IntentReset")
    public void onUserClickedFileMessage(PDFClass pdfClass) {

        String url = pdfClass.getUrlFile();
        try {
            Uri pdfUri = decodeBase64ToPdfAndGetUri(this, url, pdfClass.getFileName());
            openPdf(this, pdfUri);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Oops ,can not open file Pdf!", Toast.LENGTH_SHORT).show();
        }
    }

    public String encodeUriToBase64(Context context, Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        inputStream.close();
        byte[] fileBytes = outputStream.toByteArray();

        return Base64.encodeToString(fileBytes, Base64.NO_WRAP);
    }

    public void onUserClicked(User user) {
        Intent intent = new Intent(getApplicationContext(), ReceiverDetailProfileActivity.class);
        intent.putExtra(Contants.KEY_USER, user);
        startActivity(intent);
    }

    public void onUserClickedMessageStatus(ChatMessage chatMessage) {
        //process status of message
        if (chatMessage.getSenderID().equals(preferenceManager.getString(Contants.KEY_USER_ID))) {
            textDisableInboxForMe.setVisibility(View.VISIBLE);
            textDisableInboxForAll.setVisibility(View.VISIBLE);
        } else {
            textDisableInboxForMe.setVisibility(View.GONE);
            textDisableInboxForAll.setVisibility(View.GONE);
        }
        dialog.show();
        textDisableInboxForMe.setOnClickListener(v -> {
            String status = "disableForMe";
            viewModel.setStatusMessage(chatMessage.getMessageID(), status);

            dialog.dismiss();
        });
        textDisableInboxForAll.setOnClickListener(v -> {
            String status = "disableForAll";
            viewModel.setStatusMessage(chatMessage.getMessageID(), status);

            dialog.dismiss();
        });
        textRepInbox.setOnClickListener(v -> {
            dialog.dismiss();
            binding.imgSend.setVisibility(View.GONE);
            binding.fileSend.setVisibility(View.GONE);
            binding.llRepIbLocal.setVisibility(View.VISIBLE);
            binding.layoutSendIb.setVisibility(View.GONE);
            binding.layoutSendRepIb.setVisibility(View.VISIBLE);
            isCheckedButton = true;
            if (chatMessage.getFileName() != null) {
                binding.textMessageRepLocal.setVisibility(View.VISIBLE);
                binding.roundImageViewRepLocal.setVisibility(View.GONE);
                binding.textMessageRepLocal.setText(chatMessage.getFileName());

            } else if (chatMessage.getUrlImage() != null) {
                binding.roundImageViewRepLocal.setVisibility(View.VISIBLE);
                binding.textMessageRepLocal.setVisibility(View.GONE);
                Picasso.get().load(chatMessage.getUrlImage()).into(binding.roundImageViewRepLocal);

            } else {
                binding.textMessageRepLocal.setVisibility(View.VISIBLE);
                binding.roundImageViewRepLocal.setVisibility(View.GONE);
                binding.textMessageRepLocal.setText(chatMessage.getMessage());

            }
            binding.closeRepIbLocal.setOnClickListener(v1 -> {
                binding.llRepIbLocal.setVisibility(View.GONE);
                binding.imgSend.setVisibility(View.VISIBLE);
                binding.fileSend.setVisibility(View.VISIBLE);
                binding.textMessageRepLocal.setText(null);
                binding.roundImageViewRepLocal.setImageBitmap(null);
            });
            if (!binding.textMessageRepLocal.getText().toString().isEmpty() || binding.roundImageViewRepLocal.getDrawable() != null) {
                binding.layoutSendRepIb.setOnClickListener(v2 -> {
                    if (!binding.inputMessage.getText().toString().isEmpty()) {
                        if (chatMessage.getFileName() != null) {
                            viewModel.sendMessageRepLocal(
                                    preferenceManager.getString(Contants.KEY_USER_ID),
                                    preferenceManager.getString(Contants.KEY_NAME),
                                    preferenceManager.getString(Contants.KEY_IMAGE),
                                    receiverUser.getId(),
                                    receiverUser.getName(),
                                    receiverUser.getImgProfile(),
                                    binding.inputMessage.getText().toString(),
                                    chatMessage.getFileName(),
                                    chatMessage.getUrlFile(),
                                    chatMessage.getUrlImage()
                            );
                        } else if (chatMessage.getFileName() == null) {
                            viewModel.sendMessageRepLocal(
                                    preferenceManager.getString(Contants.KEY_USER_ID),
                                    preferenceManager.getString(Contants.KEY_NAME),
                                    preferenceManager.getString(Contants.KEY_IMAGE),
                                    receiverUser.getId(),
                                    receiverUser.getName(),
                                    receiverUser.getImgProfile(),
                                    binding.inputMessage.getText().toString(),
                                    chatMessage.getMessage(),
                                    chatMessage.getUrlFile(),
                                    chatMessage.getUrlImage());
                        }
                    }
                    isCheckedButton = false;
                    binding.llRepIbLocal.setVisibility(View.GONE);
                    binding.textMessageRepLocal.setText(null);
                    binding.roundImageViewRepLocal.setImageBitmap(null);
                    binding.layoutSendIb.setVisibility(View.VISIBLE);
                    binding.layoutSendRepIb.setVisibility(View.GONE);

                });
            }
        });
    }
}
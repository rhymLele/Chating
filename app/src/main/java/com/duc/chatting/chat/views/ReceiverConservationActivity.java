package com.duc.chatting.chat.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.duc.chatting.chat.viewmodels.BlockUserViewModel;
import com.duc.chatting.chat.viewmodels.ReceiverConservationViewModel;
import com.duc.chatting.databinding.ActivityReceiverConservationBinding;
import com.duc.chatting.utilities.Contants;
import com.duc.chatting.utilities.PreferenceManager;
import com.duc.chatting.utilities.widgets.StartGameDialogFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ReceiverConservationActivity extends AppCompatActivity implements StartGameDialogFragment.StartGameDialogListener {

    private ActivityReceiverConservationBinding binding;
    private ReceiverConservationViewModel viewModel;
    private UserGroupAdapter userGroupAdapter;
    private FileListAdapter fileListAdapter;
    private ImageListAdapter imageListAdapter;
    private Conservation conservation;
    private PreferenceManager preferenceManager;
    private User userA;
    private BlockUserViewModel blockUserViewModel;
    StartGameDialogFragment dialog;
    DatabaseReference databaseReference = FirebaseDatabase
            .getInstance()
            .getReferenceFromUrl("https://chatting-4faf6-default-rtdb.firebaseio.com/");
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
        userA = (User) getIntent().getSerializableExtra(Contants.KEY_USER);
        preferenceManager=new PreferenceManager(this);
        viewModel = new ViewModelProvider(this).get(ReceiverConservationViewModel.class);
        blockUserViewModel=new  ViewModelProvider(this).get(BlockUserViewModel.class);

        viewModel.getIsCheckedCon().observe(this,ob->{
            if(ob==Boolean.TRUE)
            {
                viewModel.getReceiverMutableLiveData().observe(this, user -> {

                    userA=user;
                    binding.profileName.setText(user.getName());
                    if (user.getImgProfile() != null) {
                        binding.profileImage.setImageBitmap(getBitmapFromEncodeString(user.getImgProfile()));
                    }


                });
            }else
            {
                binding.profileName.setText(userA.getName());
                if (userA.getImgProfile() != null) {
                    binding.profileImage.setImageBitmap(getBitmapFromEncodeString(userA.getImgProfile()));
                }
            }
        });


        viewModel.getIsCheckedGroupChatPersonalMutableLiveData().observe(this, isChecked -> {
            if (isChecked.equals(Boolean.TRUE)) {
                binding.personChatView.setVisibility(View.GONE);
                binding.lnBlock.setVisibility(View.GONE);
                binding.lnLeave.setVisibility(View.VISIBLE);
                binding.groupChatView.setVisibility(View.VISIBLE);
                binding.groupMemberChat.setVisibility(View.VISIBLE);
                binding.textReport.setText("Report");
            }
        });
        blockUserViewModel.observeBlockStatusRealtime(userA.getId());
        blockUserViewModel.getBlockStatusLiveData().observe(this, status -> {
            if (status == null) return;

            if (status.youBlockedThem) {
                binding.textBlock.setText("Unblock User");
                binding.lnBlock.setVisibility(View.VISIBLE);
            } else if (status.theyBlockedYou) {
                binding.lnBlock.setVisibility(View.GONE); // bạn bị chặn => ẩn block
            } else {
                binding.textBlock.setText("Block User");
                binding.lnBlock.setVisibility(View.VISIBLE);
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
    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        String resultTheme=intent.getStringExtra("selectedTheme");
                        viewModel.setThemeConservation(conservation.getConservationID(),resultTheme);
                        // Handle the Intent
                    }
                }
            });
    private void setClicked()
    {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.theme.setOnClickListener(v -> {
//            if(binding.listThemeBackground.getVisibility()==View.GONE)
//            {
//                binding.listThemeBackground.setVisibility(View.VISIBLE);
//            }else if(binding.listThemeBackground.getVisibility()==View.VISIBLE)
//            {
//                binding.listThemeBackground.setVisibility(View.GONE);
//            }
            mStartForResult.launch(new Intent(this, ThemeActivity.class));
        });
//        binding.theme.setOnLongClickListener(v -> {
//            mStartForResult.launch(new Intent(this, ThemeActivity.class));
//            return  false;
//        });
//        binding.themLove.setOnClickListener(v -> {
//            String theme="love";
//            viewModel.setThemeConservation(conservation.getConservationID(),theme);
//        });
//        binding.themFriend.setOnClickListener(v -> {
//            String theme="friend";
//            viewModel.setThemeConservation(conservation.getConservationID(),theme);
//        });
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
                viewModel.getListFile(conservation.getConservationID());
            }else if(binding.recyclerViewFile.getVisibility()==View.VISIBLE)
            {
                binding.recyclerViewFile.setVisibility(View.GONE);
            }
        });
        binding.textFileImage.setOnClickListener(v -> {
            if(binding.recyclerViewFileImage.getVisibility()==View.GONE)
            {
                binding.recyclerViewFileImage.setVisibility(View.VISIBLE);
                viewModel.getListImage(conservation.getConservationID());
            }else if(binding.recyclerViewFileImage.getVisibility()==View.VISIBLE)
            {
                binding.recyclerViewFileImage.setVisibility(View.GONE);
            }
        });
        binding.textReport.setOnClickListener(v -> {
            if(userA!=null) {
                showReportDialog(userA.getId());
            }else {
                Toast.makeText(this, "Oppss!", Toast.LENGTH_SHORT).show();
            }
        });
        dialog = new StartGameDialogFragment();


        binding.lnBlock.setOnClickListener(v -> {
            dialog.show(getSupportFragmentManager(), "StartGameDialog");

        });
    }
    private void showReportDialog(String conservationId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Report Conversation");

        // Tạo EditText để nhập nội dung báo cáo
        final EditText input = new EditText(this);
        input.setHint("Enter report reason...");
//        input.setPadding(50, 40, 50, 10); // Tuỳ chỉnh padding cho đẹp
        builder.setView(input);

        // Nút Gửi Báo Cáo
        builder.setPositiveButton("Report", null); // Gán null để xử lý sau trong setOnShowListener

        // Nút Hủy
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        // Tuỳ chỉnh sau khi dialog hiển thị
        dialog.setOnShowListener(dialogInterface -> {
            // Tuỳ chỉnh màu nền
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.custom_dialog_background);

            // Tuỳ chỉnh màu nút
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            negativeButton.setTextColor(Color.RED);

            // Gắn sự kiện click cho nút Report (vì lúc tạo đã set null)
            positiveButton.setOnClickListener(v -> {
                String reportMessage = input.getText().toString().trim();
                if (!reportMessage.isEmpty()) {
                    reportUser(conservationId, reportMessage);
                    dialog.dismiss();
                    Toast.makeText(this, "Reported ", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Please enter a reason for reporting!", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }
    public void reportUser(String postId,String message) {
        String currentUserId = preferenceManager.getString(Contants.KEY_USER_ID);
        Map<String, Object> report = new HashMap<>();
        report.put("reportedBy", currentUserId);
        report.put("conservationId", postId);
        report.put("timestamp", FieldValue.serverTimestamp());
        report.put("messageReport", message);

        FirebaseFirestore.getInstance().collection("Reports")
                .add(report)
                .addOnSuccessListener(documentReference ->
                        Log.d("Firebase", "Report submitted successfully"))
                .addOnFailureListener(e ->
                        Log.e("Firebase", "Failed to submit report", e));
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

    @Override
    public void onConfirm() {
        if ("Block User".contentEquals(binding.textBlock.getText())) {
            blockUserViewModel.blockUser(userA.getId());
            Toast.makeText(this, "Blocked this person", Toast.LENGTH_SHORT).show();
        } else if ("Unblock User".contentEquals(binding.textBlock.getText())) {
            blockUserViewModel.unblockUser(userA.getId());
            Toast.makeText(this, "Unblocked this person", Toast.LENGTH_SHORT).show();
        }
        dialog.dismiss();
    }

    @Override
    public void onDiscard() {
        dialog.dismiss();
    }
}
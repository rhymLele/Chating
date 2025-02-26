package com.duc.chatting.chat.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.duc.chatting.R;
import com.duc.chatting.chat.adapters.UserAdapter;
import com.duc.chatting.chat.models.User;
import com.duc.chatting.chat.viewmodels.ChatUserViewModel;
import com.duc.chatting.databinding.ActivityUserBinding;
import com.duc.chatting.home.views.HomeActivity;
import com.duc.chatting.utilities.Contants;

public class UserActivity extends AppCompatActivity {
    ActivityUserBinding binding;
    private ChatUserViewModel viewModel;
    UserAdapter userAdapter;
    @SuppressLint("ClickableViewAccessibility")
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
        binding.searchAddUserr.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    // Khi có text nhập vào, ẩn icon trái và phải
                    binding.searchAddUserr.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    viewModel.getUsers(s.toString());
                } else {
                    // Khi không có text, hiển thị lại icon trái và phải
                    binding.searchAddUserr.setCompoundDrawablesWithIntrinsicBounds(
                            ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_search_24),
                            null,
                            ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_qr_code_scanner_24),
                            null
                    );
            }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.searchAddUserr.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // Lấy drawableRight (icon QR scanner)
                Drawable drawableRight = binding.searchAddUserr.getCompoundDrawables()[2];

                if (drawableRight != null) {
                    int drawableWidth = drawableRight.getBounds().width();
                    int editTextWidth = binding.searchAddUserr.getWidth();
                    int touchX = (int) event.getX();

                    if (touchX >= (editTextWidth - drawableWidth - binding.searchAddUserr.getPaddingRight())) {
                        // Người dùng đã chạm vào drawableRight
                        Toast.makeText(v.getContext(), "QR Scanner Clicked!", Toast.LENGTH_SHORT).show();
                        return true; // Sự kiện đã được xử lý
                    }
                }
            }
            return false; // Cho phép EditText xử lý các sự kiện khác (như nhập văn bản)
        });
    }
    public void onUserClicked(User user){
        Intent intent=new Intent(getApplicationContext(), ReceiverDetailProfileActivity.class);
        intent.putExtra(Contants.KEY_USER,user);
        startActivity(intent);
    }
}
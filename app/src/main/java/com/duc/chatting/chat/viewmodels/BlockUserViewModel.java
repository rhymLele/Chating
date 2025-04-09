package com.duc.chatting.chat.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.duc.chatting.chat.models.User;
import com.duc.chatting.utilities.Contants;
import com.duc.chatting.utilities.PreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BlockUserViewModel extends AndroidViewModel {

    private final MutableLiveData<Boolean> blockStatus ;
    private final MutableLiveData<String> errorMessage ;
    private final MutableLiveData<User> user ;
    private User userBlocked;
    private PreferenceManager preferenceManager;
    DatabaseReference databaseReference = FirebaseDatabase
            .getInstance()
            .getReferenceFromUrl("https://chatting-4faf6-default-rtdb.firebaseio.com/");

    public MutableLiveData<User> getUser() {
        return user;
    }

    public BlockUserViewModel(@NonNull Application application) {
        super(application);
        blockStatus=new MutableLiveData<>();
        user=new MutableLiveData<>();
        errorMessage=new MutableLiveData<>();
        preferenceManager=new PreferenceManager(application);
    }

    public MutableLiveData<Boolean> getBlockStatus() {
        return blockStatus;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void blockUser(String userId) {
        String currentUserId = preferenceManager.getString(Contants.KEY_USER_ID);
        databaseReference.child(Contants.KEY_BLOCK_LIST)
                .child(currentUserId)  // ID của người dùng hiện tại
                .child(userId)         // ID của người bị block
                .setValue(true)        // Gán giá trị true để đánh dấu người dùng này bị block
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Xử lý thành công, đã block người dùng
                        Log.d("BlockUser", "User blocked successfully.");
                    } else {
                        // Xử lý lỗi
                        Log.d("BlockUser", "Error blocking user.");
                    }
                });
    }

    public void unblockUser(String blockUserId) {
        String currentUserId = preferenceManager.getString(Contants.KEY_USER_ID);
        databaseReference.child(Contants.KEY_BLOCK_LIST)
                .child(currentUserId)  // ID của người dùng hiện tại
                .child(blockUserId)         // ID của người bị unblock
                .removeValue()         // Xóa người dùng khỏi danh sách block
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Xử lý thành công, đã unblock người dùng
                        Log.d("UnblockUser", "User unblocked successfully.");
                    } else {
                        // Xử lý lỗi
                        Log.d("UnblockUser", "Error unblocking user.");
                    }
                });
    }

    public void getBlockedUsers() {

        String currentUserId = preferenceManager.getString(Contants.KEY_USER_ID);
        // Truy cập vào danh sách blockedUsers của người dùng hiện tại
        databaseReference
                .child(Contants.KEY_BLOCK_LIST)
                .child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Nếu có người bị block, xử lý dữ liệu
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String blockedUserId = snapshot.getKey();  // Lấy ID của người bị block
                                Log.d("BlockedUser", "Blocked User ID: " + blockedUserId);

                                // Có thể lưu các ID này vào một list hoặc xử lý theo nhu cầu của bạn
                                // Ví dụ: add vào danh sách blockedUserIds
                                List<String> blockedUserIds = new ArrayList<>();
                                blockedUserIds.add(blockedUserId);

                                // Hiển thị danh sách người bị block
                                Log.d("BlockedUser", "Blocked users: " + blockedUserIds.toString());
                            }
                        } else {
                            // Nếu không có người bị block
                            Log.d("BlockedUser", "No blocked users.");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Xử lý lỗi nếu có
                        Log.d("BlockedUser", "Error fetching blocked users.");
                    }
                });
    }
}

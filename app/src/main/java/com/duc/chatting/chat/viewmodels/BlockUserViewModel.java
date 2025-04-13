package com.duc.chatting.chat.viewmodels;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.duc.chatting.chat.models.BlockStatus;
import com.duc.chatting.chat.models.User;
import com.duc.chatting.home.viewmodels.ListFriendViewModel;
import com.duc.chatting.utilities.AppPreference;
import com.duc.chatting.utilities.Contants;
import com.duc.chatting.utilities.PreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BlockUserViewModel extends AndroidViewModel {

    private final MutableLiveData<Boolean> blockStatus ;
    private final MutableLiveData<User> user ;
    private User userBlocked;
    private  List<User> userBlockIdList;
    private PreferenceManager preferenceManager;
    private final MutableLiveData<Boolean> userrBlocker ;
    private final MutableLiveData<Boolean> userrBlocked ;
    Map<String, List<String>> blockedUserMap = new HashMap<>();
    private MutableLiveData<List<User>> friendsMutableLiveData;
    DatabaseReference databaseReference = FirebaseDatabase
            .getInstance()
            .getReferenceFromUrl("https://chatting-4faf6-default-rtdb.firebaseio.com/");
    AppPreference prefs ;
    private final MutableLiveData<BlockStatus> blockStatusLiveData = new MutableLiveData<>();
    public MutableLiveData<BlockStatus> getBlockStatusLiveData() {
        return blockStatusLiveData;
    }
    public MutableLiveData<Boolean> getIsBlockedBetweenUsers() {
        return isBlockedBetweenUsers;
    }

    private final MutableLiveData<Boolean> isBlockedBetweenUsers;
    public MutableLiveData<User> getUser() {
        return user;
    }
    public MutableLiveData<List<User>> getFriendsMutableLiveData() {
        return friendsMutableLiveData;
    }

    public BlockUserViewModel(@NonNull Application application) {
        super(application);
        blockStatus=new MutableLiveData<>();
        user=new MutableLiveData<>();
        userBlockIdList=new ArrayList<>();
        userrBlocked=new MutableLiveData<>();
        userrBlocker=new MutableLiveData<>();
        prefs=AppPreference.getInstance(application);
        friendsMutableLiveData=new MutableLiveData<>();
        isBlockedBetweenUsers = new MutableLiveData<>();
        preferenceManager=new PreferenceManager(application);
    }

    public MutableLiveData<Boolean> getBlockStatus() {
        return blockStatus;
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
                        blockStatus.postValue(Boolean.TRUE);
                        blockedUserMap.computeIfAbsent(currentUserId, k -> new ArrayList<>()).add(userId);

                        prefs.putMap(Contants.KEY_BLOCKED_MAP, blockedUserMap);
                    } else {
                        // Xử lý lỗi
                        Log.d("BlockUser", "Error blocking user.");
                    }
                });
    }
    public void observeBlockStatusRealtime(String otherUserId) {
        String currentUserId = preferenceManager.getString(Contants.KEY_USER_ID);
        DatabaseReference ref = databaseReference.child(Contants.KEY_BLOCK_LIST);
        // Khi mình block người kia
        ref.child(currentUserId).child(otherUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean youBlockedThem = snapshot.exists();
                        checkAndPostBlockStatus(youBlockedThem, null);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

        // Khi bị người kia block
        ref.child(otherUserId).child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean theyBlockedYou = snapshot.exists();
                        checkAndPostBlockStatus(null, theyBlockedYou);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private Boolean lastYouBlockedThem = false;
    private Boolean lastTheyBlockedYou = false;

    private void checkAndPostBlockStatus(Boolean youBlockedThem, Boolean theyBlockedYou) {
        if (youBlockedThem != null) lastYouBlockedThem = youBlockedThem;
        if (theyBlockedYou != null) lastTheyBlockedYou = theyBlockedYou;

        boolean isBlocked = lastYouBlockedThem || lastTheyBlockedYou;
        blockStatusLiveData.postValue(new BlockStatus(lastYouBlockedThem, lastTheyBlockedYou));
    }
    public void unblockUser(String blockUserId) {
        String currentUserId = preferenceManager.getString(Contants.KEY_USER_ID);
        databaseReference.child(Contants.KEY_BLOCK_LIST)
                .child(currentUserId)
                .child(blockUserId)
                .removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("UnblockUser", "User unblocked successfully.");
                        if (blockedUserMap.containsKey(currentUserId)) {
                            blockedUserMap.get(currentUserId).remove(blockUserId);
                            prefs.putMap(Contants.KEY_BLOCKED_MAP, blockedUserMap);
                        }
                        for (User u : new ArrayList<>(userBlockIdList)) {
                            if (u.getId().equals(blockUserId)) {
                                userBlockIdList.remove(u);
                                break;
                            }
                        }
                        friendsMutableLiveData.postValue(userBlockIdList);
                    } else {
                        Log.d("UnblockUser", "Error unblocking user.");
                    }
                });

    }

    public void getBlockedUsers() {
        String currentUserId = preferenceManager.getString(Contants.KEY_USER_ID);

        databaseReference
                .child(Contants.KEY_BLOCK_LIST)
                .child(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        blockedUserMap.clear(); // reset trước
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String blockedUserId = snapshot.getKey();
                                getUsers(blockedUserId, user -> {
                                    if (user != null && !userBlockIdList.contains(user)) {
                                        userBlockIdList.add(user);
                                        friendsMutableLiveData.postValue(userBlockIdList);
                                    }
                                });
                            }
                            Log.d("BlockedUserMap", "Loaded: " + blockedUserMap);
                        } else {
                            Log.d("BlockedUserMap", "No blocked users.");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("BlockedUserMap", "Error fetching: " + databaseError.getMessage());
                    }
                });
    }


    public interface UserCallback {
        void onUserRetrieved(User user);
    }
    public void getUsers(String phoneNumber, UserCallback callback) {
        databaseReference.child(Contants.KEY_COLLECTION_USERS).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<User> users = new ArrayList<>();
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            if (preferenceManager.getString(Contants.KEY_USER_ID).equals(postSnapshot.child(Contants.KEY_PHONE_NUMBER).getValue(String.class))) {
                                continue;
                            }
                            String phoneNumberTest = postSnapshot.child(Contants.KEY_PHONE_NUMBER).getValue(String.class);
                            if (phoneNumber.equals(phoneNumberTest)) {
                                String id = postSnapshot.child(Contants.KEY_PHONE_NUMBER).getValue(String.class);
                                String email = postSnapshot.child(Contants.KEY_EMAIL).getValue(String.class);
                                String name = postSnapshot.child(Contants.KEY_NAME).getValue(String.class);
                                String image = null, imageBanner = null, story = null;
                                if (postSnapshot.child(Contants.KEY_IMAGE).getValue(String.class) != null) {
                                    image = postSnapshot.child(Contants.KEY_IMAGE).getValue(String.class);
                                }
                                if (postSnapshot.child(Contants.KEY_IMAGE_BANNER).getValue(String.class) != null) {
                                    imageBanner = postSnapshot.child(Contants.KEY_IMAGE_BANNER).getValue(String.class);
                                }
                                if (postSnapshot.child(Contants.KEY_STORY_HISTORY).getValue(String.class) != null) {
                                    imageBanner = postSnapshot.child(Contants.KEY_STORY_HISTORY).getValue(String.class);
                                }
                                User user = new User(id, email, name, phoneNumber, image, imageBanner, story);
                                Log.d("FriendList",name+" "+id);
                                callback.onUserRetrieved(user);
                                return;
                            }
                        }
                        callback.onUserRetrieved(null);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onUserRetrieved(null);
                    }
                }
        );
    }
    public void getBlockedUsers(Runnable onComplete) {
        String currentUserId = preferenceManager.getString(Contants.KEY_USER_ID);
        databaseReference
                .child(Contants.KEY_BLOCK_LIST)
                .child(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        blockedUserMap.clear();
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String blockedUserId = snapshot.getKey();
                                blockedUserMap.computeIfAbsent(currentUserId, k -> new ArrayList<>()).add(blockedUserId);
                                Log.d("BlockID",String.valueOf(blockedUserId));
                            }

                        }
                        // lưu local
                        prefs.putMap(Contants.KEY_BLOCKED_MAP, blockedUserMap);
                        if (onComplete != null) onComplete.run();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        if (onComplete != null) onComplete.run(); // vẫn tiếp tục vào app
                    }
                });
    }


}

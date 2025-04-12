package com.duc.chatting.otherComponents.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.duc.chatting.R;
import com.duc.chatting.databinding.ActivityHelpBinding;
import com.duc.chatting.otherComponents.adapters.HelpAdapter;
import com.duc.chatting.otherComponents.models.HelpItem;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class HelpActivity extends AppCompatActivity {
    ActivityHelpBinding binding;
    HelpAdapter adapter;
    List<HelpItem> helpList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityHelpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("General"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Account"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Chats"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Groups"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Privacy"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Customization"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Payments"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Troubleshooting"));


        addContent();
        adapter = new HelpAdapter(helpList, item -> {
            if (item == null) {
                // check list empty
                binding.emptyText.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
                return;
            }
            onHelpClicked(item);
        });
        binding.helpRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.helpRecyclerView.setAdapter(adapter);
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                getSupportFragmentManager().popBackStack();
                binding.fragmentContainer.setVisibility(android.view.View.GONE);
                binding.helpRecyclerView.setVisibility(android.view.View.VISIBLE);
                String selected = tab.getText().toString();
                adapter.filterByCategory(selected); // Filter adapter
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        binding.imageBack.setOnClickListener(v -> {
            finish();
        });
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filterByKeyword(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filterByKeyword(newText);
                return true;
            }
        });


    }

    void addContent() {
        helpList.add(new HelpItem("How do I create a new account?", "1. Tap menu > Select Profile > Register...", "Account"));
        helpList.add(new HelpItem("How do I reset my password?", "Go to login screen and tap on 'Forgot password'...", "Account"));
        helpList.add(new HelpItem("How to start a group chat?", "Go to Chats tab and tap '+' to create a group.", "Chats"));
        helpList.add(new HelpItem("Can I change my username?", "Navigate to Profile > Edit Username", "General"));
        helpList.add(new HelpItem("How to block someone?", "Open chat > Tap menu > Block user.", "Chats"));
        helpList.add(new HelpItem("How to change profile picture?", "Go to Profile > Tap on photo > Choose new image.", "Account"));
        helpList.add(new HelpItem("How to delete a group?", "Open group > Tap menu > Delete group (only admins).", "Groups"));
        helpList.add(new HelpItem("How to leave a group chat?", "Open group > Tap menu > Leave group.", "Groups"));
        helpList.add(new HelpItem("How to report a user?", "Go to user profile > Tap menu > Report.", "General"));
        // Account management
        helpList.add(new HelpItem("How do I enable two-factor authentication?", "Go to Settings > Security > Two-factor authentication and follow prompts.", "Account"));
        helpList.add(new HelpItem("How to change my email address?", "Navigate to Settings > Account > Email > Edit email address.", "Account"));
        helpList.add(new HelpItem("Can I use multiple devices?", "Yes, go to Settings > Devices > Link new device and scan the QR code.", "Account"));
        helpList.add(new HelpItem("How to delete my account?", "Go to Settings > Account > Delete account. Note: this action cannot be undone.", "Account"));
        helpList.add(new HelpItem("How do I change notification settings?", "Navigate to Settings > Notifications to customize your preferences.", "Account"));

// Privacy & Security
        helpList.add(new HelpItem("How to enable message encryption?", "Go to Settings > Privacy > End-to-end encryption to enable secure messaging.", "Privacy"));
        helpList.add(new HelpItem("Who can see my online status?", "Navigate to Settings > Privacy > Last seen & online to manage visibility.", "Privacy"));
        helpList.add(new HelpItem("How to clear chat history?", "Open chat > Menu > Clear chat history > Confirm deletion.", "Privacy"));
        helpList.add(new HelpItem("Can I set messages to disappear?", "Yes, open chat > Menu > Disappearing messages > Set timer.", "Privacy"));
        helpList.add(new HelpItem("How to manage blocked users?", "Go to Settings > Privacy > Blocked users to view or unblock contacts.", "Privacy"));

// Messages & Media
        helpList.add(new HelpItem("How to send voice messages?", "In chat, tap and hold microphone icon, speak, then release to send.", "Messages"));
        helpList.add(new HelpItem("Can I edit messages after sending?", "Long press on a message > Select Edit (only available for 15 minutes after sending).", "Messages"));
        helpList.add(new HelpItem("How to forward messages?", "Long press message > Tap Forward > Select recipient.", "Messages"));
        helpList.add(new HelpItem("How to save media to my device?", "Tap on photo or video > Menu > Save to device.", "Media"));
        helpList.add(new HelpItem("How to send large files?", "Attach file > If over 100MB, app will use cloud transfer automatically.", "Media"));

// Group Features
        helpList.add(new HelpItem("How to add new members to a group?", "Open group > Menu > Group info > Add participants.", "Groups"));
        helpList.add(new HelpItem("How to create a group poll?", "In group chat > Menu > Create poll > Add options > Share.", "Groups"));
        helpList.add(new HelpItem("How to make someone a group admin?", "Group info > Tap on member > Make group admin.", "Groups"));
        helpList.add(new HelpItem("What's the maximum group size?", "Standard groups support up to 256 members, super groups up to 2000.", "Groups"));
        helpList.add(new HelpItem("How to pin messages in groups?", "Long press on message > Pin to group > Choose visibility options.", "Groups"));

// Calls & Video
        helpList.add(new HelpItem("How to make video calls?", "Open chat > Tap video icon in top right corner.", "Calls"));
        helpList.add(new HelpItem("Can I make group voice calls?", "Yes, open group > Menu > Group call > Select participants.", "Calls"));
        helpList.add(new HelpItem("How to share my screen?", "During video call > Menu > Share screen > Select window to share.", "Calls"));
        helpList.add(new HelpItem("How to mute myself during calls?", "Tap the microphone icon during an active call to toggle mute.", "Calls"));
        helpList.add(new HelpItem("Can I record calls?", "Call recording is not supported for privacy reasons.", "Calls"));

// Customization
        helpList.add(new HelpItem("How to change app theme?", "Go to Settings > Appearance > Theme > Select light, dark or system default.", "Customization"));
        helpList.add(new HelpItem("How to change chat wallpaper?", "Open chat > Menu > Wallpaper > Choose from gallery or presets.", "Customization"));
        helpList.add(new HelpItem("How to create custom stickers?", "Go to Stickers > Create new pack > Follow creation wizard.", "Customization"));
        helpList.add(new HelpItem("How to change text size?", "Settings > Appearance > Text size > Select preferred size.", "Customization"));
        helpList.add(new HelpItem("Can I customize notification sounds?", "Yes, go to Settings > Notifications > Sounds > Select contact or group.", "Customization"));

// Troubleshooting
        helpList.add(new HelpItem("App crashes frequently, what should I do?", "Try force closing the app, clearing cache, or reinstalling the latest version.", "Troubleshooting"));
        helpList.add(new HelpItem("Messages not sending, how to fix?", "Check your internet connection, or try restarting the app.", "Troubleshooting"));
        helpList.add(new HelpItem("Can't receive notifications?", "Check Settings > Notifications and ensure they're enabled. Also verify system settings.", "Troubleshooting"));
        helpList.add(new HelpItem("How to recover deleted messages?", "Messages can be recovered from backups. Go to Settings > Chats > Chat backup.", "Troubleshooting"));
        helpList.add(new HelpItem("App using too much battery?", "Check Settings > Battery optimization and exclude the app, or disable some notifications.", "Troubleshooting"));

// Payments & Business
        helpList.add(new HelpItem("How to send payments?", "Open chat > Menu > Payments > Enter amount > Confirm.", "Payments"));
        helpList.add(new HelpItem("Is payment information secure?", "Yes, all payment data is encrypted and not stored on our servers.", "Payments"));
        helpList.add(new HelpItem("How to create a business profile?", "Register at business.chatapp.com and follow the verification process.", "Business"));
        helpList.add(new HelpItem("Can I schedule messages?", "Yes, tap and hold send button > Schedule message > Set time and date.", "Business"));
        helpList.add(new HelpItem("How to set up auto-replies?", "Business accounts: Settings > Business tools > Auto-replies > Create new.", "Business"));
    }

    private void onHelpClicked(HelpItem item) {
        binding.helpRecyclerView.setVisibility(android.view.View.GONE);
        binding.fragmentContainer.setVisibility(android.view.View.VISIBLE);
        HelpDetailFragment fragment = HelpDetailFragment.newInstance(item.getTitle(), item.getContent());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            binding.fragmentContainer.setVisibility(android.view.View.GONE);
            binding.helpRecyclerView.setVisibility(android.view.View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }
}
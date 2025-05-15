package com.duc.chatting.call.views;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.duc.chatting.R;
import com.duc.chatting.call.models.DataModelType;
import com.duc.chatting.call.repository.MainRepository;
import com.duc.chatting.databinding.ActivityCallBinding;

public class CallActivity extends AppCompatActivity implements MainRepository.Listener{
    ActivityCallBinding binding;
    private MainRepository mainRepository;
    private Boolean isCameraMuted = false;
    private Boolean isMicrophoneMuted = false;
    private AlertDialog callingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String targetPhoneNumber = getIntent().getStringExtra("Target");
        init(targetPhoneNumber);

    }
    private void showCallingDialog(String target) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Calling...");
        builder.setMessage("Waiting for " + target + " to accept...");
        builder.setCancelable(false);
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            mainRepository.endCall(); // Cancel the call
            dialog.dismiss();
        });

        callingDialog = builder.create();
        callingDialog.show();
    }
    private void init(String phoneNumber){
        mainRepository = MainRepository.getInstance();
        mainRepository.sendCallRequest(phoneNumber,()->{
            Toast.makeText(this, "couldnt find the target", Toast.LENGTH_SHORT).show();
            if (callingDialog != null) callingDialog.dismiss();
        });
        showCallingDialog(phoneNumber);
        mainRepository.initLocalView(binding.localView);
        mainRepository.initRemoteView(binding.remoteView);
        mainRepository.subscribeForLatestEvent(data->{
            if (data.getType()== DataModelType.StartCall){
                runOnUiThread(()->{
                    binding.incomingNameTV.setText(data.getSender()+" is Calling you");
                    binding.incomingCallLayout.setVisibility(View.VISIBLE);
                    binding.acceptButton.setOnClickListener(v->{
                        //star the call here
                        mainRepository.startCall(data.getSender());
                        binding.incomingCallLayout.setVisibility(View.GONE);
                    });
                    binding.rejectButton.setOnClickListener(v->{
                        binding.incomingCallLayout.setVisibility(View.GONE);
                    });
                });
            }
        });
        binding.switchCameraButton.setOnClickListener(v->{
            mainRepository.switchCamera();
        });
        binding.micButton.setOnClickListener(v->{
            if (isMicrophoneMuted){
                binding.micButton.setImageResource(R.drawable.ic_baseline_mic_off_24);
            }else {
                binding.micButton.setImageResource(R.drawable.ic_baseline_mic_24);
            }
            mainRepository.toggleAudio(isMicrophoneMuted);
            isMicrophoneMuted=!isMicrophoneMuted;
        });

        binding.videoButton.setOnClickListener(v->{
            if (isCameraMuted){
                binding.videoButton.setImageResource(R.drawable.ic_baseline_videocam_off_24);
            }else {
                binding.videoButton.setImageResource(R.drawable.ic_baseline_videocam_24);
            }
            mainRepository.toggleVideo(isCameraMuted);
            isCameraMuted=!isCameraMuted;
        });

        binding.endCallButton.setOnClickListener(v->{
            mainRepository.endCall();
            finish();
        });
    }

    @Override
    public void webrtcConnected() {
        runOnUiThread(()->{
            binding.incomingCallLayout.setVisibility(View.GONE);
            if (callingDialog != null) callingDialog.dismiss();
//            binding.whoToCallLayout.setVisibility(View.GONE);
            binding.callLayout.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void webrtcClosed() {
        runOnUiThread(this::finish);
    }
}
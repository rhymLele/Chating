package com.duc.chatting.call.views;

import android.os.Bundle;
import android.view.View;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();

    }
    private void init(){
        mainRepository = MainRepository.getInstance();
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
//            binding.whoToCallLayout.setVisibility(View.GONE);
            binding.callLayout.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void webrtcClosed() {
        runOnUiThread(this::finish);
    }
}
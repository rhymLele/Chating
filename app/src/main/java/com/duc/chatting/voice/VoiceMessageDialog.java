package com.duc.chatting.voice;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.duc.chatting.R;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class VoiceMessageDialog extends Dialog {

    private MediaRecorder recorder;
    private String filePath;
    private Button btnStart, btnStop;
    private TextView statusText;
    private OnRecordingFinished listener;
    private Handler timerHandler = new Handler();
    private int seconds = 0;
    private boolean isRecording = false;
    public interface OnRecordingFinished {
        void onFinished(String filePath);
    }

    public VoiceMessageDialog(Context context, OnRecordingFinished listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_voice_message);

        btnStart = findViewById(R.id.btnRecord);
        btnStop = findViewById(R.id.btnStop);
        statusText = findViewById(R.id.txtStatus);

        btnStart.setOnClickListener(v -> startRecording());
        btnStop.setOnClickListener(v -> stopRecording());
    }

    private void startRecording() {
        File outputDir = getContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        filePath = outputDir.getAbsolutePath() + "/voice_" + System.currentTimeMillis() + ".3gp";

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(filePath);

        try {
            recorder.prepare();
            recorder.start();
            isRecording = true;
            seconds = 0;
            timerHandler.postDelayed(timerRunnable, 1000);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        if (recorder != null) {
            isRecording = false;
            timerHandler.removeCallbacks(timerRunnable);
            recorder.stop();
            recorder.release();
            recorder = null;

            String durationText = String.format(Locale.getDefault(), "✅ Ghi xong (%02d:%02d)", seconds / 60, seconds % 60);
            statusText.setText(durationText);
            listener.onFinished(filePath);
            dismiss();
        }
    }
    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (isRecording) {
                seconds++;
                String time = String.format(Locale.getDefault(), "🎙️ Đang ghi (%02d:%02d)", seconds / 60, seconds % 60);
                statusText.setText(time);
                timerHandler.postDelayed(this, 1000);
            }
        }
    };
}

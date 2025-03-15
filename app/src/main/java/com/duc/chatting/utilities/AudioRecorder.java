package com.duc.chatting.utilities;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import com.duc.chatting.base.Resource;

import java.io.File;
import java.io.IOException;



public class AudioRecorder {
    public MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String fileName;
    private File audioFile;
    private Context context;
    private final Resource<String> listener;

    public AudioRecorder(Context context, Resource<String> listener) {
        this.context = context;
        this.listener = listener;
    }

    public void startRecording() {
        try {
            fileName = "audio_" + System.currentTimeMillis() + ".mp3";
            audioFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), fileName);
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mediaRecorder.setOutputFile(audioFile);
            } else {
                mediaRecorder.setOutputFile(fileName);
            }
            mediaRecorder.prepare();
            mediaRecorder.start();
            listener.onSuccess("go");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecord() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }


    public Uri stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }

        return Uri.fromFile(audioFile);
    }

    public void startPlaying() {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(fileName);
            mediaPlayer.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopPlaying() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }


}

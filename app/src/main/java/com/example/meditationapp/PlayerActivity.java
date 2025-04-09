package com.example.meditationapp;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class PlayerActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private ImageView playPauseBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        mediaPlayer = MediaPlayer.create(this, R.raw.meditation101);
        playPauseBtn = findViewById(R.id.playPauseButton);

        // Play by default
        mediaPlayer.start();
        isPlaying = true;
        playPauseBtn.setImageResource(R.drawable.ic_pause_circle);

        // Toggle play/pause
        playPauseBtn.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                if (isPlaying) {
                    mediaPlayer.pause();
                    playPauseBtn.setImageResource(R.drawable.ic_play_circle);
                } else {
                    mediaPlayer.start();
                    playPauseBtn.setImageResource(R.drawable.ic_pause_circle);
                }
                isPlaying = !isPlaying;
            }
        });

        // Back arrow closes activity
        findViewById(R.id.backButton).setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        super.onDestroy();
    }
}

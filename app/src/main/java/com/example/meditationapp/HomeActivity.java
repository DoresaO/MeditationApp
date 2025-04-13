package com.example.meditationapp;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
            return;
        }

        TextView welcomeText = findViewById(R.id.welcomeText);
        TextView greetingText = findViewById(R.id.greetingText);
        ImageView profileIcon = findViewById(R.id.profileIcon);

        String name = currentUser.getDisplayName();
        welcomeText.setText("Welcome back, " + (name != null ? name : "User"));
        Log.d("FIREBASE_UID", "Current user ID: " + currentUser.getUid());

        String timeGreeting = getTimeGreeting();
        greetingText.setText("How are you feeling this " + timeGreeting + "?");

        profileIcon.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, ProfileActivity.class)));

        // Mood images + animation + Firebase saving
        setupMoodImage(R.id.moodCalm, "Calm");
        setupMoodImage(R.id.moodRelax, "Relax");
        setupMoodImage(R.id.moodFocus, "Focus");
        setupMoodImage(R.id.moodAnxious, "Anxious");

        Button meditationPlay = findViewById(R.id.playButton);
        meditationPlay.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, PlayerActivity.class)));

        Button cardioPlay = findViewById(R.id.playButton1);
        cardioPlay.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, PlayerActivity.class)));

    }

    private void setupMoodImage(int viewId, String mood) {
        ImageView moodView = findViewById(viewId);
        moodView.setOnClickListener(v -> {
            // Play animation
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.mood_pop);
            v.startAnimation(anim);

            // Save mood to Firestore
            saveMood(mood);
        });
    }

    private void saveMood(String mood) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        Map<String, Object> moodData = new HashMap<>();
        moodData.put("mood", mood);
        moodData.put("timestamp", System.currentTimeMillis());

        db.collection("users")
                .document(uid)
                .collection("moods")
                .document(date)
                .set(moodData)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Mood saved: " + mood, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(exc ->
                        Toast.makeText(this, "Failed to save mood", Toast.LENGTH_SHORT).show()
                );
    }

    private String getTimeGreeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour >= 5 && hour < 12) {
            return "morning";
        } else if (hour >= 12 && hour < 17) {
            return "afternoon";
        } else if (hour >= 17 && hour < 21) {
            return "evening";
        } else {
            return "night";
        }
    }
}


package com.example.meditationapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import android.os.Build;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup;
import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private LinearLayout moodHistoryLayout;
    private TextView moodInsightText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        TextView nameText = findViewById(R.id.profileName);
        TextView emailText = findViewById(R.id.profileEmail);
        ImageView profileImage = findViewById(R.id.profileImage);
        Button logoutBtn = findViewById(R.id.logoutButton);
        moodHistoryLayout = findViewById(R.id.moodHistoryLayout);
        moodInsightText = findViewById(R.id.moodInsightText);

        nameText.setText(user.getDisplayName());
        emailText.setText(user.getEmail());
        profileImage.setImageResource(R.drawable.profile);

        logoutBtn.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        });

        loadMoodHistory(user.getUid());

        RadioGroup notificationRadioGroup = findViewById(R.id.notificationRadioGroup);

        notificationRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // Schedule the reminder 1 minute from now
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 1); // ⏱ trigger in 1 minute
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            NotificationHelper.scheduleReminder(this, calendar.getTimeInMillis());
            Toast.makeText(this, "Test reminder set for 1 minute from now", Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    @SuppressLint("SetTextI18n")
    private void loadMoodHistory(String uid) {
        db.collection("users")
                .document(uid)
                .collection("moods")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    Map<String, Integer> moodCount = new HashMap<>();
                    int count = 0;

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        String mood = doc.getString("mood");
                        Long timestamp = doc.getLong("timestamp");

                        if (mood != null && timestamp != null) {
                            // Count the mood for insights
                            if (moodCount.containsKey(mood)) {
                                moodCount.put(mood, moodCount.get(mood) + 1);
                            } else {
                                moodCount.put(mood, 1);
                            }

                            if (count < 4) {
                                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                        .format(new Date(timestamp));

                                String emoji;
                                int colorRes;
                                switch (mood.toLowerCase()) {
                                    case "calm":
                                        emoji = "😌";
                                        colorRes = R.color.calm_mood_color;
                                        break;
                                    case "relax":
                                        emoji = "🌿";
                                        colorRes = android.R.color.holo_green_light;
                                        break;
                                    case "focus":
                                        emoji = "🎯";
                                        colorRes = android.R.color.holo_blue_light;
                                        break;
                                    case "anxious":
                                        emoji = "😟";
                                        colorRes = android.R.color.holo_red_light;
                                        break;
                                    default:
                                        emoji = "🌀";
                                        colorRes = android.R.color.white;
                                        break;
                                }

                                LinearLayout row = new LinearLayout(this);
                                row.setOrientation(LinearLayout.HORIZONTAL);
                                row.setLayoutParams(new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT));
                                row.setPadding(0, 8, 0, 8);

                                TextView dateText = new TextView(this);
                                dateText.setText("📅 " + date);
                                dateText.setLayoutParams(new LinearLayout.LayoutParams(0,
                                        LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                                dateText.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                                dateText.setTextSize(16f);

                                TextView moodText = new TextView(this);
                                moodText.setText(emoji + " " + mood);
                                moodText.setLayoutParams(new LinearLayout.LayoutParams(0,
                                        LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                                moodText.setTextColor(ContextCompat.getColor(this, colorRes));
                                moodText.setTextSize(16f);
                                moodText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);

                                row.addView(dateText);
                                row.addView(moodText);
                                moodHistoryLayout.addView(row);

                                count++;
                            }
                        }
                    }

                    if (!moodCount.isEmpty()) {
                        String topMood = null;
                        int maxCount = 0;
                        for (Map.Entry<String, Integer> entry : moodCount.entrySet()) {
                            if (entry.getValue() > maxCount) {
                                maxCount = entry.getValue();
                                topMood = entry.getKey();
                            }
                        }

                        if (topMood != null) {
                            String emoji;
                            switch (topMood.toLowerCase()) {
                                case "calm": emoji = "😌"; break;
                                case "relax": emoji = "🌿"; break;
                                case "focus": emoji = "🎯"; break;
                                case "anxious": emoji = "😟"; break;
                                default: emoji = "🌀"; break;
                            }

                            moodInsightText.setText("🧠 You’ve felt " + topMood + " most this week. Keep it up " + emoji);
                            moodInsightText.setVisibility(View.VISIBLE);
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load mood history", Toast.LENGTH_SHORT).show());
    }
}

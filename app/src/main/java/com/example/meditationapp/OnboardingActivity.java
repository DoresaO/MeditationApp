package com.example.meditationapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class OnboardingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_onboarding);

        Button loginButton = findViewById(R.id.loginButton);
        TextView signupText = findViewById(R.id.signupText);

        loginButton.setOnClickListener(view -> {
            Intent intent = new Intent(OnboardingActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        signupText.setOnClickListener(view -> {
            Intent intent = new Intent(OnboardingActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }
}

package com.acrenex.app.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.acrenex.app.R;
import com.google.android.material.button.MaterialButton;

public class VerificationSuccessActivity extends AppCompatActivity {

    private MaterialButton btnContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_verification_success);

        btnContinue = findViewById(R.id.btnContinue);

        btnContinue.setOnClickListener(v -> {

            Intent intent = new Intent(
                    VerificationSuccessActivity.this,
                    CitizenDashboardActivity.class
            );

            startActivity(intent);
            finish();
        });
    }
}
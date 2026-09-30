package com.acrenex.app.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.acrenex.app.R;
import com.acrenex.app.database.UserRepository;
import com.acrenex.app.firebase.FirebaseBackend;
import com.acrenex.app.security.SecurityUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ForgotPasswordActivity extends AppCompatActivity {
    private static final String PREFS = "acrenex_password_reset";
    private static final long OTP_WINDOW_MS = 5 * 60 * 1000L;
    private TextInputEditText etIdentifier, etOtp, etNewPassword, etConfirmPassword;
    private TextView tvStep, tvOtpHint;
    private MaterialButton btnPrimary;
    private View otpBlock, passwordBlock;
    private String userId;
    private String expectedOtp;
    private long otpExpiresAt;
    private int step = 1;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_forgot_password);
        etIdentifier = findViewById(R.id.etResetIdentifier);
        etOtp = findViewById(R.id.etResetOtp);
        etNewPassword = findViewById(R.id.etResetNewPassword);
        etConfirmPassword = findViewById(R.id.etResetConfirmPassword);
        tvStep = findViewById(R.id.tvResetStep);
        tvOtpHint = findViewById(R.id.tvDemoOtp);
        btnPrimary = findViewById(R.id.btnResetPrimary);
        otpBlock = findViewById(R.id.otpBlock);
        passwordBlock = findViewById(R.id.passwordBlock);
        btnPrimary.setOnClickListener(v -> next());
        findViewById(R.id.tvBackToLogin).setOnClickListener(v -> finish());
        render();
    }

    private void next() {
        if (step == 1) requestOtp();
        else if (step == 2) verifyOtp();
        else resetPassword();
    }

    private void requestOtp() {
        String identifier = text(etIdentifier);
        if (identifier.isEmpty()) { etIdentifier.setError("Enter your registered email or mobile"); return; }
        boolean email = Patterns.EMAIL_ADDRESS.matcher(identifier).matches();
        boolean mobile = identifier.matches("\\d{10}");
        if (!email && !mobile) { etIdentifier.setError("Enter a valid registered email or 10-digit mobile"); return; }

        FirebaseBackend firebase = FirebaseBackend.get(this);
        if (firebase.isConfigured() && email) {
            firebase.sendPasswordReset(identifier, task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Password reset email sent. Open the verified email link to create your new password.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    String message = task.getException() == null ? "Could not send reset email." : task.getException().getMessage();
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                }
            });
            return;
        }
        UserRepository repo = new UserRepository(this);
        android.database.Cursor c = null;
        try {
            c = repo.findByEmailOrMobile(identifier);
            if (!c.moveToFirst()) {
                Toast.makeText(this, "No account found for this email. Nothing was created.", Toast.LENGTH_LONG).show();
                return;
            }
            userId = c.getString(c.getColumnIndexOrThrow("id"));
        } finally { if (c != null) c.close(); repo.close(); }

        expectedOtp = SecurityUtils.generateOtp();
        otpExpiresAt = System.currentTimeMillis() + OTP_WINDOW_MS;
        getSharedPreferences(PREFS, MODE_PRIVATE).edit()
                .putString("user_id", userId).putString("otp", expectedOtp).putLong("expires", otpExpiresAt).apply();
        step = 2;
        render();
        // Prototype only: production must deliver this OTP through a verified backend SMS/email provider.
        tvOtpHint.setText("Prototype OTP: " + expectedOtp + "  •  valid for 5 minutes");
    }

    private void verifyOtp() {
        String entered = text(etOtp);
        if (entered.length() != 6) { etOtp.setError("Enter the 6-digit OTP"); return; }
        SharedPreferences p = getSharedPreferences(PREFS, MODE_PRIVATE);
        String saved = p.getString("otp", expectedOtp == null ? "" : expectedOtp);
        long expiry = p.getLong("expires", otpExpiresAt);
        if (System.currentTimeMillis() > expiry) { Toast.makeText(this, "OTP expired. Request a new OTP.", Toast.LENGTH_LONG).show(); step = 1; render(); return; }
        if (!entered.equals(saved)) { etOtp.setError("Invalid OTP"); return; }
        userId = p.getString("user_id", userId);
        step = 3;
        render();
    }

    private void resetPassword() {
        String password = text(etNewPassword), confirm = text(etConfirmPassword);
        if (password.length() < 6) { etNewPassword.setError("Use at least 6 characters"); return; }
        if (!password.equals(confirm)) { etConfirmPassword.setError("Passwords do not match"); return; }
        UserRepository repo = new UserRepository(this);
        boolean updated = repo.updatePassword(userId, password);
        repo.close();
        if (!updated) { Toast.makeText(this, "Password could not be updated.", Toast.LENGTH_LONG).show(); return; }
        getSharedPreferences(PREFS, MODE_PRIVATE).edit().clear().apply();
        Toast.makeText(this, "Password reset successfully. Please sign in.", Toast.LENGTH_LONG).show();
        finish();
    }

    private void render() {
        tvStep.setText(step == 1 ? "STEP 1 OF 3  •  FIND ACCOUNT" : step == 2 ? "STEP 2 OF 3  •  VERIFY OTP" : "STEP 3 OF 3  •  CREATE NEW PASSWORD");
        etIdentifier.setVisibility(step == 1 ? View.VISIBLE : View.GONE);
        otpBlock.setVisibility(step == 2 ? View.VISIBLE : View.GONE);
        passwordBlock.setVisibility(step == 3 ? View.VISIBLE : View.GONE);
        btnPrimary.setText(step == 1 ? "Send OTP" : step == 2 ? "Verify OTP" : "Reset password");
        if (step != 2) tvOtpHint.setVisibility(View.GONE); else tvOtpHint.setVisibility(View.VISIBLE);
    }

    private String text(TextInputEditText e) { return e.getText() == null ? "" : e.getText().toString().trim(); }
}

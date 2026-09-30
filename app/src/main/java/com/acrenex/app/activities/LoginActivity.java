package com.acrenex.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.acrenex.app.R;
import com.acrenex.app.database.DatabaseManager;
import com.acrenex.app.database.DemoDataSeeder;
import com.acrenex.app.database.UserRepository;
import com.acrenex.app.firebase.FirebaseBackend;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.acrenex.app.security.AuthManager;
import com.acrenex.app.security.SecurityUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Single-account login. The role is read only from the stored account record.
 * No role picker and no officer account creation are exposed to users.
 */
public class LoginActivity extends AppCompatActivity {
    private TextInputEditText etEmail, etPassword;
    private AuthManager authManager;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        authManager = new AuthManager(this);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        MaterialButton btnLogin = findViewById(R.id.btnLogin);
        TextView tvRegister = findViewById(R.id.tvRegister);
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);
        TextView tvOfficerRegister = findViewById(R.id.tvOfficerRegister);

        btnLogin.setOnClickListener(v -> login());
        tvRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
        tvForgotPassword.setOnClickListener(v -> startActivity(new Intent(this, ForgotPasswordActivity.class)));
        tvOfficerRegister.setOnClickListener(v -> startActivity(new Intent(this, OfficerRegisterActivity.class)));
    }

    private void login() {
        String email = text(etEmail);
        String password = text(etPassword);
        if (email.isEmpty()) { etEmail.setError("Enter your email"); etEmail.requestFocus(); return; }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { etEmail.setError("Enter a valid email"); etEmail.requestFocus(); return; }
        if (password.length() < 6) { etPassword.setError("Use at least 6 characters"); etPassword.requestFocus(); return; }

        FirebaseBackend firebase = FirebaseBackend.get(this);
        if (firebase.isConfigured()) {
            firebase.signIn(email, password, task -> {
                if (!task.isSuccessful() || task.getResult() == null || task.getResult().getUser() == null) {
                    String message = task.getException() == null ? "Firebase login failed." : task.getException().getMessage();
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                    return;
                }
                FirebaseUser cloudUser = task.getResult().getUser();
                firebase.getRoleAndProfile(cloudUser.getUid(), profileTask -> {
                    String role = "Citizen";
                    String name = cloudUser.getEmail() == null ? "AcreNex Citizen" : cloudUser.getEmail();
                    if (profileTask.isSuccessful() && profileTask.getResult() != null && profileTask.getResult().exists()) {
                        DocumentSnapshot doc = profileTask.getResult();
                        String cloudRole = doc.getString("role");
                        if (cloudRole != null && !cloudRole.trim().isEmpty()) role = cloudRole;
                        String cloudName = doc.getString("name");
                        if (cloudName != null && !cloudName.trim().isEmpty()) name = cloudName;
                    }
                    // Never trust a role supplied by the login UI. The role comes only from Firestore.
                    ensureLocalMirror(cloudUser.getUid(), name, email, role);
                    authManager.loginDemo(email, password, role, cloudUser.getUid());
                    firebase.syncSessionProfile(cloudUser.getUid(), email, role);
                    seedForRole(cloudUser.getUid(), role);
                    logLogin(cloudUser.getUid(), role);
                    routeAfterLogin(role);
                });
            });
            return;
        }

        UserRepository repo = new UserRepository(this);
        UserRepository.LoginResult result = repo.authenticateExisting(email, password);
        repo.close();
        if (!result.success) {
            Toast.makeText(this, result.message, Toast.LENGTH_LONG).show();
            return;
        }
        String role = result.role == null || result.role.trim().isEmpty() ? "Citizen" : result.role;
        authManager.loginDemo(email, password, role, result.userId);
        seedForRole(result.userId, role);
        logLogin(result.userId, role);
        routeAfterLogin(role);
    }

    private void routeAfterLogin(String role) {
        Toast.makeText(this, "Login successful.", Toast.LENGTH_SHORT).show();
        if ("Citizen".equalsIgnoreCase(role)) startActivity(new Intent(this, CitizenDashboardActivity.class));
        else openOfficer(role);
        finish();
    }

    private void ensureLocalMirror(String uid, String name, String email, String role) {
        UserRepository repo = new UserRepository(this);
        android.database.Cursor c = null;
        try {
            c = repo.findByEmail(email);
            if (!c.moveToFirst()) {
                repo.saveUserWithPassword(uid, name, email, "", role, "Firebase", SecurityUtils.generateSessionId(), false);
            }
        } finally {
            if (c != null) c.close();
            repo.close();
        }
    }

    private void seedForRole(String userId, String role) {
        String owner = findUserName(userId);
        if (owner == null || owner.isEmpty()) owner = "AcreNex Citizen";
        DatabaseManager manager = new DatabaseManager(this);
        try {
            android.database.Cursor c = manager.getDatabase().rawQuery(
                    "SELECT COUNT(*) FROM parcels WHERE owner_name=?", new String[]{owner});
            boolean exists = c.moveToFirst() && c.getInt(0) > 0;
            c.close();
            if (!exists && "Citizen".equalsIgnoreCase(role)) {
                manager.insertParcel("24-GJ-GN-0001-00001", "GN-101", "Gujarat", "Gandhinagar", "Gandhinagar", "Demo Village", owner,
                        "2.50", "Acres", "Residential", "Residential", "R1", "Verified", "Clear", "Paid", "Approved", 23.2156, 72.6369);
                manager.getDatabase().execSQL(
                        "INSERT OR REPLACE INTO ownership_records(id,ulpin,owner_name,ownership_type,share,record_date,source,verification_status) VALUES(?,?,?,?,?,?,?,?)",
                        new Object[]{"OWN-0001", "24-GJ-GN-0001-00001", owner, "Freehold", "100%", now(), "Synthetic RoR dataset", "VERIFIED"});
            }
        } finally { manager.close(); }
        DemoDataSeeder.seedExtended(this, userId, owner);
    }

    private void logLogin(String userId, String role) {
        DatabaseManager db = new DatabaseManager(this);
        db.addAuditLog(userId, role, "LOGIN", "USER", userId, now(), "SUCCESS");
        db.close();
    }

    private void openOfficer(String role) {
        Class<?> cls = OfficerDashboardActivity.class;
        if ("Revenue Officer".equals(role)) cls = RevenueOfficerDashboardActivity.class;
        else if ("Registration Officer".equals(role)) cls = RegistrationOfficerDashboardActivity.class;
        else if ("Survey / GIS Officer".equals(role)) cls = SurveyGISOfficerDashboardActivity.class;
        else if ("Municipal / Planning Officer".equals(role)) cls = PlanningOfficerDashboardActivity.class;
        else if ("Property Tax Officer".equals(role)) cls = TaxOfficerDashboardActivity.class;
        else if ("Dispute / Case Officer".equals(role)) cls = DisputeOfficerDashboardActivity.class;
        else if ("Environment / Restriction Officer".equals(role)) cls = EnvironmentOfficerDashboardActivity.class;
        else if ("Super Admin / Command Centre".equals(role)) cls = SuperAdminDashboardActivity.class;
        startActivity(new Intent(this, cls));
    }

    private String findUserName(String id) {
        DatabaseManager db = new DatabaseManager(this);
        android.database.Cursor c = null;
        try {
            c = db.getDatabase().query("users", new String[]{"name"}, "id=?", new String[]{id}, null, null, null);
            return c.moveToFirst() ? c.getString(0) : "";
        } finally { if (c != null) c.close(); db.close(); }
    }

    private String now() { return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date()); }
    private String text(TextInputEditText e) { return e.getText() == null ? "" : e.getText().toString().trim(); }
}

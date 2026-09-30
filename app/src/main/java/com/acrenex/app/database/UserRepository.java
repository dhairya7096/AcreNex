package com.acrenex.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.acrenex.app.security.SecurityUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UserRepository {
    private final DatabaseManager databaseManager;

    public UserRepository(Context context) { databaseManager = new DatabaseManager(context); }

    public boolean saveUser(String id, String name, String email, String mobile, String role, boolean proofVerified) {
        return saveUserWithPassword(id, name, email, mobile, role, departmentForRole(role), "", proofVerified);
    }

    public boolean saveUserWithPassword(String id, String name, String email, String mobile, String role,
                                        String department, String password, boolean proofVerified) {
        ContentValues values = new ContentValues();
        String now = now();
        values.put("id", id);
        values.put("name", name);
        values.put("email", email);
        values.put("mobile", mobile == null ? "" : mobile);
        values.put("role", role);
        values.put("user_type", isCitizen(role) ? "CITIZEN" : "OFFICER");
        values.put("department", department == null ? "" : department);
        values.put("proof_verified", proofVerified ? 1 : 0);
        values.put("created_at", now);
        if (password != null && !password.isEmpty()) {
            String salt = SecurityUtils.generatePasswordSalt();
            values.put("password_salt", salt);
            values.put("password_hash", SecurityUtils.hashPassword(password, salt));
        }
        long result = databaseManager.getDatabase().insertWithOnConflict("users", null, values, SQLiteDatabase.CONFLICT_REPLACE);
        return result != -1;
    }

    /** Authenticate an account that was previously provisioned/registered. */
    public LoginResult authenticateExisting(String email, String password) {
        Cursor c = null;
        try {
            c = findByEmail(email);
            if (!c.moveToFirst()) {
                return new LoginResult(false, "", "No AcreNex account was found for this email. Create a citizen account or use your assigned officer credentials.", "");
            }
            String id = value(c, "id");
            String storedHash = value(c, "password_hash");
            String salt = value(c, "password_salt");
            if (storedHash.isEmpty() || salt.isEmpty()) {
                return new LoginResult(false, id, "This account needs a password reset before it can sign in.", value(c, "role"));
            }
            if (!SecurityUtils.verifyPassword(password, salt, storedHash)) {
                return new LoginResult(false, id, "Incorrect password for this account.", value(c, "role"));
            }
            ContentValues update = new ContentValues();
            update.put("last_login", now());
            databaseManager.getDatabase().update("users", update, "id=?", new String[]{id});
            String existingRole = value(c, "role");
            if (existingRole.isEmpty()) existingRole = "Citizen";
            return new LoginResult(true, id, "Login successful.", existingRole);
        } finally { if (c != null) c.close(); }
    }

    /** Reset an existing account password after the OTP flow has verified ownership. */
    public boolean updatePassword(String userId, String newPassword) {
        if (userId == null || userId.trim().isEmpty() || newPassword == null || newPassword.length() < 6) return false;
        String salt = SecurityUtils.generatePasswordSalt();
        ContentValues v = new ContentValues();
        v.put("password_salt", salt);
        v.put("password_hash", SecurityUtils.hashPassword(newPassword, salt));
        return databaseManager.getDatabase().update("users", v, "id=?", new String[]{userId}) == 1;
    }

    public void updateRole(String userId, String role) {
        ContentValues v = new ContentValues();
        v.put("role", role); v.put("user_type", isCitizen(role) ? "CITIZEN" : "OFFICER"); v.put("department", departmentForRole(role));
        databaseManager.getDatabase().update("users", v, "id=?", new String[]{userId});
    }

    public Cursor findUser(String id) { return databaseManager.getDatabase().query("users", null, "id = ?", new String[]{id}, null, null, null); }
    public Cursor findByEmail(String email) { return databaseManager.getDatabase().query("users", null, "email = ?", new String[]{email}, null, null, null); }
    public Cursor findByEmailOrMobile(String identifier) { return databaseManager.getDatabase().query("users", null, "email = ? OR mobile = ?", new String[]{identifier, identifier}, null, null, null); }
    public void close() { databaseManager.close(); }

    private String value(Cursor c, String column) { int i=c.getColumnIndex(column); return i>=0 && !c.isNull(i) ? c.getString(i) : ""; }
    private String now() { return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date()); }
    private boolean isCitizen(String role) { return "Citizen".equals(role); }

    public static String departmentForRole(String role) {
        if (role == null) return "Citizen Services";
        if (role.contains("Revenue")) return "Revenue / Land Records";
        if (role.contains("Registration")) return "Registration Department";
        if (role.contains("Survey")) return "Survey & GIS";
        if (role.contains("Planning")) return "Municipal / Planning";
        if (role.contains("Tax")) return "Property Tax";
        if (role.contains("Dispute")) return "Dispute & Case Management";
        if (role.contains("Environment")) return "Environment & Restrictions";
        if (role.contains("Super Admin")) return "Land Stack Command Centre";
        return "Citizen Services";
    }

    private String displayNameFromEmail(String email) {
        String local = email == null ? "" : email.split("@")[0].replace('.', ' ').replace('_',' ').replace('-',' ').trim();
        if (local.isEmpty()) return "AcreNex User";
        StringBuilder b = new StringBuilder();
        for (String p : local.split("\\s+")) if (!p.isEmpty()) b.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1)).append(' ');
        return b.toString().trim();
    }

    public static class LoginResult {
        public final boolean success; public final String userId; public final String message; public final String role;
        public LoginResult(boolean success, String userId, String message, String role) { this.success=success; this.userId=userId; this.message=message; this.role=role; }
    }
}

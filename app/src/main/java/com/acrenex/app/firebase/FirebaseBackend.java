package com.acrenex.app.firebase;

import android.content.Context;
import android.text.TextUtils;

import com.acrenex.app.BuildConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

/** Firebase Authentication + Firestore + Storage configuration bridge for AcreNex. */
public final class FirebaseBackend {
    private static FirebaseBackend instance;
    private final FirebaseAuth auth;
    private final FirebaseFirestore firestore;
    private final boolean configured;

    private FirebaseBackend(Context context) {
        boolean ready = isConfigPresent();
        FirebaseAuth a = null;
        FirebaseFirestore f = null;
        if (ready) {
            try {
                if (FirebaseApp.getApps(context).isEmpty()) {
                    FirebaseOptions options = new FirebaseOptions.Builder()
                            .setApiKey(BuildConfig.FIREBASE_API_KEY)
                            .setApplicationId(BuildConfig.FIREBASE_APP_ID)
                            .setProjectId(BuildConfig.FIREBASE_PROJECT_ID)
                            .setStorageBucket(BuildConfig.FIREBASE_STORAGE_BUCKET)
                            .build();
                    FirebaseApp.initializeApp(context, options);
                }
                a = FirebaseAuth.getInstance();
                f = FirebaseFirestore.getInstance();
            } catch (Exception ignored) {
                ready = false;
            }
        }
        configured = ready && a != null && f != null;
        auth = a;
        firestore = f;
    }

    public static synchronized FirebaseBackend get(Context context) {
        if (instance == null) instance = new FirebaseBackend(context.getApplicationContext());
        return instance;
    }

    public boolean isConfigured() { return configured; }

    private boolean isConfigPresent() {
        return usable(BuildConfig.FIREBASE_API_KEY)
                && usable(BuildConfig.FIREBASE_APP_ID)
                && usable(BuildConfig.FIREBASE_PROJECT_ID);
    }

    private boolean usable(String value) {
        return !TextUtils.isEmpty(value) && !value.startsWith("REPLACE_WITH_");
    }

    public void signIn(String email, String password, OnCompleteListener<AuthResult> listener) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(listener);
    }

    public void createCitizen(String name, String email, String mobile, String password,
                              OnCompleteListener<AuthResult> listener) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().getUser() != null) {
                FirebaseUser user = task.getResult().getUser();
                Map<String, Object> profile = new HashMap<>();
                profile.put("uid", user.getUid());
                profile.put("name", name);
                profile.put("email", email);
                profile.put("mobile", mobile);
                profile.put("role", "Citizen");
                profile.put("status", "ACTIVE");
                profile.put("createdAt", FieldValue.serverTimestamp());
                firestore.collection("users").document(user.getUid()).set(profile)
                        .addOnCompleteListener(writeTask -> listener.onComplete(task));
            } else {
                listener.onComplete(task);
            }
        });
    }

    public void createOfficer(String name, String employeeId, String email, String mobile, String password, String role,
                              OnCompleteListener<AuthResult> listener) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().getUser() != null) {
                FirebaseUser user = task.getResult().getUser();
                Map<String, Object> profile = new HashMap<>();
                profile.put("uid", user.getUid());
                profile.put("name", name);
                profile.put("employeeId", employeeId);
                profile.put("email", email);
                profile.put("mobile", mobile);
                profile.put("role", role);
                profile.put("accountType", "OFFICER");
                profile.put("status", "ACTIVE_DEMO");
                profile.put("department", role);
                profile.put("createdAt", FieldValue.serverTimestamp());
                firestore.collection("users").document(user.getUid()).set(profile)
                        .addOnCompleteListener(writeTask -> listener.onComplete(task));
            } else {
                listener.onComplete(task);
            }
        });
    }

    public void sendPasswordReset(String email, OnCompleteListener<Void> listener) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener(listener);
    }

    public void getRoleAndProfile(String uid, com.google.android.gms.tasks.OnCompleteListener<DocumentSnapshot> listener) {
        if (!configured || TextUtils.isEmpty(uid)) return;
        firestore.collection("users").document(uid).get().addOnCompleteListener(listener);
    }

    public void syncSessionProfile(String uid, String email, String role) {
        if (!configured || TextUtils.isEmpty(uid)) return;
        Map<String, Object> update = new HashMap<>();
        update.put("uid", uid);
        update.put("email", email);
        update.put("role", role);
        update.put("lastLoginAt", FieldValue.serverTimestamp());
        firestore.collection("users").document(uid).set(update, SetOptions.merge());
    }

    public FirebaseUser currentUser() { return configured ? auth.getCurrentUser() : null; }
    public void signOut() { if (configured) auth.signOut(); }
}

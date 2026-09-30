package com.acrenex.app.security;

import android.content.Context;
import android.content.SharedPreferences;

public class SecureStorage {

    private static final String PREF_NAME =
            "acrenex_session";

    private static final String KEY_TOKEN =
            "access_token";

    private static final String KEY_ROLE =
            "user_role";

    private static final String KEY_USER_ID =
            "user_id";

    private static final String KEY_PROOF_VERIFIED =
            "proof_verified";

    private final SharedPreferences preferences;

    public SecureStorage(Context context) {

        preferences =
                context.getApplicationContext()
                        .getSharedPreferences(
                                PREF_NAME,
                                Context.MODE_PRIVATE
                        );
    }

    public void saveToken(String token) {

        if (token == null ||
                token.trim().isEmpty()) {

            return;
        }

        preferences
                .edit()
                .putString(
                        KEY_TOKEN,
                        token
                )
                .apply();
    }

    public String getToken() {

        return preferences.getString(
                KEY_TOKEN,
                null
        );
    }

    public void saveRole(String role) {

        preferences
                .edit()
                .putString(
                        KEY_ROLE,
                        role
                )
                .apply();
    }

    public String getRole() {

        return preferences.getString(
                KEY_ROLE,
                null
        );
    }

    public void saveUserId(String userId) {

        preferences
                .edit()
                .putString(
                        KEY_USER_ID,
                        userId
                )
                .apply();
    }

    public String getUserId() {

        return preferences.getString(
                KEY_USER_ID,
                null
        );
    }

    public void setProofVerified(
            boolean verified
    ) {

        preferences
                .edit()
                .putBoolean(
                        KEY_PROOF_VERIFIED,
                        verified
                )
                .apply();
    }

    public boolean isProofVerified() {

        return preferences.getBoolean(
                KEY_PROOF_VERIFIED,
                false
        );
    }

    public boolean hasSession() {

        String token = getToken();

        return token != null &&
                !token.trim().isEmpty();
    }

    public void clearSession() {

        preferences
                .edit()
                .clear()
                .apply();
    }
}
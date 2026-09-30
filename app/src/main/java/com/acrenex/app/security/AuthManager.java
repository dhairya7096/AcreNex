package com.acrenex.app.security;

import android.content.Context;

public class AuthManager {

    private final SessionManager sessionManager;

    public AuthManager(Context context) {

        sessionManager =
                new SessionManager(
                        context
                );
    }

    /**
     * Demo authentication method.
     *
     * Real authentication must be performed
     * by the AcreNex backend over HTTPS.
     */
    public boolean loginDemo(
            String email,
            String password,
            String role
    ) {

        if (email == null ||
                password == null ||
                role == null) {

            return false;
        }

        if (email.trim().isEmpty()
                || password.isEmpty()
                || role.trim().isEmpty()) {

            return false;
        }

        /*
         * Demo-only token.
         *
         * Production:
         * Backend returns a signed short-lived JWT.
         */

        String demoToken =
                SecurityUtils.generateSessionId();

        sessionManager.createSession(
                demoToken,
                "DEMO-USER-001",
                role
        );

        return true;
    }

    public boolean loginDemo(
            String email,
            String password,
            String role,
            String userId
    ) {
        if (email == null || password == null || role == null || userId == null
                || email.trim().isEmpty() || password.isEmpty()
                || role.trim().isEmpty() || userId.trim().isEmpty()) {
            return false;
        }

        String demoToken = SecurityUtils.generateSessionId();
        sessionManager.createSession(demoToken, userId, role);
        return true;
    }

    public boolean isLoggedIn() {

        return sessionManager.isLoggedIn();
    }

    public String getToken() {

        return sessionManager.getToken();
    }

    public String getRole() {

        return sessionManager.getRole();
    }

    public String getUserId() {

        return sessionManager.getUserId();
    }

    public boolean isProofVerified() {

        return sessionManager.isProofVerified();
    }

    public void markProofVerified() {

        sessionManager.markProofVerified();
    }

    public boolean isCitizen() {

        return sessionManager.isCitizen();
    }

    public boolean isOfficer() {

        return sessionManager.isOfficer();
    }

    public boolean isAdmin() {

        return sessionManager.isAdmin();
    }

    public void logout() {

        sessionManager.logout();
    }
}
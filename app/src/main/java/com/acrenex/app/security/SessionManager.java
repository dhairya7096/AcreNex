package com.acrenex.app.security;

import android.content.Context;

public class SessionManager {

    private final SecureStorage storage;

    public SessionManager(Context context) {

        storage =
                new SecureStorage(
                        context
                );
    }

    public void createSession(
            String token,
            String userId,
            String role
    ) {

        storage.saveToken(token);

        storage.saveUserId(userId);

        storage.saveRole(role);
    }

    public boolean isLoggedIn() {

        return storage.hasSession();
    }

    public String getToken() {

        return storage.getToken();
    }

    public String getUserId() {

        return storage.getUserId();
    }

    public String getRole() {

        return storage.getRole();
    }

    public void markProofVerified() {

        storage.setProofVerified(
                true
        );
    }

    public boolean isProofVerified() {

        return storage.isProofVerified();
    }

    public boolean isCitizen() {

        return "Citizen".equals(
                storage.getRole()
        );
    }

    public boolean isOfficer() {

        String role =
                storage.getRole();

        return "Revenue Officer".equals(role)
                || "Registration Officer".equals(role)
                || "Survey / GIS Officer".equals(role)
                || "Municipal / Planning Officer".equals(role)
                || "Property Tax Officer".equals(role)
                || "Dispute / Case Officer".equals(role)
                || "Environment / Restriction Officer".equals(role)
                || "Super Admin / Command Centre".equals(role);
    }

    public boolean isAdmin() {

        return "Super Admin / Command Centre".equals(
                storage.getRole()
        );
    }

    public void logout() {

        storage.clearSession();
    }
}
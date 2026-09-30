package com.acrenex.app.security;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;

public final class SecurityUtils {

    private SecurityUtils() {
        // Utility class
    }

    /**
     * SHA-256 hash.
     *
     * Suitable for non-reversible identifiers such as
     * a demo Proof ID reference.
     *
     * NEVER use plain SHA-256 for passwords.
     * Passwords must be hashed on the backend using
     * a password-specific algorithm such as Argon2id/bcrypt.
     */
    public static String sha256(String value) {

        if (value == null) {
            throw new IllegalArgumentException(
                    "Value cannot be null"
            );
        }

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    digest.digest(
                            value.trim()
                                    .getBytes(
                                            StandardCharsets.UTF_8
                                    )
                    );

            StringBuilder hex =
                    new StringBuilder();

            for (byte b : hash) {

                hex.append(
                        String.format(
                                "%02x",
                                b
                        )
                );
            }

            return hex.toString();

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Unable to generate hash",
                    e
            );
        }
    }

    /**
     * Generates a cryptographically secure
     * random demo OTP.
     *
     * Production OTP generation and verification
     * should happen on the backend.
     */
    public static String generateOtp() {

        SecureRandom random =
                new SecureRandom();

        int otp =
                100000 +
                        random.nextInt(900000);

        return String.valueOf(otp);
    }

    /**
     * Masks sensitive identifiers before
     * displaying them.
     *
     * Example:
     * ABCD123456
     * becomes
     * ******3456
     */
    public static String maskIdentifier(
            String value
    ) {

        if (value == null ||
                value.length() <= 4) {

            return "****";
        }

        int visibleCharacters = 4;

        StringBuilder masked =
                new StringBuilder();

        for (
                int i = 0;
                i < value.length() - visibleCharacters;
                i++
        ) {

            masked.append("*");
        }

        masked.append(
                value.substring(
                        value.length()
                                - visibleCharacters
                )
        );

        return masked.toString();
    }

    /** Local prototype password hashing. Production authentication should use a
     * server-side password KDF such as Argon2id/bcrypt over HTTPS. */
    public static String generatePasswordSalt() {
        byte[] bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    public static String hashPassword(String password, String saltBase64) {
        if (password == null || saltBase64 == null) throw new IllegalArgumentException("Password and salt are required");
        try {
            byte[] salt = Base64.decode(saltBase64, Base64.NO_WRAP);
            javax.crypto.spec.PBEKeySpec spec = new javax.crypto.spec.PBEKeySpec(password.toCharArray(), salt, 120000, 256);
            byte[] hash = javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
            return Base64.encodeToString(hash, Base64.NO_WRAP);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to hash password", e);
        }
    }

    public static boolean verifyPassword(String password, String saltBase64, String expectedHash) {
        if (password == null || saltBase64 == null || expectedHash == null || expectedHash.isEmpty()) return false;
        try {
            return java.security.MessageDigest.isEqual(
                    Base64.decode(expectedHash, Base64.NO_WRAP),
                    Base64.decode(hashPassword(password, saltBase64), Base64.NO_WRAP));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Generates a random session reference.
     */
    public static String generateSessionId() {

        byte[] bytes = new byte[32];

        new SecureRandom().nextBytes(bytes);

        return Base64.encodeToString(
                bytes,
                Base64.NO_WRAP
        );
    }
}
package com.acrenex.app.network;

public final class ApiConfig {

    private ApiConfig() {
        // Prevent object creation
    }

    /*
     * DEVELOPMENT SERVER
     *
     * For Android Emulator:
     * 10.0.2.2 points to your computer's localhost.
     *
     * Example:
     * FastAPI running on:
     * http://127.0.0.1:8000
     *
     * Android Emulator:
     * http://10.0.2.2:8000
     *
     * IMPORTANT:
     * This is development configuration only.
     * Production must use HTTPS.
     */

    public static final String BASE_URL =
            "http://10.0.2.2:8000/";

    public static final int CONNECT_TIMEOUT_SECONDS = 30;

    public static final int READ_TIMEOUT_SECONDS = 30;

    public static final int WRITE_TIMEOUT_SECONDS = 30;
}
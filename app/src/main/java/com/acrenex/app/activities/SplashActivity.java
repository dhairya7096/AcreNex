package com.acrenex.app.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

import androidx.appcompat.app.AppCompatActivity;

import com.acrenex.app.R;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_TIME = 2500L;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Runnable openLogin = new Runnable() {
        @Override
        public void run() {

            Intent intent = new Intent(
                    SplashActivity.this,
                    LoginActivity.class
            );

            startActivity(intent);

            // Prevent returning to splash with Back button
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Make splash completely immersive
        hideSystemBars();

        // IMPORTANT:
        // Use the actual XML splash layout.
        setContentView(R.layout.activity_splash);

        // Move to Login screen after splash duration
        handler.postDelayed(openLogin, SPLASH_TIME);
    }

    /**
     * Hides status bar and navigation bar.
     * Uses modern WindowInsets API on Android 11+
     * and compatible flags on older Android versions.
     */
    private void hideSystemBars() {

        Window window = getWindow();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            WindowInsetsController controller =
                    window.getInsetsController();

            if (controller != null) {

                controller.hide(
                        WindowInsets.Type.statusBars()
                                | WindowInsets.Type.navigationBars()
                );

                controller.setSystemBarsBehavior(
                        WindowInsetsController
                                .BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                );
            }

        } else {

            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
        }
    }

    @Override
    protected void onDestroy() {

        // Prevent delayed navigation after Activity is destroyed
        handler.removeCallbacks(openLogin);

        super.onDestroy();
    }
}
package com.acrenex.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.acrenex.app.R;

public class EnvironmentOfficerDashboardActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_environmentofficer_dashboard);
        ((TextView) findViewById(R.id.tvRoleTitle)).setText("Environment / Restriction Officer");
        ((TextView) findViewById(R.id.tvRoleSubtitle)).setText("Environmental screening, restrictions, change detection and spatial governance.");
        findViewById(R.id.btnEnvRestrictions).setOnClickListener(v -> startActivity(new Intent(this, RestrictionsActivity.class)));
        findViewById(R.id.btnEnvChange).setOnClickListener(v -> startActivity(new Intent(this, ChangeDetectionActivity.class)));
        findViewById(R.id.btnEnvMap).setOnClickListener(v -> startActivity(new Intent(this, MapActivity.class)));
        findViewById(R.id.btnEnvAnalytics).setOnClickListener(v -> startActivity(new Intent(this, AnalyticsActivity.class)));
        findViewById(R.id.btnEnvQuality).setOnClickListener(v -> startActivity(new Intent(this, DataQualityActivity.class)));
    }
}

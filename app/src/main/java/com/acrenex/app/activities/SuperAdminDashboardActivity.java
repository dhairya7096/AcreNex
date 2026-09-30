package com.acrenex.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.acrenex.app.R;

public class SuperAdminDashboardActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_superadmin_dashboard);
        ((TextView) findViewById(R.id.tvRoleTitle)).setText("Super Admin / Command Centre");
        ((TextView) findViewById(R.id.tvRoleSubtitle)).setText("Cross-department configuration, APIs, security, quality and audit workspace.");
        findViewById(R.id.btnAdminState).setOnClickListener(v -> startActivity(new Intent(this, StateConfigurationActivity.class)));
        findViewById(R.id.btnAdminInterop).setOnClickListener(v -> startActivity(new Intent(this, InteroperabilityActivity.class)));
        findViewById(R.id.btnAdminQuality).setOnClickListener(v -> startActivity(new Intent(this, DataQualityActivity.class)));
        findViewById(R.id.btnAdminAudit).setOnClickListener(v -> startActivity(new Intent(this, AuditLogActivity.class)));
        findViewById(R.id.btnAdminHub).setOnClickListener(v -> startActivity(new Intent(this, LandStackHubActivity.class)));
    }
}

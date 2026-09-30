package com.acrenex.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.acrenex.app.R;

public class RegistrationOfficerDashboardActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_registrationofficer_dashboard);
        ((TextView) findViewById(R.id.tvRoleTitle)).setText("Registration Officer");
        ((TextView) findViewById(R.id.tvRoleSubtitle)).setText("Registration, deed, transaction and document verification workspace.");
        findViewById(R.id.btnRegDocuments).setOnClickListener(v -> startActivity(new Intent(this, DocumentActivity.class)));
        findViewById(R.id.btnRegApplications).setOnClickListener(v -> startActivity(new Intent(this, ApplicationsActivity.class)));
        findViewById(R.id.btnRegStatus).setOnClickListener(v -> startActivity(new Intent(this, ApplicationStatusActivity.class)));
        findViewById(R.id.btnRegOwnership).setOnClickListener(v -> startActivity(new Intent(this, OwnershipDetailsActivity.class)));
        findViewById(R.id.btnRegAudit).setOnClickListener(v -> startActivity(new Intent(this, AuditLogActivity.class)));
    }
}

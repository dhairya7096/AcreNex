package com.acrenex.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.acrenex.app.R;

public class DisputeOfficerDashboardActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_disputeofficer_dashboard);
        ((TextView) findViewById(R.id.tvRoleTitle)).setText("Dispute / Case Officer");
        ((TextView) findViewById(R.id.tvRoleSubtitle)).setText("Parcel disputes, affected records, evidence and resolution workflow.");
        findViewById(R.id.btnDisputeCases).setOnClickListener(v -> startActivity(new Intent(this, DisputeActivity.class)));
        findViewById(R.id.btnDisputeOwnership).setOnClickListener(v -> startActivity(new Intent(this, OwnershipDetailsActivity.class)));
        findViewById(R.id.btnDisputeParcel).setOnClickListener(v -> startActivity(new Intent(this, ParcelDetailsActivity.class)));
        findViewById(R.id.btnDisputeWorkflow).setOnClickListener(v -> startActivity(new Intent(this, WorkflowActivity.class)));
        findViewById(R.id.btnDisputeAudit).setOnClickListener(v -> startActivity(new Intent(this, AuditLogActivity.class)));
    }
}

package com.acrenex.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.acrenex.app.R;

public class RevenueOfficerDashboardActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_revenueofficer_dashboard);
        ((TextView) findViewById(R.id.tvRoleTitle)).setText("Revenue Officer");
        ((TextView) findViewById(R.id.tvRoleSubtitle)).setText("RoR, ownership, mutation, dispute and parcel verification workspace.");
        findViewById(R.id.btnRevenueRecords).setOnClickListener(v -> startActivity(new Intent(this, LandRecordsActivity.class)));
        findViewById(R.id.btnRevenueOwnership).setOnClickListener(v -> startActivity(new Intent(this, OwnershipDetailsActivity.class)));
        findViewById(R.id.btnRevenueDisputes).setOnClickListener(v -> startActivity(new Intent(this, DisputeActivity.class)));
        findViewById(R.id.btnRevenueServices).setOnClickListener(v -> startActivity(new Intent(this, ServiceRequestActivity.class)));
        findViewById(R.id.btnRevenueAI).setOnClickListener(v -> startActivity(new Intent(this, AIAssistantActivity.class)));
    }
}

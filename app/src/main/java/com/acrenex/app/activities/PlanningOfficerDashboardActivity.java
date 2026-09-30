package com.acrenex.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.acrenex.app.R;

public class PlanningOfficerDashboardActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_planningofficer_dashboard);
        ((TextView) findViewById(R.id.tvRoleTitle)).setText("Municipal / Planning Officer");
        ((TextView) findViewById(R.id.tvRoleSubtitle)).setText("Master plan, zoning, building permission and development restriction workspace.");
        findViewById(R.id.btnPlanBuilding).setOnClickListener(v -> startActivity(new Intent(this, BuildingPermissionActivity.class)));
        findViewById(R.id.btnPlanRestrictions).setOnClickListener(v -> startActivity(new Intent(this, RestrictionsActivity.class)));
        findViewById(R.id.btnPlanMap).setOnClickListener(v -> startActivity(new Intent(this, MapActivity.class)));
        findViewById(R.id.btnPlanLayers).setOnClickListener(v -> startActivity(new Intent(this, LayerControlActivity.class)));
        findViewById(R.id.btnPlanAnalytics).setOnClickListener(v -> startActivity(new Intent(this, AnalyticsActivity.class)));
    }
}

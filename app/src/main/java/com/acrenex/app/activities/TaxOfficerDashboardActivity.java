package com.acrenex.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.acrenex.app.R;

public class TaxOfficerDashboardActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_taxofficer_dashboard);
        ((TextView) findViewById(R.id.tvRoleTitle)).setText("Property Tax Officer");
        ((TextView) findViewById(R.id.tvRoleSubtitle)).setText("Property taxation, valuation references, dues and fiscal parcel linkage workspace.");
        findViewById(R.id.btnTaxRecords).setOnClickListener(v -> startActivity(new Intent(this, PropertyTaxActivity.class)));
        findViewById(R.id.btnTaxSearch).setOnClickListener(v -> startActivity(new Intent(this, ParcelSearchActivity.class)));
        findViewById(R.id.btnTaxAnalytics).setOnClickListener(v -> startActivity(new Intent(this, AnalyticsActivity.class)));
        findViewById(R.id.btnTaxQuality).setOnClickListener(v -> startActivity(new Intent(this, DataQualityActivity.class)));
        findViewById(R.id.btnTaxAudit).setOnClickListener(v -> startActivity(new Intent(this, AuditLogActivity.class)));
    }
}

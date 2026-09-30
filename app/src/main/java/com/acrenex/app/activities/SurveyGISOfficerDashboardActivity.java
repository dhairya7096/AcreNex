package com.acrenex.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.acrenex.app.R;

public class SurveyGISOfficerDashboardActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_surveygisofficer_dashboard);
        ((TextView) findViewById(R.id.tvRoleTitle)).setText("Survey / GIS Officer");
        ((TextView) findViewById(R.id.tvRoleSubtitle)).setText("Cadastral, ULPIN, parcel geometry and geospatial quality workspace.");
        findViewById(R.id.btnGISMap).setOnClickListener(v -> startActivity(new Intent(this, MapActivity.class)));
        findViewById(R.id.btnGISLayers).setOnClickListener(v -> startActivity(new Intent(this, LayerControlActivity.class)));
        findViewById(R.id.btnGISChange).setOnClickListener(v -> startActivity(new Intent(this, ChangeDetectionActivity.class)));
        findViewById(R.id.btnGISSearch).setOnClickListener(v -> startActivity(new Intent(this, ParcelSearchActivity.class)));
        findViewById(R.id.btnGISQuality).setOnClickListener(v -> startActivity(new Intent(this, DataQualityActivity.class)));
    }
}

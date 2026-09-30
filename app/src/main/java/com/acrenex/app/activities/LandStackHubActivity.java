package com.acrenex.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.acrenex.app.utils;
import com.google.android.material.button.MaterialButton;

/** Premium entry point for the parcel-centric Land Stack modules. */
public class LandStackHubActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle b){
        super.onCreate(b);
        LinearLayout root=utils.screen(this,"Land Stack Intelligence","One parcel, one ULPIN, connected governance layers — built for citizen services and departmental workflows.");
        LinearLayout hero=utils.column(this);
        hero.addView(utils.text(this,"PARCEL-CENTRIC DIGITAL PUBLIC INFRASTRUCTURE",11,utils.GREEN,true));
        hero.addView(utils.text(this,"AcreNex connects spatial, record, fiscal, planning and service layers around a common parcel identifier.",16,utils.TEXT,true));
        hero.addView(utils.text(this,"Synthetic demo records are clearly labelled. Authorized state APIs can replace them without changing the UI contract.",13,utils.MUTED,false));
        utils.addCard(root,hero,this);

        add(root,"GIS & Spatial Layers","Cadastral parcels, ULPIN, layer controls, planning overlays and change signals.",MapActivity.class,"Open GIS Map"); add(root,"GIS Layer Control","Cadastral, RoR, planning, tax, utility and restriction overlays.",LayerControlActivity.class,"Manage Layers");
        add(root,"Core Governance Records","RoR, ownership, registration, land use, zoning and building permissions.",LandRecordsActivity.class,"Open Land Records");
        add(root,"Fiscal & Property Intelligence","Property tax, valuation references and parcel-level fiscal status.",PropertyTaxActivity.class,"Open Fiscal Layer");
        add(root,"Restrictions & Encumbrances","Planning restrictions, environmental screening, mortgage/encumbrance indicators.",RestrictionsActivity.class,"Open Restrictions");
        add(root,"Utilities & Infrastructure","Water, electricity and network references linked to the parcel.",UtilitiesActivity.class,"Open Utilities");
        add(root,"Dispute & Legal Signals","Dispute/case status and review workflow without exposing sensitive case data.",DisputeActivity.class,"Open Disputes");
        add(root,"Citizen Services","Service requests, department routing, application progress and status tracking.",ServiceRequestActivity.class,"Open Services");
        add(root,"AI Decision Support","OCR, explainable record-risk scan, change detection and parcel-grounded assistant.",AIAssistantActivity.class,"Open AI Layer");
        add(root,"Data Quality","Completeness, consistency, spatial accuracy and timeliness indicators.",DataQualityActivity.class,"Run Quality View");
        add(root,"Interoperability","API registry, versions, authentication scheme and synchronization status.",InteroperabilityActivity.class,"Open API Registry");
        add(root,"State Configuration","Different state formats, languages, units and workflows through configuration rather than forks.",StateConfigurationActivity.class,"Open State Config");
        add(root,"Predictive Governance","Transparent workload and service-flow indicators for officer decision support.",PredictiveInsightsActivity.class,"Open Predictive View"); add(root,"Officer Workflow","Role-based review, audit trails, alerts and analytics.",OfficerDashboardActivity.class,"Open Officer View");
        utils.setScreenContentView(this, root);
    }
    private void add(LinearLayout root,String title,String desc,Class<?> target,String label){
        LinearLayout box=utils.column(this); box.addView(utils.text(this,title,18,utils.TEXT,true)); box.addView(utils.text(this,desc,13,utils.MUTED,false));
        MaterialButton b=utils.button(this,label); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,utils.dp(this,48)); p.topMargin=utils.dp(this,8); box.addView(b,p);
        b.setOnClickListener(v->startActivity(new Intent(this,target))); utils.addCard(root,box,this);
    }
}

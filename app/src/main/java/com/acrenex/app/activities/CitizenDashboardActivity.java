package com.acrenex.app.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.acrenex.app.R;
import com.acrenex.app.ai.LandStackAIEngine;
import com.acrenex.app.database.DatabaseManager;
import com.acrenex.app.security.AuthManager;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CitizenDashboardActivity extends AppCompatActivity {
    private TextView tvWelcomeName,tvProfileInitial,tvParcelCount,tvTotalArea,tvVerifiedCount,tvPrimaryUlpin,tvPrimaryLocation,tvPrimaryArea,tvOwnershipStatus,tvRegistrationStatus,tvTaxStatus,tvLandUseStatus,tvPrimaryVerification,tvAIHeadline,tvAIInsight;
    private MaterialCardView cardNotifications,cardMap,cardSearch,cardDocuments,cardAI,cardPrimaryParcel,tvProfile;
    private View navHome,navMap,navServices,navProfile;
    private AuthManager authManager; private SQLiteDatabase db; private String currentUserId,currentUserName;
    private final List<ParcelRow> userParcels=new ArrayList<>();

    @Override protected void onCreate(Bundle savedInstanceState){super.onCreate(savedInstanceState);if(getSupportActionBar()!=null)getSupportActionBar().hide();setContentView(R.layout.activity_citizen_dashboard);authManager=new AuthManager(this);db=new DatabaseManager(this).getDatabase();init();listeners();load();}
    @Override protected void onResume(){super.onResume();if(db!=null)load();}

    private void init(){
        tvWelcomeName=findViewById(R.id.tvWelcomeName);tvProfileInitial=findViewById(R.id.tvProfileInitial);tvParcelCount=findViewById(R.id.tvParcelCount);tvTotalArea=findViewById(R.id.tvTotalArea);tvVerifiedCount=findViewById(R.id.tvVerifiedCount);
        tvPrimaryUlpin=findViewById(R.id.tvPrimaryUlpin);tvPrimaryLocation=findViewById(R.id.tvPrimaryLocation);tvPrimaryArea=findViewById(R.id.tvPrimaryArea);tvOwnershipStatus=findViewById(R.id.tvOwnershipStatus);tvRegistrationStatus=findViewById(R.id.tvRegistrationStatus);tvTaxStatus=findViewById(R.id.tvTaxStatus);tvLandUseStatus=findViewById(R.id.tvLandUseStatus);tvPrimaryVerification=findViewById(R.id.tvPrimaryVerification);tvAIHeadline=findViewById(R.id.tvAIHeadline);tvAIInsight=findViewById(R.id.tvAIInsight);
        cardNotifications=findViewById(R.id.cardNotifications);tvProfile=findViewById(R.id.tvProfile);cardMap=findViewById(R.id.cardMap);cardSearch=findViewById(R.id.cardSearch);cardDocuments=findViewById(R.id.cardDocuments);cardAI=findViewById(R.id.cardAI);cardPrimaryParcel=findViewById(R.id.cardPrimaryParcel);navHome=findViewById(R.id.navHome);navMap=findViewById(R.id.navMap);navServices=findViewById(R.id.navServices);navProfile=findViewById(R.id.navProfile);
    }

    private void load(){currentUserId=authManager.getUserId();if(currentUserId==null||currentUserId.isEmpty())currentUserId="DEMO-USER-001";currentUserName=findUserName(currentUserId);if(currentUserName.isEmpty())currentUserName="AcreNex Citizen";tvWelcomeName.setText("Welcome back, "+currentUserName);tvProfileInitial.setText(currentUserName.substring(0,1).toUpperCase(Locale.US));loadParcels();stats();ai();}
    private String findUserName(String id){Cursor c=null;try{c=db.query("users",new String[]{"name"},"id=?",new String[]{id},null,null,null,"1");return c.moveToFirst()?safe(c.getString(0)):"";}finally{if(c!=null)c.close();}}
    private void loadParcels(){userParcels.clear();Cursor c=null;try{c=db.query("parcels",null,"owner_name=?",new String[]{currentUserName},null,null,"created_at DESC");while(c.moveToNext()){ParcelRow p=new ParcelRow();p.ulpin=safe(c,"ulpin");p.state=safe(c,"state");p.district=safe(c,"district");p.taluka=safe(c,"taluka");p.village=safe(c,"village");p.area=parse(safe(c,"area"));p.unit=safe(c,"area_unit");p.registration=safe(c,"registration_status");p.tax=safe(c,"tax_status");p.landUse=safe(c,"land_use");userParcels.add(p);}}finally{if(c!=null)c.close();}}
    private void stats(){int count=userParcels.size();double area=0;int verified=0;for(ParcelRow p:userParcels){area+=p.area;boolean o=exists("ownership_records",p.ulpin,"verification_status","VERIFIED");boolean r=complete(p.registration),t=complete(p.tax),l=!p.landUse.isEmpty();if(o&&r&&t&&l)verified++;}tvParcelCount.setText(String.valueOf(count));tvTotalArea.setText(String.format(Locale.US,"%.2f",area));tvVerifiedCount.setText(String.valueOf(verified));if(userParcels.isEmpty()){tvPrimaryUlpin.setText("No parcel linked");tvPrimaryLocation.setText("Search or add a parcel to begin");tvPrimaryArea.setText("0.00 Acres");tvPrimaryVerification.setText("AI WAITING FOR PARCEL");return;}ParcelRow p=userParcels.get(0);tvPrimaryUlpin.setText(p.ulpin);tvPrimaryLocation.setText(location(p));tvPrimaryArea.setText(String.format(Locale.US,"%.2f %s",p.area,p.unit.isEmpty()?"Acres":p.unit));boolean o=exists("ownership_records",p.ulpin,"verification_status","VERIFIED"),r=complete(p.registration),t=complete(p.tax),l=!p.landUse.isEmpty();status(tvOwnershipStatus,o,o?"Ownership verified":"Ownership review");status(tvRegistrationStatus,r,p.registration.isEmpty()?"Registration review":p.registration);status(tvTaxStatus,t,p.tax.isEmpty()?"Tax review":p.tax);status(tvLandUseStatus,l,l?p.landUse:"Land use review");tvPrimaryVerification.setText((o&&r&&t&&l)?"CORE RECORDS READY":"AI REVIEW AVAILABLE");}
    private void ai(){if(userParcels.isEmpty()){tvAIHeadline.setText("Connect a parcel to start AI review");tvAIInsight.setText("AcreNex AI will inspect ownership, registration, tax, planning, restrictions and documents from the parcel-centric record.");return;}LandStackAIEngine.Analysis a=LandStackAIEngine.analyze(db,userParcels.get(0).ulpin);tvAIHeadline.setText("AI governance risk: "+a.risk+" • "+a.score+"/100");tvAIInsight.setText(a.summary+" "+a.nextAction);}
    private boolean exists(String table,String ulpin,String col,String val){Cursor c=null;try{c=db.rawQuery("SELECT COUNT(*) FROM "+table+" WHERE ulpin=? AND UPPER("+col+")=?",new String[]{ulpin,val});return c.moveToFirst()&&c.getInt(0)>0;}catch(Exception e){return false;}finally{if(c!=null)c.close();}}
    private boolean complete(String s){if(s==null)return false;s=s.toUpperCase(Locale.US);return s.contains("VERIF")||s.contains("APPROV")||s.contains("PAID")||s.contains("CLEAR")||s.contains("ACTIVE")||s.contains("COMPLET");}
    private void status(TextView v,boolean ok,String t){v.setText(ok?"✓ "+t:"⚠ "+t);v.setTextColor(getColor(ok?R.color.acrenex_success:R.color.acrenex_warning));}
    private String location(ParcelRow p){List<String>x=new ArrayList<>();if(!p.village.isEmpty())x.add(p.village);if(!p.taluka.isEmpty())x.add(p.taluka);if(!p.district.isEmpty())x.add(p.district);if(!p.state.isEmpty())x.add(p.state);return android.text.TextUtils.join(", ",x);}
    private String safe(Cursor c,String n){int i=c.getColumnIndex(n);return i>=0&&!c.isNull(i)?c.getString(i).trim():"";}private String safe(String s){return s==null?"":s.trim();}private double parse(String s){try{return Double.parseDouble(s.replace(",",""));}catch(Exception e){return 0;}}
    private void listeners(){cardNotifications.setOnClickListener(v->open(NotificationsActivity.class));tvProfile.setOnClickListener(v->open(ProfileActivity.class));cardMap.setOnClickListener(v->open(MapActivity.class));cardSearch.setOnClickListener(v->open(ParcelSearchActivity.class));cardDocuments.setOnClickListener(v->open(DocumentActivity.class));cardAI.setOnClickListener(v->open(AIAssistantActivity.class));cardPrimaryParcel.setOnClickListener(v->primary());findViewById(R.id.btnAskAI).setOnClickListener(v->open(AIAssistantActivity.class));navHome.setOnClickListener(v->{});navMap.setOnClickListener(v->open(MapActivity.class));navServices.setOnClickListener(v->open(LandStackHubActivity.class));navProfile.setOnClickListener(v->open(ProfileActivity.class));}
    private void primary(){if(userParcels.isEmpty()){Toast.makeText(this,"No parcel is linked to this profile yet.",Toast.LENGTH_SHORT).show();return;}Intent i=new Intent(this,ParcelDetailsActivity.class);i.putExtra("ulpin",userParcels.get(0).ulpin);startActivity(i);}
    private void open(Class<?> cls){try{startActivity(new Intent(this,cls));}catch(Exception e){Toast.makeText(this,"This module is not available yet.",Toast.LENGTH_SHORT).show();}}
    private static class ParcelRow{String ulpin="",state="",district="",taluka="",village="",unit="",registration="",tax="",landUse="";double area;}
}

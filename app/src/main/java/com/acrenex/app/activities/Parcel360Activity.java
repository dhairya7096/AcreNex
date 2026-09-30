package com.acrenex.app.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.acrenex.app.database.DatabaseManager;
import com.acrenex.app.utils;

/** Single-panel parcel view: one ULPIN is the navigation key for every connected land layer. */
public class Parcel360Activity extends AppCompatActivity {
    private String ulpin;
    @Override protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState); ulpin=getIntent().getStringExtra("ulpin"); if(ulpin==null||ulpin.trim().isEmpty())ulpin="GJGN0001000001";
        DatabaseManager manager=new DatabaseManager(this); LinearLayout root=utils.screen(this,"Parcel 360 Command Panel","ONE ULPIN • ALL LAND DATA • ONE OFFICER WORKSPACE");
        LinearLayout identity=utils.column(this); Cursor parcel=manager.getParcel(ulpin); try{if(parcel.moveToFirst()){utils.row(identity,this,"ULPIN",v(parcel,"ulpin"));utils.row(identity,this,"Survey No.",v(parcel,"survey_number"));utils.row(identity,this,"Owner",v(parcel,"owner_name"));utils.row(identity,this,"Location",v(parcel,"village")+", "+v(parcel,"taluka")+", "+v(parcel,"district"));utils.row(identity,this,"Coordinates",v(parcel,"latitude")+", "+v(parcel,"longitude"));utils.row(identity,this,"Area",v(parcel,"area")+" "+v(parcel,"area_unit"));utils.row(identity,this,"Land Use / Zone",v(parcel,"land_use")+" / "+v(parcel,"zoning"));utils.row(identity,this,"Registration",v(parcel,"registration_status"));utils.row(identity,this,"Tax",v(parcel,"tax_status"));utils.row(identity,this,"Building Permission",v(parcel,"building_permission_status"));utils.row(identity,this,"Encumbrance",v(parcel,"encumbrance_status"));}}finally{parcel.close();}utils.addCard(root,identity,this);
        addData(root,manager,"01 • Spatial / Cadastral","Survey, coordinate and parcel geometry","parcel_vertices","GIS");
        addData(root,manager,"02 • Ownership / RoR","Current right-holder and verification","ownership_records","ROR / Revenue");
        addData(root,manager,"03 • Registration","Transaction and registered document","registration_records","Registration");
        addData(root,manager,"04 • Land Use / Planning","Land-use, zoning and master-plan status","land_use_records","Planning");
        addData(root,manager,"05 • Building Permission","Approval / construction permission","building_permissions","Municipal Corporation");
        addData(root,manager,"06 • Property Tax","Annual tax, outstanding amount and payment status","tax_records","Municipality");
        addData(root,manager,"07 • Encumbrance","Mortgage / charge / liability","encumbrances","Revenue / Registration");
        addData(root,manager,"08 • Restrictions","Planning and environmental restrictions","restrictions","Planning / Environment");
        addData(root,manager,"09 • Utilities","Water / electricity / infrastructure links","utilities","Utility Agencies");
        addData(root,manager,"10 • Valuation","Guideline and market reference","valuation_records","Fiscal / Valuation");
        addData(root,manager,"11 • Disputes","Case and hearing status","disputes","Case Officer");
        addData(root,manager,"12 • Service Workflow","Citizen applications and departmental hand-off","service_requests","Citizen Services");
        addData(root,manager,"13 • AI / Change Detection","Explainable risk/change signals","change_events","AcreNex AI");
        Button docs=new Button(this);docs.setText("Open Shared Document Checklist");docs.setAllCaps(false);root.addView(docs,new LinearLayout.LayoutParams(-1,utils.dp(this,52)));docs.setOnClickListener(v->startActivity(new Intent(this,DocumentActivity.class)));
        setContentView((android.view.View)root.getParent()); manager.close();
    }
    private void addData(LinearLayout root,DatabaseManager m,String title,String desc,String table,String dept){
        LinearLayout box=utils.column(this); Cursor c=null; int count=0; String summary="No linked record";
        try{if(table.equals("parcel_vertices")){c=m.getDatabase().rawQuery("SELECT latitude,longitude FROM parcel_vertices WHERE ulpin=? ORDER BY vertex_order",new String[]{ulpin});if(c.moveToFirst()){count=1;summary="Boundary vertices linked • first vertex "+c.getDouble(0)+", "+c.getDouble(1);while(c.moveToNext())count++;}}else{c=m.getDatabase().rawQuery("SELECT * FROM "+table+" WHERE ulpin=? LIMIT 1",new String[]{ulpin});if(c.moveToFirst()){count=1;StringBuilder sb=new StringBuilder();for(int i=0;i<c.getColumnCount();i++){String n=c.getColumnName(i);if("id".equals(n)||"ulpin".equals(n)||"document_id".equals(n))continue;String val=c.isNull(i)?"":c.getString(i);if(val.isEmpty())continue;if(sb.length()>0)sb.append(" • ");sb.append(n.replace('_',' ')).append(": ").append(val);if(sb.length()>260)break;}summary=sb.toString();}}}catch(Exception e){summary="Layer not available";}finally{if(c!=null)c.close();}
        LinearLayout h=utils.rowContainer(this);h.addView(utils.text(this,title,15,utils.TEXT,true),new LinearLayout.LayoutParams(0,-2,1));h.addView(utils.badge(this,count>0?"LINKED":"NO DATA",count>0));box.addView(h);box.addView(utils.text(this,desc,12,utils.MUTED,false));utils.row(box,this,"Authority / owner",dept);utils.row(box,this,"Records linked",String.valueOf(count));box.addView(utils.text(this,summary,11,utils.MUTED,false));utils.addCard(root,box,this);
    }
    private String v(Cursor c,String n){int i=c.getColumnIndex(n);return i>=0&&!c.isNull(i)?c.getString(i):"—";}
}

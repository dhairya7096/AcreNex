package com.acrenex.app.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.acrenex.app.database.DatabaseManager;
import com.acrenex.app.utils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class ParcelDetailsActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle b){
        super.onCreate(b); if(getSupportActionBar()!=null)getSupportActionBar().hide();
        String ulpin=getIntent().getStringExtra("ulpin"); if(ulpin==null||ulpin.isEmpty())ulpin="24000000000001";
        final String selected=ulpin;
        DatabaseManager m=new DatabaseManager(this);
        LinearLayout root=utils.screen(this,"Parcel 360","One ULPIN connects the spatial parcel with its land-stack records.");
        Cursor c=m.getParcel(ulpin);
        try{
            if(c.moveToFirst()){
                MaterialCardView identity=utils.card(this); LinearLayout box=utils.column(this);
                LinearLayout top=utils.rowContainer(this);
                LinearLayout title=new LinearLayout(this); title.setOrientation(LinearLayout.VERTICAL);
                title.addView(utils.text(this,"PARCEL IDENTITY",9,utils.GOLD,true));
                title.addView(utils.text(this,s(c,"ulpin"),21,utils.NAVY,true));
                top.addView(title,new LinearLayout.LayoutParams(0,-2,1));
                top.addView(utils.badge(this,"DEMO RECORD",true)); box.addView(top);
                box.addView(utils.text(this,s(c,"village")+", "+s(c,"district")+", "+s(c,"state"),12,utils.MUTED,false),margin(0,8));
                box.addView(utils.text(this,"Owner  •  "+s(c,"owner_name"),13,utils.TEXT,true),margin(0,8));
                identity.addView(box); root.addView(identity,margin(12));

                MaterialCardView facts=utils.card(this); LinearLayout fb=utils.column(this);
                fb.addView(utils.section(this,"Core record"));
                utils.row(fb,this,"Survey Number",s(c,"survey_number")); utils.row(fb,this,"Area",s(c,"area")+" "+s(c,"area_unit"));
                utils.row(fb,this,"Land Use",s(c,"land_use")); utils.row(fb,this,"Zoning",s(c,"zoning"));
                utils.row(fb,this,"Registration",s(c,"registration_status")); utils.row(fb,this,"Tax",s(c,"tax_status"));
                utils.row(fb,this,"Building Permission",s(c,"building_permission_status")); utils.row(fb,this,"Encumbrance",s(c,"encumbrance_status"));
                facts.addView(fb); root.addView(facts,margin(12));
            } else root.addView(utils.text(this,"Parcel not found in the local Land Stack.",14,utils.MUTED,false),margin(0,10));
        }finally{c.close();m.close();}

        root.addView(utils.section(this,"Linked land-stack modules"));
        add(root,"GIS Map","View coordinate and parcel boundary",MapActivity.class,selected,utils.GREEN);
        add(root,"Parcel 360","Open the complete linked record",Parcel360Activity.class,selected,utils.NAVY);
        add(root,"AI Governance Review","Evidence, risk signals and next action",AIGovernanceReviewActivity.class,selected,utils.GREEN);
        add(root,"Department Workflow","Review inter-department processing",DepartmentWorkflowActivity.class,selected,utils.BLUE);
        add(root,"Ownership & RoR","Ownership verification and land records",OwnershipDetailsActivity.class,selected,utils.BLUE);
        add(root,"Building & Planning","Permission and planning checks",BuildingPermissionActivity.class,selected,utils.GOLD);
        add(root,"Tax & Valuation","Property tax and valuation records",PropertyTaxActivity.class,selected,utils.GOLD);
        add(root,"Restrictions & Encumbrances","Restrictions, mortgage and screening",RestrictionsActivity.class,selected,utils.NAVY);
        add(root,"Utilities","Utility connections linked to the parcel",UtilitiesActivity.class,selected,utils.BLUE);
        add(root,"Disputes","Case and objection records",DisputeActivity.class,selected,utils.NAVY);
        add(root,"AI Change Detection","Spatial change signals",ChangeDetectionActivity.class,selected,utils.GREEN);
        utils.setScreenContentView(this,root);
    }
    private void add(LinearLayout root,String label,String sub,Class<?> cls,String ulpin,int accent){
        MaterialCardView card=utils.card(this); LinearLayout row=new LinearLayout(this); row.setOrientation(LinearLayout.HORIZONTAL); row.setGravity(Gravity.CENTER_VERTICAL); row.setPadding(utils.dp(this,14),utils.dp(this,9),utils.dp(this,9),utils.dp(this,9));
        TextView dot=utils.text(this,"",1,accent,false); dot.setBackground(round(accent)); row.addView(dot,new LinearLayout.LayoutParams(utils.dp(this,10),utils.dp(this,40)));
        LinearLayout texts=new LinearLayout(this); texts.setOrientation(LinearLayout.VERTICAL); texts.setPadding(utils.dp(this,11),0,utils.dp(this,6),0); texts.addView(utils.text(this,label,13,utils.TEXT,true)); texts.addView(utils.text(this,sub,10,utils.MUTED,false)); row.addView(texts,new LinearLayout.LayoutParams(0,-2,1));
        MaterialButton open=utils.outlineButton(this,"Open"); open.setMinHeight(utils.dp(this,40)); open.setOnClickListener(v->{Intent i=new Intent(this,cls);i.putExtra("ulpin",ulpin);startActivity(i);}); row.addView(open,new LinearLayout.LayoutParams(utils.dp(this,70),utils.dp(this,42)));
        card.addView(row);root.addView(card,margin(9));
    }
    private android.graphics.drawable.GradientDrawable round(int color){android.graphics.drawable.GradientDrawable g=new android.graphics.drawable.GradientDrawable();g.setColor(color);g.setCornerRadius(utils.dp(this,10));return g;}
    private LinearLayout.LayoutParams margin(int bottom,int top){LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.bottomMargin=utils.dp(this,bottom);p.topMargin=utils.dp(this,top);return p;}
    private LinearLayout.LayoutParams margin(int bottom){return margin(bottom,0);}
    private String s(Cursor c,String n){int i=c.getColumnIndex(n);return i>=0&&c.getString(i)!=null?c.getString(i):"—";}
}

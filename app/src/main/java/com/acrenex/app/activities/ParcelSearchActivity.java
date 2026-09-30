package com.acrenex.app.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.acrenex.app.database.DatabaseManager;
import com.acrenex.app.database.ParcelRepository;
import com.acrenex.app.security.AuthManager;
import com.acrenex.app.utils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** Parcel search plus a prototype ULPIN creation workspace. */
public class ParcelSearchActivity extends AppCompatActivity {
    private LinearLayout results;
    private DatabaseManager manager;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        if (getSupportActionBar()!=null) getSupportActionBar().hide();
        manager=new DatabaseManager(this);
        LinearLayout root=utils.screen(this,"Parcel Intelligence","Find a parcel or create a synthetic AcreNex ULPIN for the prototype dataset.");

        root.addView(utils.section(this,"Find a parcel"));
        MaterialCardView searchCard=utils.card(this);
        LinearLayout searchBox=utils.column(this);
        EditText input=field("ULPIN, owner or survey number");
        searchBox.addView(input,new LinearLayout.LayoutParams(-1,utils.dp(this,54)));
        MaterialButton search=utils.button(this,"Search parcel  →");
        searchBox.addView(search,margin(-1,50,9,0,0,0));
        searchCard.addView(searchBox); root.addView(searchCard,margin(-1,-2,0,0,0,12));
        results=new LinearLayout(this); results.setOrientation(LinearLayout.VERTICAL); root.addView(results);
        search.setOnClickListener(v->search(input.getText().toString().trim()));

        root.addView(utils.section(this,"Create parcel identity"));
        MaterialCardView create=utils.card(this);
        LinearLayout box=utils.column(this);
        box.addView(utils.text(this,"Generate a ULPIN for your demo parcel",18,utils.TEXT,true));
        box.addView(utils.text(this,"AcreNex creates a deterministic 14-digit prototype identifier from the parcel inputs. It is not an official government ULPIN until assigned by the authoritative land administration.",11,utils.MUTED,false),margin(-1,-2,4,0,0,0));

        EditText survey=field("Survey number  •  e.g. 101/1");
        EditText village=field("Village / locality  •  e.g. Sargasan");
        EditText owner=field("Owner name");
        EditText lat=field("Latitude  •  e.g. 23.1835"); lat.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_NUMBER_FLAG_SIGNED);
        EditText lon=field("Longitude  •  e.g. 72.6368"); lon.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_NUMBER_FLAG_SIGNED);
        addField(box,survey); addField(box,village); addField(box,owner); addField(box,lat); addField(box,lon);
        MaterialButton generate=utils.button(this,"Generate ULPIN & Save Parcel");
        box.addView(generate,margin(-1,50,10,0,0,0));
        TextView result=utils.text(this,"",13,utils.GREEN,true); result.setGravity(Gravity.CENTER); box.addView(result);
        create.addView(box); root.addView(create,margin(-1,-2,0,0,0,12));

        MaterialCardView note=utils.card(this); LinearLayout nb=utils.column(this);
        nb.addView(utils.text(this,"LAND ID RULE",10,utils.GOLD,true));
        nb.addView(utils.text(this,"ULPIN is the parcel identity link across GIS, RoR, ownership, registration, tax, permissions, documents and AI signals. The prototype generator keeps that relationship local and repeatable.",11,utils.MUTED,false));
        note.addView(nb); root.addView(note,margin(-1,-2,0,0,0,12));

        generate.setOnClickListener(v->{
            String s=survey.getText().toString().trim(), vv=village.getText().toString().trim(), oo=owner.getText().toString().trim();
            if(oo.isEmpty()) oo=currentOwner();
            if(s.isEmpty()){survey.setError("Enter survey number");return;}
            if(vv.isEmpty()){village.setError("Enter village/locality");return;}
            double la=parse(lat.getText().toString(),23.1835), lo=parse(lon.getText().toString(),72.6368);
            String ulpin=ParcelRepository.generateDemoUlpin("24", "Gandhinagar", s, la, lo);
            DatabaseManager db=new DatabaseManager(this);
            Cursor existing=db.getDatabase().rawQuery("SELECT COUNT(*) FROM parcels WHERE ulpin=?",new String[]{ulpin});
            boolean already=existing.moveToFirst()&&existing.getInt(0)>0; existing.close();
            if(already){ result.setText("✓ This parcel already has ULPIN  " + ulpin); search(ulpin); db.close(); return; }
            boolean ok=db.insertParcel(ulpin,s,"Gujarat","Gandhinagar","Gandhinagar",vv,oo,"1.00","Acre","Residential","Residential","R-2","Draft","Clear","Pending","Not Required",la,lo);
            SQLiteDatabase sql=db.getDatabase();
            String now=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.US).format(new Date());
            if(ok){
                sql.execSQL("INSERT OR REPLACE INTO ownership_records(id,ulpin,owner_name,ownership_type,share,record_date,source,verification_status) VALUES(?,?,?,?,?,?,?,?)",new Object[]{"OWN-"+ulpin,ulpin,oo,"Individual","100%",now,"AcreNex prototype","PENDING"});
                sql.execSQL("DELETE FROM parcel_vertices WHERE ulpin=?",new String[]{ulpin});
                double d=0.0007;
                double[][] pts={{la-d,lo-d},{la-d,lo+d},{la+d,lo+d},{la+d,lo-d}};
                for(int i=0;i<pts.length;i++) sql.execSQL("INSERT INTO parcel_vertices(ulpin,vertex_order,latitude,longitude) VALUES(?,?,?,?)",new Object[]{ulpin,i+1,pts[i][0],pts[i][1]});
                result.setText("✓ Created demo ULPIN  " + ulpin);
                Toast.makeText(this,"Parcel saved with ULPIN "+ulpin,Toast.LENGTH_LONG).show();
                search(ulpin);
            } else result.setText("Could not save this parcel");
            db.close();
        });

        utils.setScreenContentView(this,root);
    }

    private void addField(LinearLayout box,EditText e){box.addView(e,margin(-1,52,8,0,0,0));}
    private EditText field(String hint){EditText e=new EditText(this);e.setHint(hint);e.setTextSize(13);e.setSingleLine(true);e.setTextColor(utils.TEXT);e.setHintTextColor(utils.MUTED);e.setPadding(utils.dp(this,14),0,utils.dp(this,14),0);e.setBackgroundResource(com.acrenex.app.R.drawable.bg_acrenex_card);return e;}
    private double parse(String s,double fallback){try{return Double.parseDouble(s.trim());}catch(Exception e){return fallback;}}
    private String currentOwner(){AuthManager a=new AuthManager(this);String id=a.getUserId();if(id==null)return "AcreNex Citizen";Cursor c=null;try{c=manager.getDatabase().rawQuery("SELECT name FROM users WHERE id=?",new String[]{id});return c.moveToFirst()?c.getString(0):"AcreNex Citizen";}finally{if(c!=null)c.close();}}
    private void search(String q){results.removeAllViews();if(q.isEmpty()){Toast.makeText(this,"Enter ULPIN, owner or survey number",Toast.LENGTH_SHORT).show();return;}Cursor c=manager.searchParcels(q);try{if(!c.moveToFirst()){results.addView(utils.text(this,"No parcel found for this search.",13,utils.MUTED,false));return;}do{String ulpin=c.getString(c.getColumnIndexOrThrow("ulpin"));String owner=c.getString(c.getColumnIndexOrThrow("owner_name"));String loc=c.getString(c.getColumnIndexOrThrow("village"))+", "+c.getString(c.getColumnIndexOrThrow("district"));MaterialCardView card=utils.card(this);LinearLayout box=utils.column(this);LinearLayout top=utils.rowContainer(this);TextView u=utils.text(this,ulpin,15,utils.NAVY,true);top.addView(u,new LinearLayout.LayoutParams(0,-2,1));top.addView(utils.badge(this,"ULPIN",true));box.addView(top);utils.row(box,this,"Owner",owner);utils.row(box,this,"Location",loc);utils.row(box,this,"Survey",c.getString(c.getColumnIndexOrThrow("survey_number")));MaterialButton open=utils.outlineButton(this,"Open parcel  →");box.addView(open,margin(-1,46,4,0,0,0));card.addView(box);results.addView(card,margin(-1,-2,0,0,0,10));open.setOnClickListener(v->{Intent i=new Intent(this,ParcelDetailsActivity.class);i.putExtra("ulpin",ulpin);startActivity(i);});}while(c.moveToNext());}finally{c.close();}}
    private LinearLayout.LayoutParams margin(int w,int h,int bottom,int top,int start,int end){LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(w,h);p.bottomMargin=utils.dp(this,bottom);p.topMargin=utils.dp(this,top);p.leftMargin=utils.dp(this,start);p.rightMargin=utils.dp(this,end);return p;}
    @Override protected void onDestroy(){if(manager!=null)manager.close();super.onDestroy();}
}

package com.acrenex.app.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.acrenex.app.database.DatabaseManager;
import com.acrenex.app.utils;

/** District/command view: one ULPIN is the entry point to all connected parcel layers. */
public class OfficerDashboardActivity extends AppCompatActivity {
    private DatabaseManager manager;
    @Override protected void onCreate(Bundle b){
        super.onCreate(b); manager=new DatabaseManager(this);
        LinearLayout root=utils.screen(this,"Officer ULPIN Command Centre","One ULPIN → one parcel panel → all connected land data. Authorized officers can review/update; citizen views remain read-oriented.");
        LinearLayout search=utils.column(this); search.addView(utils.text(this,"OPEN ANY PARCEL BY ULPIN",12,utils.GREEN,true));
        LinearLayout row=new LinearLayout(this); row.setOrientation(LinearLayout.HORIZONTAL); EditText input=new EditText(this); input.setHint("Enter ULPIN"); Button open=new Button(this); open.setText("Open"); open.setAllCaps(false); row.addView(input,new LinearLayout.LayoutParams(0,utils.dp(this,54),1)); row.addView(open,new LinearLayout.LayoutParams(utils.dp(this,90),utils.dp(this,54))); search.addView(row); open.setOnClickListener(v->open(input.getText().toString().trim())); utils.addCard(root,search,this);
        LinearLayout stats=utils.column(this); stats.addView(utils.text(this,"LIVE DEMO DATASET",11,utils.GREEN,true)); stat(stats,"Unique parcels","SELECT COUNT(*) FROM parcels"); stat(stats,"Pending documents","SELECT COUNT(*) FROM documents WHERE verification_status='PENDING'"); stat(stats,"Open applications","SELECT COUNT(*) FROM applications WHERE current_status NOT IN ('Completed','Closed')"); stat(stats,"AI review signals","SELECT COUNT(*) FROM ai_alerts WHERE status <> 'Resolved'"); utils.addCard(root,stats,this);
        LinearLayout quick=utils.column(this); quick.addView(utils.text(this,"OFFICER ACTIONS",15,utils.TEXT,true)); add(quick,"GIS Map & Current Location",MapActivity.class); add(quick,"Document Checklist / Verification",DocumentActivity.class); add(quick,"Land Stack Hub",LandStackHubActivity.class); add(quick,"Audit Trail",AuditLogActivity.class); add(quick,"Department Workspaces",OfficerRoleSelectionActivity.class); utils.addCard(root,quick,this);
        root.addView(utils.text(this,"Demo note: parcel values are synthetic. Production deployment should consume authorized state/department APIs and enforce server-side role permissions.",12,utils.MUTED,false));
        setContentView((android.view.View)root.getParent());
    }
    private void open(String q){if(q.isEmpty()){android.widget.Toast.makeText(this,"Enter a ULPIN",android.widget.Toast.LENGTH_SHORT).show();return;}Cursor c=null;try{c=manager.getDatabase().rawQuery("SELECT ulpin FROM parcels WHERE ulpin=?",new String[]{q});if(c.moveToFirst()){Intent i=new Intent(this,Parcel360Activity.class);i.putExtra("ulpin",q);startActivity(i);}else android.widget.Toast.makeText(this,"ULPIN not found in demo dataset",android.widget.Toast.LENGTH_SHORT).show();}finally{if(c!=null)c.close();}}
    private void stat(LinearLayout l,String label,String sql){Cursor c=null;try{c=manager.getDatabase().rawQuery(sql,null);utils.row(l,this,label,c.moveToFirst()?String.valueOf(c.getInt(0)):"0");}finally{if(c!=null)c.close();}}
    private void add(LinearLayout root,String label,Class<?> cls){Button b=new Button(this);b.setText(label);b.setAllCaps(false);root.addView(b,new LinearLayout.LayoutParams(-1,utils.dp(this,50)));b.setOnClickListener(v->startActivity(new Intent(this,cls)));}
}

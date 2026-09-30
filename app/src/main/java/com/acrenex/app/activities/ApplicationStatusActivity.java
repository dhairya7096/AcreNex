package com.acrenex.app.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.acrenex.app.database.DatabaseManager;
import com.acrenex.app.utils;

public class ApplicationStatusActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        String id=getIntent().getStringExtra("application_id");
        LinearLayout root=utils.screen(this,"Application Status","Transparent service tracking from submission to departmental disposal.");
        DatabaseManager db=new DatabaseManager(this);
        Cursor c=db.getDatabase().rawQuery("SELECT application_id,ulpin,service_type,submission_date,current_department,current_status,progress FROM applications WHERE application_id=?",new String[]{id==null?"":id});
        try {
            if(c.moveToFirst()){
                LinearLayout box=utils.column(this);
                utils.row(box,this,"Application ID",s(c,0));utils.row(box,this,"Service",s(c,2));utils.row(box,this,"ULPIN",s(c,1));utils.row(box,this,"Submitted",s(c,3));utils.row(box,this,"Department",s(c,4));utils.row(box,this,"Current Status",s(c,5));
                ProgressBar p=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);p.setMax(100);p.setProgress(c.getInt(6));box.addView(p,new LinearLayout.LayoutParams(-1,utils.dp(this,12)));TextView percent=utils.text(this,c.getInt(6)+"% complete",14,0xff16806a,true);box.addView(percent);utils.addCard(root,box,this);
                LinearLayout steps=utils.column(this);steps.addView(utils.text(this,"✓ Application submitted",15,0xff16806a,true));steps.addView(utils.text(this,"✓ Department assigned",15,0xff16806a,true));steps.addView(utils.text(this,c.getInt(6)>=70?"✓ Record verification completed":"○ Record verification in progress",15,c.getInt(6)>=70?0xff16806a:0xffe69a24,true));steps.addView(utils.text(this,c.getInt(6)>=100?"✓ Service completed":"○ Final approval / disposal",15,c.getInt(6)>=100?0xff16806a:0xff71808f,true));utils.addCard(root,steps,this);
            } else root.addView(utils.text(this,"Application not found.",15,0xff71808f,false));
        } finally {c.close();}
        utils.setScreenContentView(this, root);
    }
    private String s(Cursor c,int i){return c.isNull(i)?"—":c.getString(i);}
}

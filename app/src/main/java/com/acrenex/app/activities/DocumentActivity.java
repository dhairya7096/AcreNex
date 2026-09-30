package com.acrenex.app.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.acrenex.app.database.DatabaseManager;
import com.acrenex.app.security.AuthManager;
import com.acrenex.app.utils;
import java.util.ArrayList;
import java.util.List;

/** Shared citizen/officer document checklist around the same ULPIN-linked records. */
public class DocumentActivity extends AppCompatActivity {
    private DatabaseManager manager; private String uploadUlpin, uploadReqId, uploadReqType; private boolean officer;
    @Override protected void onCreate(Bundle b){
        super.onCreate(b); manager=new DatabaseManager(this); AuthManager auth=new AuthManager(this); officer=isOfficer(auth.getUserId());
        LinearLayout root=utils.screen(this,"Document Centre","A-to-Z land transaction checklist. Citizen uploads are linked to the same ULPIN record officers review.");
        LinearLayout intro=utils.column(this); intro.addView(utils.text(this,"LAND PURCHASE / TRANSFER",12,utils.GREEN,true)); intro.addView(utils.text(this,"The exact requirement depends on state, land type, transaction and service. Optional rows are shown for conditional cases.",13,utils.MUTED,false)); utils.addCard(root,intro,this);
        List<String[]> parcels=new ArrayList<>(); Cursor pc=null;
        try{String sql=officer?"SELECT ulpin,owner_name,village,district FROM parcels ORDER BY district":"SELECT ulpin,owner_name,village,district FROM parcels WHERE owner_name=(SELECT name FROM users WHERE id=?) ORDER BY created_at DESC"; pc=officer?manager.getDatabase().rawQuery(sql,null):manager.getDatabase().rawQuery(sql,new String[]{auth.getUserId()}); while(pc.moveToNext())parcels.add(new String[]{pc.getString(0),pc.getString(1),pc.getString(2),pc.getString(3)});}finally{if(pc!=null)pc.close();}
        if(parcels.isEmpty()) root.addView(utils.text(this,"No ULPIN-linked parcel is available yet.",14,utils.MUTED,false));
        LinearLayout guide=utils.column(this);
        guide.addView(utils.text(this,"COMPREHENSIVE LAND DOCUMENT CHECKLIST",15,utils.TEXT,true));
        guide.addView(utils.text(this,"Identity, title, revenue records, transaction, tax, survey map and conditional approvals are shown together. Exact requirements vary by state, land type and service.",12,utils.MUTED,false));
        utils.addCard(root,guide,this);
        for(String[] p:parcels) addParcel(root,p);
        setContentView((View)root.getParent());
    }
    private void addParcel(LinearLayout root,String[] p){
        LinearLayout card=utils.column(this); card.addView(utils.text(this,"ULPIN  "+p[0],16,utils.TEXT,true)); card.addView(utils.text(this,p[1]+" • "+p[2]+", "+p[3],12,utils.MUTED,false));
        Cursor req=null; try{req=manager.getDatabase().rawQuery("SELECT id,service_code,document_type,description,required FROM document_requirements ORDER BY service_code,order_no",null); String currentService=""; while(req.moveToNext()){
            String id=req.getString(0), service=req.getString(1), type=req.getString(2), desc=req.getString(3); int required=req.getInt(4); if(!service.equals(currentService)){ currentService=service; TextView serviceHeader=utils.text(this,service.replace("_"," ")+" DOCUMENTS",12,utils.BLUE,true); serviceHeader.setPadding(utils.dp(this,4),utils.dp(this,12),0,utils.dp(this,6)); card.addView(serviceHeader); } String status=findStatus(p[0],id); String docName=findName(p[0],id);
            LinearLayout row=utils.rowContainer(this); LinearLayout text=utils.column(this); text.addView(utils.text(this,type+(required==1?" *":""),14,utils.TEXT,true)); text.addView(utils.text(this,desc,11,utils.MUTED,false)); text.addView(utils.text(this,status+(docName.isEmpty()?"":" • "+docName),11,status.equals("VERIFIED")?utils.GREEN:(status.equals("UPLOADED")?utils.BLUE:utils.WARNING),true)); row.addView(text,new LinearLayout.LayoutParams(0,-2,1));
            Button action=new Button(this); action.setAllCaps(false); action.setText(status.equals("PENDING")?"Upload":"View / Update"); row.addView(action,new LinearLayout.LayoutParams(utils.dp(this,110),utils.dp(this,48))); action.setOnClickListener(v->openPicker(p[0],id,type));
            utils.addCard(card,row,this);
        }}finally{if(req!=null)req.close();}
        utils.addCard(root,card,this);
    }
    private String findStatus(String ulpin,String reqId){Cursor c=null;try{c=manager.getDatabase().rawQuery("SELECT verification_status FROM documents WHERE ulpin=? AND document_id=?",new String[]{ulpin,reqId+"-"+ulpin});if(c.moveToFirst())return c.getString(0);return "PENDING";}finally{if(c!=null)c.close();}}
    private String findName(String ulpin,String reqId){Cursor c=null;try{c=manager.getDatabase().rawQuery("SELECT document_name FROM documents WHERE ulpin=? AND document_id=?",new String[]{ulpin,reqId+"-"+ulpin});if(c.moveToFirst())return c.getString(0);return "";}finally{if(c!=null)c.close();}}
    private void openPicker(String ulpin,String reqId,String type){uploadUlpin=ulpin;uploadReqId=reqId;uploadReqType=type;Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.setType("application/pdf");i.addCategory(Intent.CATEGORY_OPENABLE);startActivityForResult(i,7001);}
    @Override protected void onActivityResult(int requestCode,int resultCode,Intent data){super.onActivityResult(requestCode,resultCode,data);if(requestCode!=7001||resultCode!=RESULT_OK||data==null||data.getData()==null)return;Uri uri=data.getData();String name=fileName(uri);manager.getDatabase().execSQL("INSERT OR REPLACE INTO documents(document_id,ulpin,document_type,document_name,uploaded_date,extracted_text,verification_status,confidence_score) VALUES(?,?,?,?,datetime('now'),?,?,?)",new Object[]{uploadReqId+"-"+uploadUlpin,uploadUlpin,uploadReqType,name,"Citizen selected local demo file","UPLOADED",0.0});Toast.makeText(this,"Document linked to ULPIN: "+uploadUlpin,Toast.LENGTH_LONG).show();recreate();}
    private String fileName(Uri uri){Cursor c=null;try{c=getContentResolver().query(uri,null,null,null,null);if(c!=null&&c.moveToFirst()){int i=c.getColumnIndex(OpenableColumns.DISPLAY_NAME);if(i>=0)return c.getString(i);}}catch(Exception ignored){}finally{if(c!=null)c.close();}return "Uploaded document";}
    private boolean isOfficer(String id){if(id==null)return false;Cursor c=null;try{c=manager.getDatabase().rawQuery("SELECT user_type,role FROM users WHERE id=?",new String[]{id});if(!c.moveToFirst())return false;String u=c.getString(0),r=c.getString(1);return "OFFICER".equalsIgnoreCase(u)||(!"CITIZEN".equalsIgnoreCase(r)&&r!=null&&r.length()>0);}catch(Exception e){return false;}finally{if(c!=null)c.close();}}
}

package com.acrenex.app.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.acrenex.app.database.DatabaseManager;
import com.acrenex.app.utils;

public class LandRecordsActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        String ulpin = getIntent().getStringExtra("ulpin");
        if (ulpin == null || ulpin.trim().isEmpty()) ulpin = "24-GJ-GN-0001-00001";
        DatabaseManager db = new DatabaseManager(this);
        LinearLayout root = utils.screen(this, "Land Records", "Registration, tax, land-use, permission and encumbrance intelligence.");
        addRegistration(root, db, ulpin);
        addTax(root, db, ulpin);
        addLandUse(root, db, ulpin);
        addPermission(root, db, ulpin);
        addEncumbrance(root, db, ulpin);
        Button owner = new Button(this);
        owner.setText("View Ownership Details");
        root.addView(owner);
        final String selected = ulpin;
        owner.setOnClickListener(v -> { android.content.Intent i=new android.content.Intent(this,OwnershipDetailsActivity.class); i.putExtra("ulpin",selected); startActivity(i); });
        utils.setScreenContentView(this, root);
    }
    private void addRegistration(LinearLayout root,DatabaseManager db,String u){Cursor c=db.getDatabase().rawQuery("SELECT document_number,registration_date,transaction_type,buyer_name,seller_name,status FROM registration_records WHERE ulpin=?",new String[]{u});try{if(c.moveToFirst()){LinearLayout b=utils.column(this);utils.row(b,this,"Document Number",s(c,0));utils.row(b,this,"Registration Date",s(c,1));utils.row(b,this,"Transaction",s(c,2));utils.row(b,this,"Buyer",s(c,3));utils.row(b,this,"Seller",s(c,4));utils.row(b,this,"Status",s(c,5));utils.addCard(root,b,this);}}finally{c.close();}}
    private void addTax(LinearLayout root,DatabaseManager db,String u){Cursor c=db.getDatabase().rawQuery("SELECT annual_tax,outstanding_amount,financial_year,last_payment_date,status FROM tax_records WHERE ulpin=?",new String[]{u});try{if(c.moveToFirst()){LinearLayout b=utils.column(this);utils.row(b,this,"Annual Tax","₹ "+c.getDouble(0));utils.row(b,this,"Outstanding","₹ "+c.getDouble(1));utils.row(b,this,"Financial Year",s(c,2));utils.row(b,this,"Last Payment",s(c,3));utils.row(b,this,"Status",s(c,4));utils.addCard(root,b,this);}}finally{c.close();}}
    private void addLandUse(LinearLayout root,DatabaseManager db,String u){Cursor c=db.getDatabase().rawQuery("SELECT land_use,zoning,master_plan_zone,effective_from,status FROM land_use_records WHERE ulpin=?",new String[]{u});try{if(c.moveToFirst()){LinearLayout b=utils.column(this);utils.row(b,this,"Land Use",s(c,0));utils.row(b,this,"Zoning",s(c,1));utils.row(b,this,"Master Plan Zone",s(c,2));utils.row(b,this,"Effective From",s(c,3));utils.row(b,this,"Status",s(c,4));utils.addCard(root,b,this);}}finally{c.close();}}
    private void addPermission(LinearLayout root,DatabaseManager db,String u){Cursor c=db.getDatabase().rawQuery("SELECT permission_number,application_date,approval_date,building_type,status FROM building_permissions WHERE ulpin=?",new String[]{u});try{if(c.moveToFirst()){LinearLayout b=utils.column(this);utils.row(b,this,"Permission No.",s(c,0));utils.row(b,this,"Application",s(c,1));utils.row(b,this,"Approval",s(c,2));utils.row(b,this,"Building Type",s(c,3));utils.row(b,this,"Status",s(c,4));utils.addCard(root,b,this);}else{LinearLayout b=utils.column(this);b.addView(utils.text(this,"Building permission",16,0xff123b5d,true));b.addView(utils.text(this,"No permission record is currently linked to this parcel.",14,0xff71808f,false));utils.addCard(root,b,this);}}finally{c.close();}}
    private void addEncumbrance(LinearLayout root,DatabaseManager db,String u){Cursor c=db.getDatabase().rawQuery("SELECT type,institution,amount,start_date,end_date,status FROM encumbrances WHERE ulpin=?",new String[]{u});try{if(c.moveToFirst()){LinearLayout b=utils.column(this);utils.row(b,this,"Type",s(c,0));utils.row(b,this,"Institution",s(c,1));utils.row(b,this,"Amount","₹ "+c.getDouble(2));utils.row(b,this,"Start",s(c,3));utils.row(b,this,"End",s(c,4));utils.row(b,this,"Status",s(c,5));utils.addCard(root,b,this);}else{LinearLayout b=utils.column(this);b.addView(utils.text(this,"Encumbrance Check",16,0xff123b5d,true));b.addView(utils.text(this,"No linked encumbrance record found.",14,0xff16806a,false));utils.addCard(root,b,this);}}finally{c.close();}}
    private String s(Cursor c,int i){return c.isNull(i)?"—":c.getString(i);}
}

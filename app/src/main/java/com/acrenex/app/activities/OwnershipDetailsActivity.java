package com.acrenex.app.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.acrenex.app.database.DatabaseManager;
import com.acrenex.app.utils;

public class OwnershipDetailsActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        String ulpin = getIntent().getStringExtra("ulpin");
        if (ulpin == null || ulpin.trim().isEmpty()) ulpin = "24-GJ-GN-0001-00001";
        DatabaseManager db = new DatabaseManager(this);
        LinearLayout root = utils.screen(this, "Ownership Details", "Record of Rights and ownership verification for the selected parcel.");
        Cursor c = db.getDatabase().rawQuery("SELECT owner_name,ownership_type,share,record_date,source,verification_status FROM ownership_records WHERE ulpin=? ORDER BY record_date DESC", new String[]{ulpin});
        try {
            if (c.moveToFirst()) {
                do {
                    LinearLayout box = utils.column(this);
                    utils.row(box,this,"ULPIN",ulpin);
                    utils.row(box,this,"Recorded Owner",s(c,0));
                    utils.row(box,this,"Ownership Type",s(c,1));
                    utils.row(box,this,"Share",s(c,2));
                    utils.row(box,this,"Record Date",s(c,3));
                    utils.row(box,this,"Source",s(c,4));
                    utils.row(box,this,"Verification",s(c,5));
                    utils.addCard(root,box,this);
                } while(c.moveToNext());
            } else root.addView(utils.text(this,"No ownership record found for this ULPIN.",15,0xff71808f,false));
        } finally { c.close(); }
        Button landRecords = new Button(this);
        landRecords.setText("View Land Records");
        root.addView(landRecords);
        final String selected = ulpin;
        landRecords.setOnClickListener(v -> {
            android.content.Intent i = new android.content.Intent(this, LandRecordsActivity.class);
            i.putExtra("ulpin", selected);
            startActivity(i);
        });
        utils.setScreenContentView(this, root);
    }
    private String s(Cursor c,int i){return c.isNull(i)?"—":c.getString(i);}
}

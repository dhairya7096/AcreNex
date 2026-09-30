package com.acrenex.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.acrenex.app.R;
import com.acrenex.app.database.DatabaseManager;
import com.acrenex.app.database.DemoDataSeeder;
import android.database.Cursor;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import com.acrenex.app.security.AuthManager;
import com.acrenex.app.security.SecurityUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ProofVerificationActivity extends AppCompatActivity {
    private TextInputEditText etProofId; private MaterialButton btnVerify; private TextView tvSecurityStatus; private AuthManager authManager;
    @Override protected void onCreate(Bundle b){super.onCreate(b);if(getSupportActionBar()!=null)getSupportActionBar().hide();setContentView(R.layout.activity_proof_verification);authManager=new AuthManager(this);etProofId=findViewById(R.id.etProofId);btnVerify=findViewById(R.id.btnVerify);tvSecurityStatus=findViewById(R.id.tvSecurityStatus);btnVerify.setOnClickListener(v->verifyProof());}
    private void verifyProof(){String proof=etProofId.getText()==null?"":etProofId.getText().toString().trim();if(proof.length()<4){etProofId.setError("Enter a valid demo Proof ID");return;}if(SecurityUtils.sha256(proof).isEmpty()){Toast.makeText(this,"Verification failed",Toast.LENGTH_SHORT).show();return;}authManager.markProofVerified();ensureUserData(); DemoDataSeeder.seedExtended(this, authManager.getUserId(), currentUserName());tvSecurityStatus.setText("✓ Proof ID Verified");tvSecurityStatus.setTextColor(getColor(R.color.verified));startActivity(new Intent(this,VerificationSuccessActivity.class));finish();}
    private void ensureUserData() {
        String userId = authManager.getUserId();
        if (userId == null || userId.trim().isEmpty()) return;
        DatabaseManager m = new DatabaseManager(this);
        String name = "AcreNex Citizen";
        Cursor u = m.getDatabase().rawQuery("SELECT name FROM users WHERE id=?", new String[]{userId});
        try { if (u.moveToFirst() && u.getString(0) != null) name = u.getString(0); } finally { u.close(); }
        m.getDatabase().execSQL("UPDATE users SET proof_verified=1 WHERE id=?", new Object[]{userId});
        Cursor p = m.getDatabase().rawQuery("SELECT COUNT(*) FROM parcels WHERE owner_name=?", new String[]{name});
        try { if (p.moveToFirst() && p.getInt(0) > 0) return; } finally { p.close(); }
        m.insertParcel("24-GJ-GN-0001-00001", "GN-101", "Gujarat", "Gandhinagar", "Gandhinagar", "Demo Village", name, "2.50", "Acres", "Residential", "Residential", "R1", "Verified", "Clear", "Paid", "Approved", 23.2156, 72.6369);
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date());
        android.database.sqlite.SQLiteDatabase db=m.getDatabase();
        db.execSQL("INSERT OR REPLACE INTO ownership_records(id,ulpin,owner_name,ownership_type,share,record_date,source,verification_status) VALUES(?,?,?,?,?,?,?,?)",new Object[]{"OWN-0001","24-GJ-GN-0001-00001",name,"Freehold","100%",now,"Demo RoR","VERIFIED"});
        db.execSQL("INSERT OR REPLACE INTO registration_records(registration_id,ulpin,document_number,registration_date,transaction_type,buyer_name,seller_name,status) VALUES(?,?,?,?,?,?,?,?)",new Object[]{"REG-0001","24-GJ-GN-0001-00001","REG-2026-0001",now,"Sale",name,"Demo Seller","Approved"});
        db.execSQL("INSERT OR REPLACE INTO tax_records(id,ulpin,annual_tax,outstanding_amount,financial_year,last_payment_date,status) VALUES(?,?,?,?,?,?,?)",new Object[]{"TAX-0001","24-GJ-GN-0001-00001",4200,0,"2026-27",now,"Paid"});
        db.execSQL("INSERT OR REPLACE INTO land_use_records(id,ulpin,land_use,zoning,master_plan_zone,effective_from,status) VALUES(?,?,?,?,?,?,?)",new Object[]{"LU-0001","24-GJ-GN-0001-00001","Residential","R1","Residential Zone",now,"Active"});
        db.execSQL("INSERT OR REPLACE INTO documents(document_id,ulpin,document_type,document_name,uploaded_date,extracted_text,verification_status,confidence_score) VALUES(?,?,?,?,?,?,?,?)",new Object[]{"DOC-0001","24-GJ-GN-0001-00001","Record of Rights","RoR_0001.pdf",now,"Demo land record","Verified",0.98});
        db.execSQL("INSERT OR REPLACE INTO applications(application_id,ulpin,applicant_id,service_type,submission_date,current_department,current_status,progress) VALUES(?,?,?,?,?,?,?,?)",new Object[]{"APP-0001","24-GJ-GN-0001-00001",userId,"Land Record Certificate",now,"Revenue Department","In Review",65});
        db.execSQL("INSERT INTO notifications(user_id,title,message,type,is_read,created_at) VALUES(?,?,?,?,?,?)",new Object[]{userId,"Welcome to AcreNex","Your land intelligence workspace is ready.","SYSTEM",0,now});
    }

    private String currentUserName(){ DatabaseManager m=new DatabaseManager(this); Cursor c=m.getDatabase().rawQuery("SELECT name FROM users WHERE id=?",new String[]{authManager.getUserId()}); try{return c.moveToFirst()?c.getString(0):"AcreNex Citizen";}finally{c.close();} }

}


package com.acrenex.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.database.Cursor;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.acrenex.app.ai.AIAssistantManager;
import com.acrenex.app.ai.ChangeDetectionManager;
import com.acrenex.app.ai.OCRManager;
import com.acrenex.app.ai.MatchingEngine;
import com.acrenex.app.ai.LandStackAIEngine;
import com.acrenex.app.ai.RiskEngine;
import com.acrenex.app.database.DatabaseManager;
import com.acrenex.app.security.AuthManager;
import com.acrenex.app.utils;

public class AIAssistantActivity extends AppCompatActivity {
    private static final int PICK_DOCUMENT_IMAGE = 7101;
    private OCRManager ocrManager;
    private TextView result;
    private String primaryUlpin = "24-GJ-GN-0001-00001";

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        LinearLayout root=utils.screen(this,"AI Land Assistant","Explainable, record-grounded intelligence for citizens and land officers.");
        LinearLayout intro=utils.column(this);
        intro.addView(utils.text(this,"AcreNex Intelligence Layer",20,0xff123b5d,true));
        intro.addView(utils.text(this,"AI features use the parcel records available in the prototype. Production models can be connected to authorized departmental data services without changing the citizen-facing flow.",14,0xff71808f,false));
        utils.addCard(root,intro,this);

        LinearLayout ask=utils.column(this);
        ask.addView(utils.text(this,"1. Ask AcreNex AI",18,0xff123b5d,true));
        android.widget.EditText question=new android.widget.EditText(this);
        question.setHint("e.g. What is the tax status of my parcel?");
        ask.addView(question,new LinearLayout.LayoutParams(-1,utils.dp(this,58)));
        Button askButton=new Button(this);askButton.setText("Ask AI");ask.addView(askButton);
        root.addView(ask);

        LinearLayout risk=utils.column(this);
        risk.addView(utils.text(this,"2. AI Record Health & Risk Scan",18,0xff123b5d,true));
        risk.addView(utils.text(this,"Checks ownership, registration, tax, encumbrance and land-use signals and produces an explainable risk score.",14,0xff71808f,false));
        Button riskButton=new Button(this);riskButton.setText("Run Risk Scan");risk.addView(riskButton);root.addView(risk);

        LinearLayout ocr=utils.column(this);
        ocr.addView(utils.text(this,"3. AI Document OCR",18,0xff123b5d,true));
        ocr.addView(utils.text(this,"Uses on-device ML Kit text recognition to extract text from a selected land-document image.",14,0xff71808f,false));
        Button ocrButton=new Button(this);ocrButton.setText("Scan Document Image");ocr.addView(ocrButton);root.addView(ocr);

        LinearLayout match=utils.column(this);
        match.addView(utils.text(this,"4. AI Document ↔ Parcel Consistency",18,0xff123b5d,true));
        match.addView(utils.text(this,"Compares OCR-extracted owner, area and ULPIN fields with the selected parcel and returns a transparent confidence score.",14,0xff71808f,false));
        Button matchButton=new Button(this);matchButton.setText("Run Consistency Check");match.addView(matchButton);root.addView(match);

        LinearLayout change=utils.column(this);
        change.addView(utils.text(this,"5. AI Change Detection",18,0xff123b5d,true));
        change.addView(utils.text(this,"Backend-ready module for comparing validated GIS/satellite features and flagging significant parcel changes for officer review.",14,0xff71808f,false));
        Button changeButton=new Button(this);changeButton.setText("Run Demo Change Analysis");change.addView(changeButton);root.addView(change);

        result=utils.text(this,"AI results will appear here.",15,0xff183b56,false);
        utils.addCard(root,result,this);
        utils.setScreenContentView(this, root);
        ocrManager=new OCRManager(this);

        askButton.setOnClickListener(v -> ask(question.getText()==null?"":question.getText().toString()));
        riskButton.setOnClickListener(v -> runRisk());
        ocrButton.setOnClickListener(v -> {Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.setType("image/*");i.addCategory(Intent.CATEGORY_OPENABLE);startActivityForResult(i,PICK_DOCUMENT_IMAGE);});
        matchButton.setOnClickListener(v -> { MatchingEngine.MatchResult mr=new MatchingEngine().compare("Demo Citizen","Demo Citizen","2.50 Acres","2.50 Acres",primaryUlpin,primaryUlpin); result.setText("AI Document ↔ Parcel Consistency\n\nOwner match: "+mr.isOwnerMatch()+"\nArea match: "+mr.isAreaMatch()+"\nULPIN match: "+mr.isUlpinMatch()+"\nConfidence: "+mr.getConfidence()+"%\n\nThis is a transparent prototype comparison. Production validation must use authorized source records."); });
        changeButton.setOnClickListener(v -> {ChangeDetectionManager.ChangeResult r=new ChangeDetectionManager().evaluate(12.0);result.setText("AI Change Detection\n\nDemo feature change: "+r.getChangePercentage()+"%\nStatus: "+(r.isChanged()?"Review required":"No significant change")+"\n\n"+r.getDescription()+"\n\nNote: production deployment should use validated GIS/satellite inputs from an authorized backend.");});
    }

    private void ask(String q){
        if(q.trim().isEmpty()){result.setText("Please enter a question.");return;}
        DatabaseManager db=new DatabaseManager(this);Cursor c=db.getParcel(primaryUlpin);
        String owner="",area="",landUse="",zoning="",tax="",registration="",enc="";
        try{if(c.moveToFirst()){owner=s(c,"owner_name");area=s(c,"area")+" "+s(c,"area_unit");landUse=s(c,"land_use");zoning=s(c,"zoning");tax=s(c,"tax_status");registration=s(c,"registration_status");enc=s(c,"encumbrance_status");}}finally{c.close();}
        String answer=new AIAssistantManager().answer(q,owner,area,landUse,zoning,tax,registration,enc);
        result.setText("AcreNex AI\n\n"+answer+"\n\nSource: local parcel record • ULPIN: "+primaryUlpin);
        db.addAuditLog(new AuthManager(this).getUserId(),new AuthManager(this).getRole(),"AI_QUERY","PARCEL",primaryUlpin,new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss",java.util.Locale.US).format(new java.util.Date()),"SUCCESS");
    }

    private void runRisk(){
        DatabaseManager db=new DatabaseManager(this);
        LandStackAIEngine.Analysis a=LandStackAIEngine.analyze(db.getDatabase(),primaryUlpin);
        result.setText("AcreNex AI • Land Guardian\n\nGovernance confidence: "+a.score+" / 100\nRisk level: "+a.risk+"\n\n"+a.summary+"\n\nNext action: "+a.nextAction+"\n\nThis explainable engine uses only parcel-linked records available to AcreNex; it does not invent government facts. Final decisions remain with authorized officers.");
        db.addAuditLog(new AuthManager(this).getUserId(),new AuthManager(this).getRole(),"AI_RISK_SCAN","PARCEL",primaryUlpin,new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss",java.util.Locale.US).format(new java.util.Date()),a.risk+"_"+a.score);
        db.close();
    }

    @Override protected void onActivityResult(int requestCode,int resultCode,Intent data){super.onActivityResult(requestCode,resultCode,data);if(requestCode==PICK_DOCUMENT_IMAGE&&resultCode==Activity.RESULT_OK&&data!=null&&data.getData()!=null){Uri uri=data.getData();result.setText("AI OCR is processing the document…");ocrManager.extractText(uri,new OCRManager.OCRCallback(){public void onSuccess(String text){
                    DatabaseManager auditDb=new DatabaseManager(AIAssistantActivity.this);
                    auditDb.addAuditLog(new AuthManager(AIAssistantActivity.this).getUserId(),new AuthManager(AIAssistantActivity.this).getRole(),"AI_OCR_SCAN","DOCUMENT",primaryUlpin,new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss",java.util.Locale.US).format(new java.util.Date()),"SUCCESS");
                    String ulpin=ocrManager.extractField(text,"ULPIN");String owner=ocrManager.extractField(text,"Owner");result.setText("AI Document OCR\n\nExtracted text:\n"+text+"\n\nDetected ULPIN: "+(ulpin.isEmpty()?"Not found":ulpin)+"\nDetected Owner: "+(owner.isEmpty()?"Not found":owner));}public void onError(String message){result.setText("AI OCR could not process this image. Please select a clear document image.");Toast.makeText(AIAssistantActivity.this,message==null?"OCR failed":message,Toast.LENGTH_SHORT).show();}});}}

    @Override protected void onDestroy(){if(ocrManager!=null)ocrManager.close();super.onDestroy();}
    private String s(Cursor c,String n){int i=c.getColumnIndex(n);return i>=0&&!c.isNull(i)?c.getString(i):"—";}
}

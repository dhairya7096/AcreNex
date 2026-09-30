package com.acrenex.app.ai;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Locale;

/**
 * AcreNex's local, explainable Land Stack reasoning layer.
 * It does not invent records or call an external AI service. It scores only
 * fields available in the parcel-centric database and exposes the reasons.
 * Production can replace/augment this engine with an authorized ML service.
 */
public final class LandStackAIEngine {
    private LandStackAIEngine() {}

    public static Analysis analyze(SQLiteDatabase db, String ulpin) {
        Analysis a = new Analysis();
        if (db == null || ulpin == null || ulpin.trim().isEmpty()) {
            a.summary = "Select a parcel to run AcreNex AI."; return a;
        }
        Cursor p = null;
        try {
            p = db.rawQuery("SELECT * FROM parcels WHERE ulpin=?", new String[]{ulpin});
            if (!p.moveToFirst()) { a.summary="No parcel record is available for this ULPIN."; return a; }
            String registration = val(p,"registration_status");
            String tax = val(p,"tax_status");
            String landUse = val(p,"land_use");
            String zoning = val(p,"zoning");
            String enc = val(p,"encumbrance_status");
            String building = val(p,"building_permission_status");
            String owner = val(p,"owner_name");

            boolean ownership = exists(db,"ownership_records",ulpin,"verification_status","VERIFIED");
            boolean reg = contains(registration,"VERIF","APPROV","COMPLET");
            boolean taxOk = contains(tax,"PAID","CLEAR","COMPLET");
            boolean encClear = contains(enc,"CLEAR","NONE","NO ");
            boolean planning = !landUse.isEmpty() && !zoning.isEmpty();
            boolean buildingOk = contains(building,"APPROV","NOT REQUIRED","CLEAR");
            boolean docs = count(db,"documents",ulpin) > 0;
            boolean restriction = restrictionClear(db,ulpin);

            a.score = 0;
            if (ownership) a.score += 18; else a.reasons += "Ownership verification is pending. ";
            if (reg) a.score += 14; else a.reasons += "Registration status needs review. ";
            if (taxOk) a.score += 10; else a.reasons += "Tax status needs attention. ";
            if (encClear) a.score += 14; else a.reasons += "An encumbrance signal requires review. ";
            if (planning) a.score += 12; else a.reasons += "Land-use/zoning linkage is incomplete. ";
            if (buildingOk) a.score += 10; else a.reasons += "Building-permission status should be checked. ";
            if (docs) a.score += 10; else a.reasons += "No linked document is available. ";
            if (restriction) a.score += 12; else a.reasons += "A restriction/environment layer needs review. ";

            a.risk = a.score >= 85 ? "LOW" : a.score >= 65 ? "MODERATE" : "HIGH";
            a.owner = owner; a.ulpin=ulpin;
            if (a.reasons.isEmpty()) a.reasons="Core parcel governance signals are internally consistent in the available demo dataset.";
            a.summary = String.format(Locale.US, "AI governance confidence %d/100 • %s risk. %s", a.score, a.risk, a.reasons.trim());
            a.nextAction = a.risk.equals("LOW") ? "Suitable for routine review." : "Route flagged signals to the responsible department for verification.";
            return a;
        } finally { if (p != null) p.close(); }
    }

    private static boolean exists(SQLiteDatabase db,String table,String ulpin,String col,String expected){
        Cursor c=null; try { c=db.rawQuery("SELECT COUNT(*) FROM "+table+" WHERE ulpin=? AND UPPER("+col+")=?",new String[]{ulpin,expected}); return c.moveToFirst()&&c.getInt(0)>0; } catch(Exception e){return false;} finally{if(c!=null)c.close();}
    }
    private static int count(SQLiteDatabase db,String table,String ulpin){Cursor c=null;try{c=db.rawQuery("SELECT COUNT(*) FROM "+table+" WHERE ulpin=?",new String[]{ulpin});return c.moveToFirst()?c.getInt(0):0;}catch(Exception e){return 0;}finally{if(c!=null)c.close();}}
    private static boolean restrictionClear(SQLiteDatabase db,String ulpin){Cursor c=null;try{c=db.rawQuery("SELECT COUNT(*) FROM restrictions WHERE ulpin=? AND UPPER(status) NOT IN ('ACTIVE','HIGH')",new String[]{ulpin});return c.moveToFirst()&&c.getInt(0)>0;}catch(Exception e){return true;}finally{if(c!=null)c.close();}}
    private static boolean contains(String value,String... terms){String s=value==null?"":value.toUpperCase(Locale.US);for(String t:terms)if(s.contains(t))return true;return false;}
    private static String val(Cursor c,String n){int i=c.getColumnIndex(n);return i>=0&&!c.isNull(i)?c.getString(i):"";}

    public static class Analysis {
        public String ulpin="", owner="", risk="UNKNOWN", summary="", reasons="", nextAction=""; public int score;
    }
}

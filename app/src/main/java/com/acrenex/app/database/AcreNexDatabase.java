package com.acrenex.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Local-first Land Stack prototype database.
 * Production deployments should replace demo persistence with authorized
 * state/department services while preserving the parcel-centric schema.
 *
 * Version 5 hardens upgrades: every expected table is created with IF NOT EXISTS
 * during both first install and upgrade, so an older local database cannot crash
 * when a newer screen opens a newly introduced module.
 */
public class AcreNexDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "acrenex_local.db";
    private static final int DATABASE_VERSION = 5;

    public AcreNexDatabase(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        createSchema(db);
        createIndexes(db);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Never drop user/demo data during prototype upgrades. Missing tables are
        // added idempotently so an installation from V3/V4 can open V5 safely.
        createSchema(db);
        createIndexes(db);
    }

    private void createSchema(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS users (id TEXT PRIMARY KEY NOT NULL,name TEXT NOT NULL,email TEXT UNIQUE NOT NULL,mobile TEXT,role TEXT NOT NULL,proof_verified INTEGER DEFAULT 0,created_at TEXT,user_type TEXT DEFAULT 'CITIZEN',department TEXT,password_hash TEXT,password_salt TEXT,last_login TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS parcels (ulpin TEXT PRIMARY KEY NOT NULL,survey_number TEXT,state TEXT,district TEXT,taluka TEXT,village TEXT,owner_name TEXT,area TEXT,area_unit TEXT,land_type TEXT,land_use TEXT,zoning TEXT,registration_status TEXT,encumbrance_status TEXT,tax_status TEXT,building_permission_status TEXT,latitude REAL,longitude REAL,created_at TEXT,updated_at TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS ownership_records (id TEXT PRIMARY KEY NOT NULL,ulpin TEXT NOT NULL,owner_name TEXT,ownership_type TEXT,share TEXT,record_date TEXT,source TEXT,verification_status TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS registration_records (registration_id TEXT PRIMARY KEY NOT NULL,ulpin TEXT NOT NULL,document_number TEXT,registration_date TEXT,transaction_type TEXT,buyer_name TEXT,seller_name TEXT,status TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS tax_records (id TEXT PRIMARY KEY NOT NULL,ulpin TEXT NOT NULL,annual_tax REAL DEFAULT 0,outstanding_amount REAL DEFAULT 0,financial_year TEXT,last_payment_date TEXT,status TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS land_use_records (id TEXT PRIMARY KEY NOT NULL,ulpin TEXT NOT NULL,land_use TEXT,zoning TEXT,master_plan_zone TEXT,effective_from TEXT,status TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS building_permissions (id TEXT PRIMARY KEY NOT NULL,ulpin TEXT NOT NULL,permission_number TEXT,application_date TEXT,approval_date TEXT,building_type TEXT,status TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS encumbrances (id TEXT PRIMARY KEY NOT NULL,ulpin TEXT NOT NULL,type TEXT,institution TEXT,amount REAL DEFAULT 0,start_date TEXT,end_date TEXT,status TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS documents (document_id TEXT PRIMARY KEY NOT NULL,ulpin TEXT,document_type TEXT,document_name TEXT,uploaded_date TEXT,extracted_text TEXT,verification_status TEXT,confidence_score REAL DEFAULT 0)");
        db.execSQL("CREATE TABLE IF NOT EXISTS ai_alerts (alert_id TEXT PRIMARY KEY NOT NULL,ulpin TEXT NOT NULL,alert_type TEXT,description TEXT,severity TEXT,risk_score INTEGER DEFAULT 0,detected_date TEXT,status TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS applications (application_id TEXT PRIMARY KEY NOT NULL,ulpin TEXT,applicant_id TEXT,service_type TEXT,submission_date TEXT,current_department TEXT,current_status TEXT,progress INTEGER DEFAULT 0)");
        db.execSQL("CREATE TABLE IF NOT EXISTS audit_logs (id INTEGER PRIMARY KEY AUTOINCREMENT,user_id TEXT,role TEXT,action TEXT NOT NULL,resource_type TEXT,resource_id TEXT,timestamp TEXT,result TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS notifications (id INTEGER PRIMARY KEY AUTOINCREMENT,user_id TEXT,title TEXT,message TEXT,type TEXT,is_read INTEGER DEFAULT 0,created_at TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS parcel_vertices (ulpin TEXT NOT NULL,vertex_order INTEGER NOT NULL,latitude REAL NOT NULL,longitude REAL NOT NULL,PRIMARY KEY(ulpin,vertex_order))");
        db.execSQL("CREATE TABLE IF NOT EXISTS document_requirements (id TEXT PRIMARY KEY NOT NULL,service_code TEXT,document_type TEXT,description TEXT,required INTEGER DEFAULT 1,order_no INTEGER DEFAULT 0)");
        db.execSQL("CREATE TABLE IF NOT EXISTS restrictions (id TEXT PRIMARY KEY NOT NULL,ulpin TEXT NOT NULL,restriction_type TEXT,authority TEXT,description TEXT,severity TEXT,status TEXT,effective_from TEXT,effective_to TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS utilities (id TEXT PRIMARY KEY NOT NULL,ulpin TEXT NOT NULL,utility_type TEXT,provider TEXT,connection_status TEXT,network_ref TEXT,last_verified TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS valuation_records (id TEXT PRIMARY KEY NOT NULL,ulpin TEXT NOT NULL,guideline_value REAL,market_reference REAL,unit TEXT,valuation_year TEXT,source TEXT,status TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS environmental_zones (id TEXT PRIMARY KEY NOT NULL,ulpin TEXT NOT NULL,zone_type TEXT,authority TEXT,buffer_meters REAL,impact_level TEXT,status TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS disputes (id TEXT PRIMARY KEY NOT NULL,ulpin TEXT NOT NULL,case_number TEXT,forum TEXT,dispute_type TEXT,filing_date TEXT,status TEXT,next_hearing TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS service_requests (id TEXT PRIMARY KEY NOT NULL,ulpin TEXT,applicant_id TEXT,service_code TEXT,service_name TEXT,department TEXT,status TEXT,submitted_at TEXT,updated_at TEXT,remarks TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS gis_layers (id TEXT PRIMARY KEY NOT NULL,layer_code TEXT UNIQUE,layer_name TEXT,category TEXT,source_department TEXT,standard TEXT,version TEXT,enabled INTEGER DEFAULT 1,access_scope TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS api_registry (id TEXT PRIMARY KEY NOT NULL,api_name TEXT,department TEXT,endpoint TEXT,method TEXT,version TEXT,auth_scheme TEXT,status TEXT,last_sync TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS state_config (state_code TEXT PRIMARY KEY NOT NULL,state_name TEXT,language TEXT,area_unit TEXT,ulppin_format TEXT,record_portal TEXT,workflow_profile TEXT,active INTEGER DEFAULT 1)");
        db.execSQL("CREATE TABLE IF NOT EXISTS data_quality (id TEXT PRIMARY KEY NOT NULL,ulpin TEXT NOT NULL,completeness REAL,consistency REAL,spatial_accuracy REAL,timeliness REAL,last_checked TEXT,status TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS change_events (id TEXT PRIMARY KEY NOT NULL,ulpin TEXT NOT NULL,baseline_date TEXT,comparison_date TEXT,change_percent REAL,change_type TEXT,confidence REAL,status TEXT,review_required INTEGER DEFAULT 0)");

        // V3/V4 installations may already have the users table without these fields.
        addColumnIfMissing(db,"users","user_type","TEXT DEFAULT 'CITIZEN'");
        addColumnIfMissing(db,"users","department","TEXT");
        addColumnIfMissing(db,"users","password_hash","TEXT");
        addColumnIfMissing(db,"users","password_salt","TEXT");
        addColumnIfMissing(db,"users","last_login","TEXT");
    }

    private void createIndexes(SQLiteDatabase db) {
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_parcel_owner ON parcels(owner_name)");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_parcel_district ON parcels(district)");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_ownership_ulpin ON ownership_records(ulpin)");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_registration_ulpin ON registration_records(ulpin)");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_tax_ulpin ON tax_records(ulpin)");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_alert_ulpin ON ai_alerts(ulpin)");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_application_ulpin ON applications(ulpin)");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_restriction_ulpin ON restrictions(ulpin)");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_utility_ulpin ON utilities(ulpin)");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_dispute_ulpin ON disputes(ulpin)");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_change_ulpin ON change_events(ulpin)");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_documents_ulpin ON documents(ulpin)");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_users_email ON users(email)");
    }

    private void addColumnIfMissing(SQLiteDatabase db,String table,String column,String definition) {
        try {
            db.execSQL("ALTER TABLE " + table + " ADD COLUMN " + column + " " + definition);
        } catch (Exception ignored) {
            // Existing column; safe for upgrades.
        }
    }
}

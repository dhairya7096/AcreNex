package com.acrenex.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** Clearly-labelled synthetic demo dataset. It is not live government data. */
public final class DemoDataSeeder {
    private DemoDataSeeder() {}

    public static void seedExtended(Context context, String userId, String ownerName) {
        if (ownerName == null || ownerName.trim().isEmpty()) ownerName = "AcreNex Citizen";
        DatabaseManager manager = new DatabaseManager(context);
        SQLiteDatabase db = manager.getDatabase();
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date());

        // Five realistic-looking but synthetic parcels for demo/judging.
        String[][] parcels = {
            {"GJGN0001000001","Survey-101/1","Gujarat","Gandhinagar","Gandhinagar","Sargasan",ownerName,"0.82","Acre","Residential","Residential","R-2","Registered","Clear","Paid","Approved","23.1835","72.6368"},
            {"GJAH0002000002","Survey-214/3","Gujarat","Ahmedabad","Daskroi","Shela","Meera Shah","1.25","Acre","Residential","Residential","R-3","Registered","Clear","Pending","Approved","22.9728","72.4747"},
            {"GJSU0003000003","Survey-77/2","Gujarat","Surat","Choryasi","Vesu","Rahul Desai","0.64","Acre","Commercial","Commercial","C-1","Registered","Mortgage Cleared","Paid","Approved","21.1418","72.7700"},
            {"GJRA0004000004","Survey-45/7","Gujarat","Rajkot","Rajkot","Mavdi","Aarav Patel","2.10","Acre","Agricultural","Agricultural","AG-1","Registered","Clear","Paid","Not Required","22.2552","70.7830"},
            {"GJVA0005000005","Survey-310/5","Gujarat","Vadodara","Vadodara","Gotri","Kavya Joshi","0.48","Acre","Residential","Residential","R-2","Mutation Pending","Under Review","Pending","Pending","22.3072","73.1321"}
        };
        for (String[] x : parcels) {
            db.execSQL("INSERT OR REPLACE INTO parcels(ulpin,survey_number,state,district,taluka,village,owner_name,area,area_unit,land_type,land_use,zoning,registration_status,encumbrance_status,tax_status,building_permission_status,latitude,longitude,created_at,updated_at) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                    new Object[]{x[0],x[1],x[2],x[3],x[4],x[5],x[6],x[7],x[8],x[9],x[10],x[11],x[12],x[13],x[14],x[15],Double.parseDouble(x[16]),Double.parseDouble(x[17]),now,now});
            seedParcelLayers(db, x[0], x[6], now);
            seedVertices(db, x[0], Double.parseDouble(x[16]), Double.parseDouble(x[17]));
        }
        seedRequirements(db);
        seedDemoDocuments(db, parcels, now);
        seedLayers(db, now);
        seedApis(db, now);
        db.execSQL("INSERT OR REPLACE INTO state_config(state_code,state_name,language,area_unit,ulppin_format,record_portal,workflow_profile,active) VALUES(?,?,?,?,?,?,?,?)", new Object[]{"GJ","Gujarat","Gujarati / English","Acre","14-character demo ULPIN; production assigned by authorized revenue administration","State RoR portal","Gujarat-Land-Workflow-v1",1});
        db.close();
    }

    private static void seedParcelLayers(SQLiteDatabase db, String ulpin, String owner, String now) {
        db.execSQL("INSERT OR REPLACE INTO ownership_records(id,ulpin,owner_name,ownership_type,share,record_date,source,verification_status) VALUES(?,?,?,?,?,?,?,?)", new Object[]{"OWN-"+ulpin,ulpin,owner,"Individual","100%","2026-04-12","Synthetic RoR","VERIFIED"});
        db.execSQL("INSERT OR REPLACE INTO registration_records(registration_id,ulpin,document_number,registration_date,transaction_type,buyer_name,seller_name,status) VALUES(?,?,?,?,?,?,?,?)", new Object[]{"REG-"+ulpin,ulpin,"DOC-"+ulpin.substring(2),"2026-04-15","Sale",owner,"Synthetic Seller","Registered"});
        db.execSQL("INSERT OR REPLACE INTO tax_records(id,ulpin,annual_tax,outstanding_amount,financial_year,last_payment_date,status) VALUES(?,?,?,?,?,?,?)", new Object[]{"TAX-"+ulpin,ulpin,18500,0,"2026-27","2026-08-18","Paid"});
        db.execSQL("INSERT OR REPLACE INTO land_use_records(id,ulpin,land_use,zoning,master_plan_zone,effective_from,status) VALUES(?,?,?,?,?,?,?)", new Object[]{"LU-"+ulpin,ulpin,"Residential","R-2","Residential Growth Zone","2026-01-01","Active"});
        db.execSQL("INSERT OR REPLACE INTO building_permissions(id,ulpin,permission_number,application_date,approval_date,building_type,status) VALUES(?,?,?,?,?,?,?)", new Object[]{"BP-"+ulpin,ulpin,"BP-"+ulpin,"2026-05-01","2026-05-12","Residential","Approved"});
        db.execSQL("INSERT OR REPLACE INTO encumbrances(id,ulpin,type,institution,amount,start_date,end_date,status) VALUES(?,?,?,?,?,?,?,?)", new Object[]{"ENC-"+ulpin,ulpin,"Mortgage","Demo Cooperative Bank",0,"2025-01-01","","Clear"});
        db.execSQL("INSERT OR REPLACE INTO restrictions(id,ulpin,restriction_type,authority,description,severity,status,effective_from,effective_to) VALUES(?,?,?,?,?,?,?,?,?)", new Object[]{"RES-"+ulpin,ulpin,"Planning Screening","Local Planning Authority","Demo zoning and setback check.","LOW","Active","2026-01-01",""});
        db.execSQL("INSERT OR REPLACE INTO utilities(id,ulpin,utility_type,provider,connection_status,network_ref,last_verified) VALUES(?,?,?,?,?,?,?)", new Object[]{"UTL-E-"+ulpin,ulpin,"Electricity","Demo Utility","Connected","GRID-"+ulpin.substring(2,8),now});
        db.execSQL("INSERT OR REPLACE INTO utilities(id,ulpin,utility_type,provider,connection_status,network_ref,last_verified) VALUES(?,?,?,?,?,?,?)", new Object[]{"UTL-W-"+ulpin,ulpin,"Water","Demo Municipal Water","Connected","WTR-"+ulpin.substring(2,8),now});
        db.execSQL("INSERT OR REPLACE INTO valuation_records(id,ulpin,guideline_value,market_reference,unit,valuation_year,source,status) VALUES(?,?,?,?,?,?,?,?)", new Object[]{"VAL-"+ulpin,ulpin,4200000,5100000,"INR/acre","2026","Synthetic valuation dataset","Reference only"});
        db.execSQL("INSERT OR REPLACE INTO environmental_zones(id,ulpin,zone_type,authority,buffer_meters,impact_level,status) VALUES(?,?,?,?,?,?,?)", new Object[]{"ENV-"+ulpin,ulpin,"General Screening","Demo Environment Authority",0,"Low","Clear"});
        db.execSQL("INSERT OR REPLACE INTO disputes(id,ulpin,case_number,forum,dispute_type,filing_date,status,next_hearing) VALUES(?,?,?,?,?,?,?,?)", new Object[]{"DSP-"+ulpin,ulpin,"DEMO-NIL","Revenue Forum","No active dispute",now,"No Active Case","—"});
        db.execSQL("INSERT OR REPLACE INTO data_quality(id,ulpin,completeness,consistency,spatial_accuracy,timeliness,last_checked,status) VALUES(?,?,?,?,?,?,?,?)", new Object[]{"DQ-"+ulpin,ulpin,96.0,94.0,92.0,90.0,now,"GOOD"});
        db.execSQL("INSERT OR REPLACE INTO change_events(id,ulpin,baseline_date,comparison_date,change_percent,change_type,confidence,status,review_required) VALUES(?,?,?,?,?,?,?,?,?)", new Object[]{"CHG-"+ulpin,ulpin,"2026-01-01","2026-09-01",4.0,"Demo spatial change signal",0.91,"CLEAR",0});
    }


    private static void seedVertices(SQLiteDatabase db, String ulpin, double lat, double lon) {
        db.delete("parcel_vertices", "ulpin=?", new String[]{ulpin});
        double dLat = 0.00075, dLon = 0.00100;
        double[][] v={{lat-dLat,lon-dLon},{lat+dLat,lon-dLon*0.75},{lat+dLat*0.75,lon+dLon},{lat-dLat*0.8,lon+dLon*0.8}};
        for(int i=0;i<v.length;i++) db.execSQL("INSERT INTO parcel_vertices(ulpin,vertex_order,latitude,longitude) VALUES(?,?,?,?)",new Object[]{ulpin,i,v[i][0],v[i][1]});
    }

    private static void seedRequirements(SQLiteDatabase db) {
        // Comprehensive prototype checklist. Exact legal/service requirements vary by
        // state, land type, transaction and authority; these rows are deliberately
        // labelled as required/conditional rather than presented as universal law.
        String[][] req = {
            {"R01","LAND_PURCHASE","Identity Proof","Aadhaar / Passport / Driving Licence / Voter ID", "1"},
            {"R02","LAND_PURCHASE","PAN Card","Buyer PAN for transaction/tax workflows where applicable", "1"},
            {"R03","LAND_PURCHASE","Address Proof","Current address proof, if required by service", "1"},
            {"R04","LAND_PURCHASE","Registered Title / Sale Deed","Seller's registered title chain / previous deed", "1"},
            {"R05","LAND_PURCHASE","Record of Rights / 7-12 / Property Card","Latest parcel rights record, format varies by area/state", "1"},
            {"R06","LAND_PURCHASE","8-A / Land Account Record","Revenue account extract where applicable", "1"},
            {"R07","LAND_PURCHASE","Mutation Entries","Latest mutation / transfer entries", "1"},
            {"R08","LAND_PURCHASE","Encumbrance / Liability Check","Mortgage, charge or encumbrance record", "1"},
            {"R09","LAND_PURCHASE","Property Tax Receipt","Latest paid tax receipt / dues statement where applicable", "1"},
            {"R10","LAND_PURCHASE","Cadastral / Survey Map","Parcel boundary and survey reference", "1"},
            {"R11","LAND_PURCHASE","Sale Agreement","Agreement to sell / transaction document", "1"},
            {"R12","LAND_PURCHASE","Stamp Duty / Registration Receipt","Proof of applicable payment", "1"},
            {"R13","LAND_PURCHASE","Land-use / NA Permission","Only where the parcel/service requires it", "0"},
            {"R14","LAND_PURCHASE","Building Permission / NOC","For built property or proposed construction, if applicable", "0"},
            {"R15","LAND_PURCHASE","Bank Release / Mortgage NOC","If an existing charge is shown", "0"},
            {"R16","LAND_PURCHASE","Power of Attorney","If transaction is through an authorised representative", "0"},
            {"R17","LAND_PURCHASE","Succession / Legal Heir Documents","If title comes through inheritance", "0"},
            {"R18","LAND_PURCHASE","Court / Dispute Order","If parcel has a disclosed dispute or order", "0"},
            {"R19","LAND_PURCHASE","RERA / Project Details","For applicable developer/project transactions", "0"},
            {"R20","LAND_PURCHASE","Possession / Handover Record","Where applicable to the transaction", "0"},
            {"R21","LAND_PURCHASE","Recent Photograph","Applicant / buyer photograph where the service requires it", "0"},
            {"R22","LAND_PURCHASE","Seller Identity Proof","Seller identity document for transaction verification", "0"},
            {"R23","LAND_PURCHASE","Title Search / Legal Opinion","Independent title search or legal opinion where requested", "0"},
            {"R24","LAND_PURCHASE","Measurement / Survey Sketch","Latest measurement sheet or survey sketch where applicable", "0"},
            {"R25","LAND_PURCHASE","Land Conversion Order","Approved land-use conversion order, if applicable", "0"},
            {"R26","LAND_PURCHASE","Layout / Development Approval","Approved layout or development permission, if applicable", "0"},
            {"R27","LAND_PURCHASE","Utility / Society / Association NOC","No-objection or no-dues document where applicable", "0"},
            {"R28","LAND_PURCHASE","No-Dues / Clearance Certificate","Relevant authority clearance where applicable", "0"},
            {"R29","LAND_PURCHASE","Possession Certificate","Authority/builder possession evidence where applicable", "0"},
            {"R30","LAND_PURCHASE","Other Authority Order / Certificate","Any transaction-specific order required by the competent authority", "0"},

            {"M01","MUTATION","Application / Mutation Form","Signed mutation or transfer application", "1"},
            {"M02","MUTATION","Registered Transfer Document","Registered sale/gift/release/partition document as applicable", "1"},
            {"M03","MUTATION","Current RoR / 7-12 / Property Card","Latest rights record", "1"},
            {"M04","MUTATION","Identity & Address Proof","Applicant identity and address evidence", "1"},
            {"M05","MUTATION","Death / Legal Heir Documents","For inheritance or succession cases", "0"},
            {"M06","MUTATION","Court / Authority Order","Where mutation follows an order", "0"},
            {"M07","MUTATION","Tax / No-Dues Evidence","Where the mutation service requires clearance", "0"},
            {"M08","MUTATION","Power of Attorney / Authorization","Where filed by an authorised representative", "0"},

            {"G01","REGISTRATION","Identity Proofs","Buyer/seller/parties identity documents", "1"},
            {"G02","REGISTRATION","PAN / Tax Identity","PAN or other tax identity as applicable", "1"},
            {"G03","REGISTRATION","Title / Previous Registered Deed","Previous registered title document", "1"},
            {"G04","REGISTRATION","Sale / Transfer Instrument","Draft or executed transaction instrument", "1"},
            {"G05","REGISTRATION","Stamp Duty Evidence","Applicable stamp duty/e-stamp evidence", "1"},
            {"G06","REGISTRATION","Registration Fee Receipt","Applicable registration fee evidence", "1"},
            {"G07","REGISTRATION","Witness / Representative Details","Witness or power-of-attorney details where applicable", "0"},
            {"G08","REGISTRATION","NOC / Clearance Documents","Authority or lender clearance where applicable", "0"},

            {"B01","BUILDING_PERMISSION","Building Permission Application","Signed application and prescribed form", "1"},
            {"B02","BUILDING_PERMISSION","Ownership / RoR / Property Card","Proof of lawful interest in the parcel", "1"},
            {"B03","BUILDING_PERMISSION","Site / Cadastral Plan","Survey/site plan with parcel boundary", "1"},
            {"B04","BUILDING_PERMISSION","Building Drawings","Architect/engineer drawings as prescribed", "1"},
            {"B05","BUILDING_PERMISSION","Land-use / Zoning Compliance","Zoning or land-use evidence", "1"},
            {"B06","BUILDING_PERMISSION","Structural / Technical Certificate","Where required for the building type", "0"},
            {"B07","BUILDING_PERMISSION","Fire NOC","For applicable building category", "0"},
            {"B08","BUILDING_PERMISSION","Environment / Tree / Water NOC","Where applicable to location/project", "0"},
            {"B09","BUILDING_PERMISSION","Utility Connection / NOC","Where required by authority", "0"},
            {"B10","BUILDING_PERMISSION","Applicant Identity & Address","Applicant/owner identification", "1"},

            {"N01","NA_CONVERSION","Land Conversion Application","Application for applicable non-agricultural conversion", "1"},
            {"N02","NA_CONVERSION","Current RoR / 7-12 / Property Card","Latest revenue record", "1"},
            {"N03","NA_CONVERSION","Ownership / Title Document","Proof of lawful ownership", "1"},
            {"N04","NA_CONVERSION","Site / Survey Map","Parcel map and measurements", "1"},
            {"N05","NA_CONVERSION","Planning / Zoning Evidence","Applicable planning/land-use documents", "0"},
            {"N06","NA_CONVERSION","NOC / Clearance Documents","Departmental clearances where applicable", "0"},

            {"T01","PROPERTY_TAX","Property / Parcel Identity","ULPIN, survey number or property reference", "1"},
            {"T02","PROPERTY_TAX","Ownership / Occupancy Proof","Ownership or lawful occupancy evidence", "1"},
            {"T03","PROPERTY_TAX","Previous Tax Receipt","Latest receipt / assessment details", "0"},
            {"T04","PROPERTY_TAX","Assessment / Building Details","Built-up/assessment information where applicable", "0"},
            {"T05","PROPERTY_TAX","No-Dues / Payment Evidence","Payment or clearance evidence", "0"},

            {"D01","DISPUTE","Dispute / Grievance Application","Signed case or grievance application", "1"},
            {"D02","DISPUTE","Identity & Address Proof","Applicant identity evidence", "1"},
            {"D03","DISPUTE","Title / RoR / Property Card","Relevant land rights records", "1"},
            {"D04","DISPUTE","Survey / Cadastral Map","Boundary or parcel evidence", "0"},
            {"D05","DISPUTE","Transaction / Mutation Documents","Relevant registered deeds and mutation history", "0"},
            {"D06","DISPUTE","Court / Authority Orders","Existing orders, notices or case documents", "0"}
        };
        for(String[] r:req) db.execSQL("INSERT OR REPLACE INTO document_requirements(id,service_code,document_type,description,required,order_no) VALUES(?,?,?,?,?,?)",new Object[]{r[0],r[1],r[2],r[3],Integer.parseInt(r[4]),Integer.parseInt(r[0].substring(1))});
    }

    private static void seedDemoDocuments(SQLiteDatabase db, String[][] parcels, String now) {
        for(String[] p:parcels){
            String ulpin=p[0];
            String[][] docs={{"R01","Identity Proof","VERIFIED"},{"R02","PAN Card","VERIFIED"},{"R04","Registered Title / Sale Deed","VERIFIED"},{"R05","Record of Rights / 7-12 / Property Card","VERIFIED"},{"R07","Mutation Entries","UPLOADED"},{"R08","Encumbrance / Liability Check","VERIFIED"},{"R09","Property Tax Receipt","PENDING"},{"R10","Cadastral / Survey Map","VERIFIED"},{"R11","Sale Agreement","PENDING"},{"R12","Stamp Duty / Registration Receipt","PENDING"}};
            for(String[] d:docs) db.execSQL("INSERT OR REPLACE INTO documents(document_id,ulpin,document_type,document_name,uploaded_date,extracted_text,verification_status,confidence_score) VALUES(?,?,?,?,?,?,?,?)",new Object[]{d[0]+"-"+ulpin,ulpin,d[1],d[1]+" — "+ulpin,now,"Synthetic demo document linked to parcel "+ulpin,d[2],d[2].equals("VERIFIED")?0.98:0.00});
        }
    }

    private static void seedLayers(SQLiteDatabase db,String now){
        String[][] layers={{"L01","CADASTRAL","Cadastral Parcel Boundaries","Revenue / Survey","OGC-compatible GIS","1.0","CITIZEN+OFFICER"},{"L02","ROR","Record of Rights","Revenue Department","Parcel-linked API","1.0","CITIZEN+OFFICER"},{"L03","REG","Registration","Registration Department","Parcel-linked API","1.0","OFFICER"},{"L04","MASTER_PLAN","Master Plan & Zoning","Planning Authority","GIS layer","1.0","CITIZEN+OFFICER"},{"L05","BUILDING","Building Permissions","ULB","Workflow API","1.0","CITIZEN+OFFICER"},{"L06","TAX","Property Tax","Municipality","Fiscal API","1.0","CITIZEN+OFFICER"},{"L07","UTILITY","Utility Infrastructure","Utility Agencies","Network layer","1.0","CITIZEN"},{"L08","RESTRICTION","Restrictions & Environment","Planning/Environment","Spatial overlay","1.0","CITIZEN+OFFICER"}};
        for(String[] l:layers) db.execSQL("INSERT OR REPLACE INTO gis_layers(id,layer_code,layer_name,category,source_department,standard,version,enabled,access_scope) VALUES(?,?,?,?,?,?,?,?,?)",new Object[]{l[0],l[1],l[2],"GOVERNANCE",l[3],l[4],l[5],1,l[6]});
    }
    private static void seedApis(SQLiteDatabase db,String now){
        String[][] a={{"API01","Land Parcel API","Revenue Department","/v1/parcels/{ulpin}","GET","v1","OAuth2/Bearer","DEMO READY"},{"API02","RoR API","Revenue Department","/v1/ror/{ulpin}","GET","v1","OAuth2/Bearer","DEMO READY"},{"API03","Registration API","Registration Department","/v1/registration/{ulpin}","GET","v1","OAuth2/Bearer","DEMO READY"},{"API04","Building Permission API","ULB","/v1/building-permissions/{ulpin}","GET","v1","OAuth2/Bearer","DEMO READY"},{"API05","Property Tax API","Municipality","/v1/tax/{ulpin}","GET","v1","OAuth2/Bearer","DEMO READY"},{"API06","GIS Layer API","State GIS","/v1/gis/layers","GET","v1","OAuth2/Bearer","DEMO READY"}};
        for(String[] x:a) db.execSQL("INSERT OR REPLACE INTO api_registry(id,api_name,department,endpoint,method,version,auth_scheme,status,last_sync) VALUES(?,?,?,?,?,?,?,?,?)",new Object[]{x[0],x[1],x[2],x[3],x[4],x[5],x[6],x[7],now});
    }
}

package com.acrenex.app.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.os.Build;

public class DatabaseManager {

    private final AcreNexDatabase database;

    public DatabaseManager(Context context) {

        database =
                new AcreNexDatabase(
                        context
                );
    }

    public SQLiteDatabase getDatabase() {

        return database.getWritableDatabase();
    }

    // =====================================================
    // INSERT PARCEL
    // =====================================================

    public boolean insertParcel(
            String ulpin,
            String surveyNumber,
            String state,
            String district,
            String taluka,
            String village,
            String ownerName,
            String area,
            String areaUnit,
            String landType,
            String landUse,
            String zoning,
            String registrationStatus,
            String encumbranceStatus,
            String taxStatus,
            String buildingPermissionStatus,
            double latitude,
            double longitude
    ) {

        ContentValues values =
                new ContentValues();

        values.put("ulpin", ulpin);
        values.put("survey_number", surveyNumber);
        values.put("state", state);
        values.put("district", district);
        values.put("taluka", taluka);
        values.put("village", village);
        values.put("owner_name", ownerName);
        values.put("area", area);
        values.put("area_unit", areaUnit);
        values.put("land_type", landType);
        values.put("land_use", landUse);
        values.put("zoning", zoning);
        values.put(
                "registration_status",
                registrationStatus
        );
        values.put(
                "encumbrance_status",
                encumbranceStatus
        );
        values.put(
                "tax_status",
                taxStatus
        );
        values.put(
                "building_permission_status",
                buildingPermissionStatus
        );
        values.put("latitude", latitude);
        values.put("longitude", longitude);

        long result =
                0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            result = getDatabase().insertWithOnConflict(
                    "parcels",
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE
            );
        }

        return result != -1;
    }


    // =====================================================
    // GET PARCEL
    // =====================================================

    public Cursor getParcel(
            String ulpin
    ) {

        return getDatabase().query(
                "parcels",
                null,
                "ulpin = ?",
                new String[]{ulpin},
                null,
                null,
                null
        );
    }


    // =====================================================
    // SEARCH PARCEL
    // =====================================================

    public Cursor searchParcels(
            String query
    ) {

        String search =
                "%" +
                        query +
                        "%";

        return getDatabase().query(
                "parcels",
                null,
                "ulpin LIKE ? OR " +
                        "owner_name LIKE ? OR " +
                        "survey_number LIKE ?",
                new String[]{
                        search,
                        search,
                        search
                },
                null,
                null,
                "owner_name ASC"
        );
    }


    // =====================================================
    // AUDIT LOG
    // =====================================================

    public void addAuditLog(
            String userId,
            String role,
            String action,
            String resourceType,
            String resourceId,
            String timestamp,
            String result
    ) {

        ContentValues values =
                new ContentValues();

        values.put(
                "user_id",
                userId
        );

        values.put(
                "role",
                role
        );

        values.put(
                "action",
                action
        );

        values.put(
                "resource_type",
                resourceType
        );

        values.put(
                "resource_id",
                resourceId
        );

        values.put(
                "timestamp",
                timestamp
        );

        values.put(
                "result",
                result
        );

        getDatabase().insert(
                "audit_logs",
                null,
                values
        );
    }


    // =====================================================
    // CLOSE
    // =====================================================

    public void close() {

        database.close();
    }
}
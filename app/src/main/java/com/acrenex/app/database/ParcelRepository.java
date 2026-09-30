package com.acrenex.app.database;

import android.content.Context;
import android.database.Cursor;

public class ParcelRepository {

    private final DatabaseManager databaseManager;

    public ParcelRepository(
            Context context
    ) {

        databaseManager =
                new DatabaseManager(
                        context
                );
    }

    public boolean saveParcel(
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

        return databaseManager.insertParcel(
                ulpin,
                surveyNumber,
                state,
                district,
                taluka,
                village,
                ownerName,
                area,
                areaUnit,
                landType,
                landUse,
                zoning,
                registrationStatus,
                encumbranceStatus,
                taxStatus,
                buildingPermissionStatus,
                latitude,
                longitude
        );
    }

    public Cursor findByUlpin(
            String ulpin
    ) {

        return databaseManager.getParcel(
                ulpin
        );
    }

    public Cursor search(
            String query
    ) {

        return databaseManager.searchParcels(
                query
        );
    }

    /**
     * Generates a deterministic 14-digit AcreNex DEMO ULPIN-like identifier from
     * parcel identity inputs. It is intentionally marked as demo-only; an official
     * government ULPIN must be assigned by the authoritative land administration.
     */
    public static String generateDemoUlpin(String stateCode, String district, String surveyNumber, double latitude, double longitude) {
        String seed = (stateCode == null ? "" : stateCode.trim().toUpperCase()) + "|"
                + (district == null ? "" : district.trim().toUpperCase()) + "|"
                + (surveyNumber == null ? "" : surveyNumber.trim().toUpperCase()) + "|"
                + String.format(java.util.Locale.US, "%.6f|%.6f", latitude, longitude);
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(seed.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            long value = 0;
            for (int i = 0; i < 8; i++) value = (value << 8) | (hash[i] & 0xffL);
            value = Math.abs(value) % 1000000000000L;
            return String.format(java.util.Locale.US, "%02d%012d", 24, value);
        } catch (Exception e) {
            long fallback = Math.abs((long) seed.hashCode()) % 1000000000000L;
            return String.format(java.util.Locale.US, "%02d%012d", 24, fallback);
        }
    }

    public void close() {

        databaseManager.close();
    }
}
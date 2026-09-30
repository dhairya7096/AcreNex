package com.acrenex.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class ApplicationRepository {

    private final DatabaseManager databaseManager;

    public ApplicationRepository(
            Context context
    ) {

        databaseManager =
                new DatabaseManager(
                        context
                );
    }

    public boolean createApplication(
            String applicationId,
            String ulpin,
            String applicantId,
            String serviceType,
            String submissionDate,
            String department,
            String status,
            int progress
    ) {

        ContentValues values =
                new ContentValues();

        values.put(
                "application_id",
                applicationId
        );

        values.put(
                "ulpin",
                ulpin
        );

        values.put(
                "applicant_id",
                applicantId
        );

        values.put(
                "service_type",
                serviceType
        );

        values.put(
                "submission_date",
                submissionDate
        );

        values.put(
                "current_department",
                department
        );

        values.put(
                "current_status",
                status
        );

        values.put(
                "progress",
                progress
        );

        long result =
                databaseManager
                        .getDatabase()
                        .insertWithOnConflict(
                                "applications",
                                null,
                                values,
                                android.database.sqlite
                                        .SQLiteDatabase
                                        .CONFLICT_REPLACE
                        );

        return result != -1;
    }

    public Cursor getApplication(
            String applicationId
    ) {

        return databaseManager
                .getDatabase()
                .query(
                        "applications",
                        null,
                        "application_id = ?",
                        new String[]{
                                applicationId
                        },
                        null,
                        null,
                        null
                );
    }

    public Cursor getApplicationsForParcel(
            String ulpin
    ) {

        return databaseManager
                .getDatabase()
                .query(
                        "applications",
                        null,
                        "ulpin = ?",
                        new String[]{
                                ulpin
                        },
                        null,
                        null,
                        "submission_date DESC"
                );
    }

    public void close() {

        databaseManager.close();
    }
}
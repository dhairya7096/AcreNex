package com.acrenex.app.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.acrenex.app.database.DatabaseManager;
import com.acrenex.app.utils;

/** Cross-department workflow view showing how a parcel request moves through Land Stack. */
public class DepartmentWorkflowActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String ulpin = getIntent().getStringExtra("ulpin");
        if (ulpin == null || ulpin.trim().isEmpty()) ulpin = "24-GJ-GN-0001-00001";
        DatabaseManager manager = new DatabaseManager(this);
        LinearLayout root = utils.screen(this, "Inter-Department Workflow", "A parcel request travels through the departments that own each authoritative layer.");

        String[][] stages = {
                {"01", "Citizen request", "Service request created against the ULPIN", "Citizen Services"},
                {"02", "Identity & access", "Role and authorised scope checked", "Security / Access"},
                {"03", "Revenue / RoR", "Ownership and rights verified", "Revenue Department"},
                {"04", "Registration", "Transaction / deed record cross-checked", "Registration Department"},
                {"05", "Planning", "Land use, zoning and master plan checked", "Planning / ULB"},
                {"06", "Fiscal", "Property tax and valuation signals checked", "Tax / Finance"},
                {"07", "Restrictions", "Encumbrance, environment and restriction overlays checked", "Planning / Environment"},
                {"08", "AI review", "Explainable inconsistencies and change signals surfaced", "AcreNex AI"},
                {"09", "Officer decision", "Authorised officer approves, rejects or requests correction", "Responsible Department"},
                {"10", "Audit & notification", "Action recorded and citizen receives status", "Land Stack"}
        };
        for (String[] s : stages) {
            LinearLayout card = utils.column(this);
            LinearLayout head = utils.rowContainer(this);
            head.addView(utils.text(this, s[0] + "  " + s[1], 16, utils.TEXT, true), new LinearLayout.LayoutParams(0, -2, 1));
            head.addView(utils.badge(this, s[3], true));
            card.addView(head);
            card.addView(utils.text(this, s[2], 13, utils.MUTED, false));
            utils.addCard(root, card, this);
        }

        LinearLayout current = utils.column(this);
        current.addView(utils.section(this, "Linked service request"));
        Cursor c = null;
        try {
            c = manager.getDatabase().rawQuery("SELECT service_name,current_status,department,remarks FROM service_requests WHERE ulpin=? ORDER BY updated_at DESC LIMIT 1", new String[]{ulpin});
            if (c.moveToFirst()) {
                utils.row(current, this, "Service", c.getString(0));
                utils.row(current, this, "Status", c.getString(1));
                utils.row(current, this, "Department", c.getString(2));
                utils.row(current, this, "Remarks", c.getString(3));
            } else current.addView(utils.text(this, "No service request is linked to this ULPIN in the demo dataset.", 13, utils.MUTED, false));
        } finally { if (c != null) c.close(); }
        utils.addCard(root, current, this);

        utils.setScreenContentView(this, root);
        manager.close();
    }
}

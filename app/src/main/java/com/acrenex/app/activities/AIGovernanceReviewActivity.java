package com.acrenex.app.activities;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.acrenex.app.ai.LandStackAIEngine;
import com.acrenex.app.database.DatabaseManager;
import com.acrenex.app.utils;

/** Explainable, human-in-the-loop AI review screen for a parcel. */
public class AIGovernanceReviewActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String ulpin = getIntent().getStringExtra("ulpin");
        if (ulpin == null || ulpin.trim().isEmpty()) ulpin = "24-GJ-GN-0001-00001";

        DatabaseManager manager = new DatabaseManager(this);
        SQLiteDatabase db = manager.getDatabase();
        LandStackAIEngine.Analysis a = LandStackAIEngine.analyze(db, ulpin);

        LinearLayout root = utils.screen(this, "AI Governance Review", "Explainable AI support for officers — the model flags evidence; an authorised officer decides.");

        LinearLayout score = utils.column(this);
        LinearLayout top = utils.rowContainer(this);
        top.addView(utils.text(this, "AcreNex AI signal", 17, utils.TEXT, true), new LinearLayout.LayoutParams(0, -2, 1));
        top.addView(utils.badge(this, a.risk + " RISK", "LOW".equals(a.risk)), new LinearLayout.LayoutParams(-2, -2));
        score.addView(top);
        score.addView(utils.text(this, a.score + "/100 governance confidence", 28, utils.NAVY, true));
        score.addView(utils.text(this, a.summary, 14, utils.MUTED, false));
        utils.addCard(root, score, this);

        LinearLayout evidence = utils.column(this);
        evidence.addView(utils.section(this, "Evidence used"));
        evidence.addView(utils.text(this, "ULPIN: " + a.ulpin, 14, utils.TEXT, true));
        evidence.addView(utils.text(this, "Owner: " + (a.owner.isEmpty() ? "Not available" : a.owner), 14, utils.TEXT, false));
        evidence.addView(utils.text(this, "The analysis reads only the linked parcel records available in the local Land Stack dataset. It does not invent missing government records.", 13, utils.MUTED, false));
        utils.addCard(root, evidence, this);

        LinearLayout reasons = utils.column(this);
        reasons.addView(utils.section(this, "Why the signal was raised"));
        reasons.addView(utils.text(this, a.reasons, 14, utils.TEXT, false));
        utils.addCard(root, reasons, this);

        LinearLayout action = utils.column(this);
        action.addView(utils.section(this, "Recommended next action"));
        action.addView(utils.text(this, a.nextAction, 14, utils.TEXT, true));
        action.addView(utils.text(this, "Human review remains mandatory for approvals, ownership decisions, disputes and statutory actions.", 13, utils.MUTED, false));
        utils.addCard(root, action, this);

        utils.setScreenContentView(this, root);
        manager.close();
    }
}

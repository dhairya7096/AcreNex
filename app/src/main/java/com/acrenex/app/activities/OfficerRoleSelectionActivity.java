package com.acrenex.app.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.acrenex.app.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.acrenex.app.database.DatabaseManager;

/**
 * AcreNex officer command entry point.
 * Existing operational officer roles are grouped into three clearer categories.
 */
public class OfficerRoleSelectionActivity extends AppCompatActivity {
    private String currentRole = "";
    private final int NAVY = Color.rgb(11, 59, 46);
    private final int BLUE = Color.rgb(23, 107, 90);
    private final int GREEN = Color.rgb(47, 143, 69);
    private final int TEXT = Color.rgb(23, 55, 45);
    private final int MUTED = Color.rgb(110, 126, 119);
    private final int BG = Color.rgb(245, 246, 241);
    private final int BORDER = Color.rgb(220, 228, 223);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        currentRole = readCurrentRole();
        if (currentRole.isEmpty()) { finish(); return; }
        setContentView(buildScreen());
    }

    private View buildScreen() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);

        androidx.core.widget.NestedScrollView scroll = new androidx.core.widget.NestedScrollView(this);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(18), dp(18), dp(18), dp(30));

        // Header
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);

        android.widget.ImageView logo = new android.widget.ImageView(this);
        logo.setImageResource(R.drawable.ic_acrenex_mark);
        logo.setContentDescription("AcreNex");
        header.addView(logo, lp(dp(54), dp(54)));

        LinearLayout headerText = new LinearLayout(this);
        headerText.setOrientation(LinearLayout.VERTICAL);
        headerText.setPadding(dp(11), 0, 0, 0);
        headerText.addView(text("ACRENEX • LAND INTELLIGENCE", 10, Color.rgb(212,167,44), true));
        headerText.addView(text("Officer Command Centre", 23, NAVY, true), lp(0, -2, dp(2)));
        headerText.addView(text("Role-based access to one ULPIN-linked land record.", 12, MUTED, false), lp(0, -2, dp(2)));
        header.addView(headerText, new LinearLayout.LayoutParams(0, -2, 1));
        content.addView(header);

        // USP hero
        MaterialCardView hero = card(20);
        LinearLayout heroBox = column(dp(18));
        TextView overline = text("ONE ULPIN  •  ONE PARCEL VIEW", 10, GREEN, true);
        heroBox.addView(overline);
        heroBox.addView(text("Complete land intelligence in one command panel", 20, NAVY, true), lp(0, -2, dp(5)));
        heroBox.addView(text("Search a parcel once. Authorized officers can review the linked GIS, RoR, ownership, registration, tax, permissions, restrictions and case records.", 12, MUTED, false), lp(0, -2, dp(6)));

        LinearLayout chips = new LinearLayout(this);
        chips.setOrientation(LinearLayout.HORIZONTAL);
        chips.setPadding(0, dp(10), 0, 0);
        chips.addView(chip("GIS"), lp(dp(58), dp(30)));
        chips.addView(chip("RoR"), lp(dp(58), dp(30), dp(7)));
        chips.addView(chip("TAX"), lp(dp(58), dp(30), dp(7)));
        chips.addView(chip("DOCS"), lp(dp(68), dp(30), dp(7)));
        heroBox.addView(chips);
        hero.addView(heroBox);
        content.addView(hero, lp(-1, -2, dp(16)));

        // Quick ULPIN lookup
        MaterialCardView lookup = card(18);
        LinearLayout lookupBox = column(dp(14));
        lookupBox.addView(text("QUICK PARCEL LOOKUP", 10, MUTED, true));
        lookupBox.addView(text("Open the shared ULPIN search workspace", 14, TEXT, true), lp(0, -2, dp(3)));
        MaterialButton search = button("Open ULPIN / Parcel Search", BLUE);
        search.setOnClickListener(v -> startActivity(new Intent(this, ParcelSearchActivity.class)));
        lookupBox.addView(search, lp(-1, dp(48), dp(10)));
        lookup.addView(lookupBox);
        content.addView(lookup, lp(-1, -2, dp(12)));

        content.addView(text("OFFICER CATEGORIES", 10, MUTED, true), lp(0, -2, dp(18)));

        if (isSuperAdmin()) {
            LinearLayout agencies = category("01", "AGENCIES & LOCAL ADMINISTRATION", "Department workspaces available to the command centre.", GREEN);
            addRole(agencies, "Municipal / Planning Officer", "Planning • building • civic land workflows", PlanningOfficerDashboardActivity.class, GREEN);
            addRole(agencies, "Revenue Officer", "RoR • ownership • mutation review", RevenueOfficerDashboardActivity.class, GREEN);
            addRole(agencies, "Officer Command Dashboard", "District command • escalation • services", OfficerDashboardActivity.class, GREEN);
            content.addView(agencies, lp(-1, -2, dp(10)));

            LinearLayout land = category("02", "LAND RECORDS & TRANSACTIONS", "Parcel identity, geometry, registration and fiscal records.", BLUE);
            addRole(land, "Registration Officer", "Sale • registration • transaction records", RegistrationOfficerDashboardActivity.class, BLUE);
            addRole(land, "Survey / GIS Officer", "Cadastral geometry • coordinates • ULPIN mapping", SurveyGISOfficerDashboardActivity.class, BLUE);
            addRole(land, "Property Tax Officer", "Tax dues • payments • parcel fiscal records", TaxOfficerDashboardActivity.class, BLUE);
            content.addView(land, lp(-1, -2, dp(10)));

            LinearLayout regulatory = category("03", "REGULATION, RISK & CASEWORK", "Disputes, restrictions, environment and system-level review.", NAVY);
            addRole(regulatory, "Dispute / Case Officer", "Disputes • objections • review workflow", DisputeOfficerDashboardActivity.class, NAVY);
            addRole(regulatory, "Environment / Restriction Officer", "Restrictions • environmental screening", EnvironmentOfficerDashboardActivity.class, NAVY);
            addRole(regulatory, "Super Admin / Command Centre", "Configuration • audit • access management", SuperAdminDashboardActivity.class, NAVY);
            content.addView(regulatory, lp(-1, -2, dp(10)));
        } else {
            LinearLayout own = category("01", "YOUR AUTHORIZED WORKSPACE", "Your account can open only the dashboard assigned to its stored role.", GREEN);
            addCurrentRole(own);
            content.addView(own, lp(-1, -2, dp(10)));
        }

        TextView note = text("Access and editing remain role-controlled. This prototype uses sample data; production access must be connected to authorized departmental systems.", 10, MUTED, false);
        note.setGravity(Gravity.CENTER);
        content.addView(note, lp(-1, -2, dp(18)));

        scroll.addView(content);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        return root;
    }

    private String readCurrentRole() {
        com.acrenex.app.security.AuthManager auth = new com.acrenex.app.security.AuthManager(this);
        String role = auth.getRole();
        return role == null ? "" : role;
    }

    private boolean isSuperAdmin() { return "Super Admin / Command Centre".equals(currentRole); }

    private void addCurrentRole(LinearLayout box) {
        if ("Revenue Officer".equals(currentRole)) addRole(box, "Revenue Officer", "RoR • ownership • mutation review", RevenueOfficerDashboardActivity.class, GREEN);
        else if ("Registration Officer".equals(currentRole)) addRole(box, "Registration Officer", "Sale • registration • transaction records", RegistrationOfficerDashboardActivity.class, BLUE);
        else if ("Survey / GIS Officer".equals(currentRole)) addRole(box, "Survey / GIS Officer", "Cadastral geometry • coordinates • ULPIN mapping", SurveyGISOfficerDashboardActivity.class, BLUE);
        else if ("Municipal / Planning Officer".equals(currentRole)) addRole(box, "Municipal / Planning Officer", "Planning • building • civic land workflows", PlanningOfficerDashboardActivity.class, GREEN);
        else if ("Property Tax Officer".equals(currentRole)) addRole(box, "Property Tax Officer", "Tax dues • payments • parcel fiscal records", TaxOfficerDashboardActivity.class, BLUE);
        else if ("Dispute / Case Officer".equals(currentRole)) addRole(box, "Dispute / Case Officer", "Disputes • objections • review workflow", DisputeOfficerDashboardActivity.class, NAVY);
        else if ("Environment / Restriction Officer".equals(currentRole)) addRole(box, "Environment / Restriction Officer", "Restrictions • environmental screening", EnvironmentOfficerDashboardActivity.class, NAVY);
        else addRole(box, "Officer Workspace", "Your assigned departmental dashboard", OfficerDashboardActivity.class, GREEN);
    }

    private LinearLayout category(String number, String title, String subtitle, int accent) {
        MaterialCardView card = card(20);
        LinearLayout box = column(dp(14));

        LinearLayout top = new LinearLayout(this);
        top.setOrientation(LinearLayout.HORIZONTAL);
        top.setGravity(Gravity.CENTER_VERTICAL);

        TextView n = text(number, 12, Color.WHITE, true);
        n.setGravity(Gravity.CENTER);
        n.setBackground(round(accent, dp(12)));
        top.addView(n, lp(dp(38), dp(38)));

        LinearLayout titles = new LinearLayout(this);
        titles.setOrientation(LinearLayout.VERTICAL);
        titles.setPadding(dp(10), 0, 0, 0);
        titles.addView(text(title, 14, NAVY, true));
        titles.addView(text(subtitle, 11, MUTED, false), lp(0, -2, dp(3)));
        top.addView(titles, new LinearLayout.LayoutParams(0, -2, 1));
        box.addView(top);
        card.addView(box);

        LinearLayout wrapper = new LinearLayout(this);
        wrapper.setOrientation(LinearLayout.VERTICAL);
        wrapper.addView(card);
        // We put role cards below the category header card for cleaner hierarchy.
        return wrapper;
    }

    private void addRole(LinearLayout category, String label, String sub, Class<?> target, int accent) {
        MaterialCardView role = card(15);
        role.setCardElevation(dp(1));
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(13), dp(9), dp(9), dp(9));

        View dot = new View(this);
        dot.setBackground(round(accent, dp(8)));
        row.addView(dot, lp(dp(10), dp(10)));

        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        texts.setPadding(dp(10), 0, dp(6), 0);
        texts.addView(text(label, 13, TEXT, true));
        texts.addView(text(sub, 10, MUTED, false), lp(0, -2, dp(2)));
        row.addView(texts, new LinearLayout.LayoutParams(0, -2, 1));

        MaterialButton open = new MaterialButton(this);
        open.setText("Open");
        open.setTextSize(11);
        open.setAllCaps(false);
        open.setTextColor(accent);
        open.setCornerRadius(dp(12));
        open.setStrokeWidth(dp(1));
        open.setStrokeColor(ColorStateList.valueOf(accent));
        open.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        open.setMinHeight(0);
        open.setMinimumHeight(0);
        open.setPadding(dp(10), 0, dp(10), 0);
        open.setOnClickListener(v -> startActivity(new Intent(this, target)));
        row.addView(open, lp(dp(66), dp(38)));

        role.addView(row);
        category.addView(role, lp(-1, dp(62), dp(8)));
    }

    private MaterialButton button(String label, int color) {
        MaterialButton b = new MaterialButton(this);
        b.setText(label);
        b.setTextSize(13);
        b.setTextColor(Color.WHITE);
        b.setAllCaps(false);
        b.setCornerRadius(dp(14));
        b.setBackgroundTintList(ColorStateList.valueOf(color));
        b.setMinHeight(0);
        b.setMinimumHeight(0);
        return b;
    }

    private TextView chip(String s) {
        TextView v = text(s, 9, GREEN, true);
        v.setGravity(Gravity.CENTER);
        v.setBackground(round(Color.rgb(232, 247, 242), dp(15)));
        return v;
    }

    private MaterialCardView card(int radius) {
        MaterialCardView c = new MaterialCardView(this);
        c.setCardBackgroundColor(Color.WHITE);
        c.setRadius(dp(radius));
        c.setCardElevation(dp(2));
        c.setStrokeColor(BORDER);
        c.setStrokeWidth(dp(1));
        c.setUseCompatPadding(false);
        return c;
    }

    private LinearLayout column(int padding) {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setPadding(padding, padding, padding, padding);
        return l;
    }

    private TextView text(String s, float size, int color, boolean bold) {
        TextView v = new TextView(this);
        v.setText(s);
        v.setTextSize(size);
        v.setTextColor(color);
        v.setTypeface(Typeface.DEFAULT, bold ? Typeface.BOLD : Typeface.NORMAL);
        return v;
    }

    private GradientDrawable round(int color, int radius) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(color);
        g.setCornerRadius(radius);
        return g;
    }

    private LinearLayout.LayoutParams lp(int w, int h) { return new LinearLayout.LayoutParams(w, h); }
    private LinearLayout.LayoutParams lp(int w, int h, int top) {
        LinearLayout.LayoutParams p = lp(w, h);
        p.topMargin = top;
        return p;
    }
    private int dp(int x) { return Math.round(x * getResources().getDisplayMetrics().density); }
}

package com.acrenex.app.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.acrenex.app.R;
import com.acrenex.app.database.UserRepository;
import com.acrenex.app.firebase.FirebaseBackend;
import com.acrenex.app.security.AuthManager;
import com.acrenex.app.security.SecurityUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Officer onboarding for the prototype.
 *
 * A real government deployment should not let a public user self-assign a privileged
 * role. In production this flow should create a PENDING officer request and an
 * authorized administrator/department should approve the role before access is granted.
 */
public class OfficerRegisterActivity extends AppCompatActivity {
    private static final String[] ROLES = {
            "Revenue Officer",
            "Registration Officer",
            "Survey / GIS Officer",
            "Municipal / Planning Officer",
            "Property Tax Officer",
            "Dispute / Case Officer",
            "Environment / Restriction Officer"
    };

    private TextInputEditText name, employeeId, email, mobile, password, confirm;
    private Spinner roleSpinner;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(buildScreen());
    }

    private android.view.View buildScreen() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.rgb(245, 246, 241));
        root.setPadding(dp(18), dp(18), dp(18), dp(30));

        androidx.core.widget.NestedScrollView scroll = new androidx.core.widget.NestedScrollView(this);
        scroll.setFillViewport(true);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(18), dp(18), dp(18), dp(28));

        TextView brand = tv("ACRENEX • OFFICER ACCESS", 10, Color.rgb(47,143,69), true);
        content.addView(brand);
        content.addView(tv("Officer onboarding", 27, Color.rgb(11,59,46), true), lp(0, -2, dp(4)));
        content.addView(tv("Create one secure officer account. After sign-in, AcreNex routes the account to its assigned departmental workspace.", 12, Color.rgb(110,126,119), false), lp(0, -2, dp(5)));

        MaterialCardView notice = card();
        LinearLayout noticeBox = col(dp(14));
        noticeBox.addView(tv("ROLE-CONTROLLED ACCESS", 10, Color.rgb(47,143,69), true));
        noticeBox.addView(tv("Prototype mode: the selected department role is stored with the account. Production deployment should require department/admin approval before a privileged role becomes active.", 11, Color.rgb(49,90,72), false), lp(0,-2,dp(5)));
        notice.addView(noticeBox);
        content.addView(notice, lp(-1,-2,dp(14)));

        name = field(content, "Full name", "Officer full name", InputType.TYPE_CLASS_TEXT);
        employeeId = field(content, "Employee / Officer ID", "e.g. GJ-REV-1024", InputType.TYPE_CLASS_TEXT);
        email = field(content, "Official email", "officer@department.gov.in", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        mobile = field(content, "Mobile number", "10-digit mobile number", InputType.TYPE_CLASS_PHONE);

        content.addView(tv("Department role", 11, Color.rgb(23,55,45), true), lp(0,-2,dp(14)));
        roleSpinner = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, ROLES);
        roleSpinner.setAdapter(adapter);
        roleSpinner.setBackgroundColor(Color.WHITE);
        content.addView(roleSpinner, lp(-1, dp(52), dp(5)));

        password = field(content, "Password", "At least 6 characters", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        confirm = field(content, "Confirm password", "Re-enter password", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        MaterialButton create = new MaterialButton(this);
        create.setText("Create Officer Account  →");
        create.setAllCaps(false);
        create.setTextColor(Color.WHITE);
        create.setTextSize(14);
        create.setCornerRadius(dp(15));
        create.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(23,107,90)));
        create.setOnClickListener(v -> registerOfficer());
        content.addView(create, lp(-1, dp(54), dp(18)));

        TextView back = tv("Already have an account?  Sign in", 11, Color.rgb(23,107,90), true);
        back.setGravity(Gravity.CENTER);
        back.setPadding(0, dp(14), 0, dp(8));
        back.setOnClickListener(v -> finish());
        content.addView(back);

        scroll.addView(content);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, -1));
        return root;
    }

    private void registerOfficer() {
        String n = text(name), eid = text(employeeId), e = text(email), m = text(mobile), p = text(password), c = text(confirm);
        String role = String.valueOf(roleSpinner.getSelectedItem());
        if (n.length() < 2) { name.setError("Enter full name"); return; }
        if (eid.length() < 4) { employeeId.setError("Enter employee / officer ID"); return; }
        if (!Patterns.EMAIL_ADDRESS.matcher(e).matches()) { email.setError("Enter a valid official email"); return; }
        if (m.length() != 10) { mobile.setError("Enter a 10-digit mobile number"); return; }
        if (p.length() < 6) { password.setError("Use at least 6 characters"); return; }
        if (!p.equals(c)) { confirm.setError("Passwords do not match"); return; }

        FirebaseBackend firebase = FirebaseBackend.get(this);
        if (firebase.isConfigured()) {
            firebase.createOfficer(n, eid, e, m, p, role, task -> {
                if (!task.isSuccessful() || task.getResult() == null || task.getResult().getUser() == null) {
                    String message = task.getException() == null ? "Officer account could not be created." : task.getException().getMessage();
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                    return;
                }
                String uid = task.getResult().getUser().getUid();
                mirrorOfficer(uid, n, e, m, role);
                new AuthManager(this).loginDemo(e, p, role, uid);
                Toast.makeText(this, "Officer account created. Opening your workspace.", Toast.LENGTH_LONG).show();
                openRole(role);
                finish();
            });
            return;
        }

        UserRepository repo = new UserRepository(this);
        android.database.Cursor existing = repo.findByEmail(e);
        if (existing.moveToFirst()) {
            existing.close(); repo.close();
            email.setError("An account already exists with this email");
            return;
        }
        existing.close();
        String id = "OFF-" + SecurityUtils.generateSessionId().substring(0, 12);
        if (!repo.saveUserWithPassword(id, n, e, m, role, role, p, false)) {
            repo.close();
            Toast.makeText(this, "Officer account could not be created.", Toast.LENGTH_SHORT).show();
            return;
        }
        repo.close();
        new AuthManager(this).loginDemo(e, p, role, id);
        Toast.makeText(this, "Officer account created. Opening your workspace.", Toast.LENGTH_LONG).show();
        openRole(role);
        finish();
    }

    private void mirrorOfficer(String uid, String n, String e, String m, String role) {
        UserRepository repo = new UserRepository(this);
        repo.saveUserWithPassword(uid, n, e, m, role, role, "", false);
        repo.close();
    }

    private void openRole(String role) {
        Intent i = new Intent(this, OfficerDashboardActivity.class);
        if ("Revenue Officer".equals(role)) i.setClass(this, RevenueOfficerDashboardActivity.class);
        else if ("Registration Officer".equals(role)) i.setClass(this, RegistrationOfficerDashboardActivity.class);
        else if ("Survey / GIS Officer".equals(role)) i.setClass(this, SurveyGISOfficerDashboardActivity.class);
        else if ("Municipal / Planning Officer".equals(role)) i.setClass(this, PlanningOfficerDashboardActivity.class);
        else if ("Property Tax Officer".equals(role)) i.setClass(this, TaxOfficerDashboardActivity.class);
        else if ("Dispute / Case Officer".equals(role)) i.setClass(this, DisputeOfficerDashboardActivity.class);
        else if ("Environment / Restriction Officer".equals(role)) i.setClass(this, EnvironmentOfficerDashboardActivity.class);
        startActivity(i);
    }

    private TextInputEditText field(LinearLayout parent, String label, String hint, int inputType) {
        TextInputLayout layout = new TextInputLayout(this);
        layout.setHint(label);
        layout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
        layout.setBoxCornerRadii(dp(13),dp(13),dp(13),dp(13));
        layout.setBoxStrokeColor(Color.rgb(23,107,90));
        TextInputEditText edit = new TextInputEditText(this);
        edit.setHint(hint);
        edit.setInputType(inputType);
        edit.setTextSize(14);
        layout.addView(edit, new LinearLayout.LayoutParams(-1,-2));
        parent.addView(layout, lp(-1,-2,dp(12)));
        return edit;
    }

    private MaterialCardView card() { MaterialCardView c=new MaterialCardView(this); c.setCardBackgroundColor(Color.WHITE); c.setRadius(dp(18)); c.setCardElevation(dp(2)); c.setStrokeColor(Color.rgb(220,228,223)); c.setStrokeWidth(dp(1)); return c; }
    private LinearLayout col(int p) { LinearLayout l=new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL); l.setPadding(p,p,p,p); return l; }
    private TextView tv(String s,float size,int color,boolean bold){TextView v=new TextView(this);v.setText(s);v.setTextSize(size);v.setTextColor(color);v.setTypeface(android.graphics.Typeface.DEFAULT,bold?android.graphics.Typeface.BOLD:android.graphics.Typeface.NORMAL);return v;}
    private LinearLayout.LayoutParams lp(int w,int h){return new LinearLayout.LayoutParams(w,h);}
    private LinearLayout.LayoutParams lp(int w,int h,int top){LinearLayout.LayoutParams p=lp(w,h);p.topMargin=top;return p;}
    private int dp(int x){return Math.round(x*getResources().getDisplayMetrics().density);}
    private String text(TextInputEditText e){return e.getText()==null?"":e.getText().toString().trim();}
}

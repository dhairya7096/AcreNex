package com.acrenex.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.acrenex.app.R;
import com.acrenex.app.database.UserRepository;
import com.acrenex.app.firebase.FirebaseBackend;
import com.acrenex.app.security.AuthManager;
import com.acrenex.app.security.SecurityUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText name,email,mobile,password,confirm;
    @Override protected void onCreate(Bundle b){super.onCreate(b);if(getSupportActionBar()!=null)getSupportActionBar().hide();setContentView(R.layout.activity_register);
        name=findViewById(R.id.etFullName);email=findViewById(R.id.etEmail);mobile=findViewById(R.id.etMobile);password=findViewById(R.id.etPassword);confirm=findViewById(R.id.etConfirmPassword);
        MaterialButton btn=findViewById(R.id.btnCreateAccount);btn.setOnClickListener(v->register());}
    private void register(){String n=t(name),e=t(email),m=t(mobile),p=t(password),c=t(confirm);if(n.length()<2){name.setError("Enter your full name");return;}if(!Patterns.EMAIL_ADDRESS.matcher(e).matches()){email.setError("Enter a valid email");return;}if(m.length()!=10){mobile.setError("Enter a 10-digit mobile number");return;}if(p.length()<6){password.setError("Use at least 6 characters");return;}if(!p.equals(c)){confirm.setError("Passwords do not match");return;}
        FirebaseBackend firebase = FirebaseBackend.get(this);
        if (firebase.isConfigured()) {
            firebase.createCitizen(n, e, m, p, task -> {
                if (!task.isSuccessful() || task.getResult() == null || task.getResult().getUser() == null) {
                    String message = task.getException() == null ? "Firebase account could not be created." : task.getException().getMessage();
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                    return;
                }
                String id = task.getResult().getUser().getUid();
                UserRepository repo = new UserRepository(this);
                repo.saveUserWithPassword(id, n, e, m, "Citizen", "Citizen Services", SecurityUtils.generateSessionId(), false);
                repo.close();
                new AuthManager(this).loginDemo(e, p, "Citizen", id);
                Toast.makeText(this, "Citizen account created successfully.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, ProofVerificationActivity.class));
                finish();
            });
            return;
        }
        UserRepository repo=new UserRepository(this);android.database.Cursor existing=repo.findByEmail(e);if(existing.moveToFirst()){existing.close();repo.close();email.setError("An account already exists with this email");email.requestFocus();return;}existing.close();String id="USER-"+ SecurityUtils.generateSessionId().substring(0,12);if(!repo.saveUserWithPassword(id,n,e,m,"Citizen","Citizen Services",p,false)){repo.close();Toast.makeText(this,"Account could not be created",Toast.LENGTH_SHORT).show();return;}repo.close();new AuthManager(this).loginDemo(e,p,"Citizen",id);Toast.makeText(this,"Account created successfully",Toast.LENGTH_SHORT).show();startActivity(new Intent(this,ProofVerificationActivity.class));finish();}
    private String t(TextInputEditText e){return e.getText()==null?"":e.getText().toString().trim();}
}

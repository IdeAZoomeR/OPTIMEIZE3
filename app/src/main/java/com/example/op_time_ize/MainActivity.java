package com.example.op_time_ize;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    private TextView registration, logInBtn, forgotPassword;
    private EditText editEmail, editPassword;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registration = (TextView) findViewById(R.id.registration);
        registration.setOnClickListener(this);

        logInBtn = (MaterialButton) findViewById(R.id.logInBtn);
        logInBtn.setOnClickListener(this);

        editEmail = (EditText) findViewById(R.id.emaillogin);
        editPassword = (EditText) findViewById(R.id.password);

        mAuth = FirebaseAuth.getInstance();

        forgotPassword = (TextView) findViewById(R.id.frgtpasswd);
        forgotPassword.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        ImageButton googlebtn =(ImageButton) findViewById(R.id.google_btn);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if(acct!=null){
            navigateToHome();
        }

        googlebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();

            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.registration:
                startActivity(new Intent(MainActivity.this, Registration.class));
                break;

            case R.id.logInBtn:
                logInBtn();
                break;

            case R.id.frgtpasswd:
                startActivity(new Intent(MainActivity.this, ForgotPassword.class));
        }
    }

    private void logInBtn() {
        final String email = editEmail.getText().toString().trim();
        final String password = editPassword.getText().toString().trim();

        if(email.isEmpty()){
            editEmail.setError("Je potrebná emailová adresa!");
            editEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editEmail.setError("Prosím zadajte platnú emailovú adresu!");
            editEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            editPassword.setError("Je potrebné heslo!");
            editPassword.requestFocus();
            return;
        }

        if(password.length() < 6){
            editPassword.setError("Minimálna dĺžka hesla je 6 znakov!");
            editPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    progressBar.setVisibility(View.GONE);

                    if(user.isEmailVerified()){
                        startActivity(new Intent(MainActivity.this, Home.class));
                    }else{
                        user.sendEmailVerification();
                        Toast.makeText(MainActivity.this, "Overte emailovú adresu!", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }

                }else{
                    Toast.makeText(MainActivity.this, "Nesprávne prihlasovacie údaje!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }

    void signIn(){
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                task.getResult(ApiException.class);
                Log.e("tees", FirebaseAuth.getInstance().getCurrentUser().getUid());

                navigateToHome();
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }
    void navigateToHome(){
        finish();
        Intent intent = new Intent(MainActivity.this,Home.class);
        startActivity(intent);
    }
}
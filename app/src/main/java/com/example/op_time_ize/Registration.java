package com.example.op_time_ize;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.firebase.database.FirebaseDatabase;

public class Registration extends AppCompatActivity implements View.OnClickListener {


    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    private FirebaseAuth mAuth;

    private TextView registerBtn, backtologin;
    private EditText email2, password2, username1;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

        registerBtn = (MaterialButton) findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(this);

        username1 = (EditText) findViewById(R.id.username);
        email2 = (EditText) findViewById(R.id.email2);
        password2 = (EditText) findViewById(R.id.password2);

        backtologin = (TextView) findViewById(R.id.backToLogin);
        backtologin.setOnClickListener(this);

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

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.registerBtn:
                registerBtn();
                break;

            case R.id.backToLogin:
                startActivity(new Intent(Registration.this, MainActivity.class));

        }
    }


    private void registerBtn() {
        String username = username1.getText().toString().trim();
        String email = email2.getText().toString().trim();
        String password = password2.getText().toString().trim();

        if(username.isEmpty()){
            username1.setError("Je potrebné meno použivateľa!");
            username1.requestFocus();
            return;
        }

        if(username.length() < 5){
            username1.setError("Minimálny počet znakov je 5!");
            username1.requestFocus();
            return;
        }

        if(email.isEmpty()){
            email2.setError("Je potrebný email!");
            email2.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            email2.setError("Zadajte platný email!");
            email2.requestFocus();
            return;
        }

        if(password.isEmpty()){
            password2.setError("Je potrebné heslo!");
            password2.requestFocus();
            return;
        }

        if(password.length() < 6){
            password2.setError("Minimálny počet znakov je 6!");
            password2.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            User user = new User(username, email);

                            FirebaseDatabase.getInstance().getReferenceFromUrl("https://registration-2e804-default-rtdb.europe-west1.firebasedatabase.app/")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){
                                                Toast.makeText(Registration.this, "Registrácia prebehla úspešne!", Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                            else{
                                                Toast.makeText(Registration.this, "Registácia neúspešná, skúste to prosím znovu!", Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE);
                                            }

                                        }
                                    });

                        }
                        else{
                            Toast.makeText(Registration.this, "Registácia neúspešná, skúste to prosím znovu!", Toast.LENGTH_SHORT).show();
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
                navigateToHome();
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }
    void navigateToHome(){
        finish();
        Intent intent = new Intent(Registration.this,Home.class);
        startActivity(intent);
    }

}
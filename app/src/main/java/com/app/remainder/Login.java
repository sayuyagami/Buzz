package com.app.remainder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    TextInputEditText mail,pass;
    Button loginbtn;
    TextView signupoption;
    FirebaseAuth mauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mail = findViewById(R.id.mail);
        pass = findViewById(R.id.pass);
        loginbtn = findViewById(R.id.loginbtn);
        signupoption = findViewById(R.id.signupoption);
        mauth = FirebaseAuth.getInstance();

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mailid = mail.getText().toString().trim();
                final String pwd = pass.getText().toString().trim();

                if (!mailid.isEmpty() && !pwd.isEmpty()) {

                    mauth.signInWithEmailAndPassword(mailid, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                finish();
                                Intent intent = new Intent(Login.this, Home.class);
                                Toast.makeText(Login.this, "Successfully Logged in", Toast.LENGTH_LONG).show();
                                startActivity(intent);
                            } else {
                                Toast.makeText(Login.this, "Login Failed", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }
            }
        });

        signupoption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });

    }
    @Override
    protected void onStart() {
        super.onStart();
        if (mauth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(this,Home.class));
        }
    }
}


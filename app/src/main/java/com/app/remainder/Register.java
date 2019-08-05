package com.app.remainder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.service.autofill.ImageTransformation;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    TextInputEditText pass, email,userid,mno;
    Button registerbtn;
    DatabaseReference reff,ureff;
    Registered_Members members;
    ProgressBar adminbar;
    FirebaseAuth mauth;

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        email = findViewById(R.id.mail);
        pass = findViewById(R.id.pass);
        userid =findViewById(R.id.uid);
        mno =findViewById(R.id.mno);
        adminbar = findViewById(R.id.adminbar);
        registerbtn = (Button) findViewById(R.id.regbtn);
        members = new Registered_Members();
        mauth = FirebaseAuth.getInstance();
        final Remainders remainders = new Remainders();
        reff = FirebaseDatabase.getInstance().getReference().child("Registered_Members");
        ureff = FirebaseDatabase.getInstance().getReference().child("Remainders");
        textView = (TextView) findViewById(R.id.signinoption);

        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mailid = email.getText().toString().trim();
                final  String unm = userid.getText().toString().trim();
                final String pwd = pass.getText().toString().trim();
                final String mobile = mno.getText().toString().trim();

                if (!mailid.isEmpty() && !pwd.isEmpty()) {

                    mauth.createUserWithEmailAndPassword(mailid, pwd).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                finish();
                                members.setMail(mailid);
                                members.setUserid(unm);
                                //members.setPass(pwd);
                                members.setPhn(mobile);

                                remainders.setRemid(0);
                                remainders.setNote("No remainders yet");
                                remainders.setTime("00:00");
                                remainders.setDate("--");

                                reff.push().setValue(members);
                                ureff.child(unm).child("0").setValue(remainders);

                                Toast.makeText(Register.this, "Registration done Successfully !!", Toast.LENGTH_LONG).show();

                                //Intent intent = new Intent(Register.this, Login.class);
                                //startActivity(intent);

                            } else {
                                Toast.makeText(Register.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(Register.this, "Please enter all fields", Toast.LENGTH_LONG);

                }
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this,Login.class);
                startActivity(intent);
            }
        });
    }
}
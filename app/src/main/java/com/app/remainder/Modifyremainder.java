package com.app.remainder;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class Modifyremainder extends AppCompatActivity {

    TextView a,b,c,d;
    Remainders notify;
    String m_Text,dt,day,tym,nt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifyremainder);

        a = findViewById(R.id.a);
        b = findViewById(R.id.b);
        c = findViewById(R.id.c);
        d = findViewById(R.id.d);
        // initiate a Switch
        Switch simpleSwitch = (Switch) findViewById(R.id.simpleSwitch);

        // check current state of a Switch (true or false).
        Boolean switchState = simpleSwitch.isChecked();

        notify = new Remainders();
        SharedPreferences share = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        final String usern = share.getString("cuser","");

        final int pos = Home.listPosition;
        final DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("Remainders").child(usern).child(String.valueOf(pos));
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                day = dataSnapshot.child("date").getValue(String.class);
                tym = dataSnapshot.child("time").getValue(String.class);
                nt = dataSnapshot.child("note").getValue(String.class);

                a.setText(day);
                b.setText(tym);
                c.setText(nt);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(Modifyremainder.this);
                builder.setTitle("Set Date");
                final DatePicker datePicker = new  DatePicker(Modifyremainder.this);
                // Set up the input

                builder.setView(datePicker)
                        .setTitle("Set Date")
                        .setPositiveButton(android.R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        int year = datePicker.getYear();
                                        int mon = datePicker.getMonth();
                                        int day = datePicker.getDayOfMonth();
                                        DatePickerDialog datePickerDialog = new DatePickerDialog(Modifyremainder.this,
                                                new DatePickerDialog.OnDateSetListener() {
                                                    @Override
                                                    public void onDateSet(DatePicker datePicker, int year, int mon, int day) {
                                                        String date = day + " / " + (mon + 1) + " / " + year;
                                                    }
                                                }, year, mon, day);
                                        datePickerDialog.show();
                                        String date = day + " / " + (mon + 1) + " / " + year;
                                        notify.setDate(date);
                                        notify.setTime(tym);
                                        notify.setNote(nt);
                                        notify.setRemid(pos);

                                        reff.setValue(notify);
                                        a.setText(dt);
                                        b.setText(tym);
                                        c.setText(nt);
                                        recreate();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final android.support.v7.app.AlertDialog.Builder builder1 = new android.support.v7.app.AlertDialog.Builder(Modifyremainder.this);
                builder1.setTitle("Set Time");

                int hour = 0;
                final TimePicker timePicker = new TimePicker(Modifyremainder.this);
                builder1.setView(timePicker);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    hour = timePicker.getHour();
                }else{
                    hour = timePicker.getCurrentHour();
                }
                int minute = 0;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    minute = timePicker.getMinute();
                }else{
                    minute = timePicker.getCurrentMinute();
                }

                final String tm = updateTime(hour,minute);
                builder1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        notify.setTime(tm);
                        notify.setDate(day);
                        notify.setNote(nt);
                        notify.setRemid(pos);
                        reff.setValue(notify);
                        a.setText(day);
                        b.setText(tm);
                        c.setText(nt);
                        recreate();
                    }
                });
                builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder1.show();
            }
        });

        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final android.support.v7.app.AlertDialog.Builder builder2 = new android.support.v7.app.AlertDialog.Builder(Modifyremainder.this);
                builder2.setTitle("Enter your Note");
                final EditText input = new EditText(Modifyremainder.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

                input.setText("");
                builder2.setView(input);

                builder2.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                        m_Text = input.getText().toString().trim();

                        notify.setNote(m_Text);
                        notify.setDate(day);
                        notify.setTime(tym);
                        notify.setRemid(pos);
                        reff.setValue(notify);
                        a.setText(day);
                        b.setText(tym);
                        c.setText(m_Text);
                        recreate();
                    }
                });
                builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder2.show();
            }
        });
    }
    private String updateTime(int hours, int mins) {

        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";

        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        String myTime = new StringBuilder().append(hours).append(':')
                .append(minutes).append(" ").append(timeSet).toString();

        return myTime;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.home) {

            Intent intent = new Intent(Modifyremainder.this, Login.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}

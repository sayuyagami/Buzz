package com.app.remainder;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Home extends AppCompatActivity {

    FirebaseAuth mauth;
    TextView vuname;
    DatabaseReference reff,userRef;
    String myTime;
    public ArrayList<String> feed = new ArrayList<>();
    Remainders notify;
    public static int listPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mauth = FirebaseAuth.getInstance();
        reff = FirebaseDatabase.getInstance().getReference();
        loaduserinfo();
        FloatingActionButton add = findViewById(R.id.fab);
        final ListView listView = findViewById(R.id.clist);
        vuname = findViewById(R.id.usernamee);
        notify = new Remainders();
        SharedPreferences share = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        final String usern = share.getString("cuser","");
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        userRef = rootRef.child("Remainders").child(usern);
        final ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Integer cid = ds.child("remid").getValue(int.class);
                    String dd = ds.child("date").getValue(String.class);
                    String tym = ds.child("time").getValue(String.class);
                    String nt = ds.child("note").getValue(String.class);

                    if (cid != 0){
                            feed.add("\n" + dd + "\n\n" + tym + "\n" + "Note :" + nt + "\n" + "");
                        }else {
                            feed.add("\n" + " No Remainders yet " + "\n");
                    }
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Home.this,android.R.layout.simple_list_item_1,feed);
                listView.setAdapter(arrayAdapter);
                listView.setBackgroundColor(Color.rgb(225, 207, 227));
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        userRef.addListenerForSingleValueEvent(eventListener);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int i,
                                    final long id) {
                listPosition = i+1;
                Intent intent = new Intent(Home.this,Modifyremainder.class);
                startActivity(intent);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                ValueEventListener event = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            Integer cid = ds.child("remid").getValue(int.class);

                            if (cid != 0){
                                int n = 1;
                                cid += n;
                            }else {
                                userRef.child(String.valueOf(cid)).setValue(null);
                                cid = 1;
                            }

                            final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(Home.this);

                            final android.support.v7.app.AlertDialog.Builder builder1 = new android.support.v7.app.AlertDialog.Builder(Home.this);
                            builder1.setTitle("Set Time");

                            final android.support.v7.app.AlertDialog.Builder builder2 = new android.support.v7.app.AlertDialog.Builder(Home.this);
                            builder2.setTitle("Enter your Note");

                            final DatePicker datePicker = new  DatePicker(Home.this);
                            // Set up the input
                            final Integer finalCid = cid;
                            builder.setView(datePicker);
                            builder.setTitle("Set Date");
                            builder.setPositiveButton(android.R.string.ok,
                                    new DialogInterface.OnClickListener() {
                                        @RequiresApi(api = Build.VERSION_CODES.M)
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            int year = datePicker.getYear();
                                            int mon = datePicker.getMonth();
                                            int day = datePicker.getDayOfMonth();
                                            DatePickerDialog datePickerDialog = new DatePickerDialog(Home.this,
                                                    new DatePickerDialog.OnDateSetListener() {
                                                        @Override
                                                        public void onDateSet(DatePicker datePicker, int year, int mon, int day) {
                                                            String date = day + " / " + (mon + 1) + " / " + year;
                                                        }
                                                    }, year, mon, day);

                                            final String date = day + " / " + (mon + 1) + " / " + year;

                                            final TimePicker timePicker = new TimePicker(Home.this);
                                            // Set up the input
                                            builder1.setView(datePicker);
                                            builder1.setTitle("Set Time");
                                            builder1.setPositiveButton(android.R.string.ok,
                                                    new DialogInterface.OnClickListener() {
                                                        @RequiresApi(api = Build.VERSION_CODES.M)
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            int hour = 0;
                                                            builder1.setView(timePicker);
                                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                                hour = timePicker.getHour();
                                                            } else {
                                                                hour = timePicker.getCurrentHour();
                                                            }
                                                            int minute = 0;
                                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                                minute = timePicker.getMinute();
                                                            } else {
                                                                minute = timePicker.getCurrentMinute();
                                                            }

                                                            final String tm = updateTime(hour, minute);
                                                            Toast.makeText(Home.this, "time :" + tm, Toast.LENGTH_LONG).show();

                                                            final EditText input = new EditText(Home.this);
                                                            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

                                                            input.setText("");
                                                            builder2.setView(input);

                                                            builder2.setPositiveButton("Set Remainder", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                                                                    String m_Text = input.getText().toString().trim();
                                                                    // String dt = date.substring(0, date.length() - 23);
                                                                    notify.setRemid(finalCid);
                                                                    notify.setTime(tm);
                                                                    notify.setDate(date);
                                                                    notify.setNote(m_Text);
                                                                    reff.child("Remainders").child(usern).child(String.valueOf(finalCid)).setValue(notify);

                                                                    Toast.makeText(Home.this, "Remainder set", Toast.LENGTH_LONG).show();
                                                                    recreate();
                                                                }
                                                            });
                                                            builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    dialog.cancel();
                                                                }
                                                            });
                                                            builder2.show();
                                                        }
                                                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            });
                                            builder1.show();
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
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Home.this, android.R.layout.simple_list_item_1, feed);
                        listView.setAdapter(arrayAdapter);
                        arrayAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                };userRef.addListenerForSingleValueEvent(event);
            }
        });
    }

    public String updateTime(int hours, int mins) {

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

        myTime = new StringBuilder().append(hours).append(':')
                .append(minutes).append(" ").append(timeSet).toString();

        return myTime;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mauth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, Login.class));
        }
    }

    private void loaduserinfo() {
        FirebaseUser user = mauth.getCurrentUser();

        reff.child("Registered_Members").orderByChild("mail").equalTo(user.getEmail()).limitToFirst(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String uemail = ds.child("mail").getValue(String.class);
                    String uname = ds.child("userid").getValue(String.class);
                    String phnum = ds.child("phn").getValue(String.class);


                    vuname.setText("Welcome " + uname);

                    SharedPreferences share = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = share.edit();
                    editor.putString("mail",uemail);
                    editor.putString("cuser",uname);
                    editor.putString("phnumber",phnum);
                    editor.apply();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.taskbar, menu);
        menu.getItem(0).getSubMenu().getItem(0).setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(Home.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            Toast.makeText(Home.this, "Successfully Logged Out", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }
}
package com.example.societysyncsecretary.Features;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.societysyncsecretary.CommonClasses.Home;
import com.example.societysyncsecretary.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class AddNotice extends AppCompatActivity {

    private EditText editTextNotice;
    private Button buttonAdd;
    ProgressDialog progressDialog;
    String id;
    int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notice);

        editTextNotice = findViewById(R.id.editTextNotice);
        buttonAdd = findViewById(R.id.buttonNotice);
        progressDialog = new ProgressDialog(AddNotice.this);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkNotice();
            }
        });

    }

    public void checkNotice(){
        if(editTextNotice.getText().toString().isEmpty()){
            editTextNotice.setError("Notice cannot be empty");
            editTextNotice.requestFocus();
            return;
        }
        addNotice();
    }

    public void addNotice(){
        String sessionKey = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference firebaseDatabaseSocietyId = FirebaseDatabase.getInstance().getReference().child("secretary_session").child(sessionKey);
        firebaseDatabaseSocietyId.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    Toast.makeText(AddNotice.this, "Please register yourself first", Toast.LENGTH_SHORT).show();

                } else {
                    Map<StringBuffer, String> map = (Map) dataSnapshot.getValue();
                    id = map.get("society_id");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.setMessage("Please Wait");
                progressDialog.show();
                final DatabaseReference firebaseDatabaseNotice = FirebaseDatabase.getInstance().getReference().child("societies").child(id).child("notices");
                firebaseDatabaseNotice.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) {
                            Toast.makeText(AddNotice.this, "Please register yourself first", Toast.LENGTH_SHORT).show();

                        } else {
                            count = 0;
                            for (DataSnapshot snap: dataSnapshot.getChildren()) {
                                count++;
                            }
                            count++;
                            Toast.makeText(AddNotice.this,String.valueOf(count), Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }, 4000);
        final Handler handler1 = new Handler(Looper.getMainLooper());
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                try{
                    final DatabaseReference firebaseDatabaseNotice = FirebaseDatabase.getInstance().getReference().child("societies").child(id).child("notices").child("notice"+count);
                    firebaseDatabaseNotice.child("date").setValue(getCurrentDateForId());
                    firebaseDatabaseNotice.child("time").setValue(getCurrentTime());
                    firebaseDatabaseNotice.child("message").setValue(editTextNotice.getText().toString());
                    finish();
                    Intent intent = new Intent(AddNotice.this, Home.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    progressDialog.dismiss();
                    startActivity(intent);

                }catch(Exception e){
                    Toast.makeText(AddNotice.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, 8000);
    }

    public String getCurrentDate()
    {
        DateFormat df = new SimpleDateFormat("dd:MM:yy");
        java.util.Date dateobj = new Date();
        String date = df.format(dateobj);
        return date;
    }
    public String getCurrentDateForId()
    {
        DateFormat df = new SimpleDateFormat("dd-MM-yy");
        java.util.Date dateobj = new Date();
        String date = df.format(dateobj);
        return date;
    }
    public String getCurrentTime()
    {
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        Date timeobj = new Date();
        String time = df.format(timeobj);
        return time;
    }

    public void onBackPressed() {

        finish();
        Intent intent = new Intent(AddNotice.this, Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }



}
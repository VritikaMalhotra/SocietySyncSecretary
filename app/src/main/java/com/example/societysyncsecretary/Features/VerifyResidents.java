package com.example.societysyncsecretary.Features;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.societysyncsecretary.CommonClasses.Home;
import com.example.societysyncsecretary.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class VerifyResidents extends AppCompatActivity {

    private ArrayList<String> mobile = new ArrayList<String>();
    private ArrayList<String> name = new ArrayList<String>();
    private ArrayList<String> flat = new ArrayList<String>();
    private ArrayList<String> block = new ArrayList<String>();
    private ArrayList<String> session_id = new ArrayList<String>();
    ProgressDialog progressDialog;
    String id;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_residents);

        progressDialog = new ProgressDialog(VerifyResidents.this);
        progressDialog.setMessage("Please wait for a moment");
        progressDialog.show();

        getData();
    }
    private class MyAdapter extends ArrayAdapter<String> {

        Context context;
        ArrayList<String> mobile;
        ArrayList<String> name;
        ArrayList<String> flat;
        ArrayList<String> block;


        MyAdapter(Context c, ArrayList<String> mobile, ArrayList<String> name,ArrayList<String> flat,ArrayList<String> block) {
            super(c, R.layout.list_view_verify_residents, R.id.textViewNameVerifyResident, mobile);
            this.context = c;
            this.mobile = mobile;
            this.name = name;
            this.flat = flat;
            this.block = block;
        }
        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.list_view_verify_residents, parent, false);

            TextView textViewMobileVerifyResident = row.findViewById(R.id.textViewMobileVerifyResident);
            TextView textViewNameVerifyResident = row.findViewById(R.id.textViewNameVerifyResident);
            TextView textViewFlatVerifyResident = row.findViewById(R.id.textViewFlatVerifyResident);
            TextView textViewBlockVerifyResident = row.findViewById(R.id.textViewBlockVerifyResident);
            Button buttonAcceptVerifyResident = row.findViewById(R.id.buttonAcceptVerifyResident);
            Button buttonRejectVerifyResident = row.findViewById(R.id.buttonRejectVerifyResident);
            buttonAcceptVerifyResident.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateStatus(position,"true");
                    Toast.makeText(context, "Button"+position, Toast.LENGTH_SHORT).show();
                }
            });
            buttonRejectVerifyResident.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateStatus(position,"rejected");
                    Toast.makeText(context, "Button"+position, Toast.LENGTH_SHORT).show();
                }
            });

            textViewMobileVerifyResident.setText(mobile.get(position));
            textViewFlatVerifyResident.setText(flat.get(position));
            textViewBlockVerifyResident.setText(block.get(position));
            textViewNameVerifyResident.setText(name.get(position));

            return row;
        }
    }
    public void getData(){
        String sessionKey = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference firebaseDatabaseSocietyId = FirebaseDatabase.getInstance().getReference().child("secretary_session").child(sessionKey);
        firebaseDatabaseSocietyId.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    Toast.makeText(VerifyResidents.this, "Please register yourself first", Toast.LENGTH_SHORT).show();

                } else {
                    Map<StringBuffer, String> map = (Map) dataSnapshot.getValue();
                    id = map.get("society_id");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final Handler handler2 = new Handler(Looper.getMainLooper());
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {

                final DatabaseReference firebaseDatabaseCheckStatus = FirebaseDatabase.getInstance().getReference().child("societies").child(id).child("resident_registration");
                firebaseDatabaseCheckStatus.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) {
                            Toast.makeText(VerifyResidents.this, "Please register yourself first", Toast.LENGTH_SHORT).show();

                        } else {
                            collectLogs((Map<String,Object>) dataSnapshot.getValue());
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }, 4000);
    }
    private void collectLogs(Map<String,Object> users) {

        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()) {
            //Get user map
            Map singleUser = (Map) entry.getValue();

            //Get phone field and append to list
            //phoneNumbers.add((String) singleUser.get("mobile_no"));
            //pinNumbers.add(pin);
            String status = (String) singleUser.get("status");

            if(status.equals("false")){
                name.add((String) singleUser.get("name"));
                mobile.add((String) singleUser.get("mobileno"));
                flat.add((String) singleUser.get("flat_no"));
                block.add((String) singleUser.get("block_no"));
                session_id.add((String) singleUser.get("session_id"));
            }

        }
        listView = findViewById(R.id.listViewVerifyResident);
        MyAdapter adapter = new MyAdapter(this,mobile,name,flat,block);
        listView.setAdapter(adapter);
        progressDialog.dismiss();
    }
    public void updateStatus(final int position, final String st){
        Toast.makeText(this, name.get(position), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, session_id.get(position), Toast.LENGTH_SHORT).show();

        String sessionKey = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference firebaseDatabaseSocietyId = FirebaseDatabase.getInstance().getReference().child("secretary_session").child(sessionKey);
        firebaseDatabaseSocietyId.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    Toast.makeText(VerifyResidents.this, "Please register yourself first", Toast.LENGTH_SHORT).show();

                } else {
                    Map<StringBuffer, String> map = (Map) dataSnapshot.getValue();
                    id = map.get("society_id");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final Handler handler2 = new Handler(Looper.getMainLooper());
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {

                final DatabaseReference firebaseDatabaseStatus = FirebaseDatabase.getInstance().getReference().child("societies").child(id).child("resident_registration").child(session_id.get(position));
                firebaseDatabaseStatus.child("status").setValue(st);
                Toast.makeText(VerifyResidents.this, "Status set", Toast.LENGTH_SHORT).show();
                finish();
                Intent intent = new Intent(VerifyResidents.this, VerifyResidents.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }, 4000);
    }

    public void onBackPressed() {

        finish();
        Intent intent = new Intent(VerifyResidents.this, Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
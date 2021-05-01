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
import java.util.List;
import java.util.Map;

public class ViewComplaints extends AppCompatActivity {

    private ArrayList<String> date = new ArrayList<String>();
    private ArrayList<String> time = new ArrayList<String>();
    private ArrayList<String> message = new ArrayList<String>();
    private ArrayList<String> flat = new ArrayList<String>();
    private ArrayList<String> block = new ArrayList<String>();
    ProgressDialog progressDialog;
    String id;
    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_complaints);

        progressDialog = new ProgressDialog(ViewComplaints.this);
        progressDialog.setMessage("Please wait for a moment");
        progressDialog.show();

        getData();
    }

    private class MyAdapter extends ArrayAdapter<String>{

        Context context;
        ArrayList<String> date;
        ArrayList<String> time;
        ArrayList<String> message;
        ArrayList<String> flat;
        ArrayList<String> block;



        MyAdapter(Context c, ArrayList<String> date, ArrayList<String> time, ArrayList<String> message,ArrayList<String> flat,ArrayList<String> block) {
            super(c, R.layout.list_view_with_button, R.id.textViewDateComplaint, date);
            this.context = c;
            this.date = date;
            this.time = time;
            this.message = message;
            this.flat = flat;
            this.block = block;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.list_view_with_button, parent, false);
            TextView textViewDate = row.findViewById(R.id.textViewDateComplaint);
            TextView textViewTime = row.findViewById(R.id.textViewTimeComplaint);
            TextView textViewMessage = row.findViewById(R.id.textViewMessageComplaint);
            TextView textViewFlat = row.findViewById(R.id.textViewFlatComplaint);
            TextView textViewBlock = row.findViewById(R.id.textViewBlockComplaint);
            Button buttonAcceptComplaint = row.findViewById(R.id.buttonAcceptComplaint);
            buttonAcceptComplaint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateStatus(position);
                    Toast.makeText(context, "Button"+position, Toast.LENGTH_SHORT).show();
                }
            });

            textViewDate.setText(date.get(position));
            textViewTime.setText(time.get(position));
            textViewMessage.setText(message.get(position));
            textViewFlat.setText(flat.get(position));
            textViewBlock.setText(block.get(position));

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
                    Toast.makeText(ViewComplaints.this, "Please register yourself first", Toast.LENGTH_SHORT).show();

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

                final DatabaseReference firebaseDatabaseCheckStatus = FirebaseDatabase.getInstance().getReference().child("societies").child(id).child("resident_complaints");
                firebaseDatabaseCheckStatus.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) {
                            Toast.makeText(ViewComplaints.this, "Please register yourself first", Toast.LENGTH_SHORT).show();

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
                date.add((String) singleUser.get("Date"));
                time.add((String) singleUser.get("time"));
                message.add((String) singleUser.get("complaint"));
                flat.add((String) singleUser.get("flat_no"));
                block.add((String) singleUser.get("block_no"));
            }

        }
        listView = findViewById(R.id.listViewComplaints);
        MyAdapter adapter = new MyAdapter(this,date,time,message,flat,block);
        listView.setAdapter(adapter);
        progressDialog.dismiss();
    }
    public void updateStatus(final int position){
        Toast.makeText(this, date.get(position), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, time.get(position), Toast.LENGTH_SHORT).show();

        String sessionKey = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference firebaseDatabaseSocietyId = FirebaseDatabase.getInstance().getReference().child("secretary_session").child(sessionKey);
        firebaseDatabaseSocietyId.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    Toast.makeText(ViewComplaints.this, "Please register yourself first", Toast.LENGTH_SHORT).show();

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
                String root = flat.get(position)+"_"+block.get(position)+"_"+date.get(position)+"_"+time.get(position);
                final DatabaseReference firebaseDatabaseStatus = FirebaseDatabase.getInstance().getReference().child("societies").child(id).child("resident_complaints").child(root);
                firebaseDatabaseStatus.child("status").setValue("true");
                Toast.makeText(ViewComplaints.this, "Status set to true", Toast.LENGTH_SHORT).show();
                finish();
                Intent intent = new Intent(ViewComplaints.this, ViewComplaints.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }, 4000);
    }

    public void onBackPressed() {

        finish();
        Intent intent = new Intent(ViewComplaints.this, Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
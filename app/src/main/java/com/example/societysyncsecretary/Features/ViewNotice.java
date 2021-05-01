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

public class ViewNotice extends AppCompatActivity {

    ListView listView;
    String id;
    ProgressDialog progressDialog;
    ArrayList<String> date = new ArrayList<>();
    ArrayList<String> time = new ArrayList<>();
    ArrayList<String> message = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notice);

        progressDialog = new ProgressDialog(ViewNotice.this);
        progressDialog.setMessage("Please wait for a moment");
        progressDialog.show();

        getData();
    }

    class MyAdapter extends ArrayAdapter<String> {
        Context context;
        ArrayList<String> date;
        ArrayList<String> time;
        ArrayList<String> message;


        MyAdapter(Context c, ArrayList<String> date, ArrayList<String> time, ArrayList<String> message) {
            super(c, R.layout.row_notices, R.id.textViewDateNotice, date);
            this.context = c;
            this.date = date;
            this.time = time;
            this.message = message;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row_notices, parent, false);
            TextView textViewDate = row.findViewById(R.id.textViewDateNotice);
            TextView textViewTime = row.findViewById(R.id.textViewTimeNotice);
            TextView textViewMessage = row.findViewById(R.id.textViewMessageNotice);

            textViewDate.setText(date.get(position));
            textViewTime.setText(time.get(position));
            textViewMessage.setText(message.get(position));

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
                    Toast.makeText(ViewNotice.this, "Please register yourself first", Toast.LENGTH_SHORT).show();

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

                final DatabaseReference firebaseDatabaseCheckStatus = FirebaseDatabase.getInstance().getReference().child("societies").child(id).child("notices");
                firebaseDatabaseCheckStatus.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) {
                            Toast.makeText(ViewNotice.this, "Please register yourself first", Toast.LENGTH_SHORT).show();

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
            date.add((String) singleUser.get("date"));
            time.add((String) singleUser.get("time"));
            message.add((String) singleUser.get("message"));
        }


        listView = findViewById(R.id.listViewNotices);
        MyAdapter adapter = new MyAdapter(this,date,time,message);
        listView.setAdapter(adapter);
        progressDialog.dismiss();
    }

    public void onBackPressed() {

        finish();
        Intent intent = new Intent(ViewNotice.this, Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
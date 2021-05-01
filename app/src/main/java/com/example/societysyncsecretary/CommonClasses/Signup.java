package com.example.societysyncsecretary.CommonClasses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.societysyncsecretary.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signup extends AppCompatActivity {

    private EditText editTextFullName, editTextMobileNumber, editTextEmail, editTextFlatNumber, editTextBlockNumber, editTextSocietyId, editTextPassword, editTextConfirmPassword;
    String fullName,email,password,confirmPassword,flatNumber,blockNumber,societyId,mobileNumber;
    Button buttonSignupSignup;
    TextView textViewLoginSignup;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editTextFullName = findViewById(R.id.editTextFullName);
        editTextMobileNumber = findViewById(R.id.editTextMobileNumber);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextFlatNumber = findViewById(R.id.editTextFlatNumber);
        editTextBlockNumber = findViewById(R.id.editTextBlockNumber);
        editTextSocietyId = findViewById(R.id.editTextSocietyId);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);

        buttonSignupSignup = findViewById(R.id.buttonSignupSignup);
        textViewLoginSignup = findViewById(R.id.textViewLoginSignup);

        mAuth = FirebaseAuth.getInstance();

        buttonSignupSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
        textViewLoginSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Signup.this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });
    }
    public void registerUser(){
        Toast.makeText(this, "in register user", Toast.LENGTH_SHORT).show();
        fullName = editTextFullName.getText().toString().trim();
        email = editTextEmail.getText().toString().trim();
        mobileNumber = editTextMobileNumber.getText().toString().trim();
        flatNumber = editTextFlatNumber.getText().toString().trim();
        blockNumber = editTextBlockNumber.getText().toString().trim();
        password = editTextPassword.getText().toString().trim();
        confirmPassword = editTextConfirmPassword.getText().toString().trim();
        societyId = editTextSocietyId.getText().toString().trim();

        if(fullName.isEmpty()){
            editTextFullName.setError("Full name is required");
            editTextFullName.requestFocus();
            return;
        }
        if(email.isEmpty()){
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            editTextEmail.setError("Please enter a correct email!");
            editTextEmail.requestFocus();
            return;
        }
        if(mobileNumber.isEmpty()){
            editTextMobileNumber.setError("<obile number is required");
            editTextMobileNumber.requestFocus();
            return;
        }
        if(flatNumber.isEmpty()){
            editTextFlatNumber.setError("Flat number is required");
            editTextFlatNumber.requestFocus();
            return;
        }
        if(blockNumber.isEmpty()){
            editTextBlockNumber.setError("Block number is required");
            editTextBlockNumber.requestFocus();
            return;
        }
        if(password.isEmpty()){
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }
        if(password.length()<6)
        {
            editTextPassword.setError("Minnimum length of password should be 6");
            editTextPassword.requestFocus();
            return;
        }
        if(confirmPassword.isEmpty())
        {
            editTextConfirmPassword.setError("Confirmed password required");
            editTextConfirmPassword.requestFocus();
            return;
        }
        if(!(password.equals(confirmPassword)))
        {
            editTextConfirmPassword.setError("Password does not match");
            editTextConfirmPassword.requestFocus();
            return;
        }

        if(societyId.isEmpty()){
            editTextSocietyId.setError("Society id is required");
            editTextSocietyId.requestFocus();
            return;
        }


        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful())
                {
                    Toast.makeText(Signup.this, "in create user", Toast.LENGTH_SHORT).show();
                    DatabaseReference databaseReferenceSocietyId = FirebaseDatabase.getInstance().getReference().child("secretary_session").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    databaseReferenceSocietyId.child("society_id").setValue(societyId);
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("societies").child(societyId).child("secretary_registration").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    databaseReference.child("name").setValue(fullName);
                    databaseReference.child("mobileno").setValue(mobileNumber);
                    databaseReference.child("email").setValue(email);
                    databaseReference.child("flat_number").setValue(flatNumber);
                    databaseReference.child("block_number").setValue(blockNumber);
                    databaseReference.child("society_id").setValue(societyId);
                    databaseReference.child("status").setValue("false");
                    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    //String name = societyId;
                    //boolean inserted = databaseHelper.insertData(id,name);
                    Toast.makeText(Signup.this, "User Registered Successfully!", Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(Signup.this, Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
                else
                {
                    if(task.getException() instanceof FirebaseAuthUserCollisionException)
                    {
                        Toast.makeText(Signup.this, "You are already registered", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(Signup.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }
}
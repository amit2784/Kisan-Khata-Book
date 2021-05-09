package com.ask2784.kisankhatabook;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    String UID;
    DocumentReference firebaseFirestore;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    TextInputEditText Name_profile, Mobile_profile, Email_profile;
    String UserMobile;
    String UserEmail;
    Button updateBtn;
    ProgressDialog progressDialog;
    NetworkCheck networkCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        networkCheck = new NetworkCheck(getApplicationContext());
        findViewByIdMethod();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        UID = firebaseUser.getUid();
        UserMobile = firebaseUser.getPhoneNumber();
        Mobile_profile.setText(UserMobile);
        UserEmail = firebaseUser.getEmail();
        Email_profile.setText(UserEmail);

        firebaseFirestore = FirebaseFirestore.getInstance().collection("Users").document(UID);


        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Updating");
        updateBtn.setOnClickListener(v -> {
            String Name = Name_profile.getText().toString().trim();
            String Mobile = Mobile_profile.getText().toString().trim();
            String Email = Email_profile.getText().toString().trim();
            progressDialog.show();
            if (!Name.isEmpty()) {
                if (Mobile.length() == 13 && Mobile.startsWith("+91")) {
                    if (!Email.isEmpty()) {
                        if (Email.endsWith("@gmail.com")
                                || Email.endsWith("@hotmail.com")
                                || Email.endsWith("@outlook.com")
                                || Email.endsWith("@yahoo.com")
                                || Email.endsWith("@yahoo.in")
                                || Email.endsWith("@rediffmail.com")) {

                            Map<String, Object> data = new HashMap<>();
                            data.put("Name", Name);
                            data.put("Email", Email);
                            data.put("Mobile", Mobile);
                            if (networkCheck.noNetwork()) {
                                firebaseFirestore.set(data, SetOptions.merge());
                                progressDialog.dismiss();
                                Toast.makeText(ProfileActivity.this, "Updated Successfully Offline", Toast.LENGTH_SHORT).show();
                            } else {
                                firebaseFirestore.set(data, SetOptions.merge()).addOnSuccessListener(aVoid -> {
                                    progressDialog.dismiss();
                                    Toast.makeText(ProfileActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                                }).addOnFailureListener(e -> {
                                    progressDialog.dismiss();
                                    Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                            }
                        } else {
                            Email_profile.setError("Enter Correct Email");
                        }
                    } else {
                        Email_profile.setError("Enter Email");
                    }
                } else {
                    Mobile_profile.setError("Enter Mobile\nOR\nAdd +91");
                }
            } else {
                Name_profile.setError("Enter Name");
            }
        });
    }

    private void findViewByIdMethod() {
        Name_profile = findViewById(R.id.textInputEditText_name_profile);
        Mobile_profile = findViewById(R.id.textInputEditText_mobile_profile);
        Email_profile = findViewById(R.id.textInputEditText_email_profile);
        updateBtn = findViewById(R.id.update_profile);
    }

    @Override
    protected void onStart() {
        super.onStart();
        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        if (!Mobile_profile.getText().toString().isEmpty()) {
            Mobile_profile.setEnabled(false);
            firebaseFirestore.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Email_profile.setText(documentSnapshot.getString("Email"));
                    Name_profile.setText(documentSnapshot.getString("Name"));
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(ProfileActivity.this, "Update Profile Needed", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
        } else if (!Email_profile.getText().toString().isEmpty()) {
            Email_profile.setEnabled(false);
            firebaseFirestore.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Mobile_profile.setText(documentSnapshot.getString("Mobile"));
                    Name_profile.setText(documentSnapshot.getString("Name"));
                    progressDialog.dismiss();
                } else {

                    progressDialog.dismiss();
                    Toast.makeText(ProfileActivity.this, "Update Profile Needed", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            progressDialog.dismiss();
            Toast.makeText(this, "There is Some Error", Toast.LENGTH_SHORT).show();
        }
    }
}
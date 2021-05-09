package com.ask2784.kisankhatabook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddFarmerActivity extends AppCompatActivity {
    CollectionReference firebaseFirestore;
    ProgressDialog progressDialog;
    ArrayList<String> arrayList, arrayList_far;
    String UID, village;
    ArrayAdapter<String> arrayAdapter;
    TextInputEditText farmer, fatherName, mobile, email;
    AutoCompleteTextView autoCompleteTextView;
    NetworkCheck networkCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_farmer);
        findViewByIdMethod();
        networkCheck = new NetworkCheck(getApplicationContext());

        Intent intent = getIntent();
        UID = intent.getStringExtra("Uid");

        firebaseFirestore = FirebaseFirestore.getInstance().collection("Users").document(UID).collection("Villages");
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please Wait");

        fetchVillage();

        arrayList_far = new ArrayList<>();
        arrayList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, arrayList);
        autoCompleteTextView.setAdapter(arrayAdapter);

        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String sel_vill = parent.getItemAtPosition(position).toString();
            village = sel_vill.trim();
        });
    }

    private void findViewByIdMethod() {
        farmer = findViewById(R.id.textInputEditText1);
        fatherName = findViewById(R.id.textInputEditText2);
        mobile = findViewById(R.id.textInputEditText3);
        email = findViewById(R.id.textInputEditText4);
        autoCompleteTextView = findViewById(R.id.sel_village_add_far);

    }

    private void fetchVillage() {
        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        firebaseFirestore
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                for (QueryDocumentSnapshot vill : queryDocumentSnapshots)
                    arrayList.add(vill.getString("Village"));
                arrayAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            } else {
                progressDialog.dismiss();
                Toast.makeText(this, "No Village Found\nAdd New Village", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    public void addFarmerMethod(View view) {
        String Farmer = farmer.getText().toString().trim();
        String FatherName = fatherName.getText().toString().trim();
        String Email = email.getText().toString().trim();
        String Mobile = mobile.getText().toString().trim();
        progressDialog.show();
        if (!Farmer.isEmpty()) {
            if (!FatherName.isEmpty()) {
                if (Mobile.length() == 10) {
                    if (Email.endsWith("@gmail.com")
                            || Email.endsWith("@hotmail.com")
                            || Email.endsWith("@outlook.com")
                            || Email.endsWith("@yahoo.com")
                            || Email.endsWith("@yahoo.in")
                            || Email.endsWith("@rediffmail.com")) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("Name", Farmer);
                        data.put("Father's Name", FatherName);
                        data.put("Email", Email);
                        data.put("Mobile", Mobile);
                        firebaseFirestore.document(village)
                                .collection("Farmers")
                                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                            for (QueryDocumentSnapshot farmerList : queryDocumentSnapshots)
                                arrayList_far.add(farmerList.getString("Name"));
                            if (!arrayList_far.contains(Farmer)) {
                                if (networkCheck.noNetwork()) {
                                    firebaseFirestore.document(village)
                                            .collection("Farmers").document(Farmer)
                                            .set(data);
                                    farmer.setText("");
                                    fatherName.setText("");
                                    mobile.setText("");
                                    email.setText("");
                                    progressDialog.dismiss();
                                    Toast.makeText(AddFarmerActivity.this, "Added Successfully Offline", Toast.LENGTH_SHORT).show();
                                } else {
                                    firebaseFirestore.document(village)
                                            .collection("Farmers").document(Farmer)
                                            .set(data).addOnSuccessListener(aVoid -> {
                                        farmer.setText("");
                                        fatherName.setText("");
                                        mobile.setText("");
                                        email.setText("");
                                        progressDialog.dismiss();
                                        Toast.makeText(AddFarmerActivity.this, "Added Successfully", Toast.LENGTH_SHORT).show();
                                    }).addOnFailureListener(e -> {
                                        progressDialog.dismiss();
                                        Toast.makeText(AddFarmerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                                }
                            } else {
                                progressDialog.dismiss();
                                farmer.setError("Farmer Already Exist");
                            }
                        });
                    } else {
                        email.setError("Enter Valid Email");
                    }
                } else {
                    mobile.setError("Enter Valid Mobile");
                }
            } else {
                fatherName.setError("Enter Father Name");
            }
        } else {
            farmer.setError("Enter Farmer Name");
        }
    }


}

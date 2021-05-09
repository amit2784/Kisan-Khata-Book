package com.ask2784.kisankhatabook;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FarmerActivity extends AppCompatActivity {
    AutoCompleteTextView sel_village_del_vil, sel_farmer_far;
    TextInputEditText edit_farmer_email, edit_farmer_mobile, edit_farmer_father, edit_farmer_name;
    CollectionReference firebaseFirestore;
    ProgressDialog progressDialog;
    ArrayList<String> arrayList_Village, arrayList_Farmer;
    ArrayAdapter<String> arrayAdapter_village, arrayAdapter_farmer;
    Button edit_farmer, delete_farmer, yes, no;
    String UID, village = "", farmer = "";
    AlertDialog alertDialog;
    NetworkCheck networkCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please Wait");
        networkCheck = new NetworkCheck(getApplicationContext());
        Intent intent = getIntent();
        UID = intent.getStringExtra("Uid");
        firebaseFirestore = FirebaseFirestore.getInstance().collection("Users").document(UID).collection("Villages");

        findViewByIdMethod();
        fetchVillage();
        SelectMethod();

        LayoutInflater inflater = LayoutInflater.from(this);
        View view1 = inflater.inflate(R.layout.alert_dialog1, null);
        yes = view1.findViewById(R.id.done1);
        no = view1.findViewById(R.id.cancel1);
        alertDialog = new AlertDialog.Builder(this)
                .setView(view1)
                .create();
    }

    private void findViewByIdMethod() {
        sel_village_del_vil = findViewById(R.id.sel_village_del_vil);
        sel_farmer_far = findViewById(R.id.sel_farmer_far);
        edit_farmer = findViewById(R.id.edit_farmer);
        delete_farmer = findViewById(R.id.delete_farmer);
        edit_farmer_name = findViewById(R.id.textInputEditText_edit_farmer_name);
        edit_farmer_father = findViewById(R.id.textInputEditText_edit_farmer_father_name);
        edit_farmer_mobile = findViewById(R.id.textInputEditText_edit_farmer_mobile);
        edit_farmer_email = findViewById(R.id.textInputEditText_edit_farmer_email);
    }

    private void SelectMethod() {
        arrayList_Village = new ArrayList<>();
        arrayAdapter_village = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, arrayList_Village);
        sel_village_del_vil.setAdapter(arrayAdapter_village);
        sel_village_del_vil.setOnItemClickListener((parent, view, position, id) -> {
            village = parent.getItemAtPosition(position).toString();
            arrayList_Farmer.clear();
            fetchFarmer(village);
            arrayAdapter_farmer.notifyDataSetChanged();
            farmer = "";
            edit_farmer_name.setText("");
            edit_farmer_father.setText("");
            edit_farmer_mobile.setText("");
            edit_farmer_email.setText("");
        });

        arrayList_Farmer = new ArrayList<>();
        arrayAdapter_farmer = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, arrayList_Farmer);
        sel_farmer_far.setAdapter(arrayAdapter_farmer);
        sel_farmer_far.setOnItemClickListener((parent, view, position, id) -> {
            farmer = parent.getItemAtPosition(position).toString().trim();
            progressDialog.show();
            firebaseFirestore.document(village).collection("Farmers").document(farmer)
                    .get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    edit_farmer_name.setText(documentSnapshot.getString("Name"));
                    edit_farmer_father.setText(documentSnapshot.getString("Father's Name"));
                    edit_farmer_mobile.setText(documentSnapshot.getString("Mobile"));
                    edit_farmer_email.setText(documentSnapshot.getString("Email"));
                    progressDialog.dismiss();
                }
            }).addOnFailureListener(e -> Toast.makeText(FarmerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }

    private void fetchVillage() {
        progressDialog.show();
        firebaseFirestore
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                for (QueryDocumentSnapshot vill : queryDocumentSnapshots)
                    arrayList_Village.add(vill.getString("Village"));
                arrayAdapter_village.notifyDataSetChanged();
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

    private void fetchFarmer(String string) {
        progressDialog.show();
        firebaseFirestore.document(string).collection("Farmers")
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                for (QueryDocumentSnapshot cus : queryDocumentSnapshots)
                    arrayAdapter_farmer.add(cus.getString("Name"));
                arrayAdapter_farmer.notifyDataSetChanged();
                progressDialog.dismiss();
            } else {
                progressDialog.dismiss();
                Toast.makeText(this, "No Farmer Found\nAdd New Farmer", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    public void DeleteFarmerMethod(View view) {
        if (!farmer.isEmpty()) {
            alertDialog.show();
            yes.setOnClickListener(v -> {
                progressDialog.show();
                String farmerdelete = farmer;
                firebaseFirestore.document(village).collection("Farmers")
                        .document(farmerdelete).collection("Entry")
                        .get().addOnCompleteListener(task -> {
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        firebaseFirestore.document(village).collection("Farmers")
                                .document(farmerdelete).collection("Entry")
                                .document(snapshot.getId()).delete();
                    }
                });
                firebaseFirestore.document(village).collection("Farmers")
                        .document(farmerdelete).delete().addOnCompleteListener(task -> {
                    arrayList_Farmer.clear();
                    fetchFarmer(village);
                    arrayAdapter_farmer.notifyDataSetChanged();
                    progressDialog.dismiss();
                });

                farmer = "";
                edit_farmer_name.setText("");
                edit_farmer_father.setText("");
                edit_farmer_mobile.setText("");
                edit_farmer_email.setText("");
                alertDialog.dismiss();
                Toast.makeText(FarmerActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
            });
            no.setOnClickListener(v -> {
                alertDialog.dismiss();
                Toast.makeText(FarmerActivity.this, "Not Deleted", Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(this, "Select Farmer \nOR\nFarmer Not Found", Toast.LENGTH_SHORT).show();
        }
    }

    public void EditFarmerMethod(View view) {
        String EditName = edit_farmer_name.getText().toString().trim();
        String EditFatherName = edit_farmer_father.getText().toString().trim();
        String EditMobile = edit_farmer_mobile.getText().toString().trim();
        String EditEmail = edit_farmer_email.getText().toString().trim();
        progressDialog.show();
        if (!EditName.isEmpty()) {
            if (!EditFatherName.isEmpty()) {
                if (EditMobile.length() == 10) {
                    if (EditEmail.endsWith("@gmail.com")
                            || EditEmail.endsWith("@hotmail.com")
                            || EditEmail.endsWith("@outlook.com")
                            || EditEmail.endsWith("@yahoo.com")
                            || EditEmail.endsWith("@yahoo.in")
                            || EditEmail.endsWith("@rediffmail.com")) {
                        Map<String, Object> NewData = new HashMap<>();
                        NewData.put("Father's Name", EditFatherName);
                        NewData.put("Email", EditEmail);
                        NewData.put("Mobile", EditMobile);
                        if (networkCheck.noNetwork()) {
                            firebaseFirestore.document(village).collection("Farmers").document(farmer)
                                    .update(NewData);
                            progressDialog.dismiss();
                            Toast.makeText(FarmerActivity.this, "Updated Successfully Offline", Toast.LENGTH_SHORT).show();
                        } else {
                            firebaseFirestore.document(village).collection("Farmers").document(farmer)
                                    .update(NewData).addOnSuccessListener(aVoid -> {
                                progressDialog.dismiss();
                                Toast.makeText(FarmerActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                Toast.makeText(FarmerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        }
                    } else {
                        edit_farmer_email.setError("Enter Valid Email");
                    }
                } else {
                    edit_farmer_mobile.setError("Enter Valid Mobile");
                }
            } else {
                edit_farmer_father.setError("Enter Father's Name");
            }
        } else {
            edit_farmer_name.setError("Select Farmer");
        }
    }
}
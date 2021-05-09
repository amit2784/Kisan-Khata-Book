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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddORDeleteVillageActivity extends AppCompatActivity {
    TextInputEditText villageName;
    AutoCompleteTextView sel_village_del_vil;
    String UID, village = "";
    CollectionReference firebaseFirestore;
    Button Add_Village, yes, no;
    TextInputLayout textInputLayout;
    ArrayList<String> arrayList, arrayList_Village;
    ArrayAdapter<String> arrayAdapter_village;
    ProgressDialog progressDialog;
    AlertDialog alertDialog;
    NetworkCheck networkCheck;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_delete_village);
        networkCheck = new NetworkCheck(getApplicationContext());
        Intent intent = getIntent();
        UID = intent.getStringExtra("Uid");

        findViewByIdMethod();

        firebaseFirestore = FirebaseFirestore.getInstance().collection("Users").document(UID).collection("Villages");
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please Wait");

        arrayList = new ArrayList<>();
        fetchVillage();

        arrayList_Village = new ArrayList<>();
        arrayAdapter_village = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, arrayList_Village);
        sel_village_del_vil.setAdapter(arrayAdapter_village);
        sel_village_del_vil.setOnItemClickListener((parent, view, position, id) -> village = parent.getItemAtPosition(position).toString());


        LayoutInflater inflater = LayoutInflater.from(this);
        View view1 = inflater.inflate(R.layout.alert_dialog, null);
        yes = view1.findViewById(R.id.done);
        no = view1.findViewById(R.id.cancel);

        alertDialog = new AlertDialog.Builder(this)
                .setView(view1)
                .create();
    }

    private void findViewByIdMethod() {
        villageName = findViewById(R.id.textInputEditText);
        Add_Village = findViewById(R.id.edit_village);
        textInputLayout = findViewById(R.id.outlinedTextField);
        sel_village_del_vil = findViewById(R.id.sel_village_add_or_del_vil);

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

    public void DeleteVillageMethod(View view) {

        if (!village.isEmpty()) {
            alertDialog.show();
            yes.setOnClickListener(v -> {
                progressDialog.show();
                String villagedelete = village;

                firebaseFirestore.document(villagedelete).collection("Farmers")
                        .get().addOnCompleteListener(task -> {
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        firebaseFirestore.document(villagedelete).collection("Farmers")
                                .document(snapshot.getId()).collection("Entry")
                                .get().addOnCompleteListener(task1 -> {
                            for (QueryDocumentSnapshot snapshot1 : task1.getResult()) {

                                firebaseFirestore.document(villagedelete).collection("Farmers")
                                        .document(snapshot.getId()).collection("Entry")
                                        .document(snapshot1.getId()).delete();
                            }
                            firebaseFirestore.document(villagedelete).collection("Farmers")
                                    .document(snapshot.getId()).delete();


                        });

                    }
                    firebaseFirestore.document(villagedelete).delete();
                    arrayList_Village.clear();
                    fetchVillage();
                    arrayAdapter_village.notifyDataSetChanged();
                    progressDialog.dismiss();
                });
                village = "";
                alertDialog.dismiss();
                Toast.makeText(AddORDeleteVillageActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
            });
            no.setOnClickListener(v -> {
                alertDialog.dismiss();
                Toast.makeText(AddORDeleteVillageActivity.this, "Not Deleted", Toast.LENGTH_SHORT).show();
            });


        } else {

            Toast.makeText(this, "Select Village \nOR\nVillage Not Found", Toast.LENGTH_SHORT).show();
        }

    }

    public void addVillageMethod(View view) {

        String New_Village = villageName.getText().toString().trim();
        if (!New_Village.isEmpty()) {
            progressDialog.show();
            firebaseFirestore
                    .get().addOnSuccessListener(queryDocumentSnapshots -> {
                for (QueryDocumentSnapshot village : queryDocumentSnapshots)
                    arrayList.add(village.getString("Village"));
                if (!arrayList.contains(New_Village)) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("Village", New_Village);
                    if (networkCheck.noNetwork()){
                        firebaseFirestore.document(New_Village)
                                .set(data);
                                    arrayList_Village.clear();
                                    fetchVillage();
                                    arrayAdapter_village.notifyDataSetChanged();
                                    progressDialog.dismiss();
                                    villageName.setText("");
                                    Toast.makeText(AddORDeleteVillageActivity.this, "Added Successfully Offline", Toast.LENGTH_SHORT).show();
                                }else {
                        firebaseFirestore.document(New_Village)
                                .set(data)
                                .addOnSuccessListener(aVoid -> {
                                    arrayList_Village.clear();
                                    fetchVillage();
                                    arrayAdapter_village.notifyDataSetChanged();

                                    progressDialog.dismiss();
                                    villageName.setText("");
                                    Toast.makeText(AddORDeleteVillageActivity.this, "Added Successfully", Toast.LENGTH_SHORT).show();

                                }).addOnFailureListener(e -> {
                            progressDialog.dismiss();
                            Toast.makeText(AddORDeleteVillageActivity.this, "Failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }

                } else {
                    progressDialog.dismiss();
                    villageName.setError("Village Already Exist");
                }
            });

        } else {
            villageName.setError("Enter Village Name");
        }

    }


}
package com.ask2784.kisankhatabook;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener, NavigationView.OnNavigationItemSelectedListener, EntryRecyclerAdapter.EntryListener {
    private AutoCompleteTextView sel_village_v_ent, sel_farmer_far;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private String Mobile, Email, Name, village = "", farmer = "";
    private FloatingActionButton addEntryFab;
    private ArrayList<String> arrayList_Village, arrayList_Farmer;
    private ArrayAdapter<String> arrayAdapter_village, arrayAdapter_farmer;
    private TextView Total_TV, Received_TV, Remain_TV, Mobile_TV;
    private Button call;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private String UID;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(toolbar);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please Wait");

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//            startActivity(new Intent(this,LoginRegisterActivity.class));
//            finish();
            UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            firebaseFirestore = FirebaseFirestore.getInstance();
            //Calling Methods
            findViewByIdMethod();
            fetchVillage();
            SelectMethod();
            callMethod();
            recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            navigationView.bringToFront();
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
            navigationView.setNavigationItemSelectedListener(this);
            addEntryFab.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddEntryActivity.class).putExtra("Uid", UID)));
        }
    }

    private void findViewByIdMethod() {
        drawerLayout = findViewById(R.id.drawer);
        addEntryFab = findViewById(R.id.addEntryFab);
        navigationView = findViewById(R.id.navMenu);
        toolbar = findViewById(R.id.toolbar);
        sel_village_v_ent = findViewById(R.id.sel_village_v_ent);
        sel_farmer_far = findViewById(R.id.sel_farmer_v_ent);
        Total_TV = findViewById(R.id.total_tv);
        Received_TV = findViewById(R.id.received_tv);
        Remain_TV = findViewById(R.id.remaining_tv);
        Mobile_TV = findViewById(R.id.mobile_tv);
        call = findViewById(R.id.call);
//        swipeRefreshLayout = findViewById(R.id.refresh);
        recyclerView = findViewById(R.id.recyclerview_entry);
    }

    private void callMethod() {
        call.setEnabled(false);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + Mobile_TV.getText().toString()));
                startActivity(intent);
            }
        });
    }

    private void SelectMethod() {
        arrayList_Village = new ArrayList<>();
        arrayAdapter_village = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, arrayList_Village);
        sel_village_v_ent.setAdapter(arrayAdapter_village);
        sel_village_v_ent.setOnItemClickListener((parent, view, position, id) -> {
            village = parent.getItemAtPosition(position).toString();
            arrayList_Farmer.clear();
            fetchFarmer(village);
            arrayAdapter_farmer.notifyDataSetChanged();
            farmer = "";
            Total_TV.setText("");
            Received_TV.setText("");
            Remain_TV.setText("");
            Mobile_TV.setText("");

        });

        arrayList_Farmer = new ArrayList<>();
        arrayAdapter_farmer = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, arrayList_Farmer);
        sel_farmer_far.setAdapter(arrayAdapter_farmer);
        sel_farmer_far.setOnItemClickListener((parent, view, position, id) -> {
            farmer = parent.getItemAtPosition(position).toString().trim();
            Total_TV.setText("");
            Received_TV.setText("");
            Remain_TV.setText("");
            Mobile_TV.setText("");
            progressDialog.show();
            initRecyclerView();
            firebaseFirestore.collection("Users").document(UID).collection("Villages").document(village).collection("Farmers").document(farmer)
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            if (value != null) {
                                Mobile_TV.setText(value.get("Mobile").toString());
                                call.setEnabled(true);

                            } else {
                                Toast.makeText(MainActivity.this, "Not Found", Toast.LENGTH_SHORT).show();
                            }
                            progressDialog.dismiss();
                        }
                    });

            firebaseFirestore.collection("Users").document(UID).collection("Villages").document(village).collection("Farmers").document(farmer)
                    .collection("Entry").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    if (value != null) {
                        fetchTRR();

                    } else {
                        Toast.makeText(MainActivity.this, "Not Found", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        });
    }

    private void fetchTRR() {

        firebaseFirestore.collection("Users").document(UID).collection("Villages").document(village).collection("Farmers").document(farmer).collection("Entry")
                .get().addOnCompleteListener(task -> {
            if (!task.getResult().isEmpty()) {
                double total = 0;
                double received = 0;
                for (QueryDocumentSnapshot snapshot : task.getResult()) {
                    double total1 = Double.parseDouble(snapshot.get("total").toString());
                    double received1 = Double.parseDouble(snapshot.get("received").toString());
                    total = total + total1;
                    received = received + received1;

                }

                Total_TV.setText(String.valueOf(Math.round(total * 100) / 100.0));
                Received_TV.setText(String.valueOf(Math.round(received * 100) / 100.0));
                Remain_TV.setText(String.valueOf(Math.round((total - received) * 100) / 100.0));


            } else {
                Toast.makeText(this, "Not Found", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void fetchFarmer(String string) {
        progressDialog.show();
        firebaseFirestore.collection("Users").document(UID).collection("Villages").document(string).collection("Farmers")
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                for (QueryDocumentSnapshot cus : queryDocumentSnapshots)
                    arrayAdapter_farmer.add(cus.getString("Name"));
                arrayAdapter_farmer.notifyDataSetChanged();

            } else {

                Toast.makeText(this, "No Farmer Found\nAdd New Farmer", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void fetchVillage() {
        firebaseFirestore.collection("Users").document(UID).collection("Villages")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        if (value != null) {
                            arrayList_Village.clear();
                            for (QueryDocumentSnapshot vill : value) {
                                arrayAdapter_village.add(vill.getString("Village"));
                                arrayAdapter_village.notifyDataSetChanged();
                            }

                        } else {
                            Toast.makeText(MainActivity.this, "No Village Found\nAdd New Village", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_profile:
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.menu_add_far:
                startActivity(new Intent(MainActivity.this, AddFarmerActivity.class).putExtra("Uid", UID));
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.menu_edit_or_delete_far:
                startActivity(new Intent(MainActivity.this, FarmerActivity.class).putExtra("Uid", UID));
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.menu_add_or_delete_vil:
                startActivity(new Intent(MainActivity.this, AddORDeleteVillageActivity.class).putExtra("Uid", UID));
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.settings:
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.logout:
                AuthUI.getInstance().signOut(this);

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.close:
                finish();
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;

        }
        return true;
    }

    private void initRecyclerView() {
        Query query = firebaseFirestore.collection("Users").document(UID).collection("Villages").document(village).collection("Farmers").document(farmer)
                .collection("Entry")
                .orderBy("date", Query.Direction.DESCENDING).orderBy("addedTime", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<EntryHelper> options = new FirestoreRecyclerOptions.Builder<EntryHelper>()
                .setQuery(query, EntryHelper.class)
                .build();
        EntryRecyclerAdapter entryRecyclerAdapter = new EntryRecyclerAdapter(options, this);
        recyclerView.setAdapter(entryRecyclerAdapter);
        entryRecyclerAdapter.startListening();


    }

    @Override
    public void handleEditReceived(DocumentSnapshot snapshot) {

        EntryHelper entryHelper = snapshot.toObject(EntryHelper.class);

        EditText editText = new EditText(this);
        editText.setSingleLine();
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        editText.setText(String.valueOf(entryHelper.getReceived()));
        editText.setSelection(String.valueOf(entryHelper.getReceived()).length());

        new AlertDialog.Builder(this)
                .setTitle("Received")
                .setView(editText)
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        double total = entryHelper.getTotal();
                        double received = Math.round((Double.parseDouble(editText.getText().toString())) * 100) / 100.0;
                        double remain = Math.round((total - received) * 100) / 100.0;
                        entryHelper.setReceived(received);
                        entryHelper.setRemain(remain);
                        snapshot.getReference().set(entryHelper).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                Toast.makeText(MainActivity.this, "Done", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                })
                .setNegativeButton("Cancel", null)
                .show();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(this);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, LoginRegisterActivity.class));
            finish();
        } else {
//            UID = currentUser.getUid();
            View headerView = navigationView.getHeaderView(0);
            TextView userName = headerView.findViewById(R.id.userName);
            TextView userMobile = headerView.findViewById(R.id.userMobile);
            TextView userEmail = headerView.findViewById(R.id.userEmail);

            firebaseFirestore.collection("Users").document(UID)
                    .get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Name = documentSnapshot.getString("Name");
                    userName.setText(Name);
                    Email = documentSnapshot.getString("Email");
                    userEmail.setText(Email);
                    Mobile = documentSnapshot.getString("Mobile");
                    userMobile.setText(Mobile);
                }
            }).addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginRegisterActivity.class));
            finish();
            return;
        }
        firebaseAuth.getCurrentUser().getIdToken(true)
                .addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                    @Override
                    public void onSuccess(GetTokenResult getTokenResult) {

                    }
                });
    }
}
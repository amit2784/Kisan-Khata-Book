package com.ask2784.kisankhatabook;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class AddEntryActivity extends AppCompatActivity {

    AutoCompleteTextView sel_village_add_entry, sel_farmer_add_entry, sel_implement_add_entry, sel_crop_or_other_add_entry, sel_area_or_time_add_entry;
    TextInputLayout set_area_layout, set_times_layout, set_hour_layout, set_minutes_layout, set_second_layout;
    TextInputEditText set_area, set_times, set_hour, set_minutes, set_second, set_rate, set_received;
    TextView set_date, set_total, set_remain;
    Button set_date_btn;
    CollectionReference firebaseFirestore;
    ProgressDialog progressDialog;
    ArrayList<String> arrayList_village, arrayList_far, arrayList_implement, arrayList_crop_or_other, arrayList_area_or_time;
    String UID, village = "", farmer = "", implement = "", crop_or_other = "", area_or_time = "";
    ArrayAdapter<String> arrayAdapter_village, arrayAdapter_farmer, arrayAdapter_implement, arrayAdapter_crop_or_other, arrayAdapter_area_times;
    Calendar calendar;
    NetworkCheck networkCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);
        findViewByIdMethod();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please Wait");


        networkCheck = new NetworkCheck(getApplicationContext());


        Intent intent = getIntent();
        UID = intent.getStringExtra("Uid");
        firebaseFirestore = FirebaseFirestore.getInstance().collection("Users").document(UID).collection("Villages");

        setVisibilityMethod();
        fetchVillage();
        SelectMethod();

        calendar = Calendar.getInstance();
        set_date_btn.setOnClickListener(v -> {

            int mYear = calendar.get(Calendar.YEAR);
            int mMonth = calendar.get(Calendar.MONTH);
            int mDate = calendar.get(Calendar.DATE);

            DatePickerDialog datePickerDialog = new DatePickerDialog(AddEntryActivity.this, android.R.style.Theme_DeviceDefault_Dialog, (view, year, month, dayOfMonth) -> {
                Time chosenDate = new Time();
                chosenDate.set(dayOfMonth, month, year);
                long dtb = chosenDate.toMillis(true);
                set_date.setText(DateFormat.format("yyyy-MM-dd", dtb));
            }, mYear, mMonth, mDate);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();

        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (area_or_time.equals("Time")) {
                //Hours & Min & Sec
                if (!set_hour.getText().toString().equals("")
                        && !set_minutes.getText().toString().equals("")
                        && !set_second.getText().toString().equals("")
                        && !set_rate.getText().toString().equals("")
                        && !set_rate.getText().toString().equals(".")) {
                    float hours = Float.parseFloat(set_hour.getText().toString());
                    float min = Float.parseFloat(set_minutes.getText().toString());
                    float sec = Float.parseFloat(set_second.getText().toString());
                    float rate = Float.parseFloat(set_rate.getText().toString());
                    set_total.setText(String.valueOf(Math.round(((hours + (min / 60) + (sec / 3600)) * rate) * 100) / 100.0));

                }
                //Hours & Min
                else if (!set_hour.getText().toString().equals("")
                        && !set_minutes.getText().toString().equals("")
                        && !set_rate.getText().toString().equals("")
                        && !set_rate.getText().toString().equals(".")) {
                    float hours = Float.parseFloat(set_hour.getText().toString());
                    float min = Float.parseFloat(set_minutes.getText().toString());
                    float rate = Float.parseFloat(set_rate.getText().toString());
                    set_total.setText(String.valueOf(Math.round(((hours + (min / 60)) * rate) * 100) / 100.0));

                }
                //Hours & Sec
                else if (!set_hour.getText().toString().equals("")
                        && !set_second.getText().toString().equals("")
                        && !set_rate.getText().toString().equals("")
                        && !set_rate.getText().toString().equals(".")) {
                    float hours = Float.parseFloat(set_hour.getText().toString());
                    float sec = Float.parseFloat(set_second.getText().toString());
                    float rate = Float.parseFloat(set_rate.getText().toString());
                    set_total.setText(String.valueOf(Math.round(((hours + (sec / 3600)) * rate) * 100) / 100.0));

                }
                // Min & Sec
                else if (!set_minutes.getText().toString().equals("")
                        && !set_second.getText().toString().equals("")
                        && !set_rate.getText().toString().equals("")
                        && !set_rate.getText().toString().equals(".")) {
                    float sec = Float.parseFloat(set_minutes.getText().toString());
                    float min = Float.parseFloat(set_second.getText().toString());
                    float rate = Float.parseFloat(set_rate.getText().toString());
                    set_total.setText(String.valueOf(Math.round((((min / 60) + (sec / 3600)) * rate) * 100) / 100.0));

                }
                //Only Hour
                else if (!set_hour.getText().toString().equals("")
                        && !set_rate.getText().toString().equals("")
                        && !set_rate.getText().toString().equals(".")) {
                    float hours = Float.parseFloat(set_hour.getText().toString());

                    float rate = Float.parseFloat(set_rate.getText().toString());
                    set_total.setText(String.valueOf(Math.round((hours * rate) * 100) / 100.0));
                }//Only Min
                else if (!set_minutes.getText().toString().equals("")
                        && !set_rate.getText().toString().equals("")
                        && !set_rate.getText().toString().equals(".")) {
                    float min = Float.parseFloat(set_minutes.getText().toString());
                    float rate = Float.parseFloat(set_rate.getText().toString());
                    set_total.setText(String.valueOf(Math.round(((min / 60) * rate) * 100) / 100.0));
                }
                //Only Sec
                else if (!set_second.getText().toString().equals("")
                        && !set_rate.getText().toString().equals("")
                        && !set_rate.getText().toString().equals(".")) {
                    float sec = Float.parseFloat(set_second.getText().toString());
                    float rate = Float.parseFloat(set_rate.getText().toString());
                    set_total.setText(String.valueOf(Math.round(((sec / 3600) * rate) * 100) / 100.0));
                }
                //Area
                else if (!set_area.getText().toString().equals("")
                        && !set_area.getText().toString().equals(".")
                        && !set_times.getText().toString().equals("")
                        && !set_rate.getText().toString().equals(".")
                        && !set_rate.getText().toString().equals("")) {
                    float area = Float.parseFloat(set_area.getText().toString());
                    float times = Float.parseFloat(set_times.getText().toString());
                    float rate = Float.parseFloat(set_rate.getText().toString());
                    set_total.setText(String.valueOf(Math.round((area * rate * times) * 100) / 100.0));
                } else {
                    set_total.setText("");

                }
                if (!set_total.getText().toString().equals("")
                        && !set_received.getText().toString().equals("")) {
                    float totalF = Float.parseFloat(set_total.getText().toString());
                    float receivedF = Float.parseFloat(set_received.getText().toString());
                    set_remain.setText(String.valueOf(Math.round((totalF - receivedF) * 100) / 100.0));
                } else {
                    set_remain.setText("");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        set_minutes.setFilters(new InputFilter[]{new MinMaxFilter(1, 59)});
        set_second.setFilters(new InputFilter[]{new MinMaxFilter(1, 59)});
        set_area.addTextChangedListener(textWatcher);
        set_times.addTextChangedListener(textWatcher);
        set_rate.addTextChangedListener(textWatcher);
        set_hour.addTextChangedListener(textWatcher);
        set_minutes.addTextChangedListener(textWatcher);
        set_second.addTextChangedListener(textWatcher);
        set_received.addTextChangedListener(textWatcher);
    }

    private void findViewByIdMethod() {
        sel_village_add_entry = findViewById(R.id.sel_village_add_entry);
        sel_farmer_add_entry = findViewById(R.id.sel_farmer_add_entry);
        sel_implement_add_entry = findViewById(R.id.sel_implement_add_entry);
        sel_crop_or_other_add_entry = findViewById(R.id.sel_crop_or_other_add_entry);
        sel_area_or_time_add_entry = findViewById(R.id.sel_area_or_time_add_entry);
        set_area = findViewById(R.id.set_area);
        set_times = findViewById(R.id.set_times);
        set_rate = findViewById(R.id.set_rate);
        set_hour = findViewById(R.id.set_hour);
        set_minutes = findViewById(R.id.set_minutes);
        set_second = findViewById(R.id.set_second);
        set_date = findViewById(R.id.set_date);
        set_date_btn = findViewById(R.id.set_date_btn);
        set_total = findViewById(R.id.set_total);
        set_received = findViewById(R.id.set_received);
        set_remain = findViewById(R.id.set_remain);
        set_area_layout = findViewById(R.id.outlinedTextField_area);
        set_times_layout = findViewById(R.id.outlinedTextField_times);
        set_hour_layout = findViewById(R.id.outlinedTextField_hour);
        set_minutes_layout = findViewById(R.id.outlinedTextField_minutes);
        set_second_layout = findViewById(R.id.outlinedTextField_second);
    }

    private void setVisibilityMethod() {
        set_area_layout.setVisibility(View.GONE);
        set_times_layout.setVisibility(View.GONE);
        set_hour_layout.setVisibility(View.GONE);
        set_minutes_layout.setVisibility(View.GONE);
        set_second_layout.setVisibility(View.GONE);

    }

    private void SelectMethod() {
        arrayList_village = new ArrayList<>();
        arrayAdapter_village = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, arrayList_village);
        sel_village_add_entry.setAdapter(arrayAdapter_village);

        sel_village_add_entry.setOnItemClickListener((parent, view, position, id) -> {
            village = parent.getItemAtPosition(position).toString();
            arrayList_far.clear();
            fetchFarmer(village);
            arrayAdapter_farmer.notifyDataSetChanged();
            farmer = "";
        });

        arrayList_far = new ArrayList<>();
        arrayAdapter_farmer = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, arrayList_far);
        sel_farmer_add_entry.setAdapter(arrayAdapter_farmer);
        sel_farmer_add_entry.setOnItemClickListener((parent, view, position, id) -> farmer = parent.getItemAtPosition(position).toString().trim());

        arrayList_implement = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.Implement)));
        arrayAdapter_implement = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, arrayList_implement);
        sel_implement_add_entry.setAdapter(arrayAdapter_implement);
        sel_implement_add_entry.setOnItemClickListener((parent, view, position, id) -> implement = parent.getItemAtPosition(position).toString().trim());

        arrayList_crop_or_other = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.Crop)));
        arrayAdapter_crop_or_other = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, arrayList_crop_or_other);
        sel_crop_or_other_add_entry.setAdapter(arrayAdapter_crop_or_other);
        sel_crop_or_other_add_entry.setOnItemClickListener((parent, view, position, id) -> crop_or_other = parent.getItemAtPosition(position).toString().trim());

        arrayList_area_or_time = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.AreaType)));
        arrayAdapter_area_times = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, arrayList_area_or_time);
        sel_area_or_time_add_entry.setAdapter(arrayAdapter_area_times);
        sel_area_or_time_add_entry.setOnItemClickListener((parent, view, position, id) -> {
            area_or_time = parent.getItemAtPosition(position).toString().trim();

            if (area_or_time.equals("Time")) {
                set_area.setText("");
                set_times.setText("");
                set_area_layout.setVisibility(View.GONE);
                set_times_layout.setVisibility(View.GONE);
                set_hour_layout.setVisibility(View.VISIBLE);
                set_minutes_layout.setVisibility(View.VISIBLE);
                set_second_layout.setVisibility(View.VISIBLE);
            } else {
                set_hour.setText("");
                set_minutes.setText("");
                set_second.setText("");
                set_hour_layout.setVisibility(View.GONE);
                set_minutes_layout.setVisibility(View.GONE);
                set_second_layout.setVisibility(View.GONE);
                set_area_layout.setVisibility(View.VISIBLE);
                set_times_layout.setVisibility(View.VISIBLE);

            }
        });
    }

    private void fetchVillage() {
        progressDialog.show();
        firebaseFirestore
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                for (QueryDocumentSnapshot vill : queryDocumentSnapshots)
                    arrayList_village.add(vill.getString("Village"));
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

    public void addEntryBtn(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Adding Entry")
                .setMessage("Make Sure All Things Are Correct?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addEntry();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void addEntry() {
        String Area = set_area.getText().toString().trim();
        String Times = set_times.getText().toString().trim();
        String Rate = set_rate.getText().toString().trim();
        String Received = set_received.getText().toString().trim();
        String Date = set_date.getText().toString().trim();
        Timestamp timestamp = new Timestamp(new Date());
        if (!village.equals("")) {
            if (!farmer.equals("")) {
                if (!implement.equals("")) {
                    if (!crop_or_other.equals("")) {
                        if (!area_or_time.equals("")) {
                            if (!Rate.isEmpty()) {
                                if (!Received.isEmpty()) {
                                    if (!Date.isEmpty()) {

                                        double RateF = Double.parseDouble(set_rate.getText().toString());

                                        double ReceivedF = Double.parseDouble(set_received.getText().toString());

                                        // For Time
                                        if (area_or_time.equals("Time")) {
                                            String Hours = set_hour.getText().toString().trim();
                                            String Minutes = set_minutes.getText().toString().trim();
                                            String Seconds = set_second.getText().toString().trim();
                                            String time = Hours + " H, " + Minutes + " M, " + Seconds + " S";
                                            if (!Hours.isEmpty() || !Minutes.isEmpty() || !Seconds.isEmpty()) {
                                                double TotalF = Double.parseDouble(set_total.getText().toString());
                                                double RemainF = Double.parseDouble(set_remain.getText().toString());
                                                final double TimesF = 1;
                                                EntryHelper entryHelper = new EntryHelper(Date, implement, crop_or_other, time, TimesF,
                                                        Math.round(RateF * 100) / 100.0, Math.round(TotalF * 100) / 100.0,
                                                        Math.round(ReceivedF * 100) / 100.0, Math.round(RemainF * 100) / 100.0, timestamp);
                                                if (networkCheck.noNetwork()) {
                                                    firebaseFirestore.document(village)
                                                            .collection("Farmers").document(farmer)
                                                            .collection("Entry").document()
                                                            .set(entryHelper);
                                                    set_hour.setText("");
                                                    set_minutes.setText("");
                                                    set_second.setText("");
                                                    set_received.setText("");
                                                    Toast.makeText(AddEntryActivity.this, "Added Successfully Offline", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    firebaseFirestore.document(village)
                                                            .collection("Farmers").document(farmer)
                                                            .collection("Entry").document()
                                                            .set(entryHelper)
                                                            .addOnSuccessListener(aVoid -> {
                                                                set_hour.setText("");
                                                                set_minutes.setText("");
                                                                set_second.setText("");
                                                                set_received.setText("");
                                                                Toast.makeText(AddEntryActivity.this, "Added Successfully", Toast.LENGTH_SHORT).show();
                                                            }).addOnFailureListener(e -> {
                                                        Toast.makeText(AddEntryActivity.this, "Failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    });
                                                }
                                            } else {
                                                set_hour.setError("Enter Hour");
                                            }
                                        }
                                        //For Area
                                        else {
                                            if (!Area.isEmpty()) {
                                                if (!Times.isEmpty() && !Times.startsWith("0")) {

                                                    double TotalF = Double.parseDouble(set_total.getText().toString());
                                                    double RemainF = Double.parseDouble(set_remain.getText().toString());
                                                    double TimesF = Double.parseDouble(set_times.getText().toString());
                                                    EntryHelper entryHelper = new EntryHelper(Date, implement, crop_or_other, Area + " " + area_or_time, TimesF,
                                                            Math.round(RateF * 100) / 100.0, Math.round(TotalF * 100) / 100.0,
                                                            Math.round(ReceivedF * 100) / 100.0, Math.round(RemainF * 100) / 100.0, timestamp);

                                                    if (networkCheck.noNetwork()) {
                                                        firebaseFirestore.document(village)
                                                                .collection("Farmers").document(farmer)
                                                                .collection("Entry").document()
                                                                .set(entryHelper);
                                                        set_area.setText("");
                                                        set_times.setText("");
                                                        set_received.setText("");
                                                        Toast.makeText(AddEntryActivity.this, "Added Successfully Offline", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        firebaseFirestore.document(village)
                                                                .collection("Farmers").document(farmer)
                                                                .collection("Entry").document()
                                                                .set(entryHelper)
                                                                .addOnSuccessListener(aVoid -> {
                                                                    set_area.setText("");
                                                                    set_times.setText("");
                                                                    set_received.setText("");
                                                                    Toast.makeText(AddEntryActivity.this, "Added Successfully", Toast.LENGTH_SHORT).show();
                                                                }).addOnFailureListener(e -> {
                                                            Toast.makeText(AddEntryActivity.this, "Failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        });

                                                    }

                                                } else {
                                                    set_times.setError("How Many Times?\nOR\nDon't Put 0");
                                                }
                                            } else {
                                                set_area.setError("Enter Area");
                                            }
                                        }
                                    } else {
                                        Toast.makeText(AddEntryActivity.this, "Select Date", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    set_received.setError("Enter Received Amount Or Put 0");
                                }
                            } else {
                                set_rate.setError("Enter Rate");
                            }
                        } else {
                            Toast.makeText(AddEntryActivity.this, "Select Area Or Time", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AddEntryActivity.this, "Select Crop Or Other", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddEntryActivity.this, "Select Implement", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(AddEntryActivity.this, "Select Farmer", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(AddEntryActivity.this, "Select Village", Toast.LENGTH_SHORT).show();
        }
    }


}
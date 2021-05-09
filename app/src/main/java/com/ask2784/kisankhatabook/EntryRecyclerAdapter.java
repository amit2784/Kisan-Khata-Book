package com.ask2784.kisankhatabook;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class EntryRecyclerAdapter extends FirestoreRecyclerAdapter<EntryHelper, EntryRecyclerAdapter.EntryViewHolder> {


    EntryListener entryListener;
    public EntryRecyclerAdapter(@NonNull FirestoreRecyclerOptions<EntryHelper> options,EntryListener entryListener) {
        super(options);
        this.entryListener = entryListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull EntryViewHolder entryViewHolder, int position, @NonNull EntryHelper entryHelper) {
        entryViewHolder.date_r_v.setText(entryHelper.getDate());
        entryViewHolder.implement_r_v.setText(entryHelper.getImplement());
        entryViewHolder.croporother_r_v.setText(entryHelper.getCropOROther());
        entryViewHolder.areaortime_r_v.setText(entryHelper.getAreaORTime());

        entryViewHolder.times_r_v.setText(String.valueOf(entryHelper.getTimes()));
        entryViewHolder.rate_r_v.setText(String.valueOf(entryHelper.getRate()));
        entryViewHolder.total_r_v.setText(String.valueOf(entryHelper.getTotal()));
        entryViewHolder.received_r_v.setText(String.valueOf(entryHelper.getReceived()));
        entryViewHolder.remain_r_v.setText(String.valueOf(entryHelper.getRemain()));

        CharSequence addeddate = DateFormat.format("EEEE, MMM d, yyyy h:mm:ss a", entryHelper.getAddedTime().toDate());
        entryViewHolder.added_time_r_v.setText(addeddate);

    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.entry_row, parent, false);
        return new EntryViewHolder(view);
    }

    class EntryViewHolder extends RecyclerView.ViewHolder {

        TextView date_r_v, implement_r_v, croporother_r_v, areaortime_r_v, times_r_v, rate_r_v, total_r_v, received_r_v, remain_r_v, added_time_r_v;
        Button edit_received_r_v;

        public EntryViewHolder(@NonNull View itemView) {
            super(itemView);
            date_r_v = itemView.findViewById(R.id.date_r_v);
            implement_r_v = itemView.findViewById(R.id.implement_r_v);
            croporother_r_v = itemView.findViewById(R.id.croporother_r_v);
            areaortime_r_v = itemView.findViewById(R.id.areaortime_r_v);
            times_r_v = itemView.findViewById(R.id.times_r_v);
            rate_r_v = itemView.findViewById(R.id.rate_r_v);
            total_r_v = itemView.findViewById(R.id.total_r_v);
            received_r_v = itemView.findViewById(R.id.received_r_v);
            remain_r_v = itemView.findViewById(R.id.remain_r_v);
            added_time_r_v = itemView.findViewById(R.id.added_time_r_v);
            edit_received_r_v = itemView.findViewById(R.id.edit_received_r_v);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });
//            itemView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    System.out.println("Long Pressed");
//                    return true;
//                }
//            });

            edit_received_r_v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DocumentSnapshot snapshot = getSnapshots().getSnapshot(getAdapterPosition());
                    entryListener.handleEditReceived(snapshot);
                }
            });
        }
    }
    interface EntryListener{
        public void handleEditReceived(DocumentSnapshot snapshot);
    }

}

package com.light.patientcloud;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class PatientListAdapter extends RecyclerView.Adapter<PatientListAdapter.MyViewHolder> {
    private List<String[]> mDataset;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    private PatientListAdapter.OnItemClickListener onItemClickListener;
    private PatientListAdapter.OnItemLongClickListener onItemLongClickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CardView linePatient;
        public MyViewHolder(CardView v) {
            super(v);
            linePatient = v;
        }
    }
    // Provide a suitable constructor (depends on the kind of dataset)
    public PatientListAdapter(List<String[]> myDataset) {
        mDataset = myDataset;
    }

    public void UpdatePaitentList(List<String[]> myDataset){
        mDataset = myDataset;
    }
    // Create new views (invoked by the layout manager)
    @Override
    public PatientListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        CardView v = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.patient_list_lineview, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(PatientListAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnItemLongClickListener(PatientListAdapter.OnItemLongClickListener listener){
        this.onItemLongClickListener = listener;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        String[] someString = mDataset.get(position);
//        TextView idnum_view = holder.linePatient.findViewById(R.id.idnum_view);
//        idnum_view.setText(someString[0]);
        TextView name_view = holder.linePatient.findViewById(R.id.name_view);
        name_view.setText(someString[1]);
        TextView surgeryposview = holder.linePatient.findViewById(R.id.text_surgery_position);
        surgeryposview.setText(someString[2]);
        TextView birthday_view = holder.linePatient.findViewById(R.id.text_surgery_time);
        birthday_view.setText(someString[3]);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(onItemClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(holder.itemView, pos);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(onItemLongClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    onItemLongClickListener.onItemLongClick(holder.itemView, pos);
                }
                return true;
            }
        });

    }

    public String[] getPatientAt(int position){
        return mDataset.get(position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


}
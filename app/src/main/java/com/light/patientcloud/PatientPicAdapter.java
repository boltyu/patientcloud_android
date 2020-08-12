package com.light.patientcloud;

import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.util.List;

public class PatientPicAdapter extends RecyclerView.Adapter<PatientPicAdapter.MyViewHolder> {

    private List<String[]> sfileList;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    private PatientPicAdapter.OnItemClickListener onItemClickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CardView linePatient;
        public MyViewHolder(CardView v) {
            super(v);
            linePatient = v;
        }
    }
    // Provide a suitable constructor (depends on the kind of dataset)
    public PatientPicAdapter(List<String[]> filelist) {
        sfileList = filelist;
        sfileList.add(new String[]{"NEW","NEW"});
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PatientPicAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        // create a new view
        CardView v = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.patient_pic_view, parent, false);
        PatientPicAdapter.MyViewHolder vh = new PatientPicAdapter.MyViewHolder(v);
        return vh;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        //void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(PatientPicAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final PatientPicAdapter.MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //String fullfilepath = Environment.getExternalStorageState() + File.separator + sfileList.get(position);
        ImageView imageView = holder.linePatient.findViewById(R.id.img_patient_pic);
        String[] imgfile = sfileList.get(position);
        if (imgfile[0].equals("NEW") && imgfile[1].equals("NEW")){
            imageView.setImageResource(R.drawable.card_new_item);
        }else{
            imageView.setImageURI(Uri.fromFile(new File(sfileList.get(position)[0])));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(onItemClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(holder.itemView, pos);
                }
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return sfileList.size();
    }


}

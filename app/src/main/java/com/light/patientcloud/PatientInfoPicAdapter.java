package com.light.patientcloud;

import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.util.List;

public class PatientPicAdapter extends RecyclerView.Adapter<PatientPicAdapter.MyViewHolder> {

    private List<String[]> sfileList;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    private PatientPicAdapter.OnItemClickListener onItemClickListener;
    private PatientPicAdapter.OnItemLongClickListener onItemLongClickListener;



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
        UpdateList(filelist);
    }

    public String[] GetFileintheList(int position){
        return sfileList.get(position);
    }

    public void UpdateList(List<String[]> fileList){
        UpdatePicList(fileList);
    }
    public void UpdatePicList(List<String[]> fileList) {
        sfileList = fileList;
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

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(PatientPicAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnItemLongClickListener(PatientPicAdapter.OnItemLongClickListener listener){
        this.onItemLongClickListener = listener;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final PatientPicAdapter.MyViewHolder holder, int position) {
        //String fullfilepath = Environment.getExternalStorageState() + File.separator + sfileList.get(position);
        ImageView imageView = holder.linePatient.findViewById(R.id.img_patient_pic);
        TextView remarktext = holder.linePatient.findViewById(R.id.remark_patient_pic);
        remarktext.setActivated(false);
        String[] imgfile = sfileList.get(position);
        if (imgfile[0].equals("NEW") && imgfile[1].equals("NEW")){
            imageView.setImageResource(R.drawable.card_new_item);
            remarktext.setText("添加新图片");
        }else{
            imageView.setImageURI(Uri.fromFile(new File(imgfile[0])));
            remarktext.setText(imgfile[1]);
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

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return sfileList.size();
    }


}

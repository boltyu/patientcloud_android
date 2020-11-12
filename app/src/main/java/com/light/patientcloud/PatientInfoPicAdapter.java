package com.light.patientcloud;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public class PatientInfoPicAdapter extends RecyclerView.Adapter<PatientInfoPicAdapter.MyViewHolder> {

    private List<String[]> sfileList;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    private PatientInfoPicAdapter.OnItemClickListener onItemClickListener;
    private PatientInfoPicAdapter.OnItemLongClickListener onItemLongClickListener;



    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CardView linePatient;
        public MyViewHolder(CardView v) {
            super(v);
            linePatient = v;
        }
    }
    // Provide a suitable constructor (depends on the kind of dataset)
    public PatientInfoPicAdapter(List<String[]> filelist) {
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
    public PatientInfoPicAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
        // create a new view
        CardView v = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.patient_info_pic_view, parent, false);
        PatientInfoPicAdapter.MyViewHolder vh = new PatientInfoPicAdapter.MyViewHolder(v);
        return vh;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        //void onItemLongClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(PatientInfoPicAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnItemLongClickListener(PatientInfoPicAdapter.OnItemLongClickListener listener){
        this.onItemLongClickListener = listener;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final PatientInfoPicAdapter.MyViewHolder holder, int position) {
        //String fullfilepath = Environment.getExternalStorageState() + File.separator + sfileList.get(position);
        ImageView imageView = holder.linePatient.findViewById(R.id.img_patient_pic);
        TextView remarktext = holder.linePatient.findViewById(R.id.remark_patient_pic);
        remarktext.setActivated(false);
        String[] imgfile = sfileList.get(position);
        if (imgfile[0].equals("NEW") && imgfile[1].equals("NEW")){
            imageView.setImageResource(R.drawable.card_new_item);
            remarktext.setText("添加新图片");
        }else{
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inSampleSize = 1;
            Bitmap bbb = BitmapFactory.decodeFile(new File(imgfile[0]).getAbsolutePath(),options);
            imageView.setImageBitmap(bbb);
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

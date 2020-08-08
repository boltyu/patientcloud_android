package com.light.patientcloud;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.OnItemActivatedListener;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StableIdKeyProvider;
import androidx.recyclerview.selection.StorageStrategy;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PatientList extends Activity {

    private RecyclerView recyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Button btnNewPatient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);
        recyclerView = (RecyclerView) findViewById(R.id.patient_list_view);
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator( new DefaultItemAnimator());
        btnNewPatient = findViewById(R.id.btn_new_patient);
        final Intent patientInfo = new Intent(this,PatientInfo.class);
        btnNewPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                patientInfo.putExtra("idnum","None");
                startActivity(patientInfo);
                finish();
            }
        });

        // specify an adapter (see also next example)
        final List<String[]> myDataset = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject patients  = MainActivity.globalConnection.getPatientList();
                Iterator<String> it_patients = patients.keys();
                int i = 0;
                while(it_patients.hasNext()){
                    String idnum = it_patients.next();
                    JSONObject patient = patients.optJSONObject(idnum);
                    myDataset.add(i,new String[]{ idnum,
                            patient.optString("name"),
                            patient.optString("gender"),
                            patient.optString("birthday")});
                    i++;
                }
                mAdapter = new MyAdapter(myDataset);

                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setAdapter(mAdapter);
                    }
                });

                mAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        LinearLayout patientView = (LinearLayout) recyclerView.getChildAt(position);
                        TextView idnumView = (TextView)patientView.findViewById(R.id.idnum_view);
                        patientInfo.putExtra("idnum",idnumView.getText());
                        startActivity(patientInfo);
                        finish();
                    }
                });
            }
        }).start();



    }




    public static class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private List<String[]> mDataset;
        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        private MyAdapter.OnItemClickListener onItemClickListener;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public LinearLayout linePatient;
            public MyViewHolder(LinearLayout v) {
                super(v);
                linePatient = v;
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(List<String[]> myDataset) {
            mDataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
            // create a new view
            LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.idnum_line_view, parent, false);
            MyViewHolder vh = new MyViewHolder(v);
            return vh;
        }

        public interface OnItemClickListener {
            void onItemClick(View view, int position);
            //void onItemLongClick(View view, int position);
        }

        public void setOnItemClickListener(MyAdapter.OnItemClickListener listener) {
            this.onItemClickListener = listener;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            String[] someString = mDataset.get(position);
            TextView idnum_view = holder.linePatient.findViewById(R.id.idnum_view);
            idnum_view.setText(someString[0]);
            TextView name_view = holder.linePatient.findViewById(R.id.name_view);
            name_view.setText(someString[1]);
            TextView gender_view = holder.linePatient.findViewById(R.id.gender_view);
            gender_view.setText(someString[2]);
            TextView age_view = holder.linePatient.findViewById(R.id.age_view);
            age_view.setText(someString[3]+"岁");
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
            return mDataset.size();
        }


    }

}

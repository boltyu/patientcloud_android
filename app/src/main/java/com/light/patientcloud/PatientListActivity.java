package com.light.patientcloud;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PatientListActivity extends Activity {

    private RecyclerView recyclerView;
    private PatientListAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Button btnNewPatient;
    private TextView textWelcome, textLogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_patient_list);
        // use a linear layout manage
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator( new DefaultItemAnimator());
        btnNewPatient = findViewById(R.id.btn_new_patient);
        final Intent patientInfo = new Intent(this, PatientInfoActivity.class);
        btnNewPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                patientInfo.putExtra("idnum","None");
                startActivity(patientInfo);
                finish();
            }
        });
        textWelcome = findViewById(R.id.text_doctor_welcome);
        textWelcome.setText(textWelcome.getText());

        textLogout = findViewById(R.id.text_doctor_logout);
        textLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if( MainActivity.globalConnection.loginUser("n","n",1) == true)
                            finish();
                    }
                }).start();
            }
        });


        // specify an adapter (see also next example)
        final List<String[]> myDataset = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject patients = MainActivity.globalConnection.getPatientList();
                Iterator<String> it_patients = patients.keys();
                int i = 0;
                while(it_patients.hasNext()){
                    String idnum = it_patients.next();
                    JSONObject patient = patients.optJSONObject(idnum);
                    myDataset.add(i,new String[]{ idnum,
                            patient.optString("name"),
                            patient.optString("phone"),
                            patient.optString("birthday")});
                    i++;
                }
                mAdapter = new PatientListAdapter(myDataset);

                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setAdapter(mAdapter);
                    }
                });

                mAdapter.setOnItemClickListener(new PatientListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        CardView patientView = (CardView) recyclerView.getChildAt(position);
                        TextView idnumView = (TextView)patientView.findViewById(R.id.idnum_view);
                        patientInfo.putExtra("idnum",idnumView.getText());
                        startActivity(patientInfo);
                    }
                });
            }
        }).start();


    }






}

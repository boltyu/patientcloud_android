package com.light.patientcloud;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

    public RecyclerView recyclerView;
    public PatientListAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Button btnNewPatient;
    private TextView textWelcome, textLogout;
    Intent patientInfo = null;

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
        patientInfo = new Intent(this, PatientInfoActivity.class);
        btnNewPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                patientInfo.putExtra("idnum","None");
                startActivityForResult(patientInfo,1);
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
                    if(MainActivity.globalConnection.loginUser("n", "n", 1)){
                        finish();
                    }
                }
            }).start();
            }
        });
        UpdatePatientList(true);
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UpdatePatientList(false);
    }

    private void UpdatePatientList(final boolean firsttime){
        final Context pcontext = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<String[]> myDataset = MainActivity.globalConnection.getPatientList();
                if(firsttime){
                    mAdapter = new PatientListAdapter(myDataset);
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.setAdapter(mAdapter);
                        }
                    });


                    mAdapter.setOnItemLongClickListener(new PatientListAdapter.OnItemLongClickListener() {
                        @Override
                        public void onItemLongClick(View view, int position) {
                            final String idnumtext= mAdapter.getPatientAt(position)[0];
                            final TextView nameview = view.findViewById(R.id.name_view);
                            new AlertDialog.Builder(pcontext)
                                    .setTitle("确认删除")
                                    .setMessage(nameview.getText())
                                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if(MainActivity.globalConnection.deletePaitent(idnumtext)){
                                                        final List<String[]> newDataset = MainActivity.globalConnection.getPatientList();
                                                        recyclerView.post(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                mAdapter.UpdatePaitentList(newDataset);
                                                                mAdapter.notifyDataSetChanged();
                                                            }
                                                        });
                                                    }
                                                }
                                            }).start();

                                        }
                                    })
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    })
                                    .show();
                        }
                    });

                    mAdapter.setOnItemClickListener(new PatientListAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            CardView patientView = (CardView) recyclerView.getChildAt(position);
                            String idnumtext = mAdapter.getPatientAt(position)[0];
                            patientInfo.putExtra("idnum",idnumtext);
                            startActivityForResult(patientInfo,1);
                        }
                    });

                }else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final List<String[]> newDataset = MainActivity.globalConnection.getPatientList();
                            recyclerView.post(new Runnable() {
                                @Override
                                public void run() {
                                    mAdapter.UpdatePaitentList(newDataset);
                                    mAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }).start();

                }
            }
        }).start();
    }






}

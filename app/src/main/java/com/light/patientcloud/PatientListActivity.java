package com.light.patientcloud;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.List;

public class PatientListActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    public PatientListAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    Intent patientInfo = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_patient_list);
        // use a linear layout manage
        layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        //recyclerView.setItemAnimator( new DefaultItemAnimator());
        patientInfo = new Intent(this, PatientInfoActivity.class);

        InitPaitentList();
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode)
        {
            case 1:
                UpdatePatientList();
                break;
        }
    }

    private void InitPaitentList(){
        final Context pcontext = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<String[]> myDataset = MainActivity.globalConnection.getPatientList();
                mAdapter = new PatientListAdapter(myDataset);
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
                                                            setTitle("患者列表   "+ myDataset.size()+"项");
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
                        String idnumtext = mAdapter.getPatientAt(position)[0];
                        patientInfo.putExtra("idnum",idnumtext);
                        startActivityForResult(patientInfo,1);
                    }
                });

                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        setTitle("患者列表   "+ myDataset.size()+"项");
                        mAdapter.setHasStableIds(true);
                        recyclerView.setAdapter(mAdapter);

                    }
                });
            }
        }).start();

    }

    private void UpdatePatientList(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<String[]> myDataset = MainActivity.globalConnection.getPatientList();
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.UpdatePaitentList(myDataset);
                        mAdapter.notifyDataSetChanged();
                        setTitle("患者列表   " + myDataset.size() + "项");
                    }
                });
            }
        }).start();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.patientlist_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add:
                if(patientInfo == null)
                    return false;
                patientInfo.putExtra("idnum","None");
                startActivityForResult(patientInfo,1);
                break;
            case R.id.action_logout:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(MainActivity.globalConnection.loginUser("n", "n", 1)){
                            finish();
                        }
                    }
                }).start();
                break;

        }
        return true;
    }


}

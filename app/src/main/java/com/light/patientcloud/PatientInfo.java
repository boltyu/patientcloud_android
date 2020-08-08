package com.light.patientcloud;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URISyntaxException;

public class PatientInfo extends AppCompatActivity {

    private TimePicker editPatient_surgerytime;
    private DatePicker editPatient_surgerydate;
    private EditText editPatient_name, editPatient_gender, editPatient_age,
            editPatient_ddescription,
            editPatient_devicetype,
            editPatient_surgerytype,
            editPatient_surgerypos;
    private Button btnPost;
    private String currentIdnum;

    private Button btnImg;
    private TextView textImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info);
        btnImg = findViewById(R.id.uploadimg);
        textImg = findViewById(R.id.uploadtext);


        btnImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                // Only the system receives the ACTION_OPEN_DOCUMENT, so no need to test.
                startActivityForResult(intent, 2);


            }
        });


        btnPost = findViewById(R.id.btn_post_patient);
        final Intent patientIndex = new Intent(this,PatientList.class);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String postdata = "name=" + editPatient_name.getText() +
                                    "&gender=" + editPatient_gender.getText() +
                                    "&birthday=" + editPatient_age.getText() +
                                    "&remark=" + editPatient_ddescription.getText() +
                                    "&devicetype=" + editPatient_devicetype.getText() +
                                    "&surgerytype=" + editPatient_surgerytype.getText() +
                                    //"&surgerytime=" + editPatient_surgerytime.getText() +
                                    "&surgerycenter=" + editPatient_surgerypos.getText()+
                                    "&surgerytime=";

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            String datetime = String.valueOf(editPatient_surgerydate.getYear());
                            int mm = editPatient_surgerydate.getMonth()+1;
                            int dd = editPatient_surgerydate.getDayOfMonth();
                            int HH = editPatient_surgerytime.getHour();
                            int MM = editPatient_surgerytime.getMinute();
                            if(mm < 10) datetime += "0";
                            datetime += mm;
                            if(dd < 10) datetime += "0";
                            datetime += dd + " ";
                            if(HH < 10) datetime += "0";
                            datetime += HH + ":";
                            if(MM < 10) datetime += "0";
                            datetime += MM;
                            postdata+=datetime;
                        }
                        MainActivity.globalConnection.postPatientInfo(currentIdnum,postdata);
                        finish();
                        startActivity(patientIndex);
                    }
                }).start();
            }
        });

        editPatient_gender = findViewById(R.id.edit_patient_gender);
        editPatient_age = findViewById(R.id.edit_patient_age);
        editPatient_name = findViewById(R.id.edit_patient_name);
        editPatient_ddescription = findViewById(R.id.edit_patient_remark);
        editPatient_devicetype = findViewById(R.id.edit_patient_device_type);
        editPatient_surgerytype= findViewById(R.id.edit_patient_surgery_type);
        editPatient_surgerytime = findViewById(R.id.edit_patient_surgery_time);
        editPatient_surgerydate = findViewById(R.id.edit_patient_surgery_date);
        editPatient_surgerypos = findViewById(R.id.edit_patient_surgery_center);

        currentIdnum = getIntent().getStringExtra("idnum");
        final String idnum = currentIdnum;
        //Toast.makeText(PatientInfo.this,idnum, Toast.LENGTH_LONG).show();
        if(idnum==null || idnum.equals("None") || idnum.equals("")){   // new

        }else{                                          // http get
            new Thread(new Runnable() {
                @Override
                public void run() {
                    JSONObject patientObj = MainActivity.globalConnection.getPatientInfo(idnum);
                    editPatient_name.setText(patientObj.optString("name"));
                    editPatient_age.setText(patientObj.optString("birthday"));
                    editPatient_gender.setText(patientObj.optString("gender"));
                    editPatient_ddescription.setText(patientObj.optString("remark"));
                    editPatient_devicetype.setText(patientObj.optString("devicetype"));
                    editPatient_surgerytype.setText(patientObj.optString("surgerytype"));
                    //editPatient_surgerytime.setText(patientObj.optString("surgerytime"));
                    String datetime = patientObj.optString("surgerytime");
                    editPatient_surgerydate.updateDate(Integer.valueOf(datetime.substring(0,4)),
                            Integer.valueOf(datetime.substring(4,6)),
                            Integer.valueOf(datetime.substring(6,8)));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        editPatient_surgerytime.setHour(Integer.valueOf(datetime.substring(9,11)));
                        editPatient_surgerytime.setMinute(Integer.valueOf(datetime.substring(12,14)));
                    }
                    editPatient_surgerypos.setText(patientObj.optString("surgerycenter"));


                }
            }).start();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2) {
            Uri fullPhotoUri = data.getData();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MainActivity.globalConnection.uploadImg(currentIdnum, "pic", "123");
                }
            }).start();

        }
    }

}

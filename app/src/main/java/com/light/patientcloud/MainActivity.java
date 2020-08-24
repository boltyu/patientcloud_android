package com.light.patientcloud;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileInputStream;

public class MainActivity extends AppCompatActivity {

    private Button btnLogin;
    private EditText editUsername;
    private EditText editPassword;
    public static UrlConnection globalConnection;
    public static FileManager fileManager;
    private Context pcontext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 2);
        fileManager = new FileManager("patientcloud");

        setContentView(R.layout.activity_main);
        btnLogin = findViewById(R.id.btn_login);
        editPassword = findViewById(R.id.edit_password);
        editUsername = findViewById(R.id.edit_username);
        editUsername.setText("test");
        editPassword.setText("qwe123");
        globalConnection = new UrlConnection();
        String hostaddress = fileManager.getSettings("hostaddress");
        if( !hostaddress.equals("") ){
            globalConnection.initUrl(hostaddress);
        }



        pcontext=this;
        final Intent patientIndex = new Intent(this, PatientListActivity.class);
        //final Intent patientInfo = new Intent(this,PatientInfoActivity.class);
        //startActivity(patientInfo);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        String username = editUsername.getText().toString();
                        String password = editPassword.getText().toString();
                        final Boolean r = globalConnection.loginUser(username,password,0);
                        editPassword.post(new Runnable() {
                            @Override
                            public void run() {
                                if(r){
                                    editPassword.setBackgroundResource(R.drawable.edit_normal);
                                    startActivity(patientIndex);
                                }
                                else
                                {
                                    editPassword.setBackgroundResource(R.drawable.edit_warning);
                                }
                            }
                        });
                    }
                }).start();
            }
        });

        editUsername.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                LinearLayout tmpview = (LinearLayout) LayoutInflater.from(pcontext).inflate(R.layout.patient_remark_view,null,false);
                final EditText sourceview = tmpview.findViewById(R.id.edit_remark_patient_pic);
                sourceview.setText(globalConnection.getHostaddress());
                new AlertDialog.Builder(pcontext)
                        .setTitle("编辑服务器地址")
                        .setView(tmpview)
                        .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String hostaddress = sourceview.getText().toString();
                                fileManager.saveSettings("hostaddress",hostaddress);
                                globalConnection.initUrl(hostaddress);
                            }
                        })
                        .show();
                return true;
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        int resultCode = 0;
        switch (requestCode) {
            case 2: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);
                } else {
                    resultCode = requestCode;
                }
                break;
            }
            case 3: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    
                } else {
                    resultCode = requestCode;
                }
                break;
            }
        }
        if (resultCode > 0) {
            AlertDialog.Builder msgbuilder = new AlertDialog.Builder(this);
            msgbuilder.setTitle("请求关键权限\r\n\n");
            String msgtext = "相机使用权限:\r\n\n调用相机拍摄照片"+
                    "内存读写权限:\r\n\n保存录像及语音数据"+
                    "位置信息权限:\r\n\n使用WIFI需要获取位置权限\r\n\nhttps://developer.android.com/guide/topics/connectivity/wifi-scan";
            msgbuilder.setMessage(msgtext);
            msgbuilder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    System.exit(0);// User clicked OK button
                }
            });
            AlertDialog msgbox = msgbuilder.create();
            msgbox.show();
        }
    }



}

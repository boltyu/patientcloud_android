package com.light.patientcloud;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private Button btnLogin;
    private EditText editUsername;
    private EditText editPassword;
    public static UrlConnection globalConnection;
    public static PatientFileManager fileManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fileManager = new PatientFileManager("patientcloud");


        setContentView(R.layout.activity_main);
        btnLogin = findViewById(R.id.btn_login);
        editPassword = findViewById(R.id.edit_password);
        editUsername = findViewById(R.id.edit_username);
        editUsername.setText("test");
        editPassword.setText("qwe123");
        globalConnection = new UrlConnection();
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
                                    finish();
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
    }

}

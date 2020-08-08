package com.light.patientcloud;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.light.patientcloud.UrlConnection;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private Button btnLogin;
    private EditText editUsername;
    private EditText editPassword;
    public static UrlConnection globalConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnLogin = findViewById(R.id.btn_login);
        editPassword = findViewById(R.id.edit_password);
        editUsername = findViewById(R.id.edit_username);
        editUsername.setText("test");
        editPassword.setText("qwe123");
        globalConnection = new UrlConnection();
        final Intent patientIndex = new Intent(this,PatientList.class);
        //final Intent patientInfo = new Intent(this,PatientInfo.class);
        //startActivity(patientInfo);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        String username = editUsername.getText().toString();
                        String password = editPassword.getText().toString();
                        final Boolean r = globalConnection.loginUser(username,password);
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
    }

}

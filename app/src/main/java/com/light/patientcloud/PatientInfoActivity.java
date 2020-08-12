package com.light.patientcloud;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

public class PatientInfoActivity extends AppCompatActivity {

    private Button btnPost;
    private String currentIdnum;
    private ViewPager infoPages;
    private Button btnImg;
    private TextView textImg;
    private PatientInfoAdapter pagesAdapter;
    private TabLayout pagesTabs;
    final int rCode_takephoto = 2;
    private Intent takePictureIntent = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info);
        @SuppressLint("HandlerLeak")
        Handler picHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 3:
                        chooseImage("pic","tmp.jpg",3);
                        break;
                }
            }
        };

        pagesTabs = findViewById(R.id.tab_patient_info);
        infoPages = findViewById(R.id.page_patient_info);
        pagesAdapter = new PatientInfoAdapter(this,picHandler);
        infoPages.setAdapter(pagesAdapter);
        pagesTabs.setupWithViewPager(infoPages);

        btnPost = findViewById(R.id.btn_post_patient);
        currentIdnum = getIntent().getStringExtra("idnum");



        MainActivity.fileManager.checkChildDir(currentIdnum);
        takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pagesAdapter.postTextInfo(currentIdnum);
                finish();
//                switch (pagesTabs.getSelectedTabPosition()){
//                    case 0:
//                    case 1:
//                        pagesAdapter.postTextInfo(currentIdnum);
//                        finish();
//                        break;
//                    case 2:
//                        chooseImage("pic","tmp.jpg",3);
//                }
                //startActivity();
            }
        });

        pagesAdapter.getTextInfo(currentIdnum);




        pagesAdapter.viewAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage("avatar","tmp.jpg",rCode_takephoto);
            }
        });

    }

    public void chooseImage(String category, String filename, int code){ // filename collision
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            Uri photoURI = FileProvider.getUriForFile(this,
                    "com.example.android.fileprovider",
                    MainActivity.fileManager.getFile(currentIdnum,category,filename));
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

            startActivityForResult(takePictureIntent, code);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode){
                case rCode_takephoto:
                        pagesAdapter.viewAvatar.setImageURI(Uri.fromFile(MainActivity.fileManager.getFile(currentIdnum,"avatar","tmp.jpg")));
                    break;
                case 3:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.globalConnection.uploadImg(currentIdnum,"pic","tmp.jpg");
                            finish();
                        }
                    }).start();
                    break;
            }

        }

    }

}


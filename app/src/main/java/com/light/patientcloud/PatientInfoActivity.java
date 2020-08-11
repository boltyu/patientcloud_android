package com.light.patientcloud;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info);
        pagesTabs = findViewById(R.id.tab_patient_info);
        infoPages = findViewById(R.id.page_patient_info);
        pagesAdapter = new PatientInfoAdapter(this);
        infoPages.setAdapter(pagesAdapter);
        pagesTabs.setupWithViewPager(infoPages);

        btnPost = findViewById(R.id.btn_post_patient);
        currentIdnum = getIntent().getStringExtra("idnum");

        MainActivity.fileManager.checkChildDir(currentIdnum);

        final Intent patientIndex = new Intent(this, PatientListActivity.class);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pagesAdapter.postTextInfo(currentIdnum);
                MainActivity.globalConnection.uploadImg(currentIdnum,"avatar","123");
                finish();
                //startActivity();
            }
        });

        pagesAdapter.getTextInfo(currentIdnum);

        pagesAdapter.viewAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, rCode_takephoto);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == rCode_takephoto && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            pagesAdapter.viewAvatar.setImageBitmap(imageBitmap);

        }

    }

}


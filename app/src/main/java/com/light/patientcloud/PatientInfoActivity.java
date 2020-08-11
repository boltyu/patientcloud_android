package com.light.patientcloud;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PatientInfoActivity extends AppCompatActivity {


    private Button btnPost;
    private String currentIdnum;
    private ViewPager infoPages;
    private Button btnImg;
    private TextView textImg;
    private PatientInfoAdapter pagesAdapter;
    private ViewPager.OnPageChangeListener pagesChanged;
    private TabLayout pagesTabs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info);
        pagesTabs = findViewById(R.id.tab_patient_info);
        infoPages = findViewById(R.id.page_patient_info);
        pagesAdapter = new PatientInfoAdapter(this);
        infoPages.setAdapter(pagesAdapter);
        pagesTabs.setupWithViewPager(infoPages);
        infoPages.addOnPageChangeListener(pagesChanged);

        btnPost = findViewById(R.id.btn_post_patient);
        currentIdnum = getIntent().getStringExtra("idnum");


        final Intent patientIndex = new Intent(this, PatientListActivity.class);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pagesAdapter.postTextInfo(currentIdnum);
                MainActivity.globalConnection.uploadImg(currentIdnum,"avatar","123");
                finish();
                Bitmap bitmap = ((BitmapDrawable)pagesAdapter.viewAvatar.getDrawable()).getBitmap();
                //startActivity();
            }
        });

        pagesAdapter.getTextInfo(currentIdnum);

        pagesAdapter.viewAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, 2);
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            pagesAdapter.viewAvatar.setImageBitmap(imageBitmap);
        }

    }

}


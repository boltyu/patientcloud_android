package com.light.patientcloud;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    private final String[] categoryString = {"undefine","avatar","pic","eval","epos"};
    private Context global_context;
    Handler addPicHandler, statusHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info);
        global_context = this;
        addPicHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what){
                    case 2:
                        chooseImage("pic","tmp.jpg",2);
                        break;
                    case 3:
                        chooseImage("eval","tmp.jpg",3);
                        break;
                    case 4:
                        chooseImage("epos","tmp.jpg",4);
                        break;
                }
                return true;
            }
        });

        statusHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what){
                    case 1:
                        Toast.makeText(global_context,"正在获取患者信息",Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        break;
                    case 3:
                        Toast.makeText(global_context,"图片上传中，请不要返回",Toast.LENGTH_LONG).show();
                        break;
                    case 4:
                        Toast.makeText(global_context,"图片上传成功",Toast.LENGTH_SHORT).show();
                        break;

                }
                return true;
            }
        });




        currentIdnum = getIntent().getStringExtra("idnum");
        pagesAdapter = new PatientInfoAdapter(this,currentIdnum,addPicHandler,statusHandler);
        pagesTabs = findViewById(R.id.tab_patient_info);
        infoPages = findViewById(R.id.page_patient_info);
        infoPages.setAdapter(pagesAdapter);
        pagesTabs.setupWithViewPager(infoPages);

        btnPost = findViewById(R.id.btn_post_patient);

        takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pagesAdapter.postTextInfo(currentIdnum)) {
                    setResult(1);
                    finish();
                }
            }
        });
        setTitle(currentIdnum.equals("None")?"新建患者":currentIdnum);
        if(currentIdnum==null || currentIdnum.equals("None") || currentIdnum.equals(""))
        {

        }else
            pagesAdapter.fillBaseInfo(currentIdnum);
        pagesAdapter.viewAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage("avatar","tmp.jpg",1);
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
    protected void onActivityResult(final int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        RecyclerView patientPicView = null;
        if (resultCode == RESULT_OK) {
            switch (requestCode){
                case 1:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String filename = MainActivity.globalConnection.uploadImg(currentIdnum,"avatar","tmp.jpg");
                            if(!filename.equals("BAD"))
                                pagesAdapter.viewAvatar.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        pagesAdapter.viewAvatar.setImageURI(Uri.fromFile(MainActivity.fileManager.getFile(currentIdnum, "avatar", filename)));

                                    }
                                });
                        }
                    }).start();
                    break;
                case 2:
                    patientPicView = pagesAdapter.viewSurgerypic;
                    break;
                case 3:
                    patientPicView = pagesAdapter.viewEvalpic;
                    break;
                case 4:
                    patientPicView = pagesAdapter.viewEpospic;
                    break;
            }
            if(requestCode > 1 && patientPicView != null)
            {
                final PatientInfoPicAdapter patientInfoPicAdapter = (PatientInfoPicAdapter) patientPicView.getAdapter();
                final RecyclerView tmpView = patientPicView;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message startmessage = Message.obtain(statusHandler,3);
                        startmessage.sendToTarget();
                        String filename = MainActivity.globalConnection.uploadImg(currentIdnum,categoryString[requestCode],"tmp.jpg");
                        if(!filename.equals("BAD")){
                            patientInfoPicAdapter.UpdateList(MainActivity.globalConnection.getPicList(currentIdnum,categoryString[requestCode]));
                            tmpView.post(new Runnable() {
                                @Override
                                public void run() {
                                    patientInfoPicAdapter.notifyDataSetChanged();
                                }
                            });
                            Message endmessage = Message.obtain(statusHandler,4);
                            endmessage.sendToTarget();

                        }

                    }
                }).start();
            }


        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.patientinfo_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_backtolist:
                setResult(0);
                finish();
                break;
        }
        return true;
    }
}


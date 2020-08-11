package com.light.patientcloud;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.Inflater;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

public class PatientInfoAdapter extends PagerAdapter {

    private PatientPicAdapter picAdapter;
    private LinearLayout viewBase, viewSurgery;
    private RecyclerView viewSurgerypic, viewEvalpic, viewEpospic;
    private String[] pageTitle = {"基本信息", "手术信息", "手术照片", "评估照片", "电极照片"};
    private List<View> mViewList = new ArrayList<>();
    private TimePicker editPatient_surgerytime;
    private DatePicker editPatient_surgerydate;
    private EditText editPatient_name, editPatient_birthday,
            editPatient_phone,
            editPatient_remark,
            editPatient_devicetype,
            editPatient_surgerytype,
            editPatient_surgerypos;
    private Spinner spinnerGender;

    public ImageView viewAvatar;

    private Context pcontext = null;
    
    public PatientInfoAdapter(final Context context) {
        viewBase = (LinearLayout) View.inflate(context,R.layout.patient_info_baseinfo,null);
        viewSurgery = (LinearLayout) View.inflate(context,R.layout.patient_info_surgery,null);
        viewSurgerypic = (RecyclerView) View.inflate(context,R.layout.patient_info_surgerypic,null);
        viewEvalpic = (RecyclerView) View.inflate(context,R.layout.patient_info_evalpic,null);
        viewEpospic = (RecyclerView) View.inflate(context,R.layout.patient_info_epospic,null);

        editPatient_birthday = viewBase.findViewById(R.id.edit_patient_birthday);
        editPatient_name = viewBase.findViewById(R.id.edit_patient_name);
        editPatient_phone = viewBase.findViewById(R.id.edit_patient_phone);
        editPatient_remark = viewBase.findViewById(R.id.edit_patient_phone);
        editPatient_devicetype = viewSurgery.findViewById(R.id.edit_patient_device_type);
        editPatient_surgerytype= viewSurgery.findViewById(R.id.edit_patient_surgery_type);
        editPatient_surgerytime = viewSurgery.findViewById(R.id.edit_patient_surgery_time);
        editPatient_surgerydate = viewSurgery.findViewById(R.id.edit_patient_surgery_date);
        editPatient_surgerypos = viewSurgery.findViewById(R.id.edit_patient_surgery_center);

        List<String> genderlist = new ArrayList<String>();
        genderlist.add(0,"女");
        genderlist.add(1,"男");
        spinnerGender = viewBase.findViewById(R.id.select_patient_gender);
        spinnerGender.setAdapter(new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,genderlist));


        mViewList.add(0,viewBase);
        mViewList.add(1,viewSurgery);
        mViewList.add(2,viewSurgerypic);
        mViewList.add(3,viewEvalpic);
        mViewList.add(4,viewEpospic);

        viewAvatar = viewBase.findViewById(R.id.img_patient_avatar);
        int  aaa  = viewBase.getWidth();

        pcontext = context;
    }

    @Override
    public int getCount() {
        return mViewList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mViewList.get(position));
        return mViewList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViewList.get(position));
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return pageTitle[position];
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view==o;
    }


    public List<String[]> getImageList(final String idnum){
        List<String[]> filelist = MainActivity.globalConnection.getPicList(idnum,"pic");
        return filelist;
    }

    public boolean postTextInfo(final String idnum){
        new Thread(new Runnable() {
            @Override
            public void run() {

                String postdata = "name=" + editPatient_name.getText() +
                        "&gender=" + spinnerGender.getSelectedItemPosition() +
                        "&birthday=" + editPatient_birthday.getText() +
                        "&remark=" + editPatient_phone.getText() +
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
                MainActivity.globalConnection.postPatientInfo(idnum,postdata);
            }
        }).start();
        return false;
    }

    public boolean getTextInfo(final String idnum){
        //Toast.makeText(PatientInfoActivity.this,idnum, Toast.LENGTH_LONG).show();
        if(idnum==null || idnum.equals("None") || idnum.equals("")){   // new

        }else{                                          // http get
            new Thread(new Runnable() {
                @Override
                public void run() {


                    final List<String[]> filelist = MainActivity.globalConnection.getPicList(idnum,"avatar");
                    final JSONObject patientObj = MainActivity.globalConnection.getPatientInfo(idnum);
                    picAdapter = new PatientPicAdapter(getImageList(idnum));
                    final String datetime = patientObj.optString("surgerytime");
                    viewAvatar.post(new Runnable() {
                        @Override
                        public void run() {
                            if(filelist.size() > 0)
                                viewAvatar.setImageURI(Uri.fromFile(new File(filelist.get(0)[0])));
                            editPatient_name.setText(patientObj.optString("name"));
                            editPatient_birthday.setText(patientObj.optString("birthday"));
                            spinnerGender.setSelection(patientObj.optInt("gender"));
                            editPatient_remark.setText(patientObj.optString("remark"));
                            editPatient_devicetype.setText(patientObj.optString("devicetype"));
                            editPatient_surgerytype.setText(patientObj.optString("surgerytype"));
                            editPatient_surgerydate.updateDate(Integer.valueOf(datetime.substring(0,4)),
                                    Integer.valueOf(datetime.substring(4,6)),
                                    Integer.valueOf(datetime.substring(6,8)));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                editPatient_surgerytime.setHour(Integer.valueOf(datetime.substring(9,11)));
                                editPatient_surgerytime.setMinute(Integer.valueOf(datetime.substring(12,14)));
                            }
                            editPatient_surgerypos.setText(patientObj.optString("surgerycenter"));


                            LinearLayoutManager layoutManager = new LinearLayoutManager(pcontext);
                            viewSurgerypic.setLayoutManager(layoutManager);
                            viewSurgerypic.setItemAnimator( new DefaultItemAnimator());
                            viewSurgerypic.setAdapter(picAdapter);
                        }
                    });

                }
            }).start();
        }
        return false;
    }
}


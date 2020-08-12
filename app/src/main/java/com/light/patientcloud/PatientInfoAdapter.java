package com.light.patientcloud;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
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
import java.util.Calendar;
import java.util.List;

public class PatientInfoAdapter extends PagerAdapter {

    public PatientPicAdapter picAdapter, evalpicAdapter, epospicAdapter;
    private LinearLayout viewBase, viewSurgery;
    public RecyclerView viewSurgerypic, viewEvalpic, viewEpospic;
    private String[] pageTitle = {"基本信息", "手术信息", "手术照片", "评估照片", "电极照片"};
    private List<View> mViewList = new ArrayList<>();
    private EditText editPatient_name,
            editPatient_phone,
            editPatient_remark,
            editPatient_devicetype,
            editPatient_surgerytype,
            editPatient_surgerypos;
    private TextView editPatient_birthday,editPatient_surgerytime;
    private Spinner spinnerGender;

    public ImageView viewAvatar;
    private String currentidnum;
    private Context pcontext = null;
    private Handler mHandler;
    public PatientInfoAdapter(final Context context, String idnum, Handler picHandler) {
        mHandler = picHandler;
        viewBase = (LinearLayout) View.inflate(context,R.layout.patient_info_baseinfo,null);
        viewSurgery = (LinearLayout) View.inflate(context,R.layout.patient_info_surgery,null);
        viewSurgerypic = (RecyclerView) View.inflate(context,R.layout.patient_info_surgerypic,null);
        viewEvalpic = (RecyclerView) View.inflate(context,R.layout.patient_info_evalpic,null);
        viewEpospic = (RecyclerView) View.inflate(context,R.layout.patient_info_epospic,null);
        currentidnum = idnum;
        editPatient_birthday = viewBase.findViewById(R.id.edit_patient_birthday);
        editPatient_name = viewBase.findViewById(R.id.edit_patient_name);
        editPatient_phone = viewBase.findViewById(R.id.edit_patient_phone);
        editPatient_remark = viewBase.findViewById(R.id.edit_patient_remark);
        editPatient_devicetype = viewSurgery.findViewById(R.id.edit_patient_device_type);
        editPatient_surgerytype = viewSurgery.findViewById(R.id.edit_patient_surgery_type);
        editPatient_surgerytime = viewSurgery.findViewById(R.id.edit_patient_surgery_time);


        editPatient_surgerytime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickDatetime(context, editPatient_surgerytime.getText().toString(), false, new OnDatePicked() {
                    @Override
                    public void setDateText(String text) {
                        editPatient_surgerytime.setText(text);
                    }
                });
            }
        });
        editPatient_birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickDatetime(context, editPatient_birthday.getText().toString(), true, new OnDatePicked() {
                    @Override
                    public void setDateText(String text) {
                        editPatient_birthday.setText(text);
                    }
                });
            }
        });


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


    public List<String[]> getImageList(String idnum, String category){
        List<String[]> filelist = MainActivity.globalConnection.getPicList(idnum,category);
        return filelist;
    }

    public boolean postTextInfo(final String idnum){
        new Thread(new Runnable() {
            @Override
            public void run() {

                String postdata = "name=" + editPatient_name.getText() +
                        "&gender=" + spinnerGender.getSelectedItemPosition() +
                        "&birthday=" + editPatient_birthday.getText() +
                        "&phone=" + editPatient_phone.getText() +
                        "&remark=" + editPatient_remark.getText() +
                        "&devicetype=" + editPatient_devicetype.getText() +
                        "&surgerytype=" + editPatient_surgerytype.getText() +
                        "&surgerytime=" + editPatient_surgerytime.getText() +
                        "&surgerycenter=" + editPatient_surgerypos.getText();

                MainActivity.globalConnection.postPatientInfo(idnum,postdata);
                MainActivity.globalConnection.uploadImg(idnum,"avatar","tmp.jpg");
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
                    picAdapter = new PatientPicAdapter(getImageList(idnum,"pic"));
                    picAdapter.setOnItemClickListener(new PatientPicAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            if( (position+1) == picAdapter.getItemCount()){
                                Message message = Message.obtain(mHandler,2);
                                message.sendToTarget();
                            }
                        }
                    });
                    picAdapter.setOnItemLongClickListener(new PatientPicAdapter.OnItemLongClickListener() {
                        @Override
                        public void onItemLongClick(View view, int position) {
                            ProcessLongClick(position,"pic",viewSurgerypic);
                        }
                    });

                    evalpicAdapter = new PatientPicAdapter(getImageList(idnum,"eval"));
                    evalpicAdapter.setOnItemClickListener(new PatientPicAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            if( (position+1) == evalpicAdapter.getItemCount()){
                                Message message = Message.obtain(mHandler,3);
                                message.sendToTarget();
                            }
                        }
                    });
                    evalpicAdapter.setOnItemLongClickListener(new PatientPicAdapter.OnItemLongClickListener() {
                        @Override
                        public void onItemLongClick(View view, int position) {
                            ProcessLongClick(position,"eval",viewEvalpic);
                        }
                    });

                    epospicAdapter = new PatientPicAdapter(getImageList(idnum,"epos"));
                    epospicAdapter.setOnItemClickListener(new PatientPicAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            if( (position+1) == epospicAdapter.getItemCount()){
                                Message message = Message.obtain(mHandler,4);
                                message.sendToTarget();
                            }
                        }
                    });
                    epospicAdapter.setOnItemLongClickListener(new PatientPicAdapter.OnItemLongClickListener() {
                        @Override
                        public void onItemLongClick(View view, int position) {
                            ProcessLongClick(position,"epos",viewEpospic);
                        }
                    });


                    final String datetime = patientObj.optString("surgerytime");
                    viewAvatar.post(new Runnable() {
                        @Override
                        public void run() {
                            if(filelist.size() > 0)
                                viewAvatar.setImageURI(Uri.fromFile(new File(filelist.get(filelist.size()-1)[0])));
                            editPatient_name.setText(patientObj.optString("name"));
                            editPatient_birthday.setText(patientObj.optString("birthday"));
                            spinnerGender.setSelection(patientObj.optInt("gender"));
                            editPatient_remark.setText(patientObj.optString("remark"));
                            editPatient_phone.setText(patientObj.optString("phone"));
                            editPatient_devicetype.setText(patientObj.optString("devicetype"));
                            editPatient_surgerytype.setText(patientObj.optString("surgerytype"));
                            editPatient_surgerytime.setText(patientObj.optString("surgerytime"));
                            editPatient_surgerypos.setText(patientObj.optString("surgerycenter"));


                            LinearLayoutManager layoutManager = new LinearLayoutManager(pcontext);
                            viewSurgerypic.setLayoutManager(layoutManager);
                            viewSurgerypic.setItemAnimator( new DefaultItemAnimator());
                            viewSurgerypic.setAdapter(picAdapter);

                            LinearLayoutManager layoutManager2 = new LinearLayoutManager(pcontext);
                            viewEpospic.setLayoutManager(layoutManager2);
                            viewEpospic.setItemAnimator( new DefaultItemAnimator());
                            viewEpospic.setAdapter(epospicAdapter);

                            LinearLayoutManager layoutManager3 = new LinearLayoutManager(pcontext);
                            viewEvalpic.setLayoutManager(layoutManager3);
                            viewEvalpic.setItemAnimator( new DefaultItemAnimator());
                            viewEvalpic.setAdapter(evalpicAdapter);
                        }
                    });

                }
            }).start();
        }
        return false;
    }

    public void ProcessLongClick(final int position, final String category,final RecyclerView targetView){
        final PatientPicAdapter tmpAdapter = (PatientPicAdapter) targetView.getAdapter();
        final String fullfilepath = tmpAdapter.GetFileintheList(position)[0];
        final File tmpfile = new File(fullfilepath);
        final String filename = tmpfile.getName();
        Log.d("filename", filename);
        new AlertDialog.Builder(pcontext)
                .setTitle("确认删除")
                .setMessage("图片描述: " + tmpAdapter.GetFileintheList(position)[1])
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if( MainActivity.globalConnection.deleteImg(currentidnum,category,filename) ){
                                    final List<String[]> newDataset = MainActivity.globalConnection.getPicList(currentidnum,category);
                                    targetView.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            tmpAdapter.UpdatePicList(newDataset);
                                            tmpAdapter.notifyDataSetChanged();
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

    public void pickDatetime(final Context context, String olddatetime, final boolean onlydate, final OnDatePicked onDatePicked){
        final Calendar currentDate = Calendar.getInstance();
        if(olddatetime.length() > 8){
            int year = Integer.parseInt(olddatetime.substring(0,4));
            int month = Integer.parseInt(olddatetime.substring(5,7))-1;
            int dayofmonth = Integer.parseInt(olddatetime.substring(8,10));
            int hour = 12;
            int minute = 12;
            if( !onlydate ){
                hour = Integer.parseInt(olddatetime.substring(11,13));
                minute = Integer.parseInt(olddatetime.substring(14,16));
            }
            currentDate.set(year,month,dayofmonth,hour,minute);
        }
        // AlertDialog.THEME_HOLO_LIGHT deprecated
        new DatePickerDialog(context, AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear+=1;
                String newdate = "";
                newdate = String.valueOf(year) + "-";
                if(monthOfYear < 10)
                    newdate += "0";
                newdate += String.valueOf(monthOfYear) + "-";
                if(dayOfMonth < 10)
                    newdate += "0";
                newdate += String.valueOf(dayOfMonth);
                final String fnewdatetime = newdate;
                if( !onlydate ){
                    new TimePickerDialog(context, AlertDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hour, int minute) {
                            String newdatetime = fnewdatetime;
                            if( !onlydate ){
                                newdatetime += " ";
                                if(hour < 10)
                                    newdatetime += "0";
                                newdatetime += String.valueOf(hour) + ":";
                                if(minute < 10)
                                    newdatetime += "0";
                                newdatetime += String.valueOf(minute);
                            }
                            onDatePicked.setDateText(newdatetime);
                        }
                    }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show();
                }else
                    onDatePicked.setDateText(newdate);
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

    interface OnDatePicked{
        void setDateText(String text);
    }


}


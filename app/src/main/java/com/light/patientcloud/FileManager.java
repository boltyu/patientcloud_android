package com.light.patientcloud;

import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class FileManager {

    String[] childPath= {"undefine", "avatar", "pic", "eval", "epos"};
    public String rootPath;

    public FileManager(String rootname){
        rootPath = Environment.getExternalStorageDirectory() + File.separator + rootname;
        File rootdir = new File(rootPath);
        if( !rootdir.exists() ){
            if( !rootdir.mkdir() )
                Log.e("cant create root dir: ",rootdir.getPath());
        }

    }

    public boolean checkChildDir(String idnum){
        try{
            String idnumPath = rootPath + File.separator + idnum;
            File idnumDir = new File(idnumPath);
            if( !idnumDir.exists() ){
                if( !idnumDir.mkdir() )
                    Log.e("cant create root dir: ",idnumDir.getPath());
            }
            for( int i=0; i<childPath.length; i++){
                File childdir = new File(idnumPath + File.separator + childPath[i]);
                if( !childdir.exists() ){
                    if( !childdir.mkdir() )
                        Log.e("cant create child dir: ",childdir.getPath());
                }
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }


    // get file intent whether if exist
    public File getFile(String idnum, String category, String filename){
        String fullfilepath = rootPath + File.separator + idnum + File.separator + category + File.separator + filename;
        return new File(fullfilepath);
    }

    public String getSettings(String key){
        String value = "";
        File fff = new File(rootPath+File.separator+key);
        try {
            FileInputStream ttt = new FileInputStream(fff);
            byte[] bbb = new byte[1024];
            int len = ttt.read(bbb);
            value = new String(bbb,0,len);
            ttt.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("hoststring", value);
        return value;
    }

    public File getUpdateFile(){
        File fff = new File(rootPath+File.separator+"base.apk");
        try {
            if(!fff.exists()){
                fff.createNewFile();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return fff;
    }

    public Boolean saveSettings(String key, String value){
        File fff = new File(rootPath+File.separator+key);
        try {
            FileOutputStream ttt = new FileOutputStream(fff);
            ttt.write(value.getBytes());
            ttt.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public File getAvatar(String idnum){
        String filename = rootPath + File.separator + idnum + File.separator + "avatar" + ".jpg";
        File avatarfile = null;
        try{
            avatarfile = new File(filename);
            if( !avatarfile.exists() ){
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return avatarfile;
    }
}

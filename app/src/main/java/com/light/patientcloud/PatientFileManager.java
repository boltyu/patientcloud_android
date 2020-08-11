package com.light.patientcloud;

import android.os.Environment;
import android.util.Log;

import java.io.File;

public class PatientFileManager {

    String[] childPath= {"settings", "avatar", "pic", "eval", "epos"};
    public String rootPath;

    public PatientFileManager(String rootname){
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

    public File getFile(String idnum, String category, String filename){
        String fullfilepath = rootPath + File.separator + idnum + File.separator + category + File.separator + filename;
        return new File(fullfilepath);
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

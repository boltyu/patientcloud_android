package com.light.patientcloud;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UrlConnection{

    private String sessionstr;
    private String hostaddress = "122.51.67.162:8000";
    private String urlHost = "";
    private String urlLogin = "", urlLogout = "";
    private String urlPatient = "";

    UrlConnection(){
        initUrl(hostaddress);
    }

    public String getHostaddress(){
        return hostaddress;
    }
    public void initUrl(String targetaddress){
        hostaddress = targetaddress;
//        hostaddress = "192.168.44.154:8000";
        urlHost = "http://" + hostaddress + "/";
        urlLogin = urlHost + "doctor/login/";
        urlLogout = urlHost + "doctor/logout/";
        urlPatient = urlHost + "patient/";
    }

    public int postPatientInfo(String idnum, String postdata){
        try{
            String urlTarget = urlPatient;
            if(!idnum.equals("None") && !idnum.equals(""))
                urlTarget = urlTarget + idnum + "/";
            URL targetUrl = new URL(urlTarget);
            HttpURLConnection privateconnection = (HttpURLConnection)targetUrl.openConnection();
            privateconnection.setRequestMethod("POST");
            privateconnection.setConnectTimeout(5000);
            privateconnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            privateconnection.setRequestProperty("Cookie", sessionstr);
            privateconnection.setDoOutput(true);
            OutputStream outLogin = privateconnection.getOutputStream();
            outLogin.write(postdata.getBytes());
            int rcode = privateconnection.getResponseCode();
            if(rcode == 200) {
                InputStreamReader inLogin = new InputStreamReader(privateconnection.getInputStream());
                int len = 0, total = privateconnection.getContentLength();
                char[] recvdata = new char[total];
                while( ( len = inLogin.read(recvdata, len, total-len)) != -1){

                }
                JSONObject re = new JSONObject(String.valueOf(recvdata));
                privateconnection.disconnect();
                if(re.optInt("result") == 200) {
                    return 0;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    public boolean deletePaitent(String idnum){
        try{
            URL targetUrl = new URL(urlPatient);
            HttpURLConnection privateconnection = (HttpURLConnection)targetUrl.openConnection();
            privateconnection.setRequestMethod("POST");
            privateconnection.setConnectTimeout(5000);
            privateconnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            privateconnection.setRequestProperty("Cookie", sessionstr);
            String postdata = "method=delete&idnum="+idnum;
            OutputStream outLogin = privateconnection.getOutputStream();
            outLogin.write(postdata.getBytes());
            int rcode = privateconnection.getResponseCode();
            if(rcode == 200) {
                InputStreamReader inLogin = new InputStreamReader(privateconnection.getInputStream());
                int len = 0, total = privateconnection.getContentLength();
                char[] recvdata = new char[total];
                while( ( len = inLogin.read(recvdata, len, total-len)) != -1){

                }
                JSONObject re = new JSONObject(String.valueOf(recvdata));
                privateconnection.disconnect();
                if(re.optInt("result") == 200) {
                    return true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteImg(String idnum, String category, String filename){
        try{
            URL targetUrl = new URL(urlPatient+idnum+"/"+category+"/"+filename);
            HttpURLConnection privateconnection = (HttpURLConnection)targetUrl.openConnection();
            privateconnection.setRequestMethod("POST");
            privateconnection.setConnectTimeout(5000);
            privateconnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            privateconnection.setRequestProperty("Cookie", sessionstr);
            String postdata = "method=delete";
            OutputStream outLogin = privateconnection.getOutputStream();
            outLogin.write(postdata.getBytes());
            int rcode = privateconnection.getResponseCode();
            if(rcode == 200) {
                InputStreamReader inLogin = new InputStreamReader(privateconnection.getInputStream());
                int len = 0, total = privateconnection.getContentLength();
                char[] recvdata = new char[total];
                while( ( len = inLogin.read(recvdata, len, total-len)) != -1){

                }
                JSONObject re = new JSONObject(String.valueOf(recvdata));
                privateconnection.disconnect();
                if(re.optInt("result") == 200) {
                    return true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public Boolean postRemark(String idnum, String category, String filename, String remark){
        try{
            URL targetUrl = new URL(urlPatient + idnum + "/" + category + "/" + filename);
            HttpURLConnection privateconnection = (HttpURLConnection)targetUrl.openConnection();
            privateconnection.setRequestMethod("POST");
            privateconnection.setConnectTimeout(5000);
            privateconnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            privateconnection.setRequestProperty("Cookie", sessionstr);
            String postdata = "method=remark&data=" + remark;
            OutputStream outLogin = privateconnection.getOutputStream();
            outLogin.write(postdata.getBytes());
            int rcode = privateconnection.getResponseCode();
            if(rcode == 200) {
                InputStreamReader inLogin = new InputStreamReader(privateconnection.getInputStream());
                int len = 0, total = privateconnection.getContentLength();
                char[] recvdata = new char[total];
                while( ( len = inLogin.read(recvdata, len, total-len)) != -1){

                }
                JSONObject re = new JSONObject(String.valueOf(recvdata));
                privateconnection.disconnect();
                if(re.optInt("result") == 200) {
                    return true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }


    public JSONObject getPatientInfo(String idnum){
        try{
            URL targetUrl = new URL(urlPatient+idnum+"/");
            HttpURLConnection privateconnection = (HttpURLConnection)targetUrl.openConnection();
            privateconnection.setRequestMethod("GET");
            privateconnection.setConnectTimeout(5000);
            privateconnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            privateconnection.setRequestProperty("Cookie", sessionstr);
            int rcode = privateconnection.getResponseCode();
            if(rcode == 200) {
                InputStreamReader inLogin = new InputStreamReader(privateconnection.getInputStream());
                int len = 0, total = privateconnection.getContentLength();
                char[] recvdata = new char[total];
                while( ( len = inLogin.read(recvdata, len, total-len)) != -1){

                }
                JSONObject re = new JSONObject(String.valueOf(recvdata));
                privateconnection.disconnect();
                if(re.optInt("result") == 200) {
                    return re.optJSONObject("data");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return new JSONObject();
    }

    public List<String[]> getPatientList(){
        try{
            URL targetUrl = new URL(urlPatient);
            HttpURLConnection privateconnection = (HttpURLConnection)targetUrl.openConnection();
            privateconnection.setRequestMethod("GET");
            privateconnection.setConnectTimeout(5000);
            privateconnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            privateconnection.setRequestProperty("Cookie", sessionstr);
            int rcode = privateconnection.getResponseCode();
            if(rcode == 200) {
                InputStreamReader inLogin = new InputStreamReader(privateconnection.getInputStream());
                int len = 0, total = privateconnection.getContentLength();
                char[] recvdata = new char[total];
                while( ( len = inLogin.read(recvdata, len, total-len)) != -1){

                }
                JSONObject re = new JSONObject(String.valueOf(recvdata));
                privateconnection.disconnect();
                if(re.optInt("result") == 200) {
                    JSONObject patients = re.optJSONObject("data");
                    List<String[]> myDataset = new ArrayList<>();
                    Iterator<String> it_patients = patients.keys();
                    int i = 0;
                    while(it_patients.hasNext()){
                        String idnum = it_patients.next();
                        JSONObject patient = patients.optJSONObject(idnum);
                        myDataset.add(i,new String[]{ idnum,
                                patient.optString("name"),
                                patient.optString("surgerytime"),
                                patient.optString("surgerycenter"),
                                patient.optString("avatar")});
                        i++;
                    }
                    return myDataset;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ArrayList<String[]>();
    }



    public Boolean uploadImg(String idnum, String category, String filename){
        try{
            URL targetUrl = new URL(urlPatient+idnum+"/"+category+"/");
            //hash = MessageDigest.getInstance("MD5").digest();
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";

            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;

            FileInputStream fileInputStream = new FileInputStream(MainActivity.fileManager.getFile(idnum,category,filename));

            HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestMethod("POST");

            connection.setRequestProperty("Cookie", sessionstr);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
                            + "tmp.jpg" + "\"" + lineEnd);
            outputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens
                    + lineEnd);

            fileInputStream.close();
            outputStream.flush();
            outputStream.close();

            return true;
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public Boolean loginUser(String username, String password, int iflogout){
        try{
            String urlTarget = urlLogin;
            if(iflogout == 1)
                urlTarget = urlLogout;
            URL targetUrl = new URL(urlTarget);
            HttpURLConnection privateconnection = (HttpURLConnection)targetUrl.openConnection();
            privateconnection.setRequestMethod("POST");
            privateconnection.setConnectTimeout(5000);
            String logindata = "username="+username+"&password="+password;
            privateconnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            privateconnection.setRequestProperty("Content-Length", logindata.length()+"");
            privateconnection.setDoOutput(true);
            OutputStream outLogin = privateconnection.getOutputStream();
            outLogin.write(logindata.getBytes());
            int rcode = privateconnection.getResponseCode();
            if(rcode == 200) {
                if(iflogout == 1)
                    return true;
                InputStreamReader inLogin = new InputStreamReader(privateconnection.getInputStream());
                int len = 0, total = privateconnection.getContentLength();
                char[] recvdata = new char[total+1024];
                while( ( len = inLogin.read(recvdata, len, total-len)) != -1){

                }
                JSONObject re = new JSONObject(String.valueOf(recvdata));
                privateconnection.disconnect();
                if(re.optInt("result") == 200) {
                    String cookieVal = privateconnection.getHeaderField("Set-Cookie");
                    sessionstr = cookieVal.substring(0, cookieVal.indexOf(";"));
                    MainActivity.fileManager.saveSettings("seesionstr",sessionstr);
                    return true;
                }
            }else{

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public List<String[]> getPicList(String idnum,String category) {
        List<String[]> piclist = new ArrayList<>();
        try {
            URL targetUrl = new URL(urlPatient + idnum + "/" + category + "/");
            HttpURLConnection privateconnection = (HttpURLConnection)targetUrl.openConnection();
            privateconnection.setRequestMethod("GET");
            privateconnection.setConnectTimeout(5000);
            privateconnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            privateconnection.setRequestProperty("Cookie", sessionstr);
            int rcode = privateconnection.getResponseCode();
            if(rcode == 200) {
                InputStreamReader inLogin = new InputStreamReader(privateconnection.getInputStream());
                int len = 0, total = privateconnection.getContentLength();
                char[] recvdata = new char[total];
                while( ( len = inLogin.read(recvdata, len, total-len)) != -1){

                }
                JSONObject re = new JSONObject(String.valueOf(recvdata));
                privateconnection.disconnect();
                if(re.optInt("result") == 200) {
                    JSONObject listdata = re.optJSONObject("data");
                    Iterator<String> it_listdata = listdata.keys();
                    int i = 0;
                    while(it_listdata.hasNext()) {
                        String filename = it_listdata.next();
                        String remark = listdata.optString(filename);
                        File tmpfile = MainActivity.fileManager.getFile(idnum,category,filename);
                        if(!tmpfile.exists()){
                            downloadImg(idnum,category,filename,tmpfile);
                        }else{
                        }
                        piclist.add(new String[]{
                                tmpfile.getAbsolutePath(),remark
                        });
                    }
                }
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return piclist;
    }

    public String getUpdateUrl(){
        return urlHost + "apk/update/";
    }



    public Boolean downloadImg(String idnum, String category, String filename, File targetfile){
        try {
            URL targetUrl = new URL(urlPatient + idnum + "/" + category + "/" + filename);
            HttpURLConnection privateconnection = (HttpURLConnection) targetUrl.openConnection();
            privateconnection.setRequestProperty("Cookie", sessionstr);
            privateconnection.setDoInput(true);
            privateconnection.connect();
            OutputStream outputStream = new FileOutputStream(targetfile);
            InputStream inputStream = privateconnection.getInputStream();
            byte [] buffer = new byte[1024];
            int len = 0, total = 0;
            while( ( len = inputStream.read(buffer)) != -1){
                outputStream.write(buffer,0,len);
                total += len;
            }
            outputStream.close();
            privateconnection.disconnect();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<String> getOptionList(String optionname){
        List<String> optionlist = new ArrayList<>();
        try {
            URL targetUrl = new URL(urlPatient + optionname + "/");
            HttpURLConnection privateconnection = (HttpURLConnection)targetUrl.openConnection();
            privateconnection.setRequestMethod("GET");
            privateconnection.setConnectTimeout(5000);
            privateconnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            privateconnection.setRequestProperty("Cookie", sessionstr);
            int rcode = privateconnection.getResponseCode();
            if(rcode == 200) {
                InputStreamReader inLogin = new InputStreamReader(privateconnection.getInputStream());
                int len = 0, total = privateconnection.getContentLength();
                char[] recvdata = new char[total];
                while( ( len = inLogin.read(recvdata, len, total-len)) != -1){

                }
                JSONObject re = new JSONObject(String.valueOf(recvdata));
                privateconnection.disconnect();
                if(re.optInt("result") == 200) {
                    JSONObject listdata = re.optJSONObject("data");
                    Iterator<String> it_listdata = listdata.keys();
                    while(it_listdata.hasNext()) {
                        String idstr = it_listdata.next();
                        optionlist.add(Integer.parseInt(idstr),listdata.optString(idstr));
                    }
                }
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return optionlist;

    }
}

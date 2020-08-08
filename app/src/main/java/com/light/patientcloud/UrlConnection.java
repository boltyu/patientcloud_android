package com.light.patientcloud;

import android.os.Environment;
import android.provider.ContactsContract;
import android.util.JsonReader;

import org.json.JSONObject;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UrlConnection{

    private String sessionid;
    private String hostaddress = "192.168.1.132";
    private String hostport = "8000";
    private String urlHost = "";
    private String urlLogin = "";
    private String urlPatient = "";
    private String urlPatient_pic_suffix = "/pic/";
    private String urlPatient_epos_suffix = "/epos/";
    UrlConnection(){
        urlHost = "http://" + hostaddress + ":" + hostport + "/";
        urlLogin = urlHost + "doctor/login/";
        urlPatient = urlHost + "patient/";
    }

    public int postPatientInfo(String idnum, JSONObject patientinfo){
        try{
            URL targetUrl = new URL(urlPatient+idnum+"/");
            HttpURLConnection privateconnection = (HttpURLConnection)targetUrl.openConnection();
            String postdata = "data=" +patientinfo.toString();
            privateconnection.setRequestMethod("POST");
            privateconnection.setConnectTimeout(5000);
            privateconnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            privateconnection.setRequestProperty("Cookie",sessionid);
            privateconnection.setRequestProperty("Content-Length", postdata.length()+"");
            int rcode = privateconnection.getResponseCode();
            if(rcode == 200) {
                InputStreamReader inLogin = new InputStreamReader(privateconnection.getInputStream());
                char[] sdf = new char[4096];
                inLogin.read(sdf);
                JSONObject re = new JSONObject(String.valueOf(sdf));
                privateconnection.disconnect();
                if(re.optInt("result") == 200) {
                    return 0;
                }else{
                    return 1;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
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
            privateconnection.setRequestProperty("Cookie",sessionid);
            privateconnection.setDoOutput(true);
            OutputStream outLogin = privateconnection.getOutputStream();
            outLogin.write(postdata.getBytes());
            int rcode = privateconnection.getResponseCode();
            if(rcode == 200) {
                InputStreamReader inLogin = new InputStreamReader(privateconnection.getInputStream());
                char[] sdf = new char[1024];
                inLogin.read(sdf);
                JSONObject re = new JSONObject(String.valueOf(sdf));
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

    public JSONObject getPatientInfo(String idnum){
        try{
            URL targetUrl = new URL(urlPatient+idnum+"/");
            HttpURLConnection privateconnection = (HttpURLConnection)targetUrl.openConnection();
            privateconnection.setRequestMethod("GET");
            privateconnection.setConnectTimeout(5000);
            privateconnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            privateconnection.setRequestProperty("Cookie",sessionid);
            int rcode = privateconnection.getResponseCode();
            if(rcode == 200) {
                InputStreamReader inLogin = new InputStreamReader(privateconnection.getInputStream());
                char[] sdf = new char[65536];
                inLogin.read(sdf);
                JSONObject re = new JSONObject(String.valueOf(sdf));
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

    public JSONObject getPatientList(){
        try{
            URL targetUrl = new URL(urlPatient);
            HttpURLConnection privateconnection = (HttpURLConnection)targetUrl.openConnection();
            privateconnection.setRequestMethod("GET");
            privateconnection.setConnectTimeout(5000);
            privateconnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            privateconnection.setRequestProperty("Cookie",sessionid);
            int rcode = privateconnection.getResponseCode();
            if(rcode == 200) {
                InputStreamReader inLogin = new InputStreamReader(privateconnection.getInputStream());
                char[] sdf = new char[4096];
                inLogin.read(sdf);
                JSONObject re = new JSONObject(String.valueOf(sdf));
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

    public Boolean uploadImg(String idnum, String category, String fullfilepath){
        try{
            URL targetUrl = new URL(urlPatient+idnum+"/"+category+"/");

            String pathToOurFile = Environment.getExternalStorageDirectory() + "/tmp.jpg";
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";

            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;

            FileInputStream fileInputStream = new FileInputStream(new File(
                    pathToOurFile));

            HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestMethod("POST");

            connection.setRequestProperty("Cookie",sessionid);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);

            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream
                    .writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
                            + pathToOurFile + "\"" + lineEnd);
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

            int serverResponseCode = connection.getResponseCode();
            String serverResponseMessage = connection.getResponseMessage();

            fileInputStream.close();
            outputStream.flush();
            outputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }




        return false;
    }

    public Boolean loginUser(String username, String password){
        try{
            URL targetUrl = new URL(urlLogin);
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
                InputStreamReader inLogin = new InputStreamReader(privateconnection.getInputStream());
                char[] sdf = new char[128];
                inLogin.read(sdf);
                JSONObject re = new JSONObject(String.valueOf(sdf));
                privateconnection.disconnect();
                if(re.optInt("result") == 200) {
                    String cookieVal = privateconnection.getHeaderField("Set-Cookie");
                    sessionid = cookieVal.substring(0, cookieVal.indexOf(";"));
                    return true;
                }
            }else{

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

}

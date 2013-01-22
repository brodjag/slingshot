package com.slingshot.lib;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.*;

/**
 * Created by IntelliJ IDEA.
 * User: 1
 * Date: 29.12.11
 * Time: 22:50
 * To change this template use File | Settings | File Templates.
 */
public class saveFile {

    private Context con;

    public saveFile(Context c) {
        con = c;
    }

    public boolean start(String strUrl, String fileName) {
        boolean ok = false;

        Log.d("saveFile_log", "mark 1");
       // Authenticator.setDefault(new1 MyAuthenticator());

        try {
            FileOutputStream ff;
            URL url = new URL(strUrl);
            url.getAuthority();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(15000);
            conn.setDefaultUseCaches(false);

           // conn.setRequestProperty("Accept-Encoding", "identity");

            conn.connect();
            Log.d("savef1", strUrl + " code: " + conn.getResponseCode());
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

                ff = con.openFileOutput(fileName, Context.MODE_PRIVATE);
                //InputStream
                InputStream inputStream = conn.getInputStream();
                int totalSize = conn.getContentLength();
                //int downloadedSize = 0;
                byte[] buffer = new byte[1024];


                int bufferLength; //used to store a temporary size of the buffer
                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    ff.write(buffer, 0, bufferLength);
                    //ff.w
                }//end while

                ff.close();
                // inputStream.
                return true;

            } else {
                Log.d("satSavefile", ("error http code" + conn.getResponseCode() + " for url" + strUrl));
            }
            // conn.wait(1000);
            //for (int q = 0; q < 500000; q++) {   String v = "";         }


        } catch (Exception e) {
            Log.d("satSavefile", ("error save file " + strUrl));
        }


//catch some possible errors...
        return ok;
    }

    public void startbg(String strUrl, String fileName) {

        new TaskAjax().doInBackground(strUrl, fileName);


    }

    private class TaskAjax extends AsyncTask<String, Void, Void> {

        protected Void doInBackground(String... Id_urls) {
            String id_local = Id_urls[0];
            start(Id_urls[0], Id_urls[1]);
            return null;
        }

        protected void onProgressUpdate() {
        }

        protected void onPostExecute(Element mydoc) {

        }

        protected void onPreExecute() {

        }
    }


    //authrecation for http
    public class MyAuthenticator extends Authenticator {
        // This method is called when a password-protected URL is accessed
        protected PasswordAuthentication getPasswordAuthentication() {
            // Get information about the request
            String promptString = getRequestingPrompt();
            String hostname = getRequestingHost();
            InetAddress ipaddr = getRequestingSite();
            int port = getRequestingPort();

            // Get the username from the user...
            String username = "nokia-full";

            // Get the password from the user...
            String password = "Spat23'Yong";

            // Return the information
            return new PasswordAuthentication(username, password.toCharArray());
        }

    }

    public Element readXmlFile(String fileName) {


        File file = new File(fileName);
        InputStream in;
        try {
            Log.d("saveFile1", "mark1");
            //InputStream  stream=con.openFileInput(fileName);

            in = new BufferedInputStream(con.openFileInput(fileName), 8);


            //xml parsing
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(in);
            Element element = document.getDocumentElement();
            in.close();
            //  ff.close();
            return element;
        } catch (Exception e) {
            Log.d("saveFile1", "error save");
        }
        Log.d("saveFile1", "exit save file");
        return null;

    }


    ///load file for sdcard
    public boolean loadFile(String strUrl, String fileName) {
        boolean ok = false;

        Log.d("loadFile", "mark 1");
        // Authenticator.setDefault(new1 MyAuthenticator());

        try {
            // FileOutputStream ff;
            URL url = new URL(strUrl);
            url.getAuthority();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(35000);
            conn.setDefaultUseCaches(false);

            conn.connect();
            Log.d("loadFile", "code: " + conn.getResponseCode());
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

                File f=new File(fileName+"tmp") ;
                //  if( (f.canWrite()==false) ){Log.d("loadFile"," no write" ); return false;} ;
                if(isSDCardMounted()==false){
                    Log.d("loadFile", "no mount cd"); return false;}
                OutputStream outStream = new FileOutputStream(f);
                // ff = openFileOutput(f, Context.MODE_PRIVATE);

                //InputStream
                InputStream inputStream = conn.getInputStream();
                int totalSize = conn.getContentLength();
                //int downloadedSize = 0;
                byte[] buffer = new byte[1024];


                int bufferLength; //used to store a temporary size of the buffer
                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    outStream.write(buffer, 0, bufferLength);
                }//end while
                outStream.close();

                File to = new File(fileName);
                f.renameTo(to);
                // ff.close();
                // inputStream.
                return true;

            } else {
                Log.d("loadFile", ("error http code" + conn.getResponseCode() + " for url" + strUrl));
            }



        } catch (Exception e) {
            Log.d("loadFile", ("error save file " + strUrl));
        }


//catch some possible errors...
        return ok;
    }

    public boolean isSDCardMounted() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }





}

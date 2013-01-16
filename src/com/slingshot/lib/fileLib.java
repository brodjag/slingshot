package com.slingshot.lib;


import android.content.Context;
import android.os.Environment;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: 1
 * Date: 17.02.11
 * Time: 21:54
 * class for read and write files
 */
public class fileLib {

    private Context context;

    public fileLib(Context context) {
        this.context = context;
    }

    //read file
    public String read(String fileName) {
        String q = "";
        String buf;
        try {
            FileInputStream ff = context.openFileInput(fileName);
            InputStreamReader inputreader = new InputStreamReader(ff);
            BufferedReader buffreader = new BufferedReader(inputreader, 8);

            while ((buf = buffreader.readLine()) != null) {
                // if (q.equals("")){q = q+buf;}else {q ="\n"+ q+buf;}
                q = q + buf;
            }

            ff.close();
        } catch (Exception e) {
            //q = null;
            return null;
        }
        if (q.equals("")) {
            q = null;
        }
        return q;
    }

    //write file
    public void write(String fileName, String data) {
        try {
            FileOutputStream ff = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ff.write(data.getBytes());

            ff.close();
        } catch (Exception e) {
        }
    }









  //  private Handler mHandler = new Handler();
    //loading fade..
   // private ProgressDialog dialog;
  /*
    public void waitShow() {
        try {
           mHandler.post(new Runnable() {
                public void run() {
                  //  dialog = ProgressDialog.show(context, "", "Loading. Please wait...", true);
                }
            });

        } catch (Exception e) {
        }


    }

    public void waitHide() {
        try {
            mHandler.post(new Runnable() {
                public void run() {
                   // dialog.dismiss();
                }
            });
        } catch (Exception e) {
        }

    }


   */

    public  void AppendToFile( String path, String dataIn )
    {
        try {

            File root = Environment.getExternalStorageDirectory();
            if (root.canWrite()){
                File f = new File(root, path);
               OutputStream outStream = new FileOutputStream(f,true);
                outStream.write(dataIn.getBytes("utf8"));

                outStream.close();

            }
        }catch (Exception e){}
    }

   public String readAppendetFile(String path){
        String q = "";
        String buf;
        try {
                File root = Environment.getExternalStorageDirectory();
                if (root.canWrite()){
                    File f = new File(root, path);
                    InputStream inputStream = new FileInputStream(f);

                    InputStreamReader inputreader = new InputStreamReader(inputStream);
                    BufferedReader buffreader = new BufferedReader(inputreader, 8);
                    while ((buf = buffreader.readLine()) != null) {
                        // if (q.equals("")){q = q+buf;}else {q ="\n"+ q+buf;}
                        q = q + buf+"\n";
                    }

                    inputStream.close();

                }

        } catch (Exception e) {
            //q = null;
            return null;
        }
      return q;
    }


  public void removeAppendedFile(String path){
      File root = Environment.getExternalStorageDirectory();
      if (root.canWrite()){
          File f = new File(root, path);
         f.delete();

      }
  }



 static public boolean isSDCardMounted() {
        boolean b=android.os.Environment.getExternalStorageState().equals( android.os.Environment.MEDIA_MOUNTED);
//        Toast.makeText(con,"сд-карта не подключена",Toast.LENGTH_LONG).show();

        return b;
    }

}


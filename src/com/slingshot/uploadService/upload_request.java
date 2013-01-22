package com.slingshot.uploadService;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewManager;
import android.widget.Toast;
import com.slingshot.R;
import com.slingshot.add_expense_view.addExpensActivity;
import com.slingshot.lib.DatabaseHelper;
import com.slingshot.lib.fileLib;
import com.slingshot.lib.soapFromFile;
import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: brodjag
 * Date: 07.01.13
 * Time: 16:00
 * the request uploads expenses asynchronous
 * and  prepares file with envelope
 */
public class upload_request {
    Context con;
    String fileEnvelopePath="slingshot/envelope.soap";
    String login="TestTraveler";
    String password="$$apvvord";
    String project="";
    String url="";
    String nameArea="";

    public upload_request(Context c){
         con=c;
        if(!fileLib.isSDCardMounted()){Toast.makeText(con,"Cd card isn't connected", Toast.LENGTH_LONG).show(); return;}
        DatabaseHelper dh=new DatabaseHelper(con);
        url=dh.getURL();
        nameArea=dh.getSetting("url_area");
        login=dh.getSetting("login");
        password=dh.getSetting("password");
        project=dh.getSetting("Project");
        dh.close();

        new spinTask().execute();

    }


private class spinTask extends AsyncTask<Void,Integer,Element> {
       // String envelope;
     //   loadNotification ln;
       postCallUpload postUpl;


        @Override
        protected Element doInBackground(Void... voids) {
            DatabaseHelper dh=new DatabaseHelper(con);
            Cursor cursor=dh.getExpenseAll();


            for (int i=0; i<cursor.getCount(); i++){
                cursor.moveToPosition(i);

                publishProgress(2*i);
                getEnvelope(cursor);
              //  ln.setPosition(i,cursor.getCount());
                soapFromFile sp=new soapFromFile(fileEnvelopePath);
                publishProgress(2*(i)+1);
               // ln.setPosition(2*(i)+1,2*cursor.getCount());
                 Element body= sp.call(url,nameArea+"/PostExpenses");
               // Log.d("PostExpensesResponse1",""+body.getLocalName());
               boolean s=  postUpl.mkItem(body, cursor.getString(0),cursor.getString(3),sp.responseString);
               if(s){publishProgress(2*(i)+1, Integer.parseInt(cursor.getString(0)));}
              //  ln.setPosition(2*(i+1),2*cursor.getCount());
                bodyText=sp.responseString;
               // Log.d("soap", envelope);

            }
            cursor.close(); dh.close();

            return null;
        }

        String bodyText="";
        ProgressDialog dialog=null;
        // public ProgressDialog waitDialog=null;
        @Override
        protected void onPreExecute(){
            postUpl=new  postCallUpload(con);
            //remove old log file
            fileLib fl=new fileLib(con);  fl.removeAppendedFile(addExpensActivity.mainFolder+"/"+postCallUpload.reportFile);

            DatabaseHelper dh=new DatabaseHelper(con);
            Cursor cursor=dh.getExpenseAll(); dh.close();
         //   ln=new1 loadNotification(con);
            //   waitDialog= ProgressDialog.show(con, "", "Loading. Please wait...", true);
            dialog = new ProgressDialog(con);
            dialog.setMessage("Uploading...");
            dialog.setIndeterminate(false);
            dialog.setTitle("UPLOADING");
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setProgress(0);
            dialog.setMax(cursor.getCount()*2);
            dialog.setCancelable(false);
            dialog.setSecondaryProgress(0);
            dialog.show();

            cursor.close();
        }
        @Override
        protected void onPostExecute(Element body){
            dialog.dismiss();
          // ((Activity) con).finish();
           // con.startActivity(new1 Intent(con,listActivity.class));
            postUpl.postLoadingDialog();
             //con.startActivity(new1 Intent(con, uploadReportActivity.class));
           // Log.d("soapFromFile",""+bodyText);
            // Toast.makeText(con,""+bodyText,Toast.LENGTH_LONG).show();
            //  waitDialog.dismiss();


        }

        @Override
        protected void onProgressUpdate(Integer... value)
        {
            dialog.setProgress(value[0]);
            dialog.setProgressNumberFormat("");
            super.onProgressUpdate(value);
            //Toast.makeText(con,"val="+value[0],Toast.LENGTH_SHORT).show();

            //remove item from list view


            if (value.length>1){
                View list= ((Activity) con).findViewById(R.id.list_expenses);
                View item= list.findViewWithTag(value[1]);
                ((ViewManager) item.getParent()).removeView(item);
            }

        }
}
//end task



private void getEnvelope(Cursor cursor) {
      File file= new File(Environment.getExternalStorageDirectory(),fileEnvelopePath);
        if (file.exists()){file.delete();}
        try {     file.createNewFile();} catch (IOException e) {e.printStackTrace();}

        fileLib fl=new fileLib(con);

      fl.AppendToFile(fileEnvelopePath, "<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
        "    <Body>\n" +
        "        <PostExpenses xmlns=\"http://www.slingshotsoftware.com/XmlNamespaces/WebServices/G2\">\n" +
        "            <Project>" + project + "</Project>\n" +
        "            <UserID>" + login + "</UserID>\n" +
        "            <Password>" + password + "</Password>\n" +
        "            <Data>\n" +
        "                <Expenses>\n");
                             getEnvelopeExpense(cursor);

      fl.AppendToFile(fileEnvelopePath,   "                </Expenses>\n" +
            "            </Data>\n" +
            "        </PostExpenses>\n" +
            "    </Body>\n" +
            "</Envelope>\n\n");

}

   String getEnvelopeExpense(Cursor c){
    String Expense="";

      // DatabaseHelper dh=new1 DatabaseHelper(con);
      // Cursor c=dh.getExpenseAll();

   //    for (int i=0; i<c.getCount(); i++){


          String id= c.getString(0);
       Log.d("upload id=",id);
          String Code=c.getString(1);

           Calendar calendar=Calendar.getInstance();
           calendar.setTimeInMillis(c.getLong(2));
          // String Timestamp= calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.DAY_OF_MONTH);
           SimpleDateFormat sdf =   new SimpleDateFormat("yyy-MM-dd");
           String Timestamp =sdf.format(calendar.getTime());

           String desc=c.getString(3);
           String amount=c.getString(4);

         //  getImgs(id);

       //  Expense=Expense+
           fileLib fl=new fileLib(con);
           fl.AppendToFile(fileEnvelopePath,      "\n\n<Expense>" +
                   "                        <Timestamp>"+Timestamp+"</Timestamp>\n" +
                   "                        <Code>"+Code+"</Code>\n" +
                   "                        <Amount>"+amount+"</Amount>\n" +
                   "                        <Description>"+desc+"</Description>\n" +
                   "                        <Images>\n");

           getImgs(id);

           fl.AppendToFile(fileEnvelopePath,"                        </Images>\n" +
                                            "                    </Expense>\n");

     //  }
     //  Log.d("Expense",Expense);
     //  c.close(); dh.close();
       return Expense;
   }



    //make <image>
   String getImgs(String id_expense){
       String path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ addExpensActivity.mainFolder+"/imgs/"+id_expense;
       Log.d("ImgBase64",path);
       File root=new File(path);
       if(!root.exists()){root.mkdir();}
     File[]  ImgsInIdFolder=root.listFiles();
       String res="";
       fileLib fl=new fileLib(con);
       for(int i=0; i<ImgsInIdFolder.length; i++){


             fl.AppendToFile(fileEnvelopePath," <Image>"+
                    "                                <Data>"+getImagesBase64(ImgsInIdFolder[i].getAbsolutePath())+"</Data>\n" +
                   "                                <Type>JPG</Type>\n" +
                   "                            </Image>\n\n");
         //   Log.d("ImgBase64_b64",ImgBase64);
       }

    return res;
   }

    //code images to Base64
   String getImagesBase64(String path){      /*
       Bitmap bm = BitmapFactory.decodeFile(path);
       ByteArrayOutputStream baos = new1 ByteArrayOutputStream();
       bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
       byte[] b = baos.toByteArray();       */

       File f=new File(path);
       String encodedImage = null;
       try {
           encodedImage = Base64.encodeToString(IOUtil.readFile(f), Base64.DEFAULT);
       } catch (IOException e) {
           e.printStackTrace();
       }
       //  Base64.encode(b, Base64.NO_WRAP);

       return encodedImage;
   }

    public static class IOUtil
    {
        public static byte[] readFile (String file) throws IOException {
            return readFile(new File(file));
        }

        public static byte[] readFile (File file) throws IOException {
            // Open file
            RandomAccessFile f = new RandomAccessFile(file, "r");

            try {
                // Get and check length
                long longlength = f.length();
                int length = (int) longlength;
                if (length != longlength) throw new IOException("File size >= 2 GB");

                // Read file and return data
                byte[] data = new byte[length];
                f.readFully(data);
                return data;
            }
            finally {
                f.close();
            }
        }
    }
}

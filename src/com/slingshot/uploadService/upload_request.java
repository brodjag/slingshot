package com.slingshot.uploadService;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;
import com.slingshot.addExpensActyvity;
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
 * and prepares file with  envelope
 */
public class upload_request {
    Context con;
    String fileEnvelopePath="slingshot/envelope.soap";
    String login="TestTraveler";
    String password="$$apvvord";
    String project="";
    String url;
    public upload_request(Context c){
         con=c;

        DatabaseHelper dh=new DatabaseHelper(con);
        url=dh.getURL();
        login=dh.getSetting("login");
        password=dh.getSetting("password");
        project=dh.getSetting("Project");
        dh.close();
        new spinTask().execute();
    }


    private class spinTask extends AsyncTask<Void,Void,Element> {
       // String envelope;
        loadNotification ln;

        @Override
        protected Element doInBackground(Void... voids) {
            DatabaseHelper dh=new DatabaseHelper(con);
            Cursor cursor=dh.getExpenseAll();




            for (int i=0; i<cursor.getCount(); i++){
                cursor.moveToPosition(i);

                getEnvelope(cursor);
                ln.setPosition(i,cursor.getCount());
                soapFromFile sp=new soapFromFile(fileEnvelopePath);
                ln.setPosition(2*(i)+1,2*cursor.getCount());
                 Element body= sp.call("http://support.slingshotsoftware.com/webservices4test/TripExpenseCapture.asmx","http://www.slingshotsoftware.com/XmlNamespaces/WebServices/G2/PostExpenses");
                ln.setPosition(2*(i+1),2*cursor.getCount());
                bodyText=sp.responseString;
               // Log.d("soap", envelope);

            }
            cursor.close(); dh.close();

            return null;
        }

        String bodyText="";
        // public ProgressDialog waitDialog=null;
        protected void onPreExecute(){
            ln=new loadNotification(con);
          //  envelope= "";

            //   waitDialog= ProgressDialog.show(con, "", "Loading. Please wait...", true);

        }

        protected void onPostExecute(Element body){
            ln.setUploaded();
            Log.d("soapFromFile",""+bodyText);
             Toast.makeText(con,""+bodyText,Toast.LENGTH_LONG).show();
            //  waitDialog.dismiss();
            if (body!=null){

                Element   PostExpensesResponse=(Element)  body.getElementsByTagName("PostExpensesResponse").item(0).getFirstChild();
                String Code=  PostExpensesResponse.getElementsByTagName("Code").item(0).getFirstChild().getNodeValue().toString();
                 Toast.makeText(con, Code, Toast.LENGTH_SHORT).show();
                if (Code.equals("Success")){
                  //  fileLib mf=new fileLib(con);
                  //  mf.write("ExpenseCodes.xml",bodyText);
                  //  startActivity(new Intent(con,listActivity.class));
                   // finish();
                }else {
                    String message =PostExpensesResponse.getElementsByTagName("Message").item(0).getFirstChild().getNodeValue().toString();
                //    showDailog(message);

                }
            }else {
                //Toast.makeText(con, "chek net (activity)", Toast.LENGTH_SHORT).show();
               // showDailog("Error connection to server ");

            }
        }
    }

private void getEnvelope(Cursor cursor) {
      File file= new File(Environment.getExternalStorageDirectory(),fileEnvelopePath);
        file.delete();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

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

      // DatabaseHelper dh=new DatabaseHelper(con);
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
       String path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ addExpensActyvity.mainFolder+"/"+id_expense;
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
       ByteArrayOutputStream baos = new ByteArrayOutputStream();
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

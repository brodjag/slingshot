package com.slingshot;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.slingshot.lib.DatabaseHelper;
import com.slingshot.lib.fileLib;
import com.slingshot.lib.soap;
import org.w3c.dom.Element;

public class SplashScreenActivity extends Activity {

    Activity con;
    String login="TestTraveler";
    String password="$$apvvord";
    String project="";
    String url;
    String nameArea="";
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen); con=this;
        DatabaseHelper dh=new DatabaseHelper(con);
        url=dh.getURL();
        login=dh.getSetting("login");
        password=dh.getSetting("password");
        project=dh.getSetting("Project");
        nameArea=dh.getSetting("url_area");
        dh.close();
        new spinTask().execute();

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            // String version = pInfo.versionName;
            ((TextView) findViewById(R.id.splash_text)).setText(""+ pInfo.versionName);
        } catch (Exception e){}

        //fileLib fl=new1 fileLib(con);
      //  fl.AppendToFile("qqqq.qq","test\n");

       // startActivity(new1 Intent(this,settingActivity.class));

    }


    private class spinTask extends AsyncTask<Void,Void,Element> {
        @Override
        protected Element doInBackground(Void... voids) {
            String envelope="<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                    "    <Body>\n" +
                    "        <GetExpenseCodes xmlns=\"http://www.slingshotsoftware.com/XmlNamespaces/WebServices/G2\">\n" +
                    "            <Project>"+project+"</Project>\n" +
                    "            <UserID>"+login+"</UserID>\n" +
                    "            <Password>"+password+"</Password>\n" +
                    "        </GetExpenseCodes>\n" +
                    "    </Body>\n" +
                    "</Envelope>";


            soap sp=new soap();
            //"http://fpat.ru/DemoEnterprise/ws/1csoap.1cws"
            Element body= sp.call(url,nameArea+"/GetExpenseCodes",envelope);
            bodyText=sp.responseString;
            Log.d("soap",envelope);
            return body;  //To change body of implemented methods use File | Settings | File Templates.
        }

        String bodyText="";
        // public ProgressDialog waitDialog=null;
        protected void onPreExecute(){
            //   waitDialog= ProgressDialog.show(con, "", "Loading. Please wait...", true);
        }

        protected void onPostExecute(Element body){
            if(!fileLib.isSDCardMounted()){    Toast.makeText(con, "Cd card isn't connected", Toast.LENGTH_LONG).show(); }

           Log.d("soap",""+bodyText);
          //  Toast.makeText(con,""+bodyText,Toast.LENGTH_LONG).show();
            //  waitDialog.dismiss();
            if (body!=null){

             Element  GetExpenseCodesResult=(Element)  body.getElementsByTagName("GetExpenseCodesResponse").item(0).getFirstChild();
             String Code=  GetExpenseCodesResult.getElementsByTagName("Code").item(0).getFirstChild().getNodeValue().toString();
               // Toast.makeText(con,Code,Toast.LENGTH_SHORT).show();
                if (Code.equals("Success")){
                 fileLib mf=new fileLib(con);
                 mf.write("ExpenseCodes.xml",bodyText);
                startActivity(new Intent(con,listActivity.class));
                finish();
                }else {
                    String message =GetExpenseCodesResult.getElementsByTagName("Message").item(0).getFirstChild().getNodeValue().toString();
                    showDailog(message);

                }
            }else {
                //Toast.makeText(con, "chek net (activity)", Toast.LENGTH_SHORT).show();
                showDailog("Error connecting to server ");

            }
        }
    }



    void showDailog(String reason){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(reason)
                .setCancelable(false)
                .setTitle("Slingshot")
                .setPositiveButton("Review Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent intent=new Intent(con, settingActivity.class);
                        intent.putExtra("back",settingActivity.backToSplash);

                        con.startActivity(intent);
                        con.finish();
                        //  Toast.makeText(con,"показать настройки",Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        con.finish();
                    }
                }) ;
                /*
                .setNeutralButton("Restart", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        con.startActivity(new Intent(con, SplashScreenActivity.class));
                        con.finish();
                    }
                });    */
        AlertDialog alert = builder.create();
        alert.show();

    }



}

package com.slingshot.uploadService;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;
import com.slingshot.add_expense_view.addExpensActivity;
import com.slingshot.lib.DatabaseHelper;
import com.slingshot.lib.fileLib;
import com.slingshot.listActivity;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: brodjag
 * Date: 15.01.13
 * Time: 15:59
 * call when expenses uploading to delete expense or to write logs;
 */
public class postCallUpload {
    Context con;
 public static    String reportFile="uploadReport.txt";
    public postCallUpload(Context c){
        con=c;

    }

public boolean isSuccess=true;
    public boolean mkItem(Element body, String id,String desc,String serverText, String expenseCode){
        fileLib fl=new fileLib(con);
        String reportPath= addExpensActivity.mainFolder+"/"+reportFile;


        try {

            if (body!=null){

              Element   PostExpensesResponse= (Element) body.getElementsByTagName("PostExpensesResponse").item(0);
                Element PostExpensesResult= (Element) PostExpensesResponse.getElementsByTagName("PostExpensesResult").item(0);
                String Code=  PostExpensesResult.getElementsByTagName("Code").item(0).getFirstChild().getNodeValue().toString();

                if (Code.equals("Success")){
                    fl.AppendToFile(reportPath,expenseCode +" \""+desc+"\""+" ... ok\n\n");
                    DatabaseHelper dh=new DatabaseHelper(con);
                    dh.removeExpenseId(id);
                    dh.close();
                    listActivity.removeImgFile(id);
                    return true;

                }else {
                    String message =PostExpensesResponse.getElementsByTagName("Message").item(0).getFirstChild().getNodeValue().toString();
                    fl.AppendToFile(reportPath,"["+desc+"]"+"... error\n");
                    fl.AppendToFile(reportPath,message+"\n\n");
                    isSuccess=false;

                }
            }else {
                fl.AppendToFile(reportPath,"["+desc+"]"+"... connection error\n");
                fl.AppendToFile(reportPath,"Server didn't answer"+"\n\n");
                isSuccess=false;

            }


        }catch (Exception e){
         //   Log.d("qqqq",e.getLocalizedMessage());
            fl.AppendToFile(reportPath,""+desc+""+"... Unknown error. \nServer log:\n");
            fl.AppendToFile(reportPath,serverText);
            isSuccess=false;
        }
        return false;
    }



public void postLoadingDialog(){
    if(!isSuccess){
        AlertDialog.Builder builder = new AlertDialog.Builder(con);

        builder.setMessage("There have been errors uploading data. Would you like to see the report? ")
                .setCancelable(false)
                .setTitle("Error")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent intent = new Intent(con, uploadReportActivity.class);
                        con.startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }else{
        Toast.makeText(con,"Uploaded successfully. ",Toast.LENGTH_SHORT).show();
    }

}



}

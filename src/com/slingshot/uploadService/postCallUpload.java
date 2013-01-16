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
    public boolean mkItem(Element body, String id,String desc,String serverText){
        fileLib fl=new fileLib(con);
        String repotPath= addExpensActivity.mainFolder+"/"+reportFile;


        try {

            if (body!=null){
                body=(Element)  body.getElementsByTagName("soap:Body").item(0).getFirstChild();
                Element   PostExpensesResponse=(Element)  body.getElementsByTagName("PostExpensesResponse").item(0).getFirstChild();
                String Code=  PostExpensesResponse.getElementsByTagName("Code").item(0).getFirstChild().getNodeValue().toString();
                if (Code.equals("Success")){
                    fl.AppendToFile(repotPath,"["+desc+"]"+" ... ok\n\n");
                    DatabaseHelper dh=new DatabaseHelper(con);
                    dh.removeExpenseId(id);
                    dh.close();
                    listActivity.removeImgFile(id);
                    return true;

                }else {
                    String message =PostExpensesResponse.getElementsByTagName("Message").item(0).getFirstChild().getNodeValue().toString();
                    fl.AppendToFile(repotPath,"["+desc+"]"+"... error\n");
                    fl.AppendToFile(repotPath,message+"\n\n");
                    isSuccess=false;

                }
            }else {
                fl.AppendToFile(repotPath,"["+desc+"]"+"... coonnection error\n");
                fl.AppendToFile(repotPath,"Server don't answer"+"\n\n");
                isSuccess=false;

            }


        }catch (Exception e){
            fl.AppendToFile(repotPath,"["+desc+"]"+"... not known error. \nServer log:\n");
            fl.AppendToFile(repotPath,serverText);
            isSuccess=false;
        }
        return false;
    }



public void postLoadingDialog(){
    if(!isSuccess){
        AlertDialog.Builder builder = new AlertDialog.Builder(con);

        builder.setMessage("Upload consists errors. Do you want to see report? ")
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

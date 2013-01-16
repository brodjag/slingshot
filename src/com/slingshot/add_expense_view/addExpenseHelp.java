package com.slingshot.add_expense_view;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.slingshot.R;
import com.slingshot.lib.DatabaseHelper;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: brodjag
 * Date: 15.01.13
 * Time: 12:58
 * To change this template use File | Settings | File Templates.
 */
public class addExpenseHelp {
    Activity con;
    String id_expense="-1";
public   addExpenseHelp(Activity c,String id_expense){
    this.id_expense=id_expense;
    con=c;
}
    //count of images for this expense
  public  int getFileCount(){
        File mainFolderF=  new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ addExpensActivity.mainFolder);
        if(!mainFolderF.exists()){mainFolderF.mkdir();}

        File imgs=  new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ addExpensActivity.mainFolder+"/imgs");
        if(!imgs.exists()){imgs.mkdir();}

        File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ addExpensActivity.mainFolder+"/imgs/"+get_id_expense());
        if(!root.exists()){root.mkdir();}
        File[] ImgsInIdFolder=root.listFiles();
        if (ImgsInIdFolder==null){return 0;}

        return ImgsInIdFolder.length;
    }

    public String get_id_expense() {
        if(id_expense.equals("0")){
            DatabaseHelper dh=new DatabaseHelper(con);
            int maxId=  dh.getMaxIdExpense(); dh.close();
            maxId++;
            Log.d("maxId", "" + maxId);
            return ""+maxId;
        }else {
            return id_expense;
        }
    }

    public String getFileName(){
        File mainFolderF=  new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ addExpensActivity.mainFolder);
        if(!mainFolderF.exists()){mainFolderF.mkdir();}
        File imgFolderF=  new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ addExpensActivity.mainFolder+"/imgs");
        if(!imgFolderF.exists()){imgFolderF.mkdir();}

        File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ addExpensActivity.mainFolder+"/imgs/"+get_id_expense());
        if(!root.exists()){root.mkdir();}
        File[] ImgsInIdFolder=root.listFiles();
        if (ImgsInIdFolder==null){return "1.jpg";}

        int max=1;
        for (int i=0; i<ImgsInIdFolder.length; i++ ){
            String fileName_i=ImgsInIdFolder[i].getName();
            Log.d("path1",fileName_i);
            Log.d("path12",fileName_i.split("\\u002E")[0]) ;
            int num_i=Integer.parseInt(fileName_i.split("\\u002E")[0]);
            if (num_i>max){max=num_i;}
        }
        max++;
        return ""+max+".jpg";
    }


    public boolean checkInput(){

        //description
        String description= ((TextView) con.findViewById(R.id.description)).getText().toString();
        if(description.equals("")){
            Toast.makeText(con,"Check description",Toast.LENGTH_SHORT).show();
            return false;
        }

        String amount= ((TextView) con.findViewById(R.id.amount)).getText().toString();
        if(amount.equals("")){
            Toast.makeText(con,"Check amount",Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
          long lAmount=  Long.parseLong(amount);
          if (lAmount==0){
              Toast.makeText(con,"Check amount",Toast.LENGTH_SHORT).show();
              return false;
          }

        }catch (Exception e){
            Toast.makeText(con,"Check amount",Toast.LENGTH_SHORT).show();
            return false;

        }



        return true;
    }


}

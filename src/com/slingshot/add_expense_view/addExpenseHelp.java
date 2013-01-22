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
    String id_expense="0";
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
       // if (ImgsInIdFolder==null){return 0;}

     // File tmpFolder= new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+addExpensActivity.mainFolder+"/imgs/"+  "temp");
    //  if (!tmpFolder.exists()){tmpFolder.mkdir();}
     // Log.d("getFileCount"," "+(ImgsInIdFolder.length+tmpFolder.listFiles().length));
     // if (true){return  (ImgsInIdFolder.length+);}
         //Log.d("getFileCount",)
      /*
      int mainFolderCount= ImgsInIdFolder.length;
      Log.d("getFileCount","mainFolderCount= " +mainFolderCount);
      int tmpFolderCount= tmpFolder.listFiles().length;
      Log.d("getFileCount","tmpFolderCount= " +tmpFolderCount);
      int res= mainFolderCount+tmpFolderCount;
      Log.d("getFileCount"," " +res);
      */
        return ImgsInIdFolder.length;
    }

    public String get_id_expense() {
        return id_expense;
      /*
        if(id_expense.equals("0")){
            DatabaseHelper dh=new DatabaseHelper(con);
            int maxId=  dh.getMaxIdExpense();
            dh.close();
            maxId++;
            Log.d("maxId", "" + maxId);
            return ""+maxId;
        }else {
            return id_expense;
        }
        */
    }

    public String getFileName(){
        File mainFolderF=  new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ addExpensActivity.mainFolder);
        if(!mainFolderF.exists()){mainFolderF.mkdir();}
        File imgFolderF=  new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ addExpensActivity.mainFolder+"/imgs");
        if(!imgFolderF.exists()){imgFolderF.mkdir();}

        File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ addExpensActivity.mainFolder+"/imgs/"+get_id_expense());
        if(!root.exists()){root.mkdir();}
        File[] ImgsInIdFolder=root.listFiles();
        //if (ImgsInIdFolder==null){return "1.jpg";}

        int max=0;
        for (int i=0; i<ImgsInIdFolder.length; i++ ){
            String fileName_i=ImgsInIdFolder[i].getName();
            Log.d("path1",fileName_i);
            Log.d("path12",fileName_i.split("\\u002E")[0]) ;
            int num_i=Integer.parseInt(fileName_i.split("\\u002E")[0]);
            if (num_i>max){max=num_i;}
        }

        //temp test too
        /*
        File rootTemp = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ addExpensActivity.mainFolder+"/imgs/"+get_id_expense());
        if(!rootTemp.exists()){root.mkdir();}
        File[] ImgsInIdFolderTemp=rootTemp.listFiles();

        for (int i=0; i<ImgsInIdFolderTemp.length; i++ ){
            String fileName_i=ImgsInIdFolderTemp[i].getName();
            Log.d("path1",fileName_i);
            Log.d("path12",fileName_i.split("\\u002E")[0]) ;
            int num_i=Integer.parseInt(fileName_i.split("\\u002E")[0]);
            if (num_i>max){max=num_i;}
        }
        */

        max++;
        return ""+max+".tmp";
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
          float lAmount=  Float.parseFloat(amount);
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


    public void cleanTemp(){
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+addExpensActivity.mainFolder+"/imgs/"+ get_id_expense());

      //  File tmpFolder= new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+addExpensActivity.mainFolder+"/imgs/"+  "temp");
        File[] files= file.listFiles();
        for(int i=0; i<files.length; i++){
           String name=""+files[i].getName();
            String end=name.split("\\u002E")[1];
           if(end.equals("tmp")) {
               files[i].delete(); //Toast.makeText(con,"rr",Toast.LENGTH_SHORT).show();
           };
        }
    }

    public void applyTemp(){
        String path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+addExpensActivity.mainFolder+"/imgs/"+ get_id_expense();
        File file = new File(path);

        //  File tmpFolder= new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+addExpensActivity.mainFolder+"/imgs/"+  "temp");
        File[] files= file.listFiles();
        for(int i=0; i<files.length; i++){
            String name=""+files[i].getName();
            String end=name.split("\\u002E")[1];
            if(end.equals("tmp")) {
                File newFile=new File(path+"/"+name.split("\\u002E")[0]+".jpg");
                files[i].renameTo(newFile);
            };
        }
    }


   public void applyFolder0(){
       File tmpFolder= new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+addExpensActivity.mainFolder+"/imgs/"+  "0");

       DatabaseHelper dh=new DatabaseHelper(con);
       int maxId=  dh.getMaxIdExpense();
       dh.close();
      // maxId++;

       String newPath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+addExpensActivity.mainFolder+"/imgs/"+ maxId;
       File newfile= new File(newPath);

       //new folder must be empty
       if(newfile.exists()){
           File[] fs=newfile.listFiles();
           for(int i=0; i<fs.length; i++){
               fs[i].delete();
           }
       }


       tmpFolder.renameTo(newfile);

   }


    public void moveToIdFolder(){
        File tmpFolder= new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+addExpensActivity.mainFolder+"/imgs/"+  "temp");
        if (!tmpFolder.exists()){return;}


        File[] files= tmpFolder.listFiles();
        for(int i=0; i<files.length; i++){

            File newfile= new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+addExpensActivity.mainFolder+"/imgs/"+ get_id_expense()+"/"+files[i].getName());
            files[i].renameTo(newfile);
        }
    }

    public void cleanZerroFolder0(){
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+addExpensActivity.mainFolder+"/imgs/0");

        if (!file.exists()){return;}
        //  File tmpFolder= new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+addExpensActivity.mainFolder+"/imgs/"+  "temp");
        File[] files= file.listFiles();
        for(int i=0; i<files.length; i++){

                files[i].delete(); //Toast.makeText(con,"rr",Toast.LENGTH_SHORT).show();

        }
    }

}

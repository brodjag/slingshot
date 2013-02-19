package com.slingshot.add_expense_view;

import android.database.Cursor;
import android.os.Environment;
import android.widget.TextView;
import com.slingshot.R;
import com.slingshot.lib.DatabaseHelper;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: brodjag
 * Date: 31.01.13
 * Time: 12:32
 * To change this template use File | Settings | File Templates.
 */
public class checkChanges4BackButton {
 addExpensActivity con;
    public checkChanges4BackButton(addExpensActivity c){
        con=c;
    }

public boolean isChanged(){
    if(con.id_expense.equals("0")){
        //if (con.isSpinerSelected>1){return true;}    //if new expanse and nothin

        //check desckription
        String descNew=((TextView) con.findViewById(R.id.description)).getText().toString();
        if(!descNew.equals("")){return true;}

        //check amount
        String amountNew=((TextView) con.findViewById(R.id.amount)).getText().toString();
        if(!amountNew.equals("")){return true;}

        return false;
    }

    //check tmp files
    File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ addExpensActivity.mainFolder+"/imgs/"+con.EHelper.get_id_expense());
    if(root.exists()){
    File[] ImgsInIdFolder=root.listFiles();
    for (int i=0; i<ImgsInIdFolder.length; i++ ){
        if(ImgsInIdFolder[i].getName().split("\\u002E")[1].equals("tmp")){return true;};
    }
    }

    //check removed folder
    File removed = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ addExpensActivity.mainFolder+"/imgs/removed");
    if(removed.exists()){
        File[] ImgsInTmpFolder=removed.listFiles();
       if (!(ImgsInTmpFolder.length==0)){return true;}
    }


    //check values with db
    if(!con.EHelper.get_id_expense().equals("0")) {
    DatabaseHelper dh=new DatabaseHelper(con);
    Cursor c=dh.getExpenseById(con.EHelper.get_id_expense());
    if(c.getCount()==0){return true;}

        //check code
        String code=c.getString(1);
        if (!code.equals(con.ExpenseCode)){return true;}

       //check Time   ??????
        String date=""+con.dateStart.getTimeInMillis();
      //  Log.d("dateq",""+con.dateStart.getTimeInMillis());
       // Log.d("dateq_db",c.getString(2));
        if (!c.getString(2).equals(date)){return true;}

        //check desckription
        String descNew=((TextView) con.findViewById(R.id.description)).getText().toString();
        if(!descNew.equals(c.getString(3))){return true;}

        //check amount
        String amountNew=((TextView) con.findViewById(R.id.amount)).getText().toString();
        if(!amountNew.equals(c.getString(4))){return true;}





        dh.close(); c.close();
    }else {
        //chek not empty ("") strings values amount and discaption
    }

 return false;
}

}

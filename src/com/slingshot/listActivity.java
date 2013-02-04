package com.slingshot;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.util.Log;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.slingshot.add_expense_view.addExpensActivity;
import com.slingshot.lib.DatabaseHelper;
import com.slingshot.lib.fileLib;
import com.slingshot.uploadService.upService;
import com.slingshot.uploadService.upload_request;

import java.io.File;
import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: brodjag
 * Date: 27.12.12
 * Time: 17:21
 * show expenses list
 */
public class listActivity extends Activity {
    Activity con;
    Intent service;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        con=this;
        setContentView(R.layout.list);
        getListItems();
        setAddButton();
        service = new Intent(con, upService.class);
    }

    void  setAddButton(){
        findViewById(R.id.add_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {     ((Vibrator) con.getSystemService(con.VIBRATOR_SERVICE)).vibrate(50); } catch (Exception e) {}
                if(!fileLib.isSDCardMounted()){Toast.makeText(con,"Cd card isn't connected", Toast.LENGTH_LONG).show(); return;}

               DatabaseHelper dh=new DatabaseHelper(con);
               // dh.newExpense("","",Calendar.getInstance().getTimeInMillis(),"","");
               // String lastId=""+dh.getMaxIdExpense();

                Intent intent=new Intent(con,addExpensActivity.class);
                  intent.putExtra("id","0");
                startActivity(intent);
                finish();
            }
        });
    }

    //add all expense items from DB  to list on view
    private void getListItems() {
        LinearLayout list=(LinearLayout) findViewById(R.id.list_expenses);
        DatabaseHelper dh=new DatabaseHelper(con);
         Cursor cursor=dh.getExpenseAll();

        if(cursor.getCount()>0){  findViewById(R.id.scrollist).setVisibility(View.VISIBLE);findViewById(R.id.message_no_items).setVisibility(View.GONE); }

        for (int i=0;i<cursor.getCount();i++){
            cursor.moveToPosition(i);
           final View v= con.getLayoutInflater().inflate(R.layout.expenses_list_item,null);

            final String id=cursor.getString(0);
           // Log.d("id_",cursor.getString(0)+"  "+cursor.getString(1));
            v.setTag(Integer.parseInt(id));

            final String epenseCode=cursor.getString(1);
            ((TextView) v.findViewById(R.id.expense_code)).setText(epenseCode);

            Calendar calendar=Calendar.getInstance();
            calendar.setTimeInMillis(cursor.getLong(2));
                    ((TextView) v.findViewById(R.id.list_item_date)).setText("" + (calendar.get(Calendar.MONTH) + 1) + "/" + (calendar.get(Calendar.DAY_OF_MONTH))+ "/"+ calendar.get(Calendar.YEAR) );

            //description
            final   String desc=cursor.getString(3);
           ((TextView) v.findViewById(R.id.list_description)).setText(desc);
            ((TextView) v.findViewById(R.id.list_item_amount)).setText("$"+cursor.getString(4));

           //edit item
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try { ((Vibrator) con.getSystemService(con.VIBRATOR_SERVICE)).vibrate(50);} catch (Exception e) {}
                    if(fileLib.isSDCardMounted()){}else {Toast.makeText(con,"Cd card isn't connected", Toast.LENGTH_LONG).show(); return;}

                    Intent intent=new Intent(con,addExpensActivity.class);
                    intent.putExtra("id",id);
                    startActivity(intent);
                    finish();
                   // Log.d("delete_edit","edit="+id);
                }
            });


            //long click
            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    /*
                    setNormaItemlSize();
                   LinearLayout w=(LinearLayout) v.findViewWithTag("body");

                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)  w.getLayoutParams();
                    params.height=200;
                    w.setLayoutParams(params);
                    */
                    return true;
                }
            });

            //remove item
            v.findViewById(R.id.remove_item).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {     ((Vibrator) con.getSystemService(con.VIBRATOR_SERVICE)).vibrate(50); } catch (Exception e) {}
                    if(!fileLib.isSDCardMounted()){Toast.makeText(con,"Cd card isn't connected", Toast.LENGTH_LONG).show(); return;}

                    final AlertDialog.Builder dlgAlert = new AlertDialog.Builder(con);
                    dlgAlert.setMessage("Remove "+epenseCode+" '" + desc + "'?");
                    //dlgAlert.setTitle("App Title");
                    dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            try {     ((Vibrator) con.getSystemService(con.VIBRATOR_SERVICE)).vibrate(50); } catch (Exception e) {}
                           DatabaseHelper dh=new DatabaseHelper(con);
                            dh.removeExpenseId(id);

                            Cursor c=dh.getExpenseAll();
                            if(c.getCount()==0){  findViewById(R.id.scrollist).setVisibility(View.GONE);findViewById(R.id.message_no_items).setVisibility(View.VISIBLE); }
                            c.close();
                            dh.close();
                            removeImgFile(id);
                            ((ViewManager) v.getParent()).removeView(v);
                        }
                    });
                    dlgAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    dlgAlert.setCancelable(true);
                    dlgAlert.create().show();

                 //   ((ViewManager) v.getParent()).removeView(v);
                }
            });
           list.addView(v);
        }
        cursor.close();
        dh.close();

    }

public void setNormaItemlSize(){

    LinearLayout list=(LinearLayout) findViewById(R.id.list_expenses);


    for(int i=0; i<list.getChildCount(); i++){

        LinearLayout w=(LinearLayout)  list.getChildAt(i).findViewWithTag("body");

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)  w.getLayoutParams();
        params.height= LinearLayout.LayoutParams.WRAP_CONTENT;
        w.setLayoutParams(params);

    }


}

    public static void removeImgFile(String id) {
        File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ addExpensActivity.mainFolder+"/imgs/"+id);
        if(!root.exists()){return;}

        File[]   ImgsInIdFolder=root.listFiles();
        if (ImgsInIdFolder==null){root.delete(); Log.d("delete","id="+id); return;}
        for (int i=0; i<ImgsInIdFolder.length; i++ ){
            ImgsInIdFolder[i].delete();
        }
       root.delete();
        Log.d("delete","id="+id);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent setting= new Intent(con,settingActivity.class);
        setting.putExtra("back",settingActivity.backToListExpense);

        switch (item.getItemId()) {
            case R.id.setting:
                con.startActivity(setting);
                con.finish();
                break;

            case R.id.upload:
               try {     ((Vibrator) con.getSystemService(con.VIBRATOR_SERVICE)).vibrate(50); } catch (Exception e) {}
                if(!fileLib.isSDCardMounted()){Toast.makeText(con,"Cd card isn't connected", Toast.LENGTH_LONG).show(); break;}

                final AlertDialog.Builder dlgAlert = new AlertDialog.Builder(con);
                dlgAlert.setMessage("Post expenses to server?");
                //dlgAlert.setTitle("App Title");
                dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {     ((Vibrator) con.getSystemService(con.VIBRATOR_SERVICE)).vibrate(50); } catch (Exception e) {}
                        new upload_request(con);
                    }
                });
                dlgAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
                break;

            case R.id.web:
                DatabaseHelper dh=new DatabaseHelper(con);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(dh.getSetting("url_pre")));
                startActivity(browserIntent);
                break;
        }
        return true;
    }


    int exit=2;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (exit>1){
                exit--;
                Toast.makeText(con,"To exit, press Back again",Toast.LENGTH_LONG).show();
                return true;
            }else {
            finish();
            //startActivity(new1 Intent(con,listActivity.class));
            }
        }
        return super.onKeyDown(keyCode, event);

    }
}
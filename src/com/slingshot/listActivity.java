package com.slingshot;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.util.Log;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.slingshot.lib.DatabaseHelper;
import com.slingshot.uploadService.upService;

import java.io.File;
import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: brodjag
 * Date: 27.12.12
 * Time: 17:21
 * To change this template use File | Settings | File Templates.
 */
public class listActivity extends Activity {
    Activity con;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        con=this;
        setContentView(R.layout.list);
        getListItems();
        setAddButton();
    }

    void  setAddButton(){
        findViewById(R.id.add_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try { ((Vibrator) con.getSystemService(con.VIBRATOR_SERVICE)).vibrate(50);} catch (Exception e) {} ;

               DatabaseHelper dh=new DatabaseHelper(con);
                dh.newExpense("","",Calendar.getInstance().getTimeInMillis(),"","");
                String lastId=""+dh.getMaxIdExpense();

                Intent intent=new Intent(con,addExpensActyvity.class);

                intent.putExtra("id",lastId);
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

        for (int i=0;i<cursor.getCount();i++){
            cursor.moveToPosition(i);
           final View v= con.getLayoutInflater().inflate(R.layout.expenses_list_item,null);

            final String id=cursor.getString(0);
            Log.d("id_",cursor.getString(0)+"  "+cursor.getString(1));
            ((TextView) v.findViewById(R.id.expense_code)).setText(cursor.getString(1));
            Calendar calendar=Calendar.getInstance();
            calendar.setTimeInMillis(cursor.getLong(2));
                    ((TextView) v.findViewById(R.id.list_item_date)).setText("" + calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + (calendar.get(Calendar.DAY_OF_MONTH)));

            //description
            final   String desc=cursor.getString(3);
           ((TextView) v.findViewById(R.id.list_description)).setText(desc);
            ((TextView) v.findViewById(R.id.list_item_amount)).setText("$"+cursor.getString(4));

           //edit item
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try { ((Vibrator) con.getSystemService(con.VIBRATOR_SERVICE)).vibrate(50);} catch (Exception e) {} ;
                    Intent intent=new Intent(con,addExpensActyvity.class);
                    intent.putExtra("id",id);
                    startActivity(intent);
                    finish();
                    Log.d("delete_edit","edit="+id);
                }
            });

            //remove item
            v.findViewById(R.id.remove_item).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {     ((Vibrator) con.getSystemService(con.VIBRATOR_SERVICE)).vibrate(50); } catch (Exception e) {}  ;
                    final AlertDialog.Builder dlgAlert = new AlertDialog.Builder(con);
                    dlgAlert.setMessage("Remove '" + desc + "'?");
                    //dlgAlert.setTitle("App Title");
                    dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            try {     ((Vibrator) con.getSystemService(con.VIBRATOR_SERVICE)).vibrate(50); } catch (Exception e) {}  ;
                           DatabaseHelper dh=new DatabaseHelper(con);
                            dh.removeExpenseId(id);
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

    private void removeImgFile(String id) {
        File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+addExpensActyvity.mainFolder+"/"+id);
        if(!root.exists()){return;}

        File[]   ImgsInIdFolder=root.listFiles();
        if (ImgsInIdFolder==null){return;}
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
               // new upload_request(con);
                Intent service = new Intent(con, upService.class);
                con.startService(service);
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
                Toast.makeText(con,"click back one more time to exit",Toast.LENGTH_LONG).show();
                return true;
            }else {
            finish();
            //startActivity(new Intent(con,listActivity.class));
            }
        }
        return super.onKeyDown(keyCode, event);

    }
}
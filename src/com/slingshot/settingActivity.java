package com.slingshot;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import com.slingshot.lib.DatabaseHelper;


/**
 * Created with IntelliJ IDEA.
 * User: brodjag
 * Date: 27.09.12
 * Time: 11:21
 * To change this template use File | Settings | File Templates.
 */
public class settingActivity extends Activity {
    private Activity con;
    private boolean restart=false;


    private int back=0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        con=this;
        setContentView(R.layout.settingsv);

        back=getIntent().getIntExtra("back",0);
        setValues();

        findViewById(R.id.setting_server_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {  ((Vibrator) con.getSystemService(con.VIBRATOR_SERVICE)).vibrate(50); } catch (Exception e) { }
                serverDialog();
            }
        });

        findViewById(R.id.setting_authofication).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {  ((Vibrator) con.getSystemService(con.VIBRATOR_SERVICE)).vibrate(50); } catch (Exception e) { }
                authDialog();
            }
        });


    }


    private void serverDialog(){
        // AlertDialog.Builder builder = new AlertDialog.Builder(con);
        final Dialog dialog = new Dialog(con,R.style.FullHeightDialog);
        dialog.setContentView(R.layout.setting_server_dialog);
        dialog.findViewById(R.id.dialog_enter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {  ((Vibrator) con.getSystemService(con.VIBRATOR_SERVICE)).vibrate(50); } catch (Exception e) { }
                DatabaseHelper dh=new DatabaseHelper(con);
                dh.setSetting("url_pre", ((TextView) dialog.findViewById(R.id.url_pre)).getText().toString());
                dh.setSetting("url_area", ((TextView) dialog.findViewById(R.id.url_area)).getText().toString());
                restart=true;
                //dh.removeAllScaned();
                setValues();
                dialog.dismiss();
            }
        });

        DatabaseHelper dh=new DatabaseHelper(con);
        ((TextView) dialog.findViewById(R.id.url_pre)).setText(dh.getSetting("url_pre"));
        ((TextView) dialog.findViewById(R.id.url_area)).setText(dh.getSetting("url_area"));
        dialog.findViewById(R.id.dialog_server_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {  ((Vibrator) con.getSystemService(con.VIBRATOR_SERVICE)).vibrate(50); } catch (Exception e) { };
                // dialog.cancel();
                dialog.dismiss();
            }
        });


      dialog.show();
    }

    private void authDialog(){
        // AlertDialog.Builder builder = new AlertDialog.Builder(con);
        final Dialog dialog = new Dialog(con,R.style.FullHeightDialog);
        dialog.setContentView(R.layout.settings_auth_dialog);
        dialog.findViewById(R.id.dialog_enter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {  ((Vibrator) con.getSystemService(con.VIBRATOR_SERVICE)).vibrate(50); } catch (Exception e) { }
                DatabaseHelper dh=new DatabaseHelper(con);

                dh.setSetting("login", ((TextView) dialog.findViewById(R.id.d_login)).getText().toString());
                dh.setSetting("password", ((TextView) dialog.findViewById(R.id.d_password)).getText().toString());
                dh.setSetting("Project", ((TextView) dialog.findViewById(R.id.d_project)).getText().toString());
                restart=true;
                setValues();
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.dialog_cancel_auth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {  ((Vibrator) con.getSystemService(con.VIBRATOR_SERVICE)).vibrate(50); } catch (Exception e) { }
                dialog.dismiss();
            }
        });

        DatabaseHelper dh=new DatabaseHelper(con);
        ((TextView) dialog.findViewById(R.id.d_login)).setText(dh.getSetting("login"));
        ((TextView) dialog.findViewById(R.id.d_password)).setText(dh.getSetting("password"));
        ((TextView) dialog.findViewById(R.id.d_project)).setText(dh.getSetting("Project"));
        dialog.show();
    }

    void setValues(){
        DatabaseHelper dh=new DatabaseHelper(con);
        ((TextView) findViewById(R.id.url_pre)).setText(dh.getSetting("url_pre"));
        ((TextView) findViewById(R.id.url_area)).setText(dh.getSetting("url_area"));
        ((TextView) findViewById(R.id.login)).setText(dh.getSetting("login"));
        ((TextView) findViewById(R.id.password)).setText(dh.getSetting("password"));
        ((TextView) findViewById(R.id.project)).setText(dh.getSetting("Project"));

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (restart){
                startActivity(new Intent(this,SplashScreenActivity.class));
                finish();
            }else {myBackFunction();finish();}
        }
        return super.onKeyDown(keyCode, event);
    };

//переопределить при инициализации!!!
public void myBackFunction(){


    if (back==backToListExpense){startActivity(new Intent(con, listActivity.class));}
    if (back==backToEditExpense){
        Intent intent=new Intent(con,addExpensActyvity.class);
        intent.putExtra("id",getIntent().getStringExtra("id_expense"));
        startActivity(intent);
    }
    if (back==backToSplash){startActivity(new Intent(con, SplashScreenActivity.class));}
   // if (back==backToSplash){startActivity(new Intent(con, SplashScreenActivity.class));}
    finish();

   // Toast.makeText(con,"переход на нужный экран",Toast.LENGTH_SHORT).show();
}

static int backToListExpense=1;
static int backToEditExpense=2;
static int backToSplash=3;
}



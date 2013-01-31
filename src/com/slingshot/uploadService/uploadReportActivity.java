package com.slingshot.uploadService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import com.slingshot.R;
import com.slingshot.add_expense_view.addExpensActivity;
import com.slingshot.lib.fileLib;
import com.slingshot.listActivity;

/**
Report for last upload expenses
 */
public class uploadReportActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_report);
        String repotPath=  addExpensActivity.mainFolder+"/"+postCallUpload.reportFile;


        Log.d("repotPath",repotPath);
        fileLib fl=new fileLib(this);

       ((EditText)findViewById(R.id.uploadReport)).setText(fl.readAppendetFile(repotPath));
        ((EditText)findViewById(R.id.uploadReport)).setKeyListener(null);

        //NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //mNotificationManager.cancel(0);

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
         finish();
         startActivity(new Intent(this, listActivity.class));
        }
        return super.onKeyDown(keyCode, event);

    }
}
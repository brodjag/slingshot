package com.slingshot.uploadService;


import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

/**
 * Created with IntelliJ IDEA.
 * User: brodjag
 * Date: 10.01.13
 * Time: 11:43
 * service to upload anything without interface ..
 */
public class upService extends Service {
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service started",Toast.LENGTH_LONG).show();

        new upload_request(this);

        return Service.START_NOT_STICKY;
    }

    private void notif3() {
        NotificationManager   mNotificationManager =    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// Sets an ID for the notification, so it can be updated
        int notifyID = 1;
      // loadNotification ln=new loadNotification(this,100);
     //   ln.setPosition(78);

    }




}

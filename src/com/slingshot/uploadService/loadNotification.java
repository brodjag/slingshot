package com.slingshot.uploadService;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import com.slingshot.R;
import com.slingshot.listActivity;

/**
 * Created with IntelliJ IDEA.
 * User: brodjag
 * Date: 10.01.13
 * Time: 18:21
 * To change this template use File | Settings | File Templates.
 */
public class loadNotification {
    Context con;
    NotificationManager     mNotifyManager;
    Notification notification;
    int maxPosition;

    public class startClass extends listActivity{};

    public loadNotification(Context c, int maxPos){
        con=c;  maxPosition=maxPos;

        createNofification();

    }

    void createNofification(){
        mNotifyManager =    (NotificationManager) con.getSystemService(Context.NOTIFICATION_SERVICE);

        RemoteViews contentView=(new RemoteViews(con.getPackageName(),R.layout.notification_load));




        //  mBuilder.setContentIntent(notifyIntent);

        //   mNotifyManager.notify(0,mBuilder.build());
        notification = new Notification(R.drawable.ic_launcher, "Slingshot", System.currentTimeMillis());
        notification.contentView=contentView;
        notification.contentIntent= PendingIntent.getActivity( con, 0,  new Intent(con,startClass.class),PendingIntent.FLAG_UPDATE_CURRENT);;

        contentView.setProgressBar(R.id.progress_bar,maxPosition,0,true);
        mNotifyManager.notify(0,notification);
    }

    public void setPosition(int pos){
        notification.contentView.setProgressBar(R.id.progress_bar,maxPosition,pos,false);
        mNotifyManager.notify(0,notification);
    }

    public void setUploaded(){
        NotificationCompat.Builder  mBuilder = new NotificationCompat.Builder(con);
        mBuilder.setContentTitle("Slingshot downloaded")
                .setContentText("uloaded 5 items")
                .setSmallIcon(R.drawable.ic_launcher);
        mBuilder.setContentIntent(PendingIntent.getActivity( con, 0,  new Intent(con,startClass.class),PendingIntent.FLAG_UPDATE_CURRENT));
       //notification= new Notification(R.drawable.ic_launcher, "Slingshot: Uploaded", System.currentTimeMillis());
      // notification.contentIntent= PendingIntent.getActivity( con, 0,  new Intent(con,MyActivity.class),PendingIntent.FLAG_UPDATE_CURRENT);
       mNotifyManager.notify(0,mBuilder.build());
    }


}

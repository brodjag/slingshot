package com.slingshot.imageView;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;
import com.slingshot.lib.fileLib;

/**
 * Created with IntelliJ IDEA.
 * User: brodjag
 * Date: 18.01.13
 * Time: 17:16
 * To change this template use File | Settings | File Templates.
 */
public class ImgStarter {
    Activity con;
public ImgStarter(Activity c){
        con=c;
    }

public void showView(String get_id_expense){

    if(!fileLib.isSDCardMounted()){    Toast.makeText(con, "Cd card isn't connected", Toast.LENGTH_LONG).show(); return; }

    Intent intent=new Intent(con,img_zoom_Activity.class);
    intent.putExtra("imageFileId",(0));
    intent.putExtra("id_expense", get_id_expense);
    con.startActivity(intent);
}
}

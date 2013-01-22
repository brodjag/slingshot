package com.slingshot.imageView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.slingshot.R;
import com.slingshot.add_expense_view.addExpensActivity;
import com.slingshot.add_expense_view.addExpenseHelp;
import com.slingshot.lib.fileLib;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: brodjag
 * Date: 04.01.13
 * Time: 15:27
 * To change this template use File | Settings | File Templates.
 */
public class img_zoom_Activity extends Activity {
    String fileName;
     String id_expense="2";
    private static int TAKE_PICTURE = 1;
     int imageFileId=0;
     float scale=1;
    String mCurrentPhotoPath;
    Activity con;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);     con=this;
        setContentView(R.layout.img_zoom);
        try{
            id_expense=getIntent().getStringExtra("id_expense") ;

            imageFileId=getIntent().getIntExtra("imageFileId",0) ;
        }catch (Exception e){}
      //  new1 img_zoom_helper(this);
        setImgArray();   //init File[] ImgsInIdFolder
        setArrows();

        //remove item
        findViewById(R.id.remove_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {((Vibrator) con.getSystemService(con.VIBRATOR_SERVICE)).vibrate(50); } catch (Exception e) {}

                final AlertDialog.Builder dlgAlert = new AlertDialog.Builder(con);
                dlgAlert.setMessage("Remove this image'?");
                //dlgAlert.setTitle("App Title");
                dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {((Vibrator) con.getSystemService(con.VIBRATOR_SERVICE)).vibrate(50); } catch (Exception e) {}
                        ImgsInIdFolder[imageFileId].delete();

                        File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ addExpensActivity.mainFolder+"/imgs/"+id_expense);
                        if(!root.exists()){root.mkdir();}
                        ImgsInIdFolder=root.listFiles();

                        if(ImgsInIdFolder.length==0){ finish();   return;}


                        Intent intent=new Intent(con,img_zoom_Activity.class);
                        intent.putExtra("id_expense", id_expense);
                        if( imageFileId!=0){intent.putExtra("imageFileId",(imageFileId-1));
                        }  else { intent.putExtra("imageFileId",(imageFileId));     }
                        con.startActivity(intent);
                        con.finish();
                        Toast.makeText(con,"Image was removed",Toast.LENGTH_SHORT).show();


                    }
                });
                dlgAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {((Vibrator) con.getSystemService(con.VIBRATOR_SERVICE)).vibrate(50); } catch (Exception e) {}
                        dialogInterface.dismiss();
                    }
                });
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();














            }
        });


        //new image

        findViewById(R.id.new_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {     ((Vibrator) con.getSystemService(con.VIBRATOR_SERVICE)).vibrate(50); } catch (Exception e) {}
                if(!fileLib.isSDCardMounted()){Toast.makeText(con,"Cd card isn't connected", Toast.LENGTH_LONG).show(); return;}

                addExpenseHelp EHelper= new addExpenseHelp(con,id_expense);
                fileName=EHelper.getFileName();

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+addExpensActivity.mainFolder+"/imgs/"+ EHelper.get_id_expense()+"/"+fileName);

                mCurrentPhotoPath=file.getAbsolutePath();
                Uri outputFileUri = Uri.fromFile(file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                startActivityForResult(intent, TAKE_PICTURE);
            }
        });
    }

void setArrows(){
    if(ImgsInIdFolder==null){   Toast.makeText(this, "no imgs for this expense", Toast.LENGTH_LONG).show(); } else{
        try{  setImg();}catch (Exception e){}
        ((TextView) findViewById(R.id.image_title)).setText("View Receipt\n(page "+(imageFileId+1)+" of "+ImgsInIdFolder.length+")");

        //arrow right
        findViewById(R.id.next_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getBaseContext(),img_zoom_Activity.class);
                intent.putExtra("imageFileId",(imageFileId+1));
                intent.putExtra("id_expense",id_expense);
                startActivity(intent);
                finish();
            }
        });

        //arrow left
        findViewById(R.id.prev_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getBaseContext(),img_zoom_Activity.class);
                intent.putExtra("imageFileId",(imageFileId-1));
                intent.putExtra("id_expense",id_expense);
                startActivity(intent);
                finish();
            }
        });


    }
    //arrows
    if (imageFileId==0){ findViewById(R.id.prev_img).setVisibility(View.INVISIBLE);}
    if ((imageFileId+1)==ImgsInIdFolder.length){ findViewById(R.id.next_img).setVisibility(View.INVISIBLE);}

}


void setImg(){

    //Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+addExpensActyvity.mainFolder+"/"+id_expense+"/"
    String path=""+Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ addExpensActivity.mainFolder+"/imgs/"+id_expense+"/"+ ImgsInIdFolder[imageFileId].getName();
    Log.d("path",path) ;
    Bitmap bitmap = BitmapFactory.decodeFile(path);

    ImageView imageView=(ImageView)  findViewById(R.id.image_view);

    Matrix matrix=new Matrix();

    if(bitmap.getWidth()>bitmap.getHeight()){
    matrix.setRotate(90,bitmap.getHeight()/2,bitmap.getWidth()/2);
    }

    Bitmap b2=Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
    (imageView).setImageBitmap(b2);
    scale=(float)dpToPx(320)/ b2.getWidth();
    matrix.setScale(scale, scale);

    imageView.setImageMatrix(matrix);

};


 File[] ImgsInIdFolder=null;
void     setImgArray(){
    File mainFolderF=  new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ addExpensActivity.mainFolder);
    if(!mainFolderF.exists()){mainFolderF.mkdir();}

    File imgs=  new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ addExpensActivity.mainFolder+"/imgs");
    if(!imgs.exists()){imgs.mkdir();}

    File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ addExpensActivity.mainFolder+"/imgs/"+id_expense);
    if(!root.exists()){root.mkdir();}
   ImgsInIdFolder=root.listFiles();
    if (ImgsInIdFolder==null){return;}

    /*
    int max=1;
    for (int i=0; i<ImgsInIdFolder.length; i++ ){
        String fileName_i=ImgsInIdFolder[i].getName();
        Log.d("path1", fileName_i);
        Log.d("path12",fileName_i.split("\\u002E")[0]) ;
        int num_i=Integer.parseInt(fileName_i.split("\\u002E")[0]);
        if (num_i>max){max=num_i;}
    }
    */
}

    private int dpToPx(int dp)
    {
        float density = getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }

    @Override
 public  void onDestroy(){


        ImageView imageView=(ImageView)  findViewById(R.id.image_view);
        ((ViewManager) imageView.getParent()).removeView(imageView);
        System.gc();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        //request from camera
        if(requestCode==1) {
            if(resultCode==-1) {

                //Toast.makeText(this, "Picture is appended", Toast.LENGTH_LONG).show();
              //  fileName= EHelper.getFileName();
                //((Button) findViewById(R.id.wach_img)).setText("view images ("+EHelper.getFileCount()+")");
                Intent intent=new Intent(con,img_zoom_Activity.class);
                intent.putExtra("id_expense", id_expense);
                startActivity(intent);
                finish();


            }else{
                Toast.makeText(this, "Picture is not taken", Toast.LENGTH_LONG).show();
            }
        }
    }
}
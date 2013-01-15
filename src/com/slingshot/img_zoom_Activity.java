package com.slingshot;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: brodjag
 * Date: 04.01.13
 * Time: 15:27
 * To change this template use File | Settings | File Templates.
 */
public class img_zoom_Activity extends Activity {

     String id_expense="2";
     int imageFileId=0;
     float scale=1;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.img_zoom);
        try{
            id_expense=getIntent().getStringExtra("id_expense") ;
            imageFileId=getIntent().getIntExtra("imageFileId",0) ;
        }catch (Exception e){}
      //  new img_zoom_helper(this);
        setImgArray();
        if(ImgsInIdFolder==null){   Toast.makeText(this,"no imgs for this expense",Toast.LENGTH_LONG).show(); } else{
          try{  setImg();}catch (Exception e){}
            ((TextView) findViewById(R.id.image_title)).setText("image #"+(imageFileId+1)+" from "+ImgsInIdFolder.length);

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
        if (imageFileId==0){ findViewById(R.id.prev_img).setVisibility(View.INVISIBLE);}
        if ((imageFileId+1)==ImgsInIdFolder.length){ findViewById(R.id.next_img).setVisibility(View.INVISIBLE);}

    }
void setImg(){

    //Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+addExpensActyvity.mainFolder+"/"+id_expense+"/"
    String path=""+Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+addExpensActyvity.mainFolder+"/imgs/"+id_expense+"/"+ ImgsInIdFolder[imageFileId].getName();
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
    File mainFolderF=  new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+addExpensActyvity.mainFolder);
    if(!mainFolderF.exists()){mainFolderF.mkdir();}

    File imgs=  new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+addExpensActyvity.mainFolder+"/imgs");
    if(!imgs.exists()){imgs.mkdir();}

    File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+addExpensActyvity.mainFolder+"/imgs/"+id_expense);
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
}
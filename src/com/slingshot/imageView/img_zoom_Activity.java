package com.slingshot.imageView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
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
 *  create image viewer activity
 */
public class img_zoom_Activity extends Activity {
    String fileName;
     String id_expense="2";
    private static int TAKE_PICTURE = 1;
     int imageFileId=0;
     float scale=1;
    String mCurrentPhotoPath;
    Activity con;
    int bitmapSmallWidth=1200;
    boolean allowClicked =false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);     con=this;
        setContentView(R.layout.img_zoom);

        java.lang.Runtime.getRuntime().gc();

        try{
            id_expense=getIntent().getStringExtra("id_expense") ;

            imageFileId=getIntent().getIntExtra("imageFileId",0) ;
        }catch (Exception e){}

        System.gc();

        setImgArray();   //init File[] ImgsInIdFolder
        setArrows();
         bottomIconSetClick();
         setImg();

    }

    //set onclickAction for remove and newPage buttons.
    private void bottomIconSetClick() {
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

                        //if *.tmp so delete else move @ folder removed
                        if(ImgsInIdFolder[imageFileId].getName().split("\\u002E")[1].equals("tmp")){
                            ImgsInIdFolder[imageFileId].delete();
                        } else {
                            File removedFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ addExpensActivity.mainFolder+"/imgs/removed");
                            if (!removedFolder.exists()){removedFolder.mkdir();}
                            File removedFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ addExpensActivity.mainFolder+"/imgs/removed/"+ImgsInIdFolder[imageFileId].getName());
                            ImgsInIdFolder[imageFileId].renameTo(removedFile);

                        }


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
        ((TextView) findViewById(R.id.image_title)).setText("View Receipt\n(page "+(imageFileId+1)+" of "+ImgsInIdFolder.length+")");

        //arrow right
        findViewById(R.id.next_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!allowClicked){return;}; allowClicked =false;
                if(!fileLib.isSDCardMounted()){    Toast.makeText(con, "Cd card isn't connected", Toast.LENGTH_LONG).show(); return; }
                Intent intent=new Intent(getBaseContext(),img_zoom_Activity.class);
                intent.putExtra("imageFileId",(imageFileId+1));
                intent.putExtra("id_expense",id_expense);
                finish();
                startActivity(intent);

            }
        });

        //arrow left
        findViewById(R.id.prev_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!allowClicked){return;}; allowClicked =false;
                if(!fileLib.isSDCardMounted()){    Toast.makeText(con, "Cd card isn't connected", Toast.LENGTH_LONG).show(); return; }
                Intent intent=new Intent(getBaseContext(),img_zoom_Activity.class);
                intent.putExtra("imageFileId",(imageFileId-1));
                intent.putExtra("id_expense",id_expense);
                finish();
                startActivity(intent);

            }
        });


    }
    //arrows
    if (imageFileId==0){ findViewById(R.id.prev_img).setVisibility(View.INVISIBLE);}
    if ((imageFileId+1)==ImgsInIdFolder.length){ findViewById(R.id.next_img).setVisibility(View.INVISIBLE);}

}


void setImg(){
    String path=""+Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ addExpensActivity.mainFolder+"/imgs/"+id_expense+"/"+ ImgsInIdFolder[imageFileId].getName();
   new bitmapTask().execute(path);
};


    protected class bitmapTask extends AsyncTask<String,Void,Bitmap> {
        String path;

        @Override
        protected Bitmap doInBackground(String... par) {
            path=par[0];
            //get only size
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bitmap4Size = BitmapFactory.decodeFile(path,options);
            int imageHeight = options.outHeight;
            int imageWidth = options.outWidth;
           // Log.d("bitmap4Size.getWidth()", "" + imageWidth);


            BitmapFactory.Options options1 = new BitmapFactory.Options();
            //rotate angle
            //float angle=0; //float scale=1;
            if(imageWidth>imageHeight){
               // angle=90;
                options1.inSampleSize=Math.round( imageHeight/bitmapSmallWidth);

            }else {
                options1.inSampleSize=Math.round( imageWidth/bitmapSmallWidth);
            }

            options1.inJustDecodeBounds=false;

            matrix=new Matrix();
            Bitmap b= BitmapFactory.decodeFile(path,options1);
            this.optionsG=options1;
            /*
            if(options1.outWidth<options1.outHeight){
                scale=(float)dpToPx(320)/ options1.outWidth;
            }else {
                scale=(float)dpToPx(320)/ options1.outHeight;
                }
            */

            //matrix.postRotate(angle,scale*options1.outHeight/2,scale*options1.outWidth/2);
            return b;
        }

        float scale;
        Matrix matrix;
        BitmapFactory.Options optionsG;

        @Override
        protected void onPostExecute(Bitmap b){
            ImageView imageView=(ImageView)  con.findViewById(R.id.image_view);



             Log.d("imageView.getWidth()/ b.getWidth();", ""+(float) imageView.getWidth()/optionsG.outWidth) ;
            float scaleX= (float) ((View) imageView.getParent()).getWidth()/optionsG.outWidth;
            float scaleY= (float) ((View) imageView.getParent()).getHeight()/optionsG.outHeight;
           // if(scaleX>scaleY){scale=scaleX;}else {scale = scaleY;}
            if(scaleX<scaleY){scale=scaleX;}else {scale = scaleY;}
            //scale= (float)dpToPx(320)/b.getWidth();
            (imageView).setImageBitmap(b);
            Log.d("scale=",""+scale);
            matrix.setScale(scale, scale);

            imageView.setScaleType(ImageView.ScaleType.MATRIX);
            imageView.setImageMatrix(matrix);
            new MyZoom(con,scale,matrix);
            allowClicked=true;
        }
    }




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
    ImgsInIdFolder=new orderFileArray(ImgsInIdFolder).makeOrder();
}



private int dpToPx(int dp){ float density = getApplicationContext().getResources().getDisplayMetrics().density;   return Math.round((float)dp * density); }

    @Override
 public  void onDestroy(){
        ImageView imageView=(ImageView)  findViewById(R.id.image_view);
        //Bitmap myBitmap1 = BitmapFactory.decodeResource(getResources(),R.drawable.n1);
          //  imageView.setImageBitmap(myBitmap1);
                ((ViewManager) imageView.getParent()).removeView(imageView);  imageView=null;
        System.gc();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        //request from camera
        if(requestCode==1) {
            if(resultCode==-1) {
                Intent intent=new Intent(con,img_zoom_Activity.class);
                intent.putExtra("id_expense", id_expense);
                intent.putExtra("imageFileId",ImgsInIdFolder.length);
                startActivity(intent);
                finish();
            }else{
                Toast.makeText(this, "Picture is not taken", Toast.LENGTH_LONG).show();
            }
        }
    }



}
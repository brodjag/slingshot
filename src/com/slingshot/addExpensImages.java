package com.slingshot;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created with IntelliJ IDEA.
 * User: brodjag
 * Date: 26.12.12
 * Time: 11:59
 * To change this template use File | Settings | File Templates.
 */
public class addExpensImages {

    Activity con;
    public addExpensImages(Activity a){
        con=a;

    }


    /**
     * set image size to fit
     *
     */
    public void scaleImage(ImageView view, final String urlToImg)
    {
         view.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {               //  "content://media/external/images/media/16"
                // Uri uri =  Uri.fromFile(new File(urlToImg));
               /*
                 Intent i = new Intent();
                 i.setAction(Intent.ACTION_VIEW);
                 i.setDataAndType(Uri.fromFile(new File(urlToImg)), "image/*");
                 con.startActivity(i);
               */
                 //con.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("content://"+urlToImg)));
             }
         });
      ///  Bitmap bitmap = ((BitmapDrawable)drawing).getBitmap();
        Bitmap bitmap = BitmapFactory.decodeFile(urlToImg);
        if (bitmap==null){
            Toast.makeText(con,"no img",Toast.LENGTH_SHORT).show(); return;}
        // Get current dimensions AND the desired bounding box
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int bounding = dpToPx(320);
        Log.i("Test", "original width = " + Integer.toString(width));
        Log.i("Test", "original height = " + Integer.toString(height));
        Log.i("Test", "bounding = " + Integer.toString(bounding));

        // Determine how much to scale: the dimension requiring less scaling is
        // closer to the its side. This way the image always stays inside your
        // bounding box AND either x/y axis touches it.
       // float xScale = ((float) bounding) / width;
      //  float yScale = ((float) bounding) / height;
         //float scale = (xScale <= yScale) ? xScale : yScale;
      //  Log.i("Test", "xScale = " + Float.toString(xScale));
       // Log.i("Test", "yScale = " + Float.toString(yScale));
        //  Log.i("Test", "scale = " + Float.toString(scale));

        // Create a matrix for the scaling and add the scaling data
        Matrix matrix = new Matrix();
        float scale;
        if (width>height){
            matrix.postRotate(90);
             scale =  ((float) bounding) / height;
        }   else { scale =  ((float) bounding) / width;}
        matrix.postScale(scale, scale);

        // Create a new bitmap and convert it to a format understood by the ImageView
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        width = scaledBitmap.getWidth(); // re-use
        height = scaledBitmap.getHeight(); // re-use
        //  BitmapDrawable result = new BitmapDrawable(scaledBitmap);
        Log.i("Test", "scaled width = " + Integer.toString(width));
        Log.i("Test", "scaled height = " + Integer.toString(height));

        // Apply the scaled bitmap
        //   view.setImageDrawable(result);
        view.setImageBitmap(scaledBitmap);

        // Now change ImageView's dimensions to match the scaled image
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
       // LinearLayout.LayoutParams params =new LinearLayout.LayoutParams(width,height);
 //       params.width = width;
        params.height = height;
        view.setLayoutParams(params);

        Log.i("Test", "done");
    }

    private int dpToPx(int dp)
    {
        float density = con.getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }
}

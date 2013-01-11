package com.slingshot;

import android.app.Activity;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created with IntelliJ IDEA.
 * User: brodjag
 * Date: 04.01.13
 * Time: 20:26
 * To change this template use File | Settings | File Templates.
 */
public class img_zoom_helper {
    Activity con;


    // We can be in one of these 3 states
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;
    String TAG="zoomImg";
    float oldDist=0;

    PointF start=new PointF(0,0);
    PointF mid=new PointF(0,0);
    //  float   start_y=0;

    public img_zoom_helper(Activity c, final float scale){
        con=c;

       con.findViewById(R.id.image_view).setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View v, MotionEvent event) {
               // These matrices will be used to move and zoom image
               Matrix matrix = new Matrix();
               Matrix savedMatrix = new Matrix();


               ImageView view = (ImageView) v;


               // Handle touch events here...
               switch (event.getAction() & MotionEvent.ACTION_MASK) {


                   case MotionEvent.ACTION_DOWN:
                       savedMatrix.set(matrix);
                       start.set(event.getX(), event.getY());
                       Log.d(TAG, "mode=DRAG");
                       mode = DRAG;
                       break;
                   case MotionEvent.ACTION_UP:
                   case MotionEvent.ACTION_POINTER_UP:
                       mode = NONE;
                       Log.d(TAG, "mode=NONE");
                       break;
                   case MotionEvent.ACTION_MOVE:
                       if (mode == DRAG) {
                           matrix.set(savedMatrix);
                           matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
                           matrix.postScale(scale,scale);

                       } else if (mode == ZOOM) {
                           float newDist = spacing(event);
                           Log.d(TAG, "newDist=" + newDist);
                           if (newDist > 10f) {
                               matrix.set(savedMatrix);
                               float scale = newDist / oldDist;
                               matrix.postScale(scale, scale, mid.x, mid.y);

                           }

                       }

                       break;


                   case MotionEvent.ACTION_POINTER_DOWN:

                       oldDist = spacing(event);
                       Log.d(TAG, "oldDist=" + oldDist);
                       if (oldDist > 10f) {
                           savedMatrix.set(matrix);
                           midPoint(mid, event);
                           mode = ZOOM;
                           Log.d(TAG, "mode=ZOOM");
                       }
                       break;
               }


               // Perform the transformation
             //  matrix.setScale(scale, scale);

               view.setImageMatrix(matrix);
               return true; // indicate event was handled
           }


       });
    }



    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
}

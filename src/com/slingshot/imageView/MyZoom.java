package com.slingshot.imageView;

import android.app.Activity;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import com.slingshot.R;

/**
 * Created with IntelliJ IDEA.
 * User: brodjag
 * Date: 22.01.13
 * Time: 20:32
 * To change this template use File | Settings | File Templates.
 */
public class MyZoom {

Activity con;
PointF start=new PointF(0,0);
PointF delta=new PointF(0,0);

PointF mpointOld=new PointF(0,0);

    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;
float oldDist;
float scale=1;
float minScale=1;

public MyZoom(Activity c, float scl){
    con=c;    minScale=scl;
    scale=scl;




    con.findViewById(R.id.image_view).setOnTouchListener(new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            Matrix matrix = new Matrix();
            switch (event.getAction() & MotionEvent.ACTION_MASK) {

                case MotionEvent.ACTION_DOWN: start.set(event.getX(), event.getY());  mode = DRAG; break;

                case MotionEvent.ACTION_POINTER_UP:

                    if(mode==ZOOM){
                        PointF mpointNew2=midPoint(event);
                       // delta.x= (mpointNew2.x-mpointOld.x)+delta.x+(event.getX() - start.x)/scale ;
                      //  delta.y= (mpointNew2.y-mpointOld.y)+delta.y+(event.getY() - start.y)/scale ;

                        float newDist2 = spacing(event);
                           //(float) Math.sqrt(



                        delta.x=(delta.x+(mpointNew2.x-mpointOld.x)/(scale));     //+event.getX() - start.x
                        delta.y=(delta.y+(mpointNew2.y-mpointOld.y)/(scale));     //+event.getY() - start.y
                        scale =scale* newDist2 / oldDist;
                        if(scale<minScale){scale=minScale;}

                    }

                    mode = NONE;
                    break;


                case MotionEvent.ACTION_MOVE:


                    if (mode == DRAG) {

                        getMaxDeltaWidth();
                    matrix.postTranslate((delta.x+(event.getX() - start.x)/scale), delta.y+ (event.getY() - start.y)/scale);
                        Matrix scaleMatrix=new Matrix(matrix);
                        scaleMatrix.postScale( scale, scale);
                        ((ImageView) v).setImageMatrix(scaleMatrix);



                    }else if (mode == ZOOM) {
                        float newDist1 = spacing(event);
                        float scale1 =newDist1 / oldDist;

                        PointF mpointNew=midPoint(event);
                       // delta.x=delta.x+(mpointNew.x-mpointOld.x);
                      //  delta.y=delta.y+(mpointNew.y-mpointOld.y);

                        matrix.postTranslate((delta.x+(mpointNew.x-mpointOld.x)*(scale)),delta.y+(mpointNew.y-mpointOld.y)*(scale));          //+event.getX() - start.x



                        Matrix scaleMatrix=new Matrix(matrix);
                        if(scale1*scale<minScale){scaleMatrix.postScale( minScale, minScale);}else{scaleMatrix.postScale( scale1*scale, scale1* scale);}
                        ((ImageView) v).setImageMatrix(scaleMatrix);

                    }



                break;

                case MotionEvent.ACTION_POINTER_DOWN:  oldDist = spacing(event); mode = ZOOM; mpointOld=midPoint(event); break;
                case MotionEvent.ACTION_UP:

                    if (mode==DRAG){delta.set(delta.x+(event.getX() - start.x)/scale, delta.y+(event.getY() - start.y)/scale);}
                    mode=NONE;
                    break;

            }


            return true; // indicate event was handled
        }


    });
}


    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    private PointF midPoint( MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        PointF p=new PointF(x / 2, y / 2) ;
        return p;
    }

    private void getMaxDeltaWidth() {
        ImageView imageView=(ImageView)  con.findViewById(R.id.image_view);
       int imageViewWidth= imageView.getWidth();

        imageView.getDrawable().getIntrinsicWidth();


       int bitmapWidth=  imageView.getDrawable().getIntrinsicWidth();

       int currentDelta=Math.round(bitmapWidth*scale)-imageViewWidth;


        Log.d("currentDelta",""+currentDelta);
    };


}
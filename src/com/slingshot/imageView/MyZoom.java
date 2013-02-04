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
double baseScale=1;
float minScale=1;
float scale=1;

Matrix mtx;

public MyZoom(Activity c, float scl, final Matrix startMatrix){
    con=c;       mtx=startMatrix;
    baseScale=scl;
  //  minScale=scl;











    con.findViewById(R.id.image_view).setOnTouchListener(new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            Matrix matrix = new Matrix(mtx);
            switch (event.getAction() & MotionEvent.ACTION_MASK) {

                case MotionEvent.ACTION_DOWN: start.set(event.getX(), event.getY());  mode = DRAG; break;

                case MotionEvent.ACTION_POINTER_UP:

                    if(mode==ZOOM){
                        float newDist1 = spacing(event);
                        float scale1 =newDist1 / oldDist;
                        PointF mpointNew=midPoint(event);


                        //check scale
                        float  ss=scale1*scale;
                        if(ss<minScale){ ss=minScale;
                        }else if (ss>(1/baseScale)){ ss=(float) (1/baseScale);
                        }
                        //end check scale
                        //ss=(float) (scale1*scale); //!!!!!!!!

                        // check horizontal translate

                         float newPositionX=(float)(delta.x+(mpointNew.x-mpointOld.x));
                        //float  newPositionX=(float)(delta.x*ss/scale+(mpointNew.x-mpointOld.x)*(baseScale*ss)+(mpointNew.x)*(1-ss));


                        if (newPositionX>0){newPositionX=0; }
                       else if (newPositionX<getMaxDeltaWidth(ss/scale).x){newPositionX=getMaxDeltaWidth(ss/scale).x; }
                        //end  check horizontal translate

                        // check vertical translate
                         float newPositionY=(float)(delta.y+(mpointNew.y-mpointOld.y));
                       // float newPositionY=(float)(delta.y*ss/scale+(mpointNew.y-mpointOld.y)*(baseScale*ss)+(mpointNew.y)*(1-ss));
                        if (newPositionY>0){newPositionY=0; }
                       else if (newPositionY<getMaxDeltaWidth(ss/scale).y){newPositionY=getMaxDeltaWidth(ss/scale).y; }
                        //end  check vertical translate


                        scale=ss;
                        delta.x=(newPositionX);     //+event.getX() - start.x
                        delta.y=(float)(newPositionY);     //+event.getY() - start.y

                    }

                    mode = NONE;
                    break;


                case MotionEvent.ACTION_MOVE:


                    if (mode == DRAG) {

                        action_on_MOVE_DROP(event,v);

                    };
                    if (mode == ZOOM) {
                        float newDist1 = spacing(event);
                        float scale1 =newDist1 / oldDist;
                        PointF mpointNew=midPoint(event);


                        //check scale
                        float  ss=scale1*scale;
                        if(ss<minScale){ ss=minScale;
                        }else if (ss>(1/baseScale)){ ss=(float) (1/baseScale);
                        }
                        //end check scale
                        //ss=(float) (scale1*scale); //!!!!!!!!

                        Log.d("ss/scale",""+ss/scale);
                        // check horizontal translate

                        float newPositionX=(float)(delta.x+(mpointNew.x-mpointOld.x));        //*(baseScale*scale)
                        //float  newPositionX=(float)(delta.x+(mpointNew.x-mpointOld.x)*(baseScale*ss)+(mpointNew.x)*(1-ss));
                        //float  newPositionX=(float)(delta.x*(ss/scale));
                        if (newPositionX>0){newPositionX=0; }
                       else if (newPositionX<getMaxDeltaWidth(ss/scale).x){newPositionX=getMaxDeltaWidth(ss/scale).x; }
                        //end  check horizontal translate

                        // check vertical translate
                        float newPositionY=(float)(delta.y+(mpointNew.y-mpointOld.y));  //*(baseScale*scale)
                       // float newPositionY=(float)(delta.y+(mpointNew.y-mpointOld.y)*(baseScale*ss)+(mpointNew.y)*(1-ss));
                       // float newPositionY=(float)(delta.y*ss/scale);
                        if (newPositionY>0){newPositionY=0; }
                       else if (newPositionY<getMaxDeltaWidth(ss/scale).y){newPositionY=getMaxDeltaWidth(ss/scale).y; }
                        //end  check vertical translate



                        Matrix matrixMove=new Matrix(matrix);
                        matrixMove.postTranslate(newPositionX,newPositionY);          //+event.getX() - start.x
                        Matrix scaleMatrix=new Matrix(matrixMove);
                       // scaleMatrix.postTranslate(newPositionX,newPositionY);

                       // scaleMatrix.postScale(ss,ss);
                        scaleMatrix.postScale(ss,ss);
                        ((ImageView) v).setImageMatrix(scaleMatrix);

                       // action_on_MOVE_DROP(event,v);


                    }



                break;

                case MotionEvent.ACTION_POINTER_DOWN:  oldDist = spacing(event); mode = ZOOM; mpointOld=midPoint(event); break;
                case MotionEvent.ACTION_UP:

                    if (mode==DRAG){

                        double newPositionX= (delta.x+(event.getX() - start.x)/scale);
                        if (newPositionX>0){newPositionX=0; }
                        else if (newPositionX<getMaxDeltaWidth(1).x){newPositionX=getMaxDeltaWidth(1).x; }

                        double newPositionY= (delta.y+(event.getY() - start.y)/scale);
                        if (newPositionY>0){newPositionY=0; }
                       else if (newPositionY<getMaxDeltaWidth(1).y){newPositionY=getMaxDeltaWidth(1).y; }

                        delta.set((float) newPositionX, (float) (newPositionY));}
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

    private PointF getMaxDeltaWidth(float addScale) {
        PointF res=new PointF(0,0);
        ImageView imageView=(ImageView)  con.findViewById(R.id.image_view);
        int imageViewWidth= ((View)imageView.getParent()) .getWidth();
        int imageViewHeight= ((View)imageView.getParent()) .getHeight();

        int bitmapWidth= imageView.getDrawable().getIntrinsicWidth();
        int bitmapHeight= imageView.getDrawable().getIntrinsicHeight();

        //if(bitmapWidth>bitmapHeight){int temp=bitmapWidth; bitmapWidth=bitmapHeight; bitmapHeight=temp;};

        //res.x=imageViewWidth-Math.round(bitmapWidth*scaleL);
       // Log.d("Scale",""+scale);     Log.d("baseScale",""+baseScale);    Log.d("baseScalebitmapWidth",""+bitmapWidth);

        res.x=imageViewWidth/(scale*addScale)-Math.round(bitmapWidth*baseScale);
        Log.d("Scale res.x",""+res.x);

        res.y=imageViewHeight/(scale*addScale)-Math.round(bitmapHeight*baseScale);
        Log.d("Scale res.y",""+res.y);
        return res;
    };

void action_on_MOVE_DROP(MotionEvent event, View v){
    Matrix matrix = new Matrix(mtx);
    // check horizontal translate
    float newPositionX=(float) (delta.x+(event.getX() - start.x)/scale);
    Log.d("newPositionX",""+newPositionX);  Log.d("newPositionX max",""+getMaxDeltaWidth(1).x);
    if (newPositionX>0){newPositionX=0; }
   else if (newPositionX<getMaxDeltaWidth(1).x){newPositionX=getMaxDeltaWidth(1).x; }
    //end  check horizontal translate

    // check vertical translate
    float newPositionY=(float) (delta.y+(event.getY() - start.y)/scale);
    Log.d("newPositionY",""+newPositionY);
    if (newPositionY>0){newPositionY=0; }
   else if (newPositionY<(getMaxDeltaWidth(1).y)){newPositionY=getMaxDeltaWidth(1).y; }      ///
    //end  check vertical translate




    matrix.postTranslate(newPositionX, newPositionY);
    Matrix scaleMatrix=new Matrix(matrix);
    scaleMatrix.postScale((float) scale,(float) scale);
    ((ImageView) v).setImageMatrix(scaleMatrix);


}




}
package com.slingshot.imageView;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: brodjag
 * Date: 31.01.13
 * Time: 11:43
 * To change this template use File | Settings | File Templates.
 */
public class orderFileArray {

public orderFileArray(File[] filesIn){
files=filesIn;
}
File[] files;

public  File[] makeOrder(){

    /*
    for(int n=0; n<files.length; n++){
        int nFileId =Integer.parseInt(files[n].getName().split("\\u002E")[0]);
        Log.d("nFileId_before",""+nFileId);
    }
    */

      for(int i=0; i<files.length; i++){

         int currentFileId =Integer.parseInt(files[i].getName().split("\\u002E")[0]);
         for(int n=0; n<files.length; n++){ //ni
             int nFileId =Integer.parseInt(files[n].getName().split("\\u002E")[0]);
             if(nFileId>currentFileId){swap(i,n);}
         }


      }

    /*
    for(int n=0; n<files.length; n++){
        int nFileId =Integer.parseInt(files[n].getName().split("\\u002E")[0]);
        Log.d("nFileId",""+nFileId);

    } */

    return files;
  }



void  swap(int i, int n){
   // Log.d("nFileId_swap","i="+i+"; n="+n);
    File temp=files[i];
    files[i]=files[n]; files[n]=temp;
    }
}

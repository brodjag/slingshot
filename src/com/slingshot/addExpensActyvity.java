package com.slingshot;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.slingshot.lib.DatabaseHelper;
import com.slingshot.lib.saveFile;
import com.slingshot.vetch.ancal.Prefs;
import com.slingshot.vetch.widgets.DateWidget;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: brodjag
 * Date: 26.12.12
 * Time: 0:13
 * To change this template use File | Settings | File Templates.
 */
public class addExpensActyvity extends Activity {
    Activity con;
    String id_expense="-1";
public static  String mainFolder="slingshot";
    String fileName="1.jpg";
    Calendar dateStart=Calendar.getInstance();

    String ExpenseCode ="";


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        con=this;
        try{
        id_expense=getIntent().getStringExtra("id");
        }catch (Exception e){}
        setFileName();
        setContentView(R.layout.add_expese);

       initSpiner();


        setButtons();

        setValues();
    }

    private void setButtons() {
        findViewById(R.id.select_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Prefs prefs;
                prefs=new Prefs(con);

                //  DateWidget.Open(con, false,dateStart, prefs.iFirstDayOfWeek);
                //Long cal_ms= getIntent().getLongExtra("date",0);
                dateStart= Calendar.getInstance();
                // dateStart.setTimeInMillis(cal_ms);

                DateWidget.Open(con, false, dateStart, prefs.iFirstDayOfWeek);


            }
        });

        findViewById(R.id.add_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //  int CAMERA_PIC_REQUEST = 1337;
                // startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+mainFolder+"/"+ get_id_expense()+"/"+fileName);
                Log.d("Environment.getExternalStorageDirectory()",""+Environment.getExternalStorageDirectory().getAbsolutePath());
                mCurrentPhotoPath=file.getAbsolutePath();
                Uri outputFileUri = Uri.fromFile(file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                startActivityForResult(intent, TAKE_PICTURE);

            }
        });





        //show images button
        findViewById(R.id.wach_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getFileCount()<1){ Toast.makeText(getBaseContext(),"no images yet",Toast.LENGTH_LONG).show(); return;}

                Intent intent=new Intent(getBaseContext(),img_zoom_Activity.class);
                intent.putExtra("imageFileId",(0));
                intent.putExtra("id_expense",get_id_expense());
                startActivity(intent);
               // finish();
            }
        });

        ((Button) findViewById(R.id.wach_img)).setText("watch images ("+getFileCount()+")");

    }

    private String get_id_expense() {
        if(id_expense.equals("0")){
          DatabaseHelper dh=new DatabaseHelper(con);
          int maxId=  dh.getMaxIdExpense();
          maxId++;
         Log.d("maxId",""+maxId);
          return ""+maxId;
        }else {
            return id_expense;
        }
    }
    //end setButton

    private static int TAKE_PICTURE = 1;
 //   private Uri outputFileUri;
    private  String mCurrentPhotoPath;


    //select expense Code spiner
    List<String> spinerItems;
    private void initSpiner() {
        saveFile sf=new saveFile(con);
        Element el= sf.readXmlFile("ExpenseCodes.xml");
        Element body=(Element) el.getElementsByTagName("soap:Body").item(0);
        Element GetExpenseCodesResponse=(Element)  (body).getElementsByTagName("GetExpenseCodesResponse").item(0);
      Element GetExpenseCodesResult=(Element) (GetExpenseCodesResponse).getElementsByTagName("GetExpenseCodesResult").item(0);
       String Code= GetExpenseCodesResult.getElementsByTagName("Code").item(0).getFirstChild().getNodeValue().toString();
        NodeList ExpenseCodes=((Element)GetExpenseCodesResult.getElementsByTagName("ExpenseCodes").item(0)).getElementsByTagName("string");
         spinerItems=new ArrayList<String>() ;
        for(int i=0; i<ExpenseCodes.getLength(); i++){
            spinerItems.add(ExpenseCodes.item(i).getFirstChild().getNodeValue().toString());
        }

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setPrompt("Select expense's code");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                addExpensActyvity.this.ExpenseCode = spinerItems.get(i);
                Toast.makeText(con, addExpensActyvity.this.ExpenseCode,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
    }
    //end select expense Code spiner


    //help function for calendar
    public static Bundle getIntentExtras(Intent data)
    {
        // data is null, when activity cancelled by BACK BUTTON
        if (data != null){
            Bundle extras = data.getExtras();
            if (extras != null)   {    return extras; }
    }
        return null;
    };



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

      //Log.d("requestCode","="+requestCode);
      //requst from calendar
      if (requestCode==111){
        Bundle extras = getIntentExtras(data);
        if (extras != null)
        {
            //check for date widget edit request code
            if (requestCode == DateWidget.SELECT_DATE_REQUEST)
            {
                final long lDate = DateWidget.GetSelectedDateOnActivityResult(requestCode, resultCode, extras, dateStart);
                if (lDate != -1)
                {
                    setDateOnButton();
                    Toast.makeText(con,""+dateStart.getTime().toString(),Toast.LENGTH_LONG).show();

                    return;
                }
            }
        }
      }

      //request from camera
        if(requestCode==1) {
      if(resultCode==-1) {
             /*
               View v= con.getLayoutInflater().inflate(R.layout.add_expence_img,null);
                ImageView img=   (ImageView) v.findViewById(R.id.img_item);
               ((LinearLayout) findViewById(R.id.add_expence_img_list)).addView(v);
                addExpensImages im=new addExpensImages(con);
                im.scaleImage(img,Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+mainFolder+"/"+id_expense+"/"+fileName);
             */
          Toast.makeText(this, "Picture is appended", Toast.LENGTH_LONG).show();
            setFileName();
          ((Button) findViewById(R.id.wach_img)).setText("watch images ("+getFileCount()+")");
        }else{
            Toast.makeText(this, "Picture is not taken", Toast.LENGTH_LONG).show();
        }
    }
    }
  //set next free file's name for value "fileName"  f.e. 1.jpg, 2.jpg...
   void setFileName(){
       File mainFolderF=  new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+mainFolder);
       if(!mainFolderF.exists()){mainFolderF.mkdir();}

       File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+mainFolder+"/"+get_id_expense());
       if(!root.exists()){root.mkdir();}
       File[] ImgsInIdFolder=root.listFiles();
       if (ImgsInIdFolder==null){return;}

       int max=1;
       for (int i=0; i<ImgsInIdFolder.length; i++ ){
           String fileName_i=ImgsInIdFolder[i].getName();
           Log.d("path1",fileName_i);
           Log.d("path12",fileName_i.split("\\u002E")[0]) ;
         int num_i=Integer.parseInt(fileName_i.split("\\u002E")[0]);
         if (num_i>max){max=num_i;}
       }
       max++;
       fileName=""+max+".jpg";
   }

    //set values from DB to View
    private void setValues() {
        if (id_expense.equals("0")) {setDateOnButton(); return;}
        DatabaseHelper dh=new DatabaseHelper(con);
        Cursor c=dh.getExpenseById(id_expense);

        //set spiner
        String code= c.getString(1);//!!!
        for(int i=0; i<spinerItems.size();i++){
         if(spinerItems.get(i).equals(code));
           ((Spinner) findViewById(R.id.spinner)).setSelection(i);
        }
        ExpenseCode=code;

        Long dateMS=c.getLong(2);
        dateStart.setTimeInMillis(dateMS);
        setDateOnButton();

       // String description=c.getString(3);
        ((TextView) findViewById(R.id.description)).setText(c.getString(3));

        ((TextView) findViewById(R.id.amount)).setText(c.getString(4));
        c.close();
        dh.close();
    }

   //format date on Button
   void setDateOnButton(){
        ((Button) findViewById(R.id.select_date)).setText(""+dateStart.get(Calendar.YEAR)+"/"+(dateStart.get(Calendar.MONTH)+1)+"/"+dateStart.get(Calendar.DAY_OF_MONTH));
    }

   //save values in DB
   void saveValues(){
       DatabaseHelper dh=new DatabaseHelper(con);
        String desc=((TextView) findViewById(R.id.description)).getText().toString();
       dh.setExpense(id_expense, ExpenseCode, dateStart.getTimeInMillis(), desc, ((TextView) findViewById(R.id.amount)).getText().toString());

       Log.d("desc",desc);
       dh.close();
   }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            saveValues();

            finish();

            startActivity(new Intent(con,listActivity.class));
        }
        return super.onKeyDown(keyCode, event);

    }

    //count of images for this expense
    int getFileCount(){
        File mainFolderF=  new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+mainFolder);
        if(!mainFolderF.exists()){mainFolderF.mkdir();}

        File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+mainFolder+"/"+get_id_expense());
        if(!root.exists()){root.mkdir();}
        File[] ImgsInIdFolder=root.listFiles();
        if (ImgsInIdFolder==null){return 0;}

        return ImgsInIdFolder.length;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case R.id.setting:
                saveValues();
                Intent setting= new Intent(con,settingActivity.class);
                setting.putExtra("back",settingActivity.backToEditExpense);
                setting.putExtra("id_expense",id_expense);
                con.startActivity(setting);
                con.finish();
                break;

        }
        return true;
    }
}
package com.slingshot.add_expense_view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import com.slingshot.R;
import com.slingshot.imageView.ImgStarter;
import com.slingshot.lib.DatabaseHelper;
import com.slingshot.lib.fileLib;
import com.slingshot.listActivity;
import com.slingshot.vetch.ancal.Prefs;
import com.slingshot.vetch.widgets.DateWidget;

import java.io.File;
import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: brodjag
 * Date: 26.12.12
 * Time: 0:13
 * create edit view
 */
public class addExpensActivity extends Activity  {
    Activity con;
    public String id_expense="0";
    public static  String mainFolder="slingshot";
    String fileName="1.jpg";
    Calendar dateStart=Calendar.getInstance();
    String ExpenseCode ="";

    //public     int isSpinerSelected=0;   //true when spiner changed


    @Override
    public void onResume(){
        super.onResume();
        ((TextView) findViewById(R.id.img_count)).setText(""+EHelper.getFileCount()+"");
       // Toast.makeText(this,"ff",Toast.LENGTH_SHORT).show();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        con=this;
        try{   id_expense=getIntent().getStringExtra("id"); }catch (Exception e){}
        EHelper= new addExpenseHelp(this,id_expense);
        EHelper.cleanTemp();
        if(id_expense.equals("0")){EHelper.cleanZerroFolder0();}

        fileName=EHelper.getFileName();
        setContentView(R.layout.add_expese);
      // initSpiner();
        new tongue(this);
        setButtons();
        setValues();
    }

    addExpenseHelp EHelper=null;

    private void setButtons() {
        EHelper=new addExpenseHelp(con,id_expense);

        //remove item
        con.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {     ((Vibrator) con.getSystemService(con.VIBRATOR_SERVICE)).vibrate(50); } catch (Exception e) {}
                if(!fileLib.isSDCardMounted()){Toast.makeText(con,"Cd card isn't connected", Toast.LENGTH_LONG).show(); return;}

                final AlertDialog.Builder dlgAlert = new AlertDialog.Builder(con);

                String desc=((EditText)con.findViewById(R.id.description)).getText().toString();
                dlgAlert.setMessage("Remove "+ExpenseCode+" '" + desc + "'?");
                //dlgAlert.setTitle("App Title");
                dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            ((Vibrator) con.getSystemService(con.VIBRATOR_SERVICE)).vibrate(50);
                        } catch (Exception e) {
                        }
                        DatabaseHelper dh = new DatabaseHelper(con);
                        dh.removeExpenseId(id_expense);

                        Cursor c = dh.getExpenseAll();
                        if (c.getCount() == 0) {

                            findViewById(R.id.scrollist).setVisibility(View.GONE);
                            findViewById(R.id.message_no_items).setVisibility(View.VISIBLE);
                        }
                        c.close();
                        dh.close();
                        listActivity.removeImgFile(id_expense);
                        con.finish();
                        startActivity(new Intent(con, listActivity.class));
                        // ((ViewManager) v.getParent()).removeView(v);
                    }
                });
                dlgAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();

                //   ((ViewManager) v.getParent()).removeView(v);
            }
        });


        findViewById(R.id.select_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Prefs prefs;
                prefs=new Prefs(con);
                dateStart= Calendar.getInstance();
                DateWidget.Open(con, false, dateStart, prefs.iFirstDayOfWeek);

            }
        });


        //show images button
        findViewById(R.id.add_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {     ((Vibrator) con.getSystemService(con.VIBRATOR_SERVICE)).vibrate(50); } catch (Exception e) {}

                if(!fileLib.isSDCardMounted()){
                    Toast.makeText(con, "Cd card isn't connected", Toast.LENGTH_LONG).show();
                    return;
                }

                if(EHelper.getFileCount()<1){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+mainFolder+"/imgs/"+ EHelper.get_id_expense()+"/"+fileName);

                    mCurrentPhotoPath=file.getAbsolutePath();
                    Uri outputFileUri = Uri.fromFile(file);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                    startActivityForResult(intent, TAKE_PICTURE);
                    //Toast.makeText(getBaseContext(),"no images yet",Toast.LENGTH_LONG).show();
                    return;
                }

                ImgStarter imgStarter=new ImgStarter(con);
                imgStarter.showView(EHelper.get_id_expense());
               // finish();
            }
        });



        ((TextView) findViewById(R.id.img_count)).setText(""+EHelper.getFileCount()+"");


        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {     ((Vibrator) con.getSystemService(con.VIBRATOR_SERVICE)).vibrate(50); } catch (Exception e) {}
              if(!EHelper.checkInput()){return;}
                saveValues();
                finish();
                startActivity(new Intent(con,listActivity.class));
            }
        });

        findViewById(R.id.new1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {     ((Vibrator) con.getSystemService(con.VIBRATOR_SERVICE)).vibrate(50); } catch (Exception e) {}
                if(!EHelper.checkInput()){return;}
                saveValues();
                Intent intent=new Intent(con,addExpensActivity.class);
                intent.putExtra("id","0");
                startActivity(intent);
                finish();
            }
        });
    }


    //end setButton

    private static int TAKE_PICTURE = 1;
 //   private Uri outputFileUri;
    private  String mCurrentPhotoPath;


    //select expense Code spiner
    /*
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
       // spinner.setPrompt("Select expense code");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                addExpensActivity.this.ExpenseCode = spinerItems.get(i);
                isSpinerSelected++;
               // Toast.makeText(con,"selected !!!",Toast.LENGTH_LONG).show();
                Log.d("selected","!!! isSpinerSelected=="+isSpinerSelected);
               // Toast.makeText(con, addExpensActivity.this.ExpenseCode,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                   Toast.makeText(con,"nothink !!!",Toast.LENGTH_LONG).show();
            }
        });

    }
    */
    //end select expense Code spiner


    //help function for calendar
    public static Bundle getIntentExtras(Intent data)
    {
        if (data != null){ Bundle extras = data.getExtras(); if (extras != null) {return extras; } }
        return null;
    };



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

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
                { setDateOnButton(); return;}
            }
        }
      }

      //request from camera
        if(requestCode==1) {
      if(resultCode==-1) {

          //Toast.makeText(this, "Picture is appended", Toast.LENGTH_LONG).show();
          fileName= EHelper.getFileName();
          ((TextView) findViewById(R.id.img_count)).setText("("+EHelper.getFileCount()+")");

          ImgStarter imgStarter=new ImgStarter(con);
          imgStarter.showView(EHelper.get_id_expense());

        }else{
            Toast.makeText(this, "Picture is not taken", Toast.LENGTH_LONG).show();
        }
    }
    }
  //set next free file's name for value "fileName"  f.e. 1.jpg, 2.jpg...


    //set values from DB to View
    private void setValues() {
        if (id_expense.equals("0")) {
            findViewById(R.id.delete).setVisibility(View.INVISIBLE);

            ((TextView)findViewById(R.id.title_edit_expense)).setText("New Expense");
            setDateOnButton();
            // ((Spinner) findViewById(R.id.spinner)).performClick();
            con.findViewById(R.id.tongue_perent).setVisibility(View.VISIBLE);
            return;
        }
        DatabaseHelper dh=new DatabaseHelper(con);
        Cursor c=dh.getExpenseById(id_expense);

        //set spiner


        String code= c.getString(1);//!!!
        /*
        for(int i=0; i<spinerItems.size();i++){
         if(spinerItems.get(i).equals(code)){ ((Spinner) findViewById(R.id.spinner)).setSelection(i);}
        }      */
        ((TextView) findViewById(R.id.selected_expense_code_name)).setText(code);

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
        ((Button) findViewById(R.id.select_date)).setText(""+(dateStart.get(Calendar.MONTH)+1)+"/"+dateStart.get(Calendar.DAY_OF_MONTH)+"/"+dateStart.get(Calendar.YEAR));
    }

   //save values in DB
   void saveValues(){
      // EHelper.cleanTemp();
       EHelper.applyTemp();
      // EHelper.moveToIdFolder();
       DatabaseHelper dh=new DatabaseHelper(con);
        String desc=((TextView) findViewById(R.id.description)).getText().toString();
     if(id_expense.equals("0")){
         dh.newExpense(id_expense, ExpenseCode, dateStart.getTimeInMillis(), desc, ((TextView) findViewById(R.id.amount)).getText().toString());
         EHelper.applyFolder0();
     }else {
         dh.setExpense(id_expense, ExpenseCode, dateStart.getTimeInMillis(), desc, ((TextView) findViewById(R.id.amount)).getText().toString());

     }

       dh.close();
   }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

         if (new checkChanges4BackButton(this).isChanged()){
            final AlertDialog.Builder dlgAlert = new AlertDialog.Builder(con);
            dlgAlert.setMessage("Discard changes?");
            dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    try {     ((Vibrator) con.getSystemService(con.VIBRATOR_SERVICE)).vibrate(50); } catch (Exception e) {}
                    EHelper.cancelRemoved();
                    EHelper.cleanTemp();
                    finish();
                    startActivity(new Intent(con,listActivity.class));
                }
            });
            dlgAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();


        }else {
            EHelper.cleanTemp();
            finish();
            startActivity(new Intent(con,listActivity.class));
        }
       // return false;
        }
        return super.onKeyDown(keyCode, event);


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if((con.findViewById(R.id.tongue_perent).getVisibility()==View.VISIBLE)||(con.findViewById(R.id.tongue_perent_hr).getVisibility()==View.VISIBLE)) {

            int orientation = con.getResources().getConfiguration().orientation;
            if (orientation== Configuration.ORIENTATION_PORTRAIT){
                con.findViewById(R.id.tongue_perent).setVisibility(View.VISIBLE);
                con.findViewById(R.id.tongue_perent_hr).setVisibility(View.GONE);
            }else {
                con.findViewById(R.id.tongue_perent_hr).setVisibility(View.VISIBLE);
                con.findViewById(R.id.tongue_perent).setVisibility(View.GONE);
            }
        }
    }



}
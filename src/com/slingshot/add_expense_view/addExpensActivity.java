package com.slingshot.add_expense_view;

import android.app.Activity;
import android.content.Intent;
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
import com.slingshot.lib.saveFile;
import com.slingshot.listActivity;
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
 * create edit view
 */
public class addExpensActivity extends Activity  {
    Activity con;
    String id_expense="0";
public static  String mainFolder="slingshot";
    String fileName="1.jpg";
    Calendar dateStart=Calendar.getInstance();
    String ExpenseCode ="";


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
        if(id_expense.equals("0")){EHelper.cleanZerroFolder0();}

        fileName=EHelper.getFileName();
        setContentView(R.layout.add_expese);
       initSpiner();
        setButtons();
        setValues();
    }

    addExpenseHelp EHelper=null;

    private void setButtons() {
        EHelper=new addExpenseHelp(con,id_expense);

        findViewById(R.id.select_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Prefs prefs;
                prefs=new Prefs(con);
                dateStart= Calendar.getInstance();
                DateWidget.Open(con, false, dateStart, prefs.iFirstDayOfWeek);

            }
        });

        /*
        findViewById(R.id.add_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {     ((Vibrator) con.getSystemService(con.VIBRATOR_SERVICE)).vibrate(50); } catch (Exception e) {}
                if(!fileLib.isSDCardMounted()){Toast.makeText(con,"Cd card isn't connected", Toast.LENGTH_LONG).show(); return;}

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+mainFolder+"/imgs/"+ EHelper.get_id_expense()+"/"+fileName);

                mCurrentPhotoPath=file.getAbsolutePath();
                Uri outputFileUri = Uri.fromFile(file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                startActivityForResult(intent, TAKE_PICTURE);

            }
        });
                 */

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
                Toast.makeText(con, addExpensActivity.this.ExpenseCode,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
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
            ((TextView)findViewById(R.id.title_edit_expense)).setText("New Expense");
            setDateOnButton(); return;
        }
        DatabaseHelper dh=new DatabaseHelper(con);
        Cursor c=dh.getExpenseById(id_expense);

        //set spiner
        String code= c.getString(1);//!!!
        for(int i=0; i<spinerItems.size();i++){
         if(spinerItems.get(i).equals(code)){ ((Spinner) findViewById(R.id.spinner)).setSelection(i);}
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
           // saveValues();
            EHelper.cleanTemp();
            finish();
            startActivity(new Intent(con,listActivity.class));
        }
        return super.onKeyDown(keyCode, event);

    }



}
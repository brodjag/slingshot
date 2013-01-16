package com.slingshot.lib;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by IntelliJ IDEA.
 * User: brodjag
 * Date: 17.09.12
 * Time: 17:49
 * To change this template use File | Settings | File Templates.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    static final String dbName="MyDb";
    static final int version=20;

  //  String createScanedDB="CREATE TABLE scaned (id integer PRIMARY KEY,barcode text,kod text,name text,article text,FullName text,count integer,id_store text, unit text, count_db integer)";




    public DatabaseHelper(Context context) {
        super(context, dbName, null,version);

    }

    public void onCreate(SQLiteDatabase db) {

      //  db.execSQL("CREATE TABLE stores (id INTEGER PRIMARY KEY, name TEXT, kod TEXT)");
        
      //  db.execSQL(createScanedDB);  //"CREATE TABLE scaned (id integer PRIMARY KEY,barcode text

      //  db.execSQL("CREATE INDEX idx_barcode ON scaned (barcode)");
        // ,kod text,name text,article text,FullName text,count integer,id_store text, unit text)"

        db.execSQL("CREATE TABLE setting (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value TEXT)");
        db.execSQL("CREATE TABLE expense (id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, code TEXT, date LONG,  description TEXT, amount TEXT )");

        db.execSQL("Insert into setting (name, value)  values('url_pre', 'http://support.slingshotsoftware.com/webservices4test/TripExpenseCapture.asmx')");
        db.execSQL("Insert into setting (name, value)  values('url_area','http://www.slingshotsoftware.com/XmlNamespaces/WebServices/G2')");
        db.execSQL("Insert into setting (name, value)  values('login','TestTraveler')");
        db.execSQL("Insert into setting (name, value)  values('password','$$apvvord')");
        db.execSQL("Insert into setting (name, value)  values('Project','G2')");

       // db.execSQL("Insert into expense (code, date, description, amount)  values('Tolls', 1,'descr', '100')");


//      setSetting("url_pre","http://fpat.ru/DemoEnterprise/ws/");
       // setSetting("url_area","1csoap");


        /*
        db.execSQL("CREATE TABLE "+deptTable+" ("+colDeptID+ " INTEGER PRIMARY KEY , "+
                colDeptName+ " TEXT)");

        db.execSQL("CREATE TABLE "+employeeTable+" ("+colID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
        colName+" TEXT, "+colAge+" Integer, "+colDept+
                " INTEGER NOT NULL ,FOREIGN KEY ("+colDept+") REFERENCES"+deptTable+" ("+colDeptID+"));");


        db.execSQL("CREATE TRIGGER fk_empdept_deptid " +
                " BEFORE INSERT "+
                " ON "+employeeTable
                +" FOR EACH ROW BEGIN"+
                " SELECT CASE WHEN ((SELECT "+colDeptID+" FROM "+deptTable+
                " WHERE "+colDeptID+"=new."+colDept+" ) IS NULL)"+
        " THEN RAISE (ABORT,'Foreign Key Violation') END;"+
                "  END;");

        db.execSQL("CREATE VIEW "+viewEmps+
                " AS SELECT "+employeeTable+"."+colID+" AS _id,"+
                " "+employeeTable+"."+colName+","+
                " "+employeeTable+"."+colAge+","+
                " "+deptTable+"."+colDeptName+""+
                " FROM "+employeeTable+" JOIN "+deptTable+
                " ON "+employeeTable+"."+colDept+" ="+deptTable+"."+colDeptID
        );
        */
        //Inserts pre-defined departments
      //  InsertDepts(db);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+"setting");
        db.execSQL("DROP TABLE IF EXISTS "+"expense");

        onCreate(db);
    }


/***************************************
stores
 ***************************************/
/*
public long insertToStore(String name, String kod) {
    SQLiteDatabase db=this.getWritableDatabase();
    ContentValues cv=new ContentValues(2);
     cv.put("name" ,name);
    cv.put("kod" ,kod);
      long  res=db.insert("stores",null,cv);
    db.close();
    return res;
}

public Cursor getAllStores()
    {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("SELECT * from stores",new String [] {});
         cur.moveToFirst();
        db.close();
        return cur;
    }

public void removeAllStores(){
    SQLiteDatabase db=this.getWritableDatabase();
    db.execSQL("DROP TABLE IF EXISTS "+"stores");
    db.execSQL("CREATE TABLE stores (id INTEGER PRIMARY KEY, name TEXT, kod TEXT)");
    db.close();
};

 */

/***************************************
 scaned
 ***************************************/
/*
public long insertToScaned(String barcode, String kod, String name, String article, String FullName,int count, String id_store, String unit,int count_db ) {
    SQLiteDatabase db=this.getWritableDatabase();
    ContentValues cv=new ContentValues(8);
    cv.put("barcode" ,barcode);
    cv.put("kod" ,kod);
    cv.put("name" ,name);
    cv.put("article" ,article);
    cv.put("FullName" ,FullName);
    //cv.put("store_id" ,store_id);
    cv.put("count",count);
    cv.put("id_store",id_store);
    cv.put("unit",unit);
    cv.put("count_db", count_db );
    long  res=db.insert("scaned",null,cv);
    db.close();
    return res;
}

public Cursor getAllScaned(String id_store)
    {

        long seconds = System.currentTimeMillis();

        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("SELECT * from scaned where id_store=?",new String [] {id_store});
        cur.moveToFirst();
        db.close();
        long seconds2 = System.currentTimeMillis();
        Log.d("getScanedById", "delta="+(seconds2-seconds));
        return cur;
    }

    public void removeAllScaned(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS "+"scaned");
        db.execSQL(createScanedDB);
        db.close();
    };

    public int removeScanedId(String id)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        int res= db.delete("scaned","id=?",new String[]{id});
        db.close();
        return res;
    }

    public int removeScanedByStoreId(String id)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        int res= db.delete("scaned","id_store=?",new String[]{id});

        db.close();
       return  res;
    }



    public Cursor getScanedById(String id)
    {


        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("SELECT * from scaned where id=?",new String [] {id});
        cur.moveToFirst();
        db.close();


        return cur;
    }

    public void updateScanedId(String id,int count)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put("count", count);
        db.update("scaned",args,"id=?",new String[]{id});
       // db.delete("scaned","id=?",new String[]{id});
        db.close();

    }

    public int getScanedIdByBare(String barcode,String id_store)
    {


        SQLiteDatabase db=this.getReadableDatabase();
        long seconds = System.currentTimeMillis();
        Cursor cur;
      // String s= String.format("SELECT * from scaned where barcode='"+barcode+"' ",new String [] {});
      //  Log.d("selectBare", s);

       cur= db.rawQuery("SELECT * from scaned where barcode=? and id_store=?",new String [] {barcode, id_store});
       // cur=db.query("scaned",new String[] { "*" },"barcode=? and id_store=?",new String []{barcode, id_store},null,null,null,null);


        long seconds2 = System.currentTimeMillis();
        Log.d("getScanedIdByBare", "delta="+(seconds2-seconds));

        cur.moveToFirst();
        int res=-1;
        if(cur.getCount()>0){res=cur.getInt(0); }
        db.close();
        cur.close();
        return res;
    }

public void deleteStorageList(String idStorage){

}
    */

//********************************
//setting values
//********************************
public String getSetting(String value){
    SQLiteDatabase db=this.getReadableDatabase();
    Cursor cur;
   // cur= db.rawQuery("SELECT * from setting ",new String [] {});
    cur= db.rawQuery("SELECT * from setting where name=\""+value+"\"",new String [] {});
    cur.moveToFirst();

    String res="";
    if (cur.getCount()>0){ res=cur.getString(2);  }
    cur.close();
    db.close();
    Log.d("setting1", value+"="+  res);
    return res;
}

    public double newSetting(String name,String value){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put("name" ,name);
        cv.put("value" ,value);
        long  res=db.insert("setting",null,cv);
        db.close();
        return res;
    }

    private void settingUpdate(String name, String value){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put("value", value);
        db.update("setting", args, "name=?", new String[]{name});
        db.close();
    }

    private double getSettingCount(String name){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur;
        cur= db.rawQuery("SELECT * from setting where name='"+name+"'",new String [] {});
        double res=cur.getCount();
        cur.close();
        db.close();
        return res;
    }

    public void setSetting(String name, String value){
     if (getSettingCount(name)==0){
         newSetting(name,value);
     } else {
         settingUpdate(name,value);
     }
    }

   public String getURL(){
    String res="";
   res=getSetting("url_pre");
   return  res;

   }




/**************************************
 * expenses
  ***************************/
public Cursor getExpenseById(String id)
{
    SQLiteDatabase db=this.getReadableDatabase();
    Cursor cur=db.rawQuery("SELECT * from expense where id=?",new String [] {id});
    cur.moveToFirst();
    db.close();
    return cur;
}

    public Cursor getExpenseAll()
    {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("SELECT * from expense ORDER BY id DESC",new String [] {});
        cur.moveToFirst();
        db.close();
        return cur;
    }


public void setExpense(String valueId, String code,long date,String description,String amount){
        if (getExpenseCount(valueId)==0){
            newExpense(valueId, code, date, description,amount);
        } else {
            expenseUpdate(valueId, code, date, description,amount);
        }
    }
    private double getExpenseCount(String id){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur;
        cur= db.rawQuery("SELECT * from expense where id="+id,new String [] {});
        double res=cur.getCount();
        cur.close();
        db.close();
        Log.d("getExpenseCount","="+res);
        return res;
    }

    private void expenseUpdate(String valueId, String code,long date,String description,String amount){

        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues args = new ContentValues();  //   amount TEXT
        args.put("code", code);
        args.put("date", date);
        args.put("description", description);
        args.put("amount", amount);
        db.update("expense", args, "id="+valueId, null);
        db.close();
    }

    public double newExpense(String valueId, String code,long date,String description,String amount){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues args = new ContentValues();  //   amount TEXT
        //args.put("id", null);
        args.put("code", code);
        args.put("date", date);
        args.put("description", description);
        args.put("amount", amount);
       // db.execSQL("insert into expense (code,date,description) values('"+code+"',"+date+",'"+description+"')");
        long  res=db.insert("expense",null,args);
        Log.d("newExpense","="+res);
        db.close();
        return res;
    }

    public int removeExpenseId(String id)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        int res= db.delete("expense","id="+id,new String[]{});
        db.close();
        return  res;
    }

    public int getMaxIdExpense()
    {
        int maxId=0;
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("SELECT MAX(id) from expense",new String [] {});
        cur.moveToFirst();

       // try{
       // Log.d("getMaxIdExpense1", "count="+cur.getCount()) ;
            maxId=cur.getInt(0);//}catch (Exception e){}
        cur.close();
        db.close();
        return maxId;
    }

}
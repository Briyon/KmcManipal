package com.kmcmanipal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ColdFire on 6/15/2016.
 */
public class TableHelper extends SQLiteOpenHelper
{
    SQLiteDatabase db;
    private static final int DATABASE_VERSION = 1;
    // private static final String DATABASE_NAME = "KMC_MANIPAL.db";
    private static final String DATABASE_NAME = "SAMPLE2.db";

    Context ctx;

    private static final String CREATE_USER_DETAILS = "create table user_details " +
            "(id text primary key," +
            "fname text not null," +
            "lname text not null," +
            "designation text not null," +
            "email text not null," +
            "uname text not null," +
            "password text not null," +
            "active BOOLEAN not null," +
            "affiliation text not null);";

    private static final String CREATE_USER_LOG = "create table user_log " +
            "(log_id INTEGER Primary key autoincrement," +
            "id text," +
            "log_date text," +
            "logout TEXT," +
            "FOREIGN KEY(id)REFERENCES user_details(id));";


/*
    private static final String CREATE_LOGIN = "create table login (user_id text,user_name text);";
*/

    private static final String CREATE_FLAGS = "create table flags " +
            "(record_id integer not null, " +
            "drug text," +
            "stock INTEGER, " +
            "docu_flag BOOLEAN," +
            "clean_flag BOOLEAN," +
            "hk_flag BOOLEAN," +
            "issues text," +
            "timestamp text,"+
            "ph_flag BOOLEAN,"+
            "avail_staff BOOLEAN,"+
            "hk_register_flag BOOLEAN,"+
            "equip_break BOOLEAN,"+
            "comments text,"+
            "FOREIGN KEY(record_id)REFERENCES records(record_id));";


    private static final String CREATE_REPORT = "create table reports (_id integer Primary key autoincrement,rdate text Unique,user_id text,submitted TEXT,others TEXT,FOREIGN KEY(user_id)REFERENCES user_details(id));";

    private static final String CREATE_RECORD = "create table records (rdate text,table_id text ,record_id integer Primary key autoincrement,FOREIGN KEY(table_id)REFERENCES table_list(table_id),FOREIGN KEY(rdate)REFERENCES reports(rdate));";

    private static final String CREATE_TABLE_LIST = "create table table_list (table_id text Primary key,table_name text);";
    private static final String CREATE_TR_ER_TABLE = "create table tr_er " +
            "(record_id integer not null, " +
            "total_mlc INTEGER ," +
            "sent_mlc INTEGER ," +
            "doct_avl BOOLEAN ," +
            "sur_flag BOOLEAN ," +
            "med_flag BOOLEAN ," +
            "FOREIGN KEY(record_id)REFERENCES records(record_id));";



    private static final String CREATE_ICU_TABLE = "create table icu " +
            "(record_id integer ," +
            "beds_use INTEGER not null," +
            "beds_avl INTEGER," +
            "vent_use INTEGER," +
            "vent_avl INTEGER ," +
            "vent_repair INTEGER," +
            "ipno INTEGER not null," +
            "FOREIGN KEY(record_id)REFERENCES records(record_id));";

    private static final String CREATE_WARDS_TABLE = "create table wards " +
            "(record_id integer not null ," +
            "w_name text not null," +
            "FOREIGN KEY(record_id)REFERENCES records(record_id));";

    private static final String CREATE_STABLE_DETAILS = "create table stable_details " +
            "(table_id text not null,"+
            "rdate text,"+
            "st_ip text not null," +
            "st_det text not null,"+
            "FOREIGN KEY(table_id)REFERENCES table_list(table_id));";


    private static final String CREATE_BLOOD_BANK = "create table blood_bank " +
            "(record_id integer REFERENCES records," +
            "blood_shortage boolean)";

    private static final String CREATE_RADIOLOGY = "create table radiology " +
            "(record_id integer REFERENCES records," +
            "support_staff boolean," +
            "doctors boolean)";

    private static final String CREATE_PHARMACY = "create table pharmacy " +
            "(record_id integer REFERENCES records," +
            "stock_out_drugs boolean," +
            "indents_sent boolean)";


    public static final String KEY_RDATE = "rdate";


  /*  *//** Field 1 of the table cust_master, which is the primary key *//*
    public static final String KEY_ROW_ID = "_id";

    *//** Field 2 of the table cust_master, stores the customer code *//*
    public static final String KEY_CODE = "cust_code";*/



    /*  //Changed Database Refer this this and correct above tables
    private static final String TR_ER_TABLE = "create table tr_er (id text not null, " +
            "cdate text not null,
             drug text not null,
              stock INTEGER not null,
               docu_flag BOOLEAN,
               hk_flag BOOLEAN ,
                mlc INTEGER not null,
                police_flag BOOLEAN,
                doct_avl BOOLEAN ,
                sur_flag BOOLEAN ,
                med_flag BOOLEAN ,
                clean_flag BOOLEAN,
                issues text,
                primary key(id,cdate));";
    private static final String ICU = "create table icu (icu_id text , " +
            "cdate text not null, beds_use INTEGER not null,beds_avl INTEGER, vent_use INTEGER, vent_avl INTEGER , vent_repair INTEGER,ipno INTEGER not null,docu_flag BOOLEAN,clean_flag BOOLEAN,hk_flag BOOLEAN,comments text,issues text, primary key(icu_id,cdate));";
    private static final String wards = "create table wards (cat_id text not null , " +
            " w_name text not null, cdate text not null,cl_flag BOOLEAN, hk_flag BOOLEAN,ph_flag BOOLEAN,issues text, primary key(w_name,cdate));";
    private static final String stab_det = "create table stable_details (icu_id integer not null," +
            "cdate text not null, st_ip text not null,st_det text not null,primary key(icu_id,cdate));";
    */


    public TableHelper(Context context) throws NullPointerException
    {
       super(context, DATABASE_NAME, null, DATABASE_VERSION);
       // super(context, Environment.getExternalStorageDirectory().getAbsolutePath()+ "/" + DATABASE_NAME, null, DATABASE_VERSION);
       // super(context, context.getExternalFilesDir(null).getAbsolutePath()+ "/" + DATABASE_NAME, null, DATABASE_VERSION);
       // super(context, "/mnt/extSdCard/Android/databases/"+ DATABASE_NAME, null, DATABASE_VERSION);
        ctx = context;
        db = getWritableDatabase();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_DETAILS);
        db.execSQL(CREATE_USER_LOG);
      //  db.execSQL(CREATE_LOGIN);
        db.execSQL(CREATE_TR_ER_TABLE);
        db.execSQL(CREATE_ICU_TABLE);
        db.execSQL(CREATE_WARDS_TABLE);
        db.execSQL(CREATE_STABLE_DETAILS);
        db.execSQL(CREATE_TABLE_LIST);
        insertTableList(db);
        db.execSQL(CREATE_FLAGS);
        db.execSQL(CREATE_REPORT);
        db.execSQL(CREATE_RECORD);
        db.execSQL(CREATE_BLOOD_BANK);
        db.execSQL(CREATE_RADIOLOGY);
        db.execSQL(CREATE_PHARMACY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP table if exists user_details");
        db.execSQL("DROP table if exists user_log");
        //db.execSQL("DROP table if exists login");
        db.execSQL("DROP table if exists tr_er");
        db.execSQL("DROP table if exists icu");
        db.execSQL("DROP table if exists wards");
        db.execSQL("DROP table if exists stable_details");
        db.execSQL("DROP table if exists reports");
        db.execSQL("DROP table if exists records");
        db.execSQL("DROP table if exists flags");
        db.execSQL("DROP table if exists table_list");
        db.execSQL("DROP table if exists blood_bank");
        db.execSQL("DROP table if exists radiology");
        db.execSQL("DROP table if exists pharmacy");
        onCreate(db);
    }

    public  void insertTableList(SQLiteDatabase db)
    {
        db.execSQL("insert into table_list values('T1','Trauma')");
        db.execSQL("insert into table_list values('T2','Emergency')");
        db.execSQL("insert into table_list values('T3','CAS-ICU')");
        db.execSQL("insert into table_list values('T4','ICU-1')");
        db.execSQL("insert into table_list values('T5','ICU-2')");
        db.execSQL("insert into table_list values('T6','ICU-3')");
        db.execSQL("insert into table_list values('T7','ICU-4')");
        db.execSQL("insert into table_list values('T8','General')");
        db.execSQL("insert into table_list values('T9','Special')");
        db.execSQL("insert into table_list values('T10','Blood-Bank')");
        db.execSQL("insert into table_list values('T11','Clinical-Lab')");
        db.execSQL("insert into table_list values('T12','CSSD')");
        db.execSQL("insert into table_list values('T13','Dialysis')");
        db.execSQL("insert into table_list values('T14','Pharmacy')");
        db.execSQL("insert into table_list values('T15','Radiology')");
        //insert statement for admin
       // db.execSQL("insert into user_details values('admin','admin','admin','admin@gmail.com','admin','admin',1,'admin')");
    }

    public boolean insertRecord(String rdate,String table_id)
    {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("rdate", rdate);
        values.put("table_id", table_id);
        long result = db.insert("records", null, values);

        if (result == -1)
            return false;
        else
            return true;

    }
    public String getRecordID(String rdate,String table_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor=db.rawQuery("select record_id from records where rdate='"+rdate+"' and table_id='"+table_id+"'",null);


        String record_id="";
        if (cursor.getCount()>0) {
            cursor.moveToFirst();
            record_id = cursor.getString(0);
        }
        return record_id;


    }



    public boolean insertUSER_DETAILS(String ID,String fname,String lname, String designation, String email, String uname, String password, boolean active,String affiliation) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

       /* String query = "select * from user_details";
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();

        String ID;
        if (count > 0) {
            ID = "USER" + (count + 1);
        } else {
            ID = "USER" + 1;
        }*/

        values.put("id", ID);
        values.put("fname", fname);
        values.put("lname", lname);
        values.put("designation", designation);
        values.put("email", email);
        values.put("uname", uname);
        values.put("password", password);
        values.put("active", active);
        values.put("affiliation", affiliation);
        long res = db.insert("user_details", null, values);
        db.close();
        if(res == -1)
            return false;
        else
            return true;
    }


    public boolean updateUSER_DETAILS(String ID,String fname,String lname, String designation, String email, String uname, String password, boolean active,String affiliation) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
/*
        String query = "select * from user_details";
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();*/

       /* String ID;
        if (count > 0) {
            ID = "USER" + (count + 1);
        } else {
            ID = "USER" + 1;
        }*/

        values.put("id", ID);
        values.put("fname", fname);
        values.put("lname", lname);
        values.put("designation", designation);
        values.put("email", email);
        values.put("uname", uname);
        values.put("password", password);
        values.put("active", active);
        values.put("affiliation", affiliation);

        int numRows  = db.update("user_details", values, "id = ?", new String[]{ID});
        db.close();
        if (numRows > 0)
            return true;
        else
            return false;

    }


    public String searchPASSWORD(String uname) {
        SQLiteDatabase db = this.getWritableDatabase();


        //for deleting database
        //ctx.deleteDatabase(DATABASE_NAME);


        String query = "select password from user_details where uname= '" + uname + "'";
        Cursor cursor = db.rawQuery(query, null);
        String username, password;
        password = "password not found";

        if (cursor.getCount()>0) {
            cursor.moveToFirst();
            password = cursor.getString(0);
        }
        return password;
    }
    public boolean insertLOG(String name, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        String uid;
        String query = "select * from user_details where uname = '" + name + "'";
        Cursor rows = db.rawQuery(query, null);

        if (rows.getCount() == 1) {
            rows.moveToFirst();
            uid = rows.getString(0);

            ContentValues contentValues = new ContentValues();
            // contentValues.put("log_id", log_id);
            contentValues.put("id",uid);
            contentValues.put("log_date", date);
            contentValues.put("logout", "0");


            long result = db.insert("user_log", null, contentValues);
            if (result == -1)
                return false;
            else
                return true;
        }

        return false;

    }


   /* public boolean insertLOGIN(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String uid;
        String query = "select id from user_details where uname = '" + name + "'";
        Cursor rows = db.rawQuery(query, null);

        if (rows.getCount() > 0) {
            rows.moveToFirst();
            uid = rows.getString(0);

            ContentValues contentValues = new ContentValues();
            contentValues.put("user_id", uid);
            contentValues.put("user_name", name);

            long result = db.insert("login", null, contentValues);
            if (result == -1)
                return false;
            else
                return true;
        }

        return false;

    }*/


    public boolean isLoggedIn() {
        SQLiteDatabase db = this.getWritableDatabase();
        // ctx.deleteDatabase(DATABASE_NAME);

        String query1 = "select * from user_log where logout = '0'";

        Cursor rows1 = db.rawQuery(query1, null);

        if (rows1.getCount() > 0) {
            rows1.moveToFirst();
            return true;

        }
        rows1.close();
        return false;

    }

    public String getUsername()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "select uname from user_details where id='" + getUserID() + "'";
        Cursor rows = db.rawQuery(query, null);

        String username = "";
        if (rows.getCount() > 0)
        {
            rows.moveToFirst();
            username = rows.getString(0);
            rows.close();
        }
        return username;
    }

    public boolean isValidUser(String user)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "select id from user_details where uname='" + user + "'";
        Cursor rows = db.rawQuery(query, null);

        if (rows.getCount() > 0)
            return true;
        else
            return false;
    }

    public String getUserID() {
        SQLiteDatabase db = this.getWritableDatabase();
        String uid = "";
        String query1 = "select id from user_log where logout = '0'";
        Cursor rows1 = db.rawQuery(query1, null);


        if (rows1.getCount() > 0) {
            rows1.moveToFirst();
            uid = rows1.getString(0);
            rows1.close();
        }
        return uid;
    }


    public String getFirstname(String usr_name)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "select fname,lname from user_details where uname='" + usr_name +"'";
        Cursor rows = db.rawQuery(query, null);

        String fname = "",lname="";
        if (rows.getCount() > 0)
        {
            rows.moveToFirst();
            fname = rows.getString(0);
            lname = rows.getString(1);

            rows.close();
        }
        return fname + " " + lname;
    }


    public boolean logout()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        String str_date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
        ContentValues contentValues = new ContentValues();

        contentValues.put("logout",str_date);

        String uid = getUserID();
        int numRows  = db.update("user_log", contentValues, "id = ? and logout = ?", new String[]{uid, "0"});
        if (numRows > 0)
            return true;
        else
            return false;

    }



    public boolean insert_FLAGS(String record_id, String drug, String stock,  boolean docu_flag,
                                 boolean clean_flag, boolean hk_flag, String issues,
                                String timestamp, boolean ph_flag, boolean avail_staff,boolean hk_register_flag,
                                boolean equip_break,String comments)
    {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("record_id", record_id);
        contentValues.put("drug",drug);
        contentValues.put("stock",stock);
        contentValues.put("docu_flag",docu_flag);

        contentValues.put("clean_flag",clean_flag);
        contentValues.put("hk_flag",hk_flag);
        contentValues.put("issues",issues);
        contentValues.put("timestamp",timestamp);

        contentValues.put("ph_flag",ph_flag);
        contentValues.put("avail_staff",avail_staff);

        contentValues.put("hk_register_flag",hk_register_flag);
        contentValues.put("equip_break",equip_break);
        contentValues.put("comments",comments);

        long result = db.insert("flags", null, contentValues);

        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean updateFLAGS(String record_id, String drug, String stock,  boolean docu_flag,
                                 boolean clean_flag, boolean hk_flag, String issues,
                               String timestamp, boolean ph_flag, boolean avail_staff,boolean hk_register_flag,
                               boolean equip_break,String comments)
    {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put("record_id", record_id);
        contentValues.put("drug", drug);
        contentValues.put("stock", stock);
        contentValues.put("docu_flag", docu_flag);
        contentValues.put("clean_flag", clean_flag);
        contentValues.put("hk_flag", hk_flag);
        contentValues.put("issues", issues);
        contentValues.put("ph_flag",ph_flag);
        contentValues.put("avail_staff",avail_staff);
        contentValues.put("hk_register_flag",hk_register_flag);
        contentValues.put("equip_break",equip_break);
        contentValues.put("comments",comments);

        int numRows  = db.update("flags", contentValues, "record_id = ?", new String[]{record_id});
        if (numRows > 0)
            return true;
        else
            return false;

    }


    public Cursor getExistingFLAGS(String id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "select * from flags where record_id = '"+ id +"'";
        Cursor rows = db.rawQuery(query, null);
        return rows;
    }


    public boolean insertTR_ER(String id,String mlc_tot, String mlc_sent, boolean doct_avl, boolean sur_flag,boolean med_flag)
    {

        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues contentValues = new ContentValues();
        contentValues.put("record_id", id);
        contentValues.put("total_mlc", mlc_tot);
        contentValues.put("sent_mlc", mlc_sent);
        contentValues.put("doct_avl", doct_avl);
        contentValues.put("sur_flag", sur_flag);
        contentValues.put("med_flag", med_flag);
        long result = db.insert("tr_er", null, contentValues);

        if (result == -1)
            return false;
        else
            return true;
    }




    public boolean updateTR_ER(String record_id,String mlc_tot, String mlc_sent, boolean doc_avl, boolean sur_flag,
                               boolean med_flag)
    {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("record_id", record_id);
        contentValues.put("total_mlc", mlc_tot);
        contentValues.put("sent_mlc", mlc_sent);
        contentValues.put("doct_avl", doc_avl);
        contentValues.put("sur_flag", sur_flag);
        contentValues.put("med_flag", med_flag);

        int numRows = db.update("tr_er", contentValues, "record_id = ?", new String[]{record_id});
        if (numRows > 0)
            return true;
        else
            return false;

    }

    public Cursor getExistingTR_ET(String id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "select * from tr_er where record_id = '"+ id +"'";
        Cursor rows = db.rawQuery(query, null);
        return rows;
    }

    public Cursor getDataTR_ER(String ID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        //String [] whereValues = new String[]{""};
        Cursor result = db.rawQuery("select * from tr_er where record_id = " + ID, null);
        return result;
    }

    public boolean insert_icu(String record_id,String beds_use, String beds_avl, String vent_use,
                              String vent_avl, String vent_repair, String ipno)
    {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("record_id", record_id);
        contentValues.put("beds_use", beds_use);
        contentValues.put("beds_avl", beds_avl);
        contentValues.put("vent_use", vent_use);
        contentValues.put("vent_avl", vent_avl);
        contentValues.put("vent_repair", vent_repair);
        contentValues.put("ipno", ipno);


        long result = db.insert("icu", null, contentValues);
        if (result == -1)

            return false;
        else
            return true;

    }

    public Cursor getExistingICU(String id)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "select * from icu where record_id = '"+ id +"'";
        Cursor rows = db.rawQuery(query, null);

        return rows;
    }


    public boolean update_icu(String record_id,String beds_use,String beds_avl,String vent_use,
                              String vent_avl,String vent_repair,String ipno)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("record_id", record_id);

        contentValues.put("beds_use", beds_use);
        contentValues.put("beds_avl", beds_avl);
        contentValues.put("vent_use", vent_use);
        contentValues.put("vent_avl", vent_avl);
        contentValues.put("vent_repair", vent_repair);
        contentValues.put("ipno", ipno);
      /*  contentValues.put("docu_flag", docu_flag);
        contentValues.put("clean_flag",claen_flag);
        contentValues.put("hk_flag",hk_flag);
        contentValues.put("comments", comments);
        contentValues.put("issues", issues);*/

        int numRows = db.update("icu", contentValues, "record_id = ?", new String[]{record_id});
        if (numRows > 0)
             return true;
        else
            return false;
    }



    public Cursor getDataICU(String ICU_ID,String date)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        //String [] whereValues = new String[]{""};
        Cursor result = db.rawQuery("select * from icu where record_id = " + ICU_ID + " AND cdate = " + date, null);

        return  result;
    }



    public boolean insert_wards(String record_id,String w_name)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("record_id", record_id);
        contentValues.put("w_name", w_name);
        long result = db.insert("wards", null, contentValues);

        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean update_wards(String record_id,String w_name)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("record_id", record_id);
        contentValues.put("w_name", w_name);
        int numRows = db.update("wards", contentValues, "record_id = ?", new String[]{record_id});

        if(numRows > 0)
            return true;
        else
            return false;
    }


    public Cursor getDataWARDS(String record_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor result = db.rawQuery("select * from wards where record_id = '" + record_id + "'", null);

        return  result;
    }

    public boolean insert_stable(String table_id,String cdate,String st_ip,String stab_det)
    {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("table_id", table_id);
        contentValues.put("rdate", cdate);
        contentValues.put("st_ip", st_ip);
        contentValues.put("st_det",stab_det);

        long result = db.insert("stable_details", null, contentValues);
        if(result==-1)

            return false;
        else
            return true;

    }

    public boolean update_stable(String table_id,String cdate,String st_ip,String stab_det)
    {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("table_id", table_id);
        contentValues.put("rdate", cdate);
        contentValues.put("st_ip", st_ip);
        contentValues.put("st_det",stab_det);

        int numRows  = db.update("stable_details", contentValues, "table_id = ? and rdate = ? and st_ip = ?", new String[]{table_id, cdate, st_ip});

        if (numRows > 0)
            return true;
        else
            return false;

    }

    public boolean delete_stable(String table_id,String rdate,String st_ip)
    {

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from stable_details where table_id = '" + table_id + "' AND rdate = '" + rdate + "' AND st_ip = " + st_ip);
        return true;

    }

    public Cursor getDataSTABLE_DETAILS(String table_id,String rdate)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        //String [] whereValues = new String[]{""};
        Cursor result = db.rawQuery("select * from stable_details where table_id = '" + table_id + "' AND rdate = '" + rdate + "'", null);

        return  result;
    }


    //"create table reports (rdate text Primary key,uname text,submitted BOOLEAN);";
    public boolean insertReport(String rdate,String user_id,String submitted,String others)
    {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("rdate", rdate);
        contentValues.put("user_id", user_id);
        contentValues.put("submitted", submitted);
        contentValues.put("others", others);

        long result = db.insert("reports", null, contentValues);
        if(result==-1)

            return false;
        else
            return true;

    }


    public Boolean deleteReport(String rdate,String userid)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        int numRows  = db.delete("reports", "rdate = ? and user_id = ?", new String[]{rdate,userid});
       if (numRows > 0)
            return true;
        else
            return false;
    }

    public String getPendingReportDate()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "select rdate from reports where user_id = '" + getUserID() + "' and submitted  =  'Pending'";
        Cursor rows = db.rawQuery(query, null);

        String reportDate  = "";
        if (rows.getCount() > 0)
        {
            rows.moveToFirst();
            reportDate = rows.getString(0);
            rows.close();
        }
        return reportDate;

    }

    public boolean updateReport(String rdate,String uid,String submitted,String others)
    {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //contentValues.put("rdate", rdate);
        //contentValues.put("uname", uname);
        contentValues.put("submitted", submitted);
        //contentValues.put("others", others);

        int numRows  = db.update("reports", contentValues, "rdate = ? and  user_id = ? ", new String[]{rdate, uid});

        if (numRows > 0)
            return true;
        else
            return false;

    }


    public boolean isReportPending() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "select * from reports where user_id = '" + getUserID() + "' and submitted  =  'Pending'" ;

        Cursor rows = db.rawQuery(query, null);

        if (rows.getCount() > 0) {
            rows.moveToFirst();
            return true;
        }

        rows.close();
        return false;
    }

    public Cursor getAllReport(String id)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        //String [] whereValues = new String[]{""};
        Cursor result = db.rawQuery("select * from reports where user_id = '" + getUserID() + "' order by rdate DESC", null);

        return  result;
    }


    public Cursor getReport(String rdate,String id)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        //String [] whereValues = new String[]{""};
        Cursor result = db.rawQuery("select * from reports where rdate = '" + rdate + "' AND user_id = '" + getUserID() + "'", null);

        return  result;
    }

    public void updatepass(String unme, String newpass) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String str = "update user_details set password='" + newpass + "' where uname='" + unme + "'";
        db.execSQL(str);
    }
    public void updateOthers(String cdate, String  uname,String others)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("others",others);
        db.update("reports",contentValues,"rdate = ? and user_id = ?",new String[]{cdate,getUserID()});
        //String str = "update reports set others='" + others + "' where rdate='" + cdate + "' and user_id= '" +getUserID()+ "' and submitted  =  'Pending'";
        //db.execSQL(str);
    }



    public String getpassword(String u_name) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "select password from user_details where uname='" + u_name + "'";
        Cursor rows = db.rawQuery(query, null);

        String pas = "";
        if (rows.getCount() > 0) {
            rows.moveToFirst();
            pas = rows.getString(0);
            rows.close();
        }
        return pas;
    }



    public Cursor getalluser()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "select uname from user_details";
        Cursor rows = db.rawQuery(query, null);

        return rows;
    }



    public Cursor getExistingothers(String date,String uname)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "select others from reports  where rdate = '"+ date +"' and user_id = '"+ getUserID() +"' and submitted  =  'Pending'";
        Cursor rows = db.rawQuery(query, null);

        return rows;
    }

    public boolean insert_pharmacy(String record_id,Boolean stock_outofdrug,Boolean indents_sentontime)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("record_id", record_id);
        contentValues.put("stock_out_drugs",stock_outofdrug);
        contentValues.put("indents_sent",indents_sentontime);
        long result = db.insert("pharmacy", null, contentValues);

        if(result == -1)
            return false;
        else
            return true;
    }
    public Cursor getExistingpharmacy(String id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "select * from pharmacy where record_id = '"+ id +"'";
        Cursor rows = db.rawQuery(query, null);
        return rows;
    }

    public boolean updatePHARMACY(String record_id, boolean drug_short, boolean indent)
    {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("record_id", record_id);
        contentValues.put("stock_out_drugs", drug_short);
        contentValues.put("indents_sent", indent);

        int numRows = db.update("pharmacy", contentValues, "record_id = ?", new String[]{record_id});
        if (numRows > 0)
            return true;
        else
            return false;

    }


    public boolean insertBloodBank(String record_id,Boolean blood)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("record_id", record_id);
        contentValues.put("blood_shortage",blood);

        long result = db.insert("blood_bank", null, contentValues);

        if(result == -1)
            return false;
        else
            return true;
    }
    public Cursor getExistingBloodBank(String id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "select * from blood_bank where record_id = '"+ id +"'";
        Cursor rows = db.rawQuery(query, null);
        return rows;
    }

    public boolean updateBloodBank(String record_id,Boolean blood)
    {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("record_id", record_id);
        contentValues.put("blood_shortage", blood);

        int numRows = db.update("blood_bank", contentValues, "record_id = ?", new String[]{record_id});
        if (numRows > 0)
            return true;
        else
            return false;

    }


    public boolean insertRadiology(String record_id,Boolean support_staff,Boolean doctor)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("record_id", record_id);
        contentValues.put("support_staff",support_staff);
        contentValues.put("doctors",doctor);

        long result = db.insert("radiology", null, contentValues);

        if(result == -1)
            return false;
        else
            return true;
    }
    public Cursor getExistingRadiology(String id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "select * from radiology where record_id = '"+ id +"'";
        Cursor rows = db.rawQuery(query, null);
        return rows;
    }

    public boolean updateRadiology(String record_id,Boolean support_staff,Boolean doctor)
    {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("record_id", record_id);
        contentValues.put("support_staff", support_staff);
        contentValues.put("doctors",doctor);

        int numRows  = db.update("radiology", contentValues, "record_id = ?", new String[]{record_id});
        if (numRows > 0)
            return true;
        else
            return false;

    }
    public void deleteLocalRecords()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM flags");
        db.execSQL("DELETE FROM tr_er");
        db.execSQL("DELETE FROM icu");
        db.execSQL("DELETE FROM wards");
        db.execSQL("DELETE FROM blood_bank");
        db.execSQL("DELETE FROM pharmacy");
        db.execSQL("DELETE FROM radiology");
        db.execSQL("DELETE FROM records");
        db.execSQL("DELETE FROM stable_details");
    }
}
